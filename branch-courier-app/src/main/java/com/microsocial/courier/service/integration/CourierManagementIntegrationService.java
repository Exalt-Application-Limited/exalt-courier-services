package com.exalt.courier.courier.service.integration;

import com.microsocial.courier.annotation.Traced;
import com.microsocial.courier.integration.CourierManagementClient;
import com.microsocial.courier.model.dto.*;
import com.microsocial.courier.service.TracingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for integrating with Courier Management Service
 */
@Slf4j
@Service
public class CourierManagementIntegrationService {

    private final CourierManagementClient courierManagementClient;
    private final TracingService tracingService;

    @Autowired
    public CourierManagementIntegrationService(CourierManagementClient courierManagementClient, TracingService tracingService) {
        this.courierManagementClient = courierManagementClient;
        this.tracingService = tracingService;
    }

    //
    // Assignment methods
    //
    
    @Traced("CourierManagementIntegration.getAssignments")
    public PagedResponseDTO<AssignmentDTO> getAssignments(Long branchId, Long courierId, String status, int page, int size, String sort) {
        tracingService.addTag("branchId", String.valueOf(branchId));
        tracingService.addTag("courierId", courierId != null ? String.valueOf(courierId) : "null");
        tracingService.addTag("status", status != null ? status : "null");
        
        log.debug("Fetching assignments from courier management service. BranchId: {}, CourierId: {}, Status: {}",
                branchId, courierId, status);
        
        try {
            return courierManagementClient.getAssignments(branchId, courierId, status, page, size, sort);
        } catch (Exception e) {
            log.error("Error fetching assignments from courier management service", e);
            tracingService.recordError(e);
            throw e;
        }
    }

    @Traced("CourierManagementIntegration.getAssignment")
    @Cacheable(value = "assignments", key = "#id", unless = "#result == null")
    public AssignmentDTO getAssignment(Long id) {
        tracingService.addTag("assignmentId", String.valueOf(id));
        log.debug("Fetching assignment details from courier management service. ID: {}", id);
        
        try {
            return courierManagementClient.getAssignment(id);
        } catch (Exception e) {
            log.error("Error fetching assignment details. ID: {}", id, e);
            tracingService.recordError(e);
            throw e;
        }
    }

    @Traced("CourierManagementIntegration.createAssignment")
    public AssignmentDTO createAssignment(AssignmentDTO assignmentDTO) {
        tracingService.addTag("branchId", String.valueOf(assignmentDTO.getBranchId()));
        tracingService.addTag("courierId", assignmentDTO.getCourierId() != null ? 
                String.valueOf(assignmentDTO.getCourierId()) : "null");
        
        log.debug("Creating assignment in courier management service. Branch: {}, Courier: {}",
                assignmentDTO.getBranchId(), assignmentDTO.getCourierId());
        
        try {
            return courierManagementClient.createAssignment(assignmentDTO);
        } catch (Exception e) {
            log.error("Error creating assignment", e);
            tracingService.recordError(e);
            throw e;
        }
    }

    @Traced("CourierManagementIntegration.updateAssignment")
    @CacheEvict(value = "assignments", key = "#id")
    public AssignmentDTO updateAssignment(Long id, AssignmentDTO assignmentDTO) {
        tracingService.addTag("assignmentId", String.valueOf(id));
        log.debug("Updating assignment in courier management service. ID: {}", id);
        
        try {
            return courierManagementClient.updateAssignment(id, assignmentDTO);
        } catch (Exception e) {
            log.error("Error updating assignment. ID: {}", id, e);
            tracingService.recordError(e);
            throw e;
        }
    }

    @Traced("CourierManagementIntegration.updateAssignmentStatus")
    @CacheEvict(value = "assignments", key = "#id")
    public AssignmentDTO updateAssignmentStatus(Long id, String status) {
        tracingService.addTag("assignmentId", String.valueOf(id));
        tracingService.addTag("status", status);
        
        log.debug("Updating assignment status in courier management service. ID: {}, Status: {}", id, status);
        
        try {
            return courierManagementClient.updateAssignmentStatus(id, status);
        } catch (Exception e) {
            log.error("Error updating assignment status. ID: {}, Status: {}", id, status, e);
            tracingService.recordError(e);
            throw e;
        }
    }

