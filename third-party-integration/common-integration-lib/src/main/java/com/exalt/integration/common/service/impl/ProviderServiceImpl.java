package com.exalt.integration.common.service.impl;

import com.exalt.integration.common.exception.ResourceNotFoundException;
import com.exalt.integration.common.model.Provider;
import com.exalt.integration.common.repository.ProviderRepository;
import com.exalt.integration.common.service.ProviderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation of ProviderService
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProviderServiceImpl implements ProviderService {

    private final ProviderRepository providerRepository;

    @Override
    public List<Provider> getAllProviders() {
        return providerRepository.findAll();
    }

    @Override
    public Optional<Provider> getProviderById(Long id) {
        return providerRepository.findById(id);
    }

    @Override
    public Optional<Provider> getProviderByCode(String providerCode) {
        return providerRepository.findByProviderCode(providerCode);
    }

    @Override
    public List<Provider> getEnabledProviders() {
        return providerRepository.findByEnabledTrue();
    }

    @Override
    @Transactional
    public Provider createProvider(Provider provider) {
        // Check if provider with same code already exists
        if (providerRepository.existsByProviderCode(provider.getProviderCode())) {
            throw new IllegalArgumentException("Provider with code " + provider.getProviderCode() + " already exists");
        }

        // If this is set as the default provider, clear any existing default providers
        if (provider.isDefaultProvider()) {
            clearExistingDefaultProvider();
        }

        log.info("Creating new provider: {}", provider.getProviderCode());
        return providerRepository.save(provider);
    }

    @Override
    @Transactional
    public Provider updateProvider(Long id, Provider providerDetails) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found with id: " + id));

        // If provider code is changed, check that new code doesn't already exist
        if (!provider.getProviderCode().equals(providerDetails.getProviderCode()) &&
                providerRepository.existsByProviderCode(providerDetails.getProviderCode())) {
            throw new IllegalArgumentException("Provider with code " + providerDetails.getProviderCode() + " already exists");
        }

        // If this is being set as the default provider, clear any existing default
        if (providerDetails.isDefaultProvider() && !provider.isDefaultProvider()) {
            clearExistingDefaultProvider();
        }

        // Update provider fields
        provider.setProviderCode(providerDetails.getProviderCode());
        provider.setProviderName(providerDetails.getProviderName());
        provider.setProviderType(providerDetails.getProviderType());
        provider.setDescription(providerDetails.getDescription());
        provider.setApiBaseUrl(providerDetails.getApiBaseUrl());
        provider.setWebhookUrl(providerDetails.getWebhookUrl());
        provider.setWebhookSecret(providerDetails.getWebhookSecret());
        provider.setApiVersion(providerDetails.getApiVersion());
        provider.setTestMode(providerDetails.isTestMode());
        provider.setEnabled(providerDetails.isEnabled());
        provider.setDefaultProvider(providerDetails.isDefaultProvider());
        provider.setSupportsTracking(providerDetails.isSupportsTracking());
        provider.setSupportsLabelGeneration(providerDetails.isSupportsLabelGeneration());
        provider.setSupportsRateCalculation(providerDetails.isSupportsRateCalculation());
        provider.setSupportsInternational(providerDetails.isSupportsInternational());

        log.info("Updating provider: {}", provider.getProviderCode());
        return providerRepository.save(provider);
    }

    @Override
    @Transactional
    public void deleteProvider(Long id) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found with id: " + id));
        
        log.info("Deleting provider: {}", provider.getProviderCode());
        providerRepository.delete(provider);
    }

    @Override
    @Transactional
    public Provider setDefaultProvider(Long providerId) {
        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found with id: " + providerId));

        // Clear any existing default provider
        clearExistingDefaultProvider();

        // Set this provider as default
        provider.setDefaultProvider(true);
        
        log.info("Setting {} as the default provider", provider.getProviderCode());
        return providerRepository.save(provider);
    }

    @Override
    public List<Provider> getProvidersByType(Provider.ProviderType providerType) {
        return providerRepository.findByProviderType(providerType);
    }

    @Override
    public List<Provider> getProvidersSupportingCountry(String countryCode) {
        return providerRepository.findProvidersSupportingCountry(countryCode);
    }

    @Override
    @Transactional
    public Provider updateServiceMappings(Long providerId, Map<String, String> serviceMappings) {
        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found with id: " + providerId));

        provider.getServiceMappings().clear();
        provider.getServiceMappings().putAll(serviceMappings);
        
        log.info("Updated service mappings for provider: {}", provider.getProviderCode());
        return providerRepository.save(provider);
    }

    @Override
    @Transactional
    public Provider updateSupportedCountries(Long providerId, Map<String, String> supportedCountries) {
        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found with id: " + providerId));

        provider.getSupportedCountries().clear();
        provider.getSupportedCountries().putAll(supportedCountries);
        
        log.info("Updated supported countries for provider: {}", provider.getProviderCode());
        return providerRepository.save(provider);
    }

    @Override
    public Optional<Provider> getDefaultProvider() {
        return providerRepository.findByDefaultProviderTrue();
    }

    @Override
    @Transactional
    public Provider setProviderEnabled(Long providerId, boolean enabled) {
        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found with id: " + providerId));
        
        provider.setEnabled(enabled);
        
        log.info("Provider {} is now {}", provider.getProviderCode(), enabled ? "enabled" : "disabled");
        return providerRepository.save(provider);
    }

    /**
     * Helper method to clear any existing default provider
     */
    private void clearExistingDefaultProvider() {
        Optional<Provider> existingDefault = providerRepository.findByDefaultProviderTrue();
        existingDefault.ifPresent(p -> {
            p.setDefaultProvider(false);
            providerRepository.save(p);
            log.info("Cleared default provider flag from: {}", p.getProviderCode());
        });
    }
}
