package com.exalt.courier.regional.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Regional settings entity representing configuration for a specific geographical region.
 */
@Entity
@Table(name = "regional_settings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegionalSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "global_region_id")
    private Long globalRegionId;

    @NotBlank
    @Column(name = "region_name")
    private String regionName;

    @Column(name = "region_code", unique = true)
    private String regionCode;

    @NotNull
    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "timezone")
    private String timezone;

    @Column(name = "locale")
    private String locale;

    @Column(name = "currency_code")
    private String currencyCode;

    @Column(name = "measurement_system")
    private String measurementSystem;

    @Column(name = "business_hours", columnDefinition = "TEXT")
    private String businessHours;

    @Column(name = "contact_email")
    private String contactEmail;

    @Column(name = "contact_phone")
    private String contactPhone;

    @Column(name = "emergency_contact")
    private String emergencyContact;

    @Column(name = "regional_manager")
    private String regionalManager;

    @Column(name = "settings_json", columnDefinition = "TEXT")
    private String settingsJson;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    @Column(name = "version")
    private Long version;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotNull
    @Column(name = "global_settings_sync")
    private Boolean globalSettingsSync;

    @Transient
    private Map<String, Object> additionalSettings;
}
