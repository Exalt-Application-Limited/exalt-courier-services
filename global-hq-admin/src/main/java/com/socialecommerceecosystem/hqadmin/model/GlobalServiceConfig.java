package com.gogidix.courier.courier.hqadmin.model;

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
 * Represents global service configuration parameters that define how the courier services operate.
 * These settings can be overridden at regional levels but provide the default global behavior.
 */
@Data
@Entity
@Table(name = "global_service_configs")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GlobalServiceConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "service_key", nullable = false)
    private String serviceKey;
    
    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "config_value", columnDefinition = "TEXT")
    private String configValue;
    
    @Column(name = "config_type")
    private String configType;
    
    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
    
    @NotNull
    @Column(name = "allow_regional_override", nullable = false)
    private Boolean allowRegionalOverride;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "global_region_id")
    private GlobalRegion globalRegion;
    
    @Column(name = "service_category")
    private String serviceCategory;
    
    @Column(name = "last_updated_by")
    private String lastUpdatedBy;
    
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
