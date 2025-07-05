package com.gogidix.courier.management.assignment.model;

import com.gogidix.courier.management.courier.model.Courier;
import com.gogidix.courier.management.util.BaseEntity;
import com.gogidix.courier.management.validation.ValidCoordinates;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents an assignment for a courier.
 * An assignment is a collection of tasks that need to be completed by a courier,
 * typically involving pickups and deliveries.
 */
@Entity
@Table(name = "assignments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Assignment extends BaseEntity {

    @NotBlank(message = "Assignment ID is required")
    @Column(name = "assignment_id", unique = true, nullable = false)
    private String assignmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "courier_id")
    private Courier courier;

    @NotNull(message = "Order ID is required")
    @Column(name = "order_id", nullable = false)
    private String orderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AssignmentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    private AssignmentPriority priority;

    @OneToMany(mappedBy = "assignment", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<AssignmentTask> tasks = new HashSet<>();

    @Column(name = "estimated_start_time")
    private LocalDateTime estimatedStartTime;

    @Column(name = "estimated_end_time")
    private LocalDateTime estimatedEndTime;

    @Column(name = "actual_start_time")
    private LocalDateTime actualStartTime;

    @Column(name = "actual_end_time")
    private LocalDateTime actualEndTime;

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    @Size(max = 1000, message = "Notes must be less than 1000 characters")
    @Column(name = "notes", length = 1000)
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Utility methods

    /**
     * Adds a task to the assignment
     * 
     * @param task the task to add
     * @return the assignment instance for method chaining
     */
    public Assignment addTask(AssignmentTask task) {
        tasks.add(task);
        task.setAssignment(this);
        return this;
    }

    /**
     * Removes a task from the assignment
     * 
     * @param task the task to remove
     * @return the assignment instance for method chaining
     */
    public Assignment removeTask(AssignmentTask task) {
        tasks.remove(task);
        task.setAssignment(null);
        return this;
    }

    /**
     * Updates the status of the assignment
     * 
     * @param newStatus the new status
     * @return the assignment instance for method chaining
     */
    public Assignment updateStatus(AssignmentStatus newStatus) {
        this.status = newStatus;
        
        if (newStatus == AssignmentStatus.IN_PROGRESS && this.actualStartTime == null) {
            this.actualStartTime = LocalDateTime.now();
        } else if (newStatus.isTerminal() && this.actualEndTime == null) {
            this.actualEndTime = LocalDateTime.now();
        }
        
        return this;
    }

    /**
     * Checks if the assignment is currently active
     * 
     * @return true if the assignment is not in a terminal state
     */
    public boolean isActive() {
        return status != null && !status.isTerminal();
    }

    /**
     * Gets all tasks of a specific type
     * 
     * @param taskType the type of tasks to retrieve
     * @return a set of tasks matching the specified type
     */
    public Set<AssignmentTask> getTasksByType(TaskType taskType) {
        Set<AssignmentTask> result = new HashSet<>();
        
        for (AssignmentTask task : tasks) {
            if (task.getTaskType() == taskType) {
                result.add(task);
            }
        }
        
        return result;
    }

    /**
     * Gets all tasks with a specific status
     * 
     * @param taskStatus the status of tasks to retrieve
     * @return a set of tasks matching the specified status
     */
    public Set<AssignmentTask> getTasksByStatus(TaskStatus taskStatus) {
        Set<AssignmentTask> result = new HashSet<>();
        
        for (AssignmentTask task : tasks) {
            if (task.getStatus() == taskStatus) {
                result.add(task);
            }
        }
        
        return result;
    }

    /**
     * Gets the completion percentage of the assignment
     * 
     * @return the percentage of tasks that are completed (0-100)
     */
    public int getCompletionPercentage() {
        if (tasks.isEmpty()) {
            return 0;
        }
        
        long completedTasks = tasks.stream()
                .filter(task -> task.getStatus() == TaskStatus.COMPLETED)
                .count();
                
        return (int) ((completedTasks * 100) / tasks.size());
    }
} 