    @Traced("CourierManagementIntegration.countAssignmentsByBranchAndStatus")
    @Cacheable(value = "assignment-counts", key = "#branchId + '-' + #status", unless = "#result == null")
    public Long countAssignmentsByBranchAndStatus(Long branchId, String status) {
        tracingService.addTag("branchId", String.valueOf(branchId));
        tracingService.addTag("status", status != null ? status : "null");
        
        log.debug("Counting assignments from courier management service. BranchId: {}, Status: {}", branchId, status);
        
        try {
            return courierManagementClient.countAssignmentsByBranchAndStatus(branchId, status);
        } catch (Exception e) {
            log.error("Error counting assignments. BranchId: {}, Status: {}", branchId, status, e);
            tracingService.recordError(e);
            throw e;
        }
    }

    //
    // Assignment Task methods
    //
    
    @Traced("CourierManagementIntegration.getAssignmentTasks")
    @Cacheable(value = "assignment-tasks", key = "#assignmentId", unless = "#result.isEmpty()")
    public List<AssignmentTaskDTO> getAssignmentTasks(Long assignmentId) {
        tracingService.addTag("assignmentId", String.valueOf(assignmentId));
        log.debug("Fetching assignment tasks from courier management service. AssignmentId: {}", assignmentId);
        
        try {
            return courierManagementClient.getAssignmentTasks(assignmentId);
        } catch (Exception e) {
            log.error("Error fetching assignment tasks. AssignmentId: {}", assignmentId, e);
            tracingService.recordError(e);
            throw e;
        }
    }

    @Traced("CourierManagementIntegration.getAssignmentTask")
    @Cacheable(value = "assignment-task", key = "#assignmentId + '-' + #taskId", unless = "#result == null")
    public AssignmentTaskDTO getAssignmentTask(Long assignmentId, Long taskId) {
        tracingService.addTag("assignmentId", String.valueOf(assignmentId));
        tracingService.addTag("taskId", String.valueOf(taskId));
        
        log.debug("Fetching assignment task from courier management service. AssignmentId: {}, TaskId: {}", 
                assignmentId, taskId);
        
        try {
            return courierManagementClient.getAssignmentTask(assignmentId, taskId);
        } catch (Exception e) {
            log.error("Error fetching assignment task. AssignmentId: {}, TaskId: {}", assignmentId, taskId, e);
            tracingService.recordError(e);
            throw e;
        }
    }

    @Traced("CourierManagementIntegration.createAssignmentTask")
    @CacheEvict(value = "assignment-tasks", key = "#assignmentId")
    public AssignmentTaskDTO createAssignmentTask(Long assignmentId, AssignmentTaskDTO taskDTO) {
        tracingService.addTag("assignmentId", String.valueOf(assignmentId));
        log.debug("Creating assignment task in courier management service. AssignmentId: {}", assignmentId);
        
        try {
            return courierManagementClient.createAssignmentTask(assignmentId, taskDTO);
        } catch (Exception e) {
            log.error("Error creating assignment task. AssignmentId: {}", assignmentId, e);
            tracingService.recordError(e);
            throw e;
        }
    }

    @Traced("CourierManagementIntegration.updateAssignmentTask")
    @CacheEvict(value = {"assignment-tasks", "assignment-task"}, allEntries = true)
    public AssignmentTaskDTO updateAssignmentTask(Long assignmentId, Long taskId, AssignmentTaskDTO taskDTO) {
        tracingService.addTag("assignmentId", String.valueOf(assignmentId));
        tracingService.addTag("taskId", String.valueOf(taskId));
        
        log.debug("Updating assignment task in courier management service. AssignmentId: {}, TaskId: {}", 
                assignmentId, taskId);
        
        try {
            return courierManagementClient.updateAssignmentTask(assignmentId, taskId, taskDTO);
        } catch (Exception e) {
            log.error("Error updating assignment task. AssignmentId: {}, TaskId: {}", assignmentId, taskId, e);
            tracingService.recordError(e);
            throw e;
        }
    }

