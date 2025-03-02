package com.myapp.ecommerce.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myapp.ecommerce.dto.response.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;


/**
 * CustomAuthenticationEntryPoint
 * This class is used to handle exceptions in the authentication process
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final AuthenticationEntryPoint delegate = new BearerTokenAuthenticationEntryPoint();

    private final ObjectMapper objectMapper;

    public CustomAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        //Call BearerTokenAuthenticationEntryPoint to handle the first part of the exception
        delegate.commence(request, response, authException);

        response.setContentType("application/json;charset=UTF-8");

        //Check if the exception has a cause, if not, use the exception message
        String errorMessage = Optional.ofNullable(authException.getCause())
                .map(Throwable::getMessage).orElse(authException.getMessage());

        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .message("Token is invalid or expired")
                .error(errorMessage)
                .build();
        // Write the response to the client
        objectMapper.writeValue(response.getWriter(), apiResponse);
    }
}
