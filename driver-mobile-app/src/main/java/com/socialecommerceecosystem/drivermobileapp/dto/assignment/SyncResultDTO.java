package com.gogidix.courier.drivermobileapp.dto.assignment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for synchronization result data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncResultDTO {
    
    private String assignmentId;
    
    private String result;
    
    private String message;
    
    private AssignmentDTO serverAssignment;
}
