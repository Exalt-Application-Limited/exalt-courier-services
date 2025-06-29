package com.exalt.integration.common.repository;

import com.exalt.integration.common.model.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for managing Provider entities
 */
@Repository
public interface ProviderRepository extends JpaRepository<Provider, Long> {

    /**
     * Find a provider by its unique code
     * @param providerCode The code of the provider
     * @return An optional containing the provider if found
     */
    Optional<Provider> findByProviderCode(String providerCode);
    
    /**
     * Find all enabled providers
     * @return List of all enabled providers
     */
    List<Provider> findByEnabledTrue();
    
    /**
     * Find all providers that support international shipping
     * @return List of providers supporting international shipping
     */
    List<Provider> findBySupportsInternationalTrue();
    
    /**
     * Find the default provider
     * @return An optional containing the default provider if set
     */
    Optional<Provider> findByDefaultProviderTrue();
    
    /**
     * Find providers by type
     * @param providerType The type of provider
     * @return List of providers of the specified type
     */
    List<Provider> findByProviderType(Provider.ProviderType providerType);
    
    /**
     * Find providers that support a specific country (using JPQL to query the supportedCountries map)
     * @param countryCode ISO country code
     * @return List of providers supporting the specified country
     */
    @Query("SELECT p FROM Provider p JOIN p.supportedCountries c WHERE KEY(c) = :countryCode AND p.enabled = true")
    List<Provider> findProvidersSupportingCountry(String countryCode);
    
    /**
     * Check if a provider code already exists
     * @param providerCode The provider code to check
     * @return true if the provider code exists, false otherwise
     */
    boolean existsByProviderCode(String providerCode);
}
