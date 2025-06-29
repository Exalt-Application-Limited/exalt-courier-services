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
import java.util.Set;

/**
 * Entity representing shipping restrictions for a particular country.
 * Includes restricted item categories, special documentation requirements,
 * and embargo information.
 */
@Entity
@Table(name = "country_restrictions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CountryRestriction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ISO 3166-1 alpha-2 country code
     */
    @NotBlank(message = "Country code is required")
    @Size(min = 2, max = 2, message = "Country code must be exactly 2 characters")
    @Column(nullable = false, length = 2, unique = true)
    private String countryCode;

    /**
     * Country name
     */
    @NotBlank(message = "Country name is required")
    @Column(nullable = false)
    private String countryName;

    /**
     * Whether shipping to this country is entirely restricted (embargo)
     */
    @NotNull(message = "Embargo status must be specified")
    @Column(nullable = false)
    private boolean embargoed;
    
    /**
     * Reason for embargo, if applicable
     */
    @Column
    private String embargoReason;
    
    /**
     * Date when the embargo is expected to be lifted, if known
     */
    @Column
    private LocalDateTime embargoEndDate;
    
    /**
     * List of restricted item categories that cannot be shipped to this country
     */
    @ElementCollection
    @CollectionTable(
        name = "country_restricted_categories",
        joinColumns = @JoinColumn(name = "country_restriction_id")
    )
    @Column(name = "category")
    private Set<String> restrictedCategories;
    
    /**
     * Special documentation required for shipping to this country
     */
    @ElementCollection
    @CollectionTable(
        name = "country_required_documents",
        joinColumns = @JoinColumn(name = "country_restriction_id")
    )
    @Column(name = "document_type")
    private Set<String> requiredDocuments;
    
    /**
     * Special handling requirements for shipments to this country
     */
    @Column(columnDefinition = "TEXT")
    private String specialHandlingRequirements;
    
    /**
     * Additional customs information required for this country
     */
    @Column(columnDefinition = "TEXT")
    private String customsInformation;
    
    /**
     * Whether VAT/taxes must be prepaid for this country
     */
    @Column(nullable = false)
    private boolean requiresPrepaidVAT;
    
    /**
     * Whether this country requires an EORI number for commercial shipments
     */
    @Column(nullable = false)
    private boolean requiresEORI;
    
    /**
     * Notes about shipping to this country
     */
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    /**
     * When this restriction was last updated
     */
    @Column(nullable = false)
    private LocalDateTime lastUpdated;
    
    /**
     * Who last updated this restriction
     */
    @Column
    private String lastUpdatedBy;
    
    @PrePersist
    @PreUpdate
    public void prePersist() {
        lastUpdated = LocalDateTime.now();
    }
}
