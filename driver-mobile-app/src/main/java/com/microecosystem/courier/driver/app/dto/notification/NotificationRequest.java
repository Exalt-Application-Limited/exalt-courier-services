package com.microecosystem.courier.driver.app.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object for notification requests.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {

    /**
     * Target driver ID (single recipient)
     */
    private Long driverId;
    
    /**
     * List of driver IDs (multiple recipients)
     */
    private List<Long> driverIds;
    
    /**
     * Driver status for targeting drivers by status
     */
    private String driverStatus;
    
    /**
     * Notification title
     */
    @NotBlank(message = "Title is required")
    private String title;
    
    /**
     * Notification body
     */
    @NotBlank(message = "Body is required")
    private String body;
    
    /**
     * Additional data payload
     */
    private Map<String, String> data;
    
    /**
     * Topic for topic-based notifications
     */
    private String topic;
    
    /**
     * Whether to send as high priority
     */
    private boolean highPriority;
    
    /**
     * Time to live in seconds
     */
    private Long timeToLive;
} 