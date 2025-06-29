package com.exalt.courierservices.tracking.$1;

import com.exalt.courierservices.tracking.model.TrackingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object for Package entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackageDTO {
    
    private String id;
    private String trackingNumber;
    private TrackingStatus status;
    private String senderName;
    private String senderAddress;
    private String recipientName;
    private String recipientAddress;
    private String recipientPhone;
    private String recipientEmail;
    private LocalDateTime estimatedDeliveryDate;
    private LocalDateTime actualDeliveryDate;
    private Double weight;
    private String dimensions;
    private String orderId;
    private Long courierId;
    private Long routeId;
    private boolean signatureRequired;
    private String signatureImage;
    private String deliveryInstructions;
    private Integer deliveryAttempts;
    private List<TrackingEventDTO> events = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 