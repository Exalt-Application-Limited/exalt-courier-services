package com.gogidix.courier.customer.onboarding.dto;

import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request DTO for creating a new customer onboarding application.
 */
@Schema(description = "Request to create a new customer onboarding application")
public record CreateCustomerOnboardingApplicationRequest(
    
    @NotBlank(message = "Customer email is required")
    @Email(message = "Valid email address is required")
    @Schema(description = "Customer's email address", example = "john.doe@example.com")
    String customerEmail,
    
    @NotBlank(message = "Customer phone is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Valid phone number is required")
    @Schema(description = "Customer's phone number", example = "+1234567890")
    String customerPhone,
    
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @Schema(description = "Customer's first name", example = "John")
    String firstName,
    
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @Schema(description = "Customer's last name", example = "Doe")
    String lastName,
    
    @Schema(description = "Customer's date of birth", example = "1990-01-15")
    String dateOfBirth,
    
    @Schema(description = "Customer's national ID", example = "123456789")
    String nationalId,
    
    @Schema(description = "Address line 1", example = "123 Main Street")
    String addressLine1,
    
    @Schema(description = "Address line 2", example = "Apt 4B")
    String addressLine2,
    
    @Schema(description = "City", example = "New York")
    String city,
    
    @Schema(description = "State or Province", example = "NY")
    String stateProvince,
    
    @Schema(description = "Postal code", example = "10001")
    String postalCode,
    
    @NotBlank(message = "Country is required")
    @Schema(description = "Country", example = "United States")
    String country,
    
    @Schema(description = "Preferred communication method", example = "EMAIL", allowableValues = {"EMAIL", "SMS", "PHONE"})
    String preferredCommunicationMethod,
    
    @Schema(description = "Marketing consent", example = "true")
    Boolean marketingConsent,
    
    @NotNull(message = "Terms acceptance is required")
    @AssertTrue(message = "Terms and conditions must be accepted")
    @Schema(description = "Terms and conditions acceptance", example = "true")
    Boolean termsAccepted,
    
    @NotNull(message = "Privacy policy acceptance is required")
    @AssertTrue(message = "Privacy policy must be accepted")
    @Schema(description = "Privacy policy acceptance", example = "true")
    Boolean privacyPolicyAccepted
) {}