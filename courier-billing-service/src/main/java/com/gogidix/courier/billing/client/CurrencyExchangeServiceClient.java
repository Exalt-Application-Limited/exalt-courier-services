package com.gogidix.courier.billing.client;

import org.springframework.cloud.openfeign.FeignClient;

/**
 * Feign client for Currency Exchange Service integration.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@FeignClient(name = "currency-exchange-service", path = "/api/v1/currency")
public interface CurrencyExchangeServiceClient {
    // Placeholder implementation - methods to be added as needed
}