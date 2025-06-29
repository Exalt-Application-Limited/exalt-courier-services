package com.exalt.courier.onboarding.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Fallback implementation for NotificationClient.
 * Used when the Notification Service is unavailable.
 */
@Component
public class NotificationClientFallback implements NotificationClient {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationClientFallback.class);

    @Override
    public ResponseEntity<Map<String, Object>> sendEmailNotification(Map<String, Object> emailRequest) {
        logger.error("Fallback: Unable to send email notification to recipient: {}", 
                emailRequest.getOrDefault("recipient", "unknown"));
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "Notification Service is currently unavailable");
        response.put("notification_logged", true);
        
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response);
    }

    @Override
    public ResponseEntity<Map<String, Object>> sendSmsNotification(Map<String, Object> smsRequest) {
        logger.error("Fallback: Unable to send SMS notification to recipient: {}", 
                smsRequest.getOrDefault("phoneNumber", "unknown"));
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "Notification Service is currently unavailable");
        response.put("notification_logged", true);
        
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response);
    }

    @Override
    public ResponseEntity<Map<String, Object>> sendPushNotification(Map<String, Object> pushRequest) {
        logger.error("Fallback: Unable to send push notification to user: {}", 
                pushRequest.getOrDefault("userId", "unknown"));
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "Notification Service is currently unavailable");
        response.put("notification_logged", true);
        
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response);
    }
}
