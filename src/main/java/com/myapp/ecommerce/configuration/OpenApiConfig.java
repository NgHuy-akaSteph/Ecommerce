package com.myapp.ecommerce.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(List.of(
                        new Server().url("http://localhost:8080/api/v1").description("Local Development")
                ))
                .info(new Info().title("E-Commerce API")
                        .description("Spring Boot REST API for E-Commerce system")
                        .version("v1.0.0"))
                .components(new Components()
                        .addSecuritySchemes("bearer-key",
                                new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("bearer-key"))
                .tags(List.of(
                        new Tag().name("Authentication").description("Login, register, verify, OTP"),
                        new Tag().name("Users").description("User profile and management"),
                        new Tag().name("Products").description("Product CRUD operations"),
                        new Tag().name("Categories").description("Category management"),
                        new Tag().name("Tags").description("Tag management"),
                        new Tag().name("Variants").description("Product variants: options, values, SKU, and stock"),
                        new Tag().name("Cart").description("Shopping cart operations"),
                        new Tag().name("Orders").description("Order placement and management"),
                        new Tag().name("Admin").description("Roles, permissions, and reports"),
                        new Tag().name("Files").description("File upload and storage")
                ));
    }
}
