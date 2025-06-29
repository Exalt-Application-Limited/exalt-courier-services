package com.exalt.courier.regionaladmin.config.dashboard;

import com.microecosystem.courier.shared.dashboard.DashboardDataAggregationService;
import com.microecosystem.courier.shared.dashboard.DashboardDataHandler;
import com.microecosystem.courier.shared.dashboard.DashboardDataTransfer;
import com.microecosystem.courier.shared.dashboard.DataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handler for dashboard data aggregation in Regional Admin.
 * Handles both aggregating data from branches and forwarding to global HQ.
 */
public class RegionalDashboardDataAggregationHandler {

    private static final Logger logger = LoggerFactory.getLogger(RegionalDashboardDataAggregationHandler.class);
    
    private final DashboardDataAggregationService aggregationService;
    private final Map<String, String> scheduleIds = new ConcurrentHashMap<>();
    
    public RegionalDashboardDataAggregationHandler(DashboardDataAggregationService aggregationService) {
        this.aggregationService = aggregationService;
    }
    
    @PostConstruct
    public void init() {
        logger.info("Initializing Regional Admin dashboard data aggregation handler");
    }
    
    @PreDestroy
    public void cleanup() {
        // Cancel all scheduled aggregations
        for (String scheduleId : scheduleIds.values()) {
            aggregationService.cancelPeriodicAggregation(scheduleId);
        }
        scheduleIds.clear();
    }
    
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        logger.info("Setting up periodic data aggregation after application startup");
        
        // Schedule periodic aggregation for various data types from branch level
        scheduleDeliveryMetricsAggregation();
        scheduleDriverPerformanceAggregation();
        scheduleFinancialMetricsAggregation();
        scheduleOperationalMetricsAggregation();
        scheduleCustomerSatisfactionAggregation();
        scheduleSystemHealthAggregation();
        
