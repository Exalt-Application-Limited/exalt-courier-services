package com.exalt.courier.international.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Feign clients.
 * Enables Feign clients for the application.
 */
@Configuration
@EnableFeignClients(basePackages = "com.socialecommerceecosystem.international.client")
public class FeignConfig {
    // Configuration is handled through application.properties
}
