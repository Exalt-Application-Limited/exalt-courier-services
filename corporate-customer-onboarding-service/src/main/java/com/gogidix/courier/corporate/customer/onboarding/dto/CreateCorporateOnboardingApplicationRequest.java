package com.gogidix.courier.corporate.customer.onboarding.dto;

import com.gogidix.courier.corporate.customer.onboarding.enums.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * Request DTO for creating a new corporate customer onboarding application.
 * 
 * @param companyName Company legal name
 * @param companyRegistrationNumber Government registration number
 * @param taxIdentificationNumber Tax ID or EIN
 * @param businessLicenseNumber Business license number
 * @param companyEmail Main company email address
 * @param companyPhone Main company phone number
 * @param companyWebsite Company website URL
 * @param businessType Type of business entity
 * @param industrySector Industry classification
 * @param companySize Number of employees classification
 * @param annualShippingVolume Expected annual shipping volume
 * @param businessAddressLine1 Street address line 1
 * @param businessAddressLine2 Street address line 2
 * @param businessCity City
 * @param businessStateProvince State or province
 * @param businessPostalCode Postal/ZIP code
 * @param businessCountry Country
 * @param primaryContactFirstName Primary contact first name
 * @param primaryContactLastName Primary contact last name
 * @param primaryContactEmail Primary contact email
 * @param primaryContactPhone Primary contact phone
 * @param primaryContactPosition Primary contact job title
 * @param billingContactFirstName Billing contact first name (optional)
 * @param billingContactLastName Billing contact last name (optional)
 * @param billingContactEmail Billing contact email (optional)
 * @param billingContactPhone Billing contact phone (optional)
 * @param requestedCreditLimit Requested credit limit
 * @param preferredPaymentTerms Preferred payment terms
 * @param slaRequirements Special SLA requirements
 * @param preferredCommunicationMethod Preferred communication method
 * @param marketingConsent Consent to marketing communications
 * @param termsAccepted Terms and conditions acceptance
 * @param privacyPolicyAccepted Privacy policy acceptance
 * @param dataProcessingAgreementAccepted Data processing agreement acceptance
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
public record CreateCorporateOnboardingApplicationRequest(
    @NotBlank(message = "Company name is required")
    @Size(max = 200, message = "Company name must not exceed 200 characters")
    String companyName,

    @NotBlank(message = "Company registration number is required")
    @Size(max = 50, message = "Company registration number must not exceed 50 characters")
    String companyRegistrationNumber,

    @Size(max = 50, message = "Tax identification number must not exceed 50 characters")
    String taxIdentificationNumber,

    @Size(max = 50, message = "Business license number must not exceed 50 characters")
    String businessLicenseNumber,

    @NotBlank(message = "Company email is required")
    @Email(message = "Company email must be valid")
    @Size(max = 100, message = "Company email must not exceed 100 characters")
    String companyEmail,

    @NotBlank(message = "Company phone is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Company phone must be a valid international format")
    String companyPhone,

    @Size(max = 200, message = "Company website must not exceed 200 characters")
    String companyWebsite,

    @NotNull(message = "Business type is required")
    BusinessType businessType,

    @NotNull(message = "Industry sector is required")
    IndustrySector industrySector,

    @NotNull(message = "Company size is required")
    CompanySize companySize,

    @NotNull(message = "Annual shipping volume is required")
    ShippingVolume annualShippingVolume,

    @NotBlank(message = "Business address is required")
    @Size(max = 200, message = "Business address line 1 must not exceed 200 characters")
    String businessAddressLine1,

    @Size(max = 200, message = "Business address line 2 must not exceed 200 characters")
    String businessAddressLine2,

    @NotBlank(message = "Business city is required")
    @Size(max = 100, message = "Business city must not exceed 100 characters")
    String businessCity,

    @Size(max = 100, message = "Business state/province must not exceed 100 characters")
    String businessStateProvince,

    @NotBlank(message = "Business postal code is required")
    @Size(max = 20, message = "Business postal code must not exceed 20 characters")
    String businessPostalCode,

    @NotBlank(message = "Business country is required")
    @Size(max = 100, message = "Business country must not exceed 100 characters")
    String businessCountry,

    @NotBlank(message = "Primary contact first name is required")
    @Size(max = 100, message = "Primary contact first name must not exceed 100 characters")
    String primaryContactFirstName,

    @NotBlank(message = "Primary contact last name is required")
    @Size(max = 100, message = "Primary contact last name must not exceed 100 characters")
    String primaryContactLastName,

    @NotBlank(message = "Primary contact email is required")
    @Email(message = "Primary contact email must be valid")
    @Size(max = 100, message = "Primary contact email must not exceed 100 characters")
    String primaryContactEmail,

    @NotBlank(message = "Primary contact phone is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Primary contact phone must be a valid international format")
    String primaryContactPhone,

    @Size(max = 100, message = "Primary contact position must not exceed 100 characters")
    String primaryContactPosition,

    @Size(max = 100, message = "Billing contact first name must not exceed 100 characters")
    String billingContactFirstName,

    @Size(max = 100, message = "Billing contact last name must not exceed 100 characters")
    String billingContactLastName,

    @Email(message = "Billing contact email must be valid")
    @Size(max = 100, message = "Billing contact email must not exceed 100 characters")
    String billingContactEmail,

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Billing contact phone must be a valid international format")
    String billingContactPhone,

    @DecimalMin(value = "0.0", message = "Requested credit limit must be non-negative")
    @DecimalMax(value = "10000000.0", message = "Requested credit limit cannot exceed $10,000,000")
    BigDecimal requestedCreditLimit,

    PaymentTerms preferredPaymentTerms,

    @Size(max = 2000, message = "SLA requirements must not exceed 2000 characters")
    String slaRequirements,

    CommunicationMethod preferredCommunicationMethod,

    Boolean marketingConsent,

    @NotNull(message = "Terms acceptance is required")
    @AssertTrue(message = "Terms and conditions must be accepted")
    Boolean termsAccepted,

    @NotNull(message = "Privacy policy acceptance is required")
    @AssertTrue(message = "Privacy policy must be accepted")
    Boolean privacyPolicyAccepted,

    @NotNull(message = "Data processing agreement acceptance is required")
    @AssertTrue(message = "Data processing agreement must be accepted")
    Boolean dataProcessingAgreementAccepted
) {
    
    /**
     * Check if billing contact information is provided.
     */
    public boolean hasBillingContact() {
        return billingContactFirstName != null && billingContactLastName != null && 
               billingContactEmail != null;
    }
    
    /**
     * Check if this is a high-value prospect based on company size and shipping volume.
     */
    public boolean isHighValueProspect() {
        return (companySize == CompanySize.LARGE || companySize == CompanySize.ENTERPRISE) ||
               (annualShippingVolume == ShippingVolume.VERY_HIGH || annualShippingVolume == ShippingVolume.ENTERPRISE);
    }
    
    /**
     * Check if expedited processing is recommended.
     */
    public boolean recommendsExpeditedProcessing() {
        return isHighValueProspect() && 
               (requestedCreditLimit != null && requestedCreditLimit.compareTo(new BigDecimal("100000")) > 0);
    }
    
    /**
     * Get the recommended initial volume discount tier.
     */
    public VolumeDiscountTier getRecommendedDiscountTier() {
        return VolumeDiscountTier.fromAnnualVolume(
            switch (annualShippingVolume) {
                case LOW -> new BigDecimal("5000");
                case MODERATE -> new BigDecimal("15000");
                case HIGH -> new BigDecimal("75000");
                case VERY_HIGH -> new BigDecimal("150000");
                case ENTERPRISE -> new BigDecimal("750000");
            }
        );
    }
    
    /**
     * Check if dedicated account management is recommended.
     */
    public boolean recommendsDedicatedAccountManager() {
        return companySize.includesDedicatedAccountManager() || 
               annualShippingVolume.qualifiesForDedicatedAccountManager();
    }
    
    /**
     * Validate that primary contact email differs from company email for large organizations.
     */
    public boolean hasValidContactEmailSeparation() {
        if (companySize == CompanySize.STARTUP || companySize == CompanySize.SMALL) {
            return true; // Allow same email for small companies
        }
        return !companyEmail.equalsIgnoreCase(primaryContactEmail);
    }
}