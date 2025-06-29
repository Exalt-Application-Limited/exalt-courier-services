package com.exalt.courier.courier.branch.dashboard.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microsocial.courier.branch.dashboard.BranchDashboardCommunicationHandler;
import com.microsocial.courier.branch.dashboard.BranchMetricsDataProvider;
import com.microsocial.courier.branch.dashboard.model.BranchMetricsData;
import com.microsocial.courier.branch.dashboard.model.DashboardMessage;
import com.microsocial.courier.branch.dashboard.model.MessagePriority;
import com.microsocial.courier.branch.dashboard.model.MessageType;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * REST Controller for Branch Dashboard operations.
 * Provides endpoints for metrics retrieval and dashboard communication.
 */
@RestController
@RequestMapping("/api/branch")
public class BranchDashboardController {

    private static final Logger logger = LoggerFactory.getLogger(BranchDashboardController.class);
    
    private final BranchMetricsDataProvider metricsDataProvider;
    private final BranchDashboardCommunicationHandler communicationHandler;
    private final String branchId;
    private final String regionId;
    
    // Simple counter to track API calls for status reporting
    private final AtomicLong apiCallCounter = new AtomicLong(0);
    private final AtomicLong lastSyncTimestamp = new AtomicLong(System.currentTimeMillis());

    @Autowired
    public BranchDashboardController(
            BranchMetricsDataProvider metricsDataProvider,
            BranchDashboardCommunicationHandler communicationHandler,
            @org.springframework.beans.factory.annotation.Value("${dashboard.branch.id}") String branchId,
            @org.springframework.beans.factory.annotation.Value("${dashboard.region.id}") String regionId) {
        this.metricsDataProvider = metricsDataProvider;
        this.communicationHandler = communicationHandler;
        this.branchId = branchId;
        this.regionId = regionId;
    }

    /**
     * Get current branch metrics
     * 
     * @return Current branch metrics data
     */
    @GetMapping("/metrics")
    public ResponseEntity<BranchMetricsData> getBranchMetrics() {
        logger.info("API call to get branch metrics");
        apiCallCounter.incrementAndGet();
        
        BranchMetricsData metrics = metricsDataProvider.collectBranchMetrics();
        return ResponseEntity.ok(metrics);
    }

    /**
     * Get dashboard connection status
     * 
     * @return Dashboard connection status information
     */
    @GetMapping("/dashboard/status")
    public ResponseEntity<Map<String, Object>> getDashboardStatus() {
        logger.info("API call to get dashboard status");
        apiCallCounter.incrementAndGet();
        
        Map<String, Object> status = new HashMap<>();
        status.put("branchId", branchId);
        status.put("regionId", regionId);
        status.put("apiCallCount", apiCallCounter.get());
        status.put("lastSyncTimestamp", lastSyncTimestamp.get());
        status.put("connectionActive", true); // This would be dynamic in a real implementation
        
        return ResponseEntity.ok(status);
    }

    /**
     * Force synchronization with Regional dashboard
     * 
     * @return Synchronization result
     */
    @PostMapping("/dashboard/sync")
    public ResponseEntity<Map<String, Object>> syncWithRegionalDashboard() {
        logger.info("API call to force synchronization with Regional dashboard");
        apiCallCounter.incrementAndGet();
        
        // Send cached messages
        communicationHandler.sendCachedMessages();
        
        // Send cached metrics
        metricsDataProvider.sendCachedMetrics();
        
        // Send a status update message
        DashboardMessage statusMessage = new DashboardMessage();
        statusMessage.setMessageType(MessageType.STATUS_UPDATE);
        statusMessage.setSourceId(branchId);
        statusMessage.setTargetId(regionId);
        statusMessage.setPriority(MessagePriority.NORMAL);
        statusMessage.setContent("Manual sync triggered from Branch");
        communicationHandler.sendMessageToRegional(statusMessage);
        
        // Collect and send fresh metrics
        BranchMetricsData metrics = metricsDataProvider.collectBranchMetrics();
        metricsDataProvider.sendMetricsToRegional(metrics);
        
        // Update last sync timestamp
        lastSyncTimestamp.set(System.currentTimeMillis());
        
        // Return sync result
        Map<String, Object> result = new HashMap<>();
        result.put("syncTriggered", true);
        result.put("timestamp", lastSyncTimestamp.get());
        result.put("metricsCollected", true);
        result.put("statusMessageSent", true);
        
        return ResponseEntity.ok(result);
    }
} 