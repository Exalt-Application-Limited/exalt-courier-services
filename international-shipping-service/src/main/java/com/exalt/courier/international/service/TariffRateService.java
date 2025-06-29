package com.exalt.courierservices.international-shipping.$1;

import com.exalt.courier.international.model.TariffRate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service interface for managing tariff rates.
 */
public interface TariffRateService {

    /**
     * Create a new tariff rate
     * @param tariffRate The tariff rate to create
     * @return The created tariff rate with ID assigned
     */
    TariffRate createTariffRate(TariffRate tariffRate);
    
    /**
     * Update an existing tariff rate
     * @param id The ID of the tariff rate to update
     * @param tariffRate The updated tariff rate
     * @return The updated tariff rate
     */
    TariffRate updateTariffRate(Long id, TariffRate tariffRate);
    
    /**
     * Get a tariff rate by its ID
     * @param id The tariff rate ID
     * @return The tariff rate if found
     */
    Optional<TariffRate> getTariffRateById(Long id);
    
    /**
     * Get a tariff rate by destination country and HS code
     * @param destinationCountryCode The destination country code
     * @param hsCode The HS code
     * @return The tariff rate if found
     */
    Optional<TariffRate> getTariffRateByCountryAndHsCode(String destinationCountryCode, String hsCode);
    
    /**
     * Get all tariff rates for a destination country
     * @param destinationCountryCode The destination country code
     * @return List of tariff rates for the country
     */
    List<TariffRate> getTariffRatesByCountry(String destinationCountryCode);
    
    /**
     * Get all tariff rates for a specific HS code
     * @param hsCode The HS code
     * @return List of tariff rates for the HS code
     */
    List<TariffRate> getTariffRatesByHsCode(String hsCode);
    
    /**
     * Calculate duties and taxes for a shipment
     * @param destinationCountryCode The destination country code
     * @param items Map of HS codes to declared values
     * @param currencyCode Currency code of the declared values
     * @return Map of HS codes to calculated duties and taxes
     */
    Map<String, Map<String, Double>> calculateDutiesAndTaxes(
            String destinationCountryCode, 
            Map<String, Double> items, 
            String currencyCode);
    
    /**
     * Check if a product is restricted for export to a country
     * @param destinationCountryCode The destination country code
     * @param hsCode The HS code of the product
     * @return true if the product is restricted, false otherwise
     */
    boolean isProductRestricted(String destinationCountryCode, String hsCode);
    
    /**
     * Get tariff rates with duty-free thresholds
     * @return List of tariff rates with duty-free thresholds
     */
    List<TariffRate> getTariffRatesWithDutyFreeThreshold();
    
    /**
     * Get restriction notes for a product in a country
     * @param destinationCountryCode The destination country code
     * @param hsCode The HS code of the product
     * @return Restriction notes if the product is restricted, empty string otherwise
     */
    String getRestrictionNotes(String destinationCountryCode, String hsCode);
    
    /**
     * Search for tariff rates by description keywords
     * @param keyword The keyword to search for
     * @return List of matching tariff rates
     */
    List<TariffRate> searchTariffRatesByKeyword(String keyword);
    
    /**
     * Get tariff rates with high duty rates (above a threshold)
     * @param threshold The minimum duty rate percentage to consider as high
     * @return List of tariff rates with high duty rates
     */
    List<TariffRate> getHighDutyRates(Double threshold);
    
    /**
     * Check if a shipment is below the duty-free threshold for a country
     * @param destinationCountryCode The destination country code
     * @param totalValue The total declared value of the shipment
     * @param currencyCode Currency code of the declared value
     * @return true if the shipment is below the duty-free threshold, false otherwise
     */
    boolean isBelowDutyFreeThreshold(String destinationCountryCode, Double totalValue, String currencyCode);
    
    /**
     * Delete a tariff rate
     * @param id The ID of the tariff rate to delete
     * @return true if successfully deleted, false otherwise
     */
    boolean deleteTariffRate(Long id);
    
    /**
     * Import tariff rates from an external source
     * @param sourceUrl URL of the external source
     * @param dataSource Name of the data source for attribution
     * @return Number of tariff rates imported
     */
    int importTariffRatesFromExternalSource(String sourceUrl, String dataSource);
    
    /**
     * Get currently valid tariff rates (based on validFrom and validUntil dates)
     * @return List of currently valid tariff rates
     */
    List<TariffRate> getCurrentlyValidTariffRates();
}
