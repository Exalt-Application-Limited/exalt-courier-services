package com.gogidix.courier.routing.service;

import com.gogidix.courier.routing.model.Location;
import com.gogidix.courier.routing.model.Route;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for caching frequently accessed routes and route calculations.
 * This service improves performance by reducing database and external API calls.
 */
public interface CacheService {
    
    /**
     * Cache a route
     *
     * @param route the route to cache
     */
    void cacheRoute(Route route);
    
    /**
     * Get a cached route by ID
     *
     * @param routeId the route ID
     * @return an Optional containing the cached route, if present
     */
    Optional<Route> getCachedRoute(Long routeId);
    
    /**
     * Cache a calculated travel time between two locations
     *
     * @param originLat the origin latitude
     * @param originLng the origin longitude
     * @param destLat the destination latitude
     * @param destLng the destination longitude
     * @param travelTimeSeconds the calculated travel time in seconds
     * @param expirationMinutes how long to cache the result (in minutes)
     */
    void cacheTravelTime(double originLat, double originLng, 
                        double destLat, double destLng, 
                        int travelTimeSeconds, int expirationMinutes);
    
    /**
     * Get a cached travel time
     *
     * @param originLat the origin latitude
     * @param originLng the origin longitude
     * @param destLat the destination latitude
     * @param destLng the destination longitude
     * @return the cached travel time in seconds, or -1 if not found or expired
     */
    int getCachedTravelTime(double originLat, double originLng, 
                          double destLat, double destLng);
    
    /**
     * Cache courier IDs near a location
     *
     * @param location the location
     * @param radiusKm the radius in kilometers
     * @param courierIds the list of courier IDs
     * @param expirationSeconds how long to cache the result (in seconds)
     */
    void cacheNearbyCouriers(Location location, double radiusKm, 
                           List<String> courierIds, int expirationSeconds);
    
    /**
     * Get cached courier IDs near a location
     *
     * @param location the location
     * @param radiusKm the radius in kilometers
     * @return list of courier IDs, or empty list if not found or expired
     */
    List<String> getCachedNearbyCouriers(Location location, double radiusKm);
    
    /**
     * Cache an estimated time of arrival
     *
     * @param shipmentId the shipment ID
     * @param eta the estimated time of arrival
     * @param expirationMinutes how long to cache the result (in minutes)
     */
    void cacheEta(String shipmentId, LocalDateTime eta, int expirationMinutes);
    
    /**
     * Get a cached estimated time of arrival
     *
     * @param shipmentId the shipment ID
     * @return the cached ETA, or null if not found or expired
     */
    LocalDateTime getCachedEta(String shipmentId);
    
    /**
     * Remove a route from the cache
     *
     * @param routeId the route ID
     */
    void evictRoute(Long routeId);
    
    /**
     * Clear all caches
     */
    void clearAllCaches();
}
