package com.exalt.courier.hqadmin.service;

import com.socialecommerceecosystem.hqadmin.model.GlobalPricing;
import com.socialecommerceecosystem.hqadmin.model.GlobalRegion;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service interface defining operations for managing global pricing strategies.
 */
public interface GlobalPricingService {
    
    /**
     * Get all global pricing strategies
     * 
     * @return List of all global pricing strategies
     */
    List<GlobalPricing> getAllPricing();
    
    /**
     * Get a global pricing strategy by ID
     * 
     * @param id The pricing ID
     * @return The global pricing strategy if found
     */
    Optional<GlobalPricing> getPricingById(Long id);
    
    /**
     * Get a global pricing strategy by its unique code
     * 
     * @param pricingCode The pricing code
     * @return The global pricing strategy if found
     */
    Optional<GlobalPricing> getPricingByCode(String pricingCode);
    
    /**
     * Create a new global pricing strategy
     * 
     * @param pricing The global pricing strategy to create
     * @return The created global pricing strategy
     */
    GlobalPricing createPricing(GlobalPricing pricing);
    
    /**
     * Update an existing global pricing strategy
     * 
     * @param id The pricing ID to update
     * @param pricingDetails The updated pricing details
     * @return The updated global pricing strategy
     * @throws RuntimeException if pricing not found
     */
    GlobalPricing updatePricing(Long id, GlobalPricing pricingDetails);
    
    /**
     * Delete a global pricing strategy
     * 
     * @param id The pricing ID to delete
     * @throws RuntimeException if pricing not found
     */
    void deletePricing(Long id);
    
    /**
     * Get all active global pricing strategies
     * 
     * @return List of active global pricing strategies
     */
    List<GlobalPricing> getAllActivePricing();
    
    /**
     * Get all pricing strategies for a specific service type
     * 
     * @param serviceType The service type
     * @return List of pricing strategies for the service type
     */
    List<GlobalPricing> getPricingByServiceType(String serviceType);
    
    /**
     * Get all pricing strategies for a specific global region
     * 
     * @param regionId The global region ID
     * @return List of pricing strategies for the region
     * @throws RuntimeException if region not found
     */
    List<GlobalPricing> getPricingByRegion(Long regionId);
    
    /**
     * Get all pricing strategies that allow regional overrides
     * 
     * @return List of pricing strategies that allow regional overrides
     */
    List<GlobalPricing> getPricingWithRegionalOverrides();
    
    /**
     * Get global default pricing strategies (without region assignment)
     * 
     * @return List of global default pricing strategies
     */
    List<GlobalPricing> getGlobalDefaultPricing();
    
    /**
     * Search pricing strategies by name
     * 
     * @param searchText The search text
     * @return List of pricing strategies matching the search
     */
    List<GlobalPricing> searchPricingByName(String searchText);
    
    /**
     * Get pricing strategies by currency code
     * 
     * @param currencyCode The currency code
     * @return List of pricing strategies using the specified currency
     */
    List<GlobalPricing> getPricingByCurrencyCode(String currencyCode);
    
    /**
     * Get pricing strategies below a specific base price
     * 
     * @param maxPrice The maximum base price
     * @return List of pricing strategies below the specified price
     */
    List<GlobalPricing> getPricingBelowBasePrice(BigDecimal maxPrice);
    
    /**
     * Get pricing strategies effective on a specific date
     * 
     * @param date The date to check
     * @return List of pricing strategies effective on the specified date
     */
    List<GlobalPricing> getPricingEffectiveOn(LocalDate date);
    
    /**
     * Get pricing strategies for a specific service type and region
     * 
     * @param serviceType The service type
     * @param regionId The global region ID
     * @return List of matching pricing strategies
     * @throws RuntimeException if region not found
     */
    List<GlobalPricing> getPricingByServiceTypeAndRegion(String serviceType, Long regionId);
    
    /**
     * Count pricing strategies by service type
     * 
     * @return List of count results by service type
     */
    List<Object[]> countPricingByServiceType();
    
    /**
     * Apply global pricing strategy to a specific region
     * 
     * @param pricingId The global pricing strategy ID
     * @param regionId The global region ID
     * @return The newly created regional pricing strategy
     * @throws RuntimeException if pricing or region not found
     */
    GlobalPricing applyGlobalPricingToRegion(Long pricingId, Long regionId);
}
