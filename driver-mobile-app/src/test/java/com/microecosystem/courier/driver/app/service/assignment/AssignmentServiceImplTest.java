package com.microecosystem.courier.driver.app.service.assignment;

import com.microecosystem.courier.driver.app.model.Driver;
import com.microecosystem.courier.driver.app.model.DriverStatus;
import com.microecosystem.courier.driver.app.model.assignment.Assignment;
import com.microecosystem.courier.driver.app.model.assignment.AssignmentStatus;
import com.microecosystem.courier.driver.app.repository.AssignmentRepository;
import com.microecosystem.courier.driver.app.repository.DriverRepository;
import com.microecosystem.courier.driver.app.service.assignment.impl.AssignmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AssignmentServiceImplTest {

    @Mock
    private AssignmentRepository assignmentRepository;

    @Mock
    private DriverRepository driverRepository;

    @InjectMocks
    private AssignmentServiceImpl assignmentService;

    private Driver testDriver;
    private Assignment testAssignment;
    private Assignment pendingAssignment;
    private Assignment acceptedAssignment;
    private Assignment completedAssignment;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        // Set up test data
        testDriver = new Driver();
        testDriver.setId(1L);
        testDriver.setFirstName("John");
        testDriver.setLastName("Doe");
        testDriver.setEmail("john.doe@example.com");
        testDriver.setPhoneNumber("1234567890");
        testDriver.setStatus(DriverStatus.ACTIVE);

        testAssignment = new Assignment();
        testAssignment.setId(1L);
        testAssignment.setDriver(testDriver);
        testAssignment.setStatus(AssignmentStatus.IN_PROGRESS);
        testAssignment.setCreatedAt(LocalDateTime.now().minusDays(1));
        testAssignment.setUpdatedAt(LocalDateTime.now().minusHours(2));

        pendingAssignment = new Assignment();
        pendingAssignment.setId(2L);
        pendingAssignment.setDriver(testDriver);
        pendingAssignment.setStatus(AssignmentStatus.PENDING);
        pendingAssignment.setCreatedAt(LocalDateTime.now().minusDays(1));
        pendingAssignment.setUpdatedAt(LocalDateTime.now().minusHours(2));

        acceptedAssignment = new Assignment();
        acceptedAssignment.setId(3L);
        acceptedAssignment.setDriver(testDriver);
        acceptedAssignment.setStatus(AssignmentStatus.ACCEPTED);
        acceptedAssignment.setCreatedAt(LocalDateTime.now().minusDays(1));
        acceptedAssignment.setUpdatedAt(LocalDateTime.now().minusHours(2));
        acceptedAssignment.setAssignedAt(LocalDateTime.now().minusHours(1));

        completedAssignment = new Assignment();
        completedAssignment.setId(4L);
        completedAssignment.setDriver(testDriver);
        completedAssignment.setStatus(AssignmentStatus.COMPLETED);
        completedAssignment.setCreatedAt(LocalDateTime.now().minusDays(2));
        completedAssignment.setUpdatedAt(LocalDateTime.now().minusHours(3));
        completedAssignment.setCompletedAt(LocalDateTime.now().minusHours(1));

        pageable = PageRequest.of(0, 10);
    }

    @Test
    void getAssignmentsByDriver_ShouldReturnPageOfAssignments() {
        // Arrange
        List<Assignment> assignments = Arrays.asList(testAssignment, pendingAssignment);
        Page<Assignment> assignmentPage = new PageImpl<>(assignments);
        when(assignmentRepository.findByDriverId(anyLong(), any(Pageable.class))).thenReturn(assignmentPage);

        // Act
        Page<Assignment> result = assignmentService.getAssignmentsByDriver(testDriver.getId(), pageable);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        verify(assignmentRepository, times(1)).findByDriverId(testDriver.getId(), pageable);
    }

    @Test
    void getAssignmentsByDriverAndStatus_ShouldReturnFilteredAssignments() {
        // Arrange
        List<Assignment> assignments = Arrays.asList(testAssignment);
        Page<Assignment> assignmentPage = new PageImpl<>(assignments);
        when(assignmentRepository.findByDriverIdAndStatus(anyLong(), any(AssignmentStatus.class), any(Pageable.class)))
                .thenReturn(assignmentPage);

        // Act
        Page<Assignment> result = assignmentService.getAssignmentsByDriverAndStatus(
                testDriver.getId(), AssignmentStatus.IN_PROGRESS, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(AssignmentStatus.IN_PROGRESS, result.getContent().get(0).getStatus());
        verify(assignmentRepository, times(1))
                .findByDriverIdAndStatus(testDriver.getId(), AssignmentStatus.IN_PROGRESS, pageable);
    }

    @Test
    void getActiveAssignmentsByDriver_ShouldReturnActiveAssignments() {
        // Arrange
        List<Assignment> activeAssignments = Arrays.asList(testAssignment, acceptedAssignment);
        when(assignmentRepository.findActiveAssignmentsByDriverId(anyLong())).thenReturn(activeAssignments);

        // Act
        List<Assignment> result = assignmentService.getActiveAssignmentsByDriver(testDriver.getId());

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(assignmentRepository, times(1)).findActiveAssignmentsByDriverId(testDriver.getId());
    }

    @Test
    void getAssignmentById_ShouldReturnAssignment() {
        // Arrange
        when(assignmentRepository.findById(anyLong())).thenReturn(Optional.of(testAssignment));

        // Act
        Optional<Assignment> result = assignmentService.getAssignmentById(testAssignment.getId());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testAssignment.getId(), result.get().getId());
        verify(assignmentRepository, times(1)).findById(testAssignment.getId());
    }

    @Test
    void getAssignmentByIdAndDriverId_ShouldReturnAssignment() {
        // Arrange
        when(assignmentRepository.findByIdAndDriverId(anyLong(), anyLong()))
                .thenReturn(Optional.of(testAssignment));

        // Act
        Optional<Assignment> result = assignmentService.getAssignmentByIdAndDriverId(
                testAssignment.getId(), testDriver.getId());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testAssignment.getId(), result.get().getId());
        assertEquals(testDriver.getId(), result.get().getDriver().getId());
        verify(assignmentRepository, times(1))
                .findByIdAndDriverId(testAssignment.getId(), testDriver.getId());
    }

    @Test
    void createAssignment_ShouldSaveNewAssignment() {
        // Arrange
        Assignment newAssignment = new Assignment();
        newAssignment.setDriver(testDriver);
        newAssignment.setStatus(AssignmentStatus.PENDING);

        when(assignmentRepository.save(any(Assignment.class))).thenReturn(newAssignment);

        // Act
        Assignment result = assignmentService.createAssignment(newAssignment);

        // Assert
        assertNotNull(result);
        assertEquals(AssignmentStatus.PENDING, result.getStatus());
        verify(assignmentRepository, times(1)).save(newAssignment);
    }

    @Test
    void updateAssignment_ShouldUpdateExistingAssignment() {
        // Arrange
        when(assignmentRepository.findById(anyLong())).thenReturn(Optional.of(testAssignment));
        when(assignmentRepository.save(any(Assignment.class))).thenReturn(testAssignment);

        testAssignment.setNotes("Updated notes");

        // Act
        Assignment result = assignmentService.updateAssignment(testAssignment);

        // Assert
        assertNotNull(result);
        assertEquals("Updated notes", result.getNotes());
        verify(assignmentRepository, times(1)).findById(testAssignment.getId());
        verify(assignmentRepository, times(1)).save(testAssignment);
    }

    @Test
    void acceptAssignment_ShouldUpdateStatusToAccepted() {
        // Arrange
        when(assignmentRepository.findByIdAndDriverId(anyLong(), anyLong()))
                .thenReturn(Optional.of(pendingAssignment));
        when(assignmentRepository.save(any(Assignment.class))).thenReturn(pendingAssignment);

        // Act
        Assignment result = assignmentService.acceptAssignment(pendingAssignment.getId(), testDriver.getId());

        // Assert
        assertNotNull(result);
        assertEquals(AssignmentStatus.ACCEPTED, result.getStatus());
        assertNotNull(result.getAssignedAt());
        verify(assignmentRepository, times(1))
                .findByIdAndDriverId(pendingAssignment.getId(), testDriver.getId());
        verify(assignmentRepository, times(1)).save(pendingAssignment);
    }

    @Test
    void startAssignment_ShouldUpdateStatusToStarted() {
        // Arrange
        when(assignmentRepository.findByIdAndDriverId(anyLong(), anyLong()))
                .thenReturn(Optional.of(acceptedAssignment));
        when(assignmentRepository.save(any(Assignment.class))).thenReturn(acceptedAssignment);

        // Act
        Assignment result = assignmentService.startAssignment(acceptedAssignment.getId(), testDriver.getId());

        // Assert
        assertNotNull(result);
        assertEquals(AssignmentStatus.STARTED, result.getStatus());
        assertNotNull(result.getStartedAt());
        verify(assignmentRepository, times(1))
                .findByIdAndDriverId(acceptedAssignment.getId(), testDriver.getId());
        verify(assignmentRepository, times(1)).save(acceptedAssignment);
    }

    @Test
    void completeAssignment_ShouldUpdateStatusToCompleted() {
        // Arrange
        Assignment inProgressAssignment = testAssignment; // Already in IN_PROGRESS status
        when(assignmentRepository.findByIdAndDriverId(anyLong(), anyLong()))
                .thenReturn(Optional.of(inProgressAssignment));
        when(assignmentRepository.save(any(Assignment.class))).thenReturn(inProgressAssignment);

        // Act
        Assignment result = assignmentService.completeAssignment(inProgressAssignment.getId(), testDriver.getId());

        // Assert
        assertNotNull(result);
        assertEquals(AssignmentStatus.COMPLETED, result.getStatus());
        assertNotNull(result.getCompletedAt());
        verify(assignmentRepository, times(1))
                .findByIdAndDriverId(inProgressAssignment.getId(), testDriver.getId());
        verify(assignmentRepository, times(1)).save(inProgressAssignment);
    }

    @Test
    void cancelAssignment_ShouldUpdateStatusToCancelled() {
        // Arrange
        String cancellationReason = "Weather conditions";
        when(assignmentRepository.findByIdAndDriverId(anyLong(), anyLong()))
                .thenReturn(Optional.of(testAssignment));
        when(assignmentRepository.save(any(Assignment.class))).thenReturn(testAssignment);

        // Act
        Assignment result = assignmentService.cancelAssignment(
                testAssignment.getId(), testDriver.getId(), cancellationReason);

        // Assert
        assertNotNull(result);
        assertEquals(AssignmentStatus.CANCELLED, result.getStatus());
        assertEquals(cancellationReason, result.getCancellationReason());
        assertNotNull(result.getCancelledAt());
        verify(assignmentRepository, times(1))
                .findByIdAndDriverId(testAssignment.getId(), testDriver.getId());
        verify(assignmentRepository, times(1)).save(testAssignment);
    }

    @Test
    void deleteAssignment_ShouldSoftDeleteAssignment() {
        // Arrange
        when(assignmentRepository.findById(anyLong())).thenReturn(Optional.of(testAssignment));
        when(assignmentRepository.save(any(Assignment.class))).thenReturn(testAssignment);

        // Act
        boolean result = assignmentService.deleteAssignment(testAssignment.getId());

        // Assert
        assertTrue(result);
        assertTrue(testAssignment.getIsDeleted());
        verify(assignmentRepository, times(1)).findById(testAssignment.getId());
        verify(assignmentRepository, times(1)).save(testAssignment);
    }

    @Test
    void deleteAssignment_ShouldReturnFalseWhenAssignmentNotFound() {
        // Arrange
        when(assignmentRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act
        boolean result = assignmentService.deleteAssignment(999L);

        // Assert
        assertFalse(result);
        verify(assignmentRepository, times(1)).findById(999L);
        verify(assignmentRepository, never()).save(any(Assignment.class));
    }
}
