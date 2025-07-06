package com.gogidix.courier.branch.service;

import com.gogidix.courier.branch.model.ShipmentTask;
import com.gogidix.courier.branch.model.TaskStatus;
import com.gogidix.courier.branch.model.TaskType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ShipmentTaskService {
    List<ShipmentTask> getAllShipmentTasks();
    Optional<ShipmentTask> getShipmentTaskById(Long id);
    List<ShipmentTask> findShipmentTasksByAssignmentId(Long assignmentId);
    List<ShipmentTask> findShipmentTasksByTaskType(TaskType taskType);
    List<ShipmentTask> findShipmentTasksByStatus(TaskStatus status);
    List<ShipmentTask> findShipmentTasksByShipmentId(String shipmentId);
    List<ShipmentTask> findShipmentTasksByAssignmentAndStatus(Long assignmentId, TaskStatus status);
    List<ShipmentTask> findShipmentTasksByScheduledTimeRange(LocalDateTime startTime, LocalDateTime endTime);
    List<ShipmentTask> findShipmentTasksByBranchAndScheduledTimeRange(Long branchId, LocalDateTime startTime, LocalDateTime endTime);
    List<ShipmentTask> findShipmentTasksByAssignmentOrderBySequence(Long assignmentId);
    ShipmentTask saveShipmentTask(ShipmentTask shipmentTask);
    ShipmentTask updateShipmentTaskStatus(Long id, TaskStatus status);
    ShipmentTask updateShipmentTaskSequence(Long id, Integer sequenceOrder);
    void deleteShipmentTask(Long id);
    boolean existsById(Long id);
}
