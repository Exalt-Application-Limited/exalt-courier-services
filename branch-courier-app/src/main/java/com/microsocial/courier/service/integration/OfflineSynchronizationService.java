package com.gogidix.courier.courier.service.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsocial.courier.annotation.Traced;
import com.microsocial.courier.model.dto.AssignmentDTO;
import com.microsocial.courier.model.dto.AssignmentTaskDTO;
import com.microsocial.courier.model.dto.CourierDTO;
import com.microsocial.courier.service.TracingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Service for handling offline synchronization with the Courier Management Service
 */
@Slf4j
@Service
public class OfflineSynchronizationService {

    private final CourierManagementIntegrationService integrationService;
    private final TracingService tracingService;
    private final ObjectMapper objectMapper;
    
    private final ConcurrentLinkedQueue<OfflineOperation> pendingOperations = new ConcurrentLinkedQueue<>();
    private final ConcurrentHashMap<String, Integer> failedOperations = new ConcurrentHashMap<>();
    
    @Value("${branch-courier-app.offline-mode.enabled:true}")
    private boolean offlineModeEnabled;
    
    @Value("${branch-courier-app.offline-mode.sync-attempts:3}")
    private int maxSyncAttempts;
    
    @Value("${branch-courier-app.offline-mode.data-dir:./offline-data}")
    private String offlineDataDir;

    @Autowired
    public OfflineSynchronizationService(CourierManagementIntegrationService integrationService, 
                                         TracingService tracingService,
                                         ObjectMapper objectMapper) {
        this.integrationService = integrationService;
        this.tracingService = tracingService;
        this.objectMapper = objectMapper;
    }
    
    @PostConstruct
    public void init() {
        if (offlineModeEnabled) {
            createDataDirectories();
            loadPendingOperations();
        }
    }
    
    /**
     * Creates necessary directories for offline data storage
     */
    private void createDataDirectories() {
        try {
            Path dataDir = Paths.get(offlineDataDir);
            if (!Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
                log.info("Created offline data directory: {}", dataDir);
            }
            
            // Create subdirectories for different operation types
            createSubDirectory("assignments");
            createSubDirectory("tasks");
            createSubDirectory("couriers");
            createSubDirectory("pending-operations");
        } catch (Exception e) {
            log.error("Error creating offline data directories", e);
        }
    }
    
    private void createSubDirectory(String name) throws IOException {
        Path subDir = Paths.get(offlineDataDir, name);
        if (!Files.exists(subDir)) {
            Files.createDirectories(subDir);
            log.info("Created offline data subdirectory: {}", subDir);
        }
    }
    
    /**
     * Load any pending operations from disk that haven't been synchronized
     */
    private void loadPendingOperations() {
        Path pendingDir = Paths.get(offlineDataDir, "pending-operations");
        if (!Files.exists(pendingDir)) {
            return;
        }
        
        try {
            Files.list(pendingDir)
                .filter(path -> path.toString().endsWith(".json"))
                .forEach(path -> {
                    try {
                        OfflineOperation operation = objectMapper.readValue(path.toFile(), OfflineOperation.class);
                        pendingOperations.add(operation);
                        log.info("Loaded pending operation: {}", operation.getOperationId());
                    } catch (Exception e) {
                        log.error("Error loading pending operation from file: {}", path, e);
                    }
                });
            
            log.info("Loaded {} pending operations", pendingOperations.size());
        } catch (Exception e) {
            log.error("Error loading pending operations", e);
        }
    }
    
    /**
     * Add an assignment update operation to the queue
     */
    @Traced("OfflineSynchronization.queueAssignmentUpdate")
    public void queueAssignmentUpdate(Long id, AssignmentDTO assignmentDTO) {
        if (!offlineModeEnabled) {
            return;
        }
        
        tracingService.addTag("assignmentId", String.valueOf(id));
        log.debug("Queueing assignment update for offline synchronization. ID: {}", id);
        
        OfflineOperation operation = new OfflineOperation(
                "assignment-update-" + id + "-" + System.currentTimeMillis(),
                OperationType.UPDATE_ASSIGNMENT,
                new Object[]{id, assignmentDTO}
        );
        
        pendingOperations.add(operation);
        saveOperationToDisk(operation);
    }
    
