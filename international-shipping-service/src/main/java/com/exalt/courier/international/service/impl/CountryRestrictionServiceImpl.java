package com.exalt.courierservices.international-shipping.$1;

import com.exalt.courier.international.model.CountryRestriction;
import com.exalt.courier.international.repository.CountryRestrictionRepository;
import com.exalt.courier.international.service.CountryRestrictionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of the CountryRestrictionService interface.
 * Provides functionality for managing country shipping restrictions.
 */
@Service
@Slf4j
public class CountryRestrictionServiceImpl implements CountryRestrictionService {

    private final CountryRestrictionRepository countryRestrictionRepository;

    @Autowired
    public CountryRestrictionServiceImpl(CountryRestrictionRepository countryRestrictionRepository) {
        this.countryRestrictionRepository = countryRestrictionRepository;
    }

    @Override
    @Transactional
    public CountryRestriction createCountryRestriction(CountryRestriction countryRestriction) {
        log.info("Creating country restriction for: {}", countryRestriction.getCountryCode());
        
        // Check if a restriction already exists for this country
        if (countryRestrictionRepository.findByCountryCode(countryRestriction.getCountryCode()).isPresent()) {
            throw new IllegalArgumentException("Country restriction already exists for country code: " + 
                    countryRestriction.getCountryCode());
        }
        
        // Set last updated timestamp if not provided
        if (countryRestriction.getLastUpdated() == null) {
            countryRestriction.setLastUpdated(LocalDateTime.now());
        }
        
        // Ensure collections are initialized
        if (countryRestriction.getRestrictedCategories() == null) {
            countryRestriction.setRestrictedCategories(new HashSet<>());
        }
        
        if (countryRestriction.getRequiredDocuments() == null) {
            countryRestriction.setRequiredDocuments(new HashSet<>());
        }
        
        return countryRestrictionRepository.save(countryRestriction);
    }

    @Override
    @Transactional
    public CountryRestriction updateCountryRestriction(String countryCode, CountryRestriction countryRestriction) {
        log.info("Updating country restriction for: {}", countryCode);
        
        CountryRestriction existingRestriction = countryRestrictionRepository.findByCountryCode(countryCode)
                .orElseThrow(() -> new NoSuchElementException("Country restriction not found for country code: " + countryCode));
        
        // Update fields
        existingRestriction.setCountryName(countryRestriction.getCountryName());
        existingRestriction.setEmbargoed(countryRestriction.isEmbargoed());
        existingRestriction.setEmbargoReason(countryRestriction.getEmbargoReason());
        existingRestriction.setEmbargoEndDate(countryRestriction.getEmbargoEndDate());
        existingRestriction.setSpecialHandlingRequirements(countryRestriction.getSpecialHandlingRequirements());
        existingRestriction.setCustomsInformation(countryRestriction.getCustomsInformation());
        existingRestriction.setRequiresPrepaidVAT(countryRestriction.isRequiresPrepaidVAT());
        existingRestriction.setRequiresEORI(countryRestriction.isRequiresEORI());
        existingRestriction.setNotes(countryRestriction.getNotes());
        existingRestriction.setLastUpdatedBy(countryRestriction.getLastUpdatedBy());
        
        // Update restricted categories if provided
        if (countryRestriction.getRestrictedCategories() != null) {
            existingRestriction.setRestrictedCategories(countryRestriction.getRestrictedCategories());
        }
        
        // Update required documents if provided
        if (countryRestriction.getRequiredDocuments() != null) {
            existingRestriction.setRequiredDocuments(countryRestriction.getRequiredDocuments());
        }
        
        // Update last updated timestamp
        existingRestriction.setLastUpdated(LocalDateTime.now());
        
        return countryRestrictionRepository.save(existingRestriction);
    }

    @Override
    public Optional<CountryRestriction> getCountryRestrictionByCode(String countryCode) {
        log.info("Getting country restriction by code: {}", countryCode);
        return countryRestrictionRepository.findByCountryCode(countryCode);
    }

    @Override
    public List<CountryRestriction> getEmbargoedCountries() {
        log.info("Getting all embargoed countries");
        return countryRestrictionRepository.findByEmbargoedTrue();
    }

    @Override
    public List<CountryRestriction> getCountriesWithRestrictions() {
        log.info("Getting all countries with restrictions");
        return countryRestrictionRepository.findAllWithRestrictions();
    }

