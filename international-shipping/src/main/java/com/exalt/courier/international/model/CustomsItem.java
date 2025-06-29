package com.exalt.courier.international.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing an item in a customs declaration.
 * Contains details about the item, including description, quantity, value, and HS code.
 */
@Entity
@Table(name = "customs_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomsItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Detailed description of the item
     */
    @NotBlank(message = "Item description is required")
    @Column(nullable = false)
    private String description;

    /**
     * Quantity of the item
     */
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Column(nullable = false)
    private Integer quantity;
    
    /**
     * Harmonized System (HS) tariff code for the item
     * International standard for classifying traded products
     */
    @Column
    @Size(min = 6, message = "HS code must be at least 6 digits")
    private String hsCode;
    
    /**
     * Country of origin for the specific item (ISO 3166-1 alpha-2 country code)
     */
    @NotBlank(message = "Origin country code is required")
    @Size(min = 2, max = 2, message = "Country code must be exactly 2 characters")
    @Column(nullable = false, length = 2)
    private String originCountryCode;
    
    /**
     * Value per unit of the item in the declared currency
     */
    @NotNull(message = "Unit value is required")
    @Min(value = 0, message = "Unit value must be non-negative")
    @Column(nullable = false)
    private Double unitValue;
    
    /**
     * Net weight of each item (without packaging) in the specified weight unit
     */
    @NotNull(message = "Net weight is required")
    @Min(value = 0, message = "Net weight must be non-negative")
    @Column(nullable = false)
    private Double netWeight;
    
    /**
     * Unit of measurement for weight (e.g., KG, LB)
     */
    @NotBlank(message = "Weight unit is required")
    @Column(nullable = false)
    private String weightUnit;
    
    /**
     * SKU (Stock Keeping Unit) or product identifier
     */
    @Column
    private String sku;
    
    /**
     * UPC or EAN barcode for the item, if available
     */
    @Column
    private String barcode;
    
    /**
     * URL to an image of the item, if available
     */
    @Column
    private String imageUrl;
    
    /**
     * Whether the item is subject to any export controls
     */
    @Column(nullable = false)
    private boolean exportControlled;
    
    /**
     * Percentage of duty applicable to this item, if known
     */
    @Column
    private Double dutyPercentage;
    
    /**
     * Additional information about the item relevant for customs
     */
    @Column(columnDefinition = "TEXT")
    private String additionalInformation;
    
    /**
     * Calculate the total value of this item (unit value × quantity)
     * @return The total value
     */
    @Transient
    public Double getTotalValue() {
        return unitValue * quantity;
    }
    
    /**
     * Calculate the total weight of this item (net weight × quantity)
     * @return The total weight
     */
    @Transient
    public Double getTotalWeight() {
        return netWeight * quantity;
    }
}
