package com.gogidix.courier.drivermobileapp.service.sync;

import com.socialecommerceecosystem.drivermobileapp.dto.assignment.AssignmentDTO;
import com.socialecommerceecosystem.drivermobileapp.dto.assignment.OfflineSyncRequestDTO;
import com.socialecommerceecosystem.drivermobileapp.dto.assignment.OfflineSyncResponseDTO;
import com.socialecommerceecosystem.drivermobileapp.dto.assignment.SyncResultDTO;
import com.socialecommerceecosystem.drivermobileapp.dto.tracking.DeliveryConfirmationDTO;
import com.socialecommerceecosystem.drivermobileapp.dto.tracking.TrackingEventDTO;

import java.util.List;
import java.util.Map;

/**
 * Service interface for managing offline data synchronization operations.
 * This service handles the synchronization of locally stored data with the server
 * when connection is re-established.
 */
public interface SynchronizationService {
    
    /**
     * Performs a comprehensive synchronization of all offline data.
     *
     * @param courierId the ID of the courier performing synchronization
     * @param deviceId unique identifier for the mobile device
     * @return summary of synchronization results
     */
    OfflineSyncResponseDTO synchronizeAll(String courierId, String deviceId);
    
    /**
     * Synchronizes assignment data with the server.
     *
     * @param syncRequest the synchronization request containing assignments to sync
     * @return the synchronization response with results
     */
    OfflineSyncResponseDTO synchronizeAssignments(OfflineSyncRequestDTO syncRequest);
    
    /**
     * Synchronizes tracking events with the server.
     *
     * @param courierId the courier ID
     * @param events list of tracking events to synchronize
     * @return map of event IDs to sync results
     */
    Map<String, String> synchronizeTrackingEvents(String courierId, List<TrackingEventDTO> events);
    
    /**
     * Synchronizes delivery confirmations with the server.
     *
     * @param courierId the courier ID
     * @param confirmations list of delivery confirmations to synchronize
     * @return map of package IDs to sync results
     */
    Map<String, String> synchronizeDeliveryConfirmations(String courierId, List<DeliveryConfirmationDTO> confirmations);
    
    /**
     * Retrieves new assignments and updates from the server.
     *
     * @param courierId the courier ID
     * @param lastSyncTimestamp timestamp of the last synchronization
     * @return list of new or updated assignments
     */
    List<AssignmentDTO> fetchServerUpdates(String courierId, String lastSyncTimestamp);
    
    /**
     * Resolves conflicts between local and server data.
     *
     * @param localData the local version of the data
     * @param serverData the server version of the data
     * @param entityType the type of entity being synchronized
     * @param entityId the ID of the entity
     * @return the resolved version of the data
     */
    Map<String, Object> resolveConflicts(Map<String, Object> localData, Map<String, Object> serverData, 
                                        String entityType, String entityId);
    
    /**
     * Marks an entity as synchronized with the server.
     *
     * @param entityType the type of entity
     * @param entityId the ID of the entity
     * @return true if successful
     */
    boolean markAsSynchronized(String entityType, String entityId);
    
    /**
     * Stores the sync token and timestamp for future synchronization operations.
     *
     * @param courierId the courier ID
     * @param syncToken the synchronization token
     * @param timestamp the timestamp of synchronization
     * @return true if successful
     */
    boolean storeSyncState(String courierId, String syncToken, String timestamp);
    
    /**
     * Gets the last synchronization state for a courier.
     *
     * @param courierId the courier ID
     * @return map containing the sync token and timestamp
     */
    Map<String, String> getLastSyncState(String courierId);
}
