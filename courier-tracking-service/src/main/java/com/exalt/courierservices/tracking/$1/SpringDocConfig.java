package com.exalt.courierservices.tracking.$1;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration for SpringDoc OpenAPI documentation.
 */
@Configuration
public class SpringDocConfig {

    /**
     * Configure the OpenAPI documentation.
     *
     * @return the OpenAPI configuration
     */
    @Bean
    public OpenAPI trackingServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Tracking Service API")
                        .description("API for package tracking operations in the courier services domain")
                        .version("0.1.0")
                        .contact(new Contact()
                                .name("Micro-Social-Ecommerce-Ecosystems Team")
                                .email("support@microecosystem.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0")))
                .externalDocs(new ExternalDocumentation()
                        .description("Tracking Service Documentation")
                        .url("https://microecosystem.com/docs/tracking-service"))
                .servers(List.of(
                        new Server().url("/api/v1").description("Base API path")
                ));
    }
} 