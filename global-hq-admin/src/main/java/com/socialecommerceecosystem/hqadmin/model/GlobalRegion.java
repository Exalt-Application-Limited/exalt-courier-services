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
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a global geographic region that contains multiple countries or areas.
 * This entity enables hierarchical management of courier services across different
 * geographical regions of the world.
 */
@Data
@Entity
@Table(name = "global_regions")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GlobalRegion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "region_code", unique = true, nullable = false)
    private String regionCode;
    
    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
    
    @Column(name = "timezone")
    private String timezone;
    
    @Column(name = "locale")
    private String locale;
    
    @Column(name = "currency_code")
    private String currencyCode;
    
    // Geographic coordinates for region center (for map visualization)
    @Column(name = "latitude")
    private Double latitude;
    
    @Column(name = "longitude")
    private Double longitude;
    
    // Parent region if this is a sub-region
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_region_id")
    private GlobalRegion parentRegion;
    
    // Child regions if this is a parent region
    @OneToMany(mappedBy = "parentRegion", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<GlobalRegion> childRegions = new HashSet<>();
    
    // Regional admin systems managed by this global region
    @OneToMany(mappedBy = "globalRegion", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RegionalAdminSystem> regionalAdminSystems = new HashSet<>();
    
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
    
    /**
     * Adds a child region to this region
     * 
     * @param childRegion The child region to add
     * @return this region for chaining
     */
    public GlobalRegion addChildRegion(GlobalRegion childRegion) {
        childRegions.add(childRegion);
        childRegion.setParentRegion(this);
        return this;
    }
    
    /**
     * Removes a child region from this region
     * 
     * @param childRegion The child region to remove
     * @return this region for chaining
     */
    public GlobalRegion removeChildRegion(GlobalRegion childRegion) {
        childRegions.remove(childRegion);
        childRegion.setParentRegion(null);
        return this;
    }
}
