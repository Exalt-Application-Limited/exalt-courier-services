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
 * Represents a standardized tracking response from any 3PL provider.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackingResponse {
    
    private boolean success;
    
    private String trackingNumber;
    
    private String carrierId; // ID of the carrier (e.g., "FEDEX", "UPS", "DHL")
    
    private String carrierName; // Name of the carrier
    
    private String referenceId; // Original reference ID from the request
    
    private TrackingStatus status; // Current tracking status
    
    private String statusDescription; // Human-readable status description
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime estimatedDeliveryDateTime; // Estimated delivery date/time if available
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate estimatedDeliveryDate; // Estimated delivery date
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime actualDeliveryDateTime; // Actual delivery date/time if delivered
    
    private String signedBy; // Name of the person who signed for the package
    
    private Address origin; // Shipment origin address
    
    private Address destination; // Shipment destination address
    
    private String service; // Service type used for the shipment
    
    private Integer packageCount; // Number of packages in the shipment
    
    private String shipmentId; // Additional identifier if available
    
    private List<TrackingEvent> events; // Tracking events in chronological order
    
    private List<ShipmentResponse.ShipmentError> errors; // Errors if any
    
    private Map<String, Object> providerSpecificData; // Additional provider-specific response data
    
    /**
     * Standardized tracking status across different carriers.
     */
    public enum TrackingStatus {
        UNKNOWN("Unknown"),
        INITIATED("Initiated"),
        PRE_TRANSIT("Pre-Transit"),
        IN_TRANSIT("In Transit"),
        OUT_FOR_DELIVERY("Out for Delivery"),
        DELIVERED("Delivered"),
        AVAILABLE_FOR_PICKUP("Available for Pickup"),
        DELAYED("Delayed"),
        EXCEPTION("Exception"),
        RETURNED("Returned to Sender"),
        EXPIRED("Tracking Expired");
        
        private final String displayName;
        
        TrackingStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    /**
     * Represents a specific tracking event in the shipment's journey.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TrackingEvent {
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime timestamp;
        
        private String status;
        
        private String description;
        
        private String locationCity;
        
        private String locationStateProvince;
        
        private String locationPostalCode;
        
        private String locationCountryCode;
        
        private String locationDescription;
        
        private Map<String, Object> additionalDetails;
    }
} 