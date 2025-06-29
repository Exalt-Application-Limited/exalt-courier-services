package com.exalt.courier.routing.service.geo;

import com.exalt.courier.routing.model.Location;

import java.util.List;

/**
 * Service interface for geospatial operations.
 */
public interface GeoService {
    
    /**
     * Find locations within a specified radius of a point.
     *
     * @param centerLocation The center location
     * @param radiusKm The radius in kilometers
     * @return List of locations within the specified radius
     */
    List<Location> findLocationsWithinRadius(Location centerLocation, double radiusKm);
    
    /**
     * Find the nearest locations to a point, ordered by proximity.
     *
     * @param referenceLocation The reference location
     * @param limit The maximum number of results to return
     * @return List of locations ordered by proximity
     */
    List<Location> findNearestLocations(Location referenceLocation, int limit);
    
    /**
     * Find locations within a geographical boundary (bounding box).
     *
     * @param southWest The southwest corner of the bounding box
     * @param northEast The northeast corner of the bounding box
     * @return List of locations within the bounding box
     */
    List<Location> findLocationsWithinBoundary(Location southWest, Location northEast);
    
    /**
     * Calculate the distance between two locations.
     *
     * @param location1 The first location
     * @param location2 The second location
     * @return The distance in meters
     */
    double calculateDistance(Location location1, Location location2);
    
    /**
     * Find all locations within a zone (polygon).
     * The polygon is represented as a WKT (Well-Known Text) string.
     *
     * @param zoneWkt The WKT representation of the zone polygon
     * @return List of locations within the zone
     */
    List<Location> findLocationsInZone(String zoneWkt);
    
    /**
     * Generate a WKT representation of a circle for geographical queries.
     *
     * @param centerLocation The center location of the circle
     * @param radiusKm The radius of the circle in kilometers
     * @return The WKT representation of the circle as a polygon
     */
    String generateCircleWkt(Location centerLocation, double radiusKm);
    
    /**
     * Find the nearest couriers to a location based on their last known positions.
     *
     * @param deliveryLocation The delivery location
     * @param radiusKm The search radius in kilometers
     * @param limit The maximum number of couriers to return
     * @return List of courier IDs ordered by proximity
     */
    List<String> findNearestCouriers(Location deliveryLocation, double radiusKm, int limit);
    
    /**
     * Create delivery zones based on geographical boundaries and courier coverage.
     *
     * @param centerLocation The center of the delivery zone network
     * @param maxRadiusKm The maximum radius in kilometers
     * @param numberOfZones The number of zones to create
     * @return List of WKT strings representing the zones
     */
    List<String> createDeliveryZones(Location centerLocation, double maxRadiusKm, int numberOfZones);
}
