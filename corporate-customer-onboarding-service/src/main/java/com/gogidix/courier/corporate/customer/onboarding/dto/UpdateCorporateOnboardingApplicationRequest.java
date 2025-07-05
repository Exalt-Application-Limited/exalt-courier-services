package com.gogidix.courier.corporate.customer.onboarding.dto;

import com.gogidix.courier.corporate.customer.onboarding.enums.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * Request DTO for updating an existing corporate customer onboarding application.
 * 
 * Allows partial updates to application information. Null values indicate
 * no change to existing data.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
public record UpdateCorporateOnboardingApplicationRequest(
    @Size(max = 200, message = "Company name must not exceed 200 characters")
    String companyName,

    @Size(max = 50, message = "Tax identification number must not exceed 50 characters")
    String taxIdentificationNumber,

    @Size(max = 50, message = "Business license number must not exceed 50 characters")
    String businessLicenseNumber,

    @Email(message = "Company email must be valid")
    @Size(max = 100, message = "Company email must not exceed 100 characters")
    String companyEmail,

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Company phone must be a valid international format")
    String companyPhone,

    @Size(max = 200, message = "Company website must not exceed 200 characters")
    String companyWebsite,

    BusinessType businessType,
    IndustrySector industrySector,
    CompanySize companySize,
    ShippingVolume annualShippingVolume,

    @Size(max = 200, message = "Business address line 1 must not exceed 200 characters")
    String businessAddressLine1,

    @Size(max = 200, message = "Business address line 2 must not exceed 200 characters")
    String businessAddressLine2,

    @Size(max = 100, message = "Business city must not exceed 100 characters")
    String businessCity,

    @Size(max = 100, message = "Business state/province must not exceed 100 characters")
    String businessStateProvince,

    @Size(max = 20, message = "Business postal code must not exceed 20 characters")
    String businessPostalCode,

    @Size(max = 100, message = "Business country must not exceed 100 characters")
    String businessCountry,

    @Size(max = 100, message = "Primary contact first name must not exceed 100 characters")
    String primaryContactFirstName,

    @Size(max = 100, message = "Primary contact last name must not exceed 100 characters")
    String primaryContactLastName,

    @Email(message = "Primary contact email must be valid")
    @Size(max = 100, message = "Primary contact email must not exceed 100 characters")
    String primaryContactEmail,

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

    Boolean marketingConsent
) {
    
    /**
     * Check if any contact information is being updated.
     */
    public boolean hasContactUpdates() {
        return primaryContactFirstName != null || primaryContactLastName != null ||
               primaryContactEmail != null || primaryContactPhone != null ||
               primaryContactPosition != null;
    }
    
    /**
     * Check if billing contact is being updated.
     */
    public boolean hasBillingContactUpdates() {
        return billingContactFirstName != null || billingContactLastName != null ||
               billingContactEmail != null || billingContactPhone != null;
    }
    
    /**
     * Check if business information is being updated.
     */
    public boolean hasBusinessInfoUpdates() {
        return companyName != null || companyEmail != null || companyPhone != null ||
               companyWebsite != null || businessType != null || industrySector != null ||
               companySize != null || annualShippingVolume != null;
    }
    
    /**
     * Check if address information is being updated.
     */
    public boolean hasAddressUpdates() {
        return businessAddressLine1 != null || businessAddressLine2 != null ||
               businessCity != null || businessStateProvince != null ||
               businessPostalCode != null || businessCountry != null;
    }
    
    /**
     * Check if service requirements are being updated.
     */
    public boolean hasServiceRequirementUpdates() {
        return requestedCreditLimit != null || preferredPaymentTerms != null ||
               slaRequirements != null || preferredCommunicationMethod != null;
    }
}