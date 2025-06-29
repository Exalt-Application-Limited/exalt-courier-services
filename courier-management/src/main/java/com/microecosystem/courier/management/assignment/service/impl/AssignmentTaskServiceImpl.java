package com.exalt.courier.management.assignment.service.impl;

import com.exalt.courier.management.assignment.model.Assignment;
import com.exalt.courier.management.assignment.model.AssignmentTask;
import com.exalt.courier.management.assignment.model.TaskStatus;
import com.exalt.courier.management.assignment.model.TaskType;
import com.exalt.courier.management.assignment.repository.AssignmentRepository;
import com.exalt.courier.management.assignment.repository.AssignmentTaskRepository;
import com.exalt.courier.management.assignment.service.AssignmentTaskService;
import com.exalt.courier.management.exception.BusinessException;
import com.exalt.courier.management.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of the AssignmentTaskService interface.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AssignmentTaskServiceImpl implements AssignmentTaskService {

    private final AssignmentTaskRepository taskRepository;
    private final AssignmentRepository assignmentRepository;

    @Override
    @Transactional
    public AssignmentTask createTask(String assignmentId, AssignmentTask task) {
        log.info("Creating new task for assignment: {}", assignmentId);
        
        // Find the assignment
        Assignment assignment = getAssignmentByIdOrThrow(assignmentId);
        
        // Set default values if not provided
        if (task.getStatus() == null) {
            task.setStatus(TaskStatus.PENDING);
        }
        
        // Set the assignment
        task.setAssignment(assignment);
        
        // Set sequence number if not provided
        if (task.getSequence() == null) {
            // Find the highest existing sequence number and add 1
            Integer maxSequence = taskRepository.findMaxSequenceNumberByAssignment(assignment);
            task.setSequence(maxSequence != null ? maxSequence + 1 : 1);
        }
        
        // Validate time windows if provided
        if (task.getStartTimeWindow() != null && task.getEndTimeWindow() != null) {
            if (task.getStartTimeWindow().isAfter(task.getEndTimeWindow())) {
                throw new BusinessException("Start time window cannot be after end time window");
            }
        }
        
        return taskRepository.save(task);
    }

    @Override
    @Transactional
    public AssignmentTask updateTask(String taskId, AssignmentTask task) {
        log.info("Updating task with ID: {}", taskId);
        
        // Ensure the task exists
        AssignmentTask existingTask = getTaskByIdOrThrow(taskId);
        
        // Update fields
        existingTask.setNotes(task.getNotes());
        existingTask.setTaskType(task.getTaskType());
        existingTask.setLocation(task.getLocation());
        existingTask.setStartTimeWindow(task.getStartTimeWindow());
        existingTask.setEndTimeWindow(task.getEndTimeWindow());
        existingTask.setEstimatedDuration(task.getEstimatedDuration());
        // Additional data is handled through getAdditionalData() method
        
        // Don't update status, sequence number, or timestamps here
        // Those have separate methods
        
        return taskRepository.save(existingTask);
    }

    @Override
    public Optional<AssignmentTask> getTaskById(String taskId) {
        return taskRepository.findById(taskId);
    }

    @Override
    public List<AssignmentTask> getTasksByAssignment(String assignmentId) {
        Assignment assignment = getAssignmentByIdOrThrow(assignmentId);
        return taskRepository.findByAssignment(assignment);
    }

    @Override
    public Page<AssignmentTask> getTasksByAssignment(String assignmentId, Pageable pageable) {
        Assignment assignment = getAssignmentByIdOrThrow(assignmentId);
        return taskRepository.findByAssignment(assignment, pageable);
    }

    @Override
    public List<AssignmentTask> getTasksByAssignmentOrderedBySequence(String assignmentId) {
        Assignment assignment = getAssignmentByIdOrThrow(assignmentId);
        return taskRepository.findByAssignmentOrderBySequenceNumberAsc(assignment);
    }

    @Override
    public List<AssignmentTask> getTasksByAssignmentAndStatus(String assignmentId, TaskStatus status) {
        Assignment assignment = getAssignmentByIdOrThrow(assignmentId);
        return taskRepository.findByAssignmentAndStatus(assignment, status);
    }

    @Override
    public List<AssignmentTask> getTasksByAssignmentAndType(String assignmentId, TaskType type) {
        Assignment assignment = getAssignmentByIdOrThrow(assignmentId);
        return taskRepository.findByAssignmentAndType(assignment, type);
    }

    @Override
    @Transactional
    public AssignmentTask updateTaskStatus(String taskId, TaskStatus status) {
        log.info("Updating task {} status to {}", taskId, status);
        
        AssignmentTask task = getTaskByIdOrThrow(taskId);
        
        // Validate status transition
        validateStatusTransition(task.getStatus(), status);
        
        // Update status and timestamps
        task.setStatus(status);
        
        // Update timestamps based on status
        switch (status) {
            case IN_PROGRESS:
                task.setStartedAt(LocalDateTime.now());
                break;
            case COMPLETED:
                task.setCompletedTime(LocalDateTime.now());
                break;
            case FAILED:
                task.setFailedAt(LocalDateTime.now());
                break;
            default:
                // No timestamp updates for other statuses
        }
        
        return taskRepository.save(task);
    }

    @Override
    @Transactional
    public AssignmentTask completeTask(String taskId) {
        log.info("Completing task {}", taskId);
        
        AssignmentTask task = getTaskByIdOrThrow(taskId);
        
        // Check if task is in a state that can be completed
        if (task.getStatus() != TaskStatus.IN_PROGRESS && task.getStatus() != TaskStatus.DELAYED) {
            throw new BusinessException("Task cannot be completed in current state: " + task.getStatus());
        }
        
        task.setStatus(TaskStatus.COMPLETED);
        task.setCompletedTime(LocalDateTime.now());
        
        // Calculate actual duration if scheduled time exists
        if (task.getScheduledTime() != null) {
            int actualMinutes = (int) java.time.Duration.between(task.getScheduledTime(), task.getCompletedTime()).toMinutes();
            task.setActualDurationMinutes(actualMinutes);
        }
        
        return taskRepository.save(task);
    }

    @Override
    @Transactional
    public AssignmentTask failTask(String taskId, String reason) {
        log.info("Marking task {} as failed with reason: {}", taskId, reason);
        
        AssignmentTask task = getTaskByIdOrThrow(taskId);
        
        // Check if task is in a state that can be failed
        if (task.getStatus() == TaskStatus.COMPLETED || 
            task.getStatus() == TaskStatus.FAILED || 
            task.getStatus() == TaskStatus.CANCELLED) {
            throw new BusinessException("Task cannot be failed in current state: " + task.getStatus());
        }
        
        task.setStatus(TaskStatus.FAILED);
        task.setFailedAt(LocalDateTime.now());
        task.setNotes(task.getNotes() != null 
            ? task.getNotes() + "\nFailure reason: " + reason 
            : "Failure reason: " + reason);
        
        return taskRepository.save(task);
    }

    @Override
    @Transactional
    public boolean deleteTask(String taskId) {
        log.info("Deleting task with ID: {}", taskId);
        
        if (taskRepository.existsById(taskId)) {
            AssignmentTask task = taskRepository.findById(taskId).get();
            
            // Don't allow deletion of completed or in-progress tasks
            if (task.getStatus() == TaskStatus.COMPLETED || task.getStatus() == TaskStatus.IN_PROGRESS) {
                throw new BusinessException("Cannot delete task in " + task.getStatus() + " state");
            }
            
            taskRepository.deleteById(taskId);
            
            // Resequence remaining tasks in the assignment
            resequenceTasksAfterDeletion(task.getAssignment());
            
            return true;
        }
        return false;
    }

    @Override
    public List<AssignmentTask> getTasksInTimeWindow(LocalDateTime startTime, LocalDateTime endTime) {
        return taskRepository.findByTimeWindow(startTime, endTime);
    }

    @Override
    public List<AssignmentTask> getOverdueTasks(String assignmentId) {
        Assignment assignment = getAssignmentByIdOrThrow(assignmentId);
        LocalDateTime now = LocalDateTime.now();
        return taskRepository.findOverdueTasksByAssignment(assignment, now);
    }

    @Override
    @Transactional
    public List<AssignmentTask> resequenceTasks(String assignmentId, List<String> taskIds) {
        log.info("Resequencing tasks for assignment: {}", assignmentId);
        
        Assignment assignment = getAssignmentByIdOrThrow(assignmentId);
        
        // Verify all tasks exist and belong to the assignment
        List<AssignmentTask> tasks = new ArrayList<>();
        Map<String, AssignmentTask> taskMap = new HashMap<>();
        
        for (String taskId : taskIds) {
            AssignmentTask task = getTaskByIdOrThrow(taskId);
            
            if (!task.getAssignment().getId().equals(assignmentId)) {
                throw new BusinessException("Task with ID " + taskId + " does not belong to assignment " + assignmentId);
            }
            
            tasks.add(task);
            taskMap.put(taskId, task);
        }
        
        // Ensure all tasks for the assignment are included
        List<AssignmentTask> allTasks = taskRepository.findByAssignment(assignment);
        if (allTasks.size() != taskIds.size()) {
            throw new BusinessException("All tasks must be included in the resequencing operation");
        }
        
        // Update sequence numbers
        for (int i = 0; i < taskIds.size(); i++) {
            AssignmentTask task = taskMap.get(taskIds.get(i));
            task.setSequence(i + 1);
            taskRepository.save(task);
        }
        
        return taskRepository.findByAssignmentOrderBySequenceNumberAsc(assignment);
    }
    
    /**
     * Resequences tasks after a task is deleted to maintain sequential order.
     * 
     * @param assignment the assignment containing tasks to resequence
     */
    private void resequenceTasksAfterDeletion(Assignment assignment) {
        List<AssignmentTask> tasks = taskRepository.findByAssignmentOrderBySequenceNumberAsc(assignment);
        
        for (int i = 0; i < tasks.size(); i++) {
            AssignmentTask task = tasks.get(i);
            task.setSequence(i + 1);
            taskRepository.save(task);
        }
    }

    /**
     * Validates a status transition.
     *
     * @param currentStatus the current status
     * @param newStatus the new status
     * @throws BusinessException if the transition is not allowed
     */
    private void validateStatusTransition(TaskStatus currentStatus, TaskStatus newStatus) {
        // Terminal states cannot be changed
        if (currentStatus.isTerminal()) {
            throw new BusinessException("Cannot change status of a terminal task: " + currentStatus);
        }
        
        // Validate specific transitions
        switch (currentStatus) {
            case PENDING:
                if (newStatus != TaskStatus.IN_PROGRESS && newStatus != TaskStatus.CANCELLED) {
                    throw new BusinessException("Task in PENDING state can only be IN_PROGRESS or CANCELLED");
                }
                break;
            case IN_PROGRESS:
                if (newStatus != TaskStatus.COMPLETED && 
                    newStatus != TaskStatus.FAILED && 
                    newStatus != TaskStatus.DELAYED && 
                    newStatus != TaskStatus.CANCELLED) {
                    throw new BusinessException("Task in IN_PROGRESS state can only be COMPLETED, FAILED, DELAYED or CANCELLED");
                }
                break;
            case DELAYED:
                if (newStatus != TaskStatus.IN_PROGRESS && 
                    newStatus != TaskStatus.FAILED && 
                    newStatus != TaskStatus.CANCELLED) {
                    throw new BusinessException("Task in DELAYED state can only be IN_PROGRESS, FAILED or CANCELLED");
                }
                break;
            default:
                throw new BusinessException("Unexpected current status: " + currentStatus);
        }
    }
    
    /**
     * Retrieves an assignment by its ID or throws an exception if not found.
     *
     * @param assignmentId the assignment ID
     * @return the assignment
     * @throws ResourceNotFoundException if the assignment is not found
     */
    private Assignment getAssignmentByIdOrThrow(String assignmentId) {
        return assignmentRepository.findByAssignmentId(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with assignmentId: " + assignmentId));
    }
    
    /**
     * Retrieves a task by its ID or throws an exception if not found.
     *
     * @param taskId the task ID
     * @return the task
     * @throws ResourceNotFoundException if the task is not found
     */
    private AssignmentTask getTaskByIdOrThrow(String taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));
    }
} 