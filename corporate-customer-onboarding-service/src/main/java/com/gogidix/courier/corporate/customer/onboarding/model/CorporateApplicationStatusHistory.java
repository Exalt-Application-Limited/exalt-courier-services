package com.gogidix.courier.corporate.customer.onboarding.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entity representing the status change history for corporate customer onboarding applications.
 */
@Entity
@Table(name = "corporate_application_status_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CorporateApplicationStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private CorporateOnboardingApplication application;

    @Enumerated(EnumType.STRING)
    @Column(name = "from_status")
    private CorporateOnboardingStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "to_status", nullable = false)
    private CorporateOnboardingStatus toStatus;

    @Column(name = "change_reason")
    private String changeReason;

    @Column(name = "notes")
    private String notes;

    @Column(name = "changed_by")
    private String changedBy;

    @CreationTimestamp
    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;
}