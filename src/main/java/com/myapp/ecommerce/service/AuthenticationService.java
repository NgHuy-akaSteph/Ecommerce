package com.myapp.ecommerce.service;

import com.myapp.ecommerce.dto.request.AuthenticationRequest;
import com.myapp.ecommerce.dto.request.IntrospectRequest;
import com.myapp.ecommerce.dto.request.UserRegisterRequest;
import com.myapp.ecommerce.dto.response.AuthenticationResponse;
import com.myapp.ecommerce.dto.response.IntrospectResponse;
import com.myapp.ecommerce.dto.response.UserResponse;
import com.myapp.ecommerce.entity.User;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.myapp.ecommerce.exception.ErrorCode;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.text.ParseException;
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
        // Authenticate user via Spring Security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                request.getUsername(), request.getPassword()
        );
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Build response with user info
        AuthenticationResponse authResponse = new AuthenticationResponse();
        User currentUser = userService.getUserByUsername(request.getUsername());
        if (currentUser != null) {
            authResponse.setUser(userMapper.toUserResponse(currentUser));
        }

        // Generate tokens
        String accessToken = securityUtil.generateAccessToken(request.getUsername(), authResponse.getUser());
        authResponse.setAccessToken(accessToken);

        // Opaque Refresh Token
        String refreshToken = securityUtil.generateRefreshToken();
        authResponse.setRefreshToken(refreshToken);
        
        // Save Refresh Token in Redis with TTL
        stringRedisTemplate.opsForValue().set(
                "refresh_token:" + refreshToken,
                request.getUsername(),
                java.time.Duration.ofSeconds(tokenExpiration)
        );
        
        userService.updateUserToken(refreshToken, request.getUsername());

        return new LoginResult(authResponse, refreshToken, tokenExpiration);
    }

    public UserResponse register(UserRegisterRequest request) throws AppException {
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(roleService.findByName("USER"));

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

    public UserResponse getAccount() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByUsername(name);
        return userMapper.toUserResponse(user);
    }

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
        
        // Revoke token using Redis
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

    public LoginResult refreshToken(String refreshToken) throws JOSEException, ParseException {
        if (refreshToken.equals("default")) {
            throw new AppException(ErrorCode.COOKIES_EMPTY);
        }

        // 1. Get username from Redis using the opaque token
        String username = stringRedisTemplate.opsForValue().get("refresh_token:" + refreshToken);
        if (username == null) {
            throw new AppException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 2. Check user by refreshToken and username in database
        User currentUser = userService.getUserByUsernameAndRefreshToken(username, refreshToken);
        if (currentUser == null) {
            throw new AppException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 3. Delete old refresh token from Redis
        stringRedisTemplate.delete("refresh_token:" + refreshToken);

        // 4. Issue new tokens
        AuthenticationResponse authResponse = new AuthenticationResponse();
        User currentUserDB = userService.getUserByUsername(username);
        if (currentUserDB != null) {
            authResponse = AuthenticationResponse.builder()
                    .user(userMapper.toUserResponse(currentUserDB)).build();
        }

        String accessToken = securityUtil.generateAccessToken(username, authResponse.getUser());
        authResponse.setAccessToken(accessToken);
        
        // Generate new Opaque Refresh Token
        String newRefreshToken = securityUtil.generateRefreshToken();
        authResponse.setRefreshToken(newRefreshToken);
        
        // Save new Refresh Token in Redis with TTL
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
