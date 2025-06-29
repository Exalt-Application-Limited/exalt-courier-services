package com.microecosystem.courier.driver.app.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Driver entity representing courier drivers in the system.
 */
@Entity
@Table(name = "driver")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Associated user account
     */
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Driver's first name
     */
    @Column(name = "first_name", nullable = false)
    private String firstName;

    /**
     * Driver's last name
     */
    @Column(name = "last_name", nullable = false)
    private String lastName;

    /**
     * Driver's phone number
     */
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    /**
     * Driver's email address
     */
    @Column(name = "email")
    private String email;

    /**
     * Driver's license number
     */
    @Column(name = "license_number", nullable = false)
    private String licenseNumber;

    /**
     * Vehicle registration number
     */
    @Column(name = "vehicle_registration")
    private String vehicleRegistration;

    /**
     * Vehicle model
     */
    @Column(name = "vehicle_model")
    private String vehicleModel;

    /**
     * Vehicle type
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type")
    private VehicleType vehicleType;

    /**
     * Current driver status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DriverStatus status;

    /**
     * Current latitude
     */
    @Column(name = "current_latitude", precision = 10, scale = 7)
    private BigDecimal currentLatitude;

    /**
     * Current longitude
     */
    @Column(name = "current_longitude", precision = 10, scale = 7)
    private BigDecimal currentLongitude;

    /**
     * Last location update timestamp
     */
    @Column(name = "last_location_update")
    private LocalDateTime lastLocationUpdate;

    /**
     * Rating (average of all ratings)
     */
    @Column(name = "rating", precision = 3, scale = 2)
    private BigDecimal rating;

    /**
     * Total number of completed deliveries
     */
    @Column(name = "completed_deliveries")
    private Integer completedDeliveries;

    /**
     * Whether the driver is currently available for deliveries
     */
    @Column(name = "available")
    private Boolean available;

    /**
     * Firebase device token for push notifications
     */
    @Column(name = "device_token")
    private String deviceToken;

    /**
     * Account creation timestamp
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * Last account update timestamp
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = DriverStatus.INACTIVE;
        }
        if (available == null) {
            available = false;
        }
        if (completedDeliveries == null) {
            completedDeliveries = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 