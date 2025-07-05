package com.gogidix.courier.corporate.customer.onboarding.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Response DTO for contract generation completion.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@Schema(description = "Response for contract generation")
public record ContractGenerationResponse(
    
    @Schema(description = "Generated contract ID", example = "CONTRACT-20241201-ABC123")
    String contractId,
    
    @Schema(description = "Contract generation status", example = "GENERATED", 
            allowableValues = {"GENERATED", "PENDING", "FAILED", "REQUIRES_REVIEW"})
    String status,
    
    @Schema(description = "Contract generation status message")
    String message,
    
    @Schema(description = "Secure URL to download the contract")
    String contractUrl,
    
    @Schema(description = "Contract expiration date if not signed")
    LocalDateTime expiresAt,
    
    @Schema(description = "Contract generation timestamp")
    LocalDateTime generatedAt,
    
    @Schema(description = "Contract metadata and details")
    ContractDetails contractDetails,
    
    @Schema(description = "Digital signature information")
    DigitalSignatureInfo signatureInfo,
    
    @Schema(description = "Required next steps")
    List<String> nextSteps,
    
    @Schema(description = "Contract review requirements")
    ReviewRequirements reviewRequirements,
    
    @Schema(description = "Contract versions and history")
    List<ContractVersion> versions
) {}

/**
 * Contract details and metadata.
 */
@Schema(description = "Contract details")
record ContractDetails(
    
    @Schema(description = "Contract title", example = "Corporate Courier Service Agreement")
    String title,
    
    @Schema(description = "Contract type", example = "STANDARD_SERVICE_AGREEMENT")
    String contractType,
    
    @Schema(description = "Contract version", example = "1.0")
    String version,
    
    @Schema(description = "Total pages in contract", example = "15")
    Integer totalPages,
    
    @Schema(description = "Contract language", example = "EN_US")
    String language,
    
    @Schema(description = "Contract format", example = "PDF")
    String format,
    
    @Schema(description = "File size in bytes")
    Long fileSizeBytes,
    
    @Schema(description = "Contract hash for integrity verification")
    String documentHash,
    
    @Schema(description = "Contract effective date")
    LocalDateTime effectiveDate,
    
    @Schema(description = "Contract expiration date")
    LocalDateTime expirationDate,
    
    @Schema(description = "Contract value summary")
    ContractValueSummary valueSummary
) {}

/**
 * Digital signature information.
 */
@Schema(description = "Digital signature information")
record DigitalSignatureInfo(
    
    @Schema(description = "Whether digital signature is required")
    Boolean required,
    
    @Schema(description = "Signature provider", example = "DocuSign")
    String provider,
    
    @Schema(description = "Signature workflow ID")
    String workflowId,
    
    @Schema(description = "Required signatories")
    List<SignatoryInfo> signatories,
    
    @Schema(description = "Signature deadline")
    LocalDateTime signatureDeadline,
    
    @Schema(description = "Signature status", example = "PENDING", 
            allowableValues = {"PENDING", "PARTIAL", "COMPLETED", "EXPIRED"})
    String signatureStatus,
    
    @Schema(description = "Signature authentication requirements")
    Map<String, String> authenticationRequirements
) {}

/**
 * Contract review requirements.
 */
@Schema(description = "Review requirements")
record ReviewRequirements(
    
    @Schema(description = "Whether legal review is required")
    Boolean legalReviewRequired,
    
    @Schema(description = "Whether business review is required")
    Boolean businessReviewRequired,
    
    @Schema(description = "Required reviewers")
    List<ReviewerInfo> requiredReviewers,
    
    @Schema(description = "Review deadline")
    LocalDateTime reviewDeadline,
    
    @Schema(description = "Review status", example = "PENDING")
    String reviewStatus,
    
    @Schema(description = "Review workflow ID")
    String reviewWorkflowId
) {}

/**
 * Contract version information.
 */
@Schema(description = "Contract version")
record ContractVersion(
    
    @Schema(description = "Version number", example = "1.0")
    String versionNumber,
    
    @Schema(description = "Version creation date")
    LocalDateTime createdAt,
    
    @Schema(description = "Version created by")
    String createdBy,
    
    @Schema(description = "Version changes description")
    String changeDescription,
    
    @Schema(description = "Version status", example = "CURRENT")
    String status,
    
    @Schema(description = "Version document URL")
    String documentUrl
) {}

/**
 * Contract value summary.
 */
@Schema(description = "Contract value summary")
record ContractValueSummary(
    
    @Schema(description = "Estimated annual contract value")
    String estimatedAnnualValue,
    
    @Schema(description = "Contract currency", example = "USD")
    String currency,
    
    @Schema(description = "Payment terms", example = "NET_30")
    String paymentTerms,
    
    @Schema(description = "Billing frequency", example = "MONTHLY")
    String billingFrequency,
    
    @Schema(description = "Volume discount applied")
    Double volumeDiscount
) {}

/**
 * Signatory information.
 */
@Schema(description = "Signatory information")
record SignatoryInfo(
    
    @Schema(description = "Signatory name", example = "John Doe")
    String name,
    
    @Schema(description = "Signatory email", example = "john.doe@company.com")
    String email,
    
    @Schema(description = "Signatory title", example = "CEO")
    String title,
    
    @Schema(description = "Organization", example = "Acme Corporation")
    String organization,
    
    @Schema(description = "Signature order", example = "1")
    Integer signatureOrder,
    
    @Schema(description = "Signature status", example = "PENDING")
    String status,
    
    @Schema(description = "Signature date")
    LocalDateTime signedAt
) {}

/**
 * Reviewer information.
 */
@Schema(description = "Reviewer information")
record ReviewerInfo(
    
    @Schema(description = "Reviewer name", example = "Jane Smith")
    String name,
    
    @Schema(description = "Reviewer email", example = "jane.smith@exaltcourier.com")
    String email,
    
    @Schema(description = "Reviewer role", example = "LEGAL_COUNSEL")
    String role,
    
    @Schema(description = "Review type", example = "LEGAL_REVIEW")
    String reviewType,
    
    @Schema(description = "Review status", example = "PENDING")
    String status,
    
    @Schema(description = "Review completion date")
    LocalDateTime reviewedAt,
    
    @Schema(description = "Review comments")
    String comments
) {}