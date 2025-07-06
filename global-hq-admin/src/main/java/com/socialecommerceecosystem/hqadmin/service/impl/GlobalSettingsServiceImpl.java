package com.gogidix.courier.courier.hqadmin.service.impl;

import com.socialecommerceecosystem.hqadmin.model.GlobalSettings;
import com.socialecommerceecosystem.hqadmin.repository.GlobalSettingsRepository;
import com.socialecommerceecosystem.hqadmin.service.GlobalSettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the GlobalSettingsService interface.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class GlobalSettingsServiceImpl implements GlobalSettingsService {

    private final GlobalSettingsRepository globalSettingsRepository;

    @Override
    public List<GlobalSettings> getAllSettings() {
        return globalSettingsRepository.findAll();
    }

    @Override
    public Optional<GlobalSettings> getSettingById(Long id) {
        return globalSettingsRepository.findById(id);
    }

    @Override
    public Optional<GlobalSettings> getSettingByKey(String key) {
        return globalSettingsRepository.findByKey(key);
    }

    @Override
    public List<GlobalSettings> getSettingsByCategory(String category) {
        return globalSettingsRepository.findByCategory(category);
    }

    @Override
    @Transactional
    public GlobalSettings createSetting(GlobalSettings setting) {
        log.info("Creating new global setting with key: {}", setting.getKey());
        
        // Check if setting already exists
        if (globalSettingsRepository.findByKey(setting.getKey()).isPresent()) {
            throw new IllegalArgumentException("Setting with key " + setting.getKey() + " already exists");
        }
        
        setting.setLastUpdatedAt(LocalDateTime.now());
        
        return globalSettingsRepository.save(setting);
    }

    @Override
    @Transactional
    public GlobalSettings updateSetting(Long id, GlobalSettings settingDetails) {
        log.info("Updating global setting with id: {}", id);
        
        return globalSettingsRepository.findById(id)
            .map(existingSetting -> {
                // Check if setting is mutable
                if (!existingSetting.getIsMutable()) {
                    throw new IllegalStateException("Cannot update immutable setting: " + existingSetting.getKey());
                }
                
                // Update fields
                existingSetting.setValue(settingDetails.getValue());
                existingSetting.setDescription(settingDetails.getDescription());
                existingSetting.setCategory(settingDetails.getCategory());
                existingSetting.setLastUpdatedBy(settingDetails.getLastUpdatedBy());
                existingSetting.setLastUpdatedAt(LocalDateTime.now());
                
                return globalSettingsRepository.save(existingSetting);
            })
            .orElseThrow(() -> new IllegalArgumentException("Setting not found with id: " + id));
    }

    @Override
    @Transactional
    public void deleteSetting(Long id) {
        log.info("Deleting global setting with id: {}", id);
        
        GlobalSettings setting = globalSettingsRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Setting not found with id: " + id));
        
        // Check if setting is mutable
        if (!setting.getIsMutable()) {
            throw new IllegalStateException("Cannot delete immutable setting: " + setting.getKey());
        }
        
        globalSettingsRepository.delete(setting);
    }

    @Override
    public String getSettingValueOrDefault(String key, String defaultValue) {
        return globalSettingsRepository.findByKey(key)
            .map(GlobalSettings::getValue)
            .orElse(defaultValue);
    }

    @Override
    public List<GlobalSettings> searchSettingsByKey(String searchText) {
        return globalSettingsRepository.findByKeyContainingIgnoreCase(searchText);
    }

    @Override
    public List<GlobalSettings> getMutableSettings() {
        return globalSettingsRepository.findByIsMutableTrue();
    }

    @Override
    @Transactional
    public List<GlobalSettings> bulkUpdateSettings(List<GlobalSettings> settings) {
        log.info("Bulk updating {} settings", settings.size());
        
        List<GlobalSettings> updatedSettings = new ArrayList<>();
        
        for (GlobalSettings setting : settings) {
            if (setting.getId() == null) {
                continue; // Skip settings without ID
            }
            
            globalSettingsRepository.findById(setting.getId())
                .ifPresent(existingSetting -> {
                    // Check if setting is mutable
                    if (!existingSetting.getIsMutable()) {
                        log.warn("Skipping update for immutable setting: {}", existingSetting.getKey());
                        return;
                    }
                    
                    // Update fields
                    existingSetting.setValue(setting.getValue());
                    existingSetting.setDescription(setting.getDescription());
                    existingSetting.setCategory(setting.getCategory());
                    existingSetting.setLastUpdatedBy(setting.getLastUpdatedBy());
                    existingSetting.setLastUpdatedAt(LocalDateTime.now());
                    
                    updatedSettings.add(globalSettingsRepository.save(existingSetting));
                });
        }
        
        return updatedSettings;
    }
}
