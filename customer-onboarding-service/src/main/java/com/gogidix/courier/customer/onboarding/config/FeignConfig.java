package com.gogidix.courier.customer.onboarding.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class that enables Feign clients for integration with shared infrastructure services.
 * 
 * This configuration enables communication with:
 * - auth-service (JWT authentication)
 * - kyc-service (identity verification)
 * - document-verification (document processing)
 * - notification-service (customer communications)
 * - payment-processing-service (billing integration)
 */
@Configuration
@EnableFeignClients(basePackages = "com.gogidix.courier.customer.onboarding.client")
public class FeignConfig {
    // Configuration is handled through application properties
}