package com.exalt.courierservices.management.$1;

import com.exalt.courier.management.assignment.model.Assignment;
import com.exalt.courier.management.assignment.model.AssignmentTask;
import com.exalt.courier.management.assignment.service.AssignmentService;
import com.exalt.courier.management.assignment.service.TaskSequencingService;
import com.exalt.courier.management.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for task sequencing operations.
 */
@RestController
@RequestMapping("/api/sequencing")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Task Sequencing", description = "APIs for optimizing task sequences within assignments")
public class TaskSequencingController {

    private final TaskSequencingService taskSequencingService;
    private final AssignmentService assignmentService;

    @Operation(summary = "Determine optimal sequence for tasks in an assignment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Optimal sequence determined successfully"),
            @ApiResponse(responseCode = "404", description = "Assignment not found")
    })
    @GetMapping("/assignments/{assignmentId}/optimal")
    public ResponseEntity<List<AssignmentTask>> determineOptimalSequence(
            @Parameter(description = "Assignment ID", required = true)
            @PathVariable String assignmentId) {
        log.info("Determining optimal sequence for assignment: {}", assignmentId);
        
        Assignment assignment = assignmentService.getAssignmentByAssignmentId(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with ID: " + assignmentId));
        
        List<AssignmentTask> optimalSequence = taskSequencingService.determineOptimalSequence(assignment);
        return ResponseEntity.ok(optimalSequence);
    }

    @Operation(summary = "Apply an optimal sequence to tasks in an assignment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sequence applied successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid sequence"),
            @ApiResponse(responseCode = "404", description = "Assignment not found")
    })
    @PostMapping("/assignments/{assignmentId}/apply-optimal")
    public ResponseEntity<Assignment> applyOptimalSequence(
            @Parameter(description = "Assignment ID", required = true)
            @PathVariable String assignmentId) {
        log.info("Applying optimal sequence to assignment: {}", assignmentId);
        
        Assignment assignment = assignmentService.getAssignmentByAssignmentId(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with ID: " + assignmentId));
        
        List<AssignmentTask> optimalSequence = taskSequencingService.determineOptimalSequence(assignment);
        assignment = taskSequencingService.applySequence(assignment, optimalSequence);
        
        return ResponseEntity.ok(assignment);
    }

    @Operation(summary = "Apply a custom sequence to tasks in an assignment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sequence applied successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid sequence"),
            @ApiResponse(responseCode = "404", description = "Assignment not found")
    })
    @PostMapping("/assignments/{assignmentId}/apply-custom")
    public ResponseEntity<Assignment> applyCustomSequence(
            @Parameter(description = "Assignment ID", required = true)
            @PathVariable String assignmentId,
            @RequestBody List<AssignmentTask> tasks) {
        log.info("Applying custom sequence to assignment: {}", assignmentId);
        
        Assignment assignment = assignmentService.getAssignmentByAssignmentId(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with ID: " + assignmentId));
        
        if (!taskSequencingService.isValidSequence(assignment, tasks)) {
            return ResponseEntity.badRequest().build();
        }
        
        assignment = taskSequencingService.applySequence(assignment, tasks);
        return ResponseEntity.ok(assignment);
    }

    @Operation(summary = "Validate a task sequence for an assignment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sequence validation result"),
            @ApiResponse(responseCode = "404", description = "Assignment not found")
    })
    @PostMapping("/assignments/{assignmentId}/validate")
    public ResponseEntity<Map<String, Boolean>> validateSequence(
            @Parameter(description = "Assignment ID", required = true)
            @PathVariable String assignmentId,
            @RequestBody List<AssignmentTask> tasks) {
        log.info("Validating sequence for assignment: {}", assignmentId);
        
        Assignment assignment = assignmentService.getAssignmentByAssignmentId(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with ID: " + assignmentId));
        
        boolean isValid = taskSequencingService.isValidSequence(assignment, tasks);
        
        Map<String, Boolean> result = new HashMap<>();
        result.put("valid", isValid);
        
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Estimate travel metrics for a task sequence")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Metrics estimated successfully"),
            @ApiResponse(responseCode = "404", description = "Assignment not found")
    })
    @GetMapping("/assignments/{assignmentId}/tasks/travel-metrics")
    public ResponseEntity<Map<String, Object>> estimateTravelMetrics(
            @Parameter(description = "Assignment ID", required = true)
            @PathVariable String assignmentId) {
        log.info("Estimating travel metrics for assignment: {}", assignmentId);
        
        Assignment assignment = assignmentService.getAssignmentByAssignmentId(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with ID: " + assignmentId));
        
        List<AssignmentTask> tasks = taskSequencingService.determineOptimalSequence(assignment);
        
        double distance = taskSequencingService.estimateDistance(tasks);
        int travelTime = taskSequencingService.estimateTravelTime(tasks);
        
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("distance", distance);
        metrics.put("travelTime", travelTime);
        metrics.put("unit", "km");
        metrics.put("timeUnit", "minutes");
        
        return ResponseEntity.ok(metrics);
    }

    @Operation(summary = "Check if tasks can be completed within their time windows")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Feasibility check completed"),
            @ApiResponse(responseCode = "404", description = "Assignment not found")
    })
    @GetMapping("/assignments/{assignmentId}/feasible")
    public ResponseEntity<Map<String, Boolean>> checkTimeWindowFeasibility(
            @Parameter(description = "Assignment ID", required = true)
            @PathVariable String assignmentId,
            @Parameter(description = "Start time (optional)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime) {
        log.info("Checking time window feasibility for assignment: {}", assignmentId);
        
        Assignment assignment = assignmentService.getAssignmentByAssignmentId(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with ID: " + assignmentId));
        
        List<AssignmentTask> tasks = taskSequencingService.determineOptimalSequence(assignment);
        
        // Use current time if startTime is not provided
        LocalDateTime effectiveStartTime = startTime != null ? startTime : LocalDateTime.now();
        
        boolean canComplete = taskSequencingService.canCompleteWithinTimeWindows(tasks, effectiveStartTime);
        
        Map<String, Boolean> result = new HashMap<>();
        result.put("feasible", canComplete);
        
        return ResponseEntity.ok(result);
    }
} 