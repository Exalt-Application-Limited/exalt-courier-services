package com.exalt.courier.international.controller;

import com.exalt.courier.international.model.CountryRestriction;
import com.exalt.courier.international.service.CountryRestrictionService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * REST controller for managing country shipping restrictions.
 */
@RestController
@RequestMapping("/api/international/restrictions")
@Slf4j
public class CountryRestrictionController {

    private final CountryRestrictionService restrictionService;

    @Autowired
    public CountryRestrictionController(CountryRestrictionService restrictionService) {
        this.restrictionService = restrictionService;
    }

    /**
     * Create a new country restriction
     * 
     * @param restriction The country restriction to create
     * @return The created country restriction
     */
    @PostMapping
    public ResponseEntity<CountryRestriction> createCountryRestriction(@Valid @RequestBody CountryRestriction restriction) {
        log.info("REST request to create country restriction for: {}", restriction.getCountryCode());
        CountryRestriction result = restrictionService.createCountryRestriction(restriction);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * Update an existing country restriction
     * 
     * @param countryCode The country code of the restriction to update
     * @param restriction The updated country restriction
     * @return The updated country restriction
     */
    @PutMapping("/{countryCode}")
    public ResponseEntity<CountryRestriction> updateCountryRestriction(
            @PathVariable String countryCode,
            @Valid @RequestBody CountryRestriction restriction) {
        log.info("REST request to update country restriction for: {}", countryCode);
        CountryRestriction result = restrictionService.updateCountryRestriction(countryCode, restriction);
        return ResponseEntity.ok(result);
    }

    /**
     * Get a country restriction by its country code
     * 
     * @param countryCode The country code
     * @return The country restriction
     */
    @GetMapping("/{countryCode}")
    public ResponseEntity<CountryRestriction> getCountryRestriction(@PathVariable String countryCode) {
        log.info("REST request to get country restriction for: {}", countryCode);
        return restrictionService.getCountryRestrictionByCode(countryCode)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Country restriction not found"));
    }

    /**
     * Get all embargoed countries
     * 
     * @return List of embargoed countries
     */
    @GetMapping("/embargoed")
    public ResponseEntity<List<CountryRestriction>> getEmbargoedCountries() {
        log.info("REST request to get all embargoed countries");
        List<CountryRestriction> embargoedCountries = restrictionService.getEmbargoedCountries();
        return ResponseEntity.ok(embargoedCountries);
    }

    /**
     * Get all countries with any shipping restrictions
     * 
     * @return List of countries with restrictions
     */
    @GetMapping("/with-restrictions")
    public ResponseEntity<List<CountryRestriction>> getCountriesWithRestrictions() {
        log.info("REST request to get all countries with shipping restrictions");
        List<CountryRestriction> restrictedCountries = restrictionService.getCountriesWithRestrictions();
        return ResponseEntity.ok(restrictedCountries);
    }

    /**
     * Check if a country has an embargo
     * 
     * @param countryCode The country code to check
     * @return true if the country has an embargo, false otherwise
     */
    @GetMapping("/{countryCode}/has-embargo")
    public ResponseEntity<Boolean> hasEmbargo(@PathVariable String countryCode) {
        log.info("REST request to check if country has embargo: {}", countryCode);
        boolean hasEmbargo = restrictionService.hasEmbargo(countryCode);
        return ResponseEntity.ok(hasEmbargo);
    }

    /**
     * Check if a specific product category is restricted for a country
     * 
     * @param countryCode The country code to check
     * @param category The product category to check
     * @return true if the category is restricted, false otherwise
     */
    @GetMapping("/{countryCode}/is-category-restricted")
    public ResponseEntity<Boolean> isCategoryRestricted(
            @PathVariable String countryCode,
            @RequestParam String category) {
        log.info("REST request to check if category {} is restricted for country: {}", category, countryCode);
        boolean isRestricted = restrictionService.isCategoryRestricted(countryCode, category);
        return ResponseEntity.ok(isRestricted);
    }

    /**
     * Get restricted categories for a country
     * 
     * @param countryCode The country code
     * @return Set of restricted categories for the country
     */
    @GetMapping("/{countryCode}/restricted-categories")
    public ResponseEntity<Set<String>> getRestrictedCategories(@PathVariable String countryCode) {
        log.info("REST request to get restricted categories for country: {}", countryCode);
        Set<String> categories = restrictionService.getRestrictedCategories(countryCode);
        return ResponseEntity.ok(categories);
    }

    /**
     * Add a restricted category to a country
     * 
     * @param countryCode The country code
     * @param category The category to add as restricted
     * @return The updated country restriction
     */
    @PostMapping("/{countryCode}/restricted-categories")
    public ResponseEntity<CountryRestriction> addRestrictedCategory(
            @PathVariable String countryCode,
            @RequestParam String category) {
        log.info("REST request to add restricted category {} for country: {}", category, countryCode);
        CountryRestriction result = restrictionService.addRestrictedCategory(countryCode, category);
        return ResponseEntity.ok(result);
    }

    /**
     * Remove a restricted category from a country
     * 
     * @param countryCode The country code
     * @param category The category to remove from restrictions
     * @return The updated country restriction
     */
    @DeleteMapping("/{countryCode}/restricted-categories")
    public ResponseEntity<CountryRestriction> removeRestrictedCategory(
            @PathVariable String countryCode,
            @RequestParam String category) {
        log.info("REST request to remove restricted category {} for country: {}", category, countryCode);
        CountryRestriction result = restrictionService.removeRestrictedCategory(countryCode, category);
        return ResponseEntity.ok(result);
    }

    /**
     * Get required documents for shipping to a country
     * 
     * @param countryCode The country code
     * @return Set of required document types
     */
    @GetMapping("/{countryCode}/required-documents")
    public ResponseEntity<Set<String>> getRequiredDocuments(@PathVariable String countryCode) {
        log.info("REST request to get required documents for country: {}", countryCode);
        Set<String> documents = restrictionService.getRequiredDocuments(countryCode);
        return ResponseEntity.ok(documents);
    }

    /**
     * Add a required document to a country
     * 
     * @param countryCode The country code
     * @param documentType The document type to add as required
     * @return The updated country restriction
     */
    @PostMapping("/{countryCode}/required-documents")
    public ResponseEntity<CountryRestriction> addRequiredDocument(
            @PathVariable String countryCode,
            @RequestParam String documentType) {
        log.info("REST request to add required document {} for country: {}", documentType, countryCode);
        CountryRestriction result = restrictionService.addRequiredDocument(countryCode, documentType);
        return ResponseEntity.ok(result);
    }

    /**
     * Remove a required document from a country
     * 
     * @param countryCode The country code
     * @param documentType The document type to remove from requirements
     * @return The updated country restriction
     */
    @DeleteMapping("/{countryCode}/required-documents")
    public ResponseEntity<CountryRestriction> removeRequiredDocument(
            @PathVariable String countryCode,
            @RequestParam String documentType) {
        log.info("REST request to remove required document {} for country: {}", documentType, countryCode);
        CountryRestriction result = restrictionService.removeRequiredDocument(countryCode, documentType);
        return ResponseEntity.ok(result);
    }

    /**
     * Set embargo status for a country
     * 
     * @param countryCode The country code
     * @param embargoInfo The embargo information
     * @return The updated country restriction
     */
    @PostMapping("/{countryCode}/embargo-status")
    public ResponseEntity<CountryRestriction> setEmbargoStatus(
            @PathVariable String countryCode,
            @RequestBody Map<String, Object> embargoInfo) {
        log.info("REST request to set embargo status for country: {}", countryCode);
        
        boolean embargoed = (boolean) embargoInfo.getOrDefault("embargoed", false);
        String reason = (String) embargoInfo.getOrDefault("reason", "");
        
        LocalDateTime endDate = null;
        if (embargoInfo.containsKey("endDate")) {
            String endDateStr = (String) embargoInfo.get("endDate");
            endDate = LocalDateTime.parse(endDateStr);
        }
        
        CountryRestriction result = restrictionService.setEmbargoStatus(countryCode, embargoed, reason, endDate);
        return ResponseEntity.ok(result);
    }

    /**
     * Get countries that require prepaid VAT
     * 
     * @return List of countries requiring prepaid VAT
     */
    @GetMapping("/require-prepaid-vat")
    public ResponseEntity<List<CountryRestriction>> getCountriesRequiringPrepaidVAT() {
        log.info("REST request to get countries requiring prepaid VAT");
        List<CountryRestriction> countries = restrictionService.getCountriesRequiringPrepaidVAT();
        return ResponseEntity.ok(countries);
    }

    /**
     * Get countries that require an EORI number
     * 
     * @return List of countries requiring an EORI number
     */
    @GetMapping("/require-eori")
    public ResponseEntity<List<CountryRestriction>> getCountriesRequiringEORI() {
        log.info("REST request to get countries requiring EORI number");
        List<CountryRestriction> countries = restrictionService.getCountriesRequiringEORI();
        return ResponseEntity.ok(countries);
    }

    /**
     * Check if a shipment is eligible for a specific country based on its restrictions
     * 
     * @param countryCode The destination country code
     * @param categories The categories of items in the shipment
     * @return Map of eligibility result and any restriction messages
     */
    @GetMapping("/{countryCode}/check-eligibility")
    public ResponseEntity<Map<Boolean, List<String>>> checkShipmentEligibility(
            @PathVariable String countryCode,
            @RequestParam List<String> categories) {
        log.info("REST request to check shipment eligibility for country: {}", countryCode);
        Map<Boolean, List<String>> eligibility = restrictionService.checkShipmentEligibility(countryCode, categories);
        return ResponseEntity.ok(eligibility);
    }

    /**
     * Delete a country restriction
     * 
     * @param countryCode The country code of the restriction to delete
     * @return Status message
     */
    @DeleteMapping("/{countryCode}")
    public ResponseEntity<Void> deleteCountryRestriction(@PathVariable String countryCode) {
        log.info("REST request to delete country restriction: {}", countryCode);
        boolean result = restrictionService.deleteCountryRestriction(countryCode);
        return result ? ResponseEntity.noContent().build() : 
                ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
