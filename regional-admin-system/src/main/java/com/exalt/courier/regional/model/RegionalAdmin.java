package com.exalt.courier.regional.model;

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
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Entity representing a regional administrator user within the courier service.
 */
@Entity
@Table(name = "regional_admin")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegionalAdmin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "username", unique = true)
    private String username;

    @NotBlank
    @Column(name = "password")
    private String password;

    @NotBlank
    @Column(name = "first_name")
    private String firstName;

    @NotBlank
    @Column(name = "last_name")
    private String lastName;

    @Email
    @NotBlank
    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @NotNull
    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "last_login_date")
    private LocalDateTime lastLoginDate;

    @NotNull
    @Column(name = "regional_settings_id")
    private Long regionalSettingsId;

    @Column(name = "position")
    private String position;

    @Column(name = "department")
    private String department;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "regional_admin_roles", joinColumns = @JoinColumn(name = "admin_id"))
    @Column(name = "role")
    private Set<String> roles;

    @Column(name = "access_level")
    private Integer accessLevel;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "emergency_contact")
    private String emergencyContact;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    @Column(name = "version")
    private Long version;

    @Column(name = "managed_locations", columnDefinition = "TEXT")
    private String managedLocations;

    @Column(name = "reports_to")
    private Long reportsTo;

    @Column(name = "two_factor_enabled")
    private Boolean twoFactorEnabled;

    @Column(name = "account_locked")
    private Boolean accountLocked;

    @Column(name = "account_expires_at")
    private LocalDateTime accountExpiresAt;
}
