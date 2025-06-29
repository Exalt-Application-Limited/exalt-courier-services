package com.microecosystem.courier.driver.app.integration;

import com.microecosystem.courier.driver.app.model.assignment.Assignment;
import com.microecosystem.courier.driver.app.model.assignment.AssignmentStatus;
import com.microecosystem.courier.driver.app.model.assignment.Task;
import com.microecosystem.courier.driver.app.model.assignment.TaskStatus;
import com.microecosystem.courier.driver.app.model.assignment.TaskType;
import com.microecosystem.courier.driver.app.repository.AssignmentRepository;
import com.microecosystem.courier.driver.app.repository.TaskRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for DataSyncController API endpoints.
 * 
 * @author Courier Services Migration Team
 * @version 1.0
 * @since 2025-05-25
 */
@DisplayName("DataSync Controller Integration Tests")
class DataSyncControllerIntegrationTest extends BaseIntegrationTest {

    @MockBean
    private AssignmentRepository assignmentRepository;

    @MockBean
    private TaskRepository taskRepository;

    @Test
    @WithMockUser(roles = "DRIVER")
    @DisplayName("Should retrieve assignments for authenticated driver")
    void shouldRetrieveAssignmentsForDriver() throws Exception {
        // Given
        Long driverId = 1L;
        Assignment assignment = createTestAssignment(driverId);
        when(assignmentRepository.findByDriverId(driverId))
                .thenReturn(Arrays.asList(assignment));

        // When & Then
        mockMvc.perform(get("/api/v1/data/assignments/{driverId}", driverId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(assignment.getId()))
                .andExpect(jsonPath("$[0].driverId").value(assignment.getDriverId()))
                .andExpect(jsonPath("$[0].status").value(assignment.getStatus().toString()));
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    @DisplayName("Should retrieve assignment details by assignment ID")
    void shouldRetrieveAssignmentDetails() throws Exception {
        // Given
        Long assignmentId = 1L;
        Assignment assignment = createTestAssignment(1L);
        when(assignmentRepository.findById(assignmentId))
                .thenReturn(Optional.of(assignment));

        // When & Then
        mockMvc.perform(get("/api/v1/data/assignments/detail/{assignmentId}", assignmentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(assignment.getId()))
                .andExpect(jsonPath("$.driverId").value(assignment.getDriverId()))
                .andExpect(jsonPath("$.status").value(assignment.getStatus().toString()));
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    @DisplayName("Should return 404 when assignment not found")
    void shouldReturn404WhenAssignmentNotFound() throws Exception {
        // Given
        Long assignmentId = 999L;
        when(assignmentRepository.findById(assignmentId))
                .thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/v1/data/assignments/detail/{assignmentId}", assignmentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    @DisplayName("Should update task status successfully")
    void shouldUpdateTaskStatus() throws Exception {
        // Given
        Long taskId = 1L;
        Task task = createTestTask();
        task.setStatus(TaskStatus.IN_PROGRESS);
        when(taskRepository.findById(taskId))
                .thenReturn(Optional.of(task));
        when(taskRepository.save(task))
                .thenReturn(task);

        // When & Then
        mockMvc.perform(put("/api/v1/data/tasks/{taskId}/status", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"COMPLETED\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(task.getId()))
                .andExpect(jsonPath("$.status").exists());
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    @DisplayName("Should return 404 when updating non-existent task")
    void shouldReturn404WhenUpdatingNonExistentTask() throws Exception {
        // Given
        Long taskId = 999L;
        when(taskRepository.findById(taskId))
                .thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(put("/api/v1/data/tasks/{taskId}/status", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"COMPLETED\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    @DisplayName("Should retrieve configuration for device")
    void shouldRetrieveDeviceConfiguration() throws Exception {
        // Given
        String deviceId = "test-device-123";

        // When & Then
        mockMvc.perform(get("/api/v1/data/config/{deviceId}", deviceId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.deviceId").value(deviceId))
                .andExpect(jsonPath("$.syncInterval").exists())
                .andExpect(jsonPath("$.maxRetries").exists());
    }

    @Test
    @DisplayName("Should require authentication for protected endpoints")
    void shouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/data/assignments/1"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Helper method to create test assignment
     */
    private Assignment createTestAssignment(Long driverId) {
        Assignment assignment = new Assignment();
        assignment.setId(1L);
        assignment.setDriverId(driverId);
        assignment.setStatus(AssignmentStatus.ASSIGNED);
        assignment.setCreatedAt(LocalDateTime.now());
        assignment.setUpdatedAt(LocalDateTime.now());
        assignment.setPickupAddress("123 Test Street");
        assignment.setDeliveryAddress("456 Delivery Ave");
        return assignment;
    }

    /**
     * Helper method to create test task
     */
    private Task createTestTask() {
        Task task = new Task();
        task.setId(1L);
        task.setType(TaskType.PICKUP);
        task.setStatus(TaskStatus.PENDING);
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        task.setAddress("123 Test Street");
        return task;
    }
}
