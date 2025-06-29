package com.exalt.courierservices.management.$1;

import com.exalt.courier.management.assignment.dto.AssignmentTaskDTO;
import com.exalt.courier.management.assignment.mapper.AssignmentTaskMapper;
import com.exalt.courier.management.assignment.model.AssignmentTask;
import com.exalt.courier.management.assignment.model.TaskStatus;
import com.exalt.courier.management.assignment.model.TaskType;
import com.exalt.courier.management.assignment.service.AssignmentTaskService;
import com.exalt.courier.management.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * REST controller for AssignmentTask operations.
 */
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Assignment Task Management", description = "APIs for managing courier assignment tasks")
public class AssignmentTaskController {

    private final AssignmentTaskService taskService;
    private final AssignmentTaskMapper taskMapper;

    @Operation(summary = "Create a new task for an assignment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Task created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Assignment not found")
    })
    @PostMapping("/assignment/{assignmentId}")
    public ResponseEntity<AssignmentTask> createTask(
            @Parameter(description = "Assignment ID", required = true)
            @PathVariable String assignmentId,
            @Valid @RequestBody AssignmentTask task) {
        log.info("Creating new task for assignment: {}", assignmentId);
        
        AssignmentTask createdTask = taskService.createTask(assignmentId, task);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @Operation(summary = "Update an existing task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @PutMapping("/{taskId}")
    public ResponseEntity<AssignmentTask> updateTask(
            @Parameter(description = "Task ID", required = true)
            @PathVariable String taskId,
            @Valid @RequestBody AssignmentTask task) {
        log.info("Updating task with ID: {}", taskId);
        
        AssignmentTask updatedTask = taskService.updateTask(taskId, task);
        return ResponseEntity.ok(updatedTask);
    }

    @Operation(summary = "Get a task by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the task"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @GetMapping("/{taskId}")
    public ResponseEntity<AssignmentTask> getTaskById(
            @Parameter(description = "Task ID", required = true)
            @PathVariable String taskId) {
        log.info("Retrieving task with ID: {}", taskId);
        
        return taskService.getTaskById(taskId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get all tasks for an assignment")
    @GetMapping("/assignment/{assignmentId}")
    public ResponseEntity<List<AssignmentTask>> getTasksByAssignment(
            @Parameter(description = "Assignment ID", required = true)
            @PathVariable String assignmentId) {
        log.info("Retrieving tasks for assignment: {}", assignmentId);
        
        List<AssignmentTask> tasks = taskService.getTasksByAssignment(assignmentId);
        return ResponseEntity.ok(tasks);
    }

    @Operation(summary = "Get all tasks for an assignment with pagination")
    @GetMapping("/assignment/{assignmentId}/paged")
    public ResponseEntity<Page<AssignmentTask>> getTasksByAssignmentPaged(
            @Parameter(description = "Assignment ID", required = true)
            @PathVariable String assignmentId,
            Pageable pageable) {
        log.info("Retrieving tasks for assignment with pagination: {}", assignmentId);
        
        Page<AssignmentTask> tasks = taskService.getTasksByAssignment(assignmentId, pageable);
        return ResponseEntity.ok(tasks);
    }

    @Operation(summary = "Get tasks for an assignment ordered by sequence")
    @GetMapping("/assignment/{assignmentId}/ordered")
    public ResponseEntity<List<AssignmentTask>> getTasksByAssignmentOrderedBySequence(
            @Parameter(description = "Assignment ID", required = true)
            @PathVariable String assignmentId) {
        log.info("Retrieving ordered tasks for assignment: {}", assignmentId);
        
        List<AssignmentTask> tasks = taskService.getTasksByAssignmentOrderedBySequence(assignmentId);
        return ResponseEntity.ok(tasks);
    }

    @Operation(summary = "Get tasks for an assignment with a specific status")
    @GetMapping("/assignment/{assignmentId}/status/{status}")
    public ResponseEntity<List<AssignmentTask>> getTasksByAssignmentAndStatus(
            @Parameter(description = "Assignment ID", required = true)
            @PathVariable String assignmentId,
            @Parameter(description = "Task status", required = true)
            @PathVariable TaskStatus status) {
        log.info("Retrieving {} tasks for assignment: {}", status, assignmentId);
        
        List<AssignmentTask> tasks = taskService.getTasksByAssignmentAndStatus(assignmentId, status);
        return ResponseEntity.ok(tasks);
    }

    @Operation(summary = "Get tasks for an assignment with a specific type")
    @GetMapping("/assignment/{assignmentId}/type/{type}")
    public ResponseEntity<List<AssignmentTask>> getTasksByAssignmentAndType(
            @Parameter(description = "Assignment ID", required = true)
            @PathVariable String assignmentId,
            @Parameter(description = "Task type", required = true)
            @PathVariable TaskType type) {
        log.info("Retrieving {} tasks for assignment: {}", type, assignmentId);
        
        List<AssignmentTask> tasks = taskService.getTasksByAssignmentAndType(assignmentId, type);
        return ResponseEntity.ok(tasks);
    }

    @Operation(summary = "Update task status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid status or state transition"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @PutMapping("/{taskId}/status/{status}")
    public ResponseEntity<AssignmentTask> updateTaskStatus(
            @Parameter(description = "Task ID", required = true)
            @PathVariable String taskId,
            @Parameter(description = "New status", required = true)
            @PathVariable TaskStatus status) {
        log.info("Updating task {} status to {}", taskId, status);
        
        AssignmentTask updatedTask = taskService.updateTaskStatus(taskId, status);
        return ResponseEntity.ok(updatedTask);
    }

    @Operation(summary = "Mark a task as completed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid state transition"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @PutMapping("/{taskId}/complete")
    public ResponseEntity<AssignmentTask> completeTask(
            @Parameter(description = "Task ID", required = true)
            @PathVariable String taskId) {
        log.info("Completing task: {}", taskId);
        
        AssignmentTask updatedTask = taskService.completeTask(taskId);
        return ResponseEntity.ok(updatedTask);
    }

    @Operation(summary = "Mark a task as failed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task marked as failed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid state transition"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @PutMapping("/{taskId}/fail")
    public ResponseEntity<AssignmentTask> failTask(
            @Parameter(description = "Task ID", required = true)
            @PathVariable String taskId,
            @RequestBody Map<String, String> failureRequest) {
        
        String reason = failureRequest.getOrDefault("reason", "No reason provided");
        log.info("Marking task {} as failed with reason: {}", taskId, reason);
        
        AssignmentTask updatedTask = taskService.failTask(taskId, reason);
        return ResponseEntity.ok(updatedTask);
    }

    @Operation(summary = "Delete a task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Task deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Task cannot be deleted in current state"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(
            @Parameter(description = "Task ID", required = true)
            @PathVariable String taskId) {
        log.info("Deleting task with ID: {}", taskId);
        
        if (taskService.deleteTask(taskId)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Get tasks scheduled within a time window")
    @GetMapping("/time-window")
    public ResponseEntity<List<AssignmentTask>> getTasksInTimeWindow(
            @Parameter(description = "Start time", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "End time", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        log.info("Retrieving tasks in time window from {} to {}", startTime, endTime);
        
        List<AssignmentTask> tasks = taskService.getTasksInTimeWindow(startTime, endTime);
        return ResponseEntity.ok(tasks);
    }

    @Operation(summary = "Get overdue tasks for an assignment")
    @GetMapping("/assignment/{assignmentId}/overdue")
    public ResponseEntity<List<AssignmentTask>> getOverdueTasks(
            @Parameter(description = "Assignment ID", required = true)
            @PathVariable String assignmentId) {
        log.info("Retrieving overdue tasks for assignment: {}", assignmentId);
        
        List<AssignmentTask> tasks = taskService.getOverdueTasks(assignmentId);
        return ResponseEntity.ok(tasks);
    }

    @Operation(summary = "Resequence tasks for an assignment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks resequenced successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Assignment or tasks not found")
    })
    @PutMapping("/assignment/{assignmentId}/resequence")
    public ResponseEntity<List<AssignmentTask>> resequenceTasks(
            @Parameter(description = "Assignment ID", required = true)
            @PathVariable String assignmentId,
            @RequestBody List<String> taskIds) {
        log.info("Resequencing tasks for assignment: {}", assignmentId);
        
        List<AssignmentTask> resequencedTasks = taskService.resequenceTasks(assignmentId, taskIds);
        return ResponseEntity.ok(resequencedTasks);
    }
} 