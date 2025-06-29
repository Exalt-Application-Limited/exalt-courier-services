package com.exalt.integration.common.repository;

import com.exalt.integration.common.model.Provider;
import com.exalt.integration.common.model.ProviderCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for managing ProviderCredential entities
 */
@Repository
public interface ProviderCredentialRepository extends JpaRepository<ProviderCredential, Long> {

    /**
     * Find credentials by provider
     * @param provider The associated provider
     * @return An optional containing the provider credentials if found
     */
    Optional<ProviderCredential> findByProvider(Provider provider);
    
    /**
     * Find credentials by provider code
     * @param providerCode The code of the provider
     * @return An optional containing the provider credentials if found
     */
    Optional<ProviderCredential> findByProvider_ProviderCode(String providerCode);
    
    /**
     * Find all active credentials
     * @return List of all active credentials
     */
    java.util.List<ProviderCredential> findByActiveTrue();
    
    /**
     * Check if credentials exist for a provider
     * @param provider The provider to check
     * @return true if credentials exist, false otherwise
     */
    boolean existsByProvider(Provider provider);
    
    /**
     * Delete credentials by provider
     * @param provider The provider whose credentials should be deleted
     */
    void deleteByProvider(Provider provider);
}
