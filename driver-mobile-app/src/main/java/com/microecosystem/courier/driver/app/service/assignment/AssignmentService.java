package com.microecosystem.courier.driver.app.service.assignment;

import com.microecosystem.courier.driver.app.model.assignment.Assignment;
import com.microecosystem.courier.driver.app.model.assignment.AssignmentStatus;
import com.microecosystem.courier.driver.app.model.assignment.Task;
import com.microecosystem.courier.driver.app.model.assignment.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service interface for Assignment operations in the driver mobile app.
 */
public interface AssignmentService {

    /**
     * Get all assignments for a driver.
     *
     * @param driverId the driver ID
     * @param pageable pagination information
     * @return page of assignments
     */
    Page<Assignment> getAssignmentsByDriver(Long driverId, Pageable pageable);
    
    /**
     * Get all assignments for a driver with a specific status.
     *
     * @param driverId the driver ID
     * @param status the assignment status
     * @param pageable pagination information
     * @return page of assignments
     */
    Page<Assignment> getAssignmentsByDriverAndStatus(Long driverId, AssignmentStatus status, Pageable pageable);
    
    /**
     * Get all active assignments for a driver.
     * Active assignments are those with status ACCEPTED, STARTED, or IN_PROGRESS.
     *
     * @param driverId the driver ID
     * @return list of active assignments
     */
    List<Assignment> getActiveAssignmentsByDriver(Long driverId);
    
    /**
     * Get assignment by ID.
     *
     * @param id the assignment ID
     * @return optional assignment
     */
    Optional<Assignment> getAssignmentById(Long id);
    
    /**
     * Get assignment by ID and driver ID.
     *
     * @param id the assignment ID
     * @param driverId the driver ID
     * @return optional assignment
     */
    Optional<Assignment> getAssignmentByIdAndDriverId(Long id, Long driverId);
    
    /**
     * Create a new assignment.
     *
     * @param assignment the assignment to create
     * @return the created assignment
     */
    Assignment createAssignment(Assignment assignment);
    
    /**
     * Update an existing assignment.
     *
     * @param assignment the assignment to update
     * @return the updated assignment
     */
    Assignment updateAssignment(Assignment assignment);
    
    /**
     * Update assignment status.
     *
     * @param id the assignment ID
     * @param status the new status
     * @return the updated assignment
     */
    Assignment updateAssignmentStatus(Long id, AssignmentStatus status);
    
    /**
     * Accept an assignment.
     *
     * @param id the assignment ID
     * @param driverId the driver ID
     * @return the updated assignment
     */
    Assignment acceptAssignment(Long id, Long driverId);
    
    /**
     * Start an assignment.
     *
     * @param id the assignment ID
     * @param driverId the driver ID
     * @return the updated assignment
     */
    Assignment startAssignment(Long id, Long driverId);
    
    /**
     * Complete an assignment.
     *
     * @param id the assignment ID
     * @param driverId the driver ID
     * @return the updated assignment
     */
    Assignment completeAssignment(Long id, Long driverId);
    
    /**
     * Cancel an assignment.
     *
     * @param id the assignment ID
     * @param driverId the driver ID
     * @param cancellationReason the cancellation reason
     * @return the updated assignment
     */
    Assignment cancelAssignment(Long id, Long driverId, String cancellationReason);
    
    /**
     * Get assignments for a driver within a date range.
     *
     * @param driverId the driver ID
     * @param startDate start of date range
     * @param endDate end of date range
     * @param pageable pagination information
     * @return page of assignments
     */
    Page<Assignment> getAssignmentsByDriverAndDateRange(
            Long driverId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    /**
     * Count assignments by status for a driver.
     *
     * @param driverId the driver ID
     * @param status the assignment status
     * @return count of assignments
     */
    long countAssignmentsByDriverAndStatus(Long driverId, AssignmentStatus status);
    
    /**
     * Get assignments with sync status.
     *
     * @param syncStatus the sync status
     * @param pageable pagination information
     * @return page of assignments
     */
    Page<Assignment> getAssignmentsBySyncStatus(String syncStatus, Pageable pageable);
    
    /**
     * Update assignment sync status.
     *
     * @param id the assignment ID
     * @param syncStatus the new sync status
     * @return the updated assignment
     */
    Assignment updateAssignmentSyncStatus(Long id, String syncStatus);
    
    /**
     * Delete an assignment (soft delete).
     *
     * @param id the assignment ID
     * @return true if successful
     */
    boolean deleteAssignment(Long id);
}
