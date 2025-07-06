package com.gogidix.courier.management.assignment.repository;

import com.gogidix.courier.management.assignment.model.Assignment;
import com.gogidix.courier.management.assignment.model.AssignmentStatus;
import com.gogidix.courier.management.courier.model.Courier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Assignment entity operations.
 */
@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, String> {

    /**
     * Find an assignment by its unique assignment ID.
     * 
     * @param assignmentId the assignment ID
     * @return the assignment if found
     */
    Optional<Assignment> findByAssignmentId(String assignmentId);

    /**
     * Find all assignments for a specific courier.
     * 
     * @param courier the courier
     * @return a list of assignments
     */
    List<Assignment> findByCourier(Courier courier);

    /**
     * Find all assignments for a specific courier with pagination.
     * 
     * @param courier the courier
     * @param pageable pagination information
     * @return a page of assignments
     */
    Page<Assignment> findByCourier(Courier courier, Pageable pageable);

    /**
     * Find all assignments by status.
     * 
     * @param status the status
     * @return a list of assignments
     */
    List<Assignment> findByStatus(AssignmentStatus status);

    /**
     * Find all assignments by status with pagination.
     * 
     * @param status the status
     * @param pageable pagination information
     * @return a page of assignments
     */
    Page<Assignment> findByStatus(AssignmentStatus status, Pageable pageable);

    /**
     * Find all assignments for a specific courier with a specific status.
     * 
     * @param courier the courier
     * @param status the status
     * @return a list of assignments
     */
    List<Assignment> findByCourierAndStatus(Courier courier, AssignmentStatus status);

    /**
     * Find all assignments for a specific courier with a specific status with pagination.
     * 
     * @param courier the courier
     * @param status the status
     * @param pageable pagination information
     * @return a page of assignments
     */
    Page<Assignment> findByCourierAndStatus(Courier courier, AssignmentStatus status, Pageable pageable);

    /**
     * Find all assignments that are scheduled to start within a specific time range.
     * 
     * @param startTime the start time of the range
     * @param endTime the end time of the range
     * @return a list of assignments
     */
    List<Assignment> findByEstimatedStartTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * Find all active assignments for a specific courier.
     * 
     * @param courier the courier
     * @return a list of active assignments
     */
    @Query("SELECT a FROM Assignment a WHERE a.courier = :courier AND (a.status = 'PENDING' OR a.status = 'IN_PROGRESS' OR a.status = 'DELAYED')")
    List<Assignment> findActiveByCourier(@Param("courier") Courier courier);

    /**
     * Find all overdue assignments.
     * 
     * @param currentTime the reference time for determining overdue
     * @return a list of overdue assignments
     */
    @Query("SELECT a FROM Assignment a WHERE a.estimatedEndTime < :currentTime AND a.status NOT IN ('COMPLETED', 'CANCELLED', 'FAILED', 'REJECTED')")
    List<Assignment> findOverdueAssignments(@Param("currentTime") LocalDateTime currentTime);

    /**
     * Find assignments by a partial assignment ID match.
     * 
     * @param assignmentIdPattern the pattern to match
     * @return a list of matching assignments
     */
    @Query("SELECT a FROM Assignment a WHERE a.assignmentId LIKE %:assignmentIdPattern%")
    List<Assignment> findByAssignmentIdContaining(@Param("assignmentIdPattern") String assignmentIdPattern);

    /**
     * Find all assignments for a specific order ID.
     * 
     * @param orderId the order ID
     * @return a list of assignments for the order
     */
    List<Assignment> findByOrderId(String orderId);

    /**
     * Count the number of assignments by status.
     * 
     * @param status the status
     * @return the count
     */
    long countByStatus(AssignmentStatus status);

    /**
     * Count the number of assignments for a specific courier.
     * 
     * @param courier the courier
     * @return the count
     */
    long countByCourier(Courier courier);

    /**
     * Count the number of assignments for a specific courier with a specific status.
     * 
     * @param courier the courier
     * @param status the status
     * @return the count
     */
    long countByCourierAndStatus(Courier courier, AssignmentStatus status);

    /**
     * Delete assignments by status.
     * 
     * @param status the status
     * @return the number of deleted assignments
     */
    long deleteByStatus(AssignmentStatus status);
} 