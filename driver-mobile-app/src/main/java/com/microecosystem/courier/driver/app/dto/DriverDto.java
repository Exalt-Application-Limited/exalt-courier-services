package com.microecosystem.courier.driver.app.dto;

import com.microecosystem.courier.driver.app.model.DriverStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Driver entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverDto {

    private Long id;
    
    @NotBlank(message = "First name is required")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    private String lastName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number should be valid")
    private String phoneNumber;
    
    private String profilePictureUrl;
    
    @NotNull(message = "Status is required")
    private DriverStatus status;
    
    private BigDecimal currentLatitude;
    private BigDecimal currentLongitude;
    private LocalDateTime lastLocationUpdate;
    
    private String deviceToken;
    private String vehicleType;
    private String vehicleLicensePlate;
    
    private Integer totalDeliveries;
    private Integer completedDeliveries;
    private Integer canceledDeliveries;
    private BigDecimal averageRating;
    private Integer totalRatings;
    
    private Boolean isActive;
    private Boolean isVerified;
    
    private Long userId;
} 