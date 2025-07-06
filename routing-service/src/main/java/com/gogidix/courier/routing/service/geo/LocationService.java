package com.gogidix.courier.routing.service.geo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Location service for routing operations.
 * Provides geolocation and distance calculation services.
 */
@Slf4j
@Service
public class LocationService {
    
    /**
     * Calculate distance between two points.
     *
     * @param lat1 Latitude of first point
     * @param lon1 Longitude of first point  
     * @param lat2 Latitude of second point
     * @param lon2 Longitude of second point
     * @return Distance in kilometers
     */
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Haversine formula implementation
        final int R = 6371; // Radius of the earth in km
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }
    
    /**
     * Get location name from coordinates.
     *
     * @param latitude Latitude
     * @param longitude Longitude
     * @return Location name
     */
    public String getLocationName(double latitude, double longitude) {
        // Stub implementation - would integrate with geocoding service
        log.debug("Getting location name for coordinates: {}, {}", latitude, longitude);
        return String.format("Location(%.4f, %.4f)", latitude, longitude);
    }
    
    /**
     * Validate coordinates.
     *
     * @param latitude Latitude
     * @param longitude Longitude
     * @return true if valid
     */
    public boolean isValidCoordinate(double latitude, double longitude) {
        return latitude >= -90 && latitude <= 90 && longitude >= -180 && longitude <= 180;
    }
}