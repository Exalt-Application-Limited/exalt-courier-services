package com.gogidix.courier.corporate.customer.onboarding.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CorporateUserRequest {
    
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s'-]+$", message = "First name can only contain letters, spaces, hyphens, and apostrophes")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s'-]+$", message = "Last name can only contain letters, spaces, hyphens, and apostrophes")
    private String lastName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;
    
    @NotBlank(message = "Role is required")
    @Pattern(regexp = "^(CORPORATE_ADMIN|CORPORATE_MANAGER|CORPORATE_USER|BILLING_ADMIN|OPERATIONS_MANAGER)$", 
            message = "Role must be one of: CORPORATE_ADMIN, CORPORATE_MANAGER, CORPORATE_USER, BILLING_ADMIN, OPERATIONS_MANAGER")
    private String role;
    
    @Size(min = 1, message = "At least one permission is required")
    private List<String> permissions;
    
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number must be valid international format")
    private String phoneNumber;
    
    private String department;
    
    private String jobTitle;
    
    @Builder.Default
    private boolean isActive = true;
    
    @Builder.Default
    private boolean isPrimaryContact = false;
    
    // Convenience methods for DTOs
    public String email() {
        return this.email;
    }
    
    public String firstName() {
        return this.firstName;
    }
    
    public String lastName() {
        return this.lastName;
    }
    
    public String role() {
        return this.role;
    }
    
    public List<String> permissions() {
        return this.permissions;
    }
}