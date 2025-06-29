package com.exalt.courier.drivermobileapp.dto.assignment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for Assignment Task data transfer
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentTaskDTO {
    
    private String id;
    
    private String assignmentId;
    
    @NotNull(message = "Task type is required")
    private String taskType;
    
    @NotNull(message = "Status is required")
    private String status;
    
    private Integer sequenceNumber;
    
    @NotNull(message = "Address is required")
    private String address;
    
    @NotNull(message = "Latitude is required")
    private BigDecimal latitude;
    
    @NotNull(message = "Longitude is required")
    private BigDecimal longitude;
    
    private String contactName;
    
    private String contactPhone;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timeWindowStart;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timeWindowEnd;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime estimatedArrivalTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime actualArrivalTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime completedAt;
    
    private String completionCode;
    
    private String notes;
    
    private String trackingNumber;
    
    private String packageId;
    
    private String syncStatus;
}
