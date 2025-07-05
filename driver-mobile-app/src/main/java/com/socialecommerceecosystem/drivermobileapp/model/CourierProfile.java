package com.gogidix.courier.drivermobileapp.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents the courier's profile in the mobile app.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "courier_profiles")
public class CourierProfile {

    @Id
    private String id;
    
    @NotBlank(message = "Courier ID is required")
    private String courierId;
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;
    
    private String password; // Stored as hash
    
    @NotBlank(message = "First name is required")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    private String lastName;
    
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Please provide a valid phone number")
    private String phoneNumber;
    
    @Email(message = "Please provide a valid email address")
    private String email;
    
    private String profileImageUrl;
    
    private Boolean isOnline;
    
    private LocationData currentLocation;
    
    private VehicleInfo vehicleInfo;
    
    private String fcmToken; // Firebase Cloud Messaging token for push notifications
    
    private Set<String> preferredDeliveryZones = new HashSet<>();
    
    private OnboardingStatus onboardingStatus;
    
    private AccountStatus accountStatus;
    
    private Set<String> deliverySkills = new HashSet<>();
    
    private Double averageRating;
    
    private Integer totalDeliveries;
    
    private String deviceInfo;
    
    private String appVersion;
    
    private String languagePreference;
    
    @CreatedDate
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    private LocalDateTime lastLoginAt;
} 

