package com.exalt.courierservices.management.$1;

import com.exalt.courier.management.assignment.model.Assignment;
import com.exalt.courier.management.assignment.model.AssignmentStatus;
import com.exalt.courier.management.assignment.repository.AssignmentRepository;
import com.exalt.courier.management.assignment.service.AssignmentService;
import com.exalt.courier.management.assignment.validation.AssignmentValidator;
import com.exalt.courier.management.courier.model.Courier;
import com.exalt.courier.management.courier.model.CourierStatus;
import com.exalt.courier.management.courier.repository.CourierRepository;
import com.exalt.courier.management.exception.BusinessException;
import com.exalt.courier.management.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the AssignmentService interface.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AssignmentServiceImpl implements AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final CourierRepository courierRepository;
    private final AssignmentValidator assignmentValidator;

    @Override
    @Transactional
    public Assignment createAssignment(Assignment assignment) {
        log.info("Creating new assignment for order ID: {}", assignment.getOrderId());
        
        // Generate a unique assignment ID if not provided
        if (assignment.getId() == null || assignment.getId().isEmpty()) {
            assignment.setAssignmentId(generateAssignmentId());
        }
        
        // Set default values if not provided
        if (assignment.getStatus() == null) {
            assignment.setStatus(AssignmentStatus.CREATED);
        }
        
        // Validate the assignment
        assignmentValidator.validateForCreation(assignment);
        
        return assignmentRepository.save(assignment);
    }

    @Override
    @Transactional
    public Assignment updateAssignment(Assignment assignment) {
        log.info("Updating assignment with ID: {}", assignment.getId());
        
        // Ensure the assignment exists
        if (!assignmentRepository.existsById(assignment.getId())) {
            throw new ResourceNotFoundException("Assignment not found with ID: " + assignment.getId());
        }
        
        // Validate the assignment
        assignmentValidator.validateForUpdate(assignment);
        
        return assignmentRepository.save(assignment);
    }

    @Override
    public Optional<Assignment> getAssignmentById(String id) {
        return assignmentRepository.findById(id);
    }

    @Override
    public Optional<Assignment> getAssignmentByAssignmentId(String assignmentId) {
        return assignmentRepository.findByAssignmentId(assignmentId);
    }

    @Override
    public Page<Assignment> getAllAssignments(Pageable pageable) {
        return assignmentRepository.findAll(pageable);
    }

    @Override
    public Page<Assignment> getAssignmentsByStatus(AssignmentStatus status, Pageable pageable) {
        return assignmentRepository.findByStatus(status, pageable);
    }

    @Override
    @Transactional
    public boolean deleteAssignment(String id) {
        log.info("Deleting assignment with ID: {}", id);
        
        if (assignmentRepository.existsById(id)) {
            assignmentRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public Assignment assignToCourier(String assignmentId, String courierId) {
        log.info("Assigning assignment {} to courier {}", assignmentId, courierId);
        
        Assignment assignment = getAssignmentByIdOrThrow(assignmentId);
        Courier courier = getCourierByIdOrThrow(courierId);
        
        // Check if assignment is in a state that can be assigned
        if (assignment.getStatus() != AssignmentStatus.CREATED && 
            assignment.getStatus() != AssignmentStatus.REJECTED) {
            throw new BusinessException("Assignment cannot be assigned in current state: " + assignment.getStatus());
        }
        
        // Check if courier is available
        if (!courier.getStatus().canAcceptAssignments()) {
            throw new BusinessException("Courier is not available. Current status: " + courier.getStatus());
        }
        
        assignment.setCourier(courier);
        assignment.setStatus(AssignmentStatus.ASSIGNED);
        assignment.setAssignedAt(LocalDateTime.now());
        
        return assignmentRepository.save(assignment);
    }

    @Override
    @Transactional
    public Assignment acceptAssignment(String assignmentId) {
        log.info("Accepting assignment {}", assignmentId);
        
        Assignment assignment = getAssignmentByIdOrThrow(assignmentId);
        
        // Check if assignment is in a state that can be accepted
        if (assignment.getStatus() != AssignmentStatus.ASSIGNED) {
            throw new BusinessException("Assignment cannot be accepted in current state: " + assignment.getStatus());
        }
        
        assignment.setStatus(AssignmentStatus.ACCEPTED);
        
        return assignmentRepository.save(assignment);
    }

    @Override
    @Transactional
    public Assignment startAssignment(String assignmentId) {
        log.info("Starting assignment {}", assignmentId);
        
        Assignment assignment = getAssignmentByIdOrThrow(assignmentId);
        
        // Check if assignment is in a state that can be started
        if (assignment.getStatus() != AssignmentStatus.ACCEPTED) {
            throw new BusinessException("Assignment cannot be started in current state: " + assignment.getStatus());
        }
        
        assignment.setStatus(AssignmentStatus.IN_PROGRESS);
        assignment.setActualStartTime(LocalDateTime.now());
        
        // Update courier status
        Courier courier = assignment.getCourier();
        if (courier != null) {
            courier.setStatus(CourierStatus.BUSY);
            courierRepository.save(courier);
        }
        
        return assignmentRepository.save(assignment);
    }

    @Override
    @Transactional
    public Assignment completeAssignment(String assignmentId) {
        log.info("Completing assignment {}", assignmentId);
        
        Assignment assignment = getAssignmentByIdOrThrow(assignmentId);
        
        // Check if assignment is in a state that can be completed
        if (assignment.getStatus() != AssignmentStatus.IN_PROGRESS) {
            throw new BusinessException("Assignment cannot be completed in current state: " + assignment.getStatus());
        }
        
        assignment.setStatus(AssignmentStatus.COMPLETED);
        assignment.setActualEndTime(LocalDateTime.now());
        
        // Update courier status
        Courier courier = assignment.getCourier();
        if (courier != null) {
            courier.setStatus(CourierStatus.AVAILABLE);
            courierRepository.save(courier);
        }
        
        return assignmentRepository.save(assignment);
    }

    @Override
    @Transactional
    public Assignment cancelAssignment(String assignmentId, String reason) {
        log.info("Cancelling assignment {} with reason: {}", assignmentId, reason);
        
        Assignment assignment = getAssignmentByIdOrThrow(assignmentId);
        
        // Check if assignment is in a state that can be cancelled
        if (assignment.getStatus() == AssignmentStatus.COMPLETED || 
            assignment.getStatus() == AssignmentStatus.CANCELLED ||
            assignment.getStatus() == AssignmentStatus.FAILED) {
            throw new BusinessException("Assignment cannot be cancelled in current state: " + assignment.getStatus());
        }
        
        assignment.setStatus(AssignmentStatus.CANCELLED);
        assignment.setNotes(assignment.getNotes() != null 
            ? assignment.getNotes() + "\nCancellation reason: " + reason 
            : "Cancellation reason: " + reason);
        assignment.setActualEndTime(LocalDateTime.now());
        
        // Update courier status if the assignment was actively being worked on
        Courier courier = assignment.getCourier();
        if (courier != null && 
            (assignment.getStatus() == AssignmentStatus.ASSIGNED || 
             assignment.getStatus() == AssignmentStatus.ACCEPTED || 
             assignment.getStatus() == AssignmentStatus.IN_PROGRESS)) {
            courier.setStatus(CourierStatus.AVAILABLE);
            courierRepository.save(courier);
        }
        
        return assignmentRepository.save(assignment);
    }

    @Override
    @Transactional
    public Assignment rejectAssignment(String assignmentId, String reason) {
        log.info("Rejecting assignment {} with reason: {}", assignmentId, reason);
        
        Assignment assignment = getAssignmentByIdOrThrow(assignmentId);
        
        // Check if assignment is in a state that can be rejected
        if (assignment.getStatus() != AssignmentStatus.ASSIGNED) {
            throw new BusinessException("Assignment cannot be rejected in current state: " + assignment.getStatus());
        }
        
        assignment.setStatus(AssignmentStatus.REJECTED);
        assignment.setNotes(assignment.getNotes() != null 
            ? assignment.getNotes() + "\nRejection reason: " + reason 
            : "Rejection reason: " + reason);
        
        return assignmentRepository.save(assignment);
    }

    @Override
    @Transactional
    public Assignment updateAssignmentStatus(String assignmentId, AssignmentStatus status) {
        log.info("Updating assignment {} status to {}", assignmentId, status);
        
        Assignment assignment = getAssignmentByIdOrThrow(assignmentId);
        
        // Validate the status transition
        validateStatusTransition(assignment.getStatus(), status);
        
        // Update status with side effects
        switch (status) {
            case ASSIGNED:
                if (assignment.getCourier() == null) {
                    throw new BusinessException("Assignment must have a courier to be assigned");
                }
                assignment.setAssignedAt(LocalDateTime.now());
                break;
            case IN_PROGRESS:
                assignment.setActualStartTime(LocalDateTime.now());
                break;
            case COMPLETED:
            case CANCELLED:
            case FAILED:
                assignment.setActualEndTime(LocalDateTime.now());
                break;
            default:
                // No special handling for other statuses
        }
        
        assignment.setStatus(status);
        return assignmentRepository.save(assignment);
    }

    @Override
    public Page<Assignment> getAssignmentsByCourier(String courierId, Pageable pageable) {
        Courier courier = getCourierByIdOrThrow(courierId);
        return assignmentRepository.findByCourier(courier, pageable);
    }

    @Override
    public Page<Assignment> getAssignmentsByCourierAndStatus(String courierId, AssignmentStatus status, Pageable pageable) {
        Courier courier = getCourierByIdOrThrow(courierId);
        return assignmentRepository.findByCourierAndStatus(courier, status, pageable);
    }

    @Override
    public List<Assignment> getActiveAssignmentsByCourier(String courierId) {
        Courier courier = getCourierByIdOrThrow(courierId);
        return assignmentRepository.findActiveByCourier(courier);
    }

    @Override
    public List<Assignment> getOverdueAssignments() {
        return assignmentRepository.findOverdueAssignments(LocalDateTime.now());
    }

    @Override
    public List<Assignment> getAssignmentsByOrderId(String orderId) {
        return assignmentRepository.findByOrderId(orderId);
    }

    @Override
    public List<Assignment> searchAssignmentsByAssignmentId(String assignmentIdPattern) {
        return assignmentRepository.findByAssignmentIdContaining(assignmentIdPattern);
    }

    @Override
    public String generateAssignmentId() {
        // Format: AS-yyyyMMdd-randomUUID(first 8 chars)
        String datePart = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uuidPart = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "AS-" + datePart + "-" + uuidPart;
    }

    /**
     * Validates a status transition.
     *
     * @param currentStatus the current status
     * @param newStatus the new status
     * @throws BusinessException if the transition is not allowed
     */
    private void validateStatusTransition(AssignmentStatus currentStatus, AssignmentStatus newStatus) {
        // Terminal states cannot be changed
        if (currentStatus.isTerminal()) {
            throw new BusinessException("Cannot change status of a terminal assignment: " + currentStatus);
        }
        
        // Validate specific transitions
        switch (currentStatus) {
            case CREATED:
                if (newStatus != AssignmentStatus.ASSIGNED && newStatus != AssignmentStatus.CANCELLED) {
                    throw new BusinessException("Assignment in CREATED state can only be ASSIGNED or CANCELLED");
                }
                break;
            case ASSIGNED:
                if (newStatus != AssignmentStatus.ACCEPTED && 
                    newStatus != AssignmentStatus.REJECTED && 
                    newStatus != AssignmentStatus.CANCELLED) {
                    throw new BusinessException("Assignment in ASSIGNED state can only be ACCEPTED, REJECTED or CANCELLED");
                }
                break;
            case ACCEPTED:
                if (newStatus != AssignmentStatus.IN_PROGRESS && newStatus != AssignmentStatus.CANCELLED) {
                    throw new BusinessException("Assignment in ACCEPTED state can only be IN_PROGRESS or CANCELLED");
                }
                break;
            case IN_PROGRESS:
                if (newStatus != AssignmentStatus.COMPLETED && 
                    newStatus != AssignmentStatus.CANCELLED && 
                    newStatus != AssignmentStatus.FAILED &&
                    newStatus != AssignmentStatus.DELAYED) {
                    throw new BusinessException("Assignment in IN_PROGRESS state can only be COMPLETED, CANCELLED, FAILED or DELAYED");
                }
                break;
            case DELAYED:
                if (newStatus != AssignmentStatus.IN_PROGRESS && 
                    newStatus != AssignmentStatus.CANCELLED && 
                    newStatus != AssignmentStatus.FAILED) {
                    throw new BusinessException("Assignment in DELAYED state can only be IN_PROGRESS, CANCELLED or FAILED");
                }
                break;
            default:
                throw new BusinessException("Unexpected current status: " + currentStatus);
        }
    }

    /**
     * Retrieves an assignment by its assignment ID or throws an exception if not found.
     *
     * @param assignmentId the assignment ID
     * @return the assignment
     * @throws ResourceNotFoundException if the assignment is not found
     */
    private Assignment getAssignmentByIdOrThrow(String assignmentId) {
        return assignmentRepository.findByAssignmentId(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with assignmentId: " + assignmentId));
    }

    /**
     * Retrieves a courier by its courier ID or throws an exception if not found.
     *
     * @param courierId the courier ID
     * @return the courier
     * @throws ResourceNotFoundException if the courier is not found
     */
    private Courier getCourierByIdOrThrow(String courierId) {
        return courierRepository.findByCourierId(courierId)
                .orElseThrow(() -> new ResourceNotFoundException("Courier not found with courierId: " + courierId));
    }
} 