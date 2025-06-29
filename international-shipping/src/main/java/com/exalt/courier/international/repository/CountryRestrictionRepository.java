package com.exalt.courier.international.repository;

import com.exalt.courier.international.model.CountryRestriction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for managing {@link CountryRestriction} entities.
 */
@Repository
public interface CountryRestrictionRepository extends JpaRepository<CountryRestriction, String> {

    /**
     * Find a country restriction by its country code
     * @param countryCode The ISO 3166-1 alpha-2 country code
     * @return The country restriction if found
     */
    Optional<CountryRestriction> findByCountryCode(String countryCode);
    
    /**
     * Find a country restriction by its country name
     * @param countryName The country name
     * @return The country restriction if found
     */
    Optional<CountryRestriction> findByCountryName(String countryName);
    
    /**
     * Find all embargoed countries
     * @return List of embargoed countries
     */
    List<CountryRestriction> findByEmbargoedTrue();
    
    /**
     * Find countries that require prepaid VAT
     * @return List of countries requiring prepaid VAT
     */
    List<CountryRestriction> findByRequiresPrepaidVATTrue();
    
    /**
     * Find countries that require EORI number
     * @return List of countries requiring EORI number
     */
    List<CountryRestriction> findByRequiresEORITrue();
    
    /**
     * Find countries with restrictions on specific item categories
     * @param category The restricted category
     * @return List of countries with restrictions on the specified category
     */
    @Query("SELECT cr FROM CountryRestriction cr JOIN cr.restrictedCategories cats WHERE :category MEMBER OF cats")
    List<CountryRestriction> findByRestrictedCategory(@Param("category") String category);
    
    /**
     * Find countries that require specific documentation
     * @param documentType The required document type
     * @return List of countries requiring the specified document
     */
    @Query("SELECT cr FROM CountryRestriction cr JOIN cr.requiredDocuments docs WHERE :documentType MEMBER OF docs")
    List<CountryRestriction> findByRequiredDocument(@Param("documentType") String documentType);
    
    /**
     * Find all countries with any type of restriction
     * @return List of countries with restrictions
     */
    @Query("SELECT cr FROM CountryRestriction cr WHERE cr.embargoed = true OR SIZE(cr.restrictedCategories) > 0")
    List<CountryRestriction> findAllWithRestrictions();
    
    /**
     * Find countries with special handling requirements
     * @return List of countries with special handling requirements
     */
    @Query("SELECT cr FROM CountryRestriction cr WHERE cr.specialHandlingRequirements IS NOT NULL AND cr.specialHandlingRequirements <> ''")
    List<CountryRestriction> findWithSpecialHandlingRequirements();
    
    /**
     * Find countries with information updated after a certain date
     * @param lastUpdated The cutoff date
     * @return List of countries with information updated after the cutoff
     */
    List<CountryRestriction> findByLastUpdatedAfter(java.time.LocalDateTime lastUpdated);
}
