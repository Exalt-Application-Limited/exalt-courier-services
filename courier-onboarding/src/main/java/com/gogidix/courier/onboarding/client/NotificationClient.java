package com.gogidix.courier.onboarding.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * Client for integrating with the Notification Service.
 */
@FeignClient(name = "notification-service", url = "${app.services.notification.url}", 
        fallback = NotificationClientFallback.class)
public interface NotificationClient {
    
    /**
     * Sends an email notification.
     * 
     * @param emailRequest The email notification request
     * @return Response from notification service
     */
    @PostMapping("/api/v1/notifications/email")
    ResponseEntity<Map<String, Object>> sendEmailNotification(@RequestBody Map<String, Object> emailRequest);
    
    /**
     * Sends an SMS notification.
     * 
     * @param smsRequest The SMS notification request
     * @return Response from notification service
     */
    @PostMapping("/api/v1/notifications/sms")
    ResponseEntity<Map<String, Object>> sendSmsNotification(@RequestBody Map<String, Object> smsRequest);
    
    /**
     * Sends a push notification.
     * 
     * @param pushRequest The push notification request
     * @return Response from notification service
     */
    @PostMapping("/api/v1/notifications/push")
    ResponseEntity<Map<String, Object>> sendPushNotification(@RequestBody Map<String, Object> pushRequest);
}
