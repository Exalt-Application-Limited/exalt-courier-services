package com.exalt.courier.courier.branch.repository;

import com.microsocial.courier.branch.model.AssignmentStatus;
import com.microsocial.courier.branch.model.CourierAssignment;
import com.microsocial.courier.branch.model.corporate.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CourierAssignmentRepository extends JpaRepository<CourierAssignment, Long> {
    
    List<CourierAssignment> findByBranch(Branch branch);
    
    @Query("SELECT ca FROM CourierAssignment ca WHERE ca.branch.id = :branchId")
    List<CourierAssignment> findByBranchId(@Param("branchId") Long branchId);
    
    @Query("SELECT ca FROM CourierAssignment ca WHERE ca.courier.id = :courierId")
    List<CourierAssignment> findByCourierId(@Param("courierId") Long courierId);
    
    List<CourierAssignment> findByStatus(AssignmentStatus status);
    
    List<CourierAssignment> findByBranchAndStatus(Branch branch, AssignmentStatus status);
    
    @Query("SELECT ca FROM CourierAssignment ca WHERE ca.courier.id = :courierId AND ca.status = :status")
    List<CourierAssignment> findByCourierIdAndStatus(
        @Param("courierId") Long courierId, 
        @Param("status") AssignmentStatus status
    );
    
    @Query("SELECT ca FROM CourierAssignment ca WHERE ca.assignedAt BETWEEN :startDate AND :endDate OR ca.completedAt BETWEEN :startDate AND :endDate")
    List<CourierAssignment> findByDateRange(
        @Param("startDate") LocalDateTime startDate, 
        @Param("endDate") LocalDateTime endDate
    );
    
    @Query("SELECT ca FROM CourierAssignment ca WHERE ca.branch.id = :branchId AND (ca.assignedAt BETWEEN :startDate AND :endDate OR ca.completedAt BETWEEN :startDate AND :endDate)")
    List<CourierAssignment> findByBranchIdAndDateRange(
        @Param("branchId") Long branchId, 
        @Param("startDate") LocalDateTime startDate, 
        @Param("endDate") LocalDateTime endDate
    );
}
