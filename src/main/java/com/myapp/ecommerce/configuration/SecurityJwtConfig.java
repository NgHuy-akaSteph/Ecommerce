package com.myapp.ecommerce.configuration;

import com.myapp.ecommerce.exception.AppException;
import com.myapp.ecommerce.service.InvalidatedTokenService;
import com.myapp.ecommerce.exception.ErrorCode;
import com.nimbusds.jose.JWSAlgorithm;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class SecurityJwtConfig {

    InvalidatedTokenService invalidatedTokenService;

    @NonFinal
    @Value("${app.jwt.public-key-path}")
    Resource publicKeyResource;

    private RSAPublicKey loadPublicKey() {
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

    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder
                .withPublicKey(loadPublicKey())
                .signatureAlgorithm(SignatureAlgorithm.RS256)
                .build();

        return token -> {
            try {
                if (invalidatedTokenService.checkToken(token)) {
                    throw new AppException(ErrorCode.UNAUTHENTICATED);
                }

                Jwt jwt = jwtDecoder.decode(token);

                String subject = jwt.getSubject();
                if (subject != null && invalidatedTokenService.isUserRevoked(subject)) {
                    throw new AppException(ErrorCode.UNAUTHENTICATED);
                }

                if ("refresh".equals(jwt.getClaims().get("token_type"))) {
                    jwt = jwtDecoder.decode(token);
                }
                return jwt;
            } catch (Exception e) {
                log.error("JWT ERROR: {}", e.getMessage());
                throw e;
            }
        };
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("");
        grantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

}