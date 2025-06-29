package com.exalt.courier.drivermobileapp.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.socialecommerceecosystem.drivermobileapp.client.tracking.TrackingServiceClient;
import com.socialecommerceecosystem.drivermobileapp.dto.tracking.DeliveryConfirmationDTO;
import com.socialecommerceecosystem.drivermobileapp.dto.tracking.TrackingEventDTO;
import com.socialecommerceecosystem.drivermobileapp.dto.tracking.TrackingInfoDTO;
import com.socialecommerceecosystem.drivermobileapp.exception.ResourceNotFoundException;
import com.socialecommerceecosystem.drivermobileapp.service.TrackingService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Implementation of the TrackingService interface.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TrackingServiceImpl implements TrackingService {
    
    private final TrackingServiceClient trackingServiceClient;
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    
    // Local cache for offline operations
    private final Map<String, TrackingInfoDTO> packageCache = new ConcurrentHashMap<>();
    private final Map<String, List<TrackingEventDTO>> offlineEvents = new ConcurrentHashMap<>();
    private final Map<String, DeliveryConfirmationDTO> offlineConfirmations = new ConcurrentHashMap<>();
    
    @Override
    @CircuitBreaker(name = "trackingService", fallbackMethod = "getTrackingInfoFallback")
    public TrackingInfoDTO getTrackingInfo(String trackingNumber) {
        log.info("Getting tracking info for tracking number: {}", trackingNumber);
        
        // Check if this is a package ID request (with "pkg:" prefix)
        if (trackingNumber.startsWith("pkg:")) {
            String packageId = trackingNumber.substring(4);
            TrackingInfoDTO cachedInfo = packageCache.get(packageId);
            if (cachedInfo != null) {
                return cachedInfo;
            }
            throw new ResourceNotFoundException("Package not found with id: " + packageId);
        }
        
        ResponseEntity<Map<String, Object>> response = trackingServiceClient.getTrackingInfo(trackingNumber);
        
        if (response.getBody() == null) {
            throw new ResourceNotFoundException("Tracking information not found for number: " + trackingNumber);
        }
        
        TrackingInfoDTO trackingInfo = convertToTrackingInfoDTO(response.getBody());
        
        // Cache tracking info
        if (trackingInfo.getPackageId() != null) {
            packageCache.put(trackingInfo.getPackageId(), trackingInfo);
            cachePackageInRedis(trackingInfo);
        }
        
        return trackingInfo;
    }
    
    @Override
    @CircuitBreaker(name = "trackingService", fallbackMethod = "updatePackageStatusFallback")
    public TrackingInfoDTO updatePackageStatus(String packageId, String status, String location, String description) {
        log.info("Updating package status for package ID: {} to status: {}", packageId, status);
        
        Map<String, Object> statusUpdate = new HashMap<>();
        statusUpdate.put("status", status);
        statusUpdate.put("location", location);
        statusUpdate.put("description", description);
        statusUpdate.put("timestamp", LocalDateTime.now().toString());
        
        ResponseEntity<Map<String, Object>> response = trackingServiceClient.updatePackageStatus(packageId, statusUpdate);
        
        if (response.getBody() == null) {
            throw new ResourceNotFoundException("Package not found with id: " + packageId);
        }
        
        TrackingInfoDTO trackingInfo = convertToTrackingInfoDTO(response.getBody());
        
        // Update cache
        packageCache.put(packageId, trackingInfo);
        cachePackageInRedis(trackingInfo);
        
        return trackingInfo;
    }
    
    @Override
    @CircuitBreaker(name = "trackingService", fallbackMethod = "confirmDeliveryFallback")
    public TrackingInfoDTO confirmDelivery(DeliveryConfirmationDTO confirmation) {
        log.info("Confirming delivery for package ID: {}", confirmation.getPackageId());
        
        Map<String, Object> confirmationData = objectMapper.convertValue(confirmation, Map.class);
        
        ResponseEntity<Map<String, Object>> response = trackingServiceClient.confirmDelivery(
                confirmation.getPackageId(), confirmationData);
        
        if (response.getBody() == null) {
            throw new ResourceNotFoundException("Package not found with id: " + confirmation.getPackageId());
        }
        
        TrackingInfoDTO trackingInfo = convertToTrackingInfoDTO(response.getBody());
        
        // Update cache
        packageCache.put(confirmation.getPackageId(), trackingInfo);
        cachePackageInRedis(trackingInfo);
        
        // Remove from offline confirmations if present
        offlineConfirmations.remove(confirmation.getPackageId());
        
        return trackingInfo;
    }
    
    @Override
    @CircuitBreaker(name = "trackingService", fallbackMethod = "addTrackingEventFallback")
    public TrackingInfoDTO addTrackingEvent(TrackingEventDTO event) {
        log.info("Adding tracking event for package ID: {}", event.getPackageId());
        
        Map<String, Object> eventData = objectMapper.convertValue(event, Map.class);
        
        ResponseEntity<Map<String, Object>> response = trackingServiceClient.addTrackingEvent(
                event.getPackageId(), eventData);
        
        if (response.getBody() == null) {
            throw new ResourceNotFoundException("Package not found with id: " + event.getPackageId());
        }
        
        TrackingInfoDTO trackingInfo = convertToTrackingInfoDTO(response.getBody());
        
        // Update cache
        packageCache.put(event.getPackageId(), trackingInfo);
        cachePackageInRedis(trackingInfo);
        
        // Remove from offline events if present
        offlineEvents.computeIfPresent(event.getPackageId(), (key, events) -> {
            events.removeIf(e -> Objects.equals(e.getId(), event.getId()));
            return events.isEmpty() ? null : events;
        });
        
        return trackingInfo;
    }
    
    @Override
    public List<TrackingEventDTO> getTrackingEvents(String packageId) {
        // Special case for getting all offline events
        if ("all".equalsIgnoreCase(packageId)) {
            log.info("Getting all offline tracking events");
            List<TrackingEventDTO> allEvents = new ArrayList<>();
            
            for (List<TrackingEventDTO> events : offlineEvents.values()) {
                allEvents.addAll(events);
            }
            
            return allEvents;
        }
        
        TrackingInfoDTO trackingInfo = packageCache.get(packageId);
        
        if (trackingInfo != null && trackingInfo.getEvents() != null) {
            // Add any offline events
            List<TrackingEventDTO> events = new ArrayList<>(trackingInfo.getEvents());
            
            if (offlineEvents.containsKey(packageId)) {
                events.addAll(offlineEvents.get(packageId));
                
                // Sort by event time
                events.sort(Comparator.comparing(TrackingEventDTO::getEventTime).reversed());
            }
            
            return events;
        }
        
        // If not in cache, try to fetch from the server
        try {
            TrackingInfoDTO info = getTrackingInfo("pkg:" + packageId);
            return info.getEvents() != null ? info.getEvents() : Collections.emptyList();
        } catch (Exception e) {
            log.warn("Could not fetch tracking events from server: {}", e.getMessage());
            
            // Return only offline events if available
            return offlineEvents.getOrDefault(packageId, Collections.emptyList());
        }
    }
    
    @Override
    public boolean storeOfflineTrackingEvent(TrackingEventDTO event) {
        log.info("Storing offline tracking event for package ID: {}", event.getPackageId());
        
        // Ensure event has a valid ID
        if (event.getId() == null || event.getId().isEmpty()) {
            event.setId(UUID.randomUUID().toString());
        }
        
        // Set as not synced
        event.setSyncStatus(false);
        
        // Add to offline events
        offlineEvents.computeIfAbsent(event.getPackageId(), k -> new ArrayList<>()).add(event);
        
        // Cache the event in Redis for persistence
        String key = "offline:tracking_event:" + event.getPackageId() + ":" + event.getId();
        redisTemplate.opsForValue().set(key, event, Duration.ofDays(30));
        
        // Also update the package cache if available
        TrackingInfoDTO trackingInfo = packageCache.get(event.getPackageId());
        if (trackingInfo != null) {
            if (trackingInfo.getEvents() == null) {
                trackingInfo.setEvents(new ArrayList<>());
            }
            trackingInfo.getEvents().add(event);
            
            // Update status if appropriate
            if ("STATUS_UPDATE".equals(event.getEventType())) {
                trackingInfo.setStatus(event.getStatus());
            }
            
            packageCache.put(event.getPackageId(), trackingInfo);
            cachePackageInRedis(trackingInfo);
        }
        
        return true;
    }
    
    @Override
    public boolean storeOfflineDeliveryConfirmation(DeliveryConfirmationDTO confirmation) {
        log.info("Storing offline delivery confirmation for package ID: {}", confirmation.getPackageId());
        
        // Store in offline confirmations
        offlineConfirmations.put(confirmation.getPackageId(), confirmation);
        
        // Cache in Redis for persistence
        String key = "offline:delivery_confirmation:" + confirmation.getPackageId();
        redisTemplate.opsForValue().set(key, confirmation, Duration.ofDays(30));
        
        // Update package cache if available
        TrackingInfoDTO trackingInfo = packageCache.get(confirmation.getPackageId());
        if (trackingInfo != null) {
            trackingInfo.setDelivered(true);
            trackingInfo.setStatus("DELIVERED");
            trackingInfo.setActualDeliveryTime(LocalDateTime.now());
            trackingInfo.setDeliveryNotes(confirmation.getNotes());
            
            // Create a delivery event
            TrackingEventDTO deliveryEvent = TrackingEventDTO.builder()
                    .id(UUID.randomUUID().toString())
                    .packageId(confirmation.getPackageId())
                    .eventType("DELIVERY")
                    .status("DELIVERED")
                    .eventTime(LocalDateTime.now())
                    .location("Delivery Address")
                    .latitude(confirmation.getLatitude())
                    .longitude(confirmation.getLongitude())
                    .description("Package delivered" + (confirmation.getReceivedBy() != null 
                            ? " to " + confirmation.getReceivedBy() 
                            : confirmation.isLeftAtDoor() ? " (left at door)" : ""))
                    .driverId(confirmation.getDriverId())
                    .syncStatus(false)
                    .build();
            
            // Store the delivery event
            storeOfflineTrackingEvent(deliveryEvent);
            
            packageCache.put(confirmation.getPackageId(), trackingInfo);
            cachePackageInRedis(trackingInfo);
        }
        
        return true;
    }
    
    @Override
    public Map<String, String> synchronizeOfflineTrackingData(String courierId) {
        log.info("Synchronizing offline tracking data for courier: {}", courierId);
        
        Map<String, String> results = new HashMap<>();
        
        // Get all offline events for this courier
        List<TrackingEventDTO> pendingEvents = getTrackingEvents("all").stream()
                .filter(e -> courierId.equals(e.getDriverId()) && !e.isSyncStatus())
                .collect(Collectors.toList());
        
        // Sync tracking events
        for (TrackingEventDTO event : pendingEvents) {
            try {
                addTrackingEvent(event);
                results.put("event:" + event.getId(), "SUCCESS");
                
                // Remove from offline storage
                String key = "offline:tracking_event:" + event.getPackageId() + ":" + event.getId();
                redisTemplate.delete(key);
            } catch (Exception e) {
                log.error("Error synchronizing tracking event {}: {}", event.getId(), e.getMessage());
                results.put("event:" + event.getId(), "ERROR: " + e.getMessage());
            }
        }
        
        // Get all offline delivery confirmations for this courier
        List<DeliveryConfirmationDTO> pendingConfirmations = new ArrayList<>();
        for (DeliveryConfirmationDTO confirmation : offlineConfirmations.values()) {
            if (courierId.equals(confirmation.getDriverId())) {
                pendingConfirmations.add(confirmation);
            }
        }
        
        // Sync delivery confirmations
        for (DeliveryConfirmationDTO confirmation : pendingConfirmations) {
            try {
                confirmDelivery(confirmation);
                results.put("confirmation:" + confirmation.getPackageId(), "SUCCESS");
                
                // Remove from offline storage
                String key = "offline:delivery_confirmation:" + confirmation.getPackageId();
                redisTemplate.delete(key);
            } catch (Exception e) {
                log.error("Error synchronizing delivery confirmation for package {}: {}", 
                        confirmation.getPackageId(), e.getMessage());
                results.put("confirmation:" + confirmation.getPackageId(), "ERROR: " + e.getMessage());
            }
        }
        
        return results;
    }
    
    @Override
    public List<TrackingInfoDTO> getPackagesByCourier(String courierId) {
        log.info("Getting packages for courier: {}", courierId);
        
        // In a real implementation, we would fetch from the server
        // For now, return from cache
        return packageCache.values().stream()
                .filter(p -> courierId.equals(p.getCourierId()))
                .collect(Collectors.toList());
    }
    
    /**
     * Caches a package in Redis for offline use.
     *
     * @param trackingInfo the tracking info to cache
     */
    private void cachePackageInRedis(TrackingInfoDTO trackingInfo) {
        try {
            String key = "package:" + trackingInfo.getPackageId();
            redisTemplate.opsForValue().set(key, trackingInfo, Duration.ofHours(24));
        } catch (Exception e) {
            log.error("Error caching package in Redis: {}", trackingInfo.getPackageId(), e);
        }
    }
    
    /**
     * Converts a map of tracking data to a TrackingInfoDTO.
     *
     * @param map the map containing tracking data
     * @return the converted TrackingInfoDTO
     */
    @SuppressWarnings("unchecked")
    private TrackingInfoDTO convertToTrackingInfoDTO(Map<String, Object> map) {
        try {
            return objectMapper.convertValue(map, TrackingInfoDTO.class);
        } catch (Exception e) {
            log.error("Error converting tracking data: {}", e.getMessage());
            
            // Fallback manual conversion
            TrackingInfoDTO dto = new TrackingInfoDTO();
            dto.setPackageId(String.valueOf(map.get("packageId")));
            dto.setTrackingNumber(String.valueOf(map.get("trackingNumber")));
            dto.setStatus(String.valueOf(map.get("status")));
            
            // Try to convert events if present
            if (map.containsKey("events") && map.get("events") instanceof List) {
                List<Map<String, Object>> eventMaps = (List<Map<String, Object>>) map.get("events");
                List<TrackingEventDTO> events = eventMaps.stream()
                        .map(eventMap -> {
                            try {
                                return objectMapper.convertValue(eventMap, TrackingEventDTO.class);
                            } catch (Exception ex) {
                                log.error("Error converting event data: {}", ex.getMessage());
                                return new TrackingEventDTO();
                            }
                        })
                        .collect(Collectors.toList());
                dto.setEvents(events);
            } else {
                dto.setEvents(Collections.emptyList());
            }
            
            return dto;
        }
    }
    
    // Fallback methods for circuit breaker
    
    public TrackingInfoDTO getTrackingInfoFallback(String trackingNumber, Exception e) {
        log.warn("Fallback: Could not fetch tracking info from server: {}", e.getMessage());
        
        // If this is a packageId request
        if (trackingNumber.startsWith("pkg:")) {
            String packageId = trackingNumber.substring(4);
            return packageCache.getOrDefault(packageId, new TrackingInfoDTO());
        }
        
        // Try to find in cache by tracking number
        return packageCache.values().stream()
                .filter(p -> trackingNumber.equals(p.getTrackingNumber()))
                .findFirst()
                .orElse(new TrackingInfoDTO());
    }
    
    public TrackingInfoDTO updatePackageStatusFallback(String packageId, String status, String location, String description, Exception e) {
        log.warn("Fallback: Could not update package status on server: {}", e.getMessage());
        
        // Create an offline event instead
        TrackingEventDTO event = TrackingEventDTO.builder()
                .id(UUID.randomUUID().toString())
                .packageId(packageId)
                .eventType("STATUS_UPDATE")
                .status(status)
                .eventTime(LocalDateTime.now())
                .location(location)
                .description(description)
                .syncStatus(false)
                .build();
        
        storeOfflineTrackingEvent(event);
        
        return packageCache.getOrDefault(packageId, new TrackingInfoDTO());
    }
    
    public TrackingInfoDTO confirmDeliveryFallback(DeliveryConfirmationDTO confirmation, Exception e) {
        log.warn("Fallback: Could not confirm delivery on server: {}", e.getMessage());
        
        // Store offline instead
        storeOfflineDeliveryConfirmation(confirmation);
        
        return packageCache.getOrDefault(confirmation.getPackageId(), new TrackingInfoDTO());
    }
    
    public TrackingInfoDTO addTrackingEventFallback(TrackingEventDTO event, Exception e) {
        log.warn("Fallback: Could not add tracking event on server: {}", e.getMessage());
        
        // Store offline instead
        storeOfflineTrackingEvent(event);
        
        return packageCache.getOrDefault(event.getPackageId(), new TrackingInfoDTO());
    }
}
