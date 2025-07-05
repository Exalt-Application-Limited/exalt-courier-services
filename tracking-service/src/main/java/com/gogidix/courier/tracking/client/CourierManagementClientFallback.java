package com.gogidix.courier.tracking.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

/**
 * Fallback implementation for the CourierManagementClient.
 */
@Component
public class CourierManagementClientFallback implements CourierManagementClient {
    
    private static final Logger log = LoggerFactory.getLogger(CourierManagementClientFallback.class);
    
    @Override
    public Map<String, Object> getCourierInfo(Long courierId) {
        log.warn("Fallback: Unable to get courier information for courier {}", courierId);
        return Collections.emptyMap();
    }
    
    @Override
    public void notifyPackageStatusChange(Long courierId, Map<String, Object> statusChangeInfo) {
        log.warn("Fallback: Unable to notify courier {} about package status change", courierId);
    }
    
    @Override
    public Map<String, Object> getCourierLocation(Long courierId) {
        log.warn("Fallback: Unable to get location for courier {}", courierId);
        return Collections.emptyMap();
    }
} 