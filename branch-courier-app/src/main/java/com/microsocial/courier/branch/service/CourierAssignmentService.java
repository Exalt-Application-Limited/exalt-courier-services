package com.gogidix.courier.branch.service;

import com.gogidix.courier.branch.model.AssignmentStatus;
import com.gogidix.courier.branch.model.CourierAssignment;
import com.gogidix.courier.branch.model.corporate.Branch;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CourierAssignmentService {
    List<CourierAssignment> getAllCourierAssignments();
    Optional<CourierAssignment> getCourierAssignmentById(Long id);
    List<CourierAssignment> findCourierAssignmentsByBranch(Branch branch);
    List<CourierAssignment> findCourierAssignmentsByBranchId(Long branchId);
    List<CourierAssignment> findCourierAssignmentsByCourierId(Long courierId);
    List<CourierAssignment> findCourierAssignmentsByStatus(AssignmentStatus status);
    List<CourierAssignment> findCourierAssignmentsByBranchAndStatus(Branch branch, AssignmentStatus status);
    List<CourierAssignment> findCourierAssignmentsByCourierAndStatus(Long courierId, AssignmentStatus status);
    List<CourierAssignment> findCourierAssignmentsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    List<CourierAssignment> findCourierAssignmentsByBranchAndDateRange(Long branchId, LocalDateTime startDate, LocalDateTime endDate);
    CourierAssignment saveCourierAssignment(CourierAssignment courierAssignment);
    CourierAssignment updateCourierAssignmentStatus(Long id, AssignmentStatus status);
    void deleteCourierAssignment(Long id);
    boolean existsById(Long id);
}
