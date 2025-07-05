import java.util.Optional;
package com.gogidix.integration.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * Represents a package to be shipped as part of a shipment.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackageInfo {
    
    private String packageId;
    
    @NotNull(message = "Package dimensions are required")
    @Valid
    private Dimensions dimensions;
    
    @NotNull(message = "Package weight is required")
    @Valid
    private Weight weight;
    
    private String packaging; // Optional packaging type
    
    private Boolean hazardous;
    
    private Boolean fragile;
    
    private Boolean electronicsInsurance;
    
    private Double declaredValue;
    
    private String description;
    
    @Valid
    private List<PackageItem> items;
    
    private Map<String, String> customFields;
    
    /**
     * Represents the physical dimensions of a package.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Dimensions {
        @NotNull(message = "Length is required")
        @Min(value = 0, message = "Length must be positive")
        private Double length;
        
        @NotNull(message = "Width is required")
        @Min(value = 0, message = "Width must be positive")
        private Double width;
        
        @NotNull(message = "Height is required")
        @Min(value = 0, message = "Height must be positive")
        private Double height;
        
        @NotNull(message = "Unit of measurement is required")
        private DimensionUnit unit;
        
        public enum DimensionUnit {
            CM, // Centimeters
            IN  // Inches
        }
    }
    
    /**
     * Represents the weight of a package.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Weight {
        @NotNull(message = "Weight value is required")
        @Min(value = 0, message = "Weight must be positive")
        private Double value;
        
        @NotNull(message = "Weight unit is required")
        private WeightUnit unit;
        
        public enum WeightUnit {
            KG, // Kilograms
            LB  // Pounds
        }
    }
} 

