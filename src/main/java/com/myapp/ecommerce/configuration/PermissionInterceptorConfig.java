package com.myapp.ecommerce.configuration;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class PermissionInterceptorConfig implements WebMvcConfigurer {

    private final PermissionInterceptor permissionInterceptor;

    public PermissionInterceptorConfig(PermissionInterceptor permissionInterceptor) {
        this.permissionInterceptor = permissionInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String[] whitelist = {
                "/",
                "/auth/**",
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/swagger-ui.html",
                "/webjars/**",
                "/favicon.ico"
        };
        registry.addInterceptor(permissionInterceptor)
                .excludePathPatterns(whitelist);
    }


}
