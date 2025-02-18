package com.myapp.ecommerce.service;

import com.myapp.ecommerce.dto.request.AuthenticationRequest;
import com.myapp.ecommerce.dto.request.IntrospectRequest;
import com.myapp.ecommerce.dto.request.UserCreationRequest;
import com.myapp.ecommerce.dto.response.AuthenticationResponse;
import com.myapp.ecommerce.dto.response.IntrospectResponse;
import com.myapp.ecommerce.dto.response.UserResponse;
import com.myapp.ecommerce.entity.InvalidatedToken;
import com.myapp.ecommerce.entity.User;
import com.myapp.ecommerce.exception.AppException;
import com.myapp.ecommerce.mapper.UserMapper;
import com.myapp.ecommerce.repository.InvalidatedTokenRepository;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import com.myapp.ecommerce.exception.ErrorCode;

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
    InvalidatedTokenRepository invalidatedTokenRepository;
    RoleService roleService;
    SecurityUtil securityUtil;

    @Value("${app.jwt.refreshKey}")
    @NonFinal
    String refreshKey;

    @Value("${app.jwt.refresh-token-validity-in-seconds}")
    @NonFinal
    long refreshTokenExpiration;

    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();
        boolean isValid = true;

        try {
            vertifyToken(token, false);
        } catch (AppException e) {
            isValid = false;
        }

        return IntrospectResponse.builder().valid(isValid).build();
    }

    public ResponseEntity<AuthenticationResponse> login(AuthenticationRequest request) throws JOSEException {

        // Nap input gom username va password vao Security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                request.getUsername(), request.getPassword()
        );
        //Xac thuc user bang AuthenticationManagerBuilder -> viet ham loadUserByUsername trong CustomUserDetailsService
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        // Luu thong tin xac thuc vao SecurityContextHolder de su dung sau nay
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Tra ve thong tin user trong body response
        AuthenticationResponse authResponse = new AuthenticationResponse();
        User currentUser = userService.getUserByUsername(request.getUsername());
        if(currentUser != null){
            authResponse.setUser(userMapper.toUserResponse(currentUser));
        }

        // Tao token
        String accessToken = securityUtil.generateAccessToken(request.getUsername(), authResponse.getUser());
        authResponse.setAccessToken(accessToken);

        // Tao refresh token
        String refreshToken = securityUtil.generateRefreshToken(request.getUsername(), authResponse);
        authResponse.setRefreshToken(refreshToken);
        userService.updateUserToken(refreshToken, request.getUsername());

        //Thiet lap cookie cho response
        ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(false) // true: chi co server moi co the doc cookie
                .secure(true) // true: chi gui cookie qua https
                .maxAge(refreshTokenExpiration)
                .path("/") // duong dan co the truy cap cookie
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(authResponse);
    }

    public ResponseEntity<UserResponse> register(UserCreationRequest request) throws AppException {
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if(request.getRole() == null){
            user.setRole(roleService.findByName("USER"));
        }
        else {
            user.setRole(roleService.findByName(request.getRole()));
        }

        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException exp) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        UserResponse response = userMapper.toUserResponse(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    public ResponseEntity<UserResponse> getAccount() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByUsername(name);
        UserResponse response = userMapper.toUserResponse(user);
        return ResponseEntity.ok().body(response);
    }

    public ResponseEntity<Void> logout(String authorizationHeader) throws AppException, ParseException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if(username == null){
            throw new AppException(ErrorCode.INVALID_ACCESS_TOKEN);
        }
        User user = userService.getUserByUsername(username);
        if(user != null){
            userService.handleUserLogout(user);
        }
        String token = "";
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
            token = authorizationHeader.substring(7);
        }
        SignedJWT signedJWT = SignedJWT.parse(token);
        InvalidatedToken invalidatedToken = InvalidatedToken.builder().accessToken(token).build();
        invalidatedTokenRepository.save(invalidatedToken);

        //xoa refresh token khoi cookie
        ResponseCookie deleteCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(false)
                .secure(true)
                .maxAge(0)
                .path("/")
                .build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, deleteCookie.toString()).body(null);
    }

    public ResponseEntity<AuthenticationResponse> refreshToken(String refreshToken) throws JOSEException, ParseException {
        if(refreshToken.equals("default")){
            throw new AppException(ErrorCode.COOKIES_EMPTY);
        }

        //Check valid token
        Jwt decodedToken = securityUtil.decodeToken(refreshToken);
        var isVertified = vertifyToken(refreshToken, true);

        //Check user by refreshToken and username (2nd layer check)
        String username = decodedToken.getSubject();
        User currentUser = userService.getUserByUsernameAndRefreshToken(username, refreshToken);
        if(currentUser == null){
            throw new AppException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // issue new access token
        AuthenticationResponse authResponse = new AuthenticationResponse();
        User currentUserDB = userService.getUserByUsername(username);
        if(currentUserDB != null){
            authResponse = AuthenticationResponse.builder()
                    .user(userMapper.toUserResponse(currentUserDB)).build();
        }
        // create new access token and refresh token for user
        String accessToken = securityUtil.generateAccessToken(username, authResponse.getUser());
        authResponse.setAccessToken(accessToken);
        String newRefreshToken = securityUtil.generateRefreshToken(username, authResponse);
        authResponse.setRefreshToken(newRefreshToken);
        userService.updateUserToken(newRefreshToken, username);

        //set cookies
        ResponseCookie cookie = ResponseCookie.from("refresh_token", newRefreshToken)
                .httpOnly(true)
                .secure(true)
                .maxAge(refreshTokenExpiration)
                .path("/")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(authResponse);
    }

    private SignedJWT vertifyToken(String token, boolean isRefresh) throws ParseException, JOSEException {
        JWSVerifier verifier = new MACVerifier(refreshKey.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expirationTime = (isRefresh) ?
                new Date(signedJWT.getJWTClaimsSet().getIssueTime()
                        .toInstant().plus(refreshTokenExpiration, ChronoUnit.SECONDS)
                        .toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        if(!expirationTime.after(new Date()))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        if(invalidatedTokenRepository.existsByAccessToken(token))
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if(username == null){
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        return signedJWT;
    }









}
