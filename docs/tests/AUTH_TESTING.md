# Auth Endpoints - Testing Guide

> Document version: 2.0 | Last updated: 2026-07-08
> Base URL: `http://localhost:8080` (local) | Swagger: `http://localhost:8080/swagger-ui.html`

> **What's new in v2.0** — six security fixes from the P0 plan landed:
> 1. JWT signing key uses Base64 decode on both generation and verification (interim fix; superseded by RS256 migration)
> 2. `PermissionInterceptor` is now default-deny on errors (no more fail-open)
> 3. JWT signing migrated HS256 to RS256 with RSA 2048 keypair (PCI DSS key separation)
> 4. Account lockout after 5 failed login attempts (15-minute lockout)
> 5. JWT now carries `roles` claim; access token TTL reduced to 15 minutes; role changes revoke tokens
> 6. Refresh-token rotation with reuse detection (attacker reuse triggers session revocation)

---

## Table of Contents

1. [Token Architecture Overview](#1-token-architecture-overview)
2. [Token Flow Diagram](#2-token-flow-diagram)
3. [Prerequisites](#3-prerequisites)
4. [P0 Security Fixes (Summary)](#4-p0-security-fixes-summary)
5. [Endpoint 1 - POST /auth/register](#5-endpoint-1---post-authregister)
6. [Endpoint 2 - POST /auth/send-verify-email](#6-endpoint-2---post-authsend-verify-email)
7. [Endpoint 3 - GET /auth/verify-email](#7-endpoint-3---get-authverify-email)
8. [Endpoint 4 - POST /auth/login](#8-endpoint-4---post-authlogin)
9. [Endpoint 5 - GET /auth/account](#9-endpoint-5---get-authaccount)
10. [Endpoint 6 - POST /auth/refresh](#10-endpoint-6---post-authrefresh)
11. [Endpoint 7 - POST /auth/logout](#11-endpoint-7---post-authlogout)
12. [Endpoint 8 - POST /auth/resend-verification](#12-endpoint-8---post-authresend-verification)
13. [End-to-End Test Scenarios](#13-end-to-end-test-scenarios)
14. [Error Codes Reference](#14-error-codes-reference)
15. [Swagger UI Testing Guide](#15-swagger-ui-testing-guide)

---

## 1. Token Architecture Overview

### Access Token (JWT, RS256)

- **Algorithm**: RS256 (RSA Signature with SHA-256) — asymmetric
- **Signing key**: Private key in `src/main/resources/keys/jwt-private-pkcs8.pem` (classpath resource, gitignored)
- **Verification key**: Public key in `src/main/resources/keys/jwt-public.pem` (classpath resource, gitignored)
- **Storage**: Client-side only (memory, localStorage, etc.)
- **Lifetime**: 15 minutes (configurable via `JWT_ACCESS_TOKEN_VALIDITY_SECONDS`, default 900)
- **Contains**:
  - `sub`: Username
  - `user.id`: User UUID
  - `user.username`: Username
  - `user.role`: Role name (e.g., `USER`, `ADMIN`)
  - `roles`: Array of granted roles (e.g., `["ROLE_USER"]`, `["ROLE_ADMIN"]`) — used by `@PreAuthorize`
  - `iat`: Issued at timestamp
  - `exp`: Expiration timestamp

> **Breaking change**: v1 used HS256 (symmetric) with a Base64-encoded secret key. v2 uses RS256 (asymmetric) with PEM key files. Existing access tokens signed with HS256 will be rejected after the v2 migration; users must re-login.

### Refresh Token (Opaque UUID)

- **Type**: UUID string (opaque, not a JWT)
- **Storage**: Redis (`refresh_token:<uuid>`) + Database (`User.refreshToken`)
- **Lifetime**: 7 days (configurable via `JWT_REFRESH_TOKEN_VALIDITY_SECONDS`, default 604800)
- **Security**: HttpOnly cookie, Secure flag, path=`/`

### Redis Token State Keys

| Key Pattern | Value | TTL | Purpose |
|---|---|---|---|
| `refresh_token:<uuid>` | Username | 7 days | Active refresh token lookup |
| `used_refresh:<uuid>` | Username | 7 days | Marked-as-used refresh tokens (rotation tracking) |
| `revoked_token:<jwt>` | `"true"` | Remaining token lifetime | Blacklisted access tokens |
| `revoked_user:<username>` | `"1"` | 15 minutes (access TTL) | User-level revocation (role-change / refresh reuse) |
| `rate_limit:<ip>` | Request count | 2 minutes | Rate limiting |

---

## 2. Token Flow Diagram

```mermaid
sequenceDiagram
    autonumber
    participant Client
    participant Backend
    participant Redis
    participant Database

    Note over Client,Database: Registration
    Client->>Backend: POST /auth/register
    Backend->>Database: Save user (emailVerified=false, failedLoginAttempts=0)
    Database-->>Backend: User created
    Backend-->>Client: 201 Created

    Note over Client,Database: Login + Lockout Check
    Client->>Backend: POST /auth/login
    Backend->>Database: Lookup user by username/email
    Database-->>Backend: User found
    alt Account locked (locked_until > now)
        Backend-->>Client: 403 ACCOUNT_LOCKED
    else Too many failed attempts (>= 5)
        Backend->>Database: Set lockedUntil = now + 15min
        Backend-->>Client: 403 ACCOUNT_LOCKED
    else Credentials invalid
        Backend->>Database: Increment failedLoginAttempts
        Backend-->>Client: 400 BAD_CREDENTIALS
    else Valid credentials
        Backend->>Database: Reset failedLoginAttempts=0, lockedUntil=null
        Backend->>Redis: SET refresh_token:<uuid> = username (TTL=7d)
        Backend->>Database: Update User.refreshToken
        Backend-->>Client: access_token (RS256 JWT) + Set-Cookie: refresh_token
    end

    Note over Client,Database: Token Refresh + Rotation
    Client->>Backend: POST /auth/refresh (with refresh_token cookie)
    Backend->>Redis: GET refresh_token:<uuid>
    Redis-->>Backend: Username
    Backend->>Redis: SET used_refresh:<uuid> = username (TTL=7d, mark as used)
    Backend->>Redis: DEL refresh_token:<uuid>
    Backend->>Database: Validate username + refreshToken match
    Backend->>Redis: SET refresh_token:<new_uuid> = username (TTL=7d)
    Backend->>Database: Update User.refreshToken
    Backend-->>Client: New access_token + Set-Cookie: new_refresh_token
    Note right of Client: Old refresh token now in<br/>used_refresh: list; reuse<br/>triggers full session revocation

    Note over Client,Database: Refresh Token Reuse Detection
    Client->>Backend: POST /auth/refresh (with already-used refresh_token)
    Backend->>Redis: GET refresh_token:<old_uuid> → null (deleted)
    Backend->>Redis: GET used_refresh:<old_uuid> → username (found!)
    Backend->>Redis: SET revoked_user:<username> = "1" (TTL=15min, revoke ALL)
    Backend-->>Client: 400 INVALID_REFRESH_TOKEN
    Note right of Client: All sessions for this user<br/>are now invalidated

    Note over Client,Database: Logout
    Client->>Backend: POST /auth/logout (Bearer access_token)
    Backend->>Redis: SET revoked_token:<access_token> = "true" (TTL=remaining)
    Backend->>Redis: DEL refresh_token:<uuid>
    Backend->>Database: Clear User.refreshToken
    Backend-->>Client: 200 OK + Set-Cookie: refresh_token=; maxAge=0
```

---

## 3. Prerequisites

### Environment Variables

Ensure these are set in your environment or `application-local.yaml`:

| Variable | Description | Example |
|---|---|---|
| `JWT_PRIVATE_KEY_PATH` | Path to RSA private key (PKCS#8 PEM) | `classpath:keys/jwt-private-pkcs8.pem` |
| `JWT_PUBLIC_KEY_PATH` | Path to RSA public key (X.509 PEM) | `classpath:keys/jwt-public.pem` |
| `JWT_ACCESS_TOKEN_VALIDITY_SECONDS` | Access token lifetime | `900` (15 min) |
| `JWT_REFRESH_TOKEN_VALIDITY_SECONDS` | Refresh token lifetime | `604800` (7 days) |
| `REDIS_URL` | Redis server URL | `localhost` |
| `DB_URL` | PostgreSQL connection URL | `jdbc:postgresql://localhost:5432/ecommerce` |
| `DB_USERNAME` | Database username | `postgres` |
| `DB_PASSWORD` | Database password | `postgres` |

### RSA Key Setup

The app requires an RSA 2048-bit keypair. Generate it once with:

```bash
bash scripts/generate-jwt-keys.sh
```

This creates:
- `src/main/resources/keys/jwt-private-pkcs8.pem` — private key (NEVER commit this)
- `src/main/resources/keys/jwt-public.pem` — public key (NEVER commit this)

Both files are gitignored. Share the public key freely; keep the private key secret.

### Swagger UI Access

1. Start the backend application.
2. Open `http://localhost:8080/swagger-ui.html`.
3. All auth endpoints are under the **authentication-controller** section.

### Authorization Setup in Swagger

After logging in via Swagger:
1. Expand the endpoint you want to test.
2. Click the **Authorize** button (green padlock icon).
3. Enter: `Bearer {access_token}` (e.g., `Bearer eyJhbGciOiJSUzI1NiJ9...`).
4. Click **Authorize** and close the dialog.

> **Note**: Swagger UI does not handle HttpOnly cookies well. For `/auth/refresh` and `/auth/logout` testing, use Postman or cURL.

---

## 4. P0 Security Fixes (Summary)

This section summarizes the six security fixes applied to the auth module.

### Fix 1: Base64 Key Encoding (interim, superseded by RS256)

`AuthenticationService.verifyToken` was using `getBytes()` on the secret key while `SecurityUtil.generateAccessToken` was using `Base64.getDecoder().decode()`. This caused `/auth/introspect` to return `INVALID` for valid tokens. Fixed by aligning both to use Base64 decode. **Superseded by the RS256 migration in Fix 3.**

### Fix 2: PermissionInterceptor Default-Deny

`PermissionInterceptor.preHandle` was returning `true` (allow) when a non-`UNAUTHORIZED` exception was thrown during permission checks — a fail-open vulnerability. Any DB error, null role, or missing user would let the request through.

**Fix**: The interceptor now re-throws any failure as `UNAUTHORIZED`. `UserServiceImpl.hasPermission` returns `false` when `user == null` or `user.role == null`, triggering the throw. This is a default-deny model.

### Fix 3: HS256 to RS256 Migration (PCI DSS Compliance)

HS256 uses a single symmetric secret for both signing and verification, violating PCI DSS key separation requirements. Migrated to RS256:

- RSA 2048-bit keypair generated (PKCS#8 private, X.509 public)
- `SecurityUtil` signs with private key via `RSASSASigner`
- `SecurityJwtConfig` verifies with public key via `NimbusJwtDecoder.withPublicKey()`
- PEM files stored in `src/main/resources/keys/`, gitignored
- Helper script: `scripts/generate-jwt-keys.sh`

**Breaking change**: Tokens signed with the old HS256 key are invalid after deploy. Users must re-login.

### Fix 4: Account Lockout (Brute-Force Protection)

The `failed_login_attempts` and `locked_until` columns existed in the DB schema but were never used. Implemented:

- Login attempt fails → `failedLoginAttempts` incremented in DB
- After 5 failed attempts → `lockedUntil = now + 15 minutes`
- Locked account → any login attempt returns `403 ACCOUNT_LOCKED`
- Successful login → both fields reset to `null` / `0`

### Fix 5: Role Claim + TTL + Revoke

`@PreAuthorize("hasRole('ADMIN')")` never fired because the JWT had no authorities and the converter looked for a non-existent `permission` claim. Implemented:

- JWT now includes `roles` claim: `["ROLE_USER"]`, `["ROLE_ADMIN"]`
- `JwtGrantedAuthoritiesConverter` reads `roles` claim (was `permission`)
- Access token TTL reduced from 24h to 15 minutes
- Role change triggers token revocation via `revoked_user:{username}` Redis key (TTL = 15 min)
- `UserDetailsServiceCustom` no longer hardcodes `ROLE_USER`

**Breaking change**: Access token lifetime is 15 minutes. Clients must call `/auth/refresh` proactively.

### Fix 6: Refresh Token Rotation

A leaked refresh token could be used for its full 7-day TTL by an attacker. Implemented OAuth-style rotation:

- Each `/auth/refresh` call returns a new refresh token
- Old refresh token moved to `used_refresh:<uuid>` key (not deleted immediately)
- Reusing a used refresh token triggers full session revocation: `revoked_user:<username>` set
- All sessions for the user invalidated immediately

---

## 5. Endpoint 1 - POST /auth/register

**Path**: `/auth/register` | **Method**: `POST` | **Auth**: Public

### Description

Registers a new user account. The new user is assigned the `USER` role by default and starts with `failedLoginAttempts = 0` and `lockedUntil = null`. Email verification must be completed separately.

### Request Body

```json
{
  "username": "testuser123",
  "password": "password123",
  "name": "Nguyen Van A",
  "email": "testuser@example.com",
  "address": "123 Nguyen Trai, District 1, HCMC"
}
```

| Field | Type | Required | Validation |
|---|---|---|---|
| `username` | string | Yes | Min 4 characters |
| `password` | string | Yes | Min 6 characters |
| `name` | string | Yes | Cannot be blank |
| `email` | string | Yes | Valid email format, unique |
| `address` | string | No | Optional |

### Success Response (201 Created)

```json
{
  "success": true,
  "message": "Register successfully",
  "data": {
    "_id": "550e8400-e29b-41d4-a716-446655440000",
    "username": "testuser123",
    "name": "Nguyen Van A",
    "address": "123 Nguyen Trai, District 1, HCMC",
    "email": "testuser@example.com",
    "emailVerified": false,
    "phone": null,
    "phoneVerified": false,
    "avatarUrl": null,
    "roleName": "USER"
  },
  "statusCode": 201
}
```

### Error Responses

**400 Bad Request - Username already exists**

```json
{
  "success": false,
  "message": "USER_EXISTED",
  "data": null,
  "statusCode": 400
}
```

**400 Bad Request - Email already exists**

```json
{
  "success": false,
  "message": "EMAIL_ALREADY_EXISTS",
  "data": null,
  "statusCode": 400
}
```

### Test Cases

| # | Scenario | Expected Result | Verification |
|---|---|---|---|
| TC1.1 | Valid registration with all fields | 201, user created with `emailVerified=false`, `failedLoginAttempts=0` | Check database |
| TC1.2 | Valid registration (only required fields) | 201, user created | Check database |
| TC1.3 | Duplicate username | 400 `USER_EXISTED` | Response message |
| TC1.4 | Duplicate email | 400 `EMAIL_ALREADY_EXISTS` | Response message |
| TC1.5 | Username < 4 chars | 400 validation error | Response message |
| TC1.6 | Password < 6 chars | 400 validation error | Response message |
| TC1.7 | Invalid email format | 400 validation error | Response message |

---

## 6. Endpoint 2 - POST /auth/send-verify-email

**Path**: `/auth/send-verify-email` | **Method**: `POST` | **Auth**: Required (Bearer Token)

### Description

Sends an email verification link to the currently authenticated user. The verification token is valid for 15 minutes. Requires authentication.

### Request Headers

```
Authorization: Bearer {access_token}
```

### Success Response (200 OK)

```json
{
  "success": true,
  "message": "Email verification sent",
  "data": null,
  "statusCode": 200
}
```

### Error Responses

**401 Unauthorized - No token or invalid token**

```json
{
  "success": false,
  "message": "Token is invalid or expired",
  "data": null,
  "statusCode": 401
}
```

### Test Cases

| # | Scenario | Expected Result | Verification |
|---|---|---|---|
| TC2.1 | Valid authenticated request | 200, email token generated | Check database |
| TC2.2 | No Authorization header | 401 `UNAUTHENTICATED` | Response message |
| TC2.3 | Invalid Bearer token | 401 `UNAUTHENTICATED` | Response message |
| TC2.4 | Expired access token | 401 `UNAUTHENTICATED` | Response message |
| TC2.5 | Revoked token (after logout) | 401 `UNAUTHENTICATED` | Response message |

---

## 7. Endpoint 3 - GET /auth/verify-email

**Path**: `/auth/verify-email` | **Method**: `GET` | **Auth**: Public

### Description

Verifies a user's email address using the token sent via email. Token expires after 15 minutes.

### Request Parameters

| Parameter | Type | Required | Description |
|---|---|---|---|
| `token` | string (UUID) | Yes | Verification token from email link |

### Success Response (200 OK)

```json
{
  "success": true,
  "message": "Email verified successfully",
  "data": null,
  "statusCode": 200
}
```

### Error Responses

**400 Bad Request - Invalid or expired token**

```json
{
  "success": false,
  "message": "INVALID_VERIFICATION_TOKEN",
  "data": null,
  "statusCode": 400
}
```

### Complete Email Verification Flow

```
Step 1: Register user
  POST /auth/register
  → Save user with emailVerified=false

Step 2: Login to get access token
  POST /auth/login
  → Get access_token

Step 3: Send verification email
  POST /auth/send-verify-email (with Bearer token)
  → Email token generated (check logs for the verification link)

Step 4: Verify email
  GET /auth/verify-email?token={uuid}
  → emailVerified = true
```

---

## 8. Endpoint 4 - POST /auth/login

**Path**: `/auth/login` | **Method**: `POST` | **Auth**: Public

### Description

Authenticates a user with username or email plus password. Returns an RS256-signed access token (JWT) and sets a refresh token as an HttpOnly cookie. Implements account lockout: after 5 failed attempts, the account is locked for 15 minutes.

### Request Body

```json
{
  "username": "testuser123",
  "password": "password123"
}
```

| Field | Type | Required | Description |
|---|---|---|---|
| `username` | string | Yes | Username or email address |
| `password` | string | Yes | Account password |

### Success Response (200 OK)

```json
{
  "success": true,
  "message": "Login successfully",
  "data": {
    "access_token": "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlcjEyMyIsInVzZXIiOnsiaWQiOiI1NTBlODQwMC1lMjliLTQxZDQiOiJ0ZXN0dXNlcjEyMyJ9LCJyb2xlcyI6WyJST0xFX1VTRVIiXSwiaWF0IjoxNzUxMDAwMDAwLCJleHAiOjE3NTEwMDAzNjB9...",
    "refresh_token": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "user": {
      "_id": "550e8400-e29b-41d4-a716-446655440000",
      "username": "testuser123",
      "name": "Nguyen Van A",
      "email": "testuser@example.com",
      "emailVerified": false,
      "roleName": "USER"
    }
  },
  "statusCode": 200
}
```

> Note: The `access_token` header is `{"alg":"RS256","typ":"JWT"}` (verify with `openssl rsa -pubin -in jwt-public.pem -text -noout`).

**Response Headers:**

```
Set-Cookie: refresh_token=a1b2c3d4-e5f6-7890-abcd-ef1234567890; Path=/; HttpOnly; Secure
```

### Lockout Behavior

Before authenticating, the server checks:
1. `user.lockedUntil > now` → 403 `ACCOUNT_LOCKED` (locked account)
2. Authentication fails → increment `failedLoginAttempts`, check `>= 5` → set `lockedUntil = now + 15min`
3. Authentication succeeds → reset `failedLoginAttempts = 0`, `lockedUntil = null`

### Error Responses

**400 Bad Request - Invalid credentials**

```json
{
  "success": false,
  "message": "BAD_CREDENTIALS",
  "data": null,
  "statusCode": 400
}
```

**403 Forbidden - Account locked**

```json
{
  "success": false,
  "message": "ACCOUNT_LOCKED",
  "data": null,
  "statusCode": 403
}
```

### Test Cases

| # | Scenario | Expected Result | Verification |
|---|---|---|---|
| TC4.1 | Valid username + correct password | 200, RS256 access_token + refresh_token cookie | JWT header shows `alg: RS256` |
| TC4.2 | Valid email + correct password | 200, access_token + refresh_token cookie | Token is valid JWT |
| TC4.3 | Wrong password | 400 `BAD_CREDENTIALS` | Check `failedLoginAttempts` incremented |
| TC4.4 | 5 wrong passwords | 400 each time, 6th attempt → 403 `ACCOUNT_LOCKED` | Check `lockedUntil` in DB |
| TC4.5 | Login while locked | 403 `ACCOUNT_LOCKED` | Check `lockedUntil > now` |
| TC4.6 | Login after lockout expires | 200 OK | Set `lockedUntil=null` |
| TC4.7 | Successful login resets counter | Check DB: `failedLoginAttempts=0`, `lockedUntil=null` | DB state |
| TC4.8 | Non-existent user | 400 `BAD_CREDENTIALS` | Response message |
| TC4.9 | Empty body | 400 validation error | Response message |

---

## 9. Endpoint 5 - GET /auth/account

**Path**: `/auth/account` | **Method**: `GET` | **Auth**: Required (Bearer Token)

### Description

Returns the currently authenticated user's account details. Useful for verifying the current session and checking profile data.

### Success Response (200 OK)

```json
{
  "success": true,
  "message": null,
  "data": {
    "_id": "550e8400-e29b-41d4-a716-446655440000",
    "username": "testuser123",
    "name": "Nguyen Van A",
    "email": "testuser@example.com",
    "emailVerified": true,
    "roleName": "USER"
  },
  "statusCode": 200
}
```

### Test Cases

| # | Scenario | Expected Result | Verification |
|---|---|---|---|
| TC5.1 | Valid RS256 token | 200, user data returned | Match user details |
| TC5.2 | No Authorization header | 401 | Response message |
| TC5.3 | Invalid token format | 401 | Response message |
| TC5.4 | Expired token (> 15 min) | 401 | Response message |
| TC5.5 | Revoked token (after logout) | 401 | Response message |
| TC5.6 | Token signed with wrong key (HS256) | 401 | Old HS256 tokens rejected |
| TC5.7 | Token for user whose role changed | 401 `UNAUTHENTICATED` | `revoked_user:{username}` set |

---

## 10. Endpoint 6 - POST /auth/refresh

**Path**: `/auth/refresh` | **Method**: `POST` | **Auth**: Public (uses cookie)

### Description

Issues a new pair of access and refresh tokens. This is a **token rotation** endpoint: the old refresh token is marked as used and a new one is issued. If the old refresh token has already been used (reuse attack), all sessions for that user are revoked.

### Request Cookies

```
refresh_token: a1b2c3d4-e5f6-7890-abcd-ef1234567890
```

### Success Response (200 OK)

```json
{
  "success": true,
  "message": "Refresh token successfully",
  "data": {
    "access_token": "eyJhbGciOiJSUzI1NiJ9...",
    "refresh_token": "new-refresh-uuid-a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "user": {
      "_id": "550e8400-e29b-41d4-a716-446655440000",
      "username": "testuser123",
      "name": "Nguyen Van A",
      "email": "testuser@example.com",
      "emailVerified": true,
      "roleName": "USER"
    }
  },
  "statusCode": 200
}
```

**Response Headers:**

```
Set-Cookie: refresh_token=new-refresh-uuid-...; Path=/; HttpOnly; Secure
```

### Error Responses

**400 Bad Request - No refresh token cookie**

```json
{
  "success": false,
  "message": "COOKIES_EMPTY",
  "data": null,
  "statusCode": 400
}
```

**400 Bad Request - Invalid or reused refresh token**

```json
{
  "success": false,
  "message": "INVALID_REFRESH_TOKEN",
  "data": null,
  "statusCode": 400
}
```

> Note: If the token was reused (security attack), `INVALID_REFRESH_TOKEN` is returned AND all sessions for that user are immediately revoked via `revoked_user:{username}` Redis key.

### Token Rotation Verification

After calling `/auth/refresh`:
1. The old refresh token UUID is moved to `used_refresh:<old_uuid>` in Redis (TTL = 7 days)
2. The new refresh token UUID is stored in `refresh_token:<new_uuid>` in Redis (TTL = 7 days)
3. `User.refreshToken` column in the database is updated with the new UUID
4. The old access token is still valid until its natural 15-minute expiry
5. If an attacker tries to use the old refresh token → `used_refresh:<uuid>` is found → user sessions revoked

### cURL Example

```bash
# Step 1: Login to get refresh token cookie
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser123","password":"password123"}' \
  -c cookies.txt

# Step 2: Refresh token
curl -X POST http://localhost:8080/auth/refresh \
  -b cookies.txt

# Step 3: Verify old token is invalidated
curl -X POST http://localhost:8080/auth/refresh \
  -b cookies.txt  # This should succeed with a NEW token

# Step 4: Try to reuse the FIRST token (attack simulation)
curl -X POST http://localhost:8080/auth/refresh \
  -H "Cookie: refresh_token=<first-token-uuid>"  # 400 + all sessions revoked
```

### Test Cases

| # | Scenario | Expected Result | Verification |
|---|---|---|---|
| TC6.1 | Valid refresh token cookie | 200, new access_token + refresh_token | Rotation verified in Redis |
| TC6.2 | No refresh_token cookie | 400 `COOKIES_EMPTY` | Response message |
| TC6.3 | Invalid refresh token (not in Redis) | 400 `INVALID_REFRESH_TOKEN` | Response message |
| TC6.4 | Reused refresh token (rotation attack) | 400 `INVALID_REFRESH_TOKEN` | `revoked_user:{username}` in Redis |
| TC6.5 | Token doesn't match DB | 400 `INVALID_REFRESH_TOKEN` | Response message |
| TC6.6 | User deleted after login | 400 `INVALID_REFRESH_TOKEN` | Response message |

---

## 11. Endpoint 7 - POST /auth/logout

**Path**: `/auth/logout` | **Method**: `POST` | **Auth**: Required (Bearer Token)

### Description

Logs out the current user by revoking the access token and invalidating the refresh token. Both the access token and refresh token become unusable.

### Request Headers

```
Authorization: Bearer {access_token}
```

### Success Response (200 OK)

```json
{
  "success": true,
  "message": "Logout successfully",
  "data": null,
  "statusCode": 200
}
```

**Response Headers:**

```
Set-Cookie: refresh_token=; Path=/; Max-Age=0
```

### Side Effects

1. **Access token blacklisted**: JWT stored in Redis under `revoked_token:<token>` with TTL = remaining lifetime
2. **Refresh token deleted**: `refresh_token:<uuid>` deleted from Redis
3. **User refresh token cleared**: `User.refreshToken` set to null in database
4. **Cookie cleared**: Client receives a cookie deletion directive

### Post-Logout Verification

After logout:
1. Attempt to use the old access token — should receive **401**
2. Attempt to call `/auth/refresh` with the old refresh token cookie — should receive **400 `INVALID_REFRESH_TOKEN`**

### Test Cases

| # | Scenario | Expected Result | Verification |
|---|---|---|---|
| TC7.1 | Valid token, valid refresh token | 200, both tokens invalidated | Token blacklisted in Redis |
| TC7.2 | No Authorization header | 401 `UNAUTHENTICATED` | Response message |
| TC7.3 | Expired token | 401 `UNAUTHENTICATED` | Response message |
| TC7.4 | Token already revoked (double logout) | 200 (idempotent) | Response message |
| TC7.5 | Malformed token | 400 `INVALID_ACCESS_TOKEN` | Response message |

---

## 12. Endpoint 8 - POST /auth/resend-verification

**Path**: `/auth/resend-verification` | **Method**: `POST` | **Auth**: Public

### Description

Resends the email verification link to a specified email address. Public endpoint (no authentication required). Useful when a user did not receive the original verification email.

### Request Body

```json
{
  "email": "testuser@example.com"
}
```

| Field | Type | Required | Validation |
|---|---|---|---|
| `email` | string | Yes | Valid email format |

### Success Response (200 OK)

```json
{
  "success": true,
  "message": "Verification email sent",
  "data": null,
  "statusCode": 200
}
```

### Test Cases

| # | Scenario | Expected Result | Verification |
|---|---|---|---|
| TC8.1 | Email exists, not verified | 200, new verification token generated | DB token updated |
| TC8.2 | Email exists, already verified | 200 "Email already verified" | Response message |
| TC8.3 | Email does not exist | 200 (no enumeration leak) | No database record created |
| TC8.4 | Invalid email format | 400 `VALIDATION_ERROR` | Response message |

---

## 13. End-to-End Test Scenarios

### Scenario 1: Account Lockout Flow

```
Objective: Verify lockout kicks in after 5 failed attempts and resets on success.

Steps:
1. Login correctly:
   POST /auth/login
   Body: {"username":"testuser","password":"password123"}
   Expected: 200 OK
   Verify DB: failedLoginAttempts=0, lockedUntil=null

2. Wrong password 5 times:
   for i in {1..5}; do
     POST /auth/login
     Body: {"username":"testuser","password":"wrongpass$i"}
     Expected: 400 BAD_CREDENTIALS
   done
   Verify DB: failedLoginAttempts=0, lockedUntil set (now + 15min)

3. 6th attempt with correct password:
   POST /auth/login
   Body: {"username":"testuser","password":"password123"}
   Expected: 403 ACCOUNT_LOCKED

4. Manually set lockedUntil to past via DB:
   UPDATE users SET locked_until = NOW() - INTERVAL '1 minute' WHERE username='testuser';
   Then login:
   POST /auth/login
   Body: {"username":"testuser","password":"password123"}
   Expected: 200 OK
   Verify DB: failedLoginAttempts=0, lockedUntil=null
```

### Scenario 2: RS256 Token Verification

```
Objective: Verify tokens are signed with RS256 and old HS256 tokens are rejected.

Steps:
1. Login to get a token:
   POST /auth/login
   Body: {"username":"testuser","password":"password123"}
   Expected: 200 OK, access_token returned

2. Decode the JWT header:
   echo "<access_token>" | cut -d. -f1 | base64 -d
   Expected: {"alg":"RS256","typ":"JWT"}

3. Verify the token works for authenticated requests:
   GET /auth/account
   Headers: Authorization: Bearer <access_token>
   Expected: 200 OK

4. Manually create an HS256 token (using the old secret):
   (Simulation: generate a fake HS256 JWT with any secret)
   Try using the HS256 token:
   GET /auth/account
   Headers: Authorization: Bearer <hs256_fake_token>
   Expected: 401 UNAUTHENTICATED (wrong algorithm)
```

### Scenario 3: Token Lifecycle with Rotation

```
Objective: Test the full token lifecycle with refresh token rotation.

Steps:
1. Login:
   POST /auth/login
   Body: {"username":"testuser","password":"password123"}
   Save: access_token_1, refresh_token_1

2. Use refresh_token_1:
   POST /auth/refresh (with refresh_token_1 cookie)
   Expected: 200 OK
   Save: access_token_2, refresh_token_2
   Verify Redis: refresh_token_1 moved to used_refresh key

3. Try reusing refresh_token_1 (attack simulation):
   POST /auth/refresh (with refresh_token_1 cookie)
   Expected: 400 INVALID_REFRESH_TOKEN
   Verify Redis: revoked_user:testuser exists

4. Verify ALL old tokens are now invalid:
   GET /auth/account (with access_token_1)
   Expected: 401 UNAUTHENTICATED

   GET /auth/account (with access_token_2)
   Expected: 401 UNAUTHENTICATED (revoked via user-level key)

5. Login again (new session):
   POST /auth/login
   Expected: 200 OK (new tokens work)

6. Logout:
   POST /auth/logout (with new access token)
   Expected: 200 OK

7. Verify old tokens still invalid after logout:
   GET /auth/account (with old access token)
   Expected: 401 UNAUTHENTICATED
```

### Scenario 4: Role-Based Access Control

```
Objective: Verify @PreAuthorize hasRole works with the roles JWT claim.

Prerequisites: ADMIN and USER role accounts exist with appropriate permissions.

Steps:
1. Login as ADMIN:
   POST /auth/login
   Body: {"username":"admin","password":"adminpass"}
   Save: admin_access_token
   Decode JWT payload — verify roles: ["ROLE_ADMIN"]

2. Login as USER:
   POST /auth/login
   Body: {"username":"user01","password":"12345678"}
   Save: user_access_token
   Decode JWT payload — verify roles: ["ROLE_USER"]

3. ADMIN calls /reports/download-url:
   GET /reports/download-url?fileKey=report.pdf
   Headers: Authorization: Bearer <admin_access_token>
   Expected: 200 OK

4. USER calls /reports/download-url:
   GET /reports/download-url?fileKey=report.pdf
   Headers: Authorization: Bearer <user_access_token>
   Expected: 403 Forbidden (no permission)

5. Admin changes USER's role to ADMIN in DB:
   UPDATE users SET role_id = (SELECT id FROM roles WHERE name='ADMIN')
   WHERE username='user01';

6. USER's existing token should now be rejected:
   GET /reports/download-url
   Headers: Authorization: Bearer <user_access_token>
   Expected: 401 UNAUTHENTICATED (role change triggered revocation)

7. USER logs in again:
   POST /auth/login
   Body: {"username":"user01","password":"12345678"}
   Save: new_access_token

8. New token works with ADMIN permissions:
   GET /reports/download-url
   Headers: Authorization: Bearer <new_access_token>
   Expected: 200 OK
```

### Scenario 5: PermissionInterceptor Default-Deny

```
Objective: Verify PermissionInterceptor denies requests when user/role is missing.

Steps:
1. Login as user with valid permissions:
   POST /auth/login
   Body: {"username":"testuser","password":"password123"}
   Expected: 200 OK

2. Manually delete the user's role in DB:
   UPDATE users SET role_id = NULL WHERE username='testuser';

3. Try any protected endpoint:
   GET /auth/account
   Headers: Authorization: Bearer <access_token>
   Expected: 401 UNAUTHORIZED (hasPermission returned false → interceptor threw)

4. Restore the role:
   UPDATE users SET role_id = (SELECT id FROM roles WHERE name='USER')
   WHERE username='testuser';

5. Login again:
   POST /auth/login
   Expected: 200 OK (user works again)
```

---

## 14. Error Codes Reference

| Error Code | HTTP Status | Trigger | Recovery |
|---|---|---|---|
| `UNAUTHORIZED` | 401 | Permission check fails (user/role null, no permission) | Login with valid user/role |
| `UNAUTHENTICATED` | 401 | Token invalid, expired, revoked, or wrong algorithm | Re-login or refresh |
| `INVALID_REFRESH_TOKEN` | 400 | Refresh token not in Redis, reused, or doesn't match DB | Re-login to get new tokens |
| `INVALID_ACCESS_TOKEN` | 400 | Cannot parse access token at logout | Token may be malformed |
| `COOKIES_EMPTY` | 400 | No refresh_token in request cookies | Include the refresh_token cookie |
| `BAD_CREDENTIALS` | 400 | Wrong username or password | Use correct credentials |
| `ACCOUNT_LOCKED` | 403 | Account locked (5 failed attempts, 15-min lockout) | Wait 15 minutes or admin unlocks |
| `USER_EXISTED` | 400 | Username already taken | Choose a different username |
| `EMAIL_ALREADY_EXISTS` | 400 | Email already registered | Use a different email or login |
| `EMAIL_NOT_VERIFIED` | 403 | Email not verified (for protected endpoints) | Verify email first |
| `INVALID_VERIFICATION_TOKEN` | 400 | Email token invalid, expired, or used | Request a new verification email |
| `TOO_MANY_REQUESTS` | 429 | Rate limit exceeded | Wait and retry after cooldown |
| `VALIDATION_ERROR` | 400 | Request body validation failed | Fix the field(s) listed in response data |

---

## 15. Swagger UI Testing Guide

### Getting Started

1. Start the Spring Boot application:
   ```bash
   cd h:/SideProjects/ecommerce
   ./mvnw spring-boot:run
   ```

2. Open Swagger UI: `http://localhost:8080/swagger-ui.html`

3. Expand the **authentication-controller** section.

### Step-by-Step: Testing Auth Flow with Swagger

#### Part A: Register and Login

```
1. Expand POST /auth/register
   → Click "Try it out"
   → Enter body:
     {
       "username": "swaggertest",
       "password": "test123456",
       "name": "Swagger Test User",
       "email": "swaggertest@example.com"
     }
   → Click "Execute"
   → Expected: 201 Created

2. Expand POST /auth/login
   → Click "Try it out"
   → Enter body:
     {
       "username": "swaggertest",
       "password": "test123456"
     }
   → Click "Execute"
   → Expected: 200 OK with access_token (RS256 JWT)
   → COPY THE access_token VALUE (starts with "eyJ...")
```

#### Part B: Authorize with Access Token

```
3. Click the green "Authorize" button (🔓) near the top of Swagger UI
   → A modal opens

4. In the "Value" field, enter:
   Bearer eyJhbGciOiJSUzI1NiJ9...

   (Replace with your actual RS256 access_token from step 2)

5. Click "Authorize"
6. Click "Close"
```

#### Part C: Test Authenticated Endpoints

```
7. Expand GET /auth/account
   → Click "Try it out"
   → Click "Execute"
   → Expected: 200 OK, user data returned

8. Expand POST /auth/send-verify-email
   → Click "Try it out"
   → Click "Execute"
   → Expected: 200 OK
   → Check logs for the verification link

9. Expand POST /auth/logout
   → Click "Try it out"
   → Click "Execute"
   → Expected: 200 OK
   → The token is now revoked

10. Try GET /auth/account again
    → Click "Execute"
    → Expected: 401 UNAUTHENTICATED
    → Swagger may still show the old token in the auth dialog

11. Click "Authorize" (🔓) again
    → Click "Logout" to clear the stored token
    → Close the dialog
```

### Limitations of Swagger UI for Auth Testing

| Feature | Swagger UI Support | Alternative |
|---|---|---|
| Bearer token in headers | ✅ Full support via Authorize dialog | — |
| HttpOnly cookies | ❌ Not supported | Use Postman or cURL |
| Token refresh (`/auth/refresh`) | ❌ Cannot send cookies | Use Postman with cookie manager |
| Token revocation verification | ⚠️ Partial (must manually clear auth) | Use Postman |
| Rate limit testing | ❌ Manual | Use automation script |

### Using Postman for Cookie-Based Endpoints

```json
// Collection: Ecommerce Auth Tests
// Environment: localhost:8080

// Request 1: Login
POST {{baseUrl}}/auth/login
Content-Type: application/json

{
  "username": "testuser",
  "password": "password123"
}

// Postman settings:
// - Disable "Automatically follow redirects"
// - Enable "Save response cookies"

// Response: Save access_token from response.data.access_token
// Cookie: refresh_token is automatically saved by Postman

// Request 2: Refresh Token (rotation)
// Postman: Enable "Automatically add cookies if response has one"
// The refresh_token cookie will be sent automatically
POST {{baseUrl}}/auth/auth/refresh

// Response: New access_token and new refresh_token cookie
// Old refresh_token is now in used_refresh: Redis key

// Request 3: Reuse Attack Simulation
// Manually set the OLD refresh_token cookie (from Request 1)
POST {{baseUrl}}/auth/auth/refresh
Cookie: refresh_token=<token-from-request-1>
// Expected: 400 INVALID_REFRESH_TOKEN
// All user sessions revoked (check Redis: revoked_user:username)

// Request 4: Logout
POST {{baseUrl}}/auth/logout
Authorization: Bearer {{access_token}}
// Cookie will be sent and invalidated
```

---

## Appendix A: Rate Limiting Configuration

| Endpoint Group | Limit | Window | Redis Key Pattern |
|---|---|---|---|
| `/auth/login`, `/auth/register`, `/auth/refresh` | 10 requests | 1 minute | `rate_limit:{ip}:{minute}` |
| POST, PUT, DELETE (other endpoints) | 30 requests | 1 minute | `rate_limit:{ip}:{minute}` |
| GET requests | 120 requests | 1 minute | `rate_limit:{ip}:{minute}` |

---

## Appendix B: Token Expiry Configuration

| Token / Code | Expiry | Config Key | Default |
|---|---|---|---|
| Access Token (JWT, RS256) | 15 minutes | `JWT_ACCESS_TOKEN_VALIDITY_SECONDS` | 900 |
| Refresh Token (UUID) | 7 days | `JWT_REFRESH_TOKEN_VALIDITY_SECONDS` | 604800 |
| Email Verification Token | 15 minutes | `app.email.verify-token-expiry-minutes` | 15 min |
| Account Lockout Duration | 15 minutes | (hardcoded in `AuthenticationService.login`) | 15 min |
| Failed Login Attempts Before Lock | 5 attempts | (hardcoded in `AuthenticationService.login`) | 5 |

---

## Appendix C: Redis Keys Reference

| Key Pattern | Value | TTL | Purpose |
|---|---|---|---|
| `refresh_token:<uuid>` | Username | 7 days | Active refresh token storage |
| `used_refresh:<uuid>` | Username | 7 days | Used/rotated-out refresh tokens (for reuse detection) |
| `revoked_token:<jwt>` | `"true"` | Remaining access token lifetime | Blacklisted access tokens |
| `revoked_user:<username>` | `"1"` | 15 minutes (access TTL) | User-level revocation (role change, refresh reuse) |
| `rate_limit:<ip>:<minute>` | Request count | 2 minutes | Rate limiting counter |

---

## Appendix D: JWT Decoding Examples

```bash
# Using jq to decode JWT payload (works for any algorithm)
ACCESS_TOKEN="eyJhbGciOiJSUzI1NiJ9..."

# Decode header
echo "$ACCESS_TOKEN" | cut -d. -f1 | base64 -d 2>/dev/null

# Decode payload
echo "$ACCESS_TOKEN" | cut -d. -f2 | base64 -d 2>/dev/null

# Verify RS256 signature
openssl dgst -sha256 -verify jwt-public.pem -signature <(echo "$ACCESS_TOKEN" | cut -d. -f3 | base64 -d) <(echo "$ACCESS_TOKEN" | cut -d. -f1,2 -d.)
```

---

*Document generated for Swagger-based API testing of the Ecommerce Auth module. Updated for P0 security fixes: RS256 migration, account lockout, refresh token rotation, role claims, default-deny interceptor.*
