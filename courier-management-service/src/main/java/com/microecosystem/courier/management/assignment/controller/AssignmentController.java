package com.exalt.courierservices.management.$1;

import com.exalt.courier.management.assignment.model.Assignment;
import com.exalt.courier.management.assignment.model.AssignmentStatus;
import com.exalt.courier.management.assignment.service.AssignmentService;
import com.exalt.courier.management.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST controller for Assignment operations.
 */
@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Assignment Management", description = "APIs for managing courier assignments")
public class AssignmentController {

    private final AssignmentService assignmentService;

    @Operation(summary = "Create a new assignment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Assignment created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public ResponseEntity<Assignment> createAssignment(@Valid @RequestBody Assignment assignment) {
        log.info("Creating new assignment");
        Assignment createdAssignment = assignmentService.createAssignment(assignment);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAssignment);
    }

    @Operation(summary = "Get an assignment by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the assignment"),
            @ApiResponse(responseCode = "404", description = "Assignment not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Assignment> getAssignmentById(
            @Parameter(description = "Assignment ID", required = true)
            @PathVariable String id) {
        log.info("Retrieving assignment with ID: {}", id);
        return assignmentService.getAssignmentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get an assignment by assignmentId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the assignment"),
            @ApiResponse(responseCode = "404", description = "Assignment not found")
    })
    @GetMapping("/by-assignment-id/{assignmentId}")
    public ResponseEntity<Assignment> getAssignmentByAssignmentId(
            @Parameter(description = "Assignment unique ID", required = true)
            @PathVariable String assignmentId) {
        log.info("Retrieving assignment with assignment ID: {}", assignmentId);
        return assignmentService.getAssignmentByAssignmentId(assignmentId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get all assignments with pagination")
    @GetMapping
    public ResponseEntity<Page<Assignment>> getAllAssignments(Pageable pageable) {
        log.info("Retrieving all assignments with pagination");
        return ResponseEntity.ok(assignmentService.getAllAssignments(pageable));
    }

    @Operation(summary = "Update an assignment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Assignment updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Assignment not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Assignment> updateAssignment(
            @Parameter(description = "Assignment ID", required = true)
            @PathVariable String id,
            @Valid @RequestBody Assignment assignment) {
        log.info("Updating assignment with ID: {}", id);
        
        Optional<Assignment> existingAssignment = assignmentService.getAssignmentById(id);
        if (existingAssignment.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        assignment.setId(id);
        Assignment updatedAssignment = assignmentService.updateAssignment(assignment);
        return ResponseEntity.ok(updatedAssignment);
    }

    @Operation(summary = "Delete an assignment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Assignment deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Assignment not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAssignment(
            @Parameter(description = "Assignment ID", required = true)
            @PathVariable String id) {
        log.info("Deleting assignment with ID: {}", id);
        
        if (assignmentService.deleteAssignment(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Assign an assignment to a courier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Assignment assigned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Assignment or courier not found")
    })
    @PutMapping("/{assignmentId}/assign/{courierId}")
    public ResponseEntity<Assignment> assignToCourier(
            @Parameter(description = "Assignment ID", required = true)
            @PathVariable String assignmentId,
            @Parameter(description = "Courier ID", required = true)
            @PathVariable String courierId) {
        log.info("Assigning assignment {} to courier {}", assignmentId, courierId);
        
        Assignment updatedAssignment = assignmentService.assignToCourier(assignmentId, courierId);
        return ResponseEntity.ok(updatedAssignment);
    }

    @Operation(summary = "Accept an assignment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Assignment accepted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid state transition"),
            @ApiResponse(responseCode = "404", description = "Assignment not found")
    })
    @PutMapping("/{assignmentId}/accept")
    public ResponseEntity<Assignment> acceptAssignment(
            @Parameter(description = "Assignment ID", required = true)
            @PathVariable String assignmentId) {
        log.info("Accepting assignment {}", assignmentId);
        
        Assignment updatedAssignment = assignmentService.acceptAssignment(assignmentId);
        return ResponseEntity.ok(updatedAssignment);
    }

    @Operation(summary = "Start an assignment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Assignment started successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid state transition"),
            @ApiResponse(responseCode = "404", description = "Assignment not found")
    })
    @PutMapping("/{assignmentId}/start")
    public ResponseEntity<Assignment> startAssignment(
            @Parameter(description = "Assignment ID", required = true)
            @PathVariable String assignmentId) {
        log.info("Starting assignment {}", assignmentId);
        
        Assignment updatedAssignment = assignmentService.startAssignment(assignmentId);
        return ResponseEntity.ok(updatedAssignment);
    }

    @Operation(summary = "Complete an assignment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Assignment completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid state transition"),
            @ApiResponse(responseCode = "404", description = "Assignment not found")
    })
    @PutMapping("/{assignmentId}/complete")
    public ResponseEntity<Assignment> completeAssignment(
            @Parameter(description = "Assignment ID", required = true)
            @PathVariable String assignmentId) {
        log.info("Completing assignment {}", assignmentId);
        
        Assignment updatedAssignment = assignmentService.completeAssignment(assignmentId);
        return ResponseEntity.ok(updatedAssignment);
    }

    @Operation(summary = "Cancel an assignment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Assignment cancelled successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid state transition"),
            @ApiResponse(responseCode = "404", description = "Assignment not found")
    })
    @PutMapping("/{assignmentId}/cancel")
    public ResponseEntity<Assignment> cancelAssignment(
            @Parameter(description = "Assignment ID", required = true)
            @PathVariable String assignmentId,
            @RequestBody Map<String, String> cancellationRequest) {
        
        String reason = cancellationRequest.getOrDefault("reason", "No reason provided");
        log.info("Cancelling assignment {} with reason: {}", assignmentId, reason);
        
        Assignment updatedAssignment = assignmentService.cancelAssignment(assignmentId, reason);
        return ResponseEntity.ok(updatedAssignment);
    }

    @Operation(summary = "Reject an assignment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Assignment rejected successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid state transition"),
            @ApiResponse(responseCode = "404", description = "Assignment not found")
    })
    @PutMapping("/{assignmentId}/reject")
    public ResponseEntity<Assignment> rejectAssignment(
            @Parameter(description = "Assignment ID", required = true)
            @PathVariable String assignmentId,
            @RequestBody Map<String, String> rejectionRequest) {
        
        String reason = rejectionRequest.getOrDefault("reason", "No reason provided");
        log.info("Rejecting assignment {} with reason: {}", assignmentId, reason);
        
        Assignment updatedAssignment = assignmentService.rejectAssignment(assignmentId, reason);
        return ResponseEntity.ok(updatedAssignment);
    }

    @Operation(summary = "Update assignment status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid status or state transition"),
            @ApiResponse(responseCode = "404", description = "Assignment not found")
    })
    @PutMapping("/{assignmentId}/status/{status}")
    public ResponseEntity<Assignment> updateAssignmentStatus(
            @Parameter(description = "Assignment ID", required = true)
            @PathVariable String assignmentId,
            @Parameter(description = "New status", required = true)
            @PathVariable AssignmentStatus status) {
        log.info("Updating assignment {} status to {}", assignmentId, status);
        
        Assignment updatedAssignment = assignmentService.updateAssignmentStatus(assignmentId, status);
        return ResponseEntity.ok(updatedAssignment);
    }

    @Operation(summary = "Get assignments by courier")
    @GetMapping("/courier/{courierId}")
    public ResponseEntity<Page<Assignment>> getAssignmentsByCourier(
            @Parameter(description = "Courier ID", required = true)
            @PathVariable String courierId,
            Pageable pageable) {
        log.info("Retrieving assignments for courier {}", courierId);
        
        return ResponseEntity.ok(assignmentService.getAssignmentsByCourier(courierId, pageable));
    }

    @Operation(summary = "Get assignments by courier and status")
    @GetMapping("/courier/{courierId}/status/{status}")
    public ResponseEntity<Page<Assignment>> getAssignmentsByCourierAndStatus(
            @Parameter(description = "Courier ID", required = true)
            @PathVariable String courierId,
            @Parameter(description = "Assignment status", required = true)
            @PathVariable AssignmentStatus status,
            Pageable pageable) {
        log.info("Retrieving {} assignments for courier {}", status, courierId);
        
        return ResponseEntity.ok(assignmentService.getAssignmentsByCourierAndStatus(courierId, status, pageable));
    }

    @Operation(summary = "Get active assignments by courier")
    @GetMapping("/courier/{courierId}/active")
    public ResponseEntity<List<Assignment>> getActiveAssignmentsByCourier(
            @Parameter(description = "Courier ID", required = true)
            @PathVariable String courierId) {
        log.info("Retrieving active assignments for courier {}", courierId);
        
        return ResponseEntity.ok(assignmentService.getActiveAssignmentsByCourier(courierId));
    }

    @Operation(summary = "Get assignments by status")
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<Assignment>> getAssignmentsByStatus(
            @Parameter(description = "Assignment status", required = true)
            @PathVariable AssignmentStatus status,
            Pageable pageable) {
        log.info("Retrieving {} assignments", status);
        
        return ResponseEntity.ok(assignmentService.getAssignmentsByStatus(status, pageable));
    }

    @Operation(summary = "Get overdue assignments")
    @GetMapping("/overdue")
    public ResponseEntity<List<Assignment>> getOverdueAssignments() {
        log.info("Retrieving overdue assignments");
        
        return ResponseEntity.ok(assignmentService.getOverdueAssignments());
    }

    @Operation(summary = "Get assignments by order ID")
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<Assignment>> getAssignmentsByOrderId(
            @Parameter(description = "Order ID", required = true)
            @PathVariable String orderId) {
        log.info("Retrieving assignments for order {}", orderId);
        
        return ResponseEntity.ok(assignmentService.getAssignmentsByOrderId(orderId));
    }

    @Operation(summary = "Search assignments by assignment ID pattern")
    @GetMapping("/search")
    public ResponseEntity<List<Assignment>> searchAssignmentsByAssignmentId(
            @Parameter(description = "Assignment ID pattern", required = true)
            @RequestParam("id") String assignmentIdPattern) {
        log.info("Searching assignments with pattern {}", assignmentIdPattern);
        
        return ResponseEntity.ok(assignmentService.searchAssignmentsByAssignmentId(assignmentIdPattern));
    }

    @Operation(summary = "Generate a unique assignment ID")
    @GetMapping("/generate-id")
    public ResponseEntity<String> generateAssignmentId() {
        log.info("Generating new assignment ID");
        
        return ResponseEntity.ok(assignmentService.generateAssignmentId());
    }
} 