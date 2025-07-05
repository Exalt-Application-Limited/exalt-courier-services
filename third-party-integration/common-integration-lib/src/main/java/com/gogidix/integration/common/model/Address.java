package com.gogidix.integration.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Represents an address for shipping origin or destination.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    
    @NotBlank(message = "Contact name is required")
    @Size(max = 50, message = "Contact name must not exceed 50 characters")
    private String contactName;
    
    @Size(max = 50, message = "Company name must not exceed 50 characters")
    private String companyName;
    
    @NotBlank(message = "Street address is required")
    @Size(max = 100, message = "Street address must not exceed 100 characters")
    private String street1;
    
    @Size(max = 100, message = "Additional address information must not exceed 100 characters")
    private String street2;
    
    @NotBlank(message = "City is required")
    @Size(max = 50, message = "City must not exceed 50 characters")
    private String city;
    
    @Size(max = 50, message = "State/province must not exceed 50 characters")
    private String stateProvince;
    
    @NotBlank(message = "Postal code is required")
    @Size(max = 20, message = "Postal code must not exceed 20 characters")
    private String postalCode;
    
    @NotBlank(message = "Country code is required")
    @Size(min = 2, max = 2, message = "Country code must be ISO 2-letter code")
    @Pattern(regexp = "^[A-Z]{2}$", message = "Country code must be 2 uppercase letters")
    private String countryCode;
    
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[0-9\\s\\-()]{10,20}$", message = "Invalid phone number format")
    private String phoneNumber;
    
    @Email(message = "Invalid email format")
    private String email;
    
    private boolean residential;
    
    // Tax identifiers - for international shipments
    private String taxId;
    private String vatNumber;
    private String eoriNumber; // Economic Operators Registration and Identification number
} 
