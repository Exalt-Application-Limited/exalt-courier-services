package com.exalt.courier.location.config;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;

/**
 * Configuration for application caching.
 * Uses Caffeine as the caching provider for its high performance and low overhead.
 * Defines different cache configurations based on access patterns and data volatility.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Cache manager for frequently accessed but short-lived data.
     * Suitable for lookup data that changes occasionally.
     * 
     * @return configured cache manager
     */
    @Bean
    public CacheManager shipmentCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCacheNames(Arrays.asList(
            "shipmentsByTrackingNumber", 
            "shipmentSummaryByTrackingNumber",
            "shipmentsByCustomer",
            "shipmentSummariesByCustomer",
            "shipmentStatusCounts",
            "pendingDeliveryShipments"
        ));
        cacheManager.setCaffeine(Caffeine.newBuilder()
            .initialCapacity(100)
            .maximumSize(1000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .recordStats());
        return cacheManager;
    }
    
    /**
     * Cache manager for reference data that changes infrequently.
     * Suitable for configuration data and lookup tables.
     * 
     * @return configured cache manager for reference data
     */
    @Bean
    public CacheManager referenceCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCacheNames(Arrays.asList(
            "serviceTypes", 
            "locationDetails",
            "countryZones",
            "shippingRates"
        ));
        cacheManager.setCaffeine(Caffeine.newBuilder()
            .initialCapacity(50)
            .maximumSize(200)
            .expireAfterWrite(6, TimeUnit.HOURS)
            .recordStats());
        return cacheManager;
    }
    
    /**
     * Cache manager for heavily accessed dashboard data.
     * Uses a shorter TTL to ensure reasonable freshness while reducing database load.
     * 
     * @return configured cache manager for dashboard data
     */
    @Bean
    public CacheManager dashboardCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCacheNames(Arrays.asList(
            "dashboardMetrics", 
            "revenueReports",
            "locationPerformance"
        ));
        cacheManager.setCaffeine(Caffeine.newBuilder()
            .initialCapacity(20)
            .maximumSize(100)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .recordStats());
        return cacheManager;
    }
}
