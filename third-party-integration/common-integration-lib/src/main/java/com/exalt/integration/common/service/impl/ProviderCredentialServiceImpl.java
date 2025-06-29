package com.exalt.integration.common.service.impl;

import com.exalt.integration.common.exception.ResourceNotFoundException;
import com.exalt.integration.common.model.Provider;
import com.exalt.integration.common.model.ProviderCredential;
import com.exalt.integration.common.repository.ProviderCredentialRepository;
import com.exalt.integration.common.service.ProviderCredentialService;
import com.exalt.integration.common.service.ShippingProviderAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation of ProviderCredentialService
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProviderCredentialServiceImpl implements ProviderCredentialService {

    private final ProviderCredentialRepository credentialRepository;
    private final ApplicationContext applicationContext;
    private final Map<String, ShippingProviderAdapter> providerAdapters;

    @Override
    public List<ProviderCredential> getAllProviderCredentials() {
        return credentialRepository.findAll();
    }

    @Override
    public Optional<ProviderCredential> getCredentialById(Long id) {
        return credentialRepository.findById(id);
    }

    @Override
    public Optional<ProviderCredential> getCredentialByProvider(Provider provider) {
        return credentialRepository.findByProvider(provider);
    }

    @Override
    public Optional<ProviderCredential> getCredentialByProviderCode(String providerCode) {
        return credentialRepository.findByProvider_ProviderCode(providerCode);
    }

    @Override
    @Transactional
    public ProviderCredential createCredential(ProviderCredential providerCredential) {
        // Check if credentials already exist for this provider
        if (credentialRepository.existsByProvider(providerCredential.getProvider())) {
            throw new IllegalArgumentException("Credentials already exist for provider: " + providerCredential.getProvider().getProviderCode());
        }

        // Set as active by default
        providerCredential.setActive(true);
        
        log.info("Creating new credentials for provider: {}", providerCredential.getProvider().getProviderCode());
        return credentialRepository.save(providerCredential);
    }

    @Override
    @Transactional
    public ProviderCredential updateCredential(Long id, ProviderCredential credentialDetails) {
        ProviderCredential credential = credentialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Provider credential not found with id: " + id));

        // Update credential fields
        credential.setCredentialName(credentialDetails.getCredentialName());
        credential.setApiKey(credentialDetails.getApiKey());
        credential.setApiSecret(credentialDetails.getApiSecret());
        credential.setUsername(credentialDetails.getUsername());
        credential.setPassword(credentialDetails.getPassword());
        credential.setAccessToken(credentialDetails.getAccessToken());
        credential.setRefreshToken(credentialDetails.getRefreshToken());
        credential.setTokenExpiry(credentialDetails.getTokenExpiry());
        credential.setAccountNumber(credentialDetails.getAccountNumber());
        credential.setAccountId(credentialDetails.getAccountId());
        credential.setClientId(credentialDetails.getClientId());
        credential.setClientSecret(credentialDetails.getClientSecret());
        credential.setActive(credentialDetails.isActive());

        // Update additional credentials (clear and re-add)
        credential.getAdditionalCredentials().clear();
        if (credentialDetails.getAdditionalCredentials() != null) {
            credential.getAdditionalCredentials().putAll(credentialDetails.getAdditionalCredentials());
        }
        
        log.info("Updating credentials for provider: {}", credential.getProvider().getProviderCode());
        return credentialRepository.save(credential);
    }

    @Override
    @Transactional
    public void deleteCredential(Long id) {
        ProviderCredential credential = credentialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Provider credential not found with id: " + id));
        
        log.info("Deleting credentials for provider: {}", credential.getProvider().getProviderCode());
        credentialRepository.delete(credential);
    }

    @Override
    public List<ProviderCredential> getActiveCredentials() {
        return credentialRepository.findByActiveTrue();
    }

    @Override
    public boolean validateCredential(Long credentialId) {
        ProviderCredential credential = credentialRepository.findById(credentialId)
                .orElseThrow(() -> new ResourceNotFoundException("Provider credential not found with id: " + credentialId));

        // Get the appropriate adapter for this provider
        ShippingProviderAdapter adapter = getAdapterForProvider(credential.getProvider().getProviderCode());
        
        if (adapter == null) {
            log.error("No adapter found for provider: {}", credential.getProvider().getProviderCode());
            return false;
        }
        
        log.info("Validating credentials for provider: {}", credential.getProvider().getProviderCode());
        return adapter.validateCredentials(credential);
    }

    @Override
    @Transactional
    public ProviderCredential refreshTokens(Long credentialId) {
        ProviderCredential credential = credentialRepository.findById(credentialId)
                .orElseThrow(() -> new ResourceNotFoundException("Provider credential not found with id: " + credentialId));

        if (credential.getRefreshToken() == null || credential.getRefreshToken().isEmpty()) {
            throw new IllegalStateException("No refresh token available for provider: " + credential.getProvider().getProviderCode());
        }

        // Get the appropriate adapter for this provider
        ShippingProviderAdapter adapter = getAdapterForProvider(credential.getProvider().getProviderCode());
        
        if (adapter == null) {
            throw new IllegalStateException("No adapter found for provider: " + credential.getProvider().getProviderCode());
        }
        
        try {
            log.info("Refreshing tokens for provider: {}", credential.getProvider().getProviderCode());
            ProviderCredential updatedCredential = adapter.refreshAccessToken(credential);
            
            // Update token fields in original entity
            credential.setAccessToken(updatedCredential.getAccessToken());
            credential.setRefreshToken(updatedCredential.getRefreshToken());
            credential.setTokenExpiry(updatedCredential.getTokenExpiry());
            
            return credentialRepository.save(credential);
        } catch (Exception e) {
            log.error("Error refreshing tokens for provider: {}", credential.getProvider().getProviderCode(), e);
            throw new RuntimeException("Failed to refresh tokens: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public ProviderCredential setCredentialActive(Long credentialId, boolean active) {
        ProviderCredential credential = credentialRepository.findById(credentialId)
                .orElseThrow(() -> new ResourceNotFoundException("Provider credential not found with id: " + credentialId));
        
        credential.setActive(active);
        
        log.info("Provider credential for {} is now {}", credential.getProvider().getProviderCode(), active ? "active" : "inactive");
        return credentialRepository.save(credential);
    }
    
    /**
     * Helper method to get the appropriate adapter for a provider
     */
    private ShippingProviderAdapter getAdapterForProvider(String providerCode) {
        return providerAdapters.values().stream()
                .filter(adapter -> adapter.getProviderCode().equalsIgnoreCase(providerCode))
                .findFirst()
                .orElse(null);
    }
}
