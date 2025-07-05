package com.gogidix.courierservices.tracking.$1;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cache configuration for the tracking service.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Cache manager bean.
     *
     * @return the cache manager
     */
    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        cacheManager.setCacheNames(java.util.Arrays.asList(
                "packages",
                "packagesByStatus",
                "trackingEvents",
                "packageStatistics",
                "courierInfo",
                "routeInfo"
        ));
        return cacheManager;
    }
} 