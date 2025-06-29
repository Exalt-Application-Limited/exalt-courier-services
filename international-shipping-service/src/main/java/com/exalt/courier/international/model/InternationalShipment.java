package com.exalt.courierservices.international-shipping.$1;

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
 * Entity representing an international shipment with additional information
 * required for cross-border shipping, including customs declarations,
 * regulatory compliance, and special handling requirements.
 */
@Entity
@Table(name = "international_shipments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InternationalShipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique reference ID for the international shipment
     */
    @NotBlank(message = "Reference ID is required")
    @Column(nullable = false, unique = true)
    private String referenceId;

    /**
     * External shipment ID from the carrier
     */
    @Column
    private String externalShipmentId;
    
    /**
     * Tracking number for the shipment
     */
    @Column
    private String trackingNumber;
    
    /**
     * Origin country (ISO 3166-1 alpha-2 country code)
     */
    @NotBlank(message = "Origin country code is required")
    @Size(min = 2, max = 2, message = "Country code must be exactly 2 characters")
    @Column(nullable = false, length = 2)
    private String originCountryCode;
    
    /**
     * Destination country (ISO 3166-1 alpha-2 country code)
     */
    @NotBlank(message = "Destination country code is required")
    @Size(min = 2, max = 2, message = "Country code must be exactly 2 characters")
    @Column(nullable = false, length = 2)
    private String destinationCountryCode;
    
    /**
     * The carrier (provider) used for this shipment
     */
    @NotBlank(message = "Carrier code is required")
    @Column(nullable = false)
    private String carrierCode;
    
    /**
     * The service level used for this shipment
     */
    @NotNull(message = "Service level is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceLevel serviceLevel;
    
    /**
     * Status of the international shipment
     */
    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShipmentStatus status;
    
    /**
     * Reference to the customs declaration for this shipment
     */
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "customs_declaration_id")
    private CustomsDeclaration customsDeclaration;
    
    /**
     * Whether the shipment requires an export license
     */
    @Column(nullable = false)
    private boolean requiresExportLicense;
    
    /**
     * Export license number, if applicable
     */
    @Column
    private String exportLicenseNumber;
    
    /**
     * Whether the shipment contains items subject to export controls
     */
    @Column(nullable = false)
    private boolean containsControlledItems;
    
    /**
     * Whether the shipment requires an import permit
     */
    @Column(nullable = false)
    private boolean requiresImportPermit;
    
    /**
     * Import permit number, if applicable
     */
    @Column
    private String importPermitNumber;
    
    /**
     * Incoterms (International Commercial Terms) for the shipment
     */
    @Column
    private String incoterms;
    
    /**
     * Whether duties and taxes are prepaid
     */
    @Column(nullable = false)
    private boolean dutiesPrepaid;
    
    /**
     * Whether the shipment requires special handling
     */
    @Column(nullable = false)
    private boolean specialHandlingRequired;
    
    /**
     * Special handling instructions, if applicable
     */
    @Column(columnDefinition = "TEXT")
    private String specialHandlingInstructions;
    
    /**
     * Whether the shipment has been approved by compliance
     */
    @Column(nullable = false)
    private boolean complianceApproved;
    
    /**
     * Name of the compliance officer who approved the shipment
     */
    @Column
    private String complianceApprovedBy;
    
    /**
     * When the shipment was created
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    /**
     * When the shipment was last updated
     */
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    /**
     * When the shipment was submitted to the carrier
     */
    @Column
    private LocalDateTime submittedAt;
    
    /**
     * Estimated delivery date
     */
    @Column
    private LocalDateTime estimatedDeliveryDate;
    
    /**
     * URL to access shipping documents
     */
    @Column
    private String documentsUrl;
    
    /**
     * URL to access the shipping label
     */
    @Column
    private String labelUrl;
    
    /**
     * Error information, if any
     */
    @Column(columnDefinition = "TEXT")
    private String errorMessage;
    
    /**
     * Service levels for international shipping
     */
    public enum ServiceLevel {
        STANDARD,
        EXPRESS,
        PRIORITY,
        ECONOMY,
        FREIGHT
    }
    
    /**
     * Possible statuses for an international shipment
     */
    public enum ShipmentStatus {
        DRAFT,
        AWAITING_CUSTOMS_DETAILS,
        CUSTOMS_DETAILS_SUBMITTED,
        AWAITING_COMPLIANCE_APPROVAL,
        COMPLIANCE_REJECTED,
        READY_FOR_PROCESSING,
        PROCESSING,
        LABEL_GENERATED,
        SUBMITTED_TO_CARRIER,
        PICKED_UP,
        IN_TRANSIT,
        HELD_BY_CUSTOMS,
        CUSTOMS_CLEARED,
        OUT_FOR_DELIVERY,
        DELIVERED,
        RETURNED,
        CANCELLED,
        ERROR
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        
        if (status == null) {
            status = ShipmentStatus.DRAFT;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
