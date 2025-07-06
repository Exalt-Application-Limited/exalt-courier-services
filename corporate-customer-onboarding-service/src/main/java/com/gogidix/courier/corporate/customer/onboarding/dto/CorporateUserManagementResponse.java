package com.gogidix.courier.corporate.customer.onboarding.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for corporate user management operations.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@Schema(description = "Response for corporate user management operations")
public record CorporateUserManagementResponse(
    
    @Schema(description = "Corporate ID", example = "CORP-AUTH-123456")
    String corporateId,
    
    @Schema(description = "Number of operations completed successfully", example = "3")
    Integer operationsCompleted,
    
    @Schema(description = "Results of each operation")
    List<String> results,
    
    @Schema(description = "Overall operation status message")
    String message,
    
    @Schema(description = "Operation completion timestamp")
    LocalDateTime completedAt,
    
    @Schema(description = "Current total user count", example = "15")
    Integer totalUsers,
    
    @Schema(description = "Active user count", example = "12")
    Integer activeUsers,
    
    @Schema(description = "Failed operations details")
    List<OperationFailure> failures
) {}

/**
 * Operation failure details.
 */
@Schema(description = "Operation failure details")
record OperationFailure(
    
    @Schema(description = "Failed operation type")
    String operationType,
    
    @Schema(description = "Target user email")
    String userEmail,
    
    @Schema(description = "Failure reason")
    String reason,
    
    @Schema(description = "Error code")
    String errorCode
) {}