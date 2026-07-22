package com.myapp.ecommerce.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myapp.ecommerce.dto.response.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;


/**
 * CustomAuthenticationEntryPoint
 * This class is used to handle exceptions in the authentication process
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public CustomAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .success(false)
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .message("Token is invalid or expired")
                .build();
        objectMapper.writeValue(response.getWriter(), apiResponse);
    }
}
