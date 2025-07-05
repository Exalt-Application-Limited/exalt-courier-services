package com.gogidix.courierservices.management.$1;

import com.gogidix.courier.management.assignment.model.Assignment;
import com.gogidix.courier.management.assignment.model.AssignmentTask;
import com.gogidix.courier.management.assignment.model.TaskStatus;
import com.gogidix.courier.management.assignment.model.TaskType;
import com.gogidix.courier.management.exception.BusinessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for AssignmentTask operations.
 */
public interface AssignmentTaskService {

    /**
     * Creates a new task for an assignment.
     *
     * @param assignmentId the ID of the assignment
     * @param task the task to create
     * @return the created task
     * @throws BusinessException if the task is invalid or the assignment doesn't exist
     */
    AssignmentTask createTask(String assignmentId, AssignmentTask task);

    /**
     * Updates an existing task.
     *
     * @param taskId the ID of the task
     * @param task the updated task details
     * @return the updated task
     * @throws BusinessException if the task is invalid or not found
     */
    AssignmentTask updateTask(String taskId, AssignmentTask task);

    /**
     * Finds a task by its ID.
     *
     * @param taskId the ID of the task
     * @return an Optional containing the task if found
     */
    Optional<AssignmentTask> getTaskById(String taskId);

    /**
     * Gets all tasks for a specific assignment.
     *
     * @param assignmentId the ID of the assignment
     * @return a list of tasks for the assignment
     */
    List<AssignmentTask> getTasksByAssignment(String assignmentId);

    /**
     * Gets all tasks for a specific assignment with pagination.
     *
     * @param assignmentId the ID of the assignment
     * @param pageable pagination information
     * @return a page of tasks for the assignment
     */
    Page<AssignmentTask> getTasksByAssignment(String assignmentId, Pageable pageable);

    /**
     * Gets all tasks for a specific assignment ordered by sequence.
     *
     * @param assignmentId the ID of the assignment
     * @return a list of tasks ordered by sequence
     */
    List<AssignmentTask> getTasksByAssignmentOrderedBySequence(String assignmentId);

    /**
     * Gets all tasks with a specific status for an assignment.
     *
     * @param assignmentId the ID of the assignment
     * @param status the status to filter by
     * @return a list of tasks with the specified status
     */
    List<AssignmentTask> getTasksByAssignmentAndStatus(String assignmentId, TaskStatus status);

    /**
     * Gets all tasks with a specific type for an assignment.
     *
     * @param assignmentId the ID of the assignment
     * @param type the type to filter by
     * @return a list of tasks with the specified type
     */
    List<AssignmentTask> getTasksByAssignmentAndType(String assignmentId, TaskType type);

    /**
     * Updates the status of a task.
     *
     * @param taskId the ID of the task
     * @param status the new status
     * @return the updated task
     * @throws BusinessException if the task is not found or the status transition is invalid
     */
    AssignmentTask updateTaskStatus(String taskId, TaskStatus status);

    /**
     * Marks a task as completed.
     *
     * @param taskId the ID of the task
     * @return the updated task
     * @throws BusinessException if the task is not found or cannot be completed
     */
    AssignmentTask completeTask(String taskId);

    /**
     * Marks a task as failed.
     *
     * @param taskId the ID of the task
     * @param reason the reason for failure
     * @return the updated task
     * @throws BusinessException if the task is not found or cannot be failed
     */
    AssignmentTask failTask(String taskId, String reason);

    /**
     * Deletes a task.
     *
     * @param taskId the ID of the task
     * @return true if the task was deleted, false otherwise
     */
    boolean deleteTask(String taskId);

    /**
     * Gets all tasks scheduled within a time window.
     *
     * @param startTime the start of the time window
     * @param endTime the end of the time window
     * @return a list of tasks scheduled within the time window
     */
    List<AssignmentTask> getTasksInTimeWindow(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * Gets all overdue tasks for an assignment.
     *
     * @param assignmentId the ID of the assignment
     * @return a list of overdue tasks
     */
    List<AssignmentTask> getOverdueTasks(String assignmentId);

    /**
     * Resequences tasks for an assignment.
     *
     * @param assignmentId the ID of the assignment
     * @param taskIds the ordered list of task IDs
     * @return the updated list of tasks
     * @throws BusinessException if any task is not found or doesn't belong to the assignment
     */
    List<AssignmentTask> resequenceTasks(String assignmentId, List<String> taskIds);
} 