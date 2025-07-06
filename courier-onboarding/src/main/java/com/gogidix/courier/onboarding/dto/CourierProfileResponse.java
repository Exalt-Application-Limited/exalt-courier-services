package com.gogidix.courier.onboarding.dto;

import com.gogidix.courier.onboarding.model.CourierStatus;
import com.gogidix.courier.onboarding.model.TransportationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for courier profile responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourierProfileResponse {

    private String courierId;
    private String applicationReferenceId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String streetAddress;
    private String city;
    private String stateProvince;
    private String postalCode;
    private String country;
    private TransportationType transportationType;
    private String vehicleMake;
    private String vehicleModel;
    private String vehicleYear;
    private String vehicleColor;
    private String vehicleLicensePlate;
    private LocalDate licenseExpiryDate;
    private LocalDate insuranceExpiryDate;
    private CourierStatus status;
    private Boolean available;
    private String workingHours;
    private String serviceRegions;
    private Integer maxDeliveryDistance;
    private LocalDateTime activatedAt;
    private String activatedBy;
    private String bankName;
    private String accountHolderName;
    private String accountNumberMasked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Double averageRating;
}
