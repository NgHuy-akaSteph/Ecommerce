package com.myapp.ecommerce.configuration;

import com.myapp.ecommerce.exception.AppException;
import com.myapp.ecommerce.service.InvalidatedTokenService;
import com.myapp.ecommerce.util.SecurityUtil;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import com.myapp.ecommerce.exception.ErrorCode;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class SecurityJwtConfig {

    InvalidatedTokenService invalidatedTokenService;

    @NonFinal
    @Value("${app.jwt.signerKey}")
    String signerKey;

    private SecretKey getSecretKey(String base64Key) {
        byte[] keyBytes = java.util.Base64.getDecoder().decode(base64Key);
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, SecurityUtil.JWT_ALGORITHM.getName());
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(getSecretKey(signerKey)));
    }

    @Bean
    public JwtDecoder jwtDecoder() {

        //Create decoder for signerKey and refreshKey
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(getSecretKey(signerKey))
                .macAlgorithm(SecurityUtil.JWT_ALGORITHM).build();

        return token -> {
            try {
                if(invalidatedTokenService.checkToken(token)){
                    throw new AppException(ErrorCode.UNAUTHENTICATED);
                }

                Jwt jwt = jwtDecoder.decode(token);
                //Check type token
                if("refresh".equals(jwt.getClaims().get("token_type"))){
                    jwt = jwtDecoder.decode(token);
                }
                return jwt;
            } catch (Exception e) {
                System.out.println("JWT ERROR: " + e.getMessage());
                throw e;
            }
        };
    }

    @Bean
    //Convert JWT to Authentication -> get Authorities from JWT
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("");
        grantedAuthoritiesConverter.setAuthoritiesClaimName("permission");
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

}
