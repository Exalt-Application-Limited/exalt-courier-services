package com.gogidix.courier.regionaladmin.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for caching.
 * Enables caching and configures cache managers for improved performance.
 */
@Configuration
@EnableCaching
public class CachingConfig {

    /**
     * Configure cache manager for regional admin dashboard data.
     * 
     * @return CacheManager for application caches
     */
    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager(
                "regionalOverview",
                "operationalMetrics",
                "financialMetrics",
                "customerMetrics",
                "deliveryMetrics"
        );
        
        return cacheManager;
    }
}
