package com.exalt.courierservices.management.$1;

import com.exalt.courier.management.assignment.model.TaskStatus;
import com.exalt.courier.management.assignment.model.TaskType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for {@link com.microecosystem.courier.management.assignment.model.AssignmentTask}
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentTaskDTO {
    
    private String id;
    
    private String assignmentId;
    
    @NotNull(message = "Task type cannot be null")
    private TaskType taskType;
    
    private TaskStatus status;
    
    private Integer sequence;
    
    @NotBlank(message = "Address cannot be blank")
    @Size(min = 1, max = 255, message = "Address must be between 1 and 255 characters")
    private String address;
    
    @Size(max = 100, message = "Address line 2 must be less than 100 characters")
    private String addressLine2;
    
    @Size(max = 100, message = "City must be less than 100 characters")
    private String city;
    
    @Size(max = 20, message = "Postal code must be less than 20 characters")
    private String postalCode;
    
    @Size(max = 100, message = "State/province must be less than 100 characters")
    private String stateProvince;
    
    @Size(max = 100, message = "Country must be less than 100 characters")
    private String country;
    
    @NotNull(message = "Latitude cannot be null")
    private Double latitude;
    
    @NotNull(message = "Longitude cannot be null")
    private Double longitude;
    
    private LocalDateTime scheduledTime;
    
    private LocalDateTime completedTime;
    
    private Integer estimatedDurationMinutes;
    
    private Integer actualDurationMinutes;
    
    @Size(max = 1000, message = "Notes must be less than 1000 characters")
    private String notes;
    
    @Size(max = 255, message = "Contact name must be less than 255 characters")
    private String contactName;
    
    @Size(max = 50, message = "Contact phone must be less than 50 characters")
    private String contactPhone;
    
    @Size(max = 500, message = "Instructions must be less than 500 characters")
    private String instructions;
    
    private LocalDateTime timeWindowStart;
    
    private LocalDateTime timeWindowEnd;
    
    @Size(max = 255, message = "Reference code must be less than 255 characters")
    private String referenceCode;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    /**
     * Checks if the task is overdue based on the scheduled time
     * 
     * @return true if the task is overdue, false otherwise
     */
    public boolean isOverdue() {
        return scheduledTime != null && 
               LocalDateTime.now().isAfter(scheduledTime) && 
               (status == null || !status.isTerminal());
    }
} 