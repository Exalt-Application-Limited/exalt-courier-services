package com.microecosystem.courier.driver.app.repository;

import com.microecosystem.courier.driver.app.model.Driver;
import com.microecosystem.courier.driver.app.model.assignment.Assignment;
import com.microecosystem.courier.driver.app.model.assignment.AssignmentStatus;
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
 * Repository interface for Assignment entity operations.
 */
@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    /**
     * Find all assignments for a specific driver.
     *
     * @param driver the driver
     * @param pageable pagination information
     * @return page of assignments
     */
    Page<Assignment> findByDriver(Driver driver, Pageable pageable);

    /**
     * Find all assignments for a specific driver by ID.
     *
     * @param driverId the driver ID
     * @param pageable pagination information
     * @return page of assignments
     */
    @Query("SELECT a FROM Assignment a WHERE a.driver.id = :driverId AND a.isDeleted = false")
    Page<Assignment> findByDriverId(@Param("driverId") Long driverId, Pageable pageable);

    /**
     * Find all assignments for a specific driver by ID with a specific status.
     *
     * @param driverId the driver ID
     * @param status the assignment status
     * @param pageable pagination information
     * @return page of assignments
     */
    @Query("SELECT a FROM Assignment a WHERE a.driver.id = :driverId AND a.status = :status AND a.isDeleted = false")
    Page<Assignment> findByDriverIdAndStatus(
            @Param("driverId") Long driverId,
            @Param("status") AssignmentStatus status,
            Pageable pageable);

    /**
     * Find all active assignments for a specific driver.
     * Active assignments are those with status ACCEPTED, STARTED, or IN_PROGRESS.
     *
     * @param driverId the driver ID
     * @return list of active assignments
     */
    @Query("SELECT a FROM Assignment a WHERE a.driver.id = :driverId AND " +
           "a.status IN ('ACCEPTED', 'STARTED', 'IN_PROGRESS') AND a.isDeleted = false " +
           "ORDER BY a.startedAt ASC NULLS LAST, a.assignedAt ASC")
    List<Assignment> findActiveAssignmentsByDriverId(@Param("driverId") Long driverId);

    /**
     * Find all assignments for a specific driver by ID within a date range.
     *
     * @param driverId the driver ID
     * @param startDate start of date range
     * @param endDate end of date range
     * @param pageable pagination information
     * @return page of assignments
     */
    @Query("SELECT a FROM Assignment a WHERE a.driver.id = :driverId AND " +
           "a.createdAt BETWEEN :startDate AND :endDate AND a.isDeleted = false")
    Page<Assignment> findByDriverIdAndDateRange(
            @Param("driverId") Long driverId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    /**
     * Find assignment by ID and driver ID.
     *
     * @param id the assignment ID
     * @param driverId the driver ID
     * @return optional assignment
     */
    @Query("SELECT a FROM Assignment a WHERE a.id = :id AND a.driver.id = :driverId AND a.isDeleted = false")
    Optional<Assignment> findByIdAndDriverId(@Param("id") Long id, @Param("driverId") Long driverId);

    /**
     * Count assignments by status for a specific driver.
     *
     * @param driverId the driver ID
     * @param status the assignment status
     * @return count of assignments
     */
    @Query("SELECT COUNT(a) FROM Assignment a WHERE a.driver.id = :driverId AND a.status = :status AND a.isDeleted = false")
    long countByDriverIdAndStatus(@Param("driverId") Long driverId, @Param("status") AssignmentStatus status);

    /**
     * Find all assignments with sync status.
     *
     * @param syncStatus the sync status
     * @param pageable pagination information
     * @return page of assignments
     */
    Page<Assignment> findBySyncStatus(String syncStatus, Pageable pageable);
}
