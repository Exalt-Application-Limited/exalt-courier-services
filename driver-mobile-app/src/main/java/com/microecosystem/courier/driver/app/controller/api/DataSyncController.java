package com.microecosystem.courier.driver.app.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST API controller for data synchronization services.
 * This controller provides endpoints for synchronizing assignments, tasks, and configuration.
 */
@RestController
@RequestMapping("/api/v1/data")
@Tag(name = "Data Synchronization", description = "APIs for data synchronization between mobile devices and server")
public class DataSyncController {

    private static final Logger logger = LoggerFactory.getLogger(DataSyncController.class);
    
    /**
     * Get all assignments for a driver.
     * 
     * @param driverId The ID of the driver
     * @param includeCompleted Whether to include completed assignments
     * @return List of assignments
     */
    @GetMapping("/assignments/{driverId}")
    @Operation(summary = "Get driver assignments", description = "Retrieves all assignments for a specific driver")
    @PreAuthorize("hasRole('DRIVER') and @securityService.isCurrentDriver(#driverId)")
    public ResponseEntity<Map<String, Object>> getAssignments(
            @Parameter(description = "Driver ID", required = true)
            @PathVariable String driverId,
            @Parameter(description = "Include completed assignments")
            @RequestParam(defaultValue = "false") boolean includeCompleted) {
        
        logger.info("Getting assignments for driver {}, includeCompleted={}", driverId, includeCompleted);
        
        // In a real implementation, this would fetch from a database
        // For now, returning simulated assignments
        
        List<Map<String, Object>> assignments = generateSampleAssignments(driverId, includeCompleted);
        
        Map<String, Object> response = new HashMap<>();
        response.put("driverId", driverId);
        response.put("assignments", assignments);
        response.put("lastUpdated", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get detailed information for a specific assignment.
     * 
     * @param assignmentId The ID of the assignment
     * @return Assignment details
     */
    @GetMapping("/assignments/detail/{assignmentId}")
    @Operation(summary = "Get assignment details", description = "Retrieves detailed information for a specific assignment")
    @PreAuthorize("hasRole('DRIVER') and @securityService.canAccessAssignment(#assignmentId)")
    public ResponseEntity<Map<String, Object>> getAssignmentDetail(
            @Parameter(description = "Assignment ID", required = true)
            @PathVariable String assignmentId) {
            
        logger.info("Getting details for assignment {}", assignmentId);
        
        // In a real implementation, this would fetch from a database
        // For now, returning a simulated assignment detail
        
        Map<String, Object> assignment = generateSampleAssignmentDetail(assignmentId);
        
        if (assignment == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(assignment);
    }
    
    /**
     * Update the status of an assignment task.
     * 
     * @param taskId The ID of the task
     * @param status The new status
     * @return Result of the update operation
     */
    @PutMapping("/tasks/{taskId}/status")
    @Operation(summary = "Update task status", description = "Updates the status of a specific task")
    @PreAuthorize("hasRole('DRIVER') and @securityService.canUpdateTask(#taskId)")
    public ResponseEntity<Map<String, Object>> updateTaskStatus(
            @Parameter(description = "Task ID", required = true)
            @PathVariable String taskId,
            @Parameter(description = "New status", required = true)
            @RequestBody Map<String, String> status) {
        
        logger.info("Updating status for task {} to {}", taskId, status.get("status"));
        
        // In a real implementation, this would update a database
        // For now, returning a simulated success response
        
        Map<String, Object> response = new HashMap<>();
        response.put("taskId", taskId);
        response.put("status", status.get("status"));
        response.put("success", true);
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get configuration settings for the mobile app.
     * 
     * @param deviceId The ID of the mobile device
     * @return Configuration settings
     */
    @GetMapping("/config/{deviceId}")
    @Operation(summary = "Get app configuration", description = "Retrieves configuration settings for the mobile app")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<Map<String, Object>> getConfiguration(
            @Parameter(description = "Device ID", required = true)
            @PathVariable String deviceId) {
            
        logger.info("Getting configuration for device {}", deviceId);
        
        // In a real implementation, this would fetch from a database
        // For now, returning simulated configuration
        
        Map<String, Object> config = new HashMap<>();
        config.put("syncIntervalMinutes", 15);
        config.put("locationTrackingIntervalSeconds", 60);
        config.put("batteryOptimization", true);
        config.put("offlineDataRetentionDays", 7);
        config.put("maxCacheSize", "100MB");
        config.put("features", Map.of(
            "navigationEnabled", true,
            "photoUploadEnabled", true,
            "signatureCapture", true,
            "barcodeScanning", true
        ));
        
        Map<String, Object> response = new HashMap<>();
        response.put("deviceId", deviceId);
        response.put("config", config);
        response.put("lastUpdated", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }
    
    // Helper methods to generate sample data
    
    private List<Map<String, Object>> generateSampleAssignments(String driverId, boolean includeCompleted) {
        List<Map<String, Object>> assignments = new ArrayList<>();
        
        // Add some sample assignments
        for (int i = 1; i <= 5; i++) {
            Map<String, Object> assignment = new HashMap<>();
            assignment.put("id", "A" + i + "-" + driverId);
            assignment.put("title", "Delivery Route #" + i);
            assignment.put("priority", i <= 2 ? "HIGH" : (i <= 4 ? "MEDIUM" : "LOW"));
            assignment.put("taskCount", 3 + i);
            assignment.put("status", i <= 2 ? "IN_PROGRESS" : (i == 3 ? "PENDING" : "COMPLETED"));
            assignment.put("startTime", System.currentTimeMillis() - (i * 3600000));
            assignment.put("estimatedEndTime", System.currentTimeMillis() + ((6 - i) * 3600000));
            
            // Only include completed assignments if requested
            if (!assignment.get("status").equals("COMPLETED") || includeCompleted) {
                assignments.add(assignment);
            }
        }
        
        return assignments;
    }
    
    private Map<String, Object> generateSampleAssignmentDetail(String assignmentId) {
        // Extract the index from the assignment ID
        int index;
        try {
            index = Integer.parseInt(assignmentId.substring(1, 2));
        } catch (Exception e) {
            // Invalid assignment ID format
            return null;
        }
        
        Map<String, Object> assignment = new HashMap<>();
        assignment.put("id", assignmentId);
        assignment.put("title", "Delivery Route #" + index);
        assignment.put("description", "Complete all deliveries in the downtown area sector " + index);
        assignment.put("priority", index <= 2 ? "HIGH" : (index <= 4 ? "MEDIUM" : "LOW"));
        assignment.put("status", index <= 2 ? "IN_PROGRESS" : (index == 3 ? "PENDING" : "COMPLETED"));
        assignment.put("startTime", System.currentTimeMillis() - (index * 3600000));
        assignment.put("estimatedEndTime", System.currentTimeMillis() + ((6 - index) * 3600000));
        
        // Add tasks
        List<Map<String, Object>> tasks = new ArrayList<>();
        for (int i = 1; i <= 3 + index; i++) {
            Map<String, Object> task = new HashMap<>();
            task.put("id", "T" + i + "-" + assignmentId);
            task.put("type", i % 3 == 0 ? "PICKUP" : "DELIVERY");
            task.put("address", i + (i % 3 == 0 ? "23" : "45") + " Main St, City" + i);
            task.put("customer", "Customer " + (i * index));
            task.put("status", i <= index ? "COMPLETED" : (i == index + 1 ? "IN_PROGRESS" : "PENDING"));
            task.put("scheduledTime", System.currentTimeMillis() + (i * 1800000)); // 30 min intervals
            
            // Add coordinates
            Map<String, Double> coordinates = new HashMap<>();
            coordinates.put("lat", 37.7749 + (i * 0.01));
            coordinates.put("lng", -122.4194 + (i * 0.01));
            task.put("coordinates", coordinates);
            
            // Add package details
            List<Map<String, Object>> packages = new ArrayList<>();
            for (int j = 1; j <= i % 3 + 1; j++) {
                Map<String, Object> pkg = new HashMap<>();
                pkg.put("id", "P" + j + "-" + task.get("id"));
                pkg.put("weight", j * 0.5);
                pkg.put("dimensions", j + "x" + j + "x" + j);
                pkg.put("description", "Package " + j + " for Task " + i);
                packages.add(pkg);
            }
            task.put("packages", packages);
            
            tasks.add(task);
        }
        
        assignment.put("tasks", tasks);
        
        // Add notes
        List<String> notes = new ArrayList<>();
        notes.add("Priority customer - handle with care");
        notes.add("Potential traffic delays on Main St");
        notes.add("Call customer before delivery");
        assignment.put("notes", notes);
        
        return assignment;
    }
}
