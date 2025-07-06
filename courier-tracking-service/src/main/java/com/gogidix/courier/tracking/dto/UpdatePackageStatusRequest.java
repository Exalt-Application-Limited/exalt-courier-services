package com.gogidix.courierservices.tracking.$1;

import com.gogidix.courierservices.tracking.model.TrackingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO for updating the status of a package.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePackageStatusRequest {
    
    @NotNull(message = "Status is required")
    private TrackingStatus status;
    
    @NotBlank(message = "Description is required")
    @Size(max = 500, message = "Description must be less than 500 characters")
    private String description;
    
    @Size(max = 255, message = "Location must be less than 255 characters")
    private String location;
    
    private Double latitude;
    
    private Double longitude;
    
    private Long courierId;
    
    private Long facilityId;
    
    @Size(max = 50, message = "Scan type must be less than 50 characters")
    private String scanType;
    
    @Size(max = 1000, message = "Notes must be less than 1000 characters")
    private String notes;
} 