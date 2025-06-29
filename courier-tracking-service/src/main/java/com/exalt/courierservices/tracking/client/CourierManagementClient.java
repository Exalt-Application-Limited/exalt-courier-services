package com.exalt.courierservices.tracking.$1;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * Feign client for interacting with the Courier Management Service.
 */
@FeignClient(name = "courier-management", fallback = CourierManagementClientFallback.class)
public interface CourierManagementClient {

    /**
     * Get courier information.
     */
    @GetMapping("/api/v1/couriers/{courierId}")
    Map<String, Object> getCourierInfo(@PathVariable("courierId") Long courierId);
    
    /**
     * Notify courier about package status change.
     */
    @PostMapping("/api/v1/couriers/{courierId}/notifications/package-status")
    void notifyPackageStatusChange(
            @PathVariable("courierId") Long courierId,
            @RequestBody Map<String, Object> statusChangeInfo);
    
    /**
     * Get courier's current location.
     */
    @GetMapping("/api/v1/couriers/{courierId}/location")
    Map<String, Object> getCourierLocation(@PathVariable("courierId") Long courierId);
} 