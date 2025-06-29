package com.exalt.courier.drivermobileapp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;

/**
 * Represents the courier's vehicle information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleInfo {
    
    private VehicleType type;
    
    @NotBlank(message = "Vehicle make is required")
    private String make;
    
    @NotBlank(message = "Vehicle model is required")
    private String model;
    
    private Integer year;
    
    private String color;
    
    @NotBlank(message = "License plate number is required")
    @Pattern(regexp = "^[A-Z0-9]{1,10}$", message = "Please provide a valid license plate number")
    private String licensePlate;
    
    private String vinNumber;
    
    private String insuranceProvider;
    
    private String insurancePolicyNumber;
    
    private LocalDate insuranceExpiryDate;
    
    private LocalDate registrationExpiryDate;
    
    private Boolean isApproved;
    
    private String inspectionNotes;
    
    private LocalDate lastInspectionDate;
    
    /**
     * Types of vehicles that couriers can use.
     */
    public enum VehicleType {
        BICYCLE,
        MOTORCYCLE,
        SCOOTER,
        CAR,
        SUV,
        VAN,
        TRUCK,
        FOOT
    }
} 
