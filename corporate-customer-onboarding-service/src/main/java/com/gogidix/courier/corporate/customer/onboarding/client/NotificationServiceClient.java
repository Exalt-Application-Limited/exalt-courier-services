package com.gogidix.courier.corporate.customer.onboarding.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Feign client for Notification Service integration.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@FeignClient(name = "notification-service", path = "/api/v1/notifications")
public interface NotificationServiceClient {

    @PostMapping("/email")
    NotificationResponse sendEmail(@RequestBody EmailNotificationRequest request);

    @PostMapping("/sms")
    NotificationResponse sendSms(@RequestBody SmsNotificationRequest request);

    // Request DTOs
    record EmailNotificationRequest(
            String recipientEmail,
            String recipientName,
            String subject,
            String template,
            Map<String, Object> templateData,
            String priority
    ) {}

    record SmsNotificationRequest(
            String recipientPhone,
            String message,
            String template,
            Map<String, Object> templateData,
            String priority
    ) {}

    // Response DTOs
    record NotificationResponse(
            String notificationId,
            String status,
            String message,
            LocalDateTime sentAt
    ) {}
}