package com.exalt.courier.regionaladmin.config.dashboard;

import com.microecosystem.courier.shared.dashboard.DashboardCommunicationService;
import com.microecosystem.courier.shared.dashboard.DashboardDataAggregationService;
import com.microecosystem.courier.shared.dashboard.DashboardLevel;
import com.microecosystem.courier.shared.dashboard.DataType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Configuration for dashboard communication and integration in the Regional Admin application.
 * Acts as a bridge between Global HQ Admin and Branch/Courier levels.
 */
@Configuration
public class RegionalDashboardConfig {

    @Autowired
    private Environment env;
    
    /**
     * Configure dashboard level for this application.
     */
    @Bean(name = "dashboardLevel")
    public String dashboardLevel() {
        return DashboardLevel.REGIONAL;
    }
    
    /**
     * Configure dashboard ID for this application.
     */
    @Bean(name = "dashboardId")
    public String dashboardId() {
        return env.getProperty("spring.application.name", "regional-admin");
    }
    
    /**
     * Configure dashboard communication handlers.
     * The regional admin dashboard communicates with both global and branch levels.
     */
    @Bean
    public RegionalDashboardCommunicationHandler dashboardCommunicationHandler(
            DashboardCommunicationService communicationService) {
        return new RegionalDashboardCommunicationHandler(communicationService);
    }
    
    /**
     * Configure dashboard data aggregation handlers.
     * The regional admin dashboard aggregates data from branch levels and forwards to global.
     */
    @Bean
    public RegionalDashboardDataAggregationHandler dashboardDataAggregationHandler(
            DashboardDataAggregationService aggregationService) {
        return new RegionalDashboardDataAggregationHandler(aggregationService);
    }
    
    /**
     * Setup standard data providers for regional metrics.
     */
    @Bean
    public RegionalMetricsDataProvider regionalMetricsDataProvider(
            DashboardDataAggregationService aggregationService) {
        RegionalMetricsDataProvider provider = new RegionalMetricsDataProvider();
        
        // Register provider for different data types
        aggregationService.registerDataProvider(DataType.DELIVERY_METRICS, provider);
        aggregationService.registerDataProvider(DataType.DRIVER_PERFORMANCE, provider);
        aggregationService.registerDataProvider(DataType.FINANCIAL_METRICS, provider);
        aggregationService.registerDataProvider(DataType.OPERATIONAL_METRICS, provider);
        aggregationService.registerDataProvider(DataType.CUSTOMER_SATISFACTION, provider);
        aggregationService.registerDataProvider(DataType.SYSTEM_HEALTH, provider);
        
        return provider;
    }
    
    /**
     * Configure the branch data collector that pulls data from branch/courier level.
     */
    @Bean
    public BranchDataCollectorService branchDataCollectorService(
            DashboardCommunicationService communicationService,
            DashboardDataAggregationService aggregationService) {
        return new BranchDataCollectorService(communicationService, aggregationService);
    }
}
