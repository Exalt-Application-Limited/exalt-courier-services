package com.gogidix.integration.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Represents an individual item within a package.
 * Used for international shipping where content declaration is required.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackageItem {
    
    @NotBlank(message = "Item description is required")
    private String description;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
    
    @NotNull(message = "Unit value is required")
    @Min(value = 0, message = "Unit value must be non-negative")
    private Double unitValue;
    
    private String hsTariffNumber; // Harmonized System tariff code for international shipments
    
    private String originCountryCode; // ISO 2-letter country code
    
    private String sku; // Stock keeping unit
    
    private String partNumber; // Manufacturer part number
    
    @Min(value = 0, message = "Weight must be non-negative")
    private Double weight;
    
    private PackageInfo.Weight.WeightUnit weightUnit;
    
    @Min(value = 0, message = "Total value must be non-negative")
    private Double totalValue; // Quantity * unitValue, calculated field
    
    /**
     * Calculate the total value of the item.
     *
     * @return the total value
     */
    public Double calculateTotalValue() {
        if (quantity != null && unitValue != null) {
            return quantity * unitValue;
        }
        return totalValue != null ? totalValue : 0.0;
    }
} 
