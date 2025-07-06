package com.gogidix.courier.courier.drivermobileapp.dto.tracking;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO for package tracking information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackingInfoDTO {
    
    private String packageId;
    
    @NotBlank(message = "Tracking number is required")
    private String trackingNumber;
    
    @NotBlank(message = "Status is required")
    private String status;
    
    private String courierName;
    
    private String courierId;
    
    private String origin;
    
    private String destination;
    
    private String receiverName;
    
    private String receiverPhone;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime estimatedDeliveryTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime actualDeliveryTime;
    
    private boolean delivered;
    
    private String proofOfDelivery;
    
    private String signatureImage;
    
    private String deliveryNotes;
    
    private List<TrackingEventDTO> events;
    
    private Map<String, Object> additionalInfo;
}
