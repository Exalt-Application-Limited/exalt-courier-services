package com.exalt.courier.onboarding.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * Client for integrating with the Courier Management Service.
 */
@FeignClient(name = "courier-management", url = "${app.services.courier-management.url}", 
        fallback = CourierManagementClientFallback.class)
public interface CourierManagementClient {
    
    /**
     * Creates a courier profile in the Courier Management Service after successful
     * onboarding application.
     * 
     * @param courierData The courier data to create
     * @return Response from Courier Management Service
     */
    @PostMapping("/api/v1/couriers")
    ResponseEntity<Map<String, Object>> createCourierProfile(@RequestBody Map<String, Object> courierData);
    
    /**
     * Updates the status of a courier profile in the Courier Management Service.
     * 
     * @param courierId The ID of the courier profile
     * @param statusData The status data to update
     * @return Response from Courier Management Service
     */
    @PostMapping("/api/v1/couriers/{courierId}/status")
    ResponseEntity<Map<String, Object>> updateCourierStatus(
            @PathVariable("courierId") String courierId, 
            @RequestBody Map<String, Object> statusData);
    
    /**
     * Assigns a courier to a zone/region in the Courier Management Service.
     * 
     * @param courierId The ID of the courier profile
     * @param assignmentData The assignment data
     * @return Response from Courier Management Service
     */
    @PostMapping("/api/v1/couriers/{courierId}/assignments")
    ResponseEntity<Map<String, Object>> assignCourierToZone(
            @PathVariable("courierId") String courierId, 
            @RequestBody Map<String, Object> assignmentData);
}