    @Override
    public boolean hasEmbargo(String countryCode) {
        log.info("Checking if country has embargo: {}", countryCode);
        
        Optional<CountryRestriction> restriction = countryRestrictionRepository.findByCountryCode(countryCode);
        
        if (!restriction.isPresent()) {
            // If no restriction is found, assume no embargo
            return false;
        }
        
        CountryRestriction countryRestriction = restriction.get();
        
        // Check if the embargo has an end date and if it has passed
        if (countryRestriction.isEmbargoed() && countryRestriction.getEmbargoEndDate() != null) {
            return countryRestriction.getEmbargoEndDate().isAfter(LocalDateTime.now());
        }
        
        return countryRestriction.isEmbargoed();
    }

    @Override
    public boolean isCategoryRestricted(String countryCode, String category) {
        log.info("Checking if category {} is restricted for country: {}", category, countryCode);
        
        Optional<CountryRestriction> restriction = countryRestrictionRepository.findByCountryCode(countryCode);
        
        if (!restriction.isPresent()) {
            // If no restriction is found, assume no category restriction
            return false;
        }
        
        CountryRestriction countryRestriction = restriction.get();
        
        // Check if the country has restricted categories and if the specified category is in the list
        return countryRestriction.getRestrictedCategories() != null && 
               countryRestriction.getRestrictedCategories().contains(category);
    }

    @Override
    public Set<String> getRestrictedCategories(String countryCode) {
        log.info("Getting restricted categories for country: {}", countryCode);
        
        Optional<CountryRestriction> restriction = countryRestrictionRepository.findByCountryCode(countryCode);
        
        if (!restriction.isPresent() || restriction.get().getRestrictedCategories() == null) {
            // If no restriction is found or no restricted categories, return empty set
            return new HashSet<>();
        }
        
        return new HashSet<>(restriction.get().getRestrictedCategories());
    }

    @Override
    @Transactional
    public CountryRestriction addRestrictedCategory(String countryCode, String category) {
        log.info("Adding restricted category {} for country: {}", category, countryCode);
        
        CountryRestriction restriction = countryRestrictionRepository.findByCountryCode(countryCode)
                .orElseThrow(() -> new NoSuchElementException("Country restriction not found for country code: " + countryCode));
        
        // Initialize the restricted categories set if null
        if (restriction.getRestrictedCategories() == null) {
            restriction.setRestrictedCategories(new HashSet<>());
        }
        
        // Add the category to the restricted categories
        restriction.getRestrictedCategories().add(category);
        
        // Update last updated timestamp
        restriction.setLastUpdated(LocalDateTime.now());
        
        return countryRestrictionRepository.save(restriction);
    }

    @Override
    @Transactional
    public CountryRestriction removeRestrictedCategory(String countryCode, String category) {
        log.info("Removing restricted category {} for country: {}", category, countryCode);
        
        CountryRestriction restriction = countryRestrictionRepository.findByCountryCode(countryCode)
                .orElseThrow(() -> new NoSuchElementException("Country restriction not found for country code: " + countryCode));
        
        // Remove the category from the restricted categories if it exists
        if (restriction.getRestrictedCategories() != null) {
            restriction.getRestrictedCategories().remove(category);
        }
        
        // Update last updated timestamp
        restriction.setLastUpdated(LocalDateTime.now());
        
        return countryRestrictionRepository.save(restriction);
    }

    @Override
    public Set<String> getRequiredDocuments(String countryCode) {
        log.info("Getting required documents for country: {}", countryCode);
        
        Optional<CountryRestriction> restriction = countryRestrictionRepository.findByCountryCode(countryCode);
        
        if (!restriction.isPresent() || restriction.get().getRequiredDocuments() == null) {
            // If no restriction is found or no required documents, return empty set
            return new HashSet<>();
        }
        
        return new HashSet<>(restriction.get().getRequiredDocuments());
    }

    @Override
    @Transactional
    public CountryRestriction addRequiredDocument(String countryCode, String documentType) {
        log.info("Adding required document {} for country: {}", documentType, countryCode);
        
        CountryRestriction restriction = countryRestrictionRepository.findByCountryCode(countryCode)
                .orElseThrow(() -> new NoSuchElementException("Country restriction not found for country code: " + countryCode));
        
        // Initialize the required documents set if null
        if (restriction.getRequiredDocuments() == null) {
            restriction.setRequiredDocuments(new HashSet<>());
        }
        
        // Add the document type to the required documents
        restriction.getRequiredDocuments().add(documentType);
        
        // Update last updated timestamp
        restriction.setLastUpdated(LocalDateTime.now());
        
        return countryRestrictionRepository.save(restriction);
    }

