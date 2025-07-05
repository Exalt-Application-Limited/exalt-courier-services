package com.gogidix.integration.common.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Represents a standardized shipment response from any 3PL provider.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentResponse {
    
    private boolean success;
    
    private String shipmentId; // Carrier's tracking number or shipment ID
    
    private List<String> trackingNumbers; // List of tracking numbers (one per package)
    
    private String carrierId; // Identifier for the carrier (e.g., "FEDEX", "UPS", "DHL")
    
    private String referenceId; // Original reference ID from the request
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt; // When the shipment was created
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate scheduledDeliveryDate; // Estimated delivery date if available
    
    private ShipmentStatus status; // Current status of the shipment
    
    private String label; // Base64 encoded shipping label data
    
    private String labelFormat; // Format of the label (e.g., "PDF", "ZPL")
    
    private Map<String, Object> additionalDocuments; // Additional documents like customs forms
    
    private List<LabelInfo> labels; // For multi-package shipments
    
    private String serviceCode; // The carrier's actual service code used
    
    private ServiceLevel serviceLevel; // Standardized service level
    
    private Double totalCost; // Total cost of the shipment
    
    private String currency; // Currency of the cost (e.g., "USD", "EUR")
    
    private List<ShipmentError> errors; // Errors if any
    
    private Map<String, Object> providerSpecificData; // Additional provider-specific response data
    
    /**
     * Standardized service level across different carriers.
     */
    public enum ServiceLevel {
        GROUND,
        EXPRESS,
        ONE_DAY,
        TWO_DAY,
        THREE_DAY,
        INTERNATIONAL_ECONOMY,
        INTERNATIONAL_PRIORITY,
        FREIGHT
    }
    
    /**
     * Standardized shipment status across different carriers.
     */
    public enum ShipmentStatus {
        CREATED,
        LABEL_CREATED,
        PICKUP_READY,
        IN_TRANSIT,
        OUT_FOR_DELIVERY,
        DELIVERED,
        EXCEPTION,
        CANCELLED
    }
    
    /**
     * Information about a specific label in a multi-package shipment.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LabelInfo {
        private String trackingNumber;
        private String packageId;
        private String label; // Base64 encoded label data
        private String labelFormat;
    }
    
    /**
     * Error information when a shipment request fails.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ShipmentError {
        private String code;
        private String message;
        private String field; // Field that caused the error, if applicable
        private ErrorSeverity severity;
        
        public enum ErrorSeverity {
            INFO,
            WARNING,
            ERROR,
            FATAL
        }
    }
} 