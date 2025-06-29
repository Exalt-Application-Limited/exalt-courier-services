package com.exalt.courier.drivermobileapp.dto.tracking;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO for tracking events
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackingEventDTO {
    
    private String id;
    
    private String packageId;
    
    @NotBlank(message = "Event type is required")
    private String eventType;
    
    @NotBlank(message = "Status is required")
    private String status;
    
    @NotNull(message = "Event time is required")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime eventTime;
    
    private String location;
    
    private Double latitude;
    
    private Double longitude;
    
    private String description;
    
    private String driverId;
    
    private String driverName;
    
    private Map<String, Object> eventData;
    
    private boolean syncStatus;
}
