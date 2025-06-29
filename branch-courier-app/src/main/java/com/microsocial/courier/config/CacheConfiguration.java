package com.exalt.courier.courier.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Configuration for caching
 */
@Configuration
@EnableCaching
@EnableScheduling
public class CacheConfiguration {

    /**
     * Creates a cache manager for local caching
     */
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(
                "assignments", 
                "assignment-tasks", 
                "assignment-task", 
                "couriers",
                "assignment-counts",
                "courier-counts",
                "courier-metrics",
                "branch-metrics"
        );
    }
    
    /**
     * Refresh assignment caches every 5 minutes
     */
    @Scheduled(fixedRateString = "${branch-courier-app.cache.refresh-rate-ms:300000}")
    public void refreshAssignmentCaches() {
        CacheManager manager = cacheManager();
        manager.getCache("assignments").clear();
        manager.getCache("assignment-tasks").clear();
        manager.getCache("assignment-task").clear();
        manager.getCache("assignment-counts").clear();
    }
    
    /**
     * Refresh courier caches every 10 minutes
     */
    @Scheduled(fixedRateString = "${branch-courier-app.cache.courier-refresh-rate-ms:600000}")
    public void refreshCourierCaches() {
        CacheManager manager = cacheManager();
        manager.getCache("couriers").clear();
        manager.getCache("courier-counts").clear();
    }
    
    /**
     * Refresh metrics caches every 15 minutes
     */
    @Scheduled(fixedRateString = "${branch-courier-app.cache.metrics-refresh-rate-ms:900000}")
    public void refreshMetricsCaches() {
        CacheManager manager = cacheManager();
        manager.getCache("courier-metrics").clear();
        manager.getCache("branch-metrics").clear();
    }
}