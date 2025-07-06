package com.gogidix.courier.branch.dashboard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.gogidix.courier.branch.dashboard.model.BranchMetricsData;
import com.gogidix.courier.branch.dashboard.model.DeliveryMetrics;
import com.gogidix.courier.branch.dashboard.model.PerformanceMetrics;
import com.gogidix.courier.branch.dashboard.model.ResourceMetrics;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Provides metrics data from the Branch level to the Regional dashboard.
 * This class collects, aggregates, and sends branch-specific metrics to the Regional dashboard
 * for further aggregation and reporting.
 */
@Component
public class BranchMetricsDataProvider {

    private static final Logger logger = LoggerFactory.getLogger(BranchMetricsDataProvider.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String branchMetricsTopic;
    private final String branchId;
    private final String regionId;
    private final BranchDataCacheService dataCacheService;

    public BranchMetricsDataProvider(
            KafkaTemplate<String, Object> kafkaTemplate,
            String branchMetricsTopic,
            String branchId,
            String regionId) {
        this.kafkaTemplate = kafkaTemplate;
        this.branchMetricsTopic = branchMetricsTopic;
        this.branchId = branchId;
        this.regionId = regionId;
        this.dataCacheService = new BranchDataCacheService();
    }

    /**
     * Scheduled task to collect and send branch metrics to the Regional dashboard.
     * Runs every 5 minutes to provide regular updates.
     */
    @Scheduled(fixedRateString = "${dashboard.metrics.reporting.interval:300000}")
    public void scheduledMetricsCollection() {
        try {
            logger.debug("Running scheduled metrics collection for branch {}", branchId);
            BranchMetricsData metricsData = collectBranchMetrics();
            sendMetricsToRegional(metricsData);
        } catch (Exception ex) {
            logger.error("Error in scheduled metrics collection", ex);
        }
    }

    /**
     * Collects all branch metrics from various sources.
     *
     * @return Aggregated branch metrics data
     */
    public BranchMetricsData collectBranchMetrics() {
        logger.info("Collecting branch metrics for branch {}", branchId);
        
        BranchMetricsData metricsData = new BranchMetricsData();
        metricsData.setMetricsId(UUID.randomUUID().toString());
        metricsData.setBranchId(branchId);
        metricsData.setRegionId(regionId);
        metricsData.setTimestamp(LocalDateTime.now());
        
        // Collect metrics from various branch systems
        metricsData.setDeliveryMetrics(collectDeliveryMetrics());
        metricsData.setPerformanceMetrics(collectPerformanceMetrics());
        metricsData.setResourceMetrics(collectResourceMetrics());
        
        logger.debug("Branch metrics collected successfully");
        return metricsData;
    }

    /**
     * Sends the collected metrics to the Regional dashboard.
     *
     * @param metricsData The metrics data to send
     * @return CompletableFuture for async handling
     */
    public CompletableFuture<Void> sendMetricsToRegional(BranchMetricsData metricsData) {
        try {
            logger.info("Sending branch metrics to Regional dashboard");
            
            // Store a copy in cache in case of offline operation
            dataCacheService.cacheOutgoingMetrics(metricsData);
            
            return kafkaTemplate.send(branchMetricsTopic, branchId, metricsData)
                    .thenRun(() -> {
                        logger.info("Metrics sent successfully to Regional dashboard");
                        dataCacheService.markMetricsDelivered(metricsData.getMetricsId());
                    })
                    .exceptionally(ex -> {
                        logger.error("Failed to send metrics to Regional dashboard", ex);
                        return null;
                    });
        } catch (Exception ex) {
            logger.error("Error preparing metrics for Regional dashboard", ex);
            return CompletableFuture.failedFuture(ex);
        }
    }
    
    /**
     * Collects delivery-related metrics from the branch system.
     *
     * @return Delivery metrics
     */
    private DeliveryMetrics collectDeliveryMetrics() {
        // This would interface with delivery tracking systems to get real metrics
        DeliveryMetrics metrics = new DeliveryMetrics();
        
        // For now, we're implementing placeholder logic
        metrics.setTotalDeliveriesInProgress(25);
        metrics.setTotalDeliveriesCompleted(150);
        metrics.setTotalDeliveriesFailed(5);
        metrics.setAverageDeliveryTime(45.5); // in minutes
        metrics.setOnTimeDeliveryPercentage(92.3);
        
        return metrics;
    }
    
    /**
     * Collects courier performance metrics from the branch system.
     *
     * @return Performance metrics
     */
    private PerformanceMetrics collectPerformanceMetrics() {
        // This would interface with courier performance systems to get real metrics
        PerformanceMetrics metrics = new PerformanceMetrics();
        
        // For now, we're implementing placeholder logic
        metrics.setActiveCouriers(18);
        metrics.setAverageDeliveriesPerCourier(8.3);
        metrics.setAverageRating(4.7);
        metrics.setEfficiencyScore(87.5);
        
        return metrics;
    }
    
    /**
     * Collects resource utilization metrics from the branch system.
     *
     * @return Resource metrics
     */
    private ResourceMetrics collectResourceMetrics() {
        // This would interface with resource management systems to get real metrics
        ResourceMetrics metrics = new ResourceMetrics();
        
        // For now, we're implementing placeholder logic
        metrics.setVehiclesInUse(15);
        metrics.setVehiclesAvailable(3);
        metrics.setFuelConsumption(125.6); // in liters
        metrics.setMaintenanceScheduled(2);
        
        return metrics;
    }
    
    /**
     * Responds to an on-demand request for metrics from the Regional dashboard.
     * This is called when a DATA_REQUEST message is received.
     *
     * @return The collected metrics data
     */
    public BranchMetricsData provideMetricsOnDemand() {
        return collectBranchMetrics();
    }
    
    /**
     * Checks for and sends any cached metrics that failed to send previously.
     * This is typically called when connectivity is restored.
     */
    public void sendCachedMetrics() {
        dataCacheService.getUndeliveredMetrics().forEach(this::sendMetricsToRegional);
    }
} 