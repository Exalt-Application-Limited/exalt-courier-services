package com.gogidix.courier.customer.onboarding.model;

import com.gogidix.ecosystem.shared.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing the status change history for customer onboarding applications.
 */
@Entity
@Table(name = "customer_application_status_history", indexes = {
    @Index(name = "idx_application_id", columnList = "application_id"),
    @Index(name = "idx_changed_at", columnList = "changed_at")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerApplicationStatusHistory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    @NotNull(message = "Application is required")
    private CustomerOnboardingApplication application;

    @Enumerated(EnumType.STRING)
    @Column(name = "from_status")
    private CustomerOnboardingStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "to_status", nullable = false, length = 30)
    @NotNull(message = "To status is required")
    private CustomerOnboardingStatus toStatus;

    @Column(name = "change_reason", length = 500)
    @Size(max = 500, message = "Change reason must not exceed 500 characters")
    private String changeReason;

    @Column(name = "notes", length = 1000)
    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;

    @Column(name = "changed_by", length = 100)
    @Size(max = 100, message = "Changed by must not exceed 100 characters")
    private String changedBy;

    @Column(name = "changed_at", nullable = false)
    @NotNull(message = "Changed at timestamp is required")
    private LocalDateTime changedAt;
}