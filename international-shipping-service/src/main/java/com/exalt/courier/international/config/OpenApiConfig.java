package com.exalt.courierservices.international-shipping.$1;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for OpenAPI documentation.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearer-key", 
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                .info(new Info()
                        .title("International Shipping API")
                        .description("API for managing international shipments, customs declarations, and tariff calculations")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Social E-commerce Ecosystem")
                                .url("https://example.com")
                                .email("support@example.com"))
                        .license(new License()
                                .name("Proprietary")
                                .url("https://example.com/terms")));
    }
}
