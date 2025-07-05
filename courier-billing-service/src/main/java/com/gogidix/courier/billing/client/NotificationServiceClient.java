package com.gogidix.courier.billing.client;

import org.springframework.cloud.openfeign.FeignClient;

/**
 * Feign client for Notification Service integration.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@FeignClient(name = "notification-service", path = "/api/v1/notifications")
public interface NotificationServiceClient {
    // Placeholder implementation - methods to be added as needed
}