        // Schedule periodic forwarding of aggregated data to Global HQ
        scheduleGlobalDataForwarding();
    }
    
    /**
     * Schedule periodic aggregation for delivery metrics from branches.
     */
    private void scheduleDeliveryMetricsAggregation() {
        DashboardDataHandler handler = this::handleDeliveryMetricsData;
        
        String scheduleId = aggregationService.schedulePeriodicAggregation(
                DataType.DELIVERY_METRICS, 
                "BRANCH", 
                3 * 60 * 1000, // Every 3 minutes
                handler);
        
        scheduleIds.put("deliveryMetricsAggregation", scheduleId);
        logger.debug("Scheduled delivery metrics aggregation with ID: {}", scheduleId);
    }
    
    /**
     * Schedule periodic aggregation for driver performance metrics from branches.
     */
    private void scheduleDriverPerformanceAggregation() {
        DashboardDataHandler handler = this::handleDriverPerformanceData;
        
        String scheduleId = aggregationService.schedulePeriodicAggregation(
                DataType.DRIVER_PERFORMANCE, 
                "BRANCH", 
                10 * 60 * 1000, // Every 10 minutes
                handler);
        
        scheduleIds.put("driverPerformanceAggregation", scheduleId);
        logger.debug("Scheduled driver performance aggregation with ID: {}", scheduleId);
    }
    
    /**
     * Schedule periodic aggregation for financial metrics from branches.
     */
    private void scheduleFinancialMetricsAggregation() {
        DashboardDataHandler handler = this::handleFinancialMetricsData;
        
        String scheduleId = aggregationService.schedulePeriodicAggregation(
                DataType.FINANCIAL_METRICS, 
                "BRANCH", 
                15 * 60 * 1000, // Every 15 minutes
                handler);
        
        scheduleIds.put("financialMetricsAggregation", scheduleId);
        logger.debug("Scheduled financial metrics aggregation with ID: {}", scheduleId);
    }
    
    /**
     * Schedule periodic aggregation for operational metrics from branches.
     */
    private void scheduleOperationalMetricsAggregation() {
        DashboardDataHandler handler = this::handleOperationalMetricsData;
        
        String scheduleId = aggregationService.schedulePeriodicAggregation(
                DataType.OPERATIONAL_METRICS, 
                "BRANCH", 
                5 * 60 * 1000, // Every 5 minutes
                handler);
        
        scheduleIds.put("operationalMetricsAggregation", scheduleId);
        logger.debug("Scheduled operational metrics aggregation with ID: {}", scheduleId);
    }
    
    /**
     * Schedule periodic aggregation for customer satisfaction metrics from branches.
     */
    private void scheduleCustomerSatisfactionAggregation() {
        DashboardDataHandler handler = this::handleCustomerSatisfactionData;
        
        String scheduleId = aggregationService.schedulePeriodicAggregation(
                DataType.CUSTOMER_SATISFACTION, 
                "BRANCH", 
                30 * 60 * 1000, // Every 30 minutes
                handler);
        
        scheduleIds.put("customerSatisfactionAggregation", scheduleId);
        logger.debug("Scheduled customer satisfaction aggregation with ID: {}", scheduleId);
    }
    
    /**
     * Schedule periodic aggregation for system health metrics from branches.
     */
    private void scheduleSystemHealthAggregation() {
        DashboardDataHandler handler = this::handleSystemHealthData;
        
        String scheduleId = aggregationService.schedulePeriodicAggregation(
                DataType.SYSTEM_HEALTH, 
                "BRANCH", 
                1 * 60 * 1000, // Every 1 minute
                handler);
        
        scheduleIds.put("systemHealthAggregation", scheduleId);
        logger.debug("Scheduled system health aggregation with ID: {}", scheduleId);
    }
    
    /**
     * Schedule periodic forwarding of aggregated data to Global HQ.
     */
    private void scheduleGlobalDataForwarding() {
        // Schedule forwarding of regional metrics to Global HQ
        scheduleForwardingTask(DataType.DELIVERY_METRICS, 5 * 60 * 1000); // Every 5 minutes
        scheduleForwardingTask(DataType.DRIVER_PERFORMANCE, 15 * 60 * 1000); // Every 15 minutes
        scheduleForwardingTask(DataType.FINANCIAL_METRICS, 30 * 60 * 1000); // Every 30 minutes
        scheduleForwardingTask(DataType.OPERATIONAL_METRICS, 10 * 60 * 1000); // Every 10 minutes
        scheduleForwardingTask(DataType.CUSTOMER_SATISFACTION, 60 * 60 * 1000); // Every hour
        scheduleForwardingTask(DataType.SYSTEM_HEALTH, 2 * 60 * 1000); // Every 2 minutes
    }
    
    /**
     * Schedule a task to forward regional metrics to Global HQ.
     */
    private void scheduleForwardingTask(String dataType, long interval) {
        String taskId = aggregationService.scheduleDataTransferTask(
                dataType, 
                "GLOBAL", 
                null, // null means send to all instances of that level
                interval);
        
        scheduleIds.put("forward" + dataType, taskId);
        logger.debug("Scheduled {} forwarding to Global HQ with ID: {}", dataType, taskId);
    }
    
    /**
     * Handle aggregated delivery metrics data from branches.
     */
    private void handleDeliveryMetricsData(DashboardDataTransfer data) {
        logger.info("Received aggregated delivery metrics data from {} branches", 
                data.getMetadata().get("aggregatedFrom"));
        
        // Process the aggregated data
        // (implementation would include updating regional dashboards, reports,
        // databases, etc. with the aggregated metrics from branches)
        
        // Store the aggregated data for later forwarding to Global HQ
        aggregationService.storeAggregatedData(DataType.DELIVERY_METRICS, data.getData(), data.getMetadata());
    }
    
    /**
     * Handle aggregated driver performance data from branches.
     */
    private void handleDriverPerformanceData(DashboardDataTransfer data) {
        logger.info("Received aggregated driver performance data from {} branches", 
                data.getMetadata().get("aggregatedFrom"));
        
        // Process the aggregated data
        // (implementation would include updating regional dashboards, reports,
        // databases, etc. with the aggregated metrics from branches)
        
        // Store the aggregated data for later forwarding to Global HQ
        aggregationService.storeAggregatedData(DataType.DRIVER_PERFORMANCE, data.getData(), data.getMetadata());
    }
    
    /**
     * Handle aggregated financial metrics data from branches.
     */
    private void handleFinancialMetricsData(DashboardDataTransfer data) {
        logger.info("Received aggregated financial metrics data from {} branches", 
                data.getMetadata().get("aggregatedFrom"));
        
        // Process the aggregated data
        // (implementation would include updating regional dashboards, reports,
        // databases, etc. with the aggregated metrics from branches)
        
        // Store the aggregated data for later forwarding to Global HQ
        aggregationService.storeAggregatedData(DataType.FINANCIAL_METRICS, data.getData(), data.getMetadata());
    }
    
    /**
     * Handle aggregated operational metrics data from branches.
     */
    private void handleOperationalMetricsData(DashboardDataTransfer data) {
        logger.info("Received aggregated operational metrics data from {} branches", 
                data.getMetadata().get("aggregatedFrom"));
        
        // Process the aggregated data
        // (implementation would include updating regional dashboards, reports,
        // databases, etc. with the aggregated metrics from branches)
        
        // Store the aggregated data for later forwarding to Global HQ
        aggregationService.storeAggregatedData(DataType.OPERATIONAL_METRICS, data.getData(), data.getMetadata());
    }
    
    /**
     * Handle aggregated customer satisfaction data from branches.
     */
    private void handleCustomerSatisfactionData(DashboardDataTransfer data) {
        logger.info("Received aggregated customer satisfaction data from {} branches", 
                data.getMetadata().get("aggregatedFrom"));
        
        // Process the aggregated data
        // (implementation would include updating regional dashboards, reports,
        // databases, etc. with the aggregated metrics from branches)
        
        // Store the aggregated data for later forwarding to Global HQ
        aggregationService.storeAggregatedData(DataType.CUSTOMER_SATISFACTION, data.getData(), data.getMetadata());
    }
    
    /**
     * Handle aggregated system health data from branches.
     */
    private void handleSystemHealthData(DashboardDataTransfer data) {
        logger.info("Received aggregated system health data from {} branches", 
                data.getMetadata().get("aggregatedFrom"));
        
        // Process the aggregated data
        // (implementation would include updating regional dashboards, monitoring systems,
        // alerting, etc. with the aggregated health metrics from branches)
        
        // Store the aggregated data for later forwarding to Global HQ
        aggregationService.storeAggregatedData(DataType.SYSTEM_HEALTH, data.getData(), data.getMetadata());
    }
}
