package com.gogidix.courier.management.assignment.repository;

import com.gogidix.courier.management.assignment.model.Assignment;
import com.gogidix.courier.management.assignment.model.AssignmentTask;
import com.gogidix.courier.management.assignment.model.TaskStatus;
import com.gogidix.courier.management.assignment.model.TaskType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for AssignmentTask entity operations.
 */
@Repository
public interface AssignmentTaskRepository extends JpaRepository<AssignmentTask, String> {

    /**
     * Find all tasks for a specific assignment.
     * 
     * @param assignment the assignment
     * @return a list of tasks
     */
    List<AssignmentTask> findByAssignment(Assignment assignment);

    /**
     * Find all tasks for a specific assignment ordered by sequence.
     * 
     * @param assignment the assignment
     * @return a list of tasks ordered by sequence
     */
    List<AssignmentTask> findByAssignmentOrderBySequenceAsc(Assignment assignment);
    
    /**
     * Alias for findByAssignmentOrderBySequenceAsc for backward compatibility.
     * 
     * @param assignment the assignment
     * @return a list of tasks ordered by sequence
     */
    default List<AssignmentTask> findByAssignmentOrderBySequenceNumberAsc(Assignment assignment) {
        return findByAssignmentOrderBySequenceAsc(assignment);
    }

    /**
     * Find all tasks for a specific assignment with pagination.
     * 
     * @param assignment the assignment
     * @param pageable pagination information
     * @return a page of tasks
     */
    Page<AssignmentTask> findByAssignment(Assignment assignment, Pageable pageable);

    /**
     * Find all tasks with a specific status.
     * 
     * @param status the status
     * @return a list of tasks
     */
    List<AssignmentTask> findByStatus(TaskStatus status);

    /**
     * Find all tasks with a specific type.
     * 
     * @param type the type
     * @return a list of tasks
     */
    List<AssignmentTask> findByTaskType(TaskType type);

    /**
     * Find all tasks for a specific assignment with a specific status.
     * 
     * @param assignment the assignment
     * @param status the status
     * @return a list of tasks
     */
    List<AssignmentTask> findByAssignmentAndStatus(Assignment assignment, TaskStatus status);

    /**
     * Find all tasks for a specific assignment with a specific type.
     * 
     * @param assignment the assignment
     * @param type the type
     * @return a list of tasks
     */
    List<AssignmentTask> findByAssignmentAndTaskType(Assignment assignment, TaskType type);
    
    /**
     * Alias for findByAssignmentAndTaskType for backward compatibility.
     * 
     * @param assignment the assignment
     * @param type the type
     * @return a list of tasks
     */
    default List<AssignmentTask> findByAssignmentAndType(Assignment assignment, TaskType type) {
        return findByAssignmentAndTaskType(assignment, type);
    }

    /**
     * Find the next task in sequence for a specific assignment.
     * 
     * @param assignment the assignment
     * @param status the status to filter by (typically PENDING)
     * @return the next task in sequence
     */
    @Query("SELECT t FROM AssignmentTask t WHERE t.assignment = :assignment AND t.status = :status ORDER BY t.sequence ASC")
    List<AssignmentTask> findNextTasksByAssignmentAndStatus(@Param("assignment") Assignment assignment, @Param("status") TaskStatus status);

    /**
     * Find all tasks that should be completed within a specific time window.
     * 
     * @param startTime the start of the time window
     * @param endTime the end of the time window
     * @return a list of tasks
     */
    List<AssignmentTask> findByScheduledTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * Find all overdue tasks that have not been completed.
     * 
     * @param currentTime the reference time for determining overdue
     * @return a list of overdue tasks
     */
    @Query("SELECT t FROM AssignmentTask t WHERE t.scheduledTime < :currentTime AND t.status NOT IN ('COMPLETED', 'CANCELLED', 'SKIPPED', 'FAILED')")
    List<AssignmentTask> findOverdueTasks(@Param("currentTime") LocalDateTime currentTime);

    /**
     * Find all tasks near a specific location.
     * 
     * @param latitude the latitude of the location
     * @param longitude the longitude of the location
     * @param radiusInKm the radius in kilometers
     * @return a list of tasks near the location
     */
    @Query(value = "SELECT t.* FROM assignment_tasks t " +
           "WHERE ST_DWithin(ST_MakePoint(t.longitude, t.latitude)::geography, " +
           "ST_MakePoint(:longitude, :latitude)::geography, :radiusInKm * 1000)", nativeQuery = true)
    List<AssignmentTask> findNearLocation(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("radiusInKm") double radiusInKm);

    /**
     * Count the number of tasks by status.
     * 
     * @param status the status
     * @return the count
     */
    long countByStatus(TaskStatus status);

    /**
     * Count the number of tasks by type.
     * 
     * @param type the type
     * @return the count
     */
    long countByTaskType(TaskType type);

    /**
     * Count the number of tasks for a specific assignment.
     * 
     * @param assignment the assignment
     * @return the count
     */
    long countByAssignment(Assignment assignment);

    /**
     * Count the number of tasks for a specific assignment with a specific status.
     * 
     * @param assignment the assignment
     * @param status the status
     * @return the count
     */
    long countByAssignmentAndStatus(Assignment assignment, TaskStatus status);

    /**
     * Delete tasks by status.
     * 
     * @param status the status
     * @return the number of deleted tasks
     */
    long deleteByStatus(TaskStatus status);
    
    /**
     * Find the maximum sequence number for a specific assignment.
     * 
     * @param assignment the assignment
     * @return the maximum sequence number or null if no tasks exist
     */
    @Query("SELECT MAX(t.sequence) FROM AssignmentTask t WHERE t.assignment = :assignment")
    Integer findMaxSequenceNumberByAssignment(@Param("assignment") Assignment assignment);
    
    /**
     * Find all tasks within a time window (alias for backward compatibility).
     * 
     * @param startTime the start of the time window
     * @param endTime the end of the time window
     * @return a list of tasks
     */
    default List<AssignmentTask> findByTimeWindow(LocalDateTime startTime, LocalDateTime endTime) {
        return findByScheduledTimeBetween(startTime, endTime);
    }
    
    /**
     * Find overdue tasks for a specific assignment.
     * 
     * @param assignment the assignment
     * @param currentTime the reference time
     * @return a list of overdue tasks
     */
    @Query("SELECT t FROM AssignmentTask t WHERE t.assignment = :assignment AND t.scheduledTime < :currentTime AND t.status NOT IN ('COMPLETED', 'CANCELLED', 'SKIPPED', 'FAILED')")
    List<AssignmentTask> findOverdueTasksByAssignment(@Param("assignment") Assignment assignment, @Param("currentTime") LocalDateTime currentTime);
} 