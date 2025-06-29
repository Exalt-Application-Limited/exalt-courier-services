package com.exalt.courier.drivermobileapp.dto.assignment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for Assignment data transfer
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentDTO {
    
    private String id;
    
    @NotNull(message = "Courier ID is required")
    private String courierId;
    
    @NotNull(message = "Status is required")
    private String status;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime assignedAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startedAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime completedAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime cancelledAt;
    
    private String cancellationReason;
    
    private Double estimatedDurationMinutes;
    
    private Double estimatedDistanceKm;
    
    @NotEmpty(message = "Assignment must have at least one delivery task")
    @Size(min = 1, message = "Assignment must have at least one delivery task")
    private List<AssignmentTaskDTO> tasks;
    
    private String routeOptimizationStatus;
    
    private String notes;
    
    private String syncStatus;
}
