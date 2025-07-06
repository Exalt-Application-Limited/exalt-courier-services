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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents global pricing strategies for courier services.
 * This entity allows HQ administrators to define base pricing models that can be
 * inherited or overridden by regional pricing systems.
 */
@Data
@Entity
@Table(name = "global_pricing")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GlobalPricing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "pricing_code", unique = true, nullable = false)
    private String pricingCode;
    
    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @NotBlank
    @Column(name = "service_type", nullable = false)
    private String serviceType;
    
    @NotNull
    @Column(name = "base_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;
    
    @Column(name = "price_per_km", precision = 10, scale = 2)
    private BigDecimal pricePerKm;
    
    @Column(name = "price_per_kg", precision = 10, scale = 2)
    private BigDecimal pricePerKg;
    
    @Column(name = "minimum_price", precision = 10, scale = 2)
    private BigDecimal minimumPrice;
    
    @Column(name = "currency_code")
    private String currencyCode;
    
    @NotNull
    @Column(name = "allow_regional_override", nullable = false)
    private Boolean allowRegionalOverride;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "global_region_id")
    private GlobalRegion globalRegion;
    
    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
    
    @Column(name = "effective_from")
    private LocalDate effectiveFrom;
    
    @Column(name = "effective_until")
    private LocalDate effectiveUntil;
    
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
    
    /**
     * Checks if this pricing is currently effective based on its date range
     * 
     * @return true if pricing is currently effective, false otherwise
     */
    public boolean isEffective() {
        LocalDate today = LocalDate.now();
        boolean afterStartDate = effectiveFrom == null || !today.isBefore(effectiveFrom);
        boolean beforeEndDate = effectiveUntil == null || !today.isAfter(effectiveUntil);
        return isActive && afterStartDate && beforeEndDate;
    }
}
