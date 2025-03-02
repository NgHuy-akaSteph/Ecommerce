package com.myapp.ecommerce.service.impl;

import com.myapp.ecommerce.repository.InvalidatedTokenRepository;
import com.myapp.ecommerce.service.InvalidatedTokenService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InvalidatedTokenServiceImpl implements InvalidatedTokenService {

    InvalidatedTokenRepository invalidatedTokenRepository;

    @Override
    public boolean checkToken(String token) {
        return invalidatedTokenRepository.existsByAccessToken(token);
    }

    // Clear all invalidated tokens at midnight every day
    @Override
    @Scheduled(cron = "0 0 0 * * *")
    public void clearToken(String token) {
        invalidatedTokenRepository.deleteAll();
    }
}
