package com.exalt.courier.drivermobileapp.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.socialecommerceecosystem.drivermobileapp.client.CourierManagementClient;
import com.socialecommerceecosystem.drivermobileapp.dto.assignment.*;
import com.socialecommerceecosystem.drivermobileapp.exception.ResourceNotFoundException;
import com.socialecommerceecosystem.drivermobileapp.service.AssignmentService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Implementation of the AssignmentService interface
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AssignmentServiceImpl implements AssignmentService {
    
    private final CourierManagementClient courierManagementClient;
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    
    // Local cache for brief storage of assignments during service disruptions
    private final Map<String, AssignmentDTO> localCache = new ConcurrentHashMap<>();
    
    @Override
    @CircuitBreaker(name = "courierManagementService", fallbackMethod = "getAssignmentsByCourierFallback")
    public List<AssignmentDTO> getAssignmentsByCourier(String courierId) {
        log.info("Getting assignments for courier: {}", courierId);
        
        ResponseEntity<List<Map<String, Object>>> response = courierManagementClient.getAssignmentsByCourier(courierId);
        
        if (response.getBody() == null) {
            return Collections.emptyList();
        }
        
        List<AssignmentDTO> assignments = response.getBody().stream()
                .map(this::convertToAssignmentDTO)
                .collect(Collectors.toList());
        
        // Cache assignments for offline use
        assignments.forEach(assignment -> localCache.put(assignment.getId(), assignment));
        
        return assignments;
    }
    
    @Override
    @CircuitBreaker(name = "courierManagementService", fallbackMethod = "getActiveAssignmentsByCourierFallback")
    public List<AssignmentDTO> getActiveAssignmentsByCourier(String courierId) {
        log.info("Getting active assignments for courier: {}", courierId);
        
        ResponseEntity<List<Map<String, Object>>> response = courierManagementClient.getActiveAssignmentsByCourier(courierId);
        
        if (response.getBody() == null) {
            return Collections.emptyList();
        }
        
        List<AssignmentDTO> assignments = response.getBody().stream()
                .map(this::convertToAssignmentDTO)
                .collect(Collectors.toList());
        
        // Cache active assignments for offline use
        assignments.forEach(assignment -> {
            localCache.put(assignment.getId(), assignment);
            cacheAssignmentInRedis(assignment);
        });
        
        return assignments;
    }
    
    @Override
    @CircuitBreaker(name = "courierManagementService", fallbackMethod = "acceptAssignmentFallback")
    public AssignmentDTO acceptAssignment(String assignmentId) {
        log.info("Accepting assignment: {}", assignmentId);
        
        ResponseEntity<Map<String, Object>> response = courierManagementClient.acceptAssignment(assignmentId);
        
        if (response.getBody() == null) {
            throw new ResourceNotFoundException("Assignment not found with id: " + assignmentId);
        }
        
        AssignmentDTO assignment = convertToAssignmentDTO(response.getBody());
        localCache.put(assignment.getId(), assignment);
        cacheAssignmentInRedis(assignment);
        
        return assignment;
    }
    
    @Override
    @CircuitBreaker(name = "courierManagementService", fallbackMethod = "startAssignmentFallback")
    public AssignmentDTO startAssignment(String assignmentId) {
        log.info("Starting assignment: {}", assignmentId);
        
        ResponseEntity<Map<String, Object>> response = courierManagementClient.startAssignment(assignmentId);
        
        if (response.getBody() == null) {
            throw new ResourceNotFoundException("Assignment not found with id: " + assignmentId);
        }
        
        AssignmentDTO assignment = convertToAssignmentDTO(response.getBody());
        localCache.put(assignment.getId(), assignment);
        cacheAssignmentInRedis(assignment);
        
        return assignment;
    }
    
    @Override
    @CircuitBreaker(name = "courierManagementService", fallbackMethod = "completeAssignmentFallback")
    public AssignmentDTO completeAssignment(String assignmentId) {
        log.info("Completing assignment: {}", assignmentId);
        
        ResponseEntity<Map<String, Object>> response = courierManagementClient.completeAssignment(assignmentId);
        
        if (response.getBody() == null) {
            throw new ResourceNotFoundException("Assignment not found with id: " + assignmentId);
        }
        
        AssignmentDTO assignment = convertToAssignmentDTO(response.getBody());
        localCache.put(assignment.getId(), assignment);
        cacheAssignmentInRedis(assignment);
        
        return assignment;
    }
    
    @Override
    @CircuitBreaker(name = "courierManagementService", fallbackMethod = "cancelAssignmentFallback")
    public AssignmentDTO cancelAssignment(String assignmentId, String cancellationReason) {
        log.info("Cancelling assignment: {} with reason: {}", assignmentId, cancellationReason);
        
        Map<String, String> cancellationRequest = new HashMap<>();
        cancellationRequest.put("reason", cancellationReason);
        
        ResponseEntity<Map<String, Object>> response = courierManagementClient.cancelAssignment(assignmentId, cancellationRequest);
        
        if (response.getBody() == null) {
            throw new ResourceNotFoundException("Assignment not found with id: " + assignmentId);
        }
        
        AssignmentDTO assignment = convertToAssignmentDTO(response.getBody());
        localCache.put(assignment.getId(), assignment);
        cacheAssignmentInRedis(assignment);
        
        return assignment;
    }
    
    @Override
    public AssignmentTaskDTO updateTaskStatus(String taskId, String status) {
        // Implementation would call the courier management service
        log.info("Updating task status not implemented yet");
        return new AssignmentTaskDTO(); // Placeholder implementation
    }
    
    @Override
    @CircuitBreaker(name = "courierManagementService", fallbackMethod = "optimizeRoutesFallback")
    public List<AssignmentDTO> optimizeRoutes(String courierId) {
        log.info("Optimizing routes for courier: {}", courierId);
        
        ResponseEntity<List<Map<String, Object>>> response = courierManagementClient.optimizeRoutes(courierId);
        
        if (response.getBody() == null) {
            return Collections.emptyList();
        }
        
        List<AssignmentDTO> assignments = response.getBody().stream()
                .map(this::convertToAssignmentDTO)
                .collect(Collectors.toList());
        
        // Update cached assignments with optimized routes
        assignments.forEach(assignment -> {
            localCache.put(assignment.getId(), assignment);
            cacheAssignmentInRedis(assignment);
        });
        
        return assignments;
    }
    
    @Override
    public OfflineSyncResponseDTO synchronizeOfflineData(OfflineSyncRequestDTO syncRequest) {
        log.info("Synchronizing offline data for courier: {}", syncRequest.getCourierId());
        
        // This would implement the logic to sync offline data with the server
        // For now, we'll just implement a simple mock version that processes the data
        
        Map<String, SyncResultDTO> results = new HashMap<>();
        
        // Process each assignment sent by the client
        syncRequest.getAssignments().forEach((assignmentId, assignmentDTO) -> {
            log.info("Processing offline assignment: {}", assignmentId);
            
            try {
                // In a real implementation, we would validate and update the assignment in the backend
                // For now, we'll just simulate a successful sync
                SyncResultDTO result = SyncResultDTO.builder()
                        .assignmentId(assignmentId)
                        .result("SUCCESS")
                        .message("Assignment synchronized successfully")
                        .serverAssignment(assignmentDTO)
                        .build();
                
                results.put(assignmentId, result);
                
                // Update local cache with the synced data
                localCache.put(assignmentId, assignmentDTO);
                cacheAssignmentInRedis(assignmentDTO);
            } catch (Exception e) {
                log.error("Error synchronizing assignment: {}", assignmentId, e);
                SyncResultDTO result = SyncResultDTO.builder()
                        .assignmentId(assignmentId)
                        .result("ERROR")
                        .message("Error: " + e.getMessage())
                        .build();
                results.put(assignmentId, result);
            }
        });
        
        // Create response with server timestamps
        Map<String, String> serverTimestamps = new HashMap<>();
        serverTimestamps.put("syncTimestamp", String.valueOf(System.currentTimeMillis()));
        
        return OfflineSyncResponseDTO.builder()
                .courierId(syncRequest.getCourierId())
                .syncResults(results)
                .serverTimestamps(serverTimestamps)
                .syncToken(UUID.randomUUID().toString())
                .message("Synchronization completed successfully")
                .build();
    }
    
    @Override
    public List<AssignmentTaskDTO> getTasksForAssignment(String assignmentId) {
        // Retrieve from local cache or fetch from service
        AssignmentDTO assignment = localCache.get(assignmentId);
        if (assignment != null) {
            return assignment.getTasks();
        }
        
        // This would normally fetch from the service, for now just return empty list
        log.info("No cached tasks found for assignment: {}", assignmentId);
        return Collections.emptyList();
    }
    
    @Override
    public AssignmentTaskDTO getTask(String taskId) {
        // This would normally fetch from the service, for now just return empty task
        log.info("Getting task not fully implemented yet: {}", taskId);
        return new AssignmentTaskDTO();
    }
    
    @Override
    public AssignmentTaskDTO completeTask(String taskId, Map<String, Object> completionData) {
        // This would normally call the service to complete the task
        log.info("Completing task not fully implemented yet: {}", taskId);
        return new AssignmentTaskDTO();
    }
    
    @Override
    public AssignmentDTO refreshAssignment(String assignmentId) {
        // This would fetch the latest assignment data from the server
        log.info("Refreshing assignment not fully implemented yet: {}", assignmentId);
        return localCache.getOrDefault(assignmentId, new AssignmentDTO());
    }
    
    @Override
    public boolean markAssignmentAsCached(String assignmentId) {
        AssignmentDTO assignment = localCache.get(assignmentId);
        if (assignment != null) {
            cacheAssignmentInRedis(assignment);
            return true;
        }
        return false;
    }
    
    @Override
    public List<AssignmentDTO> getCachedAssignments(String courierId) {
        // Get all assignments for this courier from Redis
        String cacheKeyPattern = "assignment:" + courierId + ":*";
        Set<String> keys = redisTemplate.keys(cacheKeyPattern);
        
        if (keys == null || keys.isEmpty()) {
            // Fallback to local cache
            return localCache.values().stream()
                    .filter(a -> courierId.equals(a.getCourierId()))
                    .collect(Collectors.toList());
        }
        
        List<AssignmentDTO> cachedAssignments = new ArrayList<>();
        keys.forEach(key -> {
            Object obj = redisTemplate.opsForValue().get(key);
            if (obj instanceof AssignmentDTO) {
                cachedAssignments.add((AssignmentDTO) obj);
            }
        });
        
        return cachedAssignments;
    }
    
    /**
     * Caches an assignment in Redis for offline use.
     *
     * @param assignment the assignment to cache
     */
    private void cacheAssignmentInRedis(AssignmentDTO assignment) {
        try {
            String key = "assignment:" + assignment.getCourierId() + ":" + assignment.getId();
            redisTemplate.opsForValue().set(key, assignment, Duration.ofHours(24));
        } catch (Exception e) {
            log.error("Error caching assignment in Redis: {}", assignment.getId(), e);
        }
    }
    
    /**
     * Converts a map of assignment data to an AssignmentDTO.
     *
     * @param map the map containing assignment data
     * @return the converted AssignmentDTO
     */
    @SuppressWarnings("unchecked")
    private AssignmentDTO convertToAssignmentDTO(Map<String, Object> map) {
        try {
            return objectMapper.convertValue(map, AssignmentDTO.class);
        } catch (Exception e) {
            log.error("Error converting assignment data: {}", e.getMessage());
            
            // Fallback manual conversion
            AssignmentDTO dto = new AssignmentDTO();
            dto.setId(String.valueOf(map.get("id")));
            dto.setCourierId(String.valueOf(map.get("courierId")));
            dto.setStatus(String.valueOf(map.get("status")));
            
            // Try to convert tasks if present
            if (map.containsKey("tasks") && map.get("tasks") instanceof List) {
                List<Map<String, Object>> taskMaps = (List<Map<String, Object>>) map.get("tasks");
                List<AssignmentTaskDTO> tasks = taskMaps.stream()
                        .map(taskMap -> {
                            try {
                                return objectMapper.convertValue(taskMap, AssignmentTaskDTO.class);
                            } catch (Exception ex) {
                                log.error("Error converting task data: {}", ex.getMessage());
                                return new AssignmentTaskDTO();
                            }
                        })
                        .collect(Collectors.toList());
                dto.setTasks(tasks);
            } else {
                dto.setTasks(Collections.emptyList());
            }
            
            return dto;
        }
    }
    
    // Fallback methods for circuit breaker
    
    public List<AssignmentDTO> getAssignmentsByCourierFallback(String courierId, Exception e) {
        log.warn("Fallback: Getting assignments from cache for courier: {}, reason: {}", courierId, e.getMessage());
        return getCachedAssignments(courierId);
    }
    
    public List<AssignmentDTO> getActiveAssignmentsByCourierFallback(String courierId, Exception e) {
        log.warn("Fallback: Getting active assignments from cache for courier: {}, reason: {}", courierId, e.getMessage());
        return getCachedAssignments(courierId).stream()
                .filter(a -> "ACTIVE".equals(a.getStatus()) || "ASSIGNED".equals(a.getStatus()) || "IN_PROGRESS".equals(a.getStatus()))
                .collect(Collectors.toList());
    }
    
    public AssignmentDTO acceptAssignmentFallback(String assignmentId, Exception e) {
        log.warn("Fallback: Accepting assignment locally: {}, reason: {}", assignmentId, e.getMessage());
        
        AssignmentDTO assignment = localCache.get(assignmentId);
        if (assignment == null) {
            throw new ResourceNotFoundException("Assignment not found in cache with id: " + assignmentId);
        }
        
        assignment.setStatus("ACCEPTED");
        assignment.setSyncStatus("PENDING_SYNC");
        localCache.put(assignmentId, assignment);
        
        return assignment;
    }
    
    public AssignmentDTO startAssignmentFallback(String assignmentId, Exception e) {
        log.warn("Fallback: Starting assignment locally: {}, reason: {}", assignmentId, e.getMessage());
        
        AssignmentDTO assignment = localCache.get(assignmentId);
        if (assignment == null) {
            throw new ResourceNotFoundException("Assignment not found in cache with id: " + assignmentId);
        }
        
        assignment.setStatus("IN_PROGRESS");
        assignment.setSyncStatus("PENDING_SYNC");
        localCache.put(assignmentId, assignment);
        
        return assignment;
    }
    
    public AssignmentDTO completeAssignmentFallback(String assignmentId, Exception e) {
        log.warn("Fallback: Completing assignment locally: {}, reason: {}", assignmentId, e.getMessage());
        
        AssignmentDTO assignment = localCache.get(assignmentId);
        if (assignment == null) {
            throw new ResourceNotFoundException("Assignment not found in cache with id: " + assignmentId);
        }
        
        assignment.setStatus("COMPLETED");
        assignment.setSyncStatus("PENDING_SYNC");
        localCache.put(assignmentId, assignment);
        
        return assignment;
    }
    
    public AssignmentDTO cancelAssignmentFallback(String assignmentId, String cancellationReason, Exception e) {
        log.warn("Fallback: Cancelling assignment locally: {}, reason: {}", assignmentId, e.getMessage());
        
        AssignmentDTO assignment = localCache.get(assignmentId);
        if (assignment == null) {
            throw new ResourceNotFoundException("Assignment not found in cache with id: " + assignmentId);
        }
        
        assignment.setStatus("CANCELLED");
        assignment.setCancellationReason(cancellationReason);
        assignment.setSyncStatus("PENDING_SYNC");
        localCache.put(assignmentId, assignment);
        
        return assignment;
    }
    
    public List<AssignmentDTO> optimizeRoutesFallback(String courierId, Exception e) {
        log.warn("Fallback: Cannot optimize routes, service unavailable: {}", e.getMessage());
        return Collections.emptyList();
    }
}
