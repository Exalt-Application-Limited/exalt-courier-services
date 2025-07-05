package com.gogidix.courier.management.assignment.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Embeddable location entity representing geographic coordinates.
 */
@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Location {

    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be at least -90.0")
    @DecimalMax(value = "90.0", message = "Latitude must be at most 90.0")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be at least -180.0")
    @DecimalMax(value = "180.0", message = "Longitude must be at most 180.0")
    private Double longitude;

    /**
     * Calculates the distance between this location and another location using Haversine formula.
     * 
     * @param other the other location
     * @return distance in kilometers
     */
    public double distanceTo(Location other) {
        if (other == null || other.latitude == null || other.longitude == null || 
            this.latitude == null || this.longitude == null) {
            return Double.MAX_VALUE;
        }

        final double R = 6371; // Earth's radius in kilometers

        double latDistance = Math.toRadians(other.latitude - this.latitude);
        double lonDistance = Math.toRadians(other.longitude - this.longitude);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(this.latitude)) * Math.cos(Math.toRadians(other.latitude))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }

    /**
     * Checks if this location is valid (has both latitude and longitude).
     * 
     * @return true if location is valid, false otherwise
     */
    public boolean isValid() {
        return latitude != null && longitude != null &&
               latitude >= -90.0 && latitude <= 90.0 &&
               longitude >= -180.0 && longitude <= 180.0;
    }

    @Override
    public String toString() {
        return String.format("Location(lat=%.6f, lon=%.6f)", latitude, longitude);
    }
}