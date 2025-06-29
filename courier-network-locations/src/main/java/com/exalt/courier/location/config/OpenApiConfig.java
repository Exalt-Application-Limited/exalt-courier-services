package com.exalt.courier.location.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

import java.util.Arrays;

/**
 * Configuration for OpenAPI documentation.
 * Provides detailed API documentation for the Courier Network Locations Service.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Courier Network Locations Service API")
                        .description("API for managing physical courier network locations, staff, customers, shipments, and payments")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Micro-Social-Ecommerce-Ecosystems Support")
                                .email("support@microsocial-ecommerce.com")
                                .url("https://microsocial-ecommerce.com"))
                        .license(new License()
                                .name("proprietary")
                                .url("https://microsocial-ecommerce.com/licenses")))
                .servers(Arrays.asList(
                        new Server()
                                .url("https://api.microsocial-ecommerce.com/courier-network-locations")
                                .description("Production Server"),
                        new Server()
                                .url("https://api-staging.microsocial-ecommerce.com/courier-network-locations")
                                .description("Staging Server"),
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local Development Server")))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", 
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT Authorization header using the Bearer scheme. Example: 'Authorization: Bearer {token}'")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
