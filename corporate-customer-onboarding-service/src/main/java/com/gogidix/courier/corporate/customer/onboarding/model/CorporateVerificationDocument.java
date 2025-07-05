package com.gogidix.courier.corporate.customer.onboarding.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity representing verification documents uploaded by corporate customers during onboarding.
 */
@Entity
@Table(name = "corporate_verification_documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CorporateVerificationDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private CorporateOnboardingApplication application;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false)
    private DocumentType documentType;

    @Column(name = "document_reference_id")
    private String documentReferenceId;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "mime_type")
    private String mimeType;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status")
    private DocumentVerificationStatus verificationStatus;

    @Column(name = "verification_notes")
    private String verificationNotes;

    @Column(name = "verified_by")
    private String verifiedBy;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "document_verification_service_id")
    private String documentVerificationServiceId;

    @CreationTimestamp
    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "uploaded_by")
    private String uploadedBy;

    public enum DocumentType {
        CERTIFICATE_OF_INCORPORATION,
        BUSINESS_LICENSE,
        TAX_REGISTRATION_CERTIFICATE,
        VAT_REGISTRATION,
        ARTICLES_OF_ASSOCIATION,
        MEMORANDUM_OF_ASSOCIATION,
        BOARD_RESOLUTION,
        POWER_OF_ATTORNEY,
        BANK_STATEMENT,
        CREDIT_REFERENCE,
        INSURANCE_CERTIFICATE,
        TRADE_REFERENCE,
        FINANCIAL_STATEMENTS,
        AUDITED_ACCOUNTS,
        CORPORATE_STRUCTURE_CHART,
        BENEFICIAL_OWNERSHIP_DECLARATION,
        DIRECTOR_ID_VERIFICATION,
        AUTHORIZED_SIGNATORY_ID,
        UTILITY_BILL_BUSINESS_ADDRESS,
        LEASE_AGREEMENT,
        OTHER
    }

    public enum DocumentVerificationStatus {
        PENDING,
        UNDER_REVIEW,
        APPROVED,
        REJECTED,
        REQUIRES_RESUBMISSION,
        EXPIRED,
        REQUIRES_CERTIFICATION
    }
}