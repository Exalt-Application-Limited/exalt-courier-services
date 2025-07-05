package com.gogidix.courier.courier.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for assignment task information from courier management service
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentTaskDTO {
    private Long id;
    private Long assignmentId;
    private String taskCode;
    private String type;
    private String status;
    private Integer sequence;
    private String description;
    private Double latitude;
    private Double longitude;
    private String address;
    private String recipientName;
    private String recipientPhone;
    private LocalDateTime scheduledTime;
    private LocalDateTime completedTime;
    private String notes;
}