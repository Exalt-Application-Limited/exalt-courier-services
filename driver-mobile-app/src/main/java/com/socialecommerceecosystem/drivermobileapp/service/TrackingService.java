package com.gogidix.courier.drivermobileapp.service;

import com.socialecommerceecosystem.drivermobileapp.dto.tracking.DeliveryConfirmationDTO;
import com.socialecommerceecosystem.drivermobileapp.dto.tracking.TrackingEventDTO;
import com.socialecommerceecosystem.drivermobileapp.dto.tracking.TrackingInfoDTO;

import java.util.List;
import java.util.Map;

/**
 * Service interface for tracking operations.
 */
public interface TrackingService {
    
    /**
     * Get tracking information for a tracking number.
     *
     * @param trackingNumber the tracking number or packageId (when packageId is used, pass "pkg:" prefix)
     * @return the tracking information
     */
    TrackingInfoDTO getTrackingInfo(String trackingNumber);
    
    /**
     * Update the status of a package.
     *
     * @param packageId the package ID
     * @param status the new status
     * @param location the current location
     * @param description the status update description
     * @return the updated tracking information
     */
    TrackingInfoDTO updatePackageStatus(String packageId, String status, String location, String description);
    
    /**
     * Confirm delivery of a package.
     *
     * @param confirmation the delivery confirmation data
     * @return the updated tracking information
     */
    TrackingInfoDTO confirmDelivery(DeliveryConfirmationDTO confirmation);
    
    /**
     * Add a tracking event.
     *
     * @param event the tracking event data
     * @return the updated tracking information
     */
    TrackingInfoDTO addTrackingEvent(TrackingEventDTO event);
    
    /**
     * Get all tracking events for a package.
     * If packageId is "all", returns all offline tracking events that need to be synchronized.
     *
     * @param packageId the package ID or "all" for offline events
     * @return list of tracking events
     */
    List<TrackingEventDTO> getTrackingEvents(String packageId);
    
    /**
     * Store tracking event locally when offline.
     *
     * @param event the tracking event
     * @return true if successful
     */
    boolean storeOfflineTrackingEvent(TrackingEventDTO event);
    
    /**
     * Store delivery confirmation locally when offline.
     *
     * @param confirmation the delivery confirmation
     * @return true if successful
     */
    boolean storeOfflineDeliveryConfirmation(DeliveryConfirmationDTO confirmation);
    
    /**
     * Synchronize offline tracking data with the server.
     *
     * @param courierId the courier ID
     * @return map of sync results
     */
    Map<String, String> synchronizeOfflineTrackingData(String courierId);
    
    /**
     * Get packages assigned to a courier.
     *
     * @param courierId the courier ID
     * @return list of tracking info
     */
    List<TrackingInfoDTO> getPackagesByCourier(String courierId);
}
