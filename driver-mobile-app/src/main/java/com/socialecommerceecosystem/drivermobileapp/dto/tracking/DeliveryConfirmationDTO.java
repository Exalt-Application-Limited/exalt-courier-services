package com.exalt.courier.drivermobileapp.dto.tracking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

/**
 * DTO for delivery confirmation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryConfirmationDTO {
    
    @NotBlank(message = "Package ID is required")
    private String packageId;
    
    @NotBlank(message = "Driver ID is required")
    private String driverId;
    
    private String signatureBase64;
    
    private String photoBase64;
    
    private String receivedBy;
    
    private boolean leftAtDoor;
    
    private String notes;
    
    private Double latitude;
    
    private Double longitude;
    
    private Map<String, Object> additionalInfo;
}
