package com.gogidix.courier.courier.branch.controller.api;

import com.microsocial.courier.branch.model.ShipmentTask;
import com.microsocial.courier.branch.model.TaskStatus;
import com.microsocial.courier.branch.model.TaskType;
import com.microsocial.courier.branch.service.ShipmentTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/branch/tasks")
public class ShipmentTaskController {

    private final ShipmentTaskService shipmentTaskService;

    @Autowired
    public ShipmentTaskController(ShipmentTaskService shipmentTaskService) {
        this.shipmentTaskService = shipmentTaskService;
    }

    @GetMapping
    public ResponseEntity<List<ShipmentTask>> getAllShipmentTasks() {
        List<ShipmentTask> tasks = shipmentTaskService.getAllShipmentTasks();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShipmentTask> getShipmentTaskById(@PathVariable Long id) {
        return shipmentTaskService.getShipmentTaskById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "Shipment task not found with id: " + id));
    }

    @GetMapping("/search/assignment/{assignmentId}")
    public ResponseEntity<List<ShipmentTask>> findShipmentTasksByAssignmentId(@PathVariable Long assignmentId) {
        List<ShipmentTask> tasks = shipmentTaskService.findShipmentTasksByAssignmentId(assignmentId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/search/type/{taskType}")
    public ResponseEntity<List<ShipmentTask>> findShipmentTasksByTaskType(@PathVariable TaskType taskType) {
        List<ShipmentTask> tasks = shipmentTaskService.findShipmentTasksByTaskType(taskType);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/search/status/{status}")
    public ResponseEntity<List<ShipmentTask>> findShipmentTasksByStatus(@PathVariable TaskStatus status) {
        List<ShipmentTask> tasks = shipmentTaskService.findShipmentTasksByStatus(status);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/search/shipment/{shipmentId}")
    public ResponseEntity<List<ShipmentTask>> findShipmentTasksByShipmentId(@PathVariable String shipmentId) {
        List<ShipmentTask> tasks = shipmentTaskService.findShipmentTasksByShipmentId(shipmentId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/search/assignment/{assignmentId}/status/{status}")
    public ResponseEntity<List<ShipmentTask>> findShipmentTasksByAssignmentAndStatus(
            @PathVariable Long assignmentId, 
            @PathVariable TaskStatus status) {
        
        List<ShipmentTask> tasks = shipmentTaskService.findShipmentTasksByAssignmentAndStatus(assignmentId, status);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/search/scheduled-time")
    public ResponseEntity<List<ShipmentTask>> findShipmentTasksByScheduledTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        
        List<ShipmentTask> tasks = shipmentTaskService.findShipmentTasksByScheduledTimeRange(startTime, endTime);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/search/branch/{branchId}/scheduled-time")
    public ResponseEntity<List<ShipmentTask>> findShipmentTasksByBranchAndScheduledTimeRange(
            @PathVariable Long branchId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        
        List<ShipmentTask> tasks = shipmentTaskService.findShipmentTasksByBranchAndScheduledTimeRange(branchId, startTime, endTime);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/search/assignment/{assignmentId}/ordered")
    public ResponseEntity<List<ShipmentTask>> findShipmentTasksByAssignmentOrderBySequence(@PathVariable Long assignmentId) {
        List<ShipmentTask> tasks = shipmentTaskService.findShipmentTasksByAssignmentOrderBySequence(assignmentId);
        return ResponseEntity.ok(tasks);
    }

    @PostMapping
    public ResponseEntity<ShipmentTask> createShipmentTask(@Validated @RequestBody ShipmentTask shipmentTask) {
        ShipmentTask createdTask = shipmentTaskService.saveShipmentTask(shipmentTask);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShipmentTask> updateShipmentTask(
            @PathVariable Long id, 
            @Validated @RequestBody ShipmentTask shipmentTask) {
        
        if (!shipmentTaskService.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "Shipment task not found with id: " + id);
        }
        
        shipmentTask.setId(id);
        ShipmentTask updatedTask = shipmentTaskService.saveShipmentTask(shipmentTask);
        return ResponseEntity.ok(updatedTask);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ShipmentTask> updateShipmentTaskStatus(
            @PathVariable Long id, 
            @RequestParam TaskStatus status) {
        
        if (!shipmentTaskService.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "Shipment task not found with id: " + id);
        }
        
        ShipmentTask updatedTask = shipmentTaskService.updateShipmentTaskStatus(id, status);
        return ResponseEntity.ok(updatedTask);
    }

    @PatchMapping("/{id}/sequence")
    public ResponseEntity<ShipmentTask> updateShipmentTaskSequence(
            @PathVariable Long id, 
            @RequestParam Integer sequenceOrder) {
        
        if (!shipmentTaskService.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "Shipment task not found with id: " + id);
        }
        
        ShipmentTask updatedTask = shipmentTaskService.updateShipmentTaskSequence(id, sequenceOrder);
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShipmentTask(@PathVariable Long id) {
        if (!shipmentTaskService.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "Shipment task not found with id: " + id);
        }
        
        shipmentTaskService.deleteShipmentTask(id);
        return ResponseEntity.noContent().build();
    }
}
