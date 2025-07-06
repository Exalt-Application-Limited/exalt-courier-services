package com.gogidix.courier.branch.service.impl;

import com.gogidix.courier.branch.model.AssignmentStatus;
import com.gogidix.courier.branch.model.CourierAssignment;
import com.gogidix.courier.branch.model.corporate.Branch;
import com.gogidix.courier.branch.repository.CourierAssignmentRepository;
import com.gogidix.courier.branch.service.CourierAssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CourierAssignmentServiceImpl implements CourierAssignmentService {

    private final CourierAssignmentRepository courierAssignmentRepository;

    @Autowired
    public CourierAssignmentServiceImpl(CourierAssignmentRepository courierAssignmentRepository) {
        this.courierAssignmentRepository = courierAssignmentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourierAssignment> getAllCourierAssignments() {
        return courierAssignmentRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CourierAssignment> getCourierAssignmentById(Long id) {
        return courierAssignmentRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourierAssignment> findCourierAssignmentsByBranch(Branch branch) {
        return courierAssignmentRepository.findByBranch(branch);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourierAssignment> findCourierAssignmentsByBranchId(Long branchId) {
        return courierAssignmentRepository.findByBranchId(branchId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourierAssignment> findCourierAssignmentsByCourierId(Long courierId) {
        return courierAssignmentRepository.findByCourierId(courierId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourierAssignment> findCourierAssignmentsByStatus(AssignmentStatus status) {
        return courierAssignmentRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourierAssignment> findCourierAssignmentsByBranchAndStatus(Branch branch, AssignmentStatus status) {
        return courierAssignmentRepository.findByBranchAndStatus(branch, status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourierAssignment> findCourierAssignmentsByCourierAndStatus(Long courierId, AssignmentStatus status) {
        return courierAssignmentRepository.findByCourierIdAndStatus(courierId, status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourierAssignment> findCourierAssignmentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return courierAssignmentRepository.findByDateRange(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourierAssignment> findCourierAssignmentsByBranchAndDateRange(Long branchId, LocalDateTime startDate, LocalDateTime endDate) {
        return courierAssignmentRepository.findByBranchIdAndDateRange(branchId, startDate, endDate);
    }

    @Override
    public CourierAssignment saveCourierAssignment(CourierAssignment courierAssignment) {
        LocalDateTime now = LocalDateTime.now();
        
        if (courierAssignment.getId() == null) {
            courierAssignment.setCreatedAt(now);
            // Generate an assignment code if not already set
            if (courierAssignment.getAssignmentCode() == null) {
                courierAssignment.setAssignmentCode("ASG-" + System.currentTimeMillis());
            }
        }
        
        courierAssignment.setUpdatedAt(now);
        return courierAssignmentRepository.save(courierAssignment);
    }

    @Override
    public CourierAssignment updateCourierAssignmentStatus(Long id, AssignmentStatus status) {
        CourierAssignment assignment = courierAssignmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Courier assignment not found with id: " + id));
        
        assignment.setStatus(status);
        
        // Set completedAt time if the assignment is completed
        if (status == AssignmentStatus.COMPLETED) {
            assignment.setCompletedAt(LocalDateTime.now());
        }
        
        assignment.setUpdatedAt(LocalDateTime.now());
        return courierAssignmentRepository.save(assignment);
    }

    @Override
    public void deleteCourierAssignment(Long id) {
        courierAssignmentRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return courierAssignmentRepository.existsById(id);
    }
}
