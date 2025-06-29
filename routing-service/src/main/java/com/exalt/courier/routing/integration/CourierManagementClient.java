package com.exalt.courier.routing.integration;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * Feign client for interacting with the Courier Management Service.
 * This client allows the Routing Service to fetch courier information
 * and update courier states without direct database access.
 */
@FeignClient(name = "courier-management-service")
public interface CourierManagementClient {
    
    /**
     * Check if a courier exists and is active
     *
     * @param courierId the courier ID
     * @return true if the courier exists and is active
     */
    @GetMapping("/api/couriers/{courierId}/active")
    boolean isCourierActive(@PathVariable("courierId") String courierId);
    
    /**
     * Get couriers by their current location within a radius
     *
     * @param latitude the latitude of the center point
     * @param longitude the longitude of the center point
     * @param radiusKm the radius in kilometers
     * @return list of courier IDs ordered by proximity
     */
    @GetMapping("/api/couriers/nearby")
    List<String> findNearestCouriers(
            @RequestParam("latitude") double latitude,
            @RequestParam("longitude") double longitude,
            @RequestParam("radiusKm") double radiusKm);
    
    /**
     * Update the location of a courier
     *
     * @param courierId the courier ID
     * @param latitude the current latitude
     * @param longitude the current longitude
     * @return true if the update was successful
     */
    @PutMapping("/api/couriers/{courierId}/location")
    boolean updateCourierLocation(
            @PathVariable("courierId") String courierId,
            @RequestParam("latitude") double latitude,
            @RequestParam("longitude") double longitude);
    
    /**
     * Get courier skills and capabilities
     *
     * @param courierId the courier ID
     * @return map of skill names to skill levels
     */
    @GetMapping("/api/couriers/{courierId}/skills")
    Map<String, Integer> getCourierSkills(@PathVariable("courierId") String courierId);
    
    /**
     * Get information about the courier's vehicle
     *
     * @param courierId the courier ID
     * @return map of vehicle attributes
     */
    @GetMapping("/api/couriers/{courierId}/vehicle")
    Map<String, Object> getCourierVehicle(@PathVariable("courierId") String courierId);
}
