package com.gogidix.courier.drivermobileapp.controller;

import com.socialecommerceecosystem.drivermobileapp.client.CourierManagementClient;
import com.socialecommerceecosystem.drivermobileapp.dto.assignment.AssignmentDTO;
import com.socialecommerceecosystem.drivermobileapp.dto.assignment.AssignmentTaskDTO;
import com.socialecommerceecosystem.drivermobileapp.dto.assignment.OfflineSyncRequestDTO;
import com.socialecommerceecosystem.drivermobileapp.dto.assignment.OfflineSyncResponseDTO;
import com.socialecommerceecosystem.drivermobileapp.service.AssignmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * REST controller for assignments operations in the driver mobile app.
 */
@RestController
@RequestMapping("/api/v1/assignments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Assignments", description = "Assignment operations for mobile app")
public class AssignmentController {
    
    private final CourierManagementClient courierManagementClient;
    private final AssignmentService assignmentService;
    
    /**
     * Get assignments for a courier.
     *
     * @param courierId the courier ID
     * @return the assignments for the courier
     */
    @GetMapping("/by-courier/{courierId}")
    @Operation(
        summary = "Get all assignments for a courier",
        description = "Retrieves all assignments for the specified courier, including completed and cancelled ones",
        responses = {
            @ApiResponse(responseCode = "200", description = "Assignments retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Courier not found")
        }
    )
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<List<AssignmentDTO>> getAssignmentsByCourier(@PathVariable String courierId) {
        log.info("REST request to get assignments for courier: {}", courierId);
        return ResponseEntity.ok(assignmentService.getAssignmentsByCourier(courierId));
    }
    
    /**
     * Get active assignments for a courier.
     *
     * @param courierId the courier ID
     * @return the active assignments for the courier
     */
    @GetMapping("/active/by-courier/{courierId}")
    @Operation(
        summary = "Get active assignments for a courier",
        description = "Retrieves only active assignments (ASSIGNED, ACCEPTED, IN_PROGRESS) for the specified courier",
        responses = {
            @ApiResponse(responseCode = "200", description = "Active assignments retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Courier not found")
        }
    )
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<List<AssignmentDTO>> getActiveAssignmentsByCourier(@PathVariable String courierId) {
        log.info("REST request to get active assignments for courier: {}", courierId);
        return ResponseEntity.ok(assignmentService.getActiveAssignmentsByCourier(courierId));
    }
    
    /**
     * Accept an assignment.
     *
     * @param assignmentId the assignment ID
     * @return the updated assignment
     */
    @PatchMapping("/{assignmentId}/accept")
    @Operation(
        summary = "Accept an assignment",
        description = "Driver accepts an assigned delivery assignment",
        responses = {
            @ApiResponse(responseCode = "200", description = "Assignment accepted successfully"),
            @ApiResponse(responseCode = "404", description = "Assignment not found")
        }
    )
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<AssignmentDTO> acceptAssignment(@PathVariable String assignmentId) {
        log.info("REST request to accept assignment: {}", assignmentId);
        return ResponseEntity.ok(assignmentService.acceptAssignment(assignmentId));
    }
    
    /**
     * Start an assignment.
     *
     * @param assignmentId the assignment ID
     * @return the updated assignment
     */
    @PatchMapping("/{assignmentId}/start")
    @Operation(
        summary = "Start an assignment",
        description = "Driver starts working on a previously accepted assignment",
        responses = {
            @ApiResponse(responseCode = "200", description = "Assignment started successfully"),
            @ApiResponse(responseCode = "404", description = "Assignment not found")
        }
    )
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<AssignmentDTO> startAssignment(@PathVariable String assignmentId) {
        log.info("REST request to start assignment: {}", assignmentId);
        return ResponseEntity.ok(assignmentService.startAssignment(assignmentId));
    }
    
    /**
     * Complete an assignment.
     *
     * @param assignmentId the assignment ID
     * @return the updated assignment
     */
    @PatchMapping("/{assignmentId}/complete")
    @Operation(
        summary = "Complete an assignment",
        description = "Driver marks an assignment as completed after all tasks are done",
        responses = {
            @ApiResponse(responseCode = "200", description = "Assignment completed successfully"),
            @ApiResponse(responseCode = "404", description = "Assignment not found")
        }
    )
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<AssignmentDTO> completeAssignment(@PathVariable String assignmentId) {
        log.info("REST request to complete assignment: {}", assignmentId);
        return ResponseEntity.ok(assignmentService.completeAssignment(assignmentId));
    }
    
    /**
     * Cancel an assignment.
     *
     * @param assignmentId the assignment ID
     * @param cancellationRequest the cancellation request containing the reason
     * @return the updated assignment
     */
    @PatchMapping("/{assignmentId}/cancel")
    @Operation(
        summary = "Cancel an assignment",
        description = "Driver cancels an assignment with a reason",
        responses = {
            @ApiResponse(responseCode = "200", description = "Assignment cancelled successfully"),
            @ApiResponse(responseCode = "404", description = "Assignment not found")
        }
    )
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<AssignmentDTO> cancelAssignment(
            @PathVariable String assignmentId,
            @RequestBody Map<String, String> cancellationRequest) {
        
        log.info("REST request to cancel assignment: {}", assignmentId);
        return ResponseEntity.ok(assignmentService.cancelAssignment(assignmentId, cancellationRequest.get("reason")));
    }
    
