import java.util.Optional;
package com.gogidix.integration.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Represents a standardized tracking request that can be sent to any 3PL provider.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackingRequest {
    
    @NotBlank(message = "Tracking number is required")
    private String trackingNumber;
    
    @NotNull(message = "Carrier ID is required")
    private CarrierId carrierId;
    
    private String referenceId; // Optional reference to tie the tracking to an internal order or shipment
    
    private String language; // Optional ISO language code for localized tracking information
    
    private boolean includeDetailedEvents; // Whether to include detailed tracking events
    
    /**
     * Carrier identifiers for supported 3PL providers.
     */
    public enum CarrierId {
        FEDEX("FedEx"),
        UPS("UPS"),
        DHL("DHL"),
        USPS("USPS"),
        CANADA_POST("Canada Post"),
        ROYAL_MAIL("Royal Mail"),
        AUSTRALIA_POST("Australia Post"),
        PUROLATOR("Purolator"),
        GLS("GLS"),
        TNT("TNT"),
        AMAZON_LOGISTICS("Amazon Logistics"),
        CUSTOM("Custom Carrier");
        
        private final String displayName;
        
        CarrierId(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
} 

