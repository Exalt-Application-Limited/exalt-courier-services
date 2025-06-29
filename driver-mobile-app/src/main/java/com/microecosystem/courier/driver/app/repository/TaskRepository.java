package com.microecosystem.courier.driver.app.repository;

import com.microecosystem.courier.driver.app.model.assignment.Task;
import com.microecosystem.courier.driver.app.model.assignment.TaskStatus;
import com.microecosystem.courier.driver.app.model.assignment.TaskType;
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
 * Repository interface for Task entity operations.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Find all tasks for a specific assignment.
     *
     * @param assignmentId the assignment ID
     * @return list of tasks
     */
    @Query("SELECT t FROM Task t WHERE t.assignment.id = :assignmentId AND t.isDeleted = false " +
           "ORDER BY t.sequenceNumber ASC NULLS LAST, t.id ASC")
    List<Task> findByAssignmentId(@Param("assignmentId") Long assignmentId);

    /**
     * Find all tasks for a specific assignment with a specific status.
     *
     * @param assignmentId the assignment ID
     * @param status the task status
     * @return list of tasks
     */
    @Query("SELECT t FROM Task t WHERE t.assignment.id = :assignmentId AND t.status = :status AND t.isDeleted = false " +
           "ORDER BY t.sequenceNumber ASC NULLS LAST, t.id ASC")
    List<Task> findByAssignmentIdAndStatus(
            @Param("assignmentId") Long assignmentId,
            @Param("status") TaskStatus status);

    /**
     * Find all tasks for a specific assignment with a specific type.
     *
     * @param assignmentId the assignment ID
     * @param taskType the task type
     * @return list of tasks
     */
    @Query("SELECT t FROM Task t WHERE t.assignment.id = :assignmentId AND t.taskType = :taskType AND t.isDeleted = false " +
           "ORDER BY t.sequenceNumber ASC NULLS LAST, t.id ASC")
    List<Task> findByAssignmentIdAndTaskType(
            @Param("assignmentId") Long assignmentId,
            @Param("taskType") TaskType taskType);

    /**
     * Find task by ID and assignment ID.
     *
     * @param id the task ID
     * @param assignmentId the assignment ID
     * @return optional task
     */
    @Query("SELECT t FROM Task t WHERE t.id = :id AND t.assignment.id = :assignmentId AND t.isDeleted = false")
    Optional<Task> findByIdAndAssignmentId(@Param("id") Long id, @Param("assignmentId") Long assignmentId);

    /**
     * Find all tasks with time windows within a specific date range.
     *
     * @param startDate start of date range
     * @param endDate end of date range
     * @param pageable pagination information
     * @return page of tasks
     */
    @Query("SELECT t FROM Task t WHERE " +
           "((t.timeWindowStart BETWEEN :startDate AND :endDate) OR " +
           "(t.timeWindowEnd BETWEEN :startDate AND :endDate)) AND t.isDeleted = false")
    Page<Task> findByTimeWindowRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    /**
     * Find all tasks by tracking number.
     *
     * @param trackingNumber the tracking number
     * @return list of tasks
     */
    List<Task> findByTrackingNumber(String trackingNumber);

    /**
     * Find all tasks by package ID.
     *
     * @param packageId the package ID
     * @return list of tasks
     */
    List<Task> findByPackageId(String packageId);

    /**
     * Count tasks by status for a specific assignment.
     *
     * @param assignmentId the assignment ID
     * @param status the task status
     * @return count of tasks
     */
    @Query("SELECT COUNT(t) FROM Task t WHERE t.assignment.id = :assignmentId AND t.status = :status AND t.isDeleted = false")
    long countByAssignmentIdAndStatus(
            @Param("assignmentId") Long assignmentId,
            @Param("status") TaskStatus status);

    /**
     * Find all tasks with sync status.
     *
     * @param syncStatus the sync status
     * @param pageable pagination information
     * @return page of tasks
     */
    Page<Task> findBySyncStatus(String syncStatus, Pageable pageable);

    /**
     * Find the next pending task for a specific assignment.
     *
     * @param assignmentId the assignment ID
     * @return optional task
     */
    @Query("SELECT t FROM Task t WHERE t.assignment.id = :assignmentId AND t.status = 'PENDING' AND t.isDeleted = false " +
           "ORDER BY t.sequenceNumber ASC NULLS LAST, t.id ASC")
    Optional<Task> findNextPendingTaskForAssignment(@Param("assignmentId") Long assignmentId);
}
