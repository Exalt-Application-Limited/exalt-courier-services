package com.exalt.courier.hqadmin.service.impl;

import com.socialecommerceecosystem.hqadmin.model.GlobalRegion;
import com.socialecommerceecosystem.hqadmin.model.RegionalAdminSystem;
import com.socialecommerceecosystem.hqadmin.repository.GlobalRegionRepository;
import com.socialecommerceecosystem.hqadmin.repository.RegionalAdminSystemRepository;
import com.socialecommerceecosystem.hqadmin.service.RegionalAdminSystemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation of the RegionalAdminSystemService interface.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RegionalAdminSystemServiceImpl implements RegionalAdminSystemService {

    private final RegionalAdminSystemRepository regionalAdminSystemRepository;
    private final GlobalRegionRepository globalRegionRepository;

    @Override
    public List<RegionalAdminSystem> getAllRegionalAdminSystems() {
        return regionalAdminSystemRepository.findAll();
    }

    @Override
    public Optional<RegionalAdminSystem> getRegionalAdminSystemById(Long id) {
        return regionalAdminSystemRepository.findById(id);
    }

    @Override
    public Optional<RegionalAdminSystem> getRegionalAdminSystemByCode(String systemCode) {
        return regionalAdminSystemRepository.findBySystemCode(systemCode);
    }

    @Override
    @Transactional
    public RegionalAdminSystem createRegionalAdminSystem(RegionalAdminSystem system) {
        log.info("Creating new regional admin system with code: {}", system.getSystemCode());
        
        // Check if system code already exists
        if (regionalAdminSystemRepository.findBySystemCode(system.getSystemCode()).isPresent()) {
            throw new IllegalArgumentException("Regional admin system with code " + system.getSystemCode() + " already exists");
        }
        
        // Verify that the global region exists
        if (system.getGlobalRegion() != null && system.getGlobalRegion().getId() != null) {
            GlobalRegion region = globalRegionRepository.findById(system.getGlobalRegion().getId())
                .orElseThrow(() -> new IllegalArgumentException("Global region not found with id: " + system.getGlobalRegion().getId()));
            system.setGlobalRegion(region);
        }
        
        return regionalAdminSystemRepository.save(system);
    }

    @Override
    @Transactional
    public RegionalAdminSystem updateRegionalAdminSystem(Long id, RegionalAdminSystem systemDetails) {
        log.info("Updating regional admin system with id: {}", id);
        
        RegionalAdminSystem existingSystem = regionalAdminSystemRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Regional admin system not found with id: " + id));
        
        // Check if system code is being changed and if it already exists
        if (!existingSystem.getSystemCode().equals(systemDetails.getSystemCode()) && 
            regionalAdminSystemRepository.findBySystemCode(systemDetails.getSystemCode()).isPresent()) {
            throw new IllegalArgumentException("Regional admin system with code " + systemDetails.getSystemCode() + " already exists");
        }
        
        // Update fields
        existingSystem.setSystemCode(systemDetails.getSystemCode());
        existingSystem.setName(systemDetails.getName());
        existingSystem.setDescription(systemDetails.getDescription());
        existingSystem.setIsActive(systemDetails.getIsActive());
        existingSystem.setApiEndpoint(systemDetails.getApiEndpoint());
        existingSystem.setHealthCheckEndpoint(systemDetails.getHealthCheckEndpoint());
        existingSystem.setContactEmail(systemDetails.getContactEmail());
        existingSystem.setContactPhone(systemDetails.getContactPhone());
        existingSystem.setSupportUrl(systemDetails.getSupportUrl());
        
        // Update global region if provided
        if (systemDetails.getGlobalRegion() != null && systemDetails.getGlobalRegion().getId() != null) {
            GlobalRegion newRegion = globalRegionRepository.findById(systemDetails.getGlobalRegion().getId())
                .orElseThrow(() -> new IllegalArgumentException("Global region not found with id: " + systemDetails.getGlobalRegion().getId()));
            existingSystem.setGlobalRegion(newRegion);
        }
        
        return regionalAdminSystemRepository.save(existingSystem);
    }

    @Override
    @Transactional
    public void deleteRegionalAdminSystem(Long id) {
        log.info("Deleting regional admin system with id: {}", id);
        
        RegionalAdminSystem system = regionalAdminSystemRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Regional admin system not found with id: " + id));
        
        regionalAdminSystemRepository.delete(system);
    }

    @Override
    public List<RegionalAdminSystem> getAllActiveRegionalAdminSystems() {
        return regionalAdminSystemRepository.findByIsActiveTrue();
    }

    @Override
    public List<RegionalAdminSystem> getRegionalAdminSystemsByRegion(Long regionId) {
        log.debug("Getting regional admin systems for region id: {}", regionId);
        
        GlobalRegion region = globalRegionRepository.findById(regionId)
            .orElseThrow(() -> new IllegalArgumentException("Global region not found with id: " + regionId));
        
        return regionalAdminSystemRepository.findByGlobalRegion(region);
    }

    @Override
    public List<RegionalAdminSystem> searchRegionalAdminSystemsByName(String searchText) {
        return regionalAdminSystemRepository.findByNameContainingIgnoreCase(searchText);
    }

    @Override
    @Transactional
    public RegionalAdminSystem updateHealthCheck(Long id, String healthStatus) {
        log.info("Updating health check for regional admin system id: {}", id);
        
        RegionalAdminSystem system = regionalAdminSystemRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Regional admin system not found with id: " + id));
        
        system.setLastHealthCheck(LocalDateTime.now());
        system.setHealthStatus(healthStatus);
        
        return regionalAdminSystemRepository.save(system);
    }

    @Override
    public List<RegionalAdminSystem> findSystemsWithStaleHealthChecks(LocalDateTime checkTime) {
        return regionalAdminSystemRepository.findByLastHealthCheckBefore(checkTime);
    }

    @Override
    public List<RegionalAdminSystem> findSystemsByHealthStatus(String status) {
        return regionalAdminSystemRepository.findByHealthStatus(status);
    }

    @Override
    public Map<String, Long> countActiveSystemsByRegion() {
        List<Object[]> results = regionalAdminSystemRepository.countActiveSystemsByRegion();
        Map<String, Long> countMap = new HashMap<>();
        
        for (Object[] result : results) {
            String regionName = (String) result[0];
            Long count = ((Number) result[1]).longValue();
            countMap.put(regionName, count);
        }
        
        return countMap;
    }

    @Override
    @Transactional
    public List<RegionalAdminSystem> performHealthChecksForAllActiveSystems() {
        log.info("Performing health checks for all active regional admin systems");
        
        List<RegionalAdminSystem> activeSystems = regionalAdminSystemRepository.findByIsActiveTrue();
        List<RegionalAdminSystem> updatedSystems = new ArrayList<>();
        
        for (RegionalAdminSystem system : activeSystems) {
            try {
                // Here we would actually perform a health check using the system's endpoint
                // For now, we'll just simulate a successful health check
                system.setLastHealthCheck(LocalDateTime.now());
                system.setHealthStatus("HEALTHY");
                updatedSystems.add(regionalAdminSystemRepository.save(system));
            } catch (Exception e) {
                log.error("Error performing health check for system {}: {}", system.getSystemCode(), e.getMessage());
                system.setLastHealthCheck(LocalDateTime.now());
                system.setHealthStatus("ERROR");
                updatedSystems.add(regionalAdminSystemRepository.save(system));
            }
        }
        
        return updatedSystems;
    }

    @Override
    @Transactional
    public RegionalAdminSystem updateApiCredentials(Long id, String apiKey, String authToken) {
        log.info("Updating API credentials for regional admin system id: {}", id);
        
        RegionalAdminSystem system = regionalAdminSystemRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Regional admin system not found with id: " + id));
        
        system.setApiKey(apiKey);
        system.setAuthToken(authToken);
        
        return regionalAdminSystemRepository.save(system);
    }

    @Override
    @Transactional
    public RegionalAdminSystem updateContactInfo(Long id, String email, String phone, String supportUrl) {
        log.info("Updating contact info for regional admin system id: {}", id);
        
        RegionalAdminSystem system = regionalAdminSystemRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Regional admin system not found with id: " + id));
        
        system.setContactEmail(email);
        system.setContactPhone(phone);
        system.setSupportUrl(supportUrl);
        
        return regionalAdminSystemRepository.save(system);
    }

    @Override
    @Transactional
    public RegionalAdminSystem transferToRegion(Long systemId, Long newRegionId) {
        log.info("Transferring regional admin system {} to region {}", systemId, newRegionId);
        
        RegionalAdminSystem system = regionalAdminSystemRepository.findById(systemId)
            .orElseThrow(() -> new IllegalArgumentException("Regional admin system not found with id: " + systemId));
        
        GlobalRegion newRegion = globalRegionRepository.findById(newRegionId)
            .orElseThrow(() -> new IllegalArgumentException("Global region not found with id: " + newRegionId));
        
        system.setGlobalRegion(newRegion);
        
        return regionalAdminSystemRepository.save(system);
    }
}
