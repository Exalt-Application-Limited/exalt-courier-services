package com.exalt.courierservices.commission.$1;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI (Swagger) configuration for the Commission Service.
 */
@Configuration
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenApiConfig {

    @Value("${spring.application.name:commission-service}")
    private String applicationName;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Commission Service API")
                        .description("API for managing commission rules, calculating commissions for partners, and handling payment distribution.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Social E-Commerce Ecosystem")
                                .url("https://example.com")
                                .email("support@example.com"))
                        .license(new License()
                                .name("Proprietary")
                                .url("https://example.com/terms")))
                .addServersItem(new Server()
                        .url("/")
                        .description("Default Server"))
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT token authentication")));
    }
}

