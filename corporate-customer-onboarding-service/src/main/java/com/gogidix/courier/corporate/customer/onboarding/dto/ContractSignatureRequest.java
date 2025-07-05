package com.gogidix.courier.corporate.customer.onboarding.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Request DTO for recording contract signature for corporate customers.
 * Contains digital signature information and authorized signatory details.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractSignatureRequest {
    
    @NotBlank(message = "Application reference ID is required")
    private String applicationReferenceId;
    
    @NotBlank(message = "Contract ID is required")
    private String contractId;
    
    @NotBlank(message = "Contract version is required")
    private String contractVersion;
    
    @Valid
    @NotNull(message = "Primary signatory information is required")
    private SignatoryInformation primarySignatory;
    
    @Valid
    private List<SignatoryInformation> additionalSignatories;
    
    @Valid
    @NotNull(message = "Signature method is required")
    private SignatureMethod signatureMethod;
    
    @NotNull(message = "Signature timestamp is required")
    private LocalDateTime signatureTimestamp;
    
    @NotBlank(message = "IP address is required")
    private String ipAddress;
    
    private String userAgent;
    private String deviceFingerprint;
    private String geolocation;
    
    @Valid
    private WitnessInformation witnessInformation;
    
    @Valid
    private NotarizationInformation notarizationInformation;
    
    private String additionalTermsAccepted;
    private List<String> attachedDocuments;
    private String signingCeremonyNotes;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SignatoryInformation {
        
        @NotBlank(message = "Full name is required")
        private String fullName;
        
        @NotBlank(message = "Title is required")
        private String title;
        
        @Email(message = "Valid email is required")
        @NotBlank(message = "Email is required")
        private String email;
        
        @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Valid phone number is required")
        private String phoneNumber;
        
        @NotBlank(message = "Authority to sign is required")
        private String authorityToSign; // DIRECTOR, AUTHORIZED_SIGNATORY, POWER_OF_ATTORNEY
        
        private String authorizationDocumentReference;
        private String identificationNumber;
        private String identificationType;
        private LocalDateTime verificationDate;
        private String verificationMethod;
        private boolean identityVerified;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SignatureMethod {
        
        @NotBlank(message = "Signature type is required")
        private String signatureType; // ELECTRONIC, DIGITAL, WET_INK, DOCUSIGN, ADOBE_SIGN
        
        private String signatureProvider;
        private String signatureId;
        private String certificateId;
        private String hashAlgorithm;
        private String encryptionMethod;
        private boolean timestamped;
        private String auditTrailReference;
        private boolean biometricCapture;
        private String signatureImageData;
        private String handwritingAnalysis;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WitnessInformation {
        
        @NotBlank(message = "Witness name is required")
        private String witnessName;
        
        @Email(message = "Valid witness email is required")
        private String witnessEmail;
        
        @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Valid witness phone is required")
        private String witnessPhone;
        
        private String witnessTitle;
        private String witnessRelationship;
        private String witnessIdentificationNumber;
        private LocalDateTime witnessSignatureTimestamp;
        private String witnessSignatureMethod;
        private boolean witnessPresent;
        private String witnessLocation;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotarizationInformation {
        
        private boolean notarizationRequired;
        private String notaryName;
        private String notaryCommissionNumber;
        private LocalDateTime notaryCommissionExpiry;
        private String notaryJurisdiction;
        private LocalDateTime notarizationDate;
        private String notarizationLocation;
        private String notarySignatureId;
        private String notarySeal;
        private String notarizationCertificateReference;
    }
}
