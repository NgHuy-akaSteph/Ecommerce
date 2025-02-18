package com.myapp.ecommerce.configuration;

import com.myapp.ecommerce.exception.AppException;
import com.myapp.ecommerce.service.InvalidatedTokenService;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import com.myapp.ecommerce.exception.ErrorCode;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class SecurityJwtConfig {

    InvalidatedTokenService invalidatedTokenService;

    @NonFinal
    @Value("${app.jwt.accessKey}")
    String accessKey;

    @NonFinal
    @Value("${app.jwt.refreshKey}")
    String refreshKey;

    private SecretKey getSecretKey(String base64Key) {
        byte[] keyBytes = java.util.Base64.getDecoder().decode(base64Key);
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, "HS256");
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(getSecretKey(accessKey)));
    }

    @Bean
    public JwtDecoder jwtDecoder() {

        //Create decoder for signerKey and refreshKey
        NimbusJwtDecoder accessDecoder = NimbusJwtDecoder.withSecretKey(getSecretKey(accessKey))
                .macAlgorithm(MacAlgorithm.HS256).build();

        NimbusJwtDecoder refreshDecoder = NimbusJwtDecoder.withSecretKey(getSecretKey(refreshKey))
                .macAlgorithm(MacAlgorithm.HS256).build();

        return token -> {
            try {
                if(invalidatedTokenService.checkToken(token)){
                    throw new AppException(ErrorCode.UNAUTHENTICATED);
                }

                Jwt jwt = accessDecoder.decode(token);
                //Check type token
                if("refresh".equals(jwt.getClaims().get("token_type"))){
                    jwt = refreshDecoder.decode(token);
                }
                return jwt;
            } catch (Exception e) {
                System.out.println("JWT ERROR: " + e.getMessage());
                throw e;
            }
        };
    }

}
