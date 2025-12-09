package com.coopcredit.credit_application_service.infrastructure.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration: OpenApiConfig
 * Configures Swagger/OpenAPI documentation with JWT Bearer token security
 */
@Configuration
public class OpenApiConfig {

    /**
     * Configure OpenAPI bean with application metadata and JWT security scheme
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CoopCredit API")
                        .version("1.0")
                        .description("Credit Application System - Hexagonal Architecture")
                )
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter JWT token")
                        )
                );
    }
}
