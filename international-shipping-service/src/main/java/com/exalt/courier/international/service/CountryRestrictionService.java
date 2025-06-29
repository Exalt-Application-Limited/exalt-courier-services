package com.exalt.courierservices.international-shipping.$1;

import com.exalt.courier.international.model.CountryRestriction;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Service interface for managing country shipping restrictions.
 */
public interface CountryRestrictionService {

    /**
     * Create a new country restriction
     * @param countryRestriction The country restriction to create
     * @return The created country restriction with ID assigned
     */
    CountryRestriction createCountryRestriction(CountryRestriction countryRestriction);
    
    /**
     * Update an existing country restriction
     * @param countryCode The country code of the restriction to update
     * @param countryRestriction The updated country restriction
     * @return The updated country restriction
     */
    CountryRestriction updateCountryRestriction(String countryCode, CountryRestriction countryRestriction);
    
    /**
     * Get a country restriction by its country code
     * @param countryCode The country code
     * @return The country restriction if found
     */
    Optional<CountryRestriction> getCountryRestrictionByCode(String countryCode);
    
    /**
     * Get all embargoed countries
     * @return List of embargoed countries
     */
    List<CountryRestriction> getEmbargoedCountries();
    
    /**
     * Get all countries with any shipping restrictions
     * @return List of countries with restrictions
     */
    List<CountryRestriction> getCountriesWithRestrictions();
    
    /**
     * Check if a country has an embargo
     * @param countryCode The country code to check
     * @return true if the country has an embargo, false otherwise
     */
    boolean hasEmbargo(String countryCode);
    
    /**
     * Check if a specific product category is restricted for a country
     * @param countryCode The country code to check
     * @param category The product category to check
     * @return true if the category is restricted, false otherwise
     */
    boolean isCategoryRestricted(String countryCode, String category);
    
    /**
     * Get restricted categories for a country
     * @param countryCode The country code
     * @return Set of restricted categories for the country
     */
    Set<String> getRestrictedCategories(String countryCode);
    
    /**
     * Add a restricted category to a country
     * @param countryCode The country code
     * @param category The category to add as restricted
     * @return The updated country restriction
     */
    CountryRestriction addRestrictedCategory(String countryCode, String category);
    
    /**
     * Remove a restricted category from a country
     * @param countryCode The country code
     * @param category The category to remove from restrictions
     * @return The updated country restriction
     */
    CountryRestriction removeRestrictedCategory(String countryCode, String category);
    
    /**
     * Get required documents for shipping to a country
     * @param countryCode The country code
     * @return Set of required document types
     */
    Set<String> getRequiredDocuments(String countryCode);
    
    /**
     * Add a required document to a country
     * @param countryCode The country code
     * @param documentType The document type to add as required
     * @return The updated country restriction
     */
    CountryRestriction addRequiredDocument(String countryCode, String documentType);
    
    /**
     * Remove a required document from a country
     * @param countryCode The country code
     * @param documentType The document type to remove from requirements
     * @return The updated country restriction
     */
    CountryRestriction removeRequiredDocument(String countryCode, String documentType);
    
    /**
     * Set embargo status for a country
     * @param countryCode The country code
     * @param embargoed Whether the country should be embargoed
     * @param reason The reason for the embargo (if embargoed is true)
     * @param endDate The expected end date of the embargo (optional)
     * @return The updated country restriction
     */
    CountryRestriction setEmbargoStatus(String countryCode, boolean embargoed, String reason, java.time.LocalDateTime endDate);
    
    /**
     * Get countries that require prepaid VAT
     * @return List of countries requiring prepaid VAT
     */
    List<CountryRestriction> getCountriesRequiringPrepaidVAT();
    
    /**
     * Get countries that require an EORI number
     * @return List of countries requiring an EORI number
     */
    List<CountryRestriction> getCountriesRequiringEORI();
    
    /**
     * Delete a country restriction
     * @param countryCode The country code of the restriction to delete
     * @return true if successfully deleted, false otherwise
     */
    boolean deleteCountryRestriction(String countryCode);
    
    /**
     * Check if a shipment is eligible for a specific country based on its restrictions
     * @param countryCode The destination country code
     * @param categories The categories of items in the shipment
     * @return Map of eligibility result and any restriction messages
     */
    java.util.Map<Boolean, List<String>> checkShipmentEligibility(String countryCode, List<String> categories);
}
