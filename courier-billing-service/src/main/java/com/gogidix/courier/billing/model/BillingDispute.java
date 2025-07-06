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
 * Entity representing billing disputes.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@Entity
@Table(name = "billing_disputes", indexes = {
    @Index(name = "idx_customer_id", columnList = "customer_id"),
    @Index(name = "idx_invoice_id", columnList = "invoice_id"),
    @Index(name = "idx_dispute_status", columnList = "status"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillingDispute {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "dispute_number", unique = true, nullable = false)
    @NotBlank(message = "Dispute number is required")
    private String disputeNumber;

    @Column(name = "customer_id", nullable = false)
    @NotBlank(message = "Customer ID is required")
    private String customerId;

    @Column(name = "invoice_id", nullable = false)
    @NotBlank(message = "Invoice ID is required")
    private String invoiceId;

    @Column(name = "payment_id")
    private String paymentId;

    @Column(name = "disputed_amount", precision = 19, scale = 2, nullable = false)
    @NotNull(message = "Disputed amount is required")
    private BigDecimal disputedAmount;

    @Column(name = "currency", length = 3, nullable = false)
    @NotBlank(message = "Currency is required")
    @Builder.Default
    private String currency = "USD";

    @Enumerated(EnumType.STRING)
    @Column(name = "dispute_type", nullable = false)
    @NotNull(message = "Dispute type is required")
    private DisputeType disputeType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @NotNull(message = "Dispute status is required")
    @Builder.Default
    private DisputeStatus status = DisputeStatus.SUBMITTED;

    @Column(name = "reason", columnDefinition = "TEXT", nullable = false)
    @NotBlank(message = "Dispute reason is required")
    private String reason;

    @Column(name = "customer_comments", columnDefinition = "TEXT")
    private String customerComments;

    @Column(name = "resolution_comments", columnDefinition = "TEXT")
    private String resolutionComments;

    @Column(name = "resolved_amount", precision = 19, scale = 2)
    private BigDecimal resolvedAmount;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "resolved_by")
    private String resolvedBy;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

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

    public enum DisputeType {
        BILLING_ERROR,
        UNAUTHORIZED_CHARGE,
        SERVICE_NOT_RECEIVED,
        REFUND_REQUEST,
        DUPLICATE_CHARGE,
        PRICING_DISPUTE,
        OTHER
    }

    public enum DisputeStatus {
        SUBMITTED,
        UNDER_REVIEW,
        AWAITING_CUSTOMER_RESPONSE,
        AWAITING_INTERNAL_RESPONSE,
        RESOLVED_CUSTOMER_FAVOR,
        RESOLVED_MERCHANT_FAVOR,
        CLOSED_NO_RESOLUTION,
        ESCALATED
    }
}