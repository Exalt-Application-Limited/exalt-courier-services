package com.gogidix.courier.onboarding.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class that enables Feign clients for integration with other services.
 */
@Configuration
@EnableFeignClients(basePackages = "com.gogidix.courier.onboarding.client")
public class FeignConfig {
    // Configuration is handled through application properties
}
