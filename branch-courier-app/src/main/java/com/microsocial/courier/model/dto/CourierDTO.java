package com.exalt.courier.courier.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for courier information from courier management service
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourierDTO {
    private Long id;
    private String courierCode;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String status;
    private Long branchId;
    private String branchName;
    private String vehicleType;
    private String vehicleRegistration;
    private Double currentLatitude;
    private Double currentLongitude;
    private LocalDateTime lastLocationUpdate;
    private Boolean available;
}