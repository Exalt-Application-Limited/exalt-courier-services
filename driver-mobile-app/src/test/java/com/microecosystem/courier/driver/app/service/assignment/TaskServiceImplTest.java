package com.microecosystem.courier.driver.app.service.assignment;

import com.microecosystem.courier.driver.app.model.Driver;
import com.microecosystem.courier.driver.app.model.assignment.Assignment;
import com.microecosystem.courier.driver.app.model.assignment.AssignmentStatus;
import com.microecosystem.courier.driver.app.model.assignment.Task;
import com.microecosystem.courier.driver.app.model.assignment.TaskStatus;
import com.microecosystem.courier.driver.app.model.assignment.TaskType;
import com.microecosystem.courier.driver.app.repository.AssignmentRepository;
import com.microecosystem.courier.driver.app.repository.TaskRepository;
import com.microecosystem.courier.driver.app.service.assignment.impl.TaskServiceImpl;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private AssignmentRepository assignmentRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    private Driver testDriver;
    private Assignment testAssignment;
    private Task pendingTask;
    private Task inProgressTask;
    private Task completedTask;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        // Set up test data
        testDriver = new Driver();
        testDriver.setId(1L);
        testDriver.setFirstName("John");
        testDriver.setLastName("Doe");

        testAssignment = new Assignment();
        testAssignment.setId(1L);
        testAssignment.setDriver(testDriver);
        testAssignment.setStatus(AssignmentStatus.IN_PROGRESS);
        testAssignment.setCreatedAt(LocalDateTime.now().minusDays(1));
        testAssignment.setUpdatedAt(LocalDateTime.now().minusHours(2));

        pendingTask = new Task();
        pendingTask.setId(1L);
        pendingTask.setAssignment(testAssignment);
        pendingTask.setTaskType(TaskType.DELIVERY);
        pendingTask.setStatus(TaskStatus.PENDING);
        pendingTask.setSequenceNumber(1);
        pendingTask.setAddress("123 Main St");
        pendingTask.setLatitude(new BigDecimal("37.7749"));
        pendingTask.setLongitude(new BigDecimal("-122.4194"));
        pendingTask.setCreatedAt(LocalDateTime.now().minusDays(1));
        pendingTask.setUpdatedAt(LocalDateTime.now().minusHours(2));

        inProgressTask = new Task();
        inProgressTask.setId(2L);
        inProgressTask.setAssignment(testAssignment);
        inProgressTask.setTaskType(TaskType.PICKUP);
        inProgressTask.setStatus(TaskStatus.IN_PROGRESS);
        inProgressTask.setSequenceNumber(2);
        inProgressTask.setAddress("456 Oak St");
        inProgressTask.setLatitude(new BigDecimal("37.7833"));
        inProgressTask.setLongitude(new BigDecimal("-122.4167"));
        inProgressTask.setCreatedAt(LocalDateTime.now().minusDays(1));
        inProgressTask.setUpdatedAt(LocalDateTime.now().minusHours(1));

        completedTask = new Task();
        completedTask.setId(3L);
        completedTask.setAssignment(testAssignment);
        completedTask.setTaskType(TaskType.DELIVERY);
        completedTask.setStatus(TaskStatus.COMPLETED);
        completedTask.setSequenceNumber(3);
        completedTask.setAddress("789 Pine St");
        completedTask.setLatitude(new BigDecimal("37.7913"));
        completedTask.setLongitude(new BigDecimal("-122.4089"));
        completedTask.setCreatedAt(LocalDateTime.now().minusDays(1));
        completedTask.setUpdatedAt(LocalDateTime.now().minusHours(1));
        completedTask.setCompletedAt(LocalDateTime.now().minusMinutes(30));

        pageable = PageRequest.of(0, 10);
    }

    @Test
    void getTasksByAssignment_ShouldReturnAllTasksForAssignment() {
        // Arrange
        List<Task> tasks = Arrays.asList(pendingTask, inProgressTask, completedTask);
        when(taskRepository.findByAssignmentId(anyLong())).thenReturn(tasks);

        // Act
        List<Task> result = taskService.getTasksByAssignment(testAssignment.getId());

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        verify(taskRepository, times(1)).findByAssignmentId(testAssignment.getId());
    }

    @Test
    void getTasksByAssignmentAndStatus_ShouldReturnFilteredTasks() {
        // Arrange
        List<Task> pendingTasks = Collections.singletonList(pendingTask);
        when(taskRepository.findByAssignmentIdAndStatus(anyLong(), any(TaskStatus.class)))
                .thenReturn(pendingTasks);

        // Act
        List<Task> result = taskService.getTasksByAssignmentAndStatus(
                testAssignment.getId(), TaskStatus.PENDING);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(TaskStatus.PENDING, result.get(0).getStatus());
        verify(taskRepository, times(1))
                .findByAssignmentIdAndStatus(testAssignment.getId(), TaskStatus.PENDING);
    }

    @Test
    void getTasksByAssignmentAndType_ShouldReturnFilteredTasks() {
        // Arrange
        List<Task> deliveryTasks = Arrays.asList(pendingTask, completedTask);
        when(taskRepository.findByAssignmentIdAndTaskType(anyLong(), any(TaskType.class)))
                .thenReturn(deliveryTasks);

        // Act
        List<Task> result = taskService.getTasksByAssignmentAndType(
                testAssignment.getId(), TaskType.DELIVERY);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(TaskType.DELIVERY, result.get(0).getTaskType());
        verify(taskRepository, times(1))
                .findByAssignmentIdAndTaskType(testAssignment.getId(), TaskType.DELIVERY);
    }

    @Test
    void getTaskById_ShouldReturnTask() {
        // Arrange
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(pendingTask));

        // Act
        Optional<Task> result = taskService.getTaskById(pendingTask.getId());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(pendingTask.getId(), result.get().getId());
        verify(taskRepository, times(1)).findById(pendingTask.getId());
    }

    @Test
    void getTaskByIdAndAssignmentId_ShouldReturnTask() {
        // Arrange
        when(taskRepository.findByIdAndAssignmentId(anyLong(), anyLong()))
                .thenReturn(Optional.of(pendingTask));

        // Act
        Optional<Task> result = taskService.getTaskByIdAndAssignmentId(
                pendingTask.getId(), testAssignment.getId());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(pendingTask.getId(), result.get().getId());
        assertEquals(testAssignment.getId(), result.get().getAssignment().getId());
        verify(taskRepository, times(1))
                .findByIdAndAssignmentId(pendingTask.getId(), testAssignment.getId());
    }

    @Test
    void createTask_ShouldSaveNewTask() {
        // Arrange
        Task newTask = new Task();
        newTask.setAssignment(testAssignment);
        newTask.setTaskType(TaskType.DELIVERY);
        newTask.setStatus(TaskStatus.PENDING);
        newTask.setAddress("123 New St");
        newTask.setLatitude(new BigDecimal("37.7749"));
        newTask.setLongitude(new BigDecimal("-122.4194"));

        when(taskRepository.save(any(Task.class))).thenReturn(newTask);

        // Act
        Task result = taskService.createTask(newTask);

        // Assert
        assertNotNull(result);
        assertEquals(TaskStatus.PENDING, result.getStatus());
        verify(taskRepository, times(1)).save(newTask);
    }

    @Test
    void updateTask_ShouldUpdateExistingTask() {
        // Arrange
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(pendingTask));
        when(taskRepository.save(any(Task.class))).thenReturn(pendingTask);

        pendingTask.setNotes("Updated notes");

        // Act
        Task result = taskService.updateTask(pendingTask);

        // Assert
        assertNotNull(result);
        assertEquals("Updated notes", result.getNotes());
        verify(taskRepository, times(1)).findById(pendingTask.getId());
        verify(taskRepository, times(1)).save(pendingTask);
    }

    @Test
    void updateTaskStatus_ShouldUpdateStatus() {
        // Arrange
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(pendingTask));
        when(taskRepository.save(any(Task.class))).thenReturn(pendingTask);

        // Act
        Task result = taskService.updateTaskStatus(pendingTask.getId(), TaskStatus.IN_PROGRESS);

        // Assert
        assertNotNull(result);
        assertEquals(TaskStatus.IN_PROGRESS, result.getStatus());
        verify(taskRepository, times(1)).findById(pendingTask.getId());
        verify(taskRepository, times(1)).save(pendingTask);
    }

    @Test
    void startTask_ShouldUpdateStatusToInProgress() {
        // Arrange
        when(taskRepository.findByIdAndAssignmentId(anyLong(), anyLong()))
                .thenReturn(Optional.of(pendingTask));
        when(taskRepository.save(any(Task.class))).thenReturn(pendingTask);

        // Act
        Task result = taskService.startTask(pendingTask.getId(), testAssignment.getId());

        // Assert
        assertNotNull(result);
        assertEquals(TaskStatus.IN_PROGRESS, result.getStatus());
        verify(taskRepository, times(1))
                .findByIdAndAssignmentId(pendingTask.getId(), testAssignment.getId());
        verify(taskRepository, times(1)).save(pendingTask);
    }

    @Test
    void markTaskAsArrived_ShouldUpdateStatusToArrived() {
        // Arrange
        when(taskRepository.findByIdAndAssignmentId(anyLong(), anyLong()))
                .thenReturn(Optional.of(inProgressTask));
        when(taskRepository.save(any(Task.class))).thenReturn(inProgressTask);

        // Act
        Task result = taskService.markTaskAsArrived(inProgressTask.getId(), testAssignment.getId());

        // Assert
        assertNotNull(result);
        assertEquals(TaskStatus.ARRIVED, result.getStatus());
        assertNotNull(result.getActualArrivalTime());
        verify(taskRepository, times(1))
                .findByIdAndAssignmentId(inProgressTask.getId(), testAssignment.getId());
        verify(taskRepository, times(1)).save(inProgressTask);
    }

    @Test
    void completeTask_ShouldUpdateStatusToCompleted() {
        // Arrange
        when(taskRepository.findByIdAndAssignmentId(anyLong(), anyLong()))
                .thenReturn(Optional.of(inProgressTask));
        when(taskRepository.save(any(Task.class))).thenReturn(inProgressTask);

        Map<String, Object> completionData = new HashMap<>();
        completionData.put("notes", "Delivered to receptionist");
        completionData.put("completionCode", "ABC123");

        // Act
        Task result = taskService.completeTask(
                inProgressTask.getId(), testAssignment.getId(), completionData);

        // Assert
        assertNotNull(result);
        assertEquals(TaskStatus.COMPLETED, result.getStatus());
        assertNotNull(result.getCompletedAt());
        assertEquals("Delivered to receptionist", result.getNotes());
        assertEquals("ABC123", result.getCompletionCode());
        verify(taskRepository, times(1))
                .findByIdAndAssignmentId(inProgressTask.getId(), testAssignment.getId());
        verify(taskRepository, times(1)).save(inProgressTask);
    }

    @Test
    void updateTaskSequence_ShouldUpdateSequenceNumber() {
        // Arrange
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(pendingTask));
        when(taskRepository.save(any(Task.class))).thenReturn(pendingTask);

        Integer newSequence = 5;

        // Act
        Task result = taskService.updateTaskSequence(pendingTask.getId(), newSequence);

        // Assert
        assertNotNull(result);
        assertEquals(newSequence, result.getSequenceNumber());
        verify(taskRepository, times(1)).findById(pendingTask.getId());
        verify(taskRepository, times(1)).save(pendingTask);
    }

    @Test
    void getTasksByTimeWindowRange_ShouldReturnTasksInTimeRange() {
        // Arrange
        List<Task> tasks = Arrays.asList(pendingTask, inProgressTask);
        Page<Task> taskPage = new PageImpl<>(tasks);

        LocalDateTime startDate = LocalDateTime.now().minusDays(2);
        LocalDateTime endDate = LocalDateTime.now();

        when(taskRepository.findByTimeWindowRange(any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(taskPage);

        // Act
        Page<Task> result = taskService.getTasksByTimeWindowRange(startDate, endDate, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        verify(taskRepository, times(1))
                .findByTimeWindowRange(startDate, endDate, pageable);
    }

    @Test
    void getNextPendingTaskForAssignment_ShouldReturnNextTask() {
        // Arrange
        when(taskRepository.findNextPendingTaskForAssignment(anyLong()))
                .thenReturn(Optional.of(pendingTask));

        // Act
        Optional<Task> result = taskService.getNextPendingTaskForAssignment(testAssignment.getId());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(pendingTask.getId(), result.get().getId());
        assertEquals(TaskStatus.PENDING, result.get().getStatus());
        verify(taskRepository, times(1))
                .findNextPendingTaskForAssignment(testAssignment.getId());
    }

    @Test
    void deleteTask_ShouldSoftDeleteTask() {
        // Arrange
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(pendingTask));
        when(taskRepository.save(any(Task.class))).thenReturn(pendingTask);

        // Act
        boolean result = taskService.deleteTask(pendingTask.getId());

        // Assert
        assertTrue(result);
        assertTrue(pendingTask.getIsDeleted());
        verify(taskRepository, times(1)).findById(pendingTask.getId());
        verify(taskRepository, times(1)).save(pendingTask);
    }

    @Test
    void deleteTask_ShouldReturnFalseWhenTaskNotFound() {
        // Arrange
        when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act
        boolean result = taskService.deleteTask(999L);

        // Assert
        assertFalse(result);
        verify(taskRepository, times(1)).findById(999L);
        verify(taskRepository, never()).save(any(Task.class));
    }
}