    /**
     * Optimize routes for a courier's assignments.
     *
     * @param courierId the courier ID
     * @return list of assignments with optimized route
     */
    @GetMapping("/optimize-routes/by-courier/{courierId}")
    @Operation(
        summary = "Optimize routes for a courier",
        description = "Optimizes the sequence of tasks across all active assignments for efficient delivery",
        responses = {
            @ApiResponse(responseCode = "200", description = "Routes optimized successfully"),
            @ApiResponse(responseCode = "404", description = "Courier not found")
        }
    )
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<List<AssignmentDTO>> optimizeRoutes(@PathVariable String courierId) {
        log.info("REST request to optimize routes for courier: {}", courierId);
        return ResponseEntity.ok(assignmentService.optimizeRoutes(courierId));
    }
    
    /**
     * Get tasks for an assignment.
     *
     * @param assignmentId the assignment ID
     * @return list of tasks
     */
    @GetMapping("/{assignmentId}/tasks")
    @Operation(
        summary = "Get tasks for an assignment",
        description = "Retrieves all delivery tasks associated with an assignment",
        responses = {
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Assignment not found")
        }
    )
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<List<AssignmentTaskDTO>> getTasksForAssignment(@PathVariable String assignmentId) {
        log.info("REST request to get tasks for assignment: {}", assignmentId);
        return ResponseEntity.ok(assignmentService.getTasksForAssignment(assignmentId));
    }
    
    /**
     * Complete a task.
     *
     * @param taskId the task ID
     * @param completionData the completion data (proof of delivery, notes, etc.)
     * @return the updated task
     */
    @PatchMapping("/tasks/{taskId}/complete")
    @Operation(
        summary = "Complete a task",
        description = "Driver marks a delivery task as completed with supporting data",
        responses = {
            @ApiResponse(responseCode = "200", description = "Task completed successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found")
        }
    )
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<AssignmentTaskDTO> completeTask(
            @PathVariable String taskId, 
            @RequestBody Map<String, Object> completionData) {
        
        log.info("REST request to complete task: {}", taskId);
        return ResponseEntity.ok(assignmentService.completeTask(taskId, completionData));
    }
    
    /**
     * Update task status.
     *
     * @param taskId the task ID
     * @param statusUpdate the status update
     * @return the updated task
     */
    @PatchMapping("/tasks/{taskId}/status")
    @Operation(
        summary = "Update task status",
        description = "Updates the status of a delivery task",
        responses = {
            @ApiResponse(responseCode = "200", description = "Task status updated successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found")
        }
    )
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<AssignmentTaskDTO> updateTaskStatus(
            @PathVariable String taskId,
            @RequestBody Map<String, String> statusUpdate) {
        
        log.info("REST request to update task status: {}", taskId);
        return ResponseEntity.ok(assignmentService.updateTaskStatus(taskId, statusUpdate.get("status")));
    }
    
    /**
     * Refresh assignment data.
     *
     * @param assignmentId the assignment ID
     * @return the refreshed assignment
     */
    @GetMapping("/{assignmentId}/refresh")
    @Operation(
        summary = "Refresh assignment data",
        description = "Gets the latest data for an assignment from the server",
        responses = {
            @ApiResponse(responseCode = "200", description = "Assignment refreshed successfully"),
            @ApiResponse(responseCode = "404", description = "Assignment not found")
        }
    )
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<AssignmentDTO> refreshAssignment(@PathVariable String assignmentId) {
        log.info("REST request to refresh assignment: {}", assignmentId);
        return ResponseEntity.ok(assignmentService.refreshAssignment(assignmentId));
    }
    
    /**
     * Cache assignment for offline use.
     *
     * @param assignmentId the assignment ID
     * @return success status
     */
    @PostMapping("/{assignmentId}/cache")
    @Operation(
        summary = "Cache assignment for offline use",
        description = "Marks an assignment to be cached locally for offline operations",
        responses = {
            @ApiResponse(responseCode = "200", description = "Assignment cached successfully"),
            @ApiResponse(responseCode = "404", description = "Assignment not found")
        }
    )
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<Map<String, Boolean>> cacheAssignment(@PathVariable String assignmentId) {
        log.info("REST request to cache assignment: {}", assignmentId);
        boolean success = assignmentService.markAssignmentAsCached(assignmentId);
        return ResponseEntity.ok(Map.of("success", success));
    }
    
    /**
     * Get cached assignments.
     *
     * @param courierId the courier ID
     * @return list of cached assignments
     */
    @GetMapping("/cached/by-courier/{courierId}")
    @Operation(
        summary = "Get cached assignments",
        description = "Retrieves all assignments that have been cached for offline use",
        responses = {
            @ApiResponse(responseCode = "200", description = "Cached assignments retrieved successfully")
        }
    )
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<List<AssignmentDTO>> getCachedAssignments(@PathVariable String courierId) {
        log.info("REST request to get cached assignments for courier: {}", courierId);
        return ResponseEntity.ok(assignmentService.getCachedAssignments(courierId));
    }
    
    /**
     * Synchronize offline data.
     *
     * @param syncRequest the synchronization request
     * @return the synchronization response
     */
    @PostMapping("/sync")
    @Operation(
        summary = "Synchronize offline data",
        description = "Synchronizes locally stored assignment data with the server after offline operations",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "Data synchronized successfully",
                content = @Content(schema = @Schema(implementation = OfflineSyncResponseDTO.class))
            )
        }
    )
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<OfflineSyncResponseDTO> synchronizeOfflineData(
            @Valid @RequestBody OfflineSyncRequestDTO syncRequest) {
        
        log.info("REST request to synchronize offline data for courier: {}", syncRequest.getCourierId());
        return ResponseEntity.ok(assignmentService.synchronizeOfflineData(syncRequest));
    }
}
