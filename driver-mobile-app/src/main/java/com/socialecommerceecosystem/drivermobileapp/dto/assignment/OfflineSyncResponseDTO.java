package com.gogidix.courier.drivermobileapp.dto.assignment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * DTO for offline synchronization responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfflineSyncResponseDTO {
    
    private String courierId;
    
    private Map<String, SyncResultDTO> syncResults;
    
    private List<AssignmentDTO> newAssignments;
    
    private Map<String, String> serverTimestamps;
    
    private Map<String, String> configUpdates;
    
    private String syncToken;
    
    private String message;
}