    @Traced("CourierManagementIntegration.updateTaskStatus")
    @CacheEvict(value = {"assignment-tasks", "assignment-task"}, allEntries = true)
    public AssignmentTaskDTO updateTaskStatus(Long assignmentId, Long taskId, String status) {
        tracingService.addTag("assignmentId", String.valueOf(assignmentId));
        tracingService.addTag("taskId", String.valueOf(taskId));
        tracingService.addTag("status", status);
        
        log.debug("Updating task status in courier management service. AssignmentId: {}, TaskId: {}, Status: {}", 
                assignmentId, taskId, status);
        
        try {
            return courierManagementClient.updateTaskStatus(assignmentId, taskId, status);
        } catch (Exception e) {
            log.error("Error updating task status. AssignmentId: {}, TaskId: {}, Status: {}", 
                    assignmentId, taskId, status, e);
            tracingService.recordError(e);
            throw e;
        }
    }

    @Traced("CourierManagementIntegration.getOptimalTaskSequence")
    public List<AssignmentTaskDTO> getOptimalTaskSequence(Long assignmentId) {
        tracingService.addTag("assignmentId", String.valueOf(assignmentId));
        log.debug("Fetching optimal task sequence from courier management service. AssignmentId: {}", assignmentId);
        
        try {
            return courierManagementClient.getOptimalTaskSequence(assignmentId);
        } catch (Exception e) {
            log.error("Error fetching optimal task sequence. AssignmentId: {}", assignmentId, e);
            tracingService.recordError(e);
            throw e;
        }
    }

    @Traced("CourierManagementIntegration.applyOptimalTaskSequence")
    @CacheEvict(value = {"assignment-tasks", "assignment-task"}, allEntries = true)
    public List<AssignmentTaskDTO> applyOptimalTaskSequence(Long assignmentId) {
        tracingService.addTag("assignmentId", String.valueOf(assignmentId));
        log.debug("Applying optimal task sequence in courier management service. AssignmentId: {}", assignmentId);
        
        try {
            return courierManagementClient.applyOptimalTaskSequence(assignmentId);
        } catch (Exception e) {
            log.error("Error applying optimal task sequence. AssignmentId: {}", assignmentId, e);
            tracingService.recordError(e);
            throw e;
        }
    }

    //
    // Courier methods
    //
    
    @Traced("CourierManagementIntegration.getCouriers")
    public PagedResponseDTO<CourierDTO> getCouriers(Long branchId, String status, Boolean available, int page, int size) {
        tracingService.addTag("branchId", String.valueOf(branchId));
        tracingService.addTag("status", status != null ? status : "null");
        tracingService.addTag("available", available != null ? String.valueOf(available) : "null");
        
        log.debug("Fetching couriers from courier management service. BranchId: {}, Status: {}, Available: {}", 
                branchId, status, available);
        
        try {
            return courierManagementClient.getCouriers(branchId, status, available, page, size);
        } catch (Exception e) {
            log.error("Error fetching couriers. BranchId: {}, Status: {}, Available: {}", 
                    branchId, status, available, e);
            tracingService.recordError(e);
            throw e;
        }
    }

    @Traced("CourierManagementIntegration.getCourier")
    @Cacheable(value = "couriers", key = "#id", unless = "#result == null")
    public CourierDTO getCourier(Long id) {
        tracingService.addTag("courierId", String.valueOf(id));
        log.debug("Fetching courier details from courier management service. ID: {}", id);
        
        try {
            return courierManagementClient.getCourier(id);
        } catch (Exception e) {
            log.error("Error fetching courier details. ID: {}", id, e);
            tracingService.recordError(e);
            throw e;
        }
    }

    @Traced("CourierManagementIntegration.createCourier")
    public CourierDTO createCourier(CourierDTO courierDTO) {
        tracingService.addTag("branchId", String.valueOf(courierDTO.getBranchId()));
        log.debug("Creating courier in courier management service. Branch: {}", courierDTO.getBranchId());
        
        try {
            return courierManagementClient.createCourier(courierDTO);
        } catch (Exception e) {
            log.error("Error creating courier", e);
            tracingService.recordError(e);
            throw e;
        }
    }

    @Traced("CourierManagementIntegration.updateCourier")
    @CacheEvict(value = "couriers", key = "#id")
    public CourierDTO updateCourier(Long id, CourierDTO courierDTO) {
        tracingService.addTag("courierId", String.valueOf(id));
        log.debug("Updating courier in courier management service. ID: {}", id);
        
        try {
            return courierManagementClient.updateCourier(id, courierDTO);
        } catch (Exception e) {
            log.error("Error updating courier. ID: {}", id, e);
            tracingService.recordError(e);
            throw e;
        }
    }

