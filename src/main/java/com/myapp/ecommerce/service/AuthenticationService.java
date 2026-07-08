package com.myapp.ecommerce.service;

import com.myapp.ecommerce.dto.request.AuthenticationRequest;
import com.myapp.ecommerce.dto.request.IntrospectRequest;
import com.myapp.ecommerce.dto.request.UserRegisterRequest;
import com.myapp.ecommerce.dto.response.AuthenticationResponse;
import com.myapp.ecommerce.dto.response.ApiResponse;
import com.myapp.ecommerce.dto.response.IntrospectResponse;
import com.myapp.ecommerce.dto.response.UserResponse;
import com.myapp.ecommerce.entity.EmailVerification;
import com.myapp.ecommerce.entity.User;
import com.myapp.ecommerce.entity.enums.VerificationType;
import com.myapp.ecommerce.exception.AppException;
import com.myapp.ecommerce.mapper.UserMapper;
import com.myapp.ecommerce.repository.UserRepository;
import com.myapp.ecommerce.util.SecurityUtil;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.myapp.ecommerce.exception.ErrorCode;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthenticationService {

    AuthenticationManagerBuilder authenticationManagerBuilder;
    UserService userService;
    UserMapper userMapper;
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    RoleService roleService;
    SecurityUtil securityUtil;
    StringRedisTemplate stringRedisTemplate;
    EmailService emailService;
    EmailVerificationService emailVerificationService;

    @Value("${app.jwt.signerKey}")
    @NonFinal
    String signerKey;

    @Value("${app.jwt.token-validity-in-seconds}")
    @NonFinal
    long tokenExpiration;

    /**
     * Result record for login/refresh operations that includes tokens
     * and expiration for cookie creation in the controller.
     */
    public record LoginResult(AuthenticationResponse response, String refreshToken, long tokenExpiration) {}

    @Transactional(readOnly = true)
    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();
        boolean isValid = true;

        try {
            verifyToken(token, false);
        } catch (AppException e) {
            isValid = false;
        }

        return IntrospectResponse.builder().valid(isValid).build();
    }

    public LoginResult login(AuthenticationRequest request) throws JOSEException {
        User existingUser = userService.getUserByUsernameOrEmail(request.getUsername());

        if (existingUser.getLockedUntil() != null && existingUser.getLockedUntil().isAfter(Instant.now())) {
            throw new AppException(ErrorCode.ACCOUNT_LOCKED);
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                request.getUsername(), request.getPassword()
        );

        try {
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            existingUser.setFailedLoginAttempts(0);
            existingUser.setLockedUntil(null);
            userRepository.save(existingUser);
        } catch (BadCredentialsException e) {
            int attempts = (existingUser.getFailedLoginAttempts() == null ? 0 : existingUser.getFailedLoginAttempts()) + 1;
            existingUser.setFailedLoginAttempts(attempts);
            if (attempts >= 5) {
                existingUser.setLockedUntil(Instant.now().plus(15, ChronoUnit.MINUTES));
                existingUser.setFailedLoginAttempts(0);
            }
            userRepository.save(existingUser);
            throw new AppException(ErrorCode.BAD_CREDENTIALS);
        }

        User currentUser = userService.getUserByUsernameOrEmail(request.getUsername());
        AuthenticationResponse authResponse = new AuthenticationResponse();
        if (currentUser != null) {
            authResponse.setUser(userMapper.toUserResponse(currentUser));
        }

        String canonicalUsername = currentUser.getUsername();
        String accessToken = securityUtil.generateAccessToken(canonicalUsername, authResponse.getUser());
        authResponse.setAccessToken(accessToken);

        String refreshToken = securityUtil.generateRefreshToken();
        authResponse.setRefreshToken(refreshToken);

        stringRedisTemplate.opsForValue().set(
                "refresh_token:" + refreshToken,
                canonicalUsername,
                java.time.Duration.ofSeconds(tokenExpiration)
        );

        userService.updateUserToken(refreshToken, canonicalUsername);

        return new LoginResult(authResponse, refreshToken, tokenExpiration);
    }

    @Transactional
    public UserResponse register(UserRegisterRequest request) throws AppException {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(roleService.findByName("USER"));
        user.setEmailVerified(false);

        user = userRepository.save(user);

        // Tạo email verification token (token track được, có thể resend)
        EmailVerification verification = emailVerificationService.createToken(
                user, user.getEmail(), VerificationType.EMAIL_VERIFY);

        // Không tự động gửi mail — user phải gọi /auth/send-verify-email
        log.info("Registered user {} - verification token id={}", user.getUsername(), verification.getId());

        return userMapper.toUserResponse(user);
    }

    @Transactional
    public ApiResponse<Void> verifyEmail(String token) throws AppException {
        EmailVerification verification = emailVerificationService.verify(token, VerificationType.EMAIL_VERIFY);

        User user = verification.getUser();
        if (!user.isEmailVerified()) {
            user.setEmailVerified(true);
            userRepository.save(user);
        }
        return ApiResponse.<Void>ok("Email verified successfully", null);
    }

    @Transactional
    public ApiResponse<Void> resendVerification(String email) throws AppException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (user.isEmailVerified()) {
            return ApiResponse.<Void>ok("Email already verified", null);
        }

        EmailVerification verification = emailVerificationService.createToken(
                user, email, VerificationType.EMAIL_VERIFY);

        emailService.sendVerificationEmail(email, user.getName(), verification.getToken());
        return ApiResponse.<Void>ok("Verification email sent", null);
    }

    @Transactional
    public ApiResponse<Void> sendVerifyEmail() throws AppException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (user.isEmailVerified()) {
            return ApiResponse.<Void>ok("Email already verified", null);
        }

        EmailVerification verification = emailVerificationService.createToken(
                user, user.getEmail(), VerificationType.EMAIL_VERIFY);

        emailService.sendVerificationEmail(user.getEmail(), user.getName(), verification.getToken());
        return ApiResponse.<Void>ok("Verification email sent", null);
    }

    @Transactional(readOnly = true)
    public UserResponse getAccount() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByUsername(name);
        return userMapper.toUserResponse(user);
    }

    @Transactional
    public void logout(String authorizationHeader) throws AppException, ParseException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (username == null) {
            throw new AppException(ErrorCode.INVALID_ACCESS_TOKEN);
        }
        User user = userService.getUserByUsername(username);
        if (user != null) {
            userService.handleUserLogout(user);
        }
        String token = "";
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
        }

        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            long remainingSeconds = (expirationTime.getTime() - System.currentTimeMillis()) / 1000;
            if (remainingSeconds > 0) {
                stringRedisTemplate.opsForValue().set(
                        "revoked_token:" + token,
                        "true",
                        java.time.Duration.ofSeconds(remainingSeconds)
                );
            }
        } catch (Exception e) {
            log.error("Error revoking token: {}", e.getMessage());
        }
    }

    @Transactional
    public LoginResult refreshToken(String refreshToken) throws JOSEException, ParseException {
        if (refreshToken.equals("default")) {
            throw new AppException(ErrorCode.COOKIES_EMPTY);
        }

        String username = stringRedisTemplate.opsForValue().get("refresh_token:" + refreshToken);
        if (username == null) {
            throw new AppException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        User currentUser = userService.getUserByUsernameAndRefreshToken(username, refreshToken);
        if (currentUser == null) {
            throw new AppException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        stringRedisTemplate.delete("refresh_token:" + refreshToken);

        AuthenticationResponse authResponse = new AuthenticationResponse();
        User currentUserDB = userService.getUserByUsername(username);
        if (currentUserDB != null) {
            authResponse = AuthenticationResponse.builder()
                    .user(userMapper.toUserResponse(currentUserDB)).build();
        }

        String accessToken = securityUtil.generateAccessToken(username, authResponse.getUser());
        authResponse.setAccessToken(accessToken);

        String newRefreshToken = securityUtil.generateRefreshToken();
        authResponse.setRefreshToken(newRefreshToken);

        stringRedisTemplate.opsForValue().set(
                "refresh_token:" + newRefreshToken,
                username,
                java.time.Duration.ofSeconds(tokenExpiration)
        );

        userService.updateUserToken(newRefreshToken, username);

        return new LoginResult(authResponse, newRefreshToken, tokenExpiration);
    }


    private SignedJWT verifyToken(String token, boolean isRefresh) throws ParseException, JOSEException {
        JWSVerifier verifier = new MACVerifier(signerKey.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expirationTime = (isRefresh)
                ? new Date(signedJWT.getJWTClaimsSet().getIssueTime()
                    .toInstant().plus(tokenExpiration, ChronoUnit.SECONDS)
                    .toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        boolean verified = signedJWT.verify(verifier);

        if (!verified) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        if (!expirationTime.after(new Date())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        Boolean hasKey = stringRedisTemplate.hasKey("revoked_token:" + token);
        if (hasKey) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        return signedJWT;
    }
}
