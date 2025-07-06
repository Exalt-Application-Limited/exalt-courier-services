package com.gogidix.courier.courier.hqadmin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Configuration properties for multi-region support.
 */
@Configuration
@ConfigurationProperties(prefix = "global-hq-admin.multi-region")
@Data
public class MultiRegionConfig {

    /**
     * Flag to enable/disable multi-region support.
     */
    private boolean enabled = true;
    
    /**
     * Default region code if not specified.
     */
    private String defaultRegionCode = "GLOBAL";
    
    /**
     * Regional configuration properties.
     */
    private List<RegionProperties> regions = new ArrayList<>();
    
    /**
     * Cache timeout in seconds for regional data.
     */
    private int cacheTimeoutSeconds = 300;
    
    /**
     * Health check interval in seconds.
     */
    private int healthCheckIntervalSeconds = 60;
    
    /**
     * Maximum number of retry attempts for region operations.
     */
    private int maxRetryAttempts = 3;
    
    /**
     * Sync strategy (PULL, PUSH, BIDIRECTIONAL).
     */
    private SyncStrategy syncStrategy = SyncStrategy.BIDIRECTIONAL;
    
    /**
     * Properties for a specific region.
     */
    @Data
    public static class RegionProperties {
        /**
         * Region code (unique identifier).
         */
        private String code;
        
        /**
         * Region name.
         */
        private String name;
        
        /**
         * Region API endpoint.
         */
        private String apiEndpoint;
        
        /**
         * Region API key.
         */
        private String apiKey;
        
        /**
         * Is this region active.
         */
        private boolean active = true;
        
        /**
         * Override settings specific to this region.
         */
        private Map<String, String> overrideSettings = new ConcurrentHashMap<>();
        
        /**
         * Sync interval in seconds.
         */
        private int syncIntervalSeconds = 900;
    }
    
    /**
     * Region synchronization strategy.
     */
    public enum SyncStrategy {
        /**
         * HQ pulls data from regions.
         */
        PULL,
        
        /**
         * HQ pushes data to regions.
         */
        PUSH,
        
        /**
         * Both pull and push operations happen.
         */
        BIDIRECTIONAL
    }
}
