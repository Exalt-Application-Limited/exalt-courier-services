package com.gogidix.courier.hqadmin.repository;

import com.socialecommerceecosystem.hqadmin.model.GlobalPricing;
import com.socialecommerceecosystem.hqadmin.model.GlobalRegion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for {@link GlobalPricing} entity that provides
 * data access operations for global pricing strategies.
 */
@Repository
public interface GlobalPricingRepository extends JpaRepository<GlobalPricing, Long> {
    
    /**
     * Find a global pricing strategy by its unique code
     * 
     * @param pricingCode The pricing code
     * @return The pricing strategy if found
     */
    Optional<GlobalPricing> findByPricingCode(String pricingCode);
    
    /**
     * Find all active global pricing strategies
     * 
     * @return List of active pricing strategies
     */
    List<GlobalPricing> findByIsActiveTrue();
    
    /**
     * Find all pricing strategies for a specific service type
     * 
     * @param serviceType The service type
     * @return List of pricing strategies for the service type
     */
    List<GlobalPricing> findByServiceType(String serviceType);
    
    /**
     * Find all pricing strategies for a specific global region
     * 
     * @param globalRegion The global region
     * @return List of pricing strategies for the region
     */
    List<GlobalPricing> findByGlobalRegion(GlobalRegion globalRegion);
    
    /**
     * Find all pricing strategies that allow regional overrides
     * 
     * @return List of pricing strategies that allow regional overrides
     */
    List<GlobalPricing> findByAllowRegionalOverrideTrue();
    
    /**
     * Find global default pricing strategies (without region assignment)
     * 
     * @return List of global default pricing strategies
     */
    List<GlobalPricing> findByGlobalRegionIsNull();
    
    /**
     * Find pricing strategies by name containing the search text
     * 
     * @param searchText The search text
     * @return List of matching pricing strategies
     */
    List<GlobalPricing> findByNameContainingIgnoreCase(String searchText);
    
    /**
     * Find pricing strategies by currency code
     * 
     * @param currencyCode The currency code
     * @return List of pricing strategies using the specified currency
     */
    List<GlobalPricing> findByCurrencyCode(String currencyCode);
    
    /**
     * Find pricing strategies below a specific base price
     * 
     * @param maxPrice The maximum base price
     * @return List of pricing strategies below the specified price
     */
    List<GlobalPricing> findByBasePriceLessThanEqual(BigDecimal maxPrice);
    
    /**
     * Find pricing strategies effective on a specific date
     * 
     * @param date The date to check
     * @return List of pricing strategies effective on the specified date
     */
    @Query("SELECT p FROM GlobalPricing p WHERE p.isActive = true " +
           "AND (p.effectiveFrom IS NULL OR p.effectiveFrom <= :date) " +
           "AND (p.effectiveUntil IS NULL OR p.effectiveUntil >= :date)")
    List<GlobalPricing> findPricingEffectiveOn(@Param("date") LocalDate date);
    
    /**
     * Find pricing strategies for a specific service type and region
     * 
     * @param serviceType The service type
     * @param region The global region
     * @return List of matching pricing strategies
     */
    List<GlobalPricing> findByServiceTypeAndGlobalRegion(String serviceType, GlobalRegion region);
    
    /**
     * Count pricing strategies by service type
     * 
     * @return List of count results by service type
     */
    @Query("SELECT p.serviceType as serviceType, COUNT(p) as pricingCount FROM GlobalPricing p " +
           "GROUP BY p.serviceType")
    List<Object[]> countPricingByServiceType();
}
