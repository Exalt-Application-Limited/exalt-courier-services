package com.gogidix.courier.corporate.customer.onboarding.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * Request DTO for managing corporate users.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@Schema(description = "Request for corporate user management operations")
public record CorporateUserManagementRequest(
    
    @NotEmpty(message = "At least one operation is required")
    @Valid
    @Schema(description = "List of user management operations to perform")
    List<CorporateUserOperation> operations
) {}

/**
 * Individual user management operation.
 */
@Schema(description = "Corporate user management operation")
record CorporateUserOperation(
    
    @Schema(description = "Operation type", example = "ADD_USER", 
            allowableValues = {"ADD_USER", "REMOVE_USER", "UPDATE_PERMISSIONS", "ACTIVATE_USER", "DEACTIVATE_USER", "RESET_PASSWORD"})
    String type,
    
    @Schema(description = "Target user email", example = "user@company.com")
    String userEmail,
    
    @Schema(description = "Operation action or details", example = "Grant admin permissions")
    String action,
    
    @Schema(description = "User details for ADD_USER operations")
    CorporateUserDetails userDetails,
    
    @Schema(description = "Permission changes for UPDATE_PERMISSIONS operations")
    List<String> permissions,
    
    @Schema(description = "Operation reason", example = "User promoted to manager")
    String reason
) {}

/**
 * Corporate user details.
 */
@Schema(description = "Corporate user details")
record CorporateUserDetails(
    
    @Schema(description = "User first name", example = "John")
    String firstName,
    
    @Schema(description = "User last name", example = "Doe")
    String lastName,
    
    @Schema(description = "User role", example = "MANAGER")
    String role,
    
    @Schema(description = "User department", example = "Sales")
    String department,
    
    @Schema(description = "User permissions")
    List<String> permissions,
    
    @Schema(description = "Cost center assignment")
    String costCenter,
    
    @Schema(description = "Spending limit")
    Double spendingLimit
) {}