    /**
     * Add an assignment status update operation to the queue
     */
    @Traced("OfflineSynchronization.queueAssignmentStatusUpdate")
    public void queueAssignmentStatusUpdate(Long id, String status) {
        if (!offlineModeEnabled) {
            return;
        }
        
        tracingService.addTag("assignmentId", String.valueOf(id));
        tracingService.addTag("status", status);
        log.debug("Queueing assignment status update for offline synchronization. ID: {}, Status: {}", id, status);
        
        OfflineOperation operation = new OfflineOperation(
                "assignment-status-" + id + "-" + System.currentTimeMillis(),
                OperationType.UPDATE_ASSIGNMENT_STATUS,
                new Object[]{id, status}
        );
        
        pendingOperations.add(operation);
        saveOperationToDisk(operation);
    }
    
    /**
     * Add a task update operation to the queue
     */
    @Traced("OfflineSynchronization.queueTaskUpdate")
    public void queueTaskUpdate(Long assignmentId, Long taskId, AssignmentTaskDTO taskDTO) {
        if (!offlineModeEnabled) {
            return;
        }
        
        tracingService.addTag("assignmentId", String.valueOf(assignmentId));
        tracingService.addTag("taskId", String.valueOf(taskId));
        log.debug("Queueing task update for offline synchronization. AssignmentId: {}, TaskId: {}", assignmentId, taskId);
        
        OfflineOperation operation = new OfflineOperation(
                "task-update-" + assignmentId + "-" + taskId + "-" + System.currentTimeMillis(),
                OperationType.UPDATE_TASK,
                new Object[]{assignmentId, taskId, taskDTO}
        );
        
        pendingOperations.add(operation);
        saveOperationToDisk(operation);
    }
    
    /**
     * Add a task status update operation to the queue
     */
    @Traced("OfflineSynchronization.queueTaskStatusUpdate")
    public void queueTaskStatusUpdate(Long assignmentId, Long taskId, String status) {
        if (!offlineModeEnabled) {
            return;
        }
        
        tracingService.addTag("assignmentId", String.valueOf(assignmentId));
        tracingService.addTag("taskId", String.valueOf(taskId));
        tracingService.addTag("status", status);
        log.debug("Queueing task status update for offline synchronization. AssignmentId: {}, TaskId: {}, Status: {}", 
                assignmentId, taskId, status);
        
        OfflineOperation operation = new OfflineOperation(
                "task-status-" + assignmentId + "-" + taskId + "-" + System.currentTimeMillis(),
                OperationType.UPDATE_TASK_STATUS,
                new Object[]{assignmentId, taskId, status}
        );
        
        pendingOperations.add(operation);
        saveOperationToDisk(operation);
    }
    
    /**
     * Add a courier location update operation to the queue
     */
    @Traced("OfflineSynchronization.queueCourierLocationUpdate")
    public void queueCourierLocationUpdate(Long id, Double latitude, Double longitude) {
        if (!offlineModeEnabled) {
            return;
        }
        
        tracingService.addTag("courierId", String.valueOf(id));
        tracingService.addTag("lat", String.valueOf(latitude));
        tracingService.addTag("lng", String.valueOf(longitude));
        log.debug("Queueing courier location update for offline synchronization. ID: {}", id);
        
        OfflineOperation operation = new OfflineOperation(
                "courier-location-" + id + "-" + System.currentTimeMillis(),
                OperationType.UPDATE_COURIER_LOCATION,
                new Object[]{id, latitude, longitude}
        );
        
        pendingOperations.add(operation);
        saveOperationToDisk(operation);
    }
    
    /**
     * Save an operation to disk for persistence
     */
    private void saveOperationToDisk(OfflineOperation operation) {
        try {
            Path filePath = Paths.get(offlineDataDir, "pending-operations", operation.getOperationId() + ".json");
            objectMapper.writeValue(filePath.toFile(), operation);
            log.debug("Saved operation to disk: {}", operation.getOperationId());
        } catch (Exception e) {
            log.error("Error saving operation to disk: {}", operation.getOperationId(), e);
        }
    }
    
    /**
     * Delete an operation file from disk
     */
    private void deleteOperationFile(String operationId) {
        try {
            Path filePath = Paths.get(offlineDataDir, "pending-operations", operationId + ".json");
            Files.deleteIfExists(filePath);
            log.debug("Deleted operation file: {}", operationId);
        } catch (Exception e) {
            log.error("Error deleting operation file: {}", operationId, e);
        }
    }
    
