package com.microecosystem.courier.management.assignment.service;

import com.microecosystem.courier.management.assignment.model.Assignment;
import com.microecosystem.courier.management.assignment.model.AssignmentTask;
import com.microecosystem.courier.management.assignment.model.TaskStatus;
import com.microecosystem.courier.management.assignment.model.TaskType;
import com.microecosystem.courier.management.assignment.repository.AssignmentTaskRepository;
import com.microecosystem.courier.management.assignment.service.impl.NearestNeighborTaskSequencingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NearestNeighborTaskSequencingServiceTest {

    @Mock
    private AssignmentTaskRepository taskRepository;

    @InjectMocks
    private NearestNeighborTaskSequencingServiceImpl sequencingService;

    private Assignment assignment;
    private List<AssignmentTask> tasks;

    @BeforeEach
    void setUp() {
        // Create an assignment
        assignment = new Assignment();
        assignment.setId(1L);
        assignment.setAssignmentId(UUID.randomUUID().toString());
        
        // Create tasks
        tasks = new ArrayList<>();
        
        // Task 1: Pickup in downtown
        AssignmentTask task1 = createTask(1L, TaskType.PICKUP, 40.7128, -74.0060); // New York City
        task1.setReferenceCode("PKG-001");
        tasks.add(task1);
        
        // Task 2: Delivery in Brooklyn
        AssignmentTask task2 = createTask(2L, TaskType.DELIVERY, 40.6782, -73.9442); // Brooklyn
        task2.setReferenceCode("PKG-001");
        tasks.add(task2);
        
        // Task 3: Pickup in Queens
        AssignmentTask task3 = createTask(3L, TaskType.PICKUP, 40.7282, -73.7949); // Queens
        task3.setReferenceCode("PKG-002");
        tasks.add(task3);
        
        // Task 4: Delivery in Bronx
        AssignmentTask task4 = createTask(4L, TaskType.DELIVERY, 40.8448, -73.8648); // Bronx
        task4.setReferenceCode("PKG-002");
        tasks.add(task4);
        
        // Set tasks to assignment
        assignment.setTasks(tasks);
        
        // Set up each task's assignment reference
        tasks.forEach(task -> task.setAssignment(assignment));
    }

    @Test
    void determineOptimalSequence_ShouldRespectPickupDeliveryOrder() {
        // When
        List<AssignmentTask> result = sequencingService.determineOptimalSequence(assignment);
        
        // Then
        assertNotNull(result);
        assertEquals(4, result.size());
        
        // Find indexes of tasks in the result
        int pkg1PickupIndex = -1;
        int pkg1DeliveryIndex = -1;
        int pkg2PickupIndex = -1;
        int pkg2DeliveryIndex = -1;
        
        for (int i = 0; i < result.size(); i++) {
            AssignmentTask task = result.get(i);
            if (task.getReferenceCode().equals("PKG-001")) {
                if (task.getTaskType() == TaskType.PICKUP) {
                    pkg1PickupIndex = i;
                } else if (task.getTaskType() == TaskType.DELIVERY) {
                    pkg1DeliveryIndex = i;
                }
            } else if (task.getReferenceCode().equals("PKG-002")) {
                if (task.getTaskType() == TaskType.PICKUP) {
                    pkg2PickupIndex = i;
                } else if (task.getTaskType() == TaskType.DELIVERY) {
                    pkg2DeliveryIndex = i;
                }
            }
        }
        
        // Verify pickup comes before delivery for each package
        assertTrue(pkg1PickupIndex < pkg1DeliveryIndex);
        assertTrue(pkg2PickupIndex < pkg2DeliveryIndex);
    }

    @Test
    void applySequence_ShouldUpdateTaskSequences() {
        // Given
        when(taskRepository.save(any(AssignmentTask.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        Assignment result = sequencingService.applySequence(assignment, tasks);
        
        // Then
        assertNotNull(result);
        verify(taskRepository, times(tasks.size())).save(any(AssignmentTask.class));
        
        // Verify sequence numbers are updated
        for (int i = 0; i < tasks.size(); i++) {
            assertEquals(i + 1, tasks.get(i).getSequence());
        }
    }

    @Test
    void estimateDistance_ShouldCalculateCorrectDistance() {
        // When
        double distance = sequencingService.estimateDistance(tasks);
        
        // Then
        assertTrue(distance > 0);
        // The distance between these points should be approximately 30-40 km
        assertTrue(distance > 20 && distance < 50);
    }

    @Test
    void estimateTravelTime_ShouldCalculateCorrectTime() {
        // When
        int travelTime = sequencingService.estimateTravelTime(tasks);
        
        // Then
        assertTrue(travelTime > 0);
        // Travel time should include both driving time and service time
        // For 4 tasks, service time alone would be 40 minutes (10 min each)
        assertTrue(travelTime > 40);
    }

    @Test
    void canCompleteWithinTimeWindows_ShouldReturnTrueForFeasibleSchedule() {
        // Given
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusHours(2);
        
        tasks.forEach(task -> {
            task.setTimeWindowStart(startTime);
            task.setTimeWindowEnd(endTime);
        });
        
        // When
        boolean canComplete = sequencingService.canCompleteWithinTimeWindows(tasks, startTime);
        
        // Then
        assertTrue(canComplete);
    }

    @Test
    void canCompleteWithinTimeWindows_ShouldReturnFalseForInfeasibleSchedule() {
        // Given
        LocalDateTime startTime = LocalDateTime.now();
        
        // Set very tight time windows that cannot be met
        tasks.get(0).setTimeWindowStart(startTime);
        tasks.get(0).setTimeWindowEnd(startTime.plusMinutes(5));
        
        tasks.get(1).setTimeWindowStart(startTime);
        tasks.get(1).setTimeWindowEnd(startTime.plusMinutes(10));
        
        tasks.get(2).setTimeWindowStart(startTime);
        tasks.get(2).setTimeWindowEnd(startTime.plusMinutes(15));
        
        tasks.get(3).setTimeWindowStart(startTime);
        tasks.get(3).setTimeWindowEnd(startTime.plusMinutes(20));
        
        // When
        boolean canComplete = sequencingService.canCompleteWithinTimeWindows(tasks, startTime);
        
        // Then
        assertFalse(canComplete);
    }

    @Test
    void isValidSequence_ShouldReturnTrueForValidSequence() {
        // Given
        List<AssignmentTask> validSequence = new ArrayList<>();
        validSequence.add(tasks.get(0)); // Pickup PKG-001
        validSequence.add(tasks.get(2)); // Pickup PKG-002
        validSequence.add(tasks.get(1)); // Delivery PKG-001
        validSequence.add(tasks.get(3)); // Delivery PKG-002
        
        // When
        boolean isValid = sequencingService.isValidSequence(assignment, validSequence);
        
        // Then
        assertTrue(isValid);
    }

    @Test
    void isValidSequence_ShouldReturnFalseForInvalidSequence() {
        // Given
        List<AssignmentTask> invalidSequence = new ArrayList<>();
        invalidSequence.add(tasks.get(0)); // Pickup PKG-001
        invalidSequence.add(tasks.get(3)); // Delivery PKG-002 (invalid: pickup for PKG-002 hasn't happened)
        invalidSequence.add(tasks.get(2)); // Pickup PKG-002
        invalidSequence.add(tasks.get(1)); // Delivery PKG-001
        
        // When
        boolean isValid = sequencingService.isValidSequence(assignment, invalidSequence);
        
        // Then
        assertFalse(isValid);
    }

    /**
     * Helper method to create a task with given parameters
     */
    private AssignmentTask createTask(Long id, TaskType taskType, double latitude, double longitude) {
        AssignmentTask task = new AssignmentTask();
        task.setId(id);
        task.setTaskType(taskType);
        task.setStatus(TaskStatus.PENDING);
        task.setAddress("Test Address");
        task.setLatitude(latitude);
        task.setLongitude(longitude);
        task.setEstimatedDurationMinutes(10);
        return task;
    }
} 