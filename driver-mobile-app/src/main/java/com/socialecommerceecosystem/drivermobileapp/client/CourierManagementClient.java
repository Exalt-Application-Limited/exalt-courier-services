package com.exalt.courier.drivermobileapp.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * Feign client for interacting with the Courier Management service.
 */
@FeignClient(name = "courier-management", path = "/courier-management")
public interface CourierManagementClient {
    
    /**
     * Get an assignment by courier ID.
     *
     * @param courierId the courier ID
     * @return the assignments for the courier
     */
    @GetMapping("/api/v1/assignments/by-courier/{courierId}")
    ResponseEntity<List<Map<String, Object>>> getAssignmentsByCourier(@PathVariable("courierId") String courierId);
    
    /**
     * Get active assignments for a courier.
     *
     * @param courierId the courier ID
     * @return the active assignments for the courier
     */
    @GetMapping("/api/v1/assignments/active/by-courier/{courierId}")
    ResponseEntity<List<Map<String, Object>>> getActiveAssignmentsByCourier(@PathVariable("courierId") String courierId);
    
    /**
     * Accept an assignment.
     *
     * @param assignmentId the assignment ID
     * @return the updated assignment
     */
    @PatchMapping("/api/v1/assignments/{assignmentId}/accept")
    ResponseEntity<Map<String, Object>> acceptAssignment(@PathVariable("assignmentId") String assignmentId);
    
    /**
     * Start an assignment.
     *
     * @param assignmentId the assignment ID
     * @return the updated assignment
     */
    @PatchMapping("/api/v1/assignments/{assignmentId}/start")
    ResponseEntity<Map<String, Object>> startAssignment(@PathVariable("assignmentId") String assignmentId);
    
    /**
     * Complete an assignment.
     *
     * @param assignmentId the assignment ID
     * @return the updated assignment
     */
    @PatchMapping("/api/v1/assignments/{assignmentId}/complete")
    ResponseEntity<Map<String, Object>> completeAssignment(@PathVariable("assignmentId") String assignmentId);
    
    /**
     * Cancel an assignment.
     *
     * @param assignmentId the assignment ID
     * @param cancellationRequest the cancellation request containing the reason
     * @return the updated assignment
     */
    @PatchMapping("/api/v1/assignments/{assignmentId}/cancel")
    ResponseEntity<Map<String, Object>> cancelAssignment(
            @PathVariable("assignmentId") String assignmentId,
            @RequestBody Map<String, String> cancellationRequest);
    
    /**
     * Get performance metrics for a courier for a specific date range.
     *
     * @param courierId the courier ID
     * @param startDate the start date in ISO format (YYYY-MM-DD)
     * @param endDate the end date in ISO format (YYYY-MM-DD)
     * @return the performance metrics
     */
    @GetMapping("/api/v1/performance-metrics/courier/{courierId}/date-range/{startDate}/{endDate}")
    ResponseEntity<List<Map<String, Object>>> getPerformanceMetrics(
            @PathVariable("courierId") String courierId,
            @PathVariable("startDate") String startDate,
            @PathVariable("endDate") String endDate);
    
    /**
     * Get a performance report for a courier.
     *
     * @param courierId the courier ID
     * @param startDate the start date in ISO format (YYYY-MM-DD)
     * @param endDate the end date in ISO format (YYYY-MM-DD)
     * @return the performance report
     */
    @GetMapping("/api/v1/performance-metrics/courier/{courierId}/report/{startDate}/{endDate}")
    ResponseEntity<Map<String, Object>> getPerformanceReport(
            @PathVariable("courierId") String courierId,
            @PathVariable("startDate") String startDate,
            @PathVariable("endDate") String endDate);
    
    /**
     * Optimize routes for a courier's assignments.
     *
     * @param courierId the courier ID
     * @return list of assignments with optimized route
     */
    @GetMapping("/api/v1/assignments/optimize-routes/by-courier/{courierId}")
    ResponseEntity<List<Map<String, Object>>> optimizeRoutes(@PathVariable("courierId") String courierId);
} 