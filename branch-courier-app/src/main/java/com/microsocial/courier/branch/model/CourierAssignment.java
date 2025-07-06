package com.gogidix.courier.branch.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.gogidix.courier.branch.model.corporate.Branch;

@Entity
@Table(name = "courier_assignments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourierAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String assignmentCode;

    @ManyToOne
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @ManyToOne
    @JoinColumn(name = "courier_id", nullable = false)
    private Courier courier;

    @NotNull
    private LocalDateTime assignedAt;

    private LocalDateTime completedAt;

    @Enumerated(EnumType.STRING)
    private AssignmentStatus status;

    @Enumerated(EnumType.STRING)
    private AssignmentPriority priority;

    private String notes;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "assignment")
    private Set<ShipmentTask> shipmentTasks = new HashSet<>();
}
