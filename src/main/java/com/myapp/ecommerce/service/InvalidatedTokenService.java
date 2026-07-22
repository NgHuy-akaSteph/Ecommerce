package com.myapp.ecommerce.service;

import java.time.Duration;

public interface InvalidatedTokenService {

    boolean checkToken(String token);

    void clearToken(String token);

    void invalidateAllTokensForUser(String username, Duration ttl);

    boolean isUserRevoked(String username);

    // Refresh-token rotation support

    String getUsernameByRefreshToken(String refreshToken);

    void saveRefreshToken(String username, String refreshToken, Duration ttl);

    boolean isRefreshTokenUsed(String refreshToken);

    String getUsernameByUsedRefreshToken(String refreshToken);

    void markRefreshTokenAsUsed(String refreshToken, String username, Duration ttl);
}