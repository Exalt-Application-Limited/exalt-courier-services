package com.exalt.courier.routing.integration;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * Feign client for interacting with the Tracking Service.
 * This client allows the Routing Service to update package statuses
 * and retrieve tracking information.
 */
@FeignClient(name = "tracking-service")
public interface TrackingServiceClient {
    
    /**
     * Get the current status of a package
     *
     * @param packageId the package ID
     * @return the current status of the package
     */
    @GetMapping("/api/tracking/packages/{packageId}/status")
    String getPackageStatus(@PathVariable("packageId") String packageId);
    
    /**
     * Update the status of a package
     *
     * @param packageId the package ID
     * @param status the new status
     * @param metadata additional metadata about the status change
     * @return true if the update was successful
     */
    @PostMapping("/api/tracking/packages/{packageId}/status")
    boolean updatePackageStatus(
            @PathVariable("packageId") String packageId,
            @RequestBody Map<String, Object> statusUpdate);
    
    /**
     * Get all packages assigned to a route
     *
     * @param routeId the route ID
     * @return list of package IDs
     */
    @GetMapping("/api/tracking/routes/{routeId}/packages")
    String[] getPackagesByRoute(@PathVariable("routeId") Long routeId);
    
    /**
     * Update the location of a package
     *
     * @param packageId the package ID
     * @param latitude the current latitude
     * @param longitude the current longitude
     * @return true if the update was successful
     */
    @PostMapping("/api/tracking/packages/{packageId}/location")
    boolean updatePackageLocation(
            @PathVariable("packageId") String packageId,
            @RequestBody Map<String, Double> location);
}
