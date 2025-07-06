package com.gogidix.courier.regional.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Represents a physical location or branch within the courier network.
 */
@Entity
@Table(name = "courier_location")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourierLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "location_name")
    private String locationName;

    @Column(name = "location_code", unique = true)
    private String locationCode;

    @NotNull
    @Column(name = "regional_settings_id")
    private Long regionalSettingsId;

    @NotNull
    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "location_type")
    private String locationType;

    @NotBlank
    @Column(name = "address_line1")
    private String addressLine1;

    @Column(name = "address_line2")
    private String addressLine2;

    @NotBlank
    @Column(name = "city")
    private String city;

    @NotBlank
    @Column(name = "state_province")
    private String stateProvince;

    @NotBlank
    @Column(name = "postal_code")
    private String postalCode;

    @NotBlank
    @Column(name = "country")
    private String country;

    @Column(name = "latitude", precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(name = "contact_phone")
    private String contactPhone;

    @Column(name = "contact_email")
    private String contactEmail;

    @Column(name = "manager_id")
    private Long managerId;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "operating_hours", columnDefinition = "TEXT")
    private String operatingHours;

    @Column(name = "services_offered", columnDefinition = "TEXT")
    private String servicesOffered;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "location_rating")
    private BigDecimal locationRating;

    @Column(name = "is_hub")
    private Boolean isHub;

    @Column(name = "parent_location_id")
    private Long parentLocationId;

    @Column(name = "service_area_radius")
    private Integer serviceAreaRadius;

    @Column(name = "max_daily_packages")
    private Integer maxDailyPackages;

    @Column(name = "storage_capacity")
    private Integer storageCapacity;

    @Column(name = "has_refrigeration")
    private Boolean hasRefrigeration;

    @Column(name = "has_security")
    private Boolean hasSecurity;

    @Column(name = "property_size_sqft")
    private Integer propertySizeSqft;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    @Column(name = "version")
    private Long version;

    @Column(name = "metadata_json", columnDefinition = "TEXT")
    private String metadataJson;

    @Transient
    private Map<String, Object> additionalMetadata;
}
