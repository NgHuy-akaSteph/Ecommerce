package com.myapp.ecommerce.util;


import com.myapp.ecommerce.dto.response.AuthenticationResponse;
import com.myapp.ecommerce.dto.response.UserInToken;
import com.myapp.ecommerce.dto.response.UserResponse;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.util.Base64;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class SecurityUtil {

    static MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS256;

    @Value("${app.jwt.accessKey}")
    @NonFinal
    String accessKey;

    @Value("${app.jwt.refreshKey}")
    @NonFinal
    String refreshKey;

    @Value("${app.jwt.access-token-validity-in-seconds}")
    @NonFinal
    long accessTokenExpiration;

    @Value("${app.jwt.refresh-token-validity-in-seconds}")
    @NonFinal
    long refreshTokenExpiration;

    public String generateAccessToken(String username, UserResponse userResponse) throws JOSEException {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
        // Thong tin user trong token
        UserInToken user = UserInToken.builder()
                .id(userResponse.getId())
                .username(username)
                .role(userResponse.getRole().getName())
                .build();
        // Time
        Instant now = Instant.now();
        Instant validity = now.plus(accessTokenExpiration, ChronoUnit.SECONDS);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(username)
                .claim("user", user)
                .issueTime(Date.from(now))
                .expirationTime(Date.from(validity))
                .build();

        SignedJWT signedJWT = new SignedJWT(header, claimsSet);
        MACSigner signer = new MACSigner(java.util.Base64.getDecoder().decode(accessKey));
        signedJWT.sign(signer);
        return signedJWT.serialize();
    }


    public String generateRefreshToken(String username, AuthenticationResponse authResponse) throws JOSEException {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
        UserInToken user = UserInToken.builder()
                .id(authResponse.getUser().getId())
                .username(username)
                .role(authResponse.getUser().getRole().getName())
                .build();

        Instant now = Instant.now();
        Instant validity = now.plus(refreshTokenExpiration, ChronoUnit.SECONDS);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(username)
                .claim("user", user)
                .claim("token_type", "refresh")
                .issueTime(Date.from(now))
                .expirationTime(Date.from(validity))
                .build();
        SignedJWT signedJWT = new SignedJWT(header, claimsSet);
        MACSigner signer = new MACSigner(java.util.Base64.getDecoder().decode(refreshKey));
        signedJWT.sign(signer);
        return signedJWT.serialize();
    }

    public Jwt decodeToken(String token) {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(getRefreshSecretKey())
                .macAlgorithm(MacAlgorithm.HS256).build();
        try {
            return jwtDecoder.decode(token);
        } catch (Exception e) {
            log.warn("Error decoding JWT: {}", e.getMessage());
            throw e;
        }
    }

    private SecretKey getRefreshSecretKey() {
        byte[] keyBytes = Base64.from(refreshKey).decode();
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, JWT_ALGORITHM.getName());
    }



}
