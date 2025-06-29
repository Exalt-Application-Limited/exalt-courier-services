package com.exalt.courier.international.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Entity representing a customs declaration form for an international shipment.
 * Contains information about the shipment contents, value, and other details
 * required for customs clearance.
 */
@Entity
@Table(name = "customs_declarations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomsDeclaration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique reference ID for the customs declaration
     */
    @NotBlank(message = "Reference ID is required")
    @Column(nullable = false, unique = true)
    private String referenceId;

    /**
     * Reference to the shipment this customs declaration is for
     */
    @NotBlank(message = "Shipment ID is required")
    @Column(nullable = false)
    private String shipmentId;
    
    /**
     * Country of origin for the shipment (ISO 3166-1 alpha-2 country code)
     */
    @NotBlank(message = "Origin country code is required")
    @Size(min = 2, max = 2, message = "Country code must be exactly 2 characters")
    @Column(nullable = false, length = 2)
    private String originCountryCode;
    
    /**
     * Destination country for the shipment (ISO 3166-1 alpha-2 country code)
     */
    @NotBlank(message = "Destination country code is required")
    @Size(min = 2, max = 2, message = "Country code must be exactly 2 characters")
    @Column(nullable = false, length = 2)
    private String destinationCountryCode;
    
    /**
     * Type of customs declaration (e.g., CN22, CN23, Commercial Invoice)
     */
    @NotNull(message = "Declaration type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeclarationType declarationType;
    
    /**
     * Purpose of the shipment for customs (e.g., GIFT, SALE, SAMPLE, RETURN, REPAIR)
     */
    @NotNull(message = "Shipment purpose is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShipmentPurpose shipmentPurpose;
    
    /**
     * Total declared value in the specified currency
     */
    @NotNull(message = "Declared value is required")
    @Column(nullable = false)
    private Double declaredValue;
    
    /**
     * Currency code for the declared value (ISO 4217)
     */
    @NotBlank(message = "Currency code is required")
    @Size(min = 3, max = 3, message = "Currency code must be exactly 3 characters")
    @Column(nullable = false, length = 3)
    private String currencyCode;
    
    /**
     * Items included in the customs declaration
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "customs_declaration_id")
    private List<CustomsItem> items = new ArrayList<>();
    
    /**
     * EORI number (Economic Operator Registration and Identification)
     * Required for commercial shipments to/from the EU
     */
    @Column
    private String eoriNumber;
    
    /**
     * Incoterms (International Commercial Terms) for the shipment
     * e.g., EXW (Ex Works), FOB (Free on Board), CIF (Cost, Insurance, Freight)
     */
    @Column
    private String incoterms;
    
    /**
     * Reason for export (required for some commercial declarations)
     */
    @Column
    private String reasonForExport;
    
    /**
     * Whether the shipment contains items for personal use or commercial purposes
     */
    @Column(nullable = false)
    private boolean commercial;
    
    /**
     * VAT/Tax ID number, if applicable
     */
    @Column
    private String taxId;
    
    /**
     * License numbers for regulated commodities, if applicable
     */
    @ElementCollection
    @CollectionTable(
        name = "customs_declaration_licenses",
        joinColumns = @JoinColumn(name = "customs_declaration_id")
    )
    @Column(name = "license_number")
    private Set<String> licenses = new HashSet<>();
    
    /**
     * Additional remarks for customs
     */
    @Column(columnDefinition = "TEXT")
    private String remarks;
    
    /**
     * Digital signature of the declarant
     */
    @Column(columnDefinition = "TEXT")
    private String declarantSignature;
    
    /**
     * When the customs declaration was created
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    /**
     * When the customs declaration was last updated
     */
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    /**
     * Status of the customs declaration
     */
    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomsStatus status;
    
    /**
     * Types of customs declarations
     */
    public enum DeclarationType {
        CN22,           // For lower-value items
        CN23,           // For higher-value items
        COMMERCIAL_INVOICE,
        PROFORMA_INVOICE,
        ELECTRONIC_EXPORT_INFORMATION,
        CERTIFICATE_OF_ORIGIN
    }
    
    /**
     * Purposes of shipment for customs
     */
    public enum ShipmentPurpose {
        GIFT,
        SALE,
        SAMPLE,
        RETURN,
        REPAIR,
        PERSONAL_EFFECTS,
        DOCUMENTS,
        OTHER
    }
    
    /**
     * Status of customs declarations
     */
    public enum CustomsStatus {
        DRAFT,
        SUBMITTED,
        APPROVED,
        REJECTED,
        PENDING_INFORMATION,
        CLEARED,
        HELD_BY_CUSTOMS
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Calculate the total value of all items in the declaration
     * @return The total value
     */
    @Transient
    public Double calculateTotalValue() {
        return items.stream()
                .mapToDouble(item -> item.getUnitValue() * item.getQuantity())
                .sum();
    }
    
    /**
     * Calculate the total weight of all items in the declaration
     * @return The total weight in the declared weight unit
     */
    @Transient
    public Double calculateTotalWeight() {
        return items.stream()
                .mapToDouble(item -> item.getNetWeight() * item.getQuantity())
                .sum();
    }
}
