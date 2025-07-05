package com.gogidix.courier.tracking.dto;

import com.gogidix.courier.tracking.model.TrackingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for TrackingEvent entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackingEventDTO {
    
    private String id;
    private TrackingStatus status;
    private String description;
    private LocalDateTime eventTime;
    private String location;
    private Double latitude;
    private Double longitude;
    private Long courierId;
    private Long facilityId;
    private String scanType;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 