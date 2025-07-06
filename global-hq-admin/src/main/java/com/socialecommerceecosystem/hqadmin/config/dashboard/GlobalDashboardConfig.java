package com.gogidix.courier.courier.hqadmin.config.dashboard;

import com.microecosystem.courier.shared.dashboard.DashboardCommunicationService;
import com.microecosystem.courier.shared.dashboard.DashboardDataAggregationService;
import com.microecosystem.courier.shared.dashboard.DashboardLevel;
import com.microecosystem.courier.shared.dashboard.DataType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Configuration for dashboard communication and integration in the Global HQ Admin application.
 */
@Configuration
public class GlobalDashboardConfig {

    @Autowired
    private Environment env;
    
    /**
     * Configure dashboard level for this application.
     */
    @Bean(name = "dashboardLevel")
    public String dashboardLevel() {
        return DashboardLevel.GLOBAL;
    }
    
    /**
     * Configure dashboard ID for this application.
     */
    @Bean(name = "dashboardId")
    public String dashboardId() {
        return env.getProperty("spring.application.name", "global-hq-admin");
    }
    
    /**
     * Configure dashboard communication handlers.
     */
    @Bean
    public DashboardCommunicationHandler dashboardCommunicationHandler(
            DashboardCommunicationService communicationService) {
        return new DashboardCommunicationHandler(communicationService);
    }
    
    /**
     * Configure dashboard data aggregation handlers.
     */
    @Bean
    public DashboardDataAggregationHandler dashboardDataAggregationHandler(
            DashboardDataAggregationService aggregationService) {
        return new DashboardDataAggregationHandler(aggregationService);
    }
    
    /**
     * Setup standard data providers for global metrics.
     */
    @Bean
    public GlobalMetricsDataProvider globalMetricsDataProvider(
            DashboardDataAggregationService aggregationService) {
        GlobalMetricsDataProvider provider = new GlobalMetricsDataProvider();
        
        // Register provider for different data types
        aggregationService.registerDataProvider(DataType.DELIVERY_METRICS, provider);
        aggregationService.registerDataProvider(DataType.FINANCIAL_METRICS, provider);
        aggregationService.registerDataProvider(DataType.OPERATIONAL_METRICS, provider);
        aggregationService.registerDataProvider(DataType.CUSTOMER_SATISFACTION, provider);
        aggregationService.registerDataProvider(DataType.SYSTEM_HEALTH, provider);
        
        return provider;
    }
}
