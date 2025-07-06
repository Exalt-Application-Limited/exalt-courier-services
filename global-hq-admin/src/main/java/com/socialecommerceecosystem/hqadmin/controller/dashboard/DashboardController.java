package com.gogidix.courier.hqadmin.controller.dashboard;

import com.socialecommerceecosystem.hqadmin.service.dashboard.DashboardService;
import com.socialecommerceecosystem.hqadmin.service.dashboard.DashboardService.HealthStatus;
import com.socialecommerceecosystem.hqadmin.service.dashboard.DashboardService.PolicyActivity;
import com.socialecommerceecosystem.hqadmin.service.dashboard.DashboardService.RegionSummary;
import com.socialecommerceecosystem.hqadmin.service.dashboard.DashboardService.SystemAlert;
import com.socialecommerceecosystem.hqadmin.service.dashboard.DashboardService.SyncOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for the global health dashboard.
 */
@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Slf4j
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * GET /api/v1/dashboard/health : Get system health
     * 
     * @return the ResponseEntity with status 200 (OK) and the system health information
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, HealthStatus>> getSystemHealth() {
        log.debug("REST request to get system health");
        return ResponseEntity.ok(dashboardService.getSystemHealth());
    }

    /**
     * GET /api/v1/dashboard/metrics : Get performance metrics
     * 
     * @return the ResponseEntity with status 200 (OK) and the performance metrics
     */
    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getPerformanceMetrics() {
        log.debug("REST request to get performance metrics");
        return ResponseEntity.ok(dashboardService.getPerformanceMetrics());
    }

    /**
     * GET /api/v1/dashboard/regions : Get regional status summary
     * 
     * @return the ResponseEntity with status 200 (OK) and the regional status summary
     */
    @GetMapping("/regions")
    public ResponseEntity<Map<String, RegionSummary>> getRegionalStatusSummary() {
        log.debug("REST request to get regional status summary");
        return ResponseEntity.ok(dashboardService.getRegionalStatusSummary());
    }

    /**
     * GET /api/v1/dashboard/policy-activities : Get recent policy activities
     * 
     * @param limit the maximum number of activities to return (default 10)
     * @return the ResponseEntity with status 200 (OK) and the list of recent policy activities
     */
    @GetMapping("/policy-activities")
    public ResponseEntity<List<PolicyActivity>> getRecentPolicyActivities(@RequestParam(defaultValue = "10") int limit) {
        log.debug("REST request to get recent policy activities (limit: {})", limit);
        return ResponseEntity.ok(dashboardService.getRecentPolicyActivities(limit));
    }

    /**
     * GET /api/v1/dashboard/policy-statistics : Get policy statistics
     * 
     * @return the ResponseEntity with status 200 (OK) and the policy statistics
     */
    @GetMapping("/policy-statistics")
    public ResponseEntity<Map<String, Object>> getPolicyStatistics() {
        log.debug("REST request to get policy statistics");
        return ResponseEntity.ok(dashboardService.getPolicyStatistics());
    }

    /**
     * GET /api/v1/dashboard/sync-operations : Get active sync operations
     * 
     * @return the ResponseEntity with status 200 (OK) and the list of active sync operations
     */
    @GetMapping("/sync-operations")
    public ResponseEntity<List<SyncOperation>> getActiveSyncOperations() {
        log.debug("REST request to get active sync operations");
        return ResponseEntity.ok(dashboardService.getActiveSyncOperations());
    }

    /**
     * GET /api/v1/dashboard/alerts : Get system alerts
     * 
     * @return the ResponseEntity with status 200 (OK) and the list of system alerts
     */
    @GetMapping("/alerts")
    public ResponseEntity<List<SystemAlert>> getSystemAlerts() {
        log.debug("REST request to get system alerts");
        return ResponseEntity.ok(dashboardService.getSystemAlerts());
    }

    /**
     * GET /api/v1/dashboard/summary : Get a complete dashboard summary
     * 
     * @param policyActivitiesLimit the maximum number of policy activities to return (default 5)
     * @return the ResponseEntity with status 200 (OK) and the dashboard summary
     */
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getDashboardSummary(
            @RequestParam(defaultValue = "5") int policyActivitiesLimit) {
        log.debug("REST request to get dashboard summary");
        
        Map<String, Object> summary = new HashMap<>();
        
        // Add system health
        summary.put("health", dashboardService.getSystemHealth());
        
        // Add performance metrics
        summary.put("metrics", dashboardService.getPerformanceMetrics());
        
        // Add regional status summary
        summary.put("regions", dashboardService.getRegionalStatusSummary());
        
        // Add recent policy activities
        summary.put("policyActivities", dashboardService.getRecentPolicyActivities(policyActivitiesLimit));
        
        // Add policy statistics
        summary.put("policyStatistics", dashboardService.getPolicyStatistics());
        
        // Add active sync operations
        summary.put("syncOperations", dashboardService.getActiveSyncOperations());
        
        // Add system alerts
        summary.put("alerts", dashboardService.getSystemAlerts());
        
        return ResponseEntity.ok(summary);
    }
}
