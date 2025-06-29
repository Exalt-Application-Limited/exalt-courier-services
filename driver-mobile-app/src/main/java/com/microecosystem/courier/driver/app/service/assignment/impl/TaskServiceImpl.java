package com.microecosystem.courier.driver.app.service.assignment.impl;

import com.microecosystem.courier.driver.app.model.assignment.Task;
import com.microecosystem.courier.driver.app.model.assignment.TaskStatus;
import com.microecosystem.courier.driver.app.model.assignment.TaskType;
import com.microecosystem.courier.driver.app.repository.AssignmentRepository;
import com.microecosystem.courier.driver.app.repository.TaskRepository;
import com.microecosystem.courier.driver.app.service.assignment.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation of the TaskService interface.
 */
@Service
public class TaskServiceImpl implements TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

    private final TaskRepository taskRepository;
    private final AssignmentRepository assignmentRepository;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository, AssignmentRepository assignmentRepository) {
        this.taskRepository = taskRepository;
        this.assignmentRepository = assignmentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> getTasksByAssignment(Long assignmentId) {
        logger.debug("Getting tasks for assignment with ID: {}", assignmentId);
        return taskRepository.findByAssignmentId(assignmentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> getTasksByAssignmentAndStatus(Long assignmentId, TaskStatus status) {
        logger.debug("Getting tasks for assignment with ID: {} and status: {}", assignmentId, status);
        return taskRepository.findByAssignmentIdAndStatus(assignmentId, status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> getTasksByAssignmentAndType(Long assignmentId, TaskType taskType) {
        logger.debug("Getting tasks for assignment with ID: {} and type: {}", assignmentId, taskType);
        return taskRepository.findByAssignmentIdAndTaskType(assignmentId, taskType);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Task> getTaskById(Long id) {
        logger.debug("Getting task with ID: {}", id);
        return taskRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Task> getTaskByIdAndAssignmentId(Long id, Long assignmentId) {
        logger.debug("Getting task with ID: {} for assignment with ID: {}", id, assignmentId);
        return taskRepository.findByIdAndAssignmentId(id, assignmentId);
    }

    @Override
    @Transactional
    public Task createTask(Task task) {
        logger.debug("Creating a new task");
        if (task.getId() != null) {
            logger.warn("Task has an ID, which suggests it already exists. Setting ID to null");
            task.setId(null);
        }
        
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        if (task.getStatus() == null) {
            task.setStatus(TaskStatus.PENDING);
        }
        
        return taskRepository.save(task);
    }

    @Override
    @Transactional
    public Task updateTask(Task task) {
        logger.debug("Updating task with ID: {}", task.getId());
        if (task.getId() == null) {
            throw new IllegalArgumentException("Cannot update task without an ID");
        }
        
        Optional<Task> existingTask = taskRepository.findById(task.getId());
        if (existingTask.isEmpty()) {
            throw new IllegalArgumentException("Cannot update non-existent task");
        }
        
        task.setUpdatedAt(LocalDateTime.now());
        return taskRepository.save(task);
    }

    @Override
    @Transactional
    public Task updateTaskStatus(Long id, TaskStatus status) {
        logger.debug("Updating status to {} for task with ID: {}", status, id);
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isEmpty()) {
            throw new IllegalArgumentException("Cannot update status for non-existent task");
        }
        
        Task task = optionalTask.get();
        task.setStatus(status);
        task.setUpdatedAt(LocalDateTime.now());
        
        // Set specific timestamps based on status
        switch (status) {
            case ARRIVED:
                task.setActualArrivalTime(LocalDateTime.now());
                break;
            case COMPLETED:
                task.setCompletedAt(LocalDateTime.now());
                break;
        }
        
        return taskRepository.save(task);
    }

    @Override
    @Transactional
    public Task startTask(Long id, Long assignmentId) {
        logger.debug("Starting task with ID: {} for assignment with ID: {}", id, assignmentId);
        Optional<Task> optionalTask = taskRepository.findByIdAndAssignmentId(id, assignmentId);
        if (optionalTask.isEmpty()) {
            throw new IllegalArgumentException("Task not found or not part of the specified assignment");
        }
        
        Task task = optionalTask.get();
        if (task.getStatus() != TaskStatus.PENDING) {
            throw new IllegalStateException("Task is not in PENDING status and cannot be started");
        }
        
        task.setStatus(TaskStatus.IN_PROGRESS);
        task.setUpdatedAt(LocalDateTime.now());
        
        return taskRepository.save(task);
    }

    @Override
    @Transactional
    public Task completeTask(Long id, Long assignmentId, Map<String, Object> completionData) {
        logger.debug("Completing task with ID: {} for assignment with ID: {}", id, assignmentId);
        Optional<Task> optionalTask = taskRepository.findByIdAndAssignmentId(id, assignmentId);
        if (optionalTask.isEmpty()) {
            throw new IllegalArgumentException("Task not found or not part of the specified assignment");
        }
        
        Task task = optionalTask.get();
        if (task.getStatus() != TaskStatus.IN_PROGRESS && task.getStatus() != TaskStatus.ARRIVED) {
            throw new IllegalStateException("Task must be in IN_PROGRESS or ARRIVED status before it can be completed");
        }
        
        task.setStatus(TaskStatus.COMPLETED);
        task.setCompletedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        
        // Process completion data
        if (completionData != null) {
            if (completionData.containsKey("notes")) {
                task.setNotes(completionData.get("notes").toString());
            }
            
            if (completionData.containsKey("completionCode")) {
                task.setCompletionCode(completionData.get("completionCode").toString());
            }
        }
        
        return taskRepository.save(task);
    }

    @Override
    @Transactional
    public Task markTaskAsArrived(Long id, Long assignmentId) {
        logger.debug("Marking task with ID: {} as arrived for assignment with ID: {}", id, assignmentId);
        Optional<Task> optionalTask = taskRepository.findByIdAndAssignmentId(id, assignmentId);
        if (optionalTask.isEmpty()) {
            throw new IllegalArgumentException("Task not found or not part of the specified assignment");
        }
        
        Task task = optionalTask.get();
        if (task.getStatus() != TaskStatus.IN_PROGRESS) {
            throw new IllegalStateException("Task must be in IN_PROGRESS status before it can be marked as arrived");
        }
        
        task.setStatus(TaskStatus.ARRIVED);
        task.setActualArrivalTime(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        
        return taskRepository.save(task);
    }

    @Override
    @Transactional
    public Task updateTaskSequence(Long id, Integer sequenceNumber) {
        logger.debug("Updating sequence to {} for task with ID: {}", sequenceNumber, id);
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isEmpty()) {
            throw new IllegalArgumentException("Cannot update sequence for non-existent task");
        }
        
        Task task = optionalTask.get();
        task.setSequenceNumber(sequenceNumber);
        task.setUpdatedAt(LocalDateTime.now());
        
        return taskRepository.save(task);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Task> getTasksByTimeWindowRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        logger.debug("Getting tasks with time windows between {} and {}", startDate, endDate);
        return taskRepository.findByTimeWindowRange(startDate, endDate, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> getTasksByTrackingNumber(String trackingNumber) {
        logger.debug("Getting tasks with tracking number: {}", trackingNumber);
        return taskRepository.findByTrackingNumber(trackingNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> getTasksByPackageId(String packageId) {
        logger.debug("Getting tasks with package ID: {}", packageId);
        return taskRepository.findByPackageId(packageId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countTasksByAssignmentAndStatus(Long assignmentId, TaskStatus status) {
        logger.debug("Counting tasks for assignment with ID: {} with status: {}", assignmentId, status);
        return taskRepository.countByAssignmentIdAndStatus(assignmentId, status);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Task> getTasksBySyncStatus(String syncStatus, Pageable pageable) {
        logger.debug("Getting tasks with sync status: {}", syncStatus);
        return taskRepository.findBySyncStatus(syncStatus, pageable);
    }

    @Override
    @Transactional
    public Task updateTaskSyncStatus(Long id, String syncStatus) {
        logger.debug("Updating sync status to {} for task with ID: {}", syncStatus, id);
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isEmpty()) {
            throw new IllegalArgumentException("Cannot update sync status for non-existent task");
        }
        
        Task task = optionalTask.get();
        task.setSyncStatus(syncStatus);
        task.setUpdatedAt(LocalDateTime.now());
        
        return taskRepository.save(task);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Task> getNextPendingTaskForAssignment(Long assignmentId) {
        logger.debug("Getting next pending task for assignment with ID: {}", assignmentId);
        return taskRepository.findNextPendingTaskForAssignment(assignmentId);
    }

    @Override
    @Transactional
    public boolean deleteTask(Long id) {
        logger.debug("Soft deleting task with ID: {}", id);
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isEmpty()) {
            return false;
        }
        
        Task task = optionalTask.get();
        task.setIsDeleted(true);
        task.setUpdatedAt(LocalDateTime.now());
        taskRepository.save(task);
        
        return true;
    }
}
