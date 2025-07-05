package com.gogidix.courier.regionaladmin.config.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.List;

/**
 * Configuration for cross-service integration.
 * Sets up connections to the various services that Regional Admin integrates with.
 */
@Configuration
public class CrossServiceIntegrationConfig {

    private static final Logger logger = LoggerFactory.getLogger(CrossServiceIntegrationConfig.class);
    
    @Autowired
    private DiscoveryClient discoveryClient;
    
    @Value("${regional.admin.service-integration.health-check-interval-ms:60000}")
    private long healthCheckInterval;

    // List of services that Regional Admin integrates with
    private static final String[] INTEGRATED_SERVICES = {
            "courier-management",
            "hq-admin",
            "branch-courier-app",
            "courier-onboarding",
            "routing-service",
            "commission-service",
            "third-party-integration",
            "international-shipping",
            "advanced-reporting",
            "distributed-tracing",
            "real-time-tracking"
    };
    
    /**
     * TaskScheduler for running service health checks.
     */
    @Bean
    public TaskScheduler integrationTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(5);
        scheduler.setThreadNamePrefix("svc-integration-");
        scheduler.initialize();
        return scheduler;
    }
    
    /**
     * Scheduled task to check the health of integrated services.
     * Runs periodically based on the configured interval.
     */
    @Scheduled(fixedDelayString = "${regional.admin.service-integration.health-check-interval-ms:60000}")
    public void checkServicesHealth() {
        logger.debug("Checking integrated services health...");
        
        for (String serviceName : INTEGRATED_SERVICES) {
            List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
            
            if (instances.isEmpty()) {
                logger.warn("No instances found for service: {}", serviceName);
            } else {
                logger.debug("Service {} has {} active instances", serviceName, instances.size());
            }
        }
    }
}