    /**
     * Synchronize pending operations with the courier management service
     * This is scheduled to run every 30 seconds
     */
    @Traced("OfflineSynchronization.synchronizePendingOperations")
    @Scheduled(fixedRateString = "${branch-courier-app.offline-mode.sync-interval-ms:30000}")
    public void synchronizePendingOperations() {
        if (!offlineModeEnabled || pendingOperations.isEmpty()) {
            return;
        }
        
        log.info("Starting synchronization of {} pending operations", pendingOperations.size());
        int syncCount = 0;
        int failCount = 0;
        
        List<OfflineOperation> processedOperations = new ArrayList<>();
        
        for (OfflineOperation operation : pendingOperations) {
            // Skip operations that have failed too many times
            if (failedOperations.getOrDefault(operation.getOperationId(), 0) >= maxSyncAttempts) {
                log.warn("Skipping operation that has failed too many times: {}", operation.getOperationId());
                continue;
            }
            
            try {
                processOperation(operation);
                processedOperations.add(operation);
                deleteOperationFile(operation.getOperationId());
                syncCount++;
            } catch (Exception e) {
                log.error("Error processing offline operation: {}", operation.getOperationId(), e);
                failCount++;
                
                // Track failed operations
                int attempts = failedOperations.getOrDefault(operation.getOperationId(), 0);
                failedOperations.put(operation.getOperationId(), attempts + 1);
            }
        }
        
        // Remove processed operations
        pendingOperations.removeAll(processedOperations);
        
        // Remove operations that no longer need to be tracked
        processedOperations.forEach(op -> failedOperations.remove(op.getOperationId()));
        
        log.info("Completed synchronization. Processed: {}, Failed: {}, Remaining: {}", 
                syncCount, failCount, pendingOperations.size());
    }
    
    /**
     * Process a single operation by type
     */
    private void processOperation(OfflineOperation operation) throws Exception {
        log.debug("Processing operation: {}, Type: {}", operation.getOperationId(), operation.getOperationType());
        
        Object[] params = operation.getParams();
        
        switch (operation.getOperationType()) {
            case UPDATE_ASSIGNMENT:
                integrationService.updateAssignment((Long) params[0], (AssignmentDTO) params[1]);
                break;
                
            case UPDATE_ASSIGNMENT_STATUS:
                integrationService.updateAssignmentStatus((Long) params[0], (String) params[1]);
                break;
                
            case UPDATE_TASK:
                integrationService.updateAssignmentTask((Long) params[0], (Long) params[1], (AssignmentTaskDTO) params[2]);
                break;
                
            case UPDATE_TASK_STATUS:
                integrationService.updateTaskStatus((Long) params[0], (Long) params[1], (String) params[2]);
                break;
                
            case UPDATE_COURIER_LOCATION:
                integrationService.updateCourierLocation((Long) params[0], (Double) params[1], (Double) params[2]);
                break;
                
            default:
                throw new IllegalArgumentException("Unknown operation type: " + operation.getOperationType());
        }
    }
    
    /**
     * Enum for different types of operations
     */
    public enum OperationType {
        UPDATE_ASSIGNMENT,
        UPDATE_ASSIGNMENT_STATUS,
        UPDATE_TASK,
        UPDATE_TASK_STATUS,
        UPDATE_COURIER_LOCATION
    }
    
    /**
     * Class representing an offline operation
     */
    public static class OfflineOperation {
        private String operationId;
        private OperationType operationType;
        private Object[] params;
        
        public OfflineOperation() {
        }
        
        public OfflineOperation(String operationId, OperationType operationType, Object[] params) {
            this.operationId = operationId;
            this.operationType = operationType;
            this.params = params;
        }
        
        public String getOperationId() {
            return operationId;
        }
        
        public void setOperationId(String operationId) {
            this.operationId = operationId;
        }
        
        public OperationType getOperationType() {
            return operationType;
        }
        
        public void setOperationType(OperationType operationType) {
            this.operationType = operationType;
        }
        
        public Object[] getParams() {
            return params;
        }
        
        public void setParams(Object[] params) {
            this.params = params;
        }
    }
}