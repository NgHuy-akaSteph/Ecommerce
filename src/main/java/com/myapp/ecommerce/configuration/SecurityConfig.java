package com.myapp.ecommerce.configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final String[] PUBLIC_ENDPOINTS = {
            "/", "/auth/login", "/auth/register", "/auth/refresh",
            "/roles/**"
    };

    private final String[] PUBLIC_GET_ENDPOINTS = {
            "/products/**", "/categories/**", "/tags/**"
    };

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain (HttpSecurity httpSecurity,
                                            CustomAuthenticationEntryPoint customAuthEntryPoint) throws Exception {

        // Authorization Configuration
        httpSecurity.authorizeHttpRequests(request ->
                request
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .requestMatchers(HttpMethod.GET, PUBLIC_GET_ENDPOINTS).permitAll()
                        .anyRequest().authenticated()
        );
        // JWT Configuration
        httpSecurity.oauth2ResourceServer(oauth2
                -> oauth2.jwt(Customizer.withDefaults())
                .authenticationEntryPoint(customAuthEntryPoint));

        // Disable CSRF and Form Login
        httpSecurity.csrf(AbstractHttpConfigurer::disable)
                    .cors(Customizer.withDefaults())
                    .formLogin(AbstractHttpConfigurer::disable)
                    .sessionManagement(session
                            -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return httpSecurity.build();
    }
}
