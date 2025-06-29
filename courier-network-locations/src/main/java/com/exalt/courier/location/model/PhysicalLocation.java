package com.exalt.courier.location.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vladmihalcea.hibernate.type.json.JsonType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

/**
 * Represents a physical courier network location such as a branch office,
 * hub, sorting center, or pickup point within the courier services network.
 */
@Entity
@Table(name = "physical_locations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TypeDef(name = "json", typeClass = JsonType.class)
public class PhysicalLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LocationType locationType;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private String zipCode;

    // Geographical coordinates for mapping and routing
    private Double latitude;
    private Double longitude;

    @Column(nullable = false)
    private String contactPhone;

    private String contactEmail;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "max_capacity")
    private Integer maxCapacity;

    @Column(name = "current_capacity_usage")
    private Integer currentCapacityUsage;

    @Type(type = "json")
    @Column(columnDefinition = "jsonb")
    private String additionalFeatures;

    @Column(name = "activation_date")
    private LocalDateTime activationDate;

    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    @Column(name = "regional_admin_id")
    private Long regionalAdminId;

    @OneToMany(mappedBy = "physicalLocation", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<LocationOperatingHours> operatingHours = new HashSet<>();

    @OneToMany(mappedBy = "physicalLocation", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<LocationStaff> staff = new HashSet<>();

    /**
     * Gets the capacity utilization percentage of this location.
     * 
     * @return the capacity utilization as a percentage
     */
    @Transient
    public double getCapacityUtilization() {
        if (maxCapacity == null || maxCapacity == 0 || currentCapacityUsage == null) {
            return 0.0;
        }
        return ((double) currentCapacityUsage / maxCapacity) * 100.0;
    }

    /**
     * Checks if this location has available capacity for additional packages.
     * 
     * @return true if capacity is available, false otherwise
     */
    @Transient
    public boolean hasAvailableCapacity() {
        if (maxCapacity == null || currentCapacityUsage == null) {
            return true;
        }
        return currentCapacityUsage < maxCapacity;
    }

    /**
     * Gets the additional features as a JsonNode object.
     * 
     * @return JsonNode representation of additional features
     */
    @Transient
    public JsonNode getAdditionalFeaturesAsJson() {
        try {
            if (additionalFeatures == null || additionalFeatures.isEmpty()) {
                return new ObjectMapper().createObjectNode();
            }
            return new ObjectMapper().readTree(additionalFeatures);
        } catch (Exception e) {
            return new ObjectMapper().createObjectNode();
        }
    }

    /**
     * Updates the capacity usage of this location.
     * 
     * @param delta the change in capacity usage (positive for increase, negative for decrease)
     * @return true if the update was successful, false if it would exceed capacity
     */
    public boolean updateCapacityUsage(int delta) {
        if (maxCapacity == null) {
            currentCapacityUsage = (currentCapacityUsage == null ? 0 : currentCapacityUsage) + delta;
            return true;
        }
        
        int newCapacity = (currentCapacityUsage == null ? 0 : currentCapacityUsage) + delta;
        if (newCapacity < 0 || newCapacity > maxCapacity) {
            return false;
        }
        
        currentCapacityUsage = newCapacity;
        return true;
    }

    @PrePersist
    protected void onCreate() {
        activationDate = LocalDateTime.now();
        lastModifiedDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        lastModifiedDate = LocalDateTime.now();
    }
}
