package com.myapp.ecommerce.service;

import java.time.Duration;

public interface InvalidatedTokenService {

    boolean checkToken(String token);

    void clearToken(String token);

    void invalidateAllTokensForUser(String username, Duration ttl);

    boolean isUserRevoked(String username);
}