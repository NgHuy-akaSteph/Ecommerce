package com.myapp.ecommerce.service.impl;

import com.myapp.ecommerce.service.InvalidatedTokenService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

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
}
