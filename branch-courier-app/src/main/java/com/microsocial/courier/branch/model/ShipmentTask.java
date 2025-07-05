package com.gogidix.courier.courier.branch.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "shipment_tasks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String taskCode;

    @ManyToOne
    @JoinColumn(name = "assignment_id", nullable = false)
    private CourierAssignment assignment;

    @NotNull
    private String shipmentId;

    @Enumerated(EnumType.STRING)
    private TaskType taskType;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    private String pickupAddress;

    private String deliveryAddress;

    private String recipientName;

    private String recipientPhone;

    private String notes;

    private Integer sequenceOrder;

    @NotNull
    private LocalDateTime scheduledTime;

    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    private String proofOfDelivery;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
