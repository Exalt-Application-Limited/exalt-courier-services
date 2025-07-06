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
 * Entity representing verification documents uploaded by customers during onboarding.
 */
@Entity
@Table(name = "customer_verification_documents", indexes = {
    @Index(name = "idx_application_id", columnList = "application_id"),
    @Index(name = "idx_document_type", columnList = "document_type"),
    @Index(name = "idx_verification_status", columnList = "verification_status"),
    @Index(name = "idx_uploaded_at", columnList = "uploaded_at")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerVerificationDocument extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    @NotNull(message = "Application is required")
    private CustomerOnboardingApplication application;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false, length = 30)
    @NotNull(message = "Document type is required")
    private DocumentType documentType;

    @Column(name = "document_reference_id", unique = true, length = 100)
    @Size(max = 100, message = "Document reference ID must not exceed 100 characters")
    private String documentReferenceId;

    @Column(name = "file_name", length = 255)
    @Size(max = 255, message = "File name must not exceed 255 characters")
    private String fileName;

    @Column(name = "file_path", length = 500)
    @Size(max = 500, message = "File path must not exceed 500 characters")
    private String filePath;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "mime_type", length = 100)
    @Size(max = 100, message = "MIME type must not exceed 100 characters")
    private String mimeType;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false, length = 30)
    @NotNull(message = "Verification status is required")
    private DocumentVerificationStatus verificationStatus;

    @Column(name = "verification_notes", length = 1000)
    @Size(max = 1000, message = "Verification notes must not exceed 1000 characters")
    private String verificationNotes;

    @Column(name = "verified_by", length = 100)
    @Size(max = 100, message = "Verified by must not exceed 100 characters")
    private String verifiedBy;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "document_verification_service_id", length = 100)
    @Size(max = 100, message = "Document verification service ID must not exceed 100 characters")
    private String documentVerificationServiceId;

    @Column(name = "uploaded_at", nullable = false)
    @NotNull(message = "Uploaded at timestamp is required")
    private LocalDateTime uploadedAt;

    @Column(name = "uploaded_by", length = 100)
    @Size(max = 100, message = "Uploaded by must not exceed 100 characters")
    private String uploadedBy;

    @Column(name = "document_hash", length = 64)
    @Size(max = 64, message = "Document hash must not exceed 64 characters")
    private String documentHash;

    @Column(name = "extraction_data", columnDefinition = "TEXT")
    private String extractionData; // JSON data extracted from document

    @Column(name = "confidence_score")
    private Double confidenceScore; // AI verification confidence (0.0 - 1.0)

    @Column(name = "expires_at")
    private LocalDateTime expiresAt; // Document expiration date

    @Column(name = "is_primary")
    private Boolean isPrimary; // Primary document for this type

    /**
     * Types of documents accepted for verification.
     */
    public enum DocumentType {
        // Identity Documents
        GOVERNMENT_ID("Government Issued ID", true, "National ID, State ID, or similar government issued identification"),
        PASSPORT("Passport", true, "Valid passport from any country"),
        DRIVERS_LICENSE("Driver's License", true, "Valid driver's license"),
        
        // Proof of Address Documents
        UTILITY_BILL("Utility Bill", false, "Recent utility bill (electricity, water, gas, internet)"),
        BANK_STATEMENT("Bank Statement", false, "Recent bank statement or letter from bank"),
        LEASE_AGREEMENT("Lease Agreement", false, "Property lease or rental agreement"),
        MORTGAGE_STATEMENT("Mortgage Statement", false, "Mortgage statement or property tax bill"),
        COUNCIL_TAX("Council Tax", false, "Council tax bill or municipal tax document"),
        
        // Verification Photos
        SELFIE_WITH_ID("Selfie with ID", false, "Photo of person holding their ID document"),
        PROOF_OF_LIFE("Proof of Life", false, "Recent photo showing person's face clearly"),
        
        // Business Documents (for corporate customers)
        BUSINESS_REGISTRATION("Business Registration", false, "Certificate of incorporation or business registration"),
        TAX_CERTIFICATE("Tax Certificate", false, "Business tax registration certificate"),
        BUSINESS_LICENSE("Business License", false, "Professional or business operating license"),
        
        // Additional Documents
        BIRTH_CERTIFICATE("Birth Certificate", true, "Official birth certificate"),
        MARRIAGE_CERTIFICATE("Marriage Certificate", true, "Marriage certificate for name verification"),
        OTHER("Other Document", false, "Other supporting documentation");
        
        private final String displayName;
        private final boolean isIdentityDocument;
        private final String description;
        
        DocumentType(String displayName, boolean isIdentityDocument, String description) {
            this.displayName = displayName;
            this.isIdentityDocument = isIdentityDocument;
            this.description = description;
        }
        
        public String getDisplayName() { return displayName; }
        public boolean isIdentityDocument() { return isIdentityDocument; }
        public String getDescription() { return description; }
    }

    /**
     * Verification status of uploaded documents.
     */
    public enum DocumentVerificationStatus {
        PENDING("Pending", "Document uploaded and awaiting initial review"),
        UNDER_REVIEW("Under Review", "Document is being reviewed by verification team"),
        AI_PROCESSING("AI Processing", "Document is being processed by AI verification system"),
        MANUAL_REVIEW_REQUIRED("Manual Review Required", "Document requires human verification"),
        APPROVED("Approved", "Document has been verified and approved"),
        REJECTED("Rejected", "Document verification failed"),
        EXPIRED("Expired", "Document has expired and needs to be resubmitted"),
        REQUIRES_RESUBMISSION("Requires Resubmission", "Document needs to be uploaded again with corrections"),
        SUSPICIOUS("Suspicious", "Document flagged as potentially fraudulent"),
        BLURRY_OR_UNREADABLE("Blurry/Unreadable", "Document image quality is insufficient"),
        INCORRECT_TYPE("Incorrect Type", "Wrong document type uploaded"),
        PROCESSING_ERROR("Processing Error", "Technical error during document processing");
        
        private final String displayName;
        private final String description;
        
        DocumentVerificationStatus(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }
        
        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
        
        public boolean isSuccessful() {
            return this == APPROVED;
        }
        
        public boolean isFailed() {
            return this == REJECTED || this == EXPIRED || this == SUSPICIOUS || 
                   this == BLURRY_OR_UNREADABLE || this == INCORRECT_TYPE;
        }
        
        public boolean isInProgress() {
            return this == PENDING || this == UNDER_REVIEW || this == AI_PROCESSING || 
                   this == MANUAL_REVIEW_REQUIRED;
        }
        
        public boolean requiresAction() {
            return this == REQUIRES_RESUBMISSION || this == PROCESSING_ERROR;
        }
    }
}