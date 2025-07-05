package com.gogidix.courier.regionaladmin.service.integration;

import java.util.List;
import java.util.Map;

/**
 * Interface for tracking service integration.
 * Provides methods for integrating with the real-time tracking service.
 */
public interface TrackingIntegrationService {

    /**
     * Get current tracking status for a region.
     * 
     * @param regionCode Region code
     * @return Map of tracking data
     */
    Map<String, Object> getCurrentTrackingStatus(String regionCode);
    
    /**
     * Get active delivery count for a region.
     * 
     * @param regionCode Region code
     * @return Number of active deliveries
     */
    int getActiveDeliveryCount(String regionCode);
    
    /**
     * Get tracking events for a region.
     * 
     * @param regionCode Region code
     * @param limit Maximum number of events to return
     * @return List of tracking events
     */
    List<Map<String, Object>> getTrackingEvents(String regionCode, int limit);
    
    /**
     * Get tracking status summary for a region.
     * 
     * @param regionCode Region code
     * @return Map of status counts
     */
    Map<String, Integer> getTrackingStatusSummary(String regionCode);
}
