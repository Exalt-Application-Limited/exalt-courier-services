package com.gogidix.courier.routing.service.algorithm.util;

import com.gogidix.courier.routing.model.Location;

/**
 * Utility class for geospatial calculations.
 */
public class GeoUtils {

    private static final double EARTH_RADIUS_KM = 6371.0;
    
    private GeoUtils() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Calculate the distance between two locations using the Haversine formula.
     * 
     * @param loc1 The first location
     * @param loc2 The second location
     * @return The distance in kilometers
     */
    public static double calculateHaversineDistance(Location loc1, Location loc2) {
        if (loc1 == null || loc2 == null) {
            throw new IllegalArgumentException("Locations cannot be null");
        }
        
        double lat1Rad = Math.toRadians(loc1.getLatitude());
        double lon1Rad = Math.toRadians(loc1.getLongitude());
        double lat2Rad = Math.toRadians(loc2.getLatitude());
        double lon2Rad = Math.toRadians(loc2.getLongitude());
        
        double dLat = lat2Rad - lat1Rad;
        double dLon = lon2Rad - lon1Rad;
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return EARTH_RADIUS_KM * c;
    }
    
    /**
     * Calculate the estimated travel time between two locations.
     * 
     * @param loc1 The first location
     * @param loc2 The second location
     * @param averageSpeedKmh The average travel speed in km/h
     * @return The estimated travel time in minutes
     */
    public static double calculateTravelTime(Location loc1, Location loc2, double averageSpeedKmh) {
        if (averageSpeedKmh <= 0) {
            throw new IllegalArgumentException("Average speed must be positive");
        }
        
        double distanceKm = calculateHaversineDistance(loc1, loc2);
        
        // Convert from hours to minutes: (distance / speed) * 60
        return (distanceKm / averageSpeedKmh) * 60.0;
    }
    
    /**
     * Check if two locations are approximately equal within a small epsilon.
     * 
     * @param loc1 The first location
     * @param loc2 The second location
     * @param epsilonMeters Maximum distance in meters to consider equal
     * @return true if the locations are within the specified epsilon distance
     */
    public static boolean locationsAreEqual(Location loc1, Location loc2, double epsilonMeters) {
        if (loc1 == null || loc2 == null) {
            return loc1 == loc2; // Both null or one is null
        }
        
        double distanceKm = calculateHaversineDistance(loc1, loc2);
        return distanceKm * 1000 <= epsilonMeters; // Convert km to meters
    }
} 