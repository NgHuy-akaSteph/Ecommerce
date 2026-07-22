package com.myapp.ecommerce.controller;

import com.myapp.ecommerce.dto.request.AuthenticationRequest;
import com.myapp.ecommerce.dto.request.ResendVerificationRequest;
import com.myapp.ecommerce.dto.request.UserRegisterRequest;
import com.myapp.ecommerce.dto.response.ApiResponse;
import com.myapp.ecommerce.dto.response.AuthenticationResponse;
import com.myapp.ecommerce.dto.response.UserResponse;
import com.myapp.ecommerce.exception.AppException;
import com.myapp.ecommerce.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Authentication", description = "Login, register, verify email")
public class AuthenticationController {

    AuthenticationService authenticationService;

    @PostMapping("/login")
    ResponseEntity<ApiResponse<AuthenticationResponse>> login(@RequestBody AuthenticationRequest request) throws Exception {
        AuthenticationService.LoginResult result = authenticationService.login(request);

        ResponseCookie cookie = ResponseCookie.from("refresh_token", result.refreshToken())
                .httpOnly(true)
                .secure(true)
                .maxAge(result.tokenExpiration())
                .path("/")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(ApiResponse.ok("Login successfully", result.response()));
    }

    @PostMapping("/register")
    ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody UserRegisterRequest request) throws AppException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Register successfully", authenticationService.register(request)));
    }

    @GetMapping("/account")
    ResponseEntity<ApiResponse<UserResponse>> getAccount() {
        return ResponseEntity.ok()
                .body(ApiResponse.ok("Get account successfully", authenticationService.getAccount()));
    }

    @PostMapping("/logout")
    ResponseEntity<ApiResponse<Void>> logout(@RequestHeader("Authorization") String authorizationHeader)
            throws AppException, ParseException {
        authenticationService.logout(authorizationHeader);

        ResponseCookie deleteCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(true)
                .maxAge(0)
                .path("/")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .body(ApiResponse.ok("Logout successfully", null));
    }

    @PostMapping("/refresh")
    ResponseEntity<ApiResponse<AuthenticationResponse>> refreshToken(
            @CookieValue(name = "refresh_token", defaultValue = "default") String refreshToken
    ) throws AppException, JOSEException, ParseException {
        AuthenticationService.LoginResult result = authenticationService.refreshToken(refreshToken);

        ResponseCookie cookie = ResponseCookie.from("refresh_token", result.refreshToken())
                .httpOnly(true)
                .secure(true)
                .maxAge(result.tokenExpiration())
                .path("/")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(ApiResponse.ok("Refresh token successfully", result.response()));
    }

    @GetMapping("/verify-email")
    ResponseEntity<ApiResponse<Void>> verifyEmail(@RequestParam String token) throws AppException {
        return ResponseEntity.ok(authenticationService.verifyEmail(token));
    }

    @PostMapping("/resend-verification")
    ResponseEntity<ApiResponse<Void>> resendVerification(@Valid @RequestBody ResendVerificationRequest request)
            throws AppException {
        return ResponseEntity.ok(authenticationService.resendVerification(request.getEmail()));
    }

    @PostMapping("/send-verify-email")
    ResponseEntity<ApiResponse<Void>> sendVerifyEmail() throws AppException {
        return ResponseEntity.ok(authenticationService.sendVerifyEmail());
    }
}
