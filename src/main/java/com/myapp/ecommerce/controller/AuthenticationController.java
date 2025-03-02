package com.myapp.ecommerce.controller;

import com.myapp.ecommerce.dto.request.AuthenticationRequest;
import com.myapp.ecommerce.dto.request.UserCreationRequest;
import com.myapp.ecommerce.dto.response.AuthenticationResponse;
import com.myapp.ecommerce.dto.response.UserResponse;
import com.myapp.ecommerce.exception.AppException;
import com.myapp.ecommerce.service.AuthenticationService;
import com.myapp.ecommerce.util.annotation.ApiMessage;
import com.nimbusds.jose.JOSEException;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {

    AuthenticationService authenticationService;

    @PostMapping("/login")
    @ApiMessage("Login successfully")
    ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) throws Exception {
        return authenticationService.login(request);
    }

    @PostMapping("/register")
    @ApiMessage("Register successfully")
    ResponseEntity<UserResponse> register(@Valid @RequestBody UserCreationRequest request) throws AppException {
        return authenticationService.register(request);
    }

    @GetMapping("/account")
    @ApiMessage("Get account successfully")
    ResponseEntity<UserResponse> getAccount() {
        return authenticationService.getAccount();
    }

    @PostMapping("/logout")
    @ApiMessage("Logout successfully")
    ResponseEntity<Void> logout(@RequestHeader("Authorization") String authorizationHeader)
            throws AppException, ParseException {
        return authenticationService.logout(authorizationHeader);
    }

    @PostMapping("/refresh")
    @ApiMessage("Logout successfully")
    ResponseEntity<AuthenticationResponse> refreshToken(
            @CookieValue(name = "refresh_token", defaultValue = "default") String refresh_token
    ) throws AppException, JOSEException, ParseException
    {
        return authenticationService.refreshToken(refresh_token);
    }

}
