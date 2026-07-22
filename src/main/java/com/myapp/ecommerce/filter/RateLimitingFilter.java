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
import java.util.List;

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

        int limit = 120;
        if (uri.contains("/auth/login") || uri.contains("/auth/register") || uri.contains("/auth/refresh")) {
            limit = 10;
        } else if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method) || "DELETE".equalsIgnoreCase(method)) {
            limit = 30;
        }

        String clientKey = resolveClientIp(request);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
            clientKey = authentication.getName();
        }

        long currentMinute = System.currentTimeMillis() / 60000;
        String redisKey = "rate_limit:" + clientKey + ":" + currentMinute;

        Long currentCount = stringRedisTemplate.opsForValue().increment(redisKey);
        if (currentCount != null && currentCount == 1) {
            stringRedisTemplate.expire(redisKey, Duration.ofMinutes(2));
        }

        if (currentCount != null && currentCount > limit) {
            log.warn("Rate limit exceeded for client: {} on URI: {}. Current count: {}, limit: {}",
                    clientKey, uri, currentCount, limit);

            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Retry-After", "60");
            response.setHeader("X-RateLimit-Limit", String.valueOf(limit));
            response.setHeader("X-RateLimit-Remaining", "0");
            response.setHeader("X-RateLimit-Reset", String.valueOf((currentMinute + 1) * 60000));

            ApiResponse<?> apiResponse = ApiResponse.builder()
                    .statusCode(ErrorCode.TOO_MANY_REQUESTS.getCode())
                    .message(ErrorCode.TOO_MANY_REQUESTS.getMessage())
                    .build();

            response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String resolveClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            List<String> ips = List.of(xForwardedFor.split(","));
            String realIp = ips.getFirst().trim();
            if (!realIp.isBlank() && !isPrivateIp(realIp)) {
                return realIp;
            }
            for (String ip : ips) {
                String trimmed = ip.trim();
                if (!trimmed.isBlank() && !isPrivateIp(trimmed)) {
                    return trimmed;
                }
            }
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isBlank() && !isPrivateIp(xRealIp.trim())) {
            return xRealIp.trim();
        }
        return request.getRemoteAddr();
    }

    private boolean isPrivateIp(String ip) {
        return ip.startsWith("10.") || ip.startsWith("192.168.") || ip.startsWith("127.")
                || (ip.startsWith("172.") && isPrivate172(ip));
    }

    private boolean isPrivate172(String ip) {
        try {
            int second = Integer.parseInt(ip.split("\\.")[1]);
            return second >= 16 && second <= 31;
        } catch (Exception e) {
            return false;
        }
    }
}
