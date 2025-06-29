package com.exalt.courier.international.repository;

import com.exalt.courier.international.model.TariffRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing {@link TariffRate} entities.
 */
@Repository
public interface TariffRateRepository extends JpaRepository<TariffRate, String> {

    /**
     * Find tariff rate by destination country and HS code
     * @param destinationCountryCode The destination country code
     * @param hsCode The HS code
     * @return The tariff rate if found
     */
    Optional<TariffRate> findByDestinationCountryCodeAndHsCode(String destinationCountryCode, String hsCode);
    
    /**
     * Find all tariff rates for a destination country
     * @param destinationCountryCode The destination country code
     * @return List of tariff rates for the specified country
     */
    List<TariffRate> findByDestinationCountryCode(String destinationCountryCode);
    
    /**
     * Find all tariff rates for a specific HS code across all countries
     * @param hsCode The HS code
     * @return List of tariff rates for the specified HS code
     */
    List<TariffRate> findByHsCode(String hsCode);
    
    /**
     * Find tariff rates that are valid at the current time (now is between validFrom and validUntil)
     * @param referenceDate The reference date to check validity against
     * @return List of currently valid tariff rates
     */
    @Query("SELECT t FROM TariffRate t WHERE t.validFrom <= :referenceDate AND (t.validUntil IS NULL OR t.validUntil >= :referenceDate)")
    List<TariffRate> findCurrentlyValidRates(@Param("referenceDate") LocalDateTime referenceDate);
    
    /**
     * Find tariff rates that are restricted
     * @return List of restricted tariff rates
     */
    List<TariffRate> findByRestrictedTrue();
    
    /**
     * Find tariff rates by tax type
     * @param taxType The tax type
     * @return List of tariff rates with the specified tax type
     */
    List<TariffRate> findByTaxType(String taxType);
    
    /**
     * Find tariff rates with duty rates greater than a threshold
     * @param threshold The minimum duty rate threshold
     * @return List of tariff rates with duty rates above the threshold
     */
    List<TariffRate> findByDutyRateGreaterThan(Double threshold);
    
    /**
     * Find tariff rates with tax rates greater than a threshold
     * @param threshold The minimum tax rate threshold
     * @return List of tariff rates with tax rates above the threshold
     */
    List<TariffRate> findByTaxRateGreaterThan(Double threshold);
    
    /**
     * Find tariff rates with a duty-free threshold
     * @return List of tariff rates with a duty-free threshold
     */
    @Query("SELECT t FROM TariffRate t WHERE t.dutyFreeThreshold IS NOT NULL AND t.dutyFreeThreshold > 0")
    List<TariffRate> findWithDutyFreeThreshold();
    
    /**
     * Find tariff rates with HS codes that start with a specific prefix
     * @param prefix The HS code prefix (e.g., first 2 or 4 digits)
     * @return List of tariff rates with matching HS code prefix
     */
    @Query("SELECT t FROM TariffRate t WHERE t.hsCode LIKE :prefix%")
    List<TariffRate> findByHsCodeStartingWith(@Param("prefix") String prefix);
    
    /**
     * Find tariff rates by data source
     * @param dataSource The data source
     * @return List of tariff rates from the specified data source
     */
    List<TariffRate> findByDataSource(String dataSource);
    
    /**
     * Find tariff rates updated after a specific date
     * @param date The cutoff date
     * @return List of tariff rates updated after the cutoff
     */
    List<TariffRate> findByLastUpdatedAfter(LocalDateTime date);
    
    /**
     * Search for tariff rates by description keywords
     * @param keyword The keyword to search for
     * @return List of tariff rates with descriptions containing the keyword
     */
    @Query("SELECT t FROM TariffRate t WHERE LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<TariffRate> searchByDescriptionKeyword(@Param("keyword") String keyword);
}
