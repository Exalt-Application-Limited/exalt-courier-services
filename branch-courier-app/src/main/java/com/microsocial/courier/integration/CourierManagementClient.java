package com.exalt.courier.courier.integration;

import com.microsocial.courier.model.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Feign client for integrating with Courier Management Service
 */
@FeignClient(name = "courier-management", url = "${branch-courier-app.courier-management.integration.base-url}", 
        fallback = CourierManagementClientFallback.class)
public interface CourierManagementClient {

    //
    // Assignment APIs
    //
    
    @GetMapping("/api/assignments")
    PagedResponseDTO<AssignmentDTO> getAssignments(
            @RequestParam(value = "branchId", required = false) Long branchId,
            @RequestParam(value = "courierId", required = false) Long courierId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "sort", defaultValue = "createdAt,desc") String sort);
    
    @GetMapping("/api/assignments/{id}")
    AssignmentDTO getAssignment(@PathVariable("id") Long id);
    
    @PostMapping("/api/assignments")
    AssignmentDTO createAssignment(@RequestBody AssignmentDTO assignmentDTO);
    
    @PutMapping("/api/assignments/{id}")
    AssignmentDTO updateAssignment(@PathVariable("id") Long id, @RequestBody AssignmentDTO assignmentDTO);
    
    @PutMapping("/api/assignments/{id}/status")
    AssignmentDTO updateAssignmentStatus(
            @PathVariable("id") Long id, 
            @RequestParam("status") String status);
    
    @GetMapping("/api/assignments/by-branch/{branchId}/count")
    Long countAssignmentsByBranchAndStatus(
            @PathVariable("branchId") Long branchId,
            @RequestParam(value = "status", required = false) String status);
    
    //
    // Assignment Task APIs
    //
    
    @GetMapping("/api/assignments/{assignmentId}/tasks")
    List<AssignmentTaskDTO> getAssignmentTasks(@PathVariable("assignmentId") Long assignmentId);
    
    @GetMapping("/api/assignments/{assignmentId}/tasks/{taskId}")
    AssignmentTaskDTO getAssignmentTask(
            @PathVariable("assignmentId") Long assignmentId,
            @PathVariable("taskId") Long taskId);
    
    @PostMapping("/api/assignments/{assignmentId}/tasks")
    AssignmentTaskDTO createAssignmentTask(
            @PathVariable("assignmentId") Long assignmentId,
            @RequestBody AssignmentTaskDTO taskDTO);
    
    @PutMapping("/api/assignments/{assignmentId}/tasks/{taskId}")
    AssignmentTaskDTO updateAssignmentTask(
            @PathVariable("assignmentId") Long assignmentId,
            @PathVariable("taskId") Long taskId,
            @RequestBody AssignmentTaskDTO taskDTO);
    
    @PutMapping("/api/assignments/{assignmentId}/tasks/{taskId}/status")
    AssignmentTaskDTO updateTaskStatus(
            @PathVariable("assignmentId") Long assignmentId,
            @PathVariable("taskId") Long taskId,
            @RequestParam("status") String status);
    
    @GetMapping("/api/assignments/{assignmentId}/tasks/optimal-sequence")
    List<AssignmentTaskDTO> getOptimalTaskSequence(@PathVariable("assignmentId") Long assignmentId);
    
    @PostMapping("/api/assignments/{assignmentId}/tasks/apply-optimal-sequence")
    List<AssignmentTaskDTO> applyOptimalTaskSequence(@PathVariable("assignmentId") Long assignmentId);
    
    //
    // Courier APIs
    //
    
    @GetMapping("/api/couriers")
    PagedResponseDTO<CourierDTO> getCouriers(
            @RequestParam(value = "branchId", required = false) Long branchId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "available", required = false) Boolean available,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size);
    
    @GetMapping("/api/couriers/{id}")
    CourierDTO getCourier(@PathVariable("id") Long id);
    
    @PostMapping("/api/couriers")
    CourierDTO createCourier(@RequestBody CourierDTO courierDTO);
    
    @PutMapping("/api/couriers/{id}")
    CourierDTO updateCourier(@PathVariable("id") Long id, @RequestBody CourierDTO courierDTO);
    
    @PutMapping("/api/couriers/{id}/status")
    CourierDTO updateCourierStatus(
            @PathVariable("id") Long id, 
            @RequestParam("status") String status);
    
    @PutMapping("/api/couriers/{id}/location")
    CourierDTO updateCourierLocation(
            @PathVariable("id") Long id,
            @RequestParam("latitude") Double latitude,
            @RequestParam("longitude") Double longitude);
    
    @GetMapping("/api/couriers/branch/{branchId}/count")
    Long countCouriersByBranchAndStatus(
            @PathVariable("branchId") Long branchId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "available", required = false) Boolean available);
    
    //
    // Performance Metrics APIs
    //
    
    @GetMapping("/api/metrics/courier/{courierId}")
    List<PerformanceMetricDTO> getCourierMetrics(
            @PathVariable("courierId") Long courierId,
            @RequestParam(value = "metricType", required = false) String metricType,
            @RequestParam(value = "startDate", required = false) LocalDateTime startDate,
            @RequestParam(value = "endDate", required = false) LocalDateTime endDate);
    
    @GetMapping("/api/metrics/branch/{branchId}")
    List<PerformanceMetricDTO> getBranchMetrics(
            @PathVariable("branchId") Long branchId,
            @RequestParam(value = "metricType", required = false) String metricType,
            @RequestParam(value = "startDate", required = false) LocalDateTime startDate,
            @RequestParam(value = "endDate", required = false) LocalDateTime endDate);
}