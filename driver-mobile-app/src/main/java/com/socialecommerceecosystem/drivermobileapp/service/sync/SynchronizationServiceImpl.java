package com.gogidix.courier.courier.drivermobileapp.service.sync;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.socialecommerceecosystem.drivermobileapp.dto.assignment.AssignmentDTO;
import com.socialecommerceecosystem.drivermobileapp.dto.assignment.OfflineSyncRequestDTO;
import com.socialecommerceecosystem.drivermobileapp.dto.assignment.OfflineSyncResponseDTO;
import com.socialecommerceecosystem.drivermobileapp.dto.assignment.SyncResultDTO;
import com.socialecommerceecosystem.drivermobileapp.dto.tracking.DeliveryConfirmationDTO;
import com.socialecommerceecosystem.drivermobileapp.dto.tracking.TrackingEventDTO;
import com.socialecommerceecosystem.drivermobileapp.service.AssignmentService;
import com.socialecommerceecosystem.drivermobileapp.service.TrackingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Implementation of the SynchronizationService interface.
 * Manages offline data synchronization between mobile devices and the server.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SynchronizationServiceImpl implements SynchronizationService {

    private final AssignmentService assignmentService;
    private final TrackingService trackingService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    
    // In-memory cache of sync states for quick access
    private final Map<String, Map<String, String>> syncStateCache = new ConcurrentHashMap<>();
    
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    @Override
    public OfflineSyncResponseDTO synchronizeAll(String courierId, String deviceId) {
        log.info("Starting comprehensive synchronization for courier: {}, device: {}", courierId, deviceId);
        
        // Get pending assignments for this courier that need synchronization
        List<AssignmentDTO> pendingAssignments = assignmentService.getCachedAssignments(courierId).stream()
                .filter(a -> "PENDING_SYNC".equals(a.getSyncStatus()))
                .collect(Collectors.toList());
        
        // Build sync request
        Map<String, AssignmentDTO> assignmentsMap = new HashMap<>();
        pendingAssignments.forEach(a -> assignmentsMap.put(a.getId(), a));
        
        OfflineSyncRequestDTO syncRequest = OfflineSyncRequestDTO.builder()
                .courierId(courierId)
                .assignments(assignmentsMap)
                .deviceInfo(deviceId)
                .appVersion("1.0.0") // This would normally come from the client
                .build();
        
        // Sync assignments
        OfflineSyncResponseDTO assignmentResults = synchronizeAssignments(syncRequest);
        
        // Get pending tracking events
        Map<String, String> trackingResults = new HashMap<>();
        try {
            List<TrackingEventDTO> pendingEvents = trackingService.getTrackingEvents("all").stream()
                    .filter(e -> !e.isSyncStatus())
                    .collect(Collectors.toList());
            
            if (!pendingEvents.isEmpty()) {
                trackingResults = synchronizeTrackingEvents(courierId, pendingEvents);
            }
        } catch (Exception e) {
            log.error("Error synchronizing tracking events: {}", e.getMessage());
            trackingResults.put("error", "Failed to synchronize tracking events: " + e.getMessage());
        }
        
        // Store sync state
        String syncTimestamp = LocalDateTime.now().format(ISO_FORMATTER);
        String syncToken = UUID.randomUUID().toString();
        storeSyncState(courierId, syncToken, syncTimestamp);
        
        // Combine results
        assignmentResults.setMessage("Comprehensive synchronization completed. " +
                assignmentResults.getSyncResults().size() + " assignments and " +
                trackingResults.size() + " tracking events synchronized.");
        assignmentResults.setServerTimestamps(Map.of("syncTimestamp", syncTimestamp));
        assignmentResults.setSyncToken(syncToken);
        
        log.info("Completed comprehensive synchronization for courier: {}", courierId);
        return assignmentResults;
    }

    @Override
    public OfflineSyncResponseDTO synchronizeAssignments(OfflineSyncRequestDTO syncRequest) {
        log.info("Synchronizing assignments for courier: {}", syncRequest.getCourierId());
        
        OfflineSyncResponseDTO response = assignmentService.synchronizeOfflineData(syncRequest);
        
        // Mark successfully synced assignments
        response.getSyncResults().forEach((id, result) -> {
            if ("SUCCESS".equals(result.getResult())) {
                markAsSynchronized("assignment", id);
            }
        });
        
        // Get any new assignments from server
        String lastSyncTimestamp = getLastSyncState(syncRequest.getCourierId()).get("timestamp");
        List<AssignmentDTO> newAssignments = new ArrayList<>();
        
        if (lastSyncTimestamp != null) {
            newAssignments = fetchServerUpdates(syncRequest.getCourierId(), lastSyncTimestamp);
        }
        
        response.setNewAssignments(newAssignments);
        
        return response;
    }

    @Override
    public Map<String, String> synchronizeTrackingEvents(String courierId, List<TrackingEventDTO> events) {
        log.info("Synchronizing {} tracking events for courier: {}", events.size(), courierId);
        
        Map<String, String> results = new HashMap<>();
        
        for (TrackingEventDTO event : events) {
            try {
                trackingService.addTrackingEvent(event);
                results.put(event.getId(), "SUCCESS");
                markAsSynchronized("tracking_event", event.getId());
            } catch (Exception e) {
                log.error("Error synchronizing tracking event {}: {}", event.getId(), e.getMessage());
                results.put(event.getId(), "ERROR: " + e.getMessage());
            }
        }
        
        return results;
    }

    @Override
    public Map<String, String> synchronizeDeliveryConfirmations(String courierId, List<DeliveryConfirmationDTO> confirmations) {
        log.info("Synchronizing {} delivery confirmations for courier: {}", confirmations.size(), courierId);
        
        Map<String, String> results = new HashMap<>();
        
        for (DeliveryConfirmationDTO confirmation : confirmations) {
            try {
                trackingService.confirmDelivery(confirmation);
                results.put(confirmation.getPackageId(), "SUCCESS");
                markAsSynchronized("delivery_confirmation", confirmation.getPackageId());
            } catch (Exception e) {
                log.error("Error synchronizing delivery confirmation for package {}: {}", 
                        confirmation.getPackageId(), e.getMessage());
                results.put(confirmation.getPackageId(), "ERROR: " + e.getMessage());
            }
        }
        
        return results;
    }

    @Override
    public List<AssignmentDTO> fetchServerUpdates(String courierId, String lastSyncTimestamp) {
        log.info("Fetching updates from server for courier: {} since {}", courierId, lastSyncTimestamp);
        
        // In a real implementation, this would query the server for updates since the last sync
        // For now, we'll just return active assignments
        try {
            return assignmentService.getActiveAssignmentsByCourier(courierId);
        } catch (Exception e) {
            log.error("Error fetching server updates: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public Map<String, Object> resolveConflicts(Map<String, Object> localData, Map<String, Object> serverData, 
                                              String entityType, String entityId) {
        log.info("Resolving conflicts for {} with ID: {}", entityType, entityId);
        
        // Simple conflict resolution strategy - server wins
        // In a real implementation, this would be more sophisticated based on business rules
        return serverData;
    }

    @Override
    public boolean markAsSynchronized(String entityType, String entityId) {
        log.info("Marking {} with ID: {} as synchronized", entityType, entityId);
        
        try {
            String key = "sync:status:" + entityType + ":" + entityId;
            redisTemplate.opsForValue().set(key, "SYNCED", Duration.ofDays(7));
            
            // If this is an assignment, also update the assignment object
            if ("assignment".equals(entityType)) {
                AssignmentDTO assignment = assignmentService.refreshAssignment(entityId);
                if (assignment != null && assignment.getId() != null) {
                    assignment.setSyncStatus("SYNCED");
                    // In a real impl, we would update the assignment in the database
                }
            }
            
            return true;
        } catch (Exception e) {
            log.error("Error marking entity as synchronized: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean storeSyncState(String courierId, String syncToken, String timestamp) {
        log.info("Storing sync state for courier: {}, token: {}, timestamp: {}", courierId, syncToken, timestamp);
        
        try {
            String key = "sync:state:" + courierId;
            Map<String, String> syncState = new HashMap<>();
            syncState.put("token", syncToken);
            syncState.put("timestamp", timestamp);
            
            redisTemplate.opsForValue().set(key, syncState, Duration.ofDays(30));
            
            // Update in-memory cache
            syncStateCache.put(courierId, syncState);
            
            return true;
        } catch (Exception e) {
            log.error("Error storing sync state: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public Map<String, String> getLastSyncState(String courierId) {
        log.debug("Getting last sync state for courier: {}", courierId);
        
        // Check in-memory cache first
        if (syncStateCache.containsKey(courierId)) {
            return syncStateCache.get(courierId);
        }
        
        // Try to get from Redis
        try {
            String key = "sync:state:" + courierId;
            Object value = redisTemplate.opsForValue().get(key);
            
            if (value instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, String> syncState = (Map<String, String>) value;
                
                // Update in-memory cache
                syncStateCache.put(courierId, syncState);
                
                return syncState;
            }
        } catch (Exception e) {
            log.error("Error getting sync state: {}", e.getMessage());
        }
        
        // Return default empty state
        return Map.of(
            "token", "",
            "timestamp", LocalDateTime.now().minusDays(30).format(ISO_FORMATTER)
        );
    }
}
