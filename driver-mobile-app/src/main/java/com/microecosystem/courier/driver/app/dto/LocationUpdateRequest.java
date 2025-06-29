package com.microecosystem.courier.driver.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Data Transfer Object for location update requests.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationUpdateRequest {

    /**
     * Current latitude
     */
    @NotNull(message = "Latitude is required")
    private BigDecimal latitude;
    
    /**
     * Current longitude
     */
    @NotNull(message = "Longitude is required")
    private BigDecimal longitude;
    
    /**
     * Accuracy of the location in meters (optional)
     */
    private Integer accuracyInMeters;
    
    /**
     * Current speed in km/h (optional)
     */
    private Double speed;
    
    /**
     * Heading in degrees (optional)
     */
    private Double heading;
} 