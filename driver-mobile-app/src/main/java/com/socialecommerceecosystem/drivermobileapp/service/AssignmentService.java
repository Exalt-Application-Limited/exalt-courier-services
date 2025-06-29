package com.exalt.courier.drivermobileapp.service;

import com.socialecommerceecosystem.drivermobileapp.dto.assignment.AssignmentDTO;
import com.socialecommerceecosystem.drivermobileapp.dto.assignment.AssignmentTaskDTO;
import com.socialecommerceecosystem.drivermobileapp.dto.assignment.OfflineSyncRequestDTO;
import com.socialecommerceecosystem.drivermobileapp.dto.assignment.OfflineSyncResponseDTO;

import java.util.List;
import java.util.Map;

/**
 * Service interface for Assignment operations.
 */
public interface AssignmentService {
    
    /**
     * Get assignments for a courier.
     *
     * @param courierId the courier ID
     * @return list of assignments for the courier
     */
    List<AssignmentDTO> getAssignmentsByCourier(String courierId);
    
    /**
     * Get active assignments for a courier.
     *
     * @param courierId the courier ID
     * @return list of active assignments for the courier
     */
    List<AssignmentDTO> getActiveAssignmentsByCourier(String courierId);
    
    /**
     * Accept an assignment.
     *
     * @param assignmentId the assignment ID
     * @return the updated assignment
     */
    AssignmentDTO acceptAssignment(String assignmentId);
    
    /**
     * Start an assignment.
     *
     * @param assignmentId the assignment ID
     * @return the updated assignment
     */
    AssignmentDTO startAssignment(String assignmentId);
    
    /**
     * Complete an assignment.
     *
     * @param assignmentId the assignment ID
     * @return the updated assignment
     */
    AssignmentDTO completeAssignment(String assignmentId);
    
    /**
     * Cancel an assignment.
     *
     * @param assignmentId the assignment ID
     * @param cancellationReason the cancellation reason
     * @return the updated assignment
     */
    AssignmentDTO cancelAssignment(String assignmentId, String cancellationReason);
    
    /**
     * Update task status for an assignment.
     *
     * @param taskId the task ID
     * @param status the new status
     * @return the updated task
     */
    AssignmentTaskDTO updateTaskStatus(String taskId, String status);
    
    /**
     * Optimize routes for a courier's assignments.
     *
     * @param courierId the courier ID
     * @return list of assignments with optimized route
     */
    List<AssignmentDTO> optimizeRoutes(String courierId);
    
    /**
     * Synchronize offline data with the server.
     *
     * @param syncRequest the synchronization request
     * @return the synchronization response
     */
    OfflineSyncResponseDTO synchronizeOfflineData(OfflineSyncRequestDTO syncRequest);
    
    /**
     * Get tasks for an assignment.
     *
     * @param assignmentId the assignment ID
     * @return list of tasks for the assignment
     */
    List<AssignmentTaskDTO> getTasksForAssignment(String assignmentId);
    
    /**
     * Get task by ID.
     *
     * @param taskId the task ID
     * @return the task
     */
    AssignmentTaskDTO getTask(String taskId);
    
    /**
     * Complete a task.
     *
     * @param taskId the task ID
     * @param completionData the completion data (proof of delivery, notes, etc.)
     * @return the updated task
     */
    AssignmentTaskDTO completeTask(String taskId, Map<String, Object> completionData);
    
    /**
     * Fetch the latest data for an assignment.
     *
     * @param assignmentId the assignment ID
     * @return the latest assignment data
     */
    AssignmentDTO refreshAssignment(String assignmentId);
    
    /**
     * Mark an assignment as cached locally for offline use.
     *
     * @param assignmentId the assignment ID
     * @return true if successful
     */
    boolean markAssignmentAsCached(String assignmentId);
    
    /**
     * Get all cached assignments.
     *
     * @param courierId the courier ID
     * @return list of cached assignments
     */
    List<AssignmentDTO> getCachedAssignments(String courierId);
}
