package com.myapp.ecommerce.service.impl;

import com.myapp.ecommerce.service.InvalidatedTokenService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InvalidatedTokenServiceImpl implements InvalidatedTokenService {

    StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean checkToken(String token) {
        Boolean hasKey = stringRedisTemplate.hasKey("revoked_token:" + token);
        return hasKey != null && hasKey;
    }

    @Override
    public void clearToken(String token) {
        // Redis manages expiration automatically via TTL, no manual cleanup needed
    }

    @Override
    public void invalidateAllTokensForUser(String username, Duration ttl) {
        stringRedisTemplate.opsForValue().set("revoked_user:" + username, "1", ttl);
    }

    @Override
    public boolean isUserRevoked(String username) {
        Boolean hasKey = stringRedisTemplate.hasKey("revoked_user:" + username);
        return hasKey != null && hasKey;
    }

    @Override
    public String getUsernameByRefreshToken(String refreshToken) {
        return stringRedisTemplate.opsForValue().get("refresh_token:" + refreshToken);
    }

    @Override
    public void saveRefreshToken(String username, String refreshToken, Duration ttl) {
        stringRedisTemplate.opsForValue().set("refresh_token:" + refreshToken, username, ttl);
    }

    @Override
    public boolean isRefreshTokenUsed(String refreshToken) {
        Boolean hasKey = stringRedisTemplate.hasKey("used_refresh:" + refreshToken);
        return hasKey != null && hasKey;
    }

    @Override
    public String getUsernameByUsedRefreshToken(String refreshToken) {
        return stringRedisTemplate.opsForValue().get("used_refresh:" + refreshToken);
    }

    @Override
    public void markRefreshTokenAsUsed(String refreshToken, String username, Duration ttl) {
        stringRedisTemplate.opsForValue().set("used_refresh:" + refreshToken, username, ttl);
    }
}