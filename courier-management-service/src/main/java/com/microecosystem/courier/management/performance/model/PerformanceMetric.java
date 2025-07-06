package com.gogidix.courierservices.management.$1;

import com.gogidix.courier.management.courier.model.Courier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing performance metrics for couriers.
 * Tracks various performance indicators over time.
 */
@Entity
@Table(name = "performance_metrics")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String metricId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "courier_id", nullable = false)
    private Courier courier;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MetricType metricType;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private Double value;

    @Column(length = 500)
    private String description;

    @Column(name = "target_value")
    private Double targetValue;

    @Column(name = "is_target_met")
    private Boolean isTargetMet;

    @Column(name = "trend_percentage")
    private Double trendPercentage;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Initializes a new performance metric with a unique ID.
     */
    @PrePersist
    public void prePersist() {
        if (metricId == null) {
            metricId = UUID.randomUUID().toString();
        }
        
        if (isTargetMet == null && targetValue != null) {
            isTargetMet = value >= targetValue;
        }
    }
    
    /**
     * Updates the target met flag when the entity is updated.
     */
    @PreUpdate
    public void preUpdate() {
        if (targetValue != null) {
            isTargetMet = value >= targetValue;
        }
    }
} 