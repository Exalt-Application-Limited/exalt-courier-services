package com.gogidix.courier.branch.controller.api;

import com.gogidix.courier.branch.model.AssignmentStatus;
import com.gogidix.courier.branch.model.CourierAssignment;
import com.gogidix.courier.branch.model.corporate.Branch;
import com.gogidix.courier.branch.service.BranchService;
import com.gogidix.courier.branch.service.CourierAssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/branch/assignments")
public class CourierAssignmentController {

    private final CourierAssignmentService courierAssignmentService;
    private final BranchService branchService;

    @Autowired
    public CourierAssignmentController(CourierAssignmentService courierAssignmentService,
                                       BranchService branchService) {
        this.courierAssignmentService = courierAssignmentService;
        this.branchService = branchService;
    }

    @GetMapping
    public ResponseEntity<List<CourierAssignment>> getAllCourierAssignments() {
        List<CourierAssignment> assignments = courierAssignmentService.getAllCourierAssignments();
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourierAssignment> getCourierAssignmentById(@PathVariable Long id) {
        return courierAssignmentService.getCourierAssignmentById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "Courier assignment not found with id: " + id));
    }

    @GetMapping("/search/branch/{branchId}")
    public ResponseEntity<List<CourierAssignment>> findCourierAssignmentsByBranchId(@PathVariable Long branchId) {
        if (!branchService.existsById(branchId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "Branch not found with id: " + branchId);
        }
        
        List<CourierAssignment> assignments = courierAssignmentService.findCourierAssignmentsByBranchId(branchId);
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/search/courier/{courierId}")
    public ResponseEntity<List<CourierAssignment>> findCourierAssignmentsByCourierId(@PathVariable Long courierId) {
        List<CourierAssignment> assignments = courierAssignmentService.findCourierAssignmentsByCourierId(courierId);
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/search/status/{status}")
    public ResponseEntity<List<CourierAssignment>> findCourierAssignmentsByStatus(@PathVariable AssignmentStatus status) {
        List<CourierAssignment> assignments = courierAssignmentService.findCourierAssignmentsByStatus(status);
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/search/branch/{branchId}/status/{status}")
    public ResponseEntity<List<CourierAssignment>> findCourierAssignmentsByBranchAndStatus(
            @PathVariable Long branchId, 
            @PathVariable AssignmentStatus status) {
        
        Branch branch = branchService.getBranchById(branchId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "Branch not found with id: " + branchId));
        
        List<CourierAssignment> assignments = courierAssignmentService.findCourierAssignmentsByBranchAndStatus(branch, status);
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/search/courier/{courierId}/status/{status}")
    public ResponseEntity<List<CourierAssignment>> findCourierAssignmentsByCourierAndStatus(
            @PathVariable Long courierId, 
            @PathVariable AssignmentStatus status) {
        
        List<CourierAssignment> assignments = courierAssignmentService.findCourierAssignmentsByCourierAndStatus(courierId, status);
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/search/date-range")
    public ResponseEntity<List<CourierAssignment>> findCourierAssignmentsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        List<CourierAssignment> assignments = courierAssignmentService.findCourierAssignmentsByDateRange(startDate, endDate);
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/search/branch/{branchId}/date-range")
    public ResponseEntity<List<CourierAssignment>> findCourierAssignmentsByBranchAndDateRange(
            @PathVariable Long branchId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        if (!branchService.existsById(branchId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "Branch not found with id: " + branchId);
        }
        
        List<CourierAssignment> assignments = courierAssignmentService.findCourierAssignmentsByBranchAndDateRange(branchId, startDate, endDate);
        return ResponseEntity.ok(assignments);
    }

    @PostMapping
    public ResponseEntity<CourierAssignment> createCourierAssignment(@Validated @RequestBody CourierAssignment courierAssignment) {
        // Verify that the branch exists if it's included
        if (courierAssignment.getBranch() != null && courierAssignment.getBranch().getId() != null) {
            Long branchId = courierAssignment.getBranch().getId();
            if (!branchService.existsById(branchId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                        "Branch not found with id: " + branchId);
            }
        }
        
        CourierAssignment createdAssignment = courierAssignmentService.saveCourierAssignment(courierAssignment);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAssignment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourierAssignment> updateCourierAssignment(
            @PathVariable Long id, 
            @Validated @RequestBody CourierAssignment courierAssignment) {
        
        if (!courierAssignmentService.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "Courier assignment not found with id: " + id);
        }
        
        // Verify that the branch exists if it's included
        if (courierAssignment.getBranch() != null && courierAssignment.getBranch().getId() != null) {
            Long branchId = courierAssignment.getBranch().getId();
            if (!branchService.existsById(branchId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                        "Branch not found with id: " + branchId);
            }
        }
        
        courierAssignment.setId(id);
        CourierAssignment updatedAssignment = courierAssignmentService.saveCourierAssignment(courierAssignment);
        return ResponseEntity.ok(updatedAssignment);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<CourierAssignment> updateCourierAssignmentStatus(
            @PathVariable Long id, 
            @RequestParam AssignmentStatus status) {
        
        if (!courierAssignmentService.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "Courier assignment not found with id: " + id);
        }
        
        CourierAssignment updatedAssignment = courierAssignmentService.updateCourierAssignmentStatus(id, status);
        return ResponseEntity.ok(updatedAssignment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourierAssignment(@PathVariable Long id) {
        if (!courierAssignmentService.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "Courier assignment not found with id: " + id);
        }
        
        courierAssignmentService.deleteCourierAssignment(id);
        return ResponseEntity.noContent().build();
    }
}
