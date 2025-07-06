package com.gogidix.courier.billing.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing customer pricing tiers.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@Entity
@Table(name = "pricing_tiers", indexes = {
    @Index(name = "idx_tier_name", columnList = "tier_name"),
    @Index(name = "idx_active", columnList = "active"),
    @Index(name = "idx_min_volume", columnList = "min_monthly_volume")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PricingTier {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tier_name", unique = true, nullable = false)
    @NotBlank(message = "Tier name is required")
    private String tierName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "discount_percentage", precision = 5, scale = 2)
    private BigDecimal discountPercentage;

    @Column(name = "min_monthly_volume", nullable = false)
    @NotNull(message = "Minimum monthly volume is required")
    @Builder.Default
    private Integer minMonthlyVolume = 0;

    @Column(name = "max_monthly_volume")
    private Integer maxMonthlyVolume;

    @Column(name = "base_rate_multiplier", precision = 5, scale = 4)
    @Builder.Default
    private BigDecimal baseRateMultiplier = BigDecimal.ONE;

    @Column(name = "priority_handling", nullable = false)
    @Builder.Default
    private Boolean priorityHandling = false;

    @Column(name = "dedicated_support", nullable = false)
    @Builder.Default
    private Boolean dedicatedSupport = false;

    @Column(name = "custom_reporting", nullable = false)
    @Builder.Default
    private Boolean customReporting = false;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(name = "effective_from", nullable = false)
    @Builder.Default
    private LocalDateTime effectiveFrom = LocalDateTime.now();

    @Column(name = "effective_until")
    private LocalDateTime effectiveUntil;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}