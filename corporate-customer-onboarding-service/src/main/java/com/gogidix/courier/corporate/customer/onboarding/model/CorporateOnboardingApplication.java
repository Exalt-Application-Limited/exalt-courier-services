package com.gogidix.courier.corporate.customer.onboarding.model;

import com.gogidix.courier.corporate.customer.onboarding.enums.*;
import com.gogidix.shared.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity representing a corporate customer onboarding application.
 * 
 * This entity tracks the complete corporate customer onboarding journey for businesses
 * registering for courier services via www.exaltcourier.com. It extends BaseEntity to
 * leverage shared audit and UUID patterns.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@Entity
@Table(name = "corporate_onboarding_applications", indexes = {
    @Index(name = "idx_corporate_application_reference_id", columnList = "application_reference_id"),
    @Index(name = "idx_corporate_company_email", columnList = "company_email"),
    @Index(name = "idx_corporate_company_registration", columnList = "company_registration_number"),
    @Index(name = "idx_corporate_application_status", columnList = "application_status"),
    @Index(name = "idx_corporate_auth_service_user_id", columnList = "auth_service_user_id"),
    @Index(name = "idx_corporate_kyb_verification_id", columnList = "kyb_verification_id")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CorporateOnboardingApplication extends BaseEntity {

    @NotBlank(message = "Application reference ID is required")
    @Size(max = 50, message = "Application reference ID must not exceed 50 characters")
    @Column(name = "application_reference_id", unique = true, nullable = false, length = 50)
    private String applicationReferenceId;

    @NotBlank(message = "Company name is required")
    @Size(max = 200, message = "Company name must not exceed 200 characters")
    @Column(name = "company_name", nullable = false, length = 200)
    private String companyName;

    @NotBlank(message = "Company registration number is required")
    @Size(max = 50, message = "Company registration number must not exceed 50 characters")
    @Column(name = "company_registration_number", nullable = false, length = 50)
    private String companyRegistrationNumber;

    @Size(max = 50, message = "Tax identification number must not exceed 50 characters")
    @Column(name = "tax_identification_number", length = 50)
    private String taxIdentificationNumber;

    @Size(max = 50, message = "Business license number must not exceed 50 characters")
    @Column(name = "business_license_number", length = 50)
    private String businessLicenseNumber;

    @NotBlank(message = "Company email is required")
    @Email(message = "Company email must be valid")
    @Size(max = 100, message = "Company email must not exceed 100 characters")
    @Column(name = "company_email", nullable = false, length = 100)
    private String companyEmail;

    @NotBlank(message = "Company phone is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Company phone must be a valid international format")
    @Column(name = "company_phone", nullable = false, length = 20)
    private String companyPhone;

    @Size(max = 200, message = "Company website must not exceed 200 characters")
    @Column(name = "company_website", length = 200)
    private String companyWebsite;

    @Enumerated(EnumType.STRING)
    @Column(name = "business_type", length = 50)
    private BusinessType businessType;

    @Enumerated(EnumType.STRING)
    @Column(name = "industry_sector", length = 50)
    private IndustrySector industrySector;

    @Enumerated(EnumType.STRING)
    @Column(name = "company_size", length = 20)
    private CompanySize companySize;

    @Enumerated(EnumType.STRING)
    @Column(name = "annual_shipping_volume", length = 20)
    private ShippingVolume annualShippingVolume;

    @NotBlank(message = "Business address is required")
    @Size(max = 200, message = "Business address line 1 must not exceed 200 characters")
    @Column(name = "business_address_line1", nullable = false, length = 200)
    private String businessAddressLine1;

    @Size(max = 200, message = "Business address line 2 must not exceed 200 characters")
    @Column(name = "business_address_line2", length = 200)
    private String businessAddressLine2;

    @NotBlank(message = "Business city is required")
    @Size(max = 100, message = "Business city must not exceed 100 characters")
    @Column(name = "business_city", nullable = false, length = 100)
    private String businessCity;

    @Size(max = 100, message = "Business state/province must not exceed 100 characters")
    @Column(name = "business_state_province", length = 100)
    private String businessStateProvince;

    @NotBlank(message = "Business postal code is required")
    @Size(max = 20, message = "Business postal code must not exceed 20 characters")
    @Column(name = "business_postal_code", nullable = false, length = 20)
    private String businessPostalCode;

    @NotBlank(message = "Business country is required")
    @Size(max = 100, message = "Business country must not exceed 100 characters")
    @Column(name = "business_country", nullable = false, length = 100)
    private String businessCountry;

    @NotBlank(message = "Primary contact first name is required")
    @Size(max = 100, message = "Primary contact first name must not exceed 100 characters")
    @Column(name = "primary_contact_first_name", nullable = false, length = 100)
    private String primaryContactFirstName;

    @NotBlank(message = "Primary contact last name is required")
    @Size(max = 100, message = "Primary contact last name must not exceed 100 characters")
    @Column(name = "primary_contact_last_name", nullable = false, length = 100)
    private String primaryContactLastName;

    @NotBlank(message = "Primary contact email is required")
    @Email(message = "Primary contact email must be valid")
    @Size(max = 100, message = "Primary contact email must not exceed 100 characters")
    @Column(name = "primary_contact_email", nullable = false, length = 100)
    private String primaryContactEmail;

    @NotBlank(message = "Primary contact phone is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Primary contact phone must be a valid international format")
    @Column(name = "primary_contact_phone", nullable = false, length = 20)
    private String primaryContactPhone;

    @Size(max = 100, message = "Primary contact position must not exceed 100 characters")
    @Column(name = "primary_contact_position", length = 100)
    private String primaryContactPosition;

    @Size(max = 100, message = "Billing contact first name must not exceed 100 characters")
    @Column(name = "billing_contact_first_name", length = 100)
    private String billingContactFirstName;

    @Size(max = 100, message = "Billing contact last name must not exceed 100 characters")
    @Column(name = "billing_contact_last_name", length = 100)
    private String billingContactLastName;

    @Email(message = "Billing contact email must be valid")
    @Size(max = 100, message = "Billing contact email must not exceed 100 characters")
    @Column(name = "billing_contact_email", length = 100)
    private String billingContactEmail;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Billing contact phone must be a valid international format")
    @Column(name = "billing_contact_phone", length = 20)
    private String billingContactPhone;

    @NotNull(message = "Application status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "application_status", nullable = false, length = 30)
    private CorporateOnboardingStatus applicationStatus;

    @Size(max = 50, message = "KYB verification ID must not exceed 50 characters")
    @Column(name = "kyb_verification_id", length = 50)
    private String kybVerificationId;

    @Size(max = 50, message = "Auth service user ID must not exceed 50 characters")
    @Column(name = "auth_service_user_id", length = 50)
    private String authServiceUserId;

    @Size(max = 50, message = "Billing customer ID must not exceed 50 characters")
    @Column(name = "billing_customer_id", length = 50)
    private String billingCustomerId;

    @DecimalMin(value = "0.0", message = "Requested credit limit must be non-negative")
    @Column(name = "requested_credit_limit", precision = 19, scale = 2)
    private java.math.BigDecimal requestedCreditLimit;

    @DecimalMin(value = "0.0", message = "Approved credit limit must be non-negative")
    @Column(name = "approved_credit_limit", precision = 19, scale = 2)
    private java.math.BigDecimal approvedCreditLimit;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_terms", length = 30)
    private PaymentTerms paymentTerms;

    @Enumerated(EnumType.STRING)
    @Column(name = "volume_discount_tier", length = 20)
    private VolumeDiscountTier volumeDiscountTier;

    @Column(name = "sla_requirements", columnDefinition = "TEXT")
    private String slaRequirements;

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_communication_method", length = 20)
    private CommunicationMethod preferredCommunicationMethod;

    @Builder.Default
    @Column(name = "marketing_consent", nullable = false)
    private Boolean marketingConsent = false;

    @NotNull(message = "Terms acceptance is required")
    @Column(name = "terms_accepted", nullable = false)
    private Boolean termsAccepted;

    @NotNull(message = "Privacy policy acceptance is required")
    @Column(name = "privacy_policy_accepted", nullable = false)
    private Boolean privacyPolicyAccepted;

    @NotNull(message = "Data processing agreement acceptance is required")
    @Column(name = "data_processing_agreement_accepted", nullable = false)
    private Boolean dataProcessingAgreementAccepted;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "approved_by", length = 100)
    private String approvedBy;

    @Column(name = "rejected_at")
    private LocalDateTime rejectedAt;

    @Column(name = "rejected_by", length = 100)
    private String rejectedBy;

    @Size(max = 1000, message = "Rejection reason must not exceed 1000 characters")
    @Column(name = "rejection_reason", length = 1000)
    private String rejectionReason;

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CorporateApplicationStatusHistory> statusHistory;

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CorporateVerificationDocument> verificationDocuments;

    // Note: BaseEntity provides createdAt, updatedAt, createdBy, updatedBy, and version fields
}