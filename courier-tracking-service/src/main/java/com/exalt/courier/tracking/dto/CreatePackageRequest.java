package com.exalt.courierservices.tracking.$1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * DTO for creating a new package in the tracking system.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePackageRequest {
    
    @NotBlank(message = "Sender name is required")
    @Size(max = 100, message = "Sender name must be less than 100 characters")
    private String senderName;
    
    @NotBlank(message = "Sender address is required")
    @Size(max = 255, message = "Sender address must be less than 255 characters")
    private String senderAddress;
    
    @NotBlank(message = "Recipient name is required")
    @Size(max = 100, message = "Recipient name must be less than 100 characters")
    private String recipientName;
    
    @NotBlank(message = "Recipient address is required")
    @Size(max = 255, message = "Recipient address must be less than 255 characters")
    private String recipientAddress;
    
    @Size(max = 20, message = "Recipient phone must be less than 20 characters")
    private String recipientPhone;
    
    @Size(max = 100, message = "Recipient email must be less than 100 characters")
    private String recipientEmail;
    
    @NotNull(message = "Estimated delivery date is required")
    private LocalDateTime estimatedDeliveryDate;
    
    private Double weight;
    
    private String dimensions;
    
    private String orderId;
    
    private Long courierId;
    
    private Long routeId;
    
    private boolean signatureRequired;
    
    @Size(max = 500, message = "Delivery instructions must be less than 500 characters")
    private String deliveryInstructions;
} 