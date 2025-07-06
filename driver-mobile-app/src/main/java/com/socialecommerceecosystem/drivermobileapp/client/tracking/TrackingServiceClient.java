package com.gogidix.courier.drivermobileapp.client.tracking;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Feign client for interacting with the Tracking service.
 */
@FeignClient(name = "tracking-service", path = "/tracking-service")
public interface TrackingServiceClient {
    
    /**
     * Get tracking information for a tracking number.
     *
     * @param trackingNumber the tracking number
     * @return the tracking information
     */
    @GetMapping("/api/tracking/{trackingNumber}")
    ResponseEntity<Map<String, Object>> getTrackingInfo(@PathVariable("trackingNumber") String trackingNumber);
    
    /**
     * Update the status of a package.
     *
     * @param packageId the package ID
     * @param statusUpdate the status update request
     * @return the updated tracking information
     */
    @PutMapping("/api/tracking/packages/{packageId}/status")
    ResponseEntity<Map<String, Object>> updatePackageStatus(
            @PathVariable("packageId") String packageId,
            @RequestBody Map<String, Object> statusUpdate);
    
    /**
     * Confirm delivery of a package.
     *
     * @param packageId the package ID
     * @param confirmationData the delivery confirmation data
     * @return the updated tracking information
     */
    @PostMapping("/api/tracking/packages/{packageId}/confirm-delivery")
    ResponseEntity<Map<String, Object>> confirmDelivery(
            @PathVariable("packageId") String packageId,
            @RequestBody Map<String, Object> confirmationData);
    
    /**
     * Add a tracking event.
     *
     * @param packageId the package ID
     * @param eventData the tracking event data
     * @return the updated tracking information
     */
    @PostMapping("/api/tracking/packages/{packageId}/events")
    ResponseEntity<Map<String, Object>> addTrackingEvent(
            @PathVariable("packageId") String packageId,
            @RequestBody Map<String, Object> eventData);
}
