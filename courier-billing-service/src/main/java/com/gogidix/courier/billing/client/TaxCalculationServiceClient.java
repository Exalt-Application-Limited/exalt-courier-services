package com.gogidix.courier.billing.client;

import org.springframework.cloud.openfeign.FeignClient;

/**
 * Feign client for Tax Calculation Service integration.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@FeignClient(name = "tax-calculation-service", path = "/api/v1/tax")
public interface TaxCalculationServiceClient {
    // Placeholder implementation - methods to be added as needed
}