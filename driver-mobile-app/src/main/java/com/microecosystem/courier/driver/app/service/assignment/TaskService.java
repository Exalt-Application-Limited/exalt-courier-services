package com.microecosystem.courier.driver.app.service.assignment;

import com.microecosystem.courier.driver.app.model.assignment.Task;
import com.microecosystem.courier.driver.app.model.assignment.TaskStatus;
import com.microecosystem.courier.driver.app.model.assignment.TaskType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service interface for Task operations in the driver mobile app.
 */
public interface TaskService {

    /**
     * Get all tasks for an assignment.
     *
     * @param assignmentId the assignment ID
     * @return list of tasks
     */
    List<Task> getTasksByAssignment(Long assignmentId);
    
    /**
     * Get all tasks for an assignment with a specific status.
     *
     * @param assignmentId the assignment ID
     * @param status the task status
     * @return list of tasks
     */
    List<Task> getTasksByAssignmentAndStatus(Long assignmentId, TaskStatus status);
    
    /**
     * Get all tasks for an assignment with a specific type.
     *
     * @param assignmentId the assignment ID
     * @param taskType the task type
     * @return list of tasks
     */
    List<Task> getTasksByAssignmentAndType(Long assignmentId, TaskType taskType);
    
    /**
     * Get task by ID.
     *
     * @param id the task ID
     * @return optional task
     */
    Optional<Task> getTaskById(Long id);
    
    /**
     * Get task by ID and assignment ID.
     *
     * @param id the task ID
     * @param assignmentId the assignment ID
     * @return optional task
     */
    Optional<Task> getTaskByIdAndAssignmentId(Long id, Long assignmentId);
    
    /**
     * Create a new task.
     *
     * @param task the task to create
     * @return the created task
     */
    Task createTask(Task task);
    
    /**
     * Update an existing task.
     *
     * @param task the task to update
     * @return the updated task
     */
    Task updateTask(Task task);
    
    /**
     * Update task status.
     *
     * @param id the task ID
     * @param status the new status
     * @return the updated task
     */
    Task updateTaskStatus(Long id, TaskStatus status);
    
    /**
     * Start a task.
     *
     * @param id the task ID
     * @param assignmentId the assignment ID
     * @return the updated task
     */
    Task startTask(Long id, Long assignmentId);
    
    /**
     * Complete a task.
     *
     * @param id the task ID
     * @param assignmentId the assignment ID
     * @param completionData the completion data (proof of delivery, notes, etc.)
     * @return the updated task
     */
    Task completeTask(Long id, Long assignmentId, Map<String, Object> completionData);
    
    /**
     * Mark task as arrived.
     *
     * @param id the task ID
     * @param assignmentId the assignment ID
     * @return the updated task
     */
    Task markTaskAsArrived(Long id, Long assignmentId);
    
    /**
     * Update task sequence.
     *
     * @param id the task ID
     * @param sequenceNumber the new sequence number
     * @return the updated task
     */
    Task updateTaskSequence(Long id, Integer sequenceNumber);
    
    /**
     * Get tasks with time windows within a date range.
     *
     * @param startDate start of date range
     * @param endDate end of date range
     * @param pageable pagination information
     * @return page of tasks
     */
    Page<Task> getTasksByTimeWindowRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    /**
     * Get tasks by tracking number.
     *
     * @param trackingNumber the tracking number
     * @return list of tasks
     */
    List<Task> getTasksByTrackingNumber(String trackingNumber);
    
    /**
     * Get tasks by package ID.
     *
     * @param packageId the package ID
     * @return list of tasks
     */
    List<Task> getTasksByPackageId(String packageId);
    
    /**
     * Count tasks by status for an assignment.
     *
     * @param assignmentId the assignment ID
     * @param status the task status
     * @return count of tasks
     */
    long countTasksByAssignmentAndStatus(Long assignmentId, TaskStatus status);
    
    /**
     * Get tasks with sync status.
     *
     * @param syncStatus the sync status
     * @param pageable pagination information
     * @return page of tasks
     */
    Page<Task> getTasksBySyncStatus(String syncStatus, Pageable pageable);
    
    /**
     * Update task sync status.
     *
     * @param id the task ID
     * @param syncStatus the new sync status
     * @return the updated task
     */
    Task updateTaskSyncStatus(Long id, String syncStatus);
    
    /**
     * Get the next pending task for an assignment.
     *
     * @param assignmentId the assignment ID
     * @return optional task
     */
    Optional<Task> getNextPendingTaskForAssignment(Long assignmentId);
    
    /**
     * Delete a task (soft delete).
     *
     * @param id the task ID
     * @return true if successful
     */
    boolean deleteTask(Long id);
}
