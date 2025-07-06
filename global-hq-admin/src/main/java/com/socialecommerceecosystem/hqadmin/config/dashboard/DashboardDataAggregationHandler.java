package com.gogidix.courier.courier.hqadmin.config.dashboard;

import com.microecosystem.courier.shared.dashboard.DashboardDataAggregationService;
import com.microecosystem.courier.shared.dashboard.DashboardDataHandler;
import com.microecosystem.courier.shared.dashboard.DashboardDataTransfer;
import com.microecosystem.courier.shared.dashboard.DataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handler for dashboard data aggregation in Global HQ Admin.
 */
public class DashboardDataAggregationHandler {

    private static final Logger logger = LoggerFactory.getLogger(DashboardDataAggregationHandler.class);
    
    private final DashboardDataAggregationService aggregationService;
    private final Map<String, String> scheduleIds = new ConcurrentHashMap<>();
    
    public DashboardDataAggregationHandler(DashboardDataAggregationService aggregationService) {
        this.aggregationService = aggregationService;
    }
    
    @PostConstruct
    public void init() {
        logger.info("Initializing Global HQ Admin dashboard data aggregation handler");
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
        
        // Schedule periodic aggregation for various data types from regional dashboards
        scheduleDeliveryMetricsAggregation();
        scheduleDriverPerformanceAggregation();
        scheduleFinancialMetricsAggregation();
        scheduleOperationalMetricsAggregation();
        scheduleCustomerSatisfactionAggregation();
        scheduleSystemHealthAggregation();
    }
    
    /**
     * Schedule periodic aggregation for delivery metrics.
     */
    private void scheduleDeliveryMetricsAggregation() {
        DashboardDataHandler handler = this::handleDeliveryMetricsData;
        
        String scheduleId = aggregationService.schedulePeriodicAggregation(
                DataType.DELIVERY_METRICS, 
                "REGIONAL", 
                5 * 60 * 1000, // Every 5 minutes
                handler);
        
        scheduleIds.put("deliveryMetricsAggregation", scheduleId);
        logger.debug("Scheduled delivery metrics aggregation with ID: {}", scheduleId);
    }
    
    /**
     * Schedule periodic aggregation for driver performance metrics.
     */
    private void scheduleDriverPerformanceAggregation() {
        DashboardDataHandler handler = this::handleDriverPerformanceData;
        
        String scheduleId = aggregationService.schedulePeriodicAggregation(
                DataType.DRIVER_PERFORMANCE, 
                "REGIONAL", 
                15 * 60 * 1000, // Every 15 minutes
                handler);
        
        scheduleIds.put("driverPerformanceAggregation", scheduleId);
        logger.debug("Scheduled driver performance aggregation with ID: {}", scheduleId);
    }
    
    /**
     * Schedule periodic aggregation for financial metrics.
     */
    private void scheduleFinancialMetricsAggregation() {
        DashboardDataHandler handler = this::handleFinancialMetricsData;
        
        String scheduleId = aggregationService.schedulePeriodicAggregation(
                DataType.FINANCIAL_METRICS, 
                "REGIONAL", 
                30 * 60 * 1000, // Every 30 minutes
                handler);
        
        scheduleIds.put("financialMetricsAggregation", scheduleId);
        logger.debug("Scheduled financial metrics aggregation with ID: {}", scheduleId);
    }
    
    /**
     * Schedule periodic aggregation for operational metrics.
     */
    private void scheduleOperationalMetricsAggregation() {
        DashboardDataHandler handler = this::handleOperationalMetricsData;
        
        String scheduleId = aggregationService.schedulePeriodicAggregation(
                DataType.OPERATIONAL_METRICS, 
                "REGIONAL", 
                10 * 60 * 1000, // Every 10 minutes
                handler);
        
        scheduleIds.put("operationalMetricsAggregation", scheduleId);
        logger.debug("Scheduled operational metrics aggregation with ID: {}", scheduleId);
    }
    
    /**
     * Schedule periodic aggregation for customer satisfaction metrics.
     */
    private void scheduleCustomerSatisfactionAggregation() {
        DashboardDataHandler handler = this::handleCustomerSatisfactionData;
        
        String scheduleId = aggregationService.schedulePeriodicAggregation(
                DataType.CUSTOMER_SATISFACTION, 
                "REGIONAL", 
                60 * 60 * 1000, // Every hour
                handler);
        
        scheduleIds.put("customerSatisfactionAggregation", scheduleId);
        logger.debug("Scheduled customer satisfaction aggregation with ID: {}", scheduleId);
    }
    
    /**
     * Schedule periodic aggregation for system health metrics.
     */
    private void scheduleSystemHealthAggregation() {
        DashboardDataHandler handler = this::handleSystemHealthData;
        
        String scheduleId = aggregationService.schedulePeriodicAggregation(
                DataType.SYSTEM_HEALTH, 
                "REGIONAL", 
                2 * 60 * 1000, // Every 2 minutes
                handler);
        
        scheduleIds.put("systemHealthAggregation", scheduleId);
        logger.debug("Scheduled system health aggregation with ID: {}", scheduleId);
    }
    
    /**
     * Handle aggregated delivery metrics data.
     */
    private void handleDeliveryMetricsData(DashboardDataTransfer data) {
        logger.info("Received aggregated delivery metrics data from {} regions", 
                data.getMetadata().get("aggregatedFrom"));
        
        // Process the aggregated data
        // (implementation would include updating dashboards, reports,
        // databases, etc. with the aggregated metrics)
    }
    
    /**
     * Handle aggregated driver performance data.
     */
    private void handleDriverPerformanceData(DashboardDataTransfer data) {
        logger.info("Received aggregated driver performance data from {} regions", 
                data.getMetadata().get("aggregatedFrom"));
        
        // Process the aggregated data
        // (implementation would include updating dashboards, reports,
        // databases, etc. with the aggregated metrics)
    }
    
    /**
     * Handle aggregated financial metrics data.
     */
    private void handleFinancialMetricsData(DashboardDataTransfer data) {
        logger.info("Received aggregated financial metrics data from {} regions", 
                data.getMetadata().get("aggregatedFrom"));
        
        // Process the aggregated data
        // (implementation would include updating dashboards, reports,
        // databases, etc. with the aggregated metrics)
    }
    
    /**
     * Handle aggregated operational metrics data.
     */
    private void handleOperationalMetricsData(DashboardDataTransfer data) {
        logger.info("Received aggregated operational metrics data from {} regions", 
                data.getMetadata().get("aggregatedFrom"));
        
        // Process the aggregated data
        // (implementation would include updating dashboards, reports,
        // databases, etc. with the aggregated metrics)
    }
    
    /**
     * Handle aggregated customer satisfaction data.
     */
    private void handleCustomerSatisfactionData(DashboardDataTransfer data) {
        logger.info("Received aggregated customer satisfaction data from {} regions", 
                data.getMetadata().get("aggregatedFrom"));
        
        // Process the aggregated data
        // (implementation would include updating dashboards, reports,
        // databases, etc. with the aggregated metrics)
    }
    
    /**
     * Handle aggregated system health data.
     */
    private void handleSystemHealthData(DashboardDataTransfer data) {
        logger.info("Received aggregated system health data from {} regions", 
                data.getMetadata().get("aggregatedFrom"));
        
        // Process the aggregated data
        // (implementation would include updating dashboards, monitoring systems,
        // alerting, etc. with the aggregated health metrics)
    }
}
