package com.microecosystem.courier.driver.app.service.assignment.impl;

import com.microecosystem.courier.driver.app.model.Driver;
import com.microecosystem.courier.driver.app.model.assignment.Assignment;
import com.microecosystem.courier.driver.app.model.assignment.AssignmentStatus;
import com.microecosystem.courier.driver.app.repository.AssignmentRepository;
import com.microecosystem.courier.driver.app.repository.DriverRepository;
import com.microecosystem.courier.driver.app.service.assignment.AssignmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the AssignmentService interface.
 */
@Service
public class AssignmentServiceImpl implements AssignmentService {

    private static final Logger logger = LoggerFactory.getLogger(AssignmentServiceImpl.class);

    private final AssignmentRepository assignmentRepository;
    private final DriverRepository driverRepository;

    @Autowired
    public AssignmentServiceImpl(AssignmentRepository assignmentRepository, DriverRepository driverRepository) {
        this.assignmentRepository = assignmentRepository;
        this.driverRepository = driverRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Assignment> getAssignmentsByDriver(Long driverId, Pageable pageable) {
        logger.debug("Getting assignments for driver with ID: {}", driverId);
        return assignmentRepository.findByDriverId(driverId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Assignment> getAssignmentsByDriverAndStatus(Long driverId, AssignmentStatus status, Pageable pageable) {
        logger.debug("Getting assignments for driver with ID: {} and status: {}", driverId, status);
        return assignmentRepository.findByDriverIdAndStatus(driverId, status, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Assignment> getActiveAssignmentsByDriver(Long driverId) {
        logger.debug("Getting active assignments for driver with ID: {}", driverId);
        return assignmentRepository.findActiveAssignmentsByDriverId(driverId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Assignment> getAssignmentById(Long id) {
        logger.debug("Getting assignment with ID: {}", id);
        return assignmentRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Assignment> getAssignmentByIdAndDriverId(Long id, Long driverId) {
        logger.debug("Getting assignment with ID: {} for driver with ID: {}", id, driverId);
        return assignmentRepository.findByIdAndDriverId(id, driverId);
    }

    @Override
    @Transactional
    public Assignment createAssignment(Assignment assignment) {
        logger.debug("Creating a new assignment");
        if (assignment.getId() != null) {
            logger.warn("Assignment has an ID, which suggests it already exists. Setting ID to null");
            assignment.setId(null);
        }
        
        assignment.setCreatedAt(LocalDateTime.now());
        assignment.setUpdatedAt(LocalDateTime.now());
        if (assignment.getStatus() == null) {
            assignment.setStatus(AssignmentStatus.PENDING);
        }
        
        return assignmentRepository.save(assignment);
    }

    @Override
    @Transactional
    public Assignment updateAssignment(Assignment assignment) {
        logger.debug("Updating assignment with ID: {}", assignment.getId());
        if (assignment.getId() == null) {
            throw new IllegalArgumentException("Cannot update assignment without an ID");
        }
        
        Optional<Assignment> existingAssignment = assignmentRepository.findById(assignment.getId());
        if (existingAssignment.isEmpty()) {
            throw new IllegalArgumentException("Cannot update non-existent assignment");
        }
        
        assignment.setUpdatedAt(LocalDateTime.now());
        return assignmentRepository.save(assignment);
    }

    @Override
    @Transactional
    public Assignment updateAssignmentStatus(Long id, AssignmentStatus status) {
        logger.debug("Updating status to {} for assignment with ID: {}", status, id);
        Optional<Assignment> optionalAssignment = assignmentRepository.findById(id);
        if (optionalAssignment.isEmpty()) {
            throw new IllegalArgumentException("Cannot update status for non-existent assignment");
        }
        
        Assignment assignment = optionalAssignment.get();
        assignment.setStatus(status);
        assignment.setUpdatedAt(LocalDateTime.now());
        
        // Set specific timestamps based on status
        switch (status) {
            case ACCEPTED:
                assignment.setAssignedAt(LocalDateTime.now());
                break;
            case STARTED:
                assignment.setStartedAt(LocalDateTime.now());
                break;
            case COMPLETED:
                assignment.setCompletedAt(LocalDateTime.now());
                break;
            case CANCELLED:
                assignment.setCancelledAt(LocalDateTime.now());
                break;
        }
        
        return assignmentRepository.save(assignment);
    }

    @Override
    @Transactional
    public Assignment acceptAssignment(Long id, Long driverId) {
        logger.debug("Driver {} accepting assignment with ID: {}", driverId, id);
        Optional<Assignment> optionalAssignment = assignmentRepository.findByIdAndDriverId(id, driverId);
        if (optionalAssignment.isEmpty()) {
            throw new IllegalArgumentException("Assignment not found or not assigned to this driver");
        }
        
        Assignment assignment = optionalAssignment.get();
        if (assignment.getStatus() != AssignmentStatus.PENDING) {
            throw new IllegalStateException("Assignment is not in PENDING status and cannot be accepted");
        }
        
        assignment.setStatus(AssignmentStatus.ACCEPTED);
        assignment.setAssignedAt(LocalDateTime.now());
        assignment.setUpdatedAt(LocalDateTime.now());
        
        return assignmentRepository.save(assignment);
    }

    @Override
    @Transactional
    public Assignment startAssignment(Long id, Long driverId) {
        logger.debug("Driver {} starting assignment with ID: {}", driverId, id);
        Optional<Assignment> optionalAssignment = assignmentRepository.findByIdAndDriverId(id, driverId);
        if (optionalAssignment.isEmpty()) {
            throw new IllegalArgumentException("Assignment not found or not assigned to this driver");
        }
        
        Assignment assignment = optionalAssignment.get();
        if (assignment.getStatus() != AssignmentStatus.ACCEPTED) {
            throw new IllegalStateException("Assignment must be in ACCEPTED status before it can be started");
        }
        
        assignment.setStatus(AssignmentStatus.STARTED);
        assignment.setStartedAt(LocalDateTime.now());
        assignment.setUpdatedAt(LocalDateTime.now());
        
        return assignmentRepository.save(assignment);
    }

    @Override
    @Transactional
    public Assignment completeAssignment(Long id, Long driverId) {
        logger.debug("Driver {} completing assignment with ID: {}", driverId, id);
        Optional<Assignment> optionalAssignment = assignmentRepository.findByIdAndDriverId(id, driverId);
        if (optionalAssignment.isEmpty()) {
            throw new IllegalArgumentException("Assignment not found or not assigned to this driver");
        }
        
        Assignment assignment = optionalAssignment.get();
        if (assignment.getStatus() != AssignmentStatus.STARTED && assignment.getStatus() != AssignmentStatus.IN_PROGRESS) {
            throw new IllegalStateException("Assignment must be in STARTED or IN_PROGRESS status before it can be completed");
        }
        
        assignment.setStatus(AssignmentStatus.COMPLETED);
        assignment.setCompletedAt(LocalDateTime.now());
        assignment.setUpdatedAt(LocalDateTime.now());
        
        return assignmentRepository.save(assignment);
    }

    @Override
    @Transactional
    public Assignment cancelAssignment(Long id, Long driverId, String cancellationReason) {
        logger.debug("Driver {} cancelling assignment with ID: {}, reason: {}", driverId, id, cancellationReason);
        Optional<Assignment> optionalAssignment = assignmentRepository.findByIdAndDriverId(id, driverId);
        if (optionalAssignment.isEmpty()) {
            throw new IllegalArgumentException("Assignment not found or not assigned to this driver");
        }
        
        Assignment assignment = optionalAssignment.get();
        if (assignment.getStatus() == AssignmentStatus.COMPLETED || assignment.getStatus() == AssignmentStatus.CANCELLED) {
            throw new IllegalStateException("Assignment is already in a final state and cannot be cancelled");
        }
        
        assignment.setStatus(AssignmentStatus.CANCELLED);
        assignment.setCancellationReason(cancellationReason);
        assignment.setCancelledAt(LocalDateTime.now());
        assignment.setUpdatedAt(LocalDateTime.now());
        
        return assignmentRepository.save(assignment);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Assignment> getAssignmentsByDriverAndDateRange(
            Long driverId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        logger.debug("Getting assignments for driver with ID: {} between {} and {}", driverId, startDate, endDate);
        return assignmentRepository.findByDriverIdAndDateRange(driverId, startDate, endDate, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public long countAssignmentsByDriverAndStatus(Long driverId, AssignmentStatus status) {
        logger.debug("Counting assignments for driver with ID: {} with status: {}", driverId, status);
        return assignmentRepository.countByDriverIdAndStatus(driverId, status);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Assignment> getAssignmentsBySyncStatus(String syncStatus, Pageable pageable) {
        logger.debug("Getting assignments with sync status: {}", syncStatus);
        return assignmentRepository.findBySyncStatus(syncStatus, pageable);
    }

    @Override
    @Transactional
    public Assignment updateAssignmentSyncStatus(Long id, String syncStatus) {
        logger.debug("Updating sync status to {} for assignment with ID: {}", syncStatus, id);
        Optional<Assignment> optionalAssignment = assignmentRepository.findById(id);
        if (optionalAssignment.isEmpty()) {
            throw new IllegalArgumentException("Cannot update sync status for non-existent assignment");
        }
        
        Assignment assignment = optionalAssignment.get();
        assignment.setSyncStatus(syncStatus);
        assignment.setUpdatedAt(LocalDateTime.now());
        
        return assignmentRepository.save(assignment);
    }

    @Override
    @Transactional
    public boolean deleteAssignment(Long id) {
        logger.debug("Soft deleting assignment with ID: {}", id);
        Optional<Assignment> optionalAssignment = assignmentRepository.findById(id);
        if (optionalAssignment.isEmpty()) {
            return false;
        }
        
        Assignment assignment = optionalAssignment.get();
        assignment.setIsDeleted(true);
        assignment.setUpdatedAt(LocalDateTime.now());
        assignmentRepository.save(assignment);
        
        return true;
    }
}
