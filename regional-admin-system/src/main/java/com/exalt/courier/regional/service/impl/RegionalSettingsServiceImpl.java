package com.exalt.courier.regional.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.socialecommerceecosystem.regional.model.RegionalSettings;
import com.socialecommerceecosystem.regional.repository.RegionalSettingsRepository;
import com.socialecommerceecosystem.regional.service.RegionalSettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of the RegionalSettingsService interface.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RegionalSettingsServiceImpl implements RegionalSettingsService {

    private final RegionalSettingsRepository regionalSettingsRepository;
    private final ObjectMapper objectMapper;
    
    @Override
    public List<RegionalSettings> getAllRegionalSettings() {
        log.debug("Retrieving all regional settings");
        List<RegionalSettings> settings = regionalSettingsRepository.findAll();
        settings.forEach(this::processAdditionalSettings);
        return settings;
    }

    @Override
    public Optional<RegionalSettings> getRegionalSettingsById(Long id) {
        log.debug("Retrieving regional settings with ID: {}", id);
        Optional<RegionalSettings> settings = regionalSettingsRepository.findById(id);
        settings.ifPresent(this::processAdditionalSettings);
        return settings;
    }

    @Override
    public Optional<RegionalSettings> getRegionalSettingsByCode(String regionCode) {
        log.debug("Retrieving regional settings with code: {}", regionCode);
        Optional<RegionalSettings> settings = regionalSettingsRepository.findByRegionCode(regionCode);
        settings.ifPresent(this::processAdditionalSettings);
        return settings;
    }

    @Override
    public List<RegionalSettings> getActiveRegionalSettings() {
        log.debug("Retrieving all active regional settings");
        List<RegionalSettings> settings = regionalSettingsRepository.findByIsActiveTrue();
        settings.forEach(this::processAdditionalSettings);
        return settings;
    }

    @Override
    public List<RegionalSettings> getRegionalSettingsByGlobalRegionId(Long globalRegionId) {
        log.debug("Retrieving regional settings for global region ID: {}", globalRegionId);
        List<RegionalSettings> settings = regionalSettingsRepository.findByGlobalRegionId(globalRegionId);
        settings.forEach(this::processAdditionalSettings);
        return settings;
    }

    @Override
    public RegionalSettings createRegionalSettings(RegionalSettings regionalSettings) {
        log.debug("Creating new regional settings: {}", regionalSettings);
        
        if (regionalSettings.getRegionCode() != null && 
            regionalSettingsRepository.existsByRegionCode(regionalSettings.getRegionCode())) {
            throw new IllegalArgumentException("Region code already exists: " + regionalSettings.getRegionCode());
        }
        
        // Process additional settings before saving
        if (regionalSettings.getAdditionalSettings() != null) {
            try {
                regionalSettings.setSettingsJson(objectMapper.writeValueAsString(regionalSettings.getAdditionalSettings()));
            } catch (IOException e) {
                log.error("Error converting additional settings to JSON", e);
                throw new IllegalArgumentException("Invalid additional settings format");
            }
        }
        
        return regionalSettingsRepository.save(regionalSettings);
    }

    @Override
    public RegionalSettings updateRegionalSettings(Long id, RegionalSettings regionalSettings) {
        log.debug("Updating regional settings with ID: {}", id);
        
        RegionalSettings existingSettings = regionalSettingsRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Regional settings not found with ID: " + id));
        
        // Check if the region code is being changed and if it already exists
        if (regionalSettings.getRegionCode() != null && 
            !regionalSettings.getRegionCode().equals(existingSettings.getRegionCode()) &&
            regionalSettingsRepository.existsByRegionCode(regionalSettings.getRegionCode())) {
            throw new IllegalArgumentException("Region code already exists: " + regionalSettings.getRegionCode());
        }
        
        // Update fields
        existingSettings.setRegionName(regionalSettings.getRegionName());
        existingSettings.setRegionCode(regionalSettings.getRegionCode());
        existingSettings.setIsActive(regionalSettings.getIsActive());
        existingSettings.setTimezone(regionalSettings.getTimezone());
        existingSettings.setLocale(regionalSettings.getLocale());
        existingSettings.setCurrencyCode(regionalSettings.getCurrencyCode());
        existingSettings.setMeasurementSystem(regionalSettings.getMeasurementSystem());
        existingSettings.setBusinessHours(regionalSettings.getBusinessHours());
        existingSettings.setContactEmail(regionalSettings.getContactEmail());
        existingSettings.setContactPhone(regionalSettings.getContactPhone());
        existingSettings.setEmergencyContact(regionalSettings.getEmergencyContact());
        existingSettings.setRegionalManager(regionalSettings.getRegionalManager());
        existingSettings.setDescription(regionalSettings.getDescription());
        existingSettings.setGlobalSettingsSync(regionalSettings.getGlobalSettingsSync());
        
        // Process additional settings before saving
        if (regionalSettings.getAdditionalSettings() != null) {
            try {
                existingSettings.setSettingsJson(objectMapper.writeValueAsString(regionalSettings.getAdditionalSettings()));
            } catch (IOException e) {
                log.error("Error converting additional settings to JSON", e);
                throw new IllegalArgumentException("Invalid additional settings format");
            }
        }
        
        RegionalSettings updatedSettings = regionalSettingsRepository.save(existingSettings);
        processAdditionalSettings(updatedSettings);
        return updatedSettings;
    }

    @Override
    public void deleteRegionalSettings(Long id) {
        log.debug("Deleting regional settings with ID: {}", id);
        
        if (!regionalSettingsRepository.existsById(id)) {
            throw new IllegalArgumentException("Regional settings not found with ID: " + id);
        }
        
        regionalSettingsRepository.deleteById(id);
    }

    @Override
    public List<RegionalSettings> searchRegionalSettingsByName(String searchText) {
        log.debug("Searching for regional settings with name containing: {}", searchText);
        List<RegionalSettings> settings = regionalSettingsRepository.findByRegionNameContainingIgnoreCase(searchText);
        settings.forEach(this::processAdditionalSettings);
        return settings;
    }

    @Override
    public List<RegionalSettings> getRegionalSettingsByTimezone(String timezone) {
        log.debug("Retrieving regional settings with timezone: {}", timezone);
        List<RegionalSettings> settings = regionalSettingsRepository.findByTimezone(timezone);
        settings.forEach(this::processAdditionalSettings);
        return settings;
    }

    @Override
    public List<RegionalSettings> getRegionalSettingsByCurrencyCode(String currencyCode) {
        log.debug("Retrieving regional settings with currency code: {}", currencyCode);
        List<RegionalSettings> settings = regionalSettingsRepository.findByCurrencyCode(currencyCode);
        settings.forEach(this::processAdditionalSettings);
        return settings;
    }

    @Override
    public List<RegionalSettings> getRegionalSettingsByLocale(String locale) {
        log.debug("Retrieving regional settings with locale: {}", locale);
        List<RegionalSettings> settings = regionalSettingsRepository.findByLocale(locale);
        settings.forEach(this::processAdditionalSettings);
        return settings;
    }

    @Override
    public List<RegionalSettings> getRegionalSettingsByMeasurementSystem(String measurementSystem) {
        log.debug("Retrieving regional settings with measurement system: {}", measurementSystem);
        List<RegionalSettings> settings = regionalSettingsRepository.findByMeasurementSystem(measurementSystem);
        settings.forEach(this::processAdditionalSettings);
        return settings;
    }

    @Override
    public List<RegionalSettings> getRegionalSettingsByManager(String managerName) {
        log.debug("Retrieving regional settings managed by: {}", managerName);
        List<RegionalSettings> settings = regionalSettingsRepository.findByRegionalManagerContainingIgnoreCase(managerName);
        settings.forEach(this::processAdditionalSettings);
        return settings;
    }

    @Override
    public List<RegionalSettings> getRegionalSettingsWithGlobalSync() {
        log.debug("Retrieving regional settings with global sync enabled");
        List<RegionalSettings> settings = regionalSettingsRepository.findByGlobalSettingsSyncTrue();
        settings.forEach(this::processAdditionalSettings);
        return settings;
    }

    @Override
    public List<RegionalSettings> getRegionalSettingsWithMissingEmergencyContact() {
        log.debug("Retrieving regional settings with missing emergency contact");
        List<RegionalSettings> settings = regionalSettingsRepository.findWithMissingEmergencyContact();
        settings.forEach(this::processAdditionalSettings);
        return settings;
    }

    @Override
    public RegionalSettings synchronizeWithGlobalSettings(Long regionalSettingsId) {
        log.debug("Synchronizing regional settings with ID {} with global settings", regionalSettingsId);
        
        RegionalSettings settings = regionalSettingsRepository.findById(regionalSettingsId)
            .orElseThrow(() -> new IllegalArgumentException("Regional settings not found with ID: " + regionalSettingsId));
        
        if (!settings.getGlobalSettingsSync()) {
            throw new IllegalArgumentException("Global settings sync is not enabled for this region");
        }
        
        // Here we would implement the logic to fetch and apply global settings
        // This is a placeholder for the actual implementation that would interact with the Global HQ Admin service
        
        settings.setIsActive(true); // Example update
        
        RegionalSettings updatedSettings = regionalSettingsRepository.save(settings);
        processAdditionalSettings(updatedSettings);
        return updatedSettings;
    }

    @Override
    public boolean existsByRegionCode(String regionCode) {
        log.debug("Checking if regional settings exist with code: {}", regionCode);
        return regionalSettingsRepository.existsByRegionCode(regionCode);
    }

    @Override
    public long countByGlobalRegionId(Long globalRegionId) {
        log.debug("Counting regional settings for global region ID: {}", globalRegionId);
        return regionalSettingsRepository.countByGlobalRegionId(globalRegionId);
    }

    @Override
    public RegionalSettings updateSpecificSettings(Long id, Map<String, Object> settings) {
        log.debug("Updating specific settings for regional settings with ID: {}", id);
        
        RegionalSettings existingSettings = regionalSettingsRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Regional settings not found with ID: " + id));
        
        // Load existing additional settings
        Map<String, Object> additionalSettings = getAdditionalSettings(existingSettings);
        
        // Update with new settings
        additionalSettings.putAll(settings);
        
        // Save updated settings
        try {
            existingSettings.setSettingsJson(objectMapper.writeValueAsString(additionalSettings));
        } catch (IOException e) {
            log.error("Error converting additional settings to JSON", e);
            throw new IllegalArgumentException("Invalid additional settings format");
        }
        
        RegionalSettings updatedSettings = regionalSettingsRepository.save(existingSettings);
        processAdditionalSettings(updatedSettings);
        return updatedSettings;
    }
    
    /**
     * Helper method to process additional settings JSON into a map.
     * 
     * @param settings The regional settings to process
     */
    private void processAdditionalSettings(RegionalSettings settings) {
        if (settings.getSettingsJson() != null && !settings.getSettingsJson().isEmpty()) {
            try {
                settings.setAdditionalSettings(objectMapper.readValue(settings.getSettingsJson(), Map.class));
            } catch (IOException e) {
                log.error("Error parsing settings JSON", e);
                settings.setAdditionalSettings(new HashMap<>());
            }
        } else {
            settings.setAdditionalSettings(new HashMap<>());
        }
    }
    
    /**
     * Helper method to get additional settings as a map.
     * 
     * @param settings The regional settings to process
     * @return Map of additional settings
     */
    private Map<String, Object> getAdditionalSettings(RegionalSettings settings) {
        if (settings.getSettingsJson() != null && !settings.getSettingsJson().isEmpty()) {
            try {
                return objectMapper.readValue(settings.getSettingsJson(), Map.class);
            } catch (IOException e) {
                log.error("Error parsing settings JSON", e);
                return new HashMap<>();
            }
        } else {
            return new HashMap<>();
        }
    }
}
