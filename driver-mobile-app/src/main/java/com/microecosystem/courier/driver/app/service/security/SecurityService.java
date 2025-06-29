package com.microecosystem.courier.driver.app.service.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * Service for handling security-related operations and authorization checks.
 * This service provides methods to verify if a user has the right to access specific resources.
 */
@Service
public class SecurityService {

    private static final Logger logger = LoggerFactory.getLogger(SecurityService.class);
    
    // In a real implementation, this would be stored in a database
    private final Set<String> authorizedDevices = new HashSet<>();
    
    /**
     * Check if the current authenticated user is the specified driver.
     * 
     * @param driverId The ID of the driver to check
     * @return True if the current user is the specified driver, false otherwise
     */
    public boolean isCurrentDriver(String driverId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return false;
        }
        
        String username = auth.getName();
        logger.debug("Checking if current user '{}' is driver '{}'", username, driverId);
        
        // In a real implementation, this would check against a database
        // For simulation, we'll assume username matches driverId
        return username.equals(driverId);
    }
    
    /**
     * Check if the current authenticated user can access a specific assignment.
     * 
     * @param assignmentId The ID of the assignment to check
     * @return True if the current user can access the assignment, false otherwise
     */
    public boolean canAccessAssignment(String assignmentId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return false;
        }
        
        String username = auth.getName();
        logger.debug("Checking if user '{}' can access assignment '{}'", username, assignmentId);
        
        // In a real implementation, this would check against a database
        // For simulation, we'll assume the user can access assignments
        // that start with their username or are specifically assigned to them
        return assignmentId.contains(username) || isAssignmentAccessible(username, assignmentId);
    }
    
    /**
     * Check if the current authenticated user can update a specific task.
     * 
     * @param taskId The ID of the task to check
     * @return True if the current user can update the task, false otherwise
     */
    public boolean canUpdateTask(String taskId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return false;
        }
        
        String username = auth.getName();
        logger.debug("Checking if user '{}' can update task '{}'", username, taskId);
        
        // In a real implementation, this would check against a database
        // For simulation, we'll assume the user can update tasks
        // that contain their username (as part of assignment) or are specifically assigned to them
        return taskId.contains(username) || isTaskUpdatable(username, taskId);
    }
    
    /**
     * Check if a device is authorized.
     * 
     * @param deviceId The ID of the device to check
     * @return True if the device is authorized, false otherwise
     */
    public boolean isAuthorizedDevice(String deviceId) {
        logger.debug("Checking if device '{}' is authorized", deviceId);
        
        // In a real implementation, this would check against a database
        // For simulation, we'll use our in-memory set
        if (authorizedDevices.isEmpty()) {
            // Initialize with some test devices
            authorizedDevices.add("device-001");
            authorizedDevices.add("device-002");
            authorizedDevices.add("device-003");
        }
        
        // For simplicity, we'll also authorize devices that have a "valid" format
        return authorizedDevices.contains(deviceId) || deviceId.startsWith("device-");
    }
    
    // Helper methods
    
    private boolean isAssignmentAccessible(String username, String assignmentId) {
        // In a real implementation, this would check against a database
        // For simulation, we'll assume all assignments are accessible
        return true;
    }
    
    private boolean isTaskUpdatable(String username, String taskId) {
        // In a real implementation, this would check against a database
        // For simulation, we'll assume all tasks are updatable
        return true;
    }
}
