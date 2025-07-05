package com.gogidix.courier.onboarding.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Client for integrating with the external Background Check API.
 */
@FeignClient(name = "background-check-api", url = "${app.services.background-check.url}", 
        fallback = BackgroundCheckClientFallback.class)
public interface BackgroundCheckClient {
    
    /**
     * Initiates a background check for an applicant.
     * 
     * @param checkData The data required for the background check
     * @return Response from background check service
     */
    @PostMapping("/api/v1/checks")
    ResponseEntity<Map<String, Object>> initiateBackgroundCheck(@RequestBody Map<String, Object> checkData);
    
    /**
     * Gets the status of an ongoing background check.
     * 
     * @param checkId The ID of the background check
     * @return Response with check status
     */
    @GetMapping("/api/v1/checks/{checkId}")
    ResponseEntity<Map<String, Object>> getBackgroundCheckStatus(@PathVariable("checkId") String checkId);
    
    /**
     * Manually approves a background check when automated checks fail.
     * Used by administrators in special cases.
     * 
     * @param checkId The ID of the background check
     * @param approvalData The approval data including reason and approver
     * @return Response with updated check status
     */
    @PostMapping("/api/v1/checks/{checkId}/approve")
    ResponseEntity<Map<String, Object>> manuallyApproveBackgroundCheck(
            @PathVariable("checkId") String checkId, 
            @RequestBody Map<String, Object> approvalData);
    
    /**
     * Cancels an ongoing background check.
     * 
     * @param checkId The ID of the background check
     * @return Response confirming cancellation
     */
    @DeleteMapping("/api/v1/checks/{checkId}")
    ResponseEntity<Map<String, Object>> cancelBackgroundCheck(@PathVariable("checkId") String checkId);
}