    @Traced("CourierManagementIntegration.updateCourierStatus")
    @CacheEvict(value = "couriers", key = "#id")
    public CourierDTO updateCourierStatus(Long id, String status) {
        tracingService.addTag("courierId", String.valueOf(id));
        tracingService.addTag("status", status);
        
        log.debug("Updating courier status in courier management service. ID: {}, Status: {}", id, status);
        
        try {
            return courierManagementClient.updateCourierStatus(id, status);
        } catch (Exception e) {
            log.error("Error updating courier status. ID: {}, Status: {}", id, status, e);
            tracingService.recordError(e);
            throw e;
        }
    }

    @Traced("CourierManagementIntegration.updateCourierLocation")
    @CacheEvict(value = "couriers", key = "#id")
    public CourierDTO updateCourierLocation(Long id, Double latitude, Double longitude) {
        tracingService.addTag("courierId", String.valueOf(id));
        tracingService.addTag("lat", String.valueOf(latitude));
        tracingService.addTag("lng", String.valueOf(longitude));
        
        log.debug("Updating courier location in courier management service. ID: {}, Lat: {}, Lng: {}", 
                id, latitude, longitude);
        
        try {
            return courierManagementClient.updateCourierLocation(id, latitude, longitude);
        } catch (Exception e) {
            log.error("Error updating courier location. ID: {}", id, e);
            tracingService.recordError(e);
            throw e;
        }
    }

    @Traced("CourierManagementIntegration.countCouriersByBranchAndStatus")
    @Cacheable(value = "courier-counts", key = "#branchId + '-' + #status + '-' + #available", unless = "#result == null")
    public Long countCouriersByBranchAndStatus(Long branchId, String status, Boolean available) {
        tracingService.addTag("branchId", String.valueOf(branchId));
        tracingService.addTag("status", status != null ? status : "null");
        tracingService.addTag("available", available != null ? String.valueOf(available) : "null");
        
        log.debug("Counting couriers from courier management service. BranchId: {}, Status: {}, Available: {}", 
                branchId, status, available);
        
        try {
            return courierManagementClient.countCouriersByBranchAndStatus(branchId, status, available);
        } catch (Exception e) {
            log.error("Error counting couriers. BranchId: {}, Status: {}, Available: {}", 
                    branchId, status, available, e);
            tracingService.recordError(e);
            throw e;
        }
    }

    //
    // Performance Metrics methods
    //
    
    @Traced("CourierManagementIntegration.getCourierMetrics")
    @Cacheable(value = "courier-metrics", 
            key = "#courierId + '-' + #metricType + '-' + #startDate + '-' + #endDate", 
            unless = "#result.isEmpty()")
    public List<PerformanceMetricDTO> getCourierMetrics(Long courierId, String metricType, 
                                                 LocalDateTime startDate, LocalDateTime endDate) {
        tracingService.addTag("courierId", String.valueOf(courierId));
        tracingService.addTag("metricType", metricType != null ? metricType : "null");
        
        log.debug("Fetching courier metrics from courier management service. CourierId: {}, MetricType: {}", 
                courierId, metricType);
        
        try {
            return courierManagementClient.getCourierMetrics(courierId, metricType, startDate, endDate);
        } catch (Exception e) {
            log.error("Error fetching courier metrics. CourierId: {}, MetricType: {}", courierId, metricType, e);
            tracingService.recordError(e);
            throw e;
        }
    }

    @Traced("CourierManagementIntegration.getBranchMetrics")
    @Cacheable(value = "branch-metrics", 
            key = "#branchId + '-' + #metricType + '-' + #startDate + '-' + #endDate", 
            unless = "#result.isEmpty()")
    public List<PerformanceMetricDTO> getBranchMetrics(Long branchId, String metricType, 
                                               LocalDateTime startDate, LocalDateTime endDate) {
        tracingService.addTag("branchId", String.valueOf(branchId));
        tracingService.addTag("metricType", metricType != null ? metricType : "null");
        
        log.debug("Fetching branch metrics from courier management service. BranchId: {}, MetricType: {}", 
                branchId, metricType);
        
        try {
            return courierManagementClient.getBranchMetrics(branchId, metricType, startDate, endDate);
        } catch (Exception e) {
            log.error("Error fetching branch metrics. BranchId: {}, MetricType: {}", branchId, metricType, e);
            tracingService.recordError(e);
            throw e;
        }
    }
}