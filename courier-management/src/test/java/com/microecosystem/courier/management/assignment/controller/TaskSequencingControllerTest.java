package com.microecosystem.courier.management.assignment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microecosystem.courier.management.assignment.dto.AssignmentTaskDTO;
import com.microecosystem.courier.management.assignment.mapper.AssignmentTaskMapper;
import com.microecosystem.courier.management.assignment.model.Assignment;
import com.microecosystem.courier.management.assignment.model.AssignmentTask;
import com.microecosystem.courier.management.assignment.model.TaskStatus;
import com.microecosystem.courier.management.assignment.model.TaskType;
import com.microecosystem.courier.management.assignment.repository.AssignmentRepository;
import com.microecosystem.courier.management.assignment.service.TaskSequencingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskSequencingController.class)
public class TaskSequencingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskSequencingService taskSequencingService;

    @MockBean
    private AssignmentRepository assignmentRepository;

    @MockBean
    private AssignmentTaskMapper taskMapper;

    private Assignment assignment;
    private List<AssignmentTask> tasks;
    private List<AssignmentTaskDTO> taskDTOs;
    private String assignmentId;

    @BeforeEach
    void setUp() {
        assignmentId = "test-assignment-123";
        
        // Create assignment
        assignment = new Assignment();
        assignment.setId(1L);
        assignment.setAssignmentId(assignmentId);
        
        // Create tasks
        tasks = new ArrayList<>();
        taskDTOs = new ArrayList<>();
        
        // Task 1: Pickup
        AssignmentTask task1 = createTask(1L, TaskType.PICKUP, 40.7128, -74.0060);
        task1.setReferenceCode("PKG-001");
        tasks.add(task1);
        
        AssignmentTaskDTO dto1 = createTaskDTO(1L, TaskType.PICKUP, 40.7128, -74.0060);
        dto1.setReferenceCode("PKG-001");
        taskDTOs.add(dto1);
        
        // Task 2: Delivery
        AssignmentTask task2 = createTask(2L, TaskType.DELIVERY, 40.6782, -73.9442);
        task2.setReferenceCode("PKG-001");
        tasks.add(task2);
        
        AssignmentTaskDTO dto2 = createTaskDTO(2L, TaskType.DELIVERY, 40.6782, -73.9442);
        dto2.setReferenceCode("PKG-001");
        taskDTOs.add(dto2);
        
        // Set tasks to assignment
        assignment.setTasks(tasks);
        
        // Set up repository mock
        when(assignmentRepository.findByAssignmentId(assignmentId)).thenReturn(Optional.of(assignment));
        
        // Set up mapper mock
        when(taskMapper.toDTOList(any())).thenReturn(taskDTOs);
        
        // Set up service mock
        when(taskSequencingService.determineOptimalSequence(any(Assignment.class))).thenReturn(tasks);
        when(taskSequencingService.applySequence(eq(assignment), any())).thenReturn(assignment);
    }

    @Test
    void getOptimalSequence_ShouldReturnTaskList() throws Exception {
        mockMvc.perform(get("/api/v1/assignments/{assignmentId}/tasks/optimal-sequence", assignmentId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].taskType", is("PICKUP")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].taskType", is("DELIVERY")));
    }

    @Test
    void applyOptimalSequence_ShouldReturnTaskList() throws Exception {
        mockMvc.perform(post("/api/v1/assignments/{assignmentId}/tasks/apply-optimal-sequence", assignmentId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));
    }

    @Test
    void applyCustomSequence_ShouldReturnTaskList() throws Exception {
        // Given
        List<Long> taskIds = List.of(1L, 2L);
        when(taskSequencingService.isValidSequence(eq(assignment), any())).thenReturn(true);
        
        // When & Then
        mockMvc.perform(post("/api/v1/assignments/{assignmentId}/tasks/apply-custom-sequence", assignmentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskIds)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void applyCustomSequence_WithInvalidSequence_ShouldReturnBadRequest() throws Exception {
        // Given
        List<Long> taskIds = List.of(1L, 2L);
        when(taskSequencingService.isValidSequence(eq(assignment), any())).thenReturn(false);
        
        // When & Then
        mockMvc.perform(post("/api/v1/assignments/{assignmentId}/tasks/apply-custom-sequence", assignmentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskIds)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void estimateTravelMetrics_ShouldReturnMetrics() throws Exception {
        // Given
        when(taskSequencingService.estimateDistance(any())).thenReturn(25.5);
        when(taskSequencingService.estimateTravelTime(any())).thenReturn(90);
        
        // When & Then
        mockMvc.perform(get("/api/v1/assignments/{assignmentId}/tasks/estimate-travel-metrics", assignmentId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.distanceKm", is(25.5)))
                .andExpect(jsonPath("$.travelTimeMinutes", is(90)))
                .andExpect(jsonPath("$.travelTimeFormatted", is("1h 30m")));
    }

    @Test
    void canCompleteWithinTimeWindows_ShouldReturnTrue() throws Exception {
        // Given
        LocalDateTime startTime = LocalDateTime.now();
        String startTimeStr = startTime.format(DateTimeFormatter.ISO_DATE_TIME);
        when(taskSequencingService.canCompleteWithinTimeWindows(any(), any())).thenReturn(true);
        
        // When & Then
        mockMvc.perform(get("/api/v1/assignments/{assignmentId}/tasks/can-complete-within-time-windows", assignmentId)
                .param("startTime", startTimeStr))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void canCompleteWithinTimeWindows_ShouldReturnFalse() throws Exception {
        // Given
        LocalDateTime startTime = LocalDateTime.now();
        String startTimeStr = startTime.format(DateTimeFormatter.ISO_DATE_TIME);
        when(taskSequencingService.canCompleteWithinTimeWindows(any(), any())).thenReturn(false);
        
        // When & Then
        mockMvc.perform(get("/api/v1/assignments/{assignmentId}/tasks/can-complete-within-time-windows", assignmentId)
                .param("startTime", startTimeStr))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
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
    
    /**
     * Helper method to create a task DTO with given parameters
     */
    private AssignmentTaskDTO createTaskDTO(Long id, TaskType taskType, double latitude, double longitude) {
        return AssignmentTaskDTO.builder()
                .id(id)
                .assignmentId(assignmentId)
                .taskType(taskType)
                .status(TaskStatus.PENDING)
                .address("Test Address")
                .latitude(latitude)
                .longitude(longitude)
                .estimatedDurationMinutes(10)
                .build();
    }
} 