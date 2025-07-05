package com.gogidix.integration.common.service;

import com.gogidix.integration.common.model.Provider;
import com.gogidix.integration.common.model.ProviderCredential;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing shipping provider credentials
 */
public interface ProviderCredentialService {

    /**
     * Get all provider credentials
     * @return List of all provider credentials
     */
    List<ProviderCredential> getAllProviderCredentials();

    /**
     * Get provider credential by ID
     * @param id Credential ID
     * @return Optional credential
     */
    Optional<ProviderCredential> getCredentialById(Long id);

    /**
     * Get credential for a specific provider
     * @param provider The provider
     * @return Optional credential
     */
    Optional<ProviderCredential> getCredentialByProvider(Provider provider);

    /**
     * Get credential for a provider by code
     * @param providerCode The provider code
     * @return Optional credential
     */
    Optional<ProviderCredential> getCredentialByProviderCode(String providerCode);

    /**
     * Create new provider credentials
     * @param providerCredential The credentials to create
     * @return The created credentials
     */
    ProviderCredential createCredential(ProviderCredential providerCredential);

    /**
     * Update existing provider credentials
     * @param id Credential ID
     * @param providerCredential Updated credential data
     * @return The updated credentials
     */
    ProviderCredential updateCredential(Long id, ProviderCredential providerCredential);

    /**
     * Delete provider credentials
     * @param id Credential ID
     */
    void deleteCredential(Long id);

    /**
     * Get all active provider credentials
     * @return List of active credentials
     */
    List<ProviderCredential> getActiveCredentials();

    /**
     * Validate credentials by testing the connection to the provider
     * @param credentialId Credential ID
     * @return true if valid, false otherwise
     */
    boolean validateCredential(Long credentialId);

    /**
     * Refresh access tokens for OAuth-based providers
     * @param credentialId Credential ID
     * @return Updated credentials with refreshed tokens
     */
    ProviderCredential refreshTokens(Long credentialId);
    
    /**
     * Set credential active or inactive
     * @param credentialId Credential ID
     * @param active true to activate, false to deactivate
     * @return Updated credentials
     */
    ProviderCredential setCredentialActive(Long credentialId, boolean active);
}
