package com.exalt.courier.courier.branch.repository;

import com.microsocial.courier.branch.model.ShipmentTask;
import com.microsocial.courier.branch.model.TaskStatus;
import com.microsocial.courier.branch.model.TaskType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShipmentTaskRepository extends JpaRepository<ShipmentTask, Long> {
    
    @Query("SELECT st FROM ShipmentTask st WHERE st.assignment.id = :assignmentId")
    List<ShipmentTask> findByAssignmentId(@Param("assignmentId") Long assignmentId);
    
    List<ShipmentTask> findByTaskType(TaskType taskType);
    
    List<ShipmentTask> findByStatus(TaskStatus status);
    
    List<ShipmentTask> findByShipmentId(String shipmentId);
    
    @Query("SELECT st FROM ShipmentTask st WHERE st.assignment.id = :assignmentId AND st.status = :status")
    List<ShipmentTask> findByAssignmentIdAndStatus(
        @Param("assignmentId") Long assignmentId, 
        @Param("status") TaskStatus status
    );
    
    @Query("SELECT st FROM ShipmentTask st WHERE st.scheduledTime BETWEEN :startTime AND :endTime")
    List<ShipmentTask> findByScheduledTimeRange(
        @Param("startTime") LocalDateTime startTime, 
        @Param("endTime") LocalDateTime endTime
    );
    
    @Query("SELECT st FROM ShipmentTask st WHERE st.assignment.branch.id = :branchId AND st.scheduledTime BETWEEN :startTime AND :endTime")
    List<ShipmentTask> findByBranchIdAndScheduledTimeRange(
        @Param("branchId") Long branchId, 
        @Param("startTime") LocalDateTime startTime, 
        @Param("endTime") LocalDateTime endTime
    );
    
    @Query("SELECT st FROM ShipmentTask st WHERE st.assignment.id = :assignmentId ORDER BY st.sequenceOrder ASC")
    List<ShipmentTask> findByAssignmentIdOrderBySequenceAsc(@Param("assignmentId") Long assignmentId);
}
