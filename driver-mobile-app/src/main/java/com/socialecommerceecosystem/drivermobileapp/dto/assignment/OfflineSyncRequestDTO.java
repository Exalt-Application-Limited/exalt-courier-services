package com.gogidix.courier.courier.drivermobileapp.dto.assignment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

/**
 * DTO for offline synchronization requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfflineSyncRequestDTO {
    
    @NotNull(message = "Courier ID is required")
    private String courierId;
    
    @NotEmpty(message = "Assignments to sync cannot be empty")
    private Map<String, AssignmentDTO> assignments;
    
    private Map<String, String> completionProofs;
    
    private String deviceInfo;
    
    private String appVersion;
}
