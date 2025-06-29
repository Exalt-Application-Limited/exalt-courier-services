package com.exalt.courier.regionaladmin.config.dashboard;

import com.microecosystem.courier.shared.dashboard.DashboardCommunicationService;
import com.microecosystem.courier.shared.dashboard.DashboardDataAggregationService;
import com.microecosystem.courier.shared.dashboard.DashboardLevel;
import com.microecosystem.courier.shared.dashboard.DashboardMessage;
import com.microecosystem.courier.shared.dashboard.DataType;
import com.microecosystem.courier.shared.dashboard.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Service to actively collect data from branch/courier level when needed.
 * This complements the passive receipt of data through the aggregation handlers.
 */
public class BranchDataCollectorService {

    private static final Logger logger = LoggerFactory.getLogger(BranchDataCollectorService.class);
    
    private final DashboardCommunicationService communicationService;
    private final DashboardDataAggregationService aggregationService;
    
    public BranchDataCollectorService(DashboardCommunicationService communicationService,
                                     DashboardDataAggregationService aggregationService) {
        this.communicationService = communicationService;
        this.aggregationService = aggregationService;
    }
    
    @PostConstruct
    public void init() {
        logger.info("Initializing Branch Data Collector Service");
    }
    
    /**
     * Proactively request system health data from all branches every 5 minutes.
     * This complements the passive reception of data.
     */
    @Scheduled(fixedRate = 300000) // Every 5 minutes
    public void collectSystemHealthData() {
        logger.debug("Proactively collecting system health data from branches");
        requestDataFromBranches(DataType.SYSTEM_HEALTH);
    }
    
    /**
     * Proactively request delivery metrics from all branches every 15 minutes.
     */
    @Scheduled(fixedRate = 900000) // Every 15 minutes
    public void collectDeliveryMetricsData() {
        logger.debug("Proactively collecting delivery metrics data from branches");
        requestDataFromBranches(DataType.DELIVERY_METRICS);
    }
    
    /**
     * Proactively request driver performance data from all branches hourly.
     */
    @Scheduled(fixedRate = 3600000) // Every hour
    public void collectDriverPerformanceData() {
        logger.debug("Proactively collecting driver performance data from branches");
        requestDataFromBranches(DataType.DRIVER_PERFORMANCE);
    }
    
    /**
     * Request specific data from all branches in this region.
     */
    public CompletableFuture<Boolean> requestDataFromBranches(String dataType) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("dataType", dataType);
        metadata.put("requestId", UUID.randomUUID().toString());
        metadata.put("priority", "normal");
        
        DashboardMessage dataRequest = new DashboardMessage.Builder()
                .withId(UUID.randomUUID().toString())
                .from(DashboardLevel.REGIONAL, communicationService.getCurrentDashboardId())
                .to(DashboardLevel.BRANCH, null) // null ID means broadcast to all branches
                .ofType(MessageType.DATA_REQUEST)
                .withSubject("Request for " + dataType + " data")
                .withContent("Please provide the latest " + dataType + " data for regional aggregation")
                .withMetadata(metadata)
                .build();
        
        // Create a completable future to track the overall operation
        CompletableFuture<Boolean> resultFuture = new CompletableFuture<>();
        
        // Send the message to all branches
        communicationService.broadcastMessage(dataRequest, 
                java.util.Arrays.asList(DashboardLevel.BRANCH, DashboardLevel.LOCAL), null)
                .thenAccept(result -> {
                    int successCount = 0;
                    for (Boolean success : result.values()) {
                        if (success) {
                            successCount++;
                        }
                    }
                    
                    logger.info("Data request sent to {} branches with {} successful", 
                            result.size(), successCount);
                    
                    // Consider the operation successful if at least one branch received the request
                    resultFuture.complete(successCount > 0);
                })
                .exceptionally(ex -> {
                    logger.error("Error sending data request to branches", ex);
                    resultFuture.completeExceptionally(ex);
                    return null;
                });
        
        return resultFuture;
    }
    
    /**
     * Request data from a specific branch.
     */
    public CompletableFuture<Boolean> requestDataFromBranch(String branchId, String dataType) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("dataType", dataType);
        metadata.put("requestId", UUID.randomUUID().toString());
        metadata.put("priority", "high");
        
        DashboardMessage dataRequest = new DashboardMessage.Builder()
                .withId(UUID.randomUUID().toString())
                .from(DashboardLevel.REGIONAL, communicationService.getCurrentDashboardId())
                .to(DashboardLevel.BRANCH, branchId)
                .ofType(MessageType.DATA_REQUEST)
                .withSubject("Request for " + dataType + " data")
                .withContent("Please provide the latest " + dataType + " data for regional aggregation")
                .withMetadata(metadata)
                .build();
        
        // Send the message to the specific branch
        return communicationService.sendMessage(dataRequest);
    }
    
    /**
     * Check the status of a specific branch.
     */
    public CompletableFuture<Boolean> checkBranchStatus(String branchId) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("requestId", UUID.randomUUID().toString());
        metadata.put("priority", "high");
        
        DashboardMessage statusRequest = new DashboardMessage.Builder()
                .withId(UUID.randomUUID().toString())
                .from(DashboardLevel.REGIONAL, communicationService.getCurrentDashboardId())
                .to(DashboardLevel.BRANCH, branchId)
                .ofType(MessageType.ACTION_REQUEST)
                .withSubject("Status check request")
                .withContent("Please provide your current operational status")
                .withMetadata(metadata)
                .build();
        
        // Send the message to the specific branch
        return communicationService.sendMessage(statusRequest);
    }
    
    /**
     * Force synchronization of all branch data.
     * This is useful for recovering from failures or synchronizing a new regional instance.
     */
    public CompletableFuture<Map<String, Boolean>> forceSyncAllBranchData() {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("requestId", UUID.randomUUID().toString());
        metadata.put("priority", "high");
        metadata.put("fullSync", "true");
        
        DashboardMessage syncRequest = new DashboardMessage.Builder()
                .withId(UUID.randomUUID().toString())
                .from(DashboardLevel.REGIONAL, communicationService.getCurrentDashboardId())
                .to(DashboardLevel.BRANCH, null) // null ID means broadcast to all branches
                .ofType(MessageType.SYNC_REQUEST)
                .withSubject("Full data synchronization request")
                .withContent("Please provide all available metrics and status data for regional synchronization")
                .withMetadata(metadata)
                .build();
        
        // Send the message to all branches
        return communicationService.broadcastMessage(syncRequest, 
                java.util.Arrays.asList(DashboardLevel.BRANCH, DashboardLevel.LOCAL), null);
    }
}
