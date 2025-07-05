package com.gogidix.courier.billing.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing a billing subscription.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@Entity
@Table(name = "subscriptions", indexes = {
    @Index(name = "idx_customer_id", columnList = "customer_id"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_next_billing_date", columnList = "next_billing_date")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "customer_id", nullable = false)
    @NotBlank(message = "Customer ID is required")
    private String customerId;

    @Column(name = "customer_name", nullable = false)
    @NotBlank(message = "Customer name is required")
    private String customerName;

    @Column(name = "customer_email", nullable = false)
    @NotBlank(message = "Customer email is required")
    private String customerEmail;

    @Column(name = "billing_address", columnDefinition = "TEXT")
    private String billingAddress;

    @Column(name = "service_plan", nullable = false)
    @NotBlank(message = "Service plan is required")
    private String servicePlan;

    @Column(name = "monthly_amount", precision = 19, scale = 2, nullable = false)
    @NotNull(message = "Monthly amount is required")
    @Positive(message = "Monthly amount must be positive")
    private BigDecimal monthlyAmount;

    @Column(name = "currency", length = 3, nullable = false)
    @NotBlank(message = "Currency is required")
    @Builder.Default
    private String currency = "USD";

    @Column(name = "discount_percentage", precision = 5, scale = 2)
    private BigDecimal discountPercentage;

    @Column(name = "status", nullable = false)
    @NotBlank(message = "Subscription status is required")
    @Builder.Default
    private String status = "ACTIVE";

    @Column(name = "billing_cycle", nullable = false)
    @NotBlank(message = "Billing cycle is required")
    @Builder.Default
    private String billingCycle = "MONTHLY";

    @Column(name = "start_date", nullable = false)
    @NotNull(message = "Start date is required")
    @Builder.Default
    private LocalDateTime startDate = LocalDateTime.now();

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "next_billing_date", nullable = false)
    @NotNull(message = "Next billing date is required")
    private LocalDateTime nextBillingDate;

    @Column(name = "last_billed_at")
    private LocalDateTime lastBilledAt;

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