    @Override
    @Transactional
    public CountryRestriction removeRequiredDocument(String countryCode, String documentType) {
        log.info("Removing required document {} for country: {}", documentType, countryCode);
        
        CountryRestriction restriction = countryRestrictionRepository.findByCountryCode(countryCode)
                .orElseThrow(() -> new NoSuchElementException("Country restriction not found for country code: " + countryCode));
        
        // Remove the document type from the required documents if it exists
        if (restriction.getRequiredDocuments() != null) {
            restriction.getRequiredDocuments().remove(documentType);
        }
        
        // Update last updated timestamp
        restriction.setLastUpdated(LocalDateTime.now());
        
        return countryRestrictionRepository.save(restriction);
    }

    @Override
    @Transactional
    public CountryRestriction setEmbargoStatus(String countryCode, boolean embargoed, String reason, LocalDateTime endDate) {
        log.info("Setting embargo status for country: {} to {}", countryCode, embargoed);
        
        CountryRestriction restriction = countryRestrictionRepository.findByCountryCode(countryCode)
                .orElseThrow(() -> new NoSuchElementException("Country restriction not found for country code: " + countryCode));
        
        // Update embargo status
        restriction.setEmbargoed(embargoed);
        restriction.setEmbargoReason(reason);
        restriction.setEmbargoEndDate(endDate);
        
        // Update last updated timestamp
        restriction.setLastUpdated(LocalDateTime.now());
        
        return countryRestrictionRepository.save(restriction);
    }

    @Override
    public List<CountryRestriction> getCountriesRequiringPrepaidVAT() {
        log.info("Getting countries requiring prepaid VAT");
        return countryRestrictionRepository.findByRequiresPrepaidVATTrue();
    }

    @Override
    public List<CountryRestriction> getCountriesRequiringEORI() {
        log.info("Getting countries requiring EORI number");
        return countryRestrictionRepository.findByRequiresEORITrue();
    }

    @Override
    @Transactional
    public boolean deleteCountryRestriction(String countryCode) {
        log.info("Deleting country restriction: {}", countryCode);
        
        Optional<CountryRestriction> restriction = countryRestrictionRepository.findByCountryCode(countryCode);
        
        if (!restriction.isPresent()) {
            return false;
        }
        
        countryRestrictionRepository.delete(restriction.get());
        return true;
    }

    @Override
    public Map<Boolean, List<String>> checkShipmentEligibility(String countryCode, List<String> categories) {
        log.info("Checking shipment eligibility for country: {}", countryCode);
        
        Map<Boolean, List<String>> result = new HashMap<>();
        List<String> messages = new ArrayList<>();
        
        // Check if the country has an embargo
        if (hasEmbargo(countryCode)) {
            messages.add("Destination country is under embargo");
            result.put(false, messages);
            return result;
        }
        
        // Check if any of the categories are restricted
        List<String> restrictedCategoriesFound = new ArrayList<>();
        for (String category : categories) {
            if (isCategoryRestricted(countryCode, category)) {
                restrictedCategoriesFound.add(category);
            }
        }
        
        if (!restrictedCategoriesFound.isEmpty()) {
            messages.add("The following categories are restricted for the destination country: " + 
                    String.join(", ", restrictedCategoriesFound));
            result.put(false, messages);
            return result;
        }
        
        // Get required documents for the country
        Set<String> requiredDocs = getRequiredDocuments(countryCode);
        if (!requiredDocs.isEmpty()) {
            messages.add("The following documents are required for shipment to this country: " + 
                    String.join(", ", requiredDocs));
        }
        
        // Check if the country requires prepaid VAT
        Optional<CountryRestriction> restriction = countryRestrictionRepository.findByCountryCode(countryCode);
        if (restriction.isPresent()) {
            if (restriction.get().isRequiresPrepaidVAT()) {
                messages.add("This country requires prepaid VAT/taxes");
            }
            
            if (restriction.get().isRequiresEORI()) {
                messages.add("This country requires an EORI number for commercial shipments");
            }
            
            if (restriction.get().getSpecialHandlingRequirements() != null && 
                    !restriction.get().getSpecialHandlingRequirements().isEmpty()) {
                messages.add("Special handling requirements: " + restriction.get().getSpecialHandlingRequirements());
            }
        }
        
        // If we get here, the shipment is eligible
        result.put(true, messages);
        return result;
    }
}
