package com.exalt.courier.hqadmin.model;

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

/**
 * Represents a Regional Admin System that operates under the global HQ.
 * Each Regional Admin System handles routing requests to appropriate local courier networks
 * and manages all courier operations within a specific geographic region.
 */
@Data
@Entity
@Table(name = "regional_admin_systems")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegionalAdminSystem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "system_code", unique = true, nullable = false)
    private String systemCode;
    
    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
    
    @Column(name = "api_endpoint")
    private String apiEndpoint;
    
    @Column(name = "api_key")
    private String apiKey;
    
    @Column(name = "auth_token")
    private String authToken;
    
    @Column(name = "health_check_endpoint")
    private String healthCheckEndpoint;
    
    @Column(name = "last_health_check")
    private LocalDateTime lastHealthCheck;
    
    @Column(name = "health_status")
    private String healthStatus;
    
    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "global_region_id", nullable = false)
    private GlobalRegion globalRegion;
    
    @Column(name = "contact_email")
    private String contactEmail;
    
    @Column(name = "contact_phone")
    private String contactPhone;
    
    @Column(name = "support_url")
    private String supportUrl;
    
    @NotNull
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @NotNull
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Version
    private Integer version;
}
