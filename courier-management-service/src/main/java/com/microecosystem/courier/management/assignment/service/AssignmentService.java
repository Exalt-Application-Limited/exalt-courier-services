package com.gogidix.courierservices.management.$1;

import com.gogidix.courier.management.assignment.model.Assignment;
import com.gogidix.courier.management.assignment.model.AssignmentStatus;
import com.gogidix.courier.management.courier.model.Courier;
import com.gogidix.courier.management.exception.BusinessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for Assignment operations.
 */
public interface AssignmentService {

    /**
     * Creates a new assignment.
     *
     * @param assignment the assignment to create
     * @return the created assignment
     * @throws BusinessException if the assignment is invalid
     */
    Assignment createAssignment(Assignment assignment);

    /**
     * Updates an existing assignment.
     *
     * @param assignment the assignment to update
     * @return the updated assignment
     * @throws BusinessException if the assignment is invalid or not found
     */
    Assignment updateAssignment(Assignment assignment);

    /**
     * Finds an assignment by its ID.
     *
     * @param id the ID of the assignment
     * @return an Optional containing the assignment if found
     */
    Optional<Assignment> getAssignmentById(String id);

    /**
     * Finds an assignment by its unique assignment ID.
     *
     * @param assignmentId the unique assignment ID
     * @return an Optional containing the assignment if found
     */
    Optional<Assignment> getAssignmentByAssignmentId(String assignmentId);

    /**
     * Gets all assignments with pagination.
     *
     * @param pageable pagination information
     * @return a page of assignments
     */
    Page<Assignment> getAllAssignments(Pageable pageable);

    /**
     * Gets all assignments with a specific status.
     *
     * @param status the status to filter by
     * @param pageable pagination information
     * @return a page of assignments with the specified status
     */
    Page<Assignment> getAssignmentsByStatus(AssignmentStatus status, Pageable pageable);

    /**
     * Deletes an assignment by its ID.
     *
     * @param id the ID of the assignment to delete
     * @return true if the assignment was deleted, false otherwise
     */
    boolean deleteAssignment(String id);

    /**
     * Assigns an assignment to a courier.
     *
     * @param assignmentId the assignment ID
     * @param courierId the courier ID
     * @return the updated assignment
     * @throws BusinessException if the assignment or courier is not found, or if the assignment cannot be assigned
     */
    Assignment assignToCourier(String assignmentId, String courierId);

    /**
     * Accepts an assignment by a courier.
     *
     * @param assignmentId the assignment ID
     * @return the updated assignment
     * @throws BusinessException if the assignment is not found or cannot be accepted
     */
    Assignment acceptAssignment(String assignmentId);

    /**
     * Starts an assignment.
     *
     * @param assignmentId the assignment ID
     * @return the updated assignment
     * @throws BusinessException if the assignment is not found or cannot be started
     */
    Assignment startAssignment(String assignmentId);

    /**
     * Completes an assignment.
     *
     * @param assignmentId the assignment ID
     * @return the updated assignment
     * @throws BusinessException if the assignment is not found or cannot be completed
     */
    Assignment completeAssignment(String assignmentId);

    /**
     * Cancels an assignment.
     *
     * @param assignmentId the assignment ID
     * @param reason the reason for cancellation
     * @return the updated assignment
     * @throws BusinessException if the assignment is not found or cannot be cancelled
     */
    Assignment cancelAssignment(String assignmentId, String reason);

    /**
     * Rejects an assignment by a courier.
     *
     * @param assignmentId the assignment ID
     * @param reason the reason for rejection
     * @return the updated assignment
     * @throws BusinessException if the assignment is not found or cannot be rejected
     */
    Assignment rejectAssignment(String assignmentId, String reason);

    /**
     * Updates the status of an assignment.
     *
     * @param assignmentId the assignment ID
     * @param status the new status
     * @return the updated assignment
     * @throws BusinessException if the assignment is not found or the status change is invalid
     */
    Assignment updateAssignmentStatus(String assignmentId, AssignmentStatus status);

    /**
     * Gets all assignments for a courier.
     *
     * @param courierId the courier ID
     * @param pageable pagination information
     * @return a page of assignments for the courier
     */
    Page<Assignment> getAssignmentsByCourier(String courierId, Pageable pageable);

    /**
     * Gets assignments for a courier with a specific status.
     *
     * @param courierId the courier ID
     * @param status the assignment status
     * @param pageable pagination information
     * @return a page of assignments for the courier with the specified status
     */
    Page<Assignment> getAssignmentsByCourierAndStatus(String courierId, AssignmentStatus status, Pageable pageable);

    /**
     * Gets active assignments for a courier.
     *
     * @param courierId the courier ID
     * @return a list of active assignments for the courier
     */
    List<Assignment> getActiveAssignmentsByCourier(String courierId);

    /**
     * Gets overdue assignments.
     *
     * @return a list of overdue assignments
     */
    List<Assignment> getOverdueAssignments();

    /**
     * Gets assignments for a specific order.
     *
     * @param orderId the order ID
     * @return a list of assignments for the order
     */
    List<Assignment> getAssignmentsByOrderId(String orderId);

    /**
     * Searches for assignments by partial assignment ID match.
     *
     * @param assignmentIdPattern the pattern to match
     * @return a list of matching assignments
     */
    List<Assignment> searchAssignmentsByAssignmentId(String assignmentIdPattern);

    /**
     * Generates a unique assignment ID.
     *
     * @return a unique assignment ID
     */
    String generateAssignmentId();
} 