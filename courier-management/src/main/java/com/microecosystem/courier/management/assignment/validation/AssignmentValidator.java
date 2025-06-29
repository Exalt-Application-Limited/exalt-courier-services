package com.exalt.courier.management.assignment.validation;

import com.exalt.courier.management.assignment.model.Assignment;
import com.exalt.courier.management.assignment.model.AssignmentTask;
import com.exalt.courier.management.assignment.model.TaskType;
import com.exalt.courier.management.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Validator for Assignment entities.
 * This class contains validation rules for assignments and their tasks.
 */
@Component
public class AssignmentValidator {
    private static final Logger logger = LoggerFactory.getLogger(AssignmentValidator.class);
    
    private static final int MIN_TASKS_PER_ASSIGNMENT = 1;
    private static final int MAX_TASKS_PER_ASSIGNMENT = 50;
    private static final Duration MIN_ASSIGNMENT_DURATION = Duration.ofMinutes(5);
    private static final Duration MAX_ASSIGNMENT_DURATION = Duration.ofHours(12);

    /**
     * Validates an assignment for creation.
     * 
     * @param assignment the assignment to validate
     * @throws BusinessException if the assignment is invalid
     */
    public void validateForCreation(Assignment assignment) {
        validateBasicFields(assignment);
        validateTasks(assignment.getTasks());
        validateTaskSequences(assignment.getTasks());
        validateScheduling(assignment);
        
        logger.debug("Assignment validated for creation: {}", assignment.getId());
    }
    
    /**
     * Validates an assignment for update.
     * 
     * @param assignment the assignment to validate
     * @throws BusinessException if the assignment is invalid
     */
    public void validateForUpdate(Assignment assignment) {
        validateBasicFields(assignment);
        validateTasks(assignment.getTasks());
        validateTaskSequences(assignment.getTasks());
        validateScheduling(assignment);
        
        // Additional validation for updates
        if (assignment.getId() == null) {
            throw new BusinessException("Assignment ID cannot be null for update");
        }
        
        logger.debug("Assignment validated for update: {}", assignment.getId());
    }
    
    /**
     * Validates basic fields of an assignment.
     * 
     * @param assignment the assignment to validate
     * @throws BusinessException if the assignment has invalid fields
     */
    private void validateBasicFields(Assignment assignment) {
        if (assignment.getId() == null || assignment.getId().trim().isEmpty()) {
            throw new BusinessException("Assignment ID cannot be empty");
        }
        
        if (assignment.getStatus() == null) {
            throw new BusinessException("Assignment status cannot be null");
        }
        
        if (assignment.getEstimatedStartTime() == null) {
            throw new BusinessException("Assignment estimated start time cannot be null");
        }
        
        if (assignment.getPriority() == null) {
            throw new BusinessException("Assignment priority cannot be null");
        }
    }
    
    /**
     * Validates tasks in an assignment.
     * 
     * @param tasks the tasks to validate
     * @throws BusinessException if the tasks are invalid
     */
    private void validateTasks(Set<AssignmentTask> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            throw new BusinessException("Assignment must have at least one task");
        }
        
        if (tasks.size() < MIN_TASKS_PER_ASSIGNMENT) {
            throw new BusinessException("Assignment must have at least " + MIN_TASKS_PER_ASSIGNMENT + " task(s)");
        }
        
        if (tasks.size() > MAX_TASKS_PER_ASSIGNMENT) {
            throw new BusinessException("Assignment cannot have more than " + MAX_TASKS_PER_ASSIGNMENT + " tasks");
        }
        
        // Check if at least one pickup and one delivery task exists for delivery-type assignments
        boolean hasPickup = false;
        boolean hasDelivery = false;
        
        for (AssignmentTask task : tasks) {
            if (task.getTaskType() == TaskType.PICKUP) {
                hasPickup = true;
            } else if (task.getTaskType() == TaskType.DELIVERY) {
                hasDelivery = true;
            }
            
            validateTaskFields(task);
        }
        
        // Only enforce this rule if both pickup and delivery task types are used
        if ((hasPickup || hasDelivery) && !(hasPickup && hasDelivery)) {
            throw new BusinessException("A delivery assignment must have at least one pickup and one delivery task");
        }
    }
    
    /**
     * Validates individual task fields.
     * 
     * @param task the task to validate
     * @throws BusinessException if the task has invalid fields
     */
    private void validateTaskFields(AssignmentTask task) {
        if (task.getTaskType() == null) {
            throw new BusinessException("Task type cannot be null");
        }
        
        if (task.getStatus() == null) {
            throw new BusinessException("Task status cannot be null");
        }
        
        if (task.getSequence() == null) {
            throw new BusinessException("Task sequence cannot be null");
        }
        
        if (task.getAddress() == null || task.getAddress().trim().isEmpty()) {
            throw new BusinessException("Task address cannot be empty");
        }
        
        if (task.getLatitude() == null) {
            throw new BusinessException("Task latitude cannot be null");
        }
        
        if (task.getLongitude() == null) {
            throw new BusinessException("Task longitude cannot be null");
        }
        
        // Validate time windows if both are provided
        if (task.getTimeWindowStart() != null && task.getTimeWindowEnd() != null) {
            if (task.getTimeWindowStart().isAfter(task.getTimeWindowEnd())) {
                throw new BusinessException("Task time window start cannot be after time window end");
            }
        }
    }
    
    /**
     * Validates task sequences for an assignment.
     * 
     * @param tasks the tasks to validate
     * @throws BusinessException if task sequences are invalid
     */
    private void validateTaskSequences(Set<AssignmentTask> tasks) {
        // Convert set to list for easier sequence validation
        List<AssignmentTask> taskList = new ArrayList<>(tasks);
        
        // Check that sequences are unique
        boolean[] sequenceUsed = new boolean[taskList.size() + 1];
        
        for (AssignmentTask task : taskList) {
            int sequence = task.getSequence();
            
            if (sequence < 1 || sequence > taskList.size()) {
                throw new BusinessException("Task sequence must be between 1 and " + taskList.size());
            }
            
            if (sequenceUsed[sequence]) {
                throw new BusinessException("Duplicate task sequence: " + sequence);
            }
            
            sequenceUsed[sequence] = true;
        }
    }
    
    /**
     * Validates scheduling of an assignment.
     * 
     * @param assignment the assignment to validate
     * @throws BusinessException if the scheduling is invalid
     */
    private void validateScheduling(Assignment assignment) {
        LocalDateTime now = LocalDateTime.now();
        
        // Start time should be in the future for new assignments
        if (assignment.getId() == null && assignment.getEstimatedStartTime().isBefore(now)) {
            throw new BusinessException("Assignment estimated start time must be in the future");
        }
        
        // If end time is provided, validate duration
        if (assignment.getEstimatedEndTime() != null) {
            if (assignment.getEstimatedEndTime().isBefore(assignment.getEstimatedStartTime())) {
                throw new BusinessException("Assignment estimated end time cannot be before start time");
            }
            
            Duration duration = Duration.between(assignment.getEstimatedStartTime(), assignment.getEstimatedEndTime());
            
            if (duration.compareTo(MIN_ASSIGNMENT_DURATION) < 0) {
                throw new BusinessException("Assignment duration must be at least " + MIN_ASSIGNMENT_DURATION.toMinutes() + " minutes");
            }
            
            if (duration.compareTo(MAX_ASSIGNMENT_DURATION) > 0) {
                throw new BusinessException("Assignment duration cannot exceed " + MAX_ASSIGNMENT_DURATION.toHours() + " hours");
            }
        }
    }
} 