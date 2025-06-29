package com.exalt.courier.courier.controller;

import com.microsocial.courier.annotation.Traced;
import com.microsocial.courier.model.dto.*;
import com.microsocial.courier.service.integration.CourierManagementIntegrationService;
import com.microsocial.courier.service.integration.OfflineSynchronizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller for integrating with Courier Management Service
 */
@Slf4j
@RestController
@RequestMapping("/api/integration/courier-management")
@Tag(name = "Courier Management Integration", description = "APIs for interacting with Courier Management Service")
public class CourierManagementIntegrationController {

    private final CourierManagementIntegrationService integrationService;
    private final OfflineSynchronizationService offlineSyncService;
    
    @Value("${branch-courier-app.offline-mode.enabled:true}")
    private boolean offlineModeEnabled;

    @Autowired
    public CourierManagementIntegrationController(
            CourierManagementIntegrationService integrationService,
            OfflineSynchronizationService offlineSyncService) {
        this.integrationService = integrationService;
        this.offlineSyncService = offlineSyncService;
    }

    //
    // Assignment endpoints
    //
    
    @Traced("CourierManagementIntegrationController.getAssignments")
    @GetMapping("/assignments")
    @Operation(summary = "Get assignments", description = "Retrieve assignments from courier management service")
    public ResponseEntity<PagedResponseDTO<AssignmentDTO>> getAssignments(
            @RequestParam(value = "branchId", required = false) Long branchId,
            @RequestParam(value = "courierId", required = false) Long courierId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "sort", defaultValue = "createdAt,desc") String sort) {
        
        log.info("Fetching assignments. BranchId: {}, CourierId: {}, Status: {}, Page: {}, Size: {}",
                branchId, courierId, status, page, size);
        
        PagedResponseDTO<AssignmentDTO> assignments = integrationService.getAssignments(
                branchId, courierId, status, page, size, sort);
        
        return ResponseEntity.ok(assignments);
    }

    @Traced("CourierManagementIntegrationController.getAssignment")
    @GetMapping("/assignments/{id}")
    @Operation(summary = "Get assignment details", description = "Retrieve a specific assignment from courier management service")
    public ResponseEntity<AssignmentDTO> getAssignment(@PathVariable("id") Long id) {
        log.info("Fetching assignment details. ID: {}", id);
        
        AssignmentDTO assignment = integrationService.getAssignment(id);
        if (assignment == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(assignment);
    }

    @Traced("CourierManagementIntegrationController.createAssignment")
    @PostMapping("/assignments")
    @Operation(summary = "Create assignment", description = "Create a new assignment in courier management service")
    public ResponseEntity<AssignmentDTO> createAssignment(@Valid @RequestBody AssignmentDTO assignmentDTO) {
        log.info("Creating assignment. BranchId: {}, CourierId: {}", 
                assignmentDTO.getBranchId(), assignmentDTO.getCourierId());
        
        AssignmentDTO createdAssignment = integrationService.createAssignment(assignmentDTO);
        return ResponseEntity.ok(createdAssignment);
    }

    @Traced("CourierManagementIntegrationController.updateAssignment")
    @PutMapping("/assignments/{id}")
    @Operation(summary = "Update assignment", description = "Update an existing assignment in courier management service")
    public ResponseEntity<AssignmentDTO> updateAssignment(
            @PathVariable("id") Long id, 
            @Valid @RequestBody AssignmentDTO assignmentDTO) {
        
        log.info("Updating assignment. ID: {}", id);
        
        try {
            AssignmentDTO updatedAssignment = integrationService.updateAssignment(id, assignmentDTO);
            return ResponseEntity.ok(updatedAssignment);
        } catch (Exception e) {
            log.warn("Error updating assignment, queueing for offline sync. ID: {}", id, e);
            
            if (offlineModeEnabled) {
                offlineSyncService.queueAssignmentUpdate(id, assignmentDTO);
                return ResponseEntity.accepted().body(assignmentDTO);
            } else {
                throw e;
            }
        }
    }

    @Traced("CourierManagementIntegrationController.updateAssignmentStatus")
    @PutMapping("/assignments/{id}/status")
    @Operation(summary = "Update assignment status", description = "Update the status of an assignment in courier management service")
    public ResponseEntity<AssignmentDTO> updateAssignmentStatus(
            @PathVariable("id") Long id, 
            @RequestParam("status") String status) {
        
        log.info("Updating assignment status. ID: {}, Status: {}", id, status);
        
        try {
            AssignmentDTO updatedAssignment = integrationService.updateAssignmentStatus(id, status);
            return ResponseEntity.ok(updatedAssignment);
        } catch (Exception e) {
            log.warn("Error updating assignment status, queueing for offline sync. ID: {}, Status: {}", id, status, e);
            
            if (offlineModeEnabled) {
                offlineSyncService.queueAssignmentStatusUpdate(id, status);
                
                // Return the best response we can
                AssignmentDTO assignment = new AssignmentDTO();
                assignment.setId(id);
                assignment.setStatus(status);
                return ResponseEntity.accepted().body(assignment);
            } else {
                throw e;
            }
        }
    }

    @Traced("CourierManagementIntegrationController.countAssignmentsByBranchAndStatus")
    @GetMapping("/assignments/branch/{branchId}/count")
    @Operation(summary = "Count assignments", description = "Count assignments by branch and status")
    public ResponseEntity<Long> countAssignmentsByBranchAndStatus(
            @PathVariable("branchId") Long branchId,
            @RequestParam(value = "status", required = false) String status) {
        
        log.info("Counting assignments. BranchId: {}, Status: {}", branchId, status);
        
        Long count = integrationService.countAssignmentsByBranchAndStatus(branchId, status);
        return ResponseEntity.ok(count);
    }

    //
    // Assignment Task endpoints
    //
    
    @Traced("CourierManagementIntegrationController.getAssignmentTasks")
    @GetMapping("/assignments/{assignmentId}/tasks")
    @Operation(summary = "Get assignment tasks", description = "Retrieve tasks for a specific assignment")
    public ResponseEntity<List<AssignmentTaskDTO>> getAssignmentTasks(
            @PathVariable("assignmentId") Long assignmentId) {
        
        log.info("Fetching assignment tasks. AssignmentId: {}", assignmentId);
        
        List<AssignmentTaskDTO> tasks = integrationService.getAssignmentTasks(assignmentId);
        return ResponseEntity.ok(tasks);
    }

    @Traced("CourierManagementIntegrationController.getAssignmentTask")
    @GetMapping("/assignments/{assignmentId}/tasks/{taskId}")
    @Operation(summary = "Get assignment task", description = "Retrieve a specific task from an assignment")
    public ResponseEntity<AssignmentTaskDTO> getAssignmentTask(
            @PathVariable("assignmentId") Long assignmentId,
            @PathVariable("taskId") Long taskId) {
        
        log.info("Fetching assignment task. AssignmentId: {}, TaskId: {}", assignmentId, taskId);
        
        AssignmentTaskDTO task = integrationService.getAssignmentTask(assignmentId, taskId);
        if (task == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(task);
    }

    @Traced("CourierManagementIntegrationController.createAssignmentTask")
    @PostMapping("/assignments/{assignmentId}/tasks")
    @Operation(summary = "Create assignment task", description = "Create a new task for an assignment")
    public ResponseEntity<AssignmentTaskDTO> createAssignmentTask(
            @PathVariable("assignmentId") Long assignmentId,
            @Valid @RequestBody AssignmentTaskDTO taskDTO) {
        
        log.info("Creating assignment task. AssignmentId: {}", assignmentId);
        
        AssignmentTaskDTO createdTask = integrationService.createAssignmentTask(assignmentId, taskDTO);
        return ResponseEntity.ok(createdTask);
    }

    @Traced("CourierManagementIntegrationController.updateAssignmentTask")
    @PutMapping("/assignments/{assignmentId}/tasks/{taskId}")
    @Operation(summary = "Update assignment task", description = "Update an existing task for an assignment")
    public ResponseEntity<AssignmentTaskDTO> updateAssignmentTask(
            @PathVariable("assignmentId") Long assignmentId,
            @PathVariable("taskId") Long taskId,
            @Valid @RequestBody AssignmentTaskDTO taskDTO) {
        
        log.info("Updating assignment task. AssignmentId: {}, TaskId: {}", assignmentId, taskId);
        
        try {
            AssignmentTaskDTO updatedTask = integrationService.updateAssignmentTask(assignmentId, taskId, taskDTO);
            return ResponseEntity.ok(updatedTask);
        } catch (Exception e) {
            log.warn("Error updating task, queueing for offline sync. AssignmentId: {}, TaskId: {}", assignmentId, taskId, e);
            
            if (offlineModeEnabled) {
                offlineSyncService.queueTaskUpdate(assignmentId, taskId, taskDTO);
                return ResponseEntity.accepted().body(taskDTO);
            } else {
                throw e;
            }
        }
    }

    @Traced("CourierManagementIntegrationController.updateTaskStatus")
    @PutMapping("/assignments/{assignmentId}/tasks/{taskId}/status")
    @Operation(summary = "Update task status", description = "Update the status of a task")
    public ResponseEntity<AssignmentTaskDTO> updateTaskStatus(
            @PathVariable("assignmentId") Long assignmentId,
            @PathVariable("taskId") Long taskId,
            @RequestParam("status") String status) {
        
        log.info("Updating task status. AssignmentId: {}, TaskId: {}, Status: {}", assignmentId, taskId, status);
        
        try {
            AssignmentTaskDTO updatedTask = integrationService.updateTaskStatus(assignmentId, taskId, status);
            return ResponseEntity.ok(updatedTask);
        } catch (Exception e) {
            log.warn("Error updating task status, queueing for offline sync. AssignmentId: {}, TaskId: {}, Status: {}", 
                    assignmentId, taskId, status, e);
            
            if (offlineModeEnabled) {
                offlineSyncService.queueTaskStatusUpdate(assignmentId, taskId, status);
                
                // Return the best response we can
                AssignmentTaskDTO task = new AssignmentTaskDTO();
                task.setId(taskId);
                task.setAssignmentId(assignmentId);
                task.setStatus(status);
                return ResponseEntity.accepted().body(task);
            } else {
                throw e;
            }
        }
    }

    @Traced("CourierManagementIntegrationController.getOptimalTaskSequence")
    @GetMapping("/assignments/{assignmentId}/tasks/optimal-sequence")
    @Operation(summary = "Get optimal task sequence", description = "Calculate the optimal sequence for tasks in an assignment")
    public ResponseEntity<List<AssignmentTaskDTO>> getOptimalTaskSequence(
            @PathVariable("assignmentId") Long assignmentId) {
        
        log.info("Calculating optimal task sequence. AssignmentId: {}", assignmentId);
        
        List<AssignmentTaskDTO> sequence = integrationService.getOptimalTaskSequence(assignmentId);
        return ResponseEntity.ok(sequence);
    }

    @Traced("CourierManagementIntegrationController.applyOptimalTaskSequence")
    @PostMapping("/assignments/{assignmentId}/tasks/apply-optimal-sequence")
    @Operation(summary = "Apply optimal task sequence", description = "Apply the optimal sequence to tasks in an assignment")
    public ResponseEntity<List<AssignmentTaskDTO>> applyOptimalTaskSequence(
            @PathVariable("assignmentId") Long assignmentId) {
        
        log.info("Applying optimal task sequence. AssignmentId: {}", assignmentId);
        
        List<AssignmentTaskDTO> sequence = integrationService.applyOptimalTaskSequence(assignmentId);
        return ResponseEntity.ok(sequence);
    }

    //
    // Courier endpoints
    //
    
    @Traced("CourierManagementIntegrationController.getCouriers")
    @GetMapping("/couriers")
    @Operation(summary = "Get couriers", description = "Retrieve couriers from courier management service")
    public ResponseEntity<PagedResponseDTO<CourierDTO>> getCouriers(
            @RequestParam(value = "branchId", required = false) Long branchId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "available", required = false) Boolean available,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        
        log.info("Fetching couriers. BranchId: {}, Status: {}, Available: {}, Page: {}, Size: {}",
                branchId, status, available, page, size);
        
        PagedResponseDTO<CourierDTO> couriers = integrationService.getCouriers(branchId, status, available, page, size);
        return ResponseEntity.ok(couriers);
    }

    @Traced("CourierManagementIntegrationController.getCourier")
    @GetMapping("/couriers/{id}")
    @Operation(summary = "Get courier details", description = "Retrieve a specific courier from courier management service")
    public ResponseEntity<CourierDTO> getCourier(@PathVariable("id") Long id) {
        log.info("Fetching courier details. ID: {}", id);
        
        CourierDTO courier = integrationService.getCourier(id);
        if (courier == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(courier);
    }

    @Traced("CourierManagementIntegrationController.createCourier")
    @PostMapping("/couriers")
    @Operation(summary = "Create courier", description = "Create a new courier in courier management service")
    public ResponseEntity<CourierDTO> createCourier(@Valid @RequestBody CourierDTO courierDTO) {
        log.info("Creating courier. BranchId: {}", courierDTO.getBranchId());
        
        CourierDTO createdCourier = integrationService.createCourier(courierDTO);
        return ResponseEntity.ok(createdCourier);
    }

    @Traced("CourierManagementIntegrationController.updateCourier")
    @PutMapping("/couriers/{id}")
    @Operation(summary = "Update courier", description = "Update an existing courier in courier management service")
    public ResponseEntity<CourierDTO> updateCourier(
            @PathVariable("id") Long id, 
            @Valid @RequestBody CourierDTO courierDTO) {
        
        log.info("Updating courier. ID: {}", id);
        
        CourierDTO updatedCourier = integrationService.updateCourier(id, courierDTO);
        return ResponseEntity.ok(updatedCourier);
    }

    @Traced("CourierManagementIntegrationController.updateCourierStatus")
    @PutMapping("/couriers/{id}/status")
    @Operation(summary = "Update courier status", description = "Update the status of a courier in courier management service")
    public ResponseEntity<CourierDTO> updateCourierStatus(
            @PathVariable("id") Long id, 
            @RequestParam("status") String status) {
        
        log.info("Updating courier status. ID: {}, Status: {}", id, status);
        
        CourierDTO updatedCourier = integrationService.updateCourierStatus(id, status);
        return ResponseEntity.ok(updatedCourier);
    }

    @Traced("CourierManagementIntegrationController.updateCourierLocation")
    @PutMapping("/couriers/{id}/location")
    @Operation(summary = "Update courier location", description = "Update the location of a courier in courier management service")
    public ResponseEntity<CourierDTO> updateCourierLocation(
            @PathVariable("id") Long id,
            @RequestParam("latitude") Double latitude,
            @RequestParam("longitude") Double longitude) {
        
        log.info("Updating courier location. ID: {}, Lat: {}, Lng: {}", id, latitude, longitude);
        
        try {
            CourierDTO updatedCourier = integrationService.updateCourierLocation(id, latitude, longitude);
            return ResponseEntity.ok(updatedCourier);
        } catch (Exception e) {
            log.warn("Error updating courier location, queueing for offline sync. ID: {}", id, e);
            
            if (offlineModeEnabled) {
                offlineSyncService.queueCourierLocationUpdate(id, latitude, longitude);
                
                // Return the best response we can
                CourierDTO courier = new CourierDTO();
                courier.setId(id);
                courier.setCurrentLatitude(latitude);
                courier.setCurrentLongitude(longitude);
                return ResponseEntity.accepted().body(courier);
            } else {
                throw e;
            }
        }
    }

    @Traced("CourierManagementIntegrationController.countCouriersByBranchAndStatus")
    @GetMapping("/couriers/branch/{branchId}/count")
    @Operation(summary = "Count couriers", description = "Count couriers by branch, status, and availability")
    public ResponseEntity<Long> countCouriersByBranchAndStatus(
            @PathVariable("branchId") Long branchId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "available", required = false) Boolean available) {
        
        log.info("Counting couriers. BranchId: {}, Status: {}, Available: {}", branchId, status, available);
        
        Long count = integrationService.countCouriersByBranchAndStatus(branchId, status, available);
        return ResponseEntity.ok(count);
    }

    //
    // Performance Metrics endpoints
    //
    
    @Traced("CourierManagementIntegrationController.getCourierMetrics")
    @GetMapping("/metrics/courier/{courierId}")
    @Operation(summary = "Get courier metrics", description = "Retrieve performance metrics for a specific courier")
    public ResponseEntity<List<PerformanceMetricDTO>> getCourierMetrics(
            @PathVariable("courierId") Long courierId,
            @RequestParam(value = "metricType", required = false) String metricType,
            @RequestParam(value = "startDate", required = false) 
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(value = "endDate", required = false) 
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        log.info("Fetching courier metrics. CourierId: {}, MetricType: {}", courierId, metricType);
        
        List<PerformanceMetricDTO> metrics = integrationService.getCourierMetrics(
                courierId, metricType, startDate, endDate);
        
        return ResponseEntity.ok(metrics);
    }

    @Traced("CourierManagementIntegrationController.getBranchMetrics")
    @GetMapping("/metrics/branch/{branchId}")
    @Operation(summary = "Get branch metrics", description = "Retrieve performance metrics for a specific branch")
    public ResponseEntity<List<PerformanceMetricDTO>> getBranchMetrics(
            @PathVariable("branchId") Long branchId,
            @RequestParam(value = "metricType", required = false) String metricType,
            @RequestParam(value = "startDate", required = false) 
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(value = "endDate", required = false) 
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        log.info("Fetching branch metrics. BranchId: {}, MetricType: {}", branchId, metricType);
        
        List<PerformanceMetricDTO> metrics = integrationService.getBranchMetrics(
                branchId, metricType, startDate, endDate);
        
        return ResponseEntity.ok(metrics);
    }
    
    //
    // Synchronization endpoints
    //
    
    @Traced("CourierManagementIntegrationController.synchronizePendingOperations")
    @PostMapping("/sync")
    @Operation(summary = "Synchronize pending operations", description = "Manually trigger synchronization of pending offline operations")
    public ResponseEntity<String> synchronizePendingOperations() {
        log.info("Manually triggering synchronization of pending operations");
        
        offlineSyncService.synchronizePendingOperations();
        return ResponseEntity.ok("Synchronization triggered successfully");
    }
}