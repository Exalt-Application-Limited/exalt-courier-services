package com.exalt.courier.hqadmin.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration for Swagger/OpenAPI documentation.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Global HQ Admin API")
                .description("API for managing global configuration and policies for the courier services domain")
                .version("1.0.0")
                .contact(new Contact()
                    .name("Courier Services Team")
                    .email("courier-services@socialecommerceecosystem.com")
                    .url("https://socialecommerceecosystem.com"))
                .license(new License()
                    .name("Private")
                    .url("https://socialecommerceecosystem.com/licenses")))
            .servers(List.of(
                new Server()
                    .url("/global-hq-admin")
                    .description("Development Server"),
                new Server()
                    .url("https://api.socialecommerceecosystem.com/global-hq-admin")
                    .description("Production Server")))
            .components(new Components()
                .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("JWT Authorization header using the Bearer scheme")))
            .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
    }
}
