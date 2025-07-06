package com.gogidix.courierservices.payout.$1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing an individual earnings entry that contributes to a payout.
 */
@Entity
@Table(name = "earnings_entry")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EarningsEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The payout this entry belongs to
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payout_id")
    private Payout payout;

    /**
     * ID of the courier associated with this earnings entry
     */
    @NotBlank
    @Column(name = "courier_id", nullable = false)
    private String courierId;

    /**
     * Type of earnings
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private EarningsType type;

    /**
     * Amount of the earnings entry
     */
    @NotNull
    @PositiveOrZero
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    /**
     * Currency of the earnings amount (ISO currency code)
     */
    @NotBlank
    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    /**
     * Description of the earnings entry
     */
    @Column(name = "description", length = 500)
    private String description;

    /**
     * Reference to the assignment/delivery associated with this earnings entry (if applicable)
     */
    @Column(name = "assignment_id")
    private String assignmentId;

    /**
     * Date and time when the earnings were accrued
     */
    @NotNull
    @Column(name = "earned_at", nullable = false)
    private LocalDateTime earnedAt;

    /**
     * Any additional metadata or attributes as JSON
     */
    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata;

    /**
     * Date when the entity was created
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Date when the entity was last updated
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
