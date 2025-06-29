package com.exalt.courier.courier.branch.service.impl;

import com.microsocial.courier.branch.model.ShipmentTask;
import com.microsocial.courier.branch.model.TaskStatus;
import com.microsocial.courier.branch.model.TaskType;
import com.microsocial.courier.branch.repository.ShipmentTaskRepository;
import com.microsocial.courier.branch.service.ShipmentTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ShipmentTaskServiceImpl implements ShipmentTaskService {

    private final ShipmentTaskRepository shipmentTaskRepository;

    @Autowired
    public ShipmentTaskServiceImpl(ShipmentTaskRepository shipmentTaskRepository) {
        this.shipmentTaskRepository = shipmentTaskRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShipmentTask> getAllShipmentTasks() {
        return shipmentTaskRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ShipmentTask> getShipmentTaskById(Long id) {
        return shipmentTaskRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShipmentTask> findShipmentTasksByAssignmentId(Long assignmentId) {
        return shipmentTaskRepository.findByAssignmentId(assignmentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShipmentTask> findShipmentTasksByTaskType(TaskType taskType) {
        return shipmentTaskRepository.findByTaskType(taskType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShipmentTask> findShipmentTasksByStatus(TaskStatus status) {
        return shipmentTaskRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShipmentTask> findShipmentTasksByShipmentId(String shipmentId) {
        return shipmentTaskRepository.findByShipmentId(shipmentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShipmentTask> findShipmentTasksByAssignmentAndStatus(Long assignmentId, TaskStatus status) {
        return shipmentTaskRepository.findByAssignmentIdAndStatus(assignmentId, status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShipmentTask> findShipmentTasksByScheduledTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return shipmentTaskRepository.findByScheduledTimeRange(startTime, endTime);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShipmentTask> findShipmentTasksByBranchAndScheduledTimeRange(Long branchId, LocalDateTime startTime, LocalDateTime endTime) {
        return shipmentTaskRepository.findByBranchIdAndScheduledTimeRange(branchId, startTime, endTime);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShipmentTask> findShipmentTasksByAssignmentOrderBySequence(Long assignmentId) {
        return shipmentTaskRepository.findByAssignmentIdOrderBySequenceAsc(assignmentId);
    }

    @Override
    public ShipmentTask saveShipmentTask(ShipmentTask shipmentTask) {
        LocalDateTime now = LocalDateTime.now();
        
        if (shipmentTask.getId() == null) {
            shipmentTask.setCreatedAt(now);
            // Generate a task code if not already set
            if (shipmentTask.getTaskCode() == null) {
                shipmentTask.setTaskCode("TASK-" + System.currentTimeMillis());
            }
        }
        
        shipmentTask.setUpdatedAt(now);
        return shipmentTaskRepository.save(shipmentTask);
    }

    @Override
    public ShipmentTask updateShipmentTaskStatus(Long id, TaskStatus status) {
        ShipmentTask task = shipmentTaskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Shipment task not found with id: " + id));
        
        task.setStatus(status);
        
        // Set start and completion times based on status
        if (status == TaskStatus.IN_PROGRESS && task.getStartedAt() == null) {
            task.setStartedAt(LocalDateTime.now());
        } else if (status == TaskStatus.COMPLETED && task.getCompletedAt() == null) {
            task.setCompletedAt(LocalDateTime.now());
        }
        
        task.setUpdatedAt(LocalDateTime.now());
        return shipmentTaskRepository.save(task);
    }

    @Override
    public ShipmentTask updateShipmentTaskSequence(Long id, Integer sequenceOrder) {
        ShipmentTask task = shipmentTaskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Shipment task not found with id: " + id));
        
        task.setSequenceOrder(sequenceOrder);
        task.setUpdatedAt(LocalDateTime.now());
        return shipmentTaskRepository.save(task);
    }

    @Override
    public void deleteShipmentTask(Long id) {
        shipmentTaskRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return shipmentTaskRepository.existsById(id);
    }
}
