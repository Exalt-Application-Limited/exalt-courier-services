package com.microecosystem.courier.driver.app.model.assignment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing a task within a driver assignment.
 * Tasks can be pickups, deliveries, or other types of activities.
 */
@Entity
@Table(name = "assignment_tasks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskType taskType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    @Column
    private Integer sequenceNumber;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column
    private String contactName;

    @Column
    private String contactPhone;

    @Column
    private LocalDateTime timeWindowStart;

    @Column
    private LocalDateTime timeWindowEnd;

    @Column
    private LocalDateTime estimatedArrivalTime;

    @Column
    private LocalDateTime actualArrivalTime;

    @Column
    private LocalDateTime completedAt;

    @Column
    private String completionCode;

    @Column(length = 1000)
    private String notes;

    @Column
    private String trackingNumber;

    @Column
    private String packageId;

    @Column
    private String syncStatus;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column
    private Boolean isDeleted = false;
}
