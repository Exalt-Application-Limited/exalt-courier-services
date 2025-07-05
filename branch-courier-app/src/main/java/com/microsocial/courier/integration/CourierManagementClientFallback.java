package com.gogidix.courier.courier.integration;

import com.microsocial.courier.model.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Fallback implementation for CourierManagementClient when the service is unavailable
 */
@Slf4j
@Component
public class CourierManagementClientFallback implements CourierManagementClient {

    // Common fallback response message
    private static final String FALLBACK_MESSAGE = "Courier Management Service is currently unavailable. Using fallback response.";

    @Override
    public PagedResponseDTO<AssignmentDTO> getAssignments(Long branchId, Long courierId, String status, int page, int size, String sort) {
        log.warn(FALLBACK_MESSAGE);
        return new PagedResponseDTO<>(Collections.emptyList(), page, size, 0, 0, true);
    }

    @Override
    public AssignmentDTO getAssignment(Long id) {
        log.warn("{}. Assignment ID: {}", FALLBACK_MESSAGE, id);
        return null;
    }

    @Override
    public AssignmentDTO createAssignment(AssignmentDTO assignmentDTO) {
        log.warn("{}. Unable to create assignment.", FALLBACK_MESSAGE);
        return null;
    }

    @Override
    public AssignmentDTO updateAssignment(Long id, AssignmentDTO assignmentDTO) {
        log.warn("{}. Unable to update assignment ID: {}", FALLBACK_MESSAGE, id);
        return null;
    }

    @Override
    public AssignmentDTO updateAssignmentStatus(Long id, String status) {
        log.warn("{}. Unable to update assignment status. Assignment ID: {}, Status: {}", FALLBACK_MESSAGE, id, status);
        return null;
    }

    @Override
    public Long countAssignmentsByBranchAndStatus(Long branchId, String status) {
        log.warn("{}. Unable to count assignments. Branch ID: {}, Status: {}", FALLBACK_MESSAGE, branchId, status);
        return 0L;
    }

    @Override
    public List<AssignmentTaskDTO> getAssignmentTasks(Long assignmentId) {
        log.warn("{}. Unable to retrieve tasks for assignment ID: {}", FALLBACK_MESSAGE, assignmentId);
        return Collections.emptyList();
    }

    @Override
    public AssignmentTaskDTO getAssignmentTask(Long assignmentId, Long taskId) {
        log.warn("{}. Unable to retrieve task. Assignment ID: {}, Task ID: {}", FALLBACK_MESSAGE, assignmentId, taskId);
        return null;
    }

    @Override
    public AssignmentTaskDTO createAssignmentTask(Long assignmentId, AssignmentTaskDTO taskDTO) {
        log.warn("{}. Unable to create task for assignment ID: {}", FALLBACK_MESSAGE, assignmentId);
        return null;
    }

    @Override
    public AssignmentTaskDTO updateAssignmentTask(Long assignmentId, Long taskId, AssignmentTaskDTO taskDTO) {
        log.warn("{}. Unable to update task. Assignment ID: {}, Task ID: {}", FALLBACK_MESSAGE, assignmentId, taskId);
        return null;
    }

    @Override
    public AssignmentTaskDTO updateTaskStatus(Long assignmentId, Long taskId, String status) {
        log.warn("{}. Unable to update task status. Assignment ID: {}, Task ID: {}, Status: {}", 
                FALLBACK_MESSAGE, assignmentId, taskId, status);
        return null;
    }

    @Override
    public List<AssignmentTaskDTO> getOptimalTaskSequence(Long assignmentId) {
        log.warn("{}. Unable to get optimal task sequence for assignment ID: {}", FALLBACK_MESSAGE, assignmentId);
        return Collections.emptyList();
    }

    @Override
    public List<AssignmentTaskDTO> applyOptimalTaskSequence(Long assignmentId) {
        log.warn("{}. Unable to apply optimal task sequence for assignment ID: {}", FALLBACK_MESSAGE, assignmentId);
        return Collections.emptyList();
    }

    @Override
    public PagedResponseDTO<CourierDTO> getCouriers(Long branchId, String status, Boolean available, int page, int size) {
        log.warn("{}. Unable to retrieve couriers for branch ID: {}", FALLBACK_MESSAGE, branchId);
        return new PagedResponseDTO<>(Collections.emptyList(), page, size, 0, 0, true);
    }

    @Override
    public CourierDTO getCourier(Long id) {
        log.warn("{}. Unable to retrieve courier ID: {}", FALLBACK_MESSAGE, id);
        return null;
    }

    @Override
    public CourierDTO createCourier(CourierDTO courierDTO) {
        log.warn("{}. Unable to create courier.", FALLBACK_MESSAGE);
        return null;
    }

    @Override
    public CourierDTO updateCourier(Long id, CourierDTO courierDTO) {
        log.warn("{}. Unable to update courier ID: {}", FALLBACK_MESSAGE, id);
        return null;
    }

    @Override
    public CourierDTO updateCourierStatus(Long id, String status) {
        log.warn("{}. Unable to update courier status. Courier ID: {}, Status: {}", FALLBACK_MESSAGE, id, status);
        return null;
    }

    @Override
    public CourierDTO updateCourierLocation(Long id, Double latitude, Double longitude) {
        log.warn("{}. Unable to update courier location. Courier ID: {}", FALLBACK_MESSAGE, id);
        return null;
    }

    @Override
    public Long countCouriersByBranchAndStatus(Long branchId, String status, Boolean available) {
        log.warn("{}. Unable to count couriers. Branch ID: {}, Status: {}, Available: {}", 
                FALLBACK_MESSAGE, branchId, status, available);
        return 0L;
    }

    @Override
    public List<PerformanceMetricDTO> getCourierMetrics(Long courierId, String metricType, LocalDateTime startDate, LocalDateTime endDate) {
        log.warn("{}. Unable to retrieve courier metrics. Courier ID: {}", FALLBACK_MESSAGE, courierId);
        return Collections.emptyList();
    }

    @Override
    public List<PerformanceMetricDTO> getBranchMetrics(Long branchId, String metricType, LocalDateTime startDate, LocalDateTime endDate) {
        log.warn("{}. Unable to retrieve branch metrics. Branch ID: {}", FALLBACK_MESSAGE, branchId);
        return Collections.emptyList();
    }
}