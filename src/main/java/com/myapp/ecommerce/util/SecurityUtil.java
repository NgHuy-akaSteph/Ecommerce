package com.myapp.ecommerce.util;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class SecurityUtil {
    @Value("${app.jwt.signerKey}")
    @NonFinal
    String signerKey;

    @Value("${app.jwt.refreshKey}")
    @NonFinal
    String refreshKey;

    @Value("${app.jwt.access-token-validity-in-seconds}")
    @NonFinal
    private long accessTokenExpiration;

    @Value("${app.jwt.refresh-token-validity-in-seconds}")
    @NonFinal
    private long refreshTokenExpiration;

}
