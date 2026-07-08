package com.myapp.ecommerce.util;


import com.myapp.ecommerce.dto.response.UserInToken;
import com.myapp.ecommerce.dto.response.UserResponse;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class SecurityUtil {

    public static SignatureAlgorithm JWT_ALGORITHM = SignatureAlgorithm.RS256;

    @Value("${app.jwt.private-key-path}")
    @NonFinal
    Resource privateKeyResource;

    @Value("${app.jwt.public-key-path}")
    @NonFinal
    Resource publicKeyResource;

    @Value("${app.jwt.access-token-validity-seconds}")
    @NonFinal
    long tokenExpiration;

    private RSAPrivateKey loadPrivateKey() {
        try {
            String pem = new String(privateKeyResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8)
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");
            byte[] der = Base64.getDecoder().decode(pem);
            KeyFactory factory = KeyFactory.getInstance("RSA");
            return (RSAPrivateKey) factory.generatePrivate(new PKCS8EncodedKeySpec(der));
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("Cannot load JWT private key from " + privateKeyResource, e);
        }
    }

    public RSAPublicKey loadPublicKey() {
        try {
            String pem = new String(publicKeyResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8)
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s+", "");
            byte[] der = Base64.getDecoder().decode(pem);
            KeyFactory factory = KeyFactory.getInstance("RSA");
            return (RSAPublicKey) factory.generatePublic(new X509EncodedKeySpec(der));
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("Cannot load JWT public key from " + publicKeyResource, e);
        }
    }

    public RSASSAVerifier buildVerifier() throws JOSEException {
        return new RSASSAVerifier(loadPublicKey());
    }

    public String generateAccessToken(String username, UserResponse userResponse) throws JOSEException {
        JWSHeader header = new JWSHeader(JWSAlgorithm.RS256);

        UserInToken user = UserInToken.builder()
                .id(userResponse.getId())
                .username(username)
                .role(userResponse.getRoleName())
                .build();

        Instant now = Instant.now();
        Instant validity = now.plus(tokenExpiration, ChronoUnit.SECONDS);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(username)
                .claim("user", user)
                .claim("roles", List.of("ROLE_" + userResponse.getRoleName()))
                .issueTime(Date.from(now))
                .expirationTime(Date.from(validity))
                .build();

        SignedJWT signedJWT = new SignedJWT(header, claimsSet);
        signedJWT.sign(new RSASSASigner(loadPrivateKey()));
        return signedJWT.serialize();
    }


    public String generateRefreshToken() {
        return java.util.UUID.randomUUID().toString();
    }


    public static Optional<String> getCurrentUserLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(extractPrincipal(securityContext.getAuthentication()));
    }

    private static String extractPrincipal(Authentication authentication) {
        if (authentication == null) {
            return null;
        } else if (authentication.getPrincipal() instanceof UserDetails springSecurityUser) {
            return springSecurityUser.getUsername();
        } else if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getSubject();
        } else if (authentication.getPrincipal() instanceof String s) {
            return s;
        }
        return null;
    }


}