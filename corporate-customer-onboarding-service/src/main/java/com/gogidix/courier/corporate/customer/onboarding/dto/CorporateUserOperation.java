package com.gogidix.courier.corporate.customer.onboarding.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CorporateUserOperation {
    
    @NotBlank(message = "Operation ID is required")
    private String operationId;
    
    @NotNull(message = "Operation type is required")
    @Pattern(regexp = "CREATE|UPDATE|DELETE|SUSPEND|ACTIVATE|RESET_PASSWORD|CHANGE_ROLE", message = "Invalid operation type")
    private String operationType;
    
    @NotBlank(message = "User ID is required")
    private String userId;
    
    @NotBlank(message = "Corporate ID is required")
    private String corporateId;
    
    @NotBlank(message = "Performed by is required")
    private String performedBy;
    
    private LocalDateTime operationTimestamp;
    
    @Pattern(regexp = "PENDING|IN_PROGRESS|COMPLETED|FAILED|CANCELLED", message = "Invalid operation status")
    private String status;
    
    private String reason;
    
    private Map<String, Object> operationDetails;
    
    private Map<String, Object> previousState;
    
    private Map<String, Object> newState;
    
    private String approvalRequired;
    
    private String approvedBy;
    
    private LocalDateTime approvalTimestamp;
    
    private String ipAddress;
    
    private String userAgent;
    
    private Integer retryCount;
    
    private String errorMessage;
    
    private LocalDateTime completedAt;
}