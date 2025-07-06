package com.gogidix.courier.courier.drivermobileapp.controller;

import com.socialecommerceecosystem.drivermobileapp.client.CourierManagementClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * REST controller for performance-related operations in the driver mobile app.
 */
@RestController
@RequestMapping("/api/v1/performance")
@RequiredArgsConstructor
@Slf4j
public class PerformanceController {
    
    private final CourierManagementClient courierManagementClient;
    
    /**
     * Get performance metrics for a courier for a specific date range.
     *
     * @param courierId the courier ID
     * @param startDate the start date in ISO format (YYYY-MM-DD)
     * @param endDate the end date in ISO format (YYYY-MM-DD)
     * @return the performance metrics
     */
    @GetMapping("/courier/{courierId}/metrics/{startDate}/{endDate}")
    public ResponseEntity<List<Map<String, Object>>> getPerformanceMetrics(
            @PathVariable String courierId,
            @PathVariable String startDate,
            @PathVariable String endDate) {
        
        log.info("REST request to get performance metrics for courier: {} from: {} to: {}", 
                courierId, startDate, endDate);
        
        return courierManagementClient.getPerformanceMetrics(courierId, startDate, endDate);
    }
    
    /**
     * Get a performance report for a courier.
     *
     * @param courierId the courier ID
     * @param startDate the start date in ISO format (YYYY-MM-DD)
     * @param endDate the end date in ISO format (YYYY-MM-DD)
     * @return the performance report
     */
    @GetMapping("/courier/{courierId}/report/{startDate}/{endDate}")
    public ResponseEntity<Map<String, Object>> getPerformanceReport(
            @PathVariable String courierId,
            @PathVariable String startDate,
            @PathVariable String endDate) {
        
        log.info("REST request to get performance report for courier: {} from: {} to: {}", 
                courierId, startDate, endDate);
        
        return courierManagementClient.getPerformanceReport(courierId, startDate, endDate);
    }
} 