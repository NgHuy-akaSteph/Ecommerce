package com.myapp.ecommerce.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myapp.ecommerce.dto.response.ApiResponse;
import com.myapp.ecommerce.exception.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RateLimitingFilter extends OncePerRequestFilter {

    StringRedisTemplate stringRedisTemplate;
    ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();
        String method = request.getMethod();

        // 1. Determine limit based on API endpoint
        int limit = 120; // Default: 120 requests/minute for general read APIs
        if (uri.contains("/auth/login") || uri.contains("/auth/register") || uri.contains("/auth/refresh")) {
            limit = 10; // Strict limit: 10 requests/minute for sensitive auth APIs
        } else if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method) || "DELETE".equalsIgnoreCase(method)) {
            limit = 30; // Medium limit: 30 requests/minute for state-changing APIs
        }

        // 2. Identify client (IP or Authenticated Username)
        String clientKey = request.getRemoteAddr();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
            clientKey = authentication.getName();
        }

        // 3. Redis Rate Limiting using Fixed Window
        long currentMinute = System.currentTimeMillis() / 60000;
        String redisKey = "rate_limit:" + clientKey + ":" + currentMinute;

        Long currentCount = stringRedisTemplate.opsForValue().increment(redisKey);
        if (currentCount != null && currentCount == 1) {
            stringRedisTemplate.expire(redisKey, Duration.ofMinutes(2)); // Set TTL
        }

        if (currentCount != null && currentCount > limit) {
            log.warn("Rate limit exceeded for client: {} on URI: {}. Current count: {}, limit: {}",
                    clientKey, uri, currentCount, limit);
            
            // Build Error Response
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");

            ApiResponse<?> apiResponse = ApiResponse.builder()
                    .statusCode(ErrorCode.TOO_MANY_REQUESTS.getCode())
                    .message(ErrorCode.TOO_MANY_REQUESTS.getMessage())
                    .build();

            response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
            return;
        }

        filterChain.doFilter(request, response);
    }
}
