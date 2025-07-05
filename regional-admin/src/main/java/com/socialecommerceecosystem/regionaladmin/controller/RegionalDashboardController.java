package com.gogidix.courier.regionaladmin.controller;

import com.socialecommerceecosystem.regionaladmin.dto.dashboard.PerformanceDashboardDTO;
import com.socialecommerceecosystem.regionaladmin.dto.dashboard.RegionalMetricsDTO;
import com.socialecommerceecosystem.regionaladmin.service.dashboard.RegionalPerformanceDashboardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * REST API controller for the Regional Performance Dashboard.
 * Provides endpoints for retrieving various dashboard metrics and views.
 */
@RestController
@RequestMapping("/api/v1/dashboard")
public class RegionalDashboardController {

    private static final Logger logger = LoggerFactory.getLogger(RegionalDashboardController.class);
    
    @Autowired
    private RegionalPerformanceDashboardService dashboardService;

    /**
     * Get the complete performance dashboard data.
     * 
     * @return A comprehensive dashboard DTO with all performance metrics
     */
    @GetMapping
    public ResponseEntity<PerformanceDashboardDTO> getDashboard() {
        logger.info("Request received for complete dashboard data");
        return ResponseEntity.ok(dashboardService.getDashboardData());
    }
    
    /**
     * Get delivery metrics data only.
     * 
     * @return Delivery metrics data
     */
    @GetMapping("/delivery")
    public ResponseEntity<Map<String, Object>> getDeliveryMetrics() {
        logger.info("Request received for delivery metrics data");
        return ResponseEntity.ok(dashboardService.getDashboardData().getDeliveryMetrics());
    }
    
    /**
     * Get driver performance metrics data only.
     * 
     * @return Driver performance metrics data
     */
    @GetMapping("/driver-performance")
    public ResponseEntity<Map<String, Object>> getDriverPerformanceMetrics() {
        logger.info("Request received for driver performance metrics data");
        return ResponseEntity.ok(dashboardService.getDashboardData().getDriverPerformance());
    }
    
    /**
     * Get financial metrics data only.
     * 
     * @return Financial metrics data
     */
    @GetMapping("/financial")
    public ResponseEntity<Map<String, Object>> getFinancialMetrics() {
        logger.info("Request received for financial metrics data");
        return ResponseEntity.ok(dashboardService.getDashboardData().getFinancialMetrics());
    }
    
    /**
     * Get operational metrics data only.
     * 
     * @return Operational metrics data
     */
    @GetMapping("/operational")
    public ResponseEntity<Map<String, Object>> getOperationalMetrics() {
        logger.info("Request received for operational metrics data");
        return ResponseEntity.ok(dashboardService.getDashboardData().getOperationalMetrics());
    }
    
    /**
     * Get customer satisfaction metrics data only.
     * 
     * @return Customer satisfaction metrics data
     */
    @GetMapping("/customer-satisfaction")
    public ResponseEntity<Map<String, Object>> getCustomerSatisfactionMetrics() {
        logger.info("Request received for customer satisfaction metrics data");
        return ResponseEntity.ok(dashboardService.getDashboardData().getCustomerSatisfaction());
    }
    
    /**
     * Get system health metrics data only.
     * 
     * @return System health metrics data
     */
    @GetMapping("/system-health")
    public ResponseEntity<Map<String, Object>> getSystemHealthMetrics() {
        logger.info("Request received for system health metrics data");
        return ResponseEntity.ok(dashboardService.getDashboardData().getSystemHealth());
    }
    
    /**
     * Get cross-service integrated metrics data only.
     * 
     * @return Cross-service metrics data
     */
    @GetMapping("/cross-service")
    public ResponseEntity<Map<String, Object>> getCrossServiceMetrics() {
        logger.info("Request received for cross-service metrics data");
        return ResponseEntity.ok(dashboardService.getDashboardData().getCrossServiceMetrics());
    }
    
    /**
     * Get historical trend data only.
     * 
     * @return Historical trend data
     */
    @GetMapping("/trends")
    public ResponseEntity<Map<String, Object>> getHistoricalTrends() {
        logger.info("Request received for historical trend data");
        return ResponseEntity.ok(dashboardService.getDashboardData().getHistoricalTrends());
    }
}
