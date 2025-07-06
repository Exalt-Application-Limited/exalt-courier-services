package com.gogidix.courier.courier.drivermobileapp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Represents the courier's geo-location data.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationData {
    
    private Double latitude;
    private Double longitude;
    private Double accuracy;
    private Double heading;
    private Double speed;
    private String address;
    private LocalDateTime timestamp;
    private LocationSource source;
    
    /**
     * Source of the location data.
     */
    public enum LocationSource {
        GPS,
        NETWORK,
        MANUAL,
        LAST_KNOWN
    }
} 