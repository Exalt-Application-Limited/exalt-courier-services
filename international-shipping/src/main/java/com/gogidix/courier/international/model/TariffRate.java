package com.gogidix.courier.international.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing tariff rates for specific product categories and destination countries.
 * Used for estimating duties and taxes for international shipments.
 */
@Entity
@Table(name = "tariff_rates", 
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"destination_country_code", "hs_code"}
    )
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TariffRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Destination country (ISO 3166-1 alpha-2 country code)
     */
    @NotBlank(message = "Destination country code is required")
    @Size(min = 2, max = 2, message = "Country code must be exactly 2 characters")
    @Column(name = "destination_country_code", nullable = false, length = 2)
    private String destinationCountryCode;

    /**
     * Harmonized System (HS) tariff code for the product category
     */
    @NotBlank(message = "HS code is required")
    @Column(name = "hs_code", nullable = false)
    private String hsCode;
    
    /**
     * Description of the product category
     */
    @NotBlank(message = "Description is required")
    @Column(nullable = false)
    private String description;
    
    /**
     * Rate of duty as a percentage (e.g., 5.0 for 5%)
     */
    @NotNull(message = "Duty rate is required")
    @Column(nullable = false)
    private Double dutyRate;
    
    /**
     * Additional taxes as a percentage (e.g., VAT/GST)
     */
    @Column
    private Double taxRate;
    
    /**
     * Type of tax applied (e.g., VAT, GST)
     */
    @Column
    private String taxType;
    
    /**
     * Minimum threshold value for duty-free imports in the destination currency
     */
    @Column
    private Double dutyFreeThreshold;
    
    /**
     * Currency code for the duty-free threshold (ISO 4217)
     */
    @Column(length = 3)
    private String thresholdCurrencyCode;
    
    /**
     * Whether certain goods in this category are restricted
     */
    @Column(nullable = false)
    private boolean restricted;
    
    /**
     * Notes about restrictions or special requirements
     */
    @Column(columnDefinition = "TEXT")
    private String restrictionNotes;
    
    /**
     * URL to official tariff information
     */
    @Column
    private String officialReferenceUrl;
    
    /**
     * When the tariff rate was last updated
     */
    @Column(nullable = false)
    private LocalDateTime lastUpdated;
    
    /**
     * Valid from date for this tariff rate
     */
    @Column(nullable = false)
    private LocalDateTime validFrom;
    
    /**
     * Valid until date for this tariff rate, null if no expiration
     */
    @Column
    private LocalDateTime validUntil;
    
    /**
     * Source of the tariff data
     */
    @Column
    private String dataSource;
    
    @PrePersist
    @PreUpdate
    public void prePersist() {
        lastUpdated = LocalDateTime.now();
        if (validFrom == null) {
            validFrom = LocalDateTime.now();
        }
    }
    
    /**
     * Calculate the duty amount for a given value
     * @param declaredValue The declared value of the goods
     * @return The calculated duty amount
     */
    @Transient
    public Double calculateDuty(Double declaredValue) {
        if (declaredValue == null || dutyRate == null) {
            return 0.0;
        }
        
        // Check if the value is below the duty-free threshold
        if (dutyFreeThreshold != null && declaredValue <= dutyFreeThreshold) {
            return 0.0;
        }
        
        return declaredValue * (dutyRate / 100.0);
    }
    
    /**
     * Calculate the tax amount for a given value
     * @param declaredValue The declared value of the goods
     * @param dutyAmount The duty amount (some taxes apply to value + duty)
     * @return The calculated tax amount
     */
    @Transient
    public Double calculateTax(Double declaredValue, Double dutyAmount) {
        if (declaredValue == null || taxRate == null) {
            return 0.0;
        }
        
        // For some tax types (e.g., VAT), tax applies to value + duty
        Double taxableAmount = declaredValue;
        if ("VAT".equalsIgnoreCase(taxType)) {
            taxableAmount += (dutyAmount != null ? dutyAmount : 0.0);
        }
        
        return taxableAmount * (taxRate / 100.0);
    }
    
    /**
     * Calculate the total import charges (duty + tax) for a given value
     * @param declaredValue The declared value of the goods
     * @return The total import charges
     */
    @Transient
    public Double calculateTotalCharges(Double declaredValue) {
        Double dutyAmount = calculateDuty(declaredValue);
        Double taxAmount = calculateTax(declaredValue, dutyAmount);
        
        return dutyAmount + taxAmount;
    }
}
