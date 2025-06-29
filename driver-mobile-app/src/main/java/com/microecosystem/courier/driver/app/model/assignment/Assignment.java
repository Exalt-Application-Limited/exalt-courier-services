package com.microecosystem.courier.driver.app.model.assignment;

import com.microecosystem.courier.driver.app.model.Driver;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a driver assignment.
 * An assignment is a collection of tasks assigned to a driver.
 */
@Entity
@Table(name = "driver_assignments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssignmentStatus status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime assignedAt;

    @Column
    private LocalDateTime startedAt;

    @Column
    private LocalDateTime completedAt;

    @Column
    private LocalDateTime cancelledAt;

    @Column
    private String cancellationReason;

    @Column
    private Double estimatedDurationMinutes;

    @Column
    private Double estimatedDistanceKm;

    @OneToMany(mappedBy = "assignment", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Task> tasks = new ArrayList<>();

    @Column
    private String routeOptimizationStatus;

    @Column(length = 1000)
    private String notes;

    @Column
    private String syncStatus;

    @Column
    private Boolean isDeleted = false;

    /**
     * Add a task to this assignment.
     * 
     * @param task Task to add
     * @return Updated assignment
     */
    public Assignment addTask(Task task) {
        tasks.add(task);
        task.setAssignment(this);
        return this;
    }

    /**
     * Remove a task from this assignment.
     * 
     * @param task Task to remove
     * @return Updated assignment
     */
    public Assignment removeTask(Task task) {
        tasks.remove(task);
        task.setAssignment(null);
        return this;
    }
}
