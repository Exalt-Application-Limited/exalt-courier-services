package com.exalt.integration.common.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Represents a standardized shipment request that can be converted to
 * provider-specific formats for various 3PL providers.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentRequest {
    
    @NotBlank(message = "Reference ID is required")
    private String referenceId;
    
    @NotNull(message = "Sender information is required")
    @Valid
    private Address sender;
    
    @NotNull(message = "Recipient information is required")
    @Valid
    private Address recipient;
    
    @NotEmpty(message = "At least one package is required")
    @Valid
    private List<PackageInfo> packages;
    
    @NotNull(message = "Service type is required")
    private ServiceType serviceType;
    
    @NotNull(message = "Shipment date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate shipmentDate;
    
    private boolean saturdayDelivery;
    
    private boolean residentialDelivery;
    
    private boolean signatureRequired;
    
    private boolean insurance;
    
    private Double declaredValue;
    
    private String specialInstructions;
    
    private String customerReference;
    
    private Map<String, String> customFields;
    
    /**
     * Service types that are common across different carriers.
     * Specific mappings to provider-specific service codes will be handled
     * within each provider's implementation.
     */
    public enum ServiceType {
        STANDARD,        // Standard ground service
        EXPRESS,         // Express/expedited delivery
        PRIORITY,        // Priority/next-day delivery
        ECONOMY,         // Economy/deferred delivery
        INTERNATIONAL,   // International standard
        INTERNATIONAL_EXPRESS, // International express/priority
        FREIGHT          // Freight service
    }
} 
