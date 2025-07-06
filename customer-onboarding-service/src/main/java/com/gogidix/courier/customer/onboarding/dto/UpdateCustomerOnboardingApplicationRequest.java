package com.gogidix.courier.customer.onboarding.dto;

import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request DTO for updating a customer onboarding application.
 */
@Schema(description = "Request to update a customer onboarding application")
public record UpdateCustomerOnboardingApplicationRequest(
    
    @Email(message = "Valid email address is required")
    @Schema(description = "Customer's email address", example = "john.doe@example.com")
    String customerEmail,
    
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Valid phone number is required")
    @Schema(description = "Customer's phone number", example = "+1234567890")
    String customerPhone,
    
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @Schema(description = "Customer's first name", example = "John")
    String firstName,
    
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
    
    @Schema(description = "Country", example = "United States")
    String country,
    
    @Schema(description = "Preferred communication method", example = "EMAIL")
    String preferredCommunicationMethod,
    
    @Schema(description = "Marketing consent", example = "true")
    Boolean marketingConsent
) {}