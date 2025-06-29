package com.exalt.courier.routing.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Request model for creating delivery zones.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDeliveryZonesRequest {
    
    @NotNull(message = "Latitude is required")
    private Double latitude;
    
    @NotNull(message = "Longitude is required")
    private Double longitude;
    
    @NotNull(message = "Maximum radius is required")
    @Min(value = 1, message = "Maximum radius must be at least 1 kilometer")
    private Double maxRadiusKm;
    
    @NotNull(message = "Number of zones is required")
    @Min(value = 1, message = "Number of zones must be at least 1")
    private Integer numberOfZones;
}
