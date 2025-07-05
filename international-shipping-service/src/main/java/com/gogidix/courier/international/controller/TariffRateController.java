package com.gogidix.courierservices.international-shipping.$1;

import com.gogidix.courier.international.model.TariffRate;
import com.gogidix.courier.international.service.TariffRateService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

/**
 * REST controller for managing tariff rates.
 */
@RestController
@RequestMapping("/api/international/tariffs")
@Slf4j
public class TariffRateController {

    private final TariffRateService tariffService;

    @Autowired
    public TariffRateController(TariffRateService tariffService) {
        this.tariffService = tariffService;
    }

    /**
     * Create a new tariff rate
     * 
     * @param tariffRate The tariff rate to create
     * @return The created tariff rate
     */
    @PostMapping
    public ResponseEntity<TariffRate> createTariffRate(@Valid @RequestBody TariffRate tariffRate) {
        log.info("REST request to create tariff rate for {} and HS code {}", 
                tariffRate.getDestinationCountryCode(), tariffRate.getHsCode());
        TariffRate result = tariffService.createTariffRate(tariffRate);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * Update an existing tariff rate
     * 
     * @param id The ID of the tariff rate to update
     * @param tariffRate The updated tariff rate
     * @return The updated tariff rate
     */
    @PutMapping("/{id}")
    public ResponseEntity<TariffRate> updateTariffRate(
            @PathVariable Long id,
            @Valid @RequestBody TariffRate tariffRate) {
        log.info("REST request to update tariff rate: {}", id);
        TariffRate result = tariffService.updateTariffRate(id, tariffRate);
        return ResponseEntity.ok(result);
    }

    /**
     * Get a tariff rate by its ID
     * 
     * @param id The tariff rate ID
     * @return The tariff rate
     */
    @GetMapping("/{id}")
    public ResponseEntity<TariffRate> getTariffRateById(@PathVariable Long id) {
        log.info("REST request to get tariff rate by ID: {}", id);
        return tariffService.getTariffRateById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tariff rate not found"));
    }

    /**
     * Get a tariff rate by destination country and HS code
     * 
     * @param countryCode The destination country code
     * @param hsCode The HS code
     * @return The tariff rate
     */
    @GetMapping("/country/{countryCode}/hs-code/{hsCode}")
    public ResponseEntity<TariffRate> getTariffRateByCountryAndHsCode(
            @PathVariable String countryCode,
            @PathVariable String hsCode) {
        log.info("REST request to get tariff rate for country {} and HS code {}", countryCode, hsCode);
        return tariffService.getTariffRateByCountryAndHsCode(countryCode, hsCode)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tariff rate not found"));
    }

    /**
     * Get all tariff rates for a destination country
     * 
     * @param countryCode The destination country code
     * @return List of tariff rates for the country
     */
    @GetMapping("/country/{countryCode}")
    public ResponseEntity<List<TariffRate>> getTariffRatesByCountry(@PathVariable String countryCode) {
        log.info("REST request to get all tariff rates for country: {}", countryCode);
        List<TariffRate> tariffRates = tariffService.getTariffRatesByCountry(countryCode);
        return ResponseEntity.ok(tariffRates);
    }

    /**
     * Get all tariff rates for a specific HS code
     * 
     * @param hsCode The HS code
     * @return List of tariff rates for the HS code
     */
    @GetMapping("/hs-code/{hsCode}")
    public ResponseEntity<List<TariffRate>> getTariffRatesByHsCode(@PathVariable String hsCode) {
        log.info("REST request to get all tariff rates for HS code: {}", hsCode);
        List<TariffRate> tariffRates = tariffService.getTariffRatesByHsCode(hsCode);
        return ResponseEntity.ok(tariffRates);
    }

    /**
     * Calculate duties and taxes for a shipment
     * 
     * @param destinationCountryCode The destination country code
     * @param items Map of HS codes to declared values
     * @param currencyCode Currency code of the declared values
     * @return Map of HS codes to calculated duties and taxes
     */
    @PostMapping("/calculate")
    public ResponseEntity<Map<String, Map<String, Double>>> calculateDutiesAndTaxes(
            @RequestParam String destinationCountryCode,
            @RequestBody Map<String, Double> items,
            @RequestParam String currencyCode) {
        log.info("REST request to calculate duties and taxes for shipment to {}", destinationCountryCode);
        Map<String, Map<String, Double>> calculations = tariffService.calculateDutiesAndTaxes(
                destinationCountryCode, items, currencyCode);
        return ResponseEntity.ok(calculations);
    }

    /**
     * Check if a product is restricted for export to a country
     * 
     * @param destinationCountryCode The destination country code
     * @param hsCode The HS code of the product
     * @return true if the product is restricted, false otherwise
     */
    @GetMapping("/check-restriction")
    public ResponseEntity<Boolean> isProductRestricted(
            @RequestParam String destinationCountryCode,
            @RequestParam String hsCode) {
        log.info("REST request to check if product with HS code {} is restricted for country: {}",
                hsCode, destinationCountryCode);
        boolean isRestricted = tariffService.isProductRestricted(destinationCountryCode, hsCode);
        return ResponseEntity.ok(isRestricted);
    }

    /**
     * Get tariff rates with duty-free thresholds
     * 
     * @return List of tariff rates with duty-free thresholds
     */
    @GetMapping("/with-duty-free-threshold")
    public ResponseEntity<List<TariffRate>> getTariffRatesWithDutyFreeThreshold() {
        log.info("REST request to get all tariff rates with duty-free thresholds");
        List<TariffRate> tariffRates = tariffService.getTariffRatesWithDutyFreeThreshold();
        return ResponseEntity.ok(tariffRates);
    }

    /**
     * Get restriction notes for a product in a country
     * 
     * @param destinationCountryCode The destination country code
     * @param hsCode The HS code of the product
     * @return Restriction notes if the product is restricted, empty string otherwise
     */
    @GetMapping("/restriction-notes")
    public ResponseEntity<String> getRestrictionNotes(
            @RequestParam String destinationCountryCode,
            @RequestParam String hsCode) {
        log.info("REST request to get restriction notes for HS code {} in country: {}",
                hsCode, destinationCountryCode);
        String notes = tariffService.getRestrictionNotes(destinationCountryCode, hsCode);
        return ResponseEntity.ok(notes);
    }

    /**
     * Search for tariff rates by description keywords
     * 
     * @param keyword The keyword to search for
     * @return List of matching tariff rates
     */
    @GetMapping("/search")
    public ResponseEntity<List<TariffRate>> searchTariffRatesByKeyword(@RequestParam String keyword) {
        log.info("REST request to search tariff rates by keyword: {}", keyword);
        List<TariffRate> searchResults = tariffService.searchTariffRatesByKeyword(keyword);
        return ResponseEntity.ok(searchResults);
    }

    /**
     * Get tariff rates with high duty rates (above a threshold)
     * 
     * @param threshold The minimum duty rate percentage to consider as high
     * @return List of tariff rates with high duty rates
     */
    @GetMapping("/high-duty-rates")
    public ResponseEntity<List<TariffRate>> getHighDutyRates(@RequestParam Double threshold) {
        log.info("REST request to get tariff rates with duty rates above: {}", threshold);
        List<TariffRate> highDutyRates = tariffService.getHighDutyRates(threshold);
        return ResponseEntity.ok(highDutyRates);
    }

    /**
     * Check if a shipment is below the duty-free threshold for a country
     * 
     * @param destinationCountryCode The destination country code
     * @param totalValue The total declared value of the shipment
     * @param currencyCode Currency code of the declared value
     * @return true if the shipment is below the duty-free threshold, false otherwise
     */
    @GetMapping("/check-duty-free-threshold")
    public ResponseEntity<Boolean> isBelowDutyFreeThreshold(
            @RequestParam String destinationCountryCode,
            @RequestParam Double totalValue,
            @RequestParam String currencyCode) {
        log.info("REST request to check if shipment value {} {} is below duty-free threshold for country: {}",
                totalValue, currencyCode, destinationCountryCode);
        boolean isBelowThreshold = tariffService.isBelowDutyFreeThreshold(
                destinationCountryCode, totalValue, currencyCode);
        return ResponseEntity.ok(isBelowThreshold);
    }

    /**
     * Import tariff rates from an external source
     * 
     * @param importInfo Map containing source URL and data source name
     * @return Number of tariff rates imported
     */
    @PostMapping("/import")
    public ResponseEntity<Integer> importTariffRatesFromExternalSource(@RequestBody Map<String, String> importInfo) {
        String sourceUrl = importInfo.get("sourceUrl");
        String dataSource = importInfo.get("dataSource");
        
        log.info("REST request to import tariff rates from source: {}", sourceUrl);
        int importedCount = tariffService.importTariffRatesFromExternalSource(sourceUrl, dataSource);
        return ResponseEntity.ok(importedCount);
    }

    /**
     * Get currently valid tariff rates (based on validFrom and validUntil dates)
     * 
     * @return List of currently valid tariff rates
     */
    @GetMapping("/current")
    public ResponseEntity<List<TariffRate>> getCurrentlyValidTariffRates() {
        log.info("REST request to get currently valid tariff rates");
        List<TariffRate> currentRates = tariffService.getCurrentlyValidTariffRates();
        return ResponseEntity.ok(currentRates);
    }

    /**
     * Delete a tariff rate
     * 
     * @param id The ID of the tariff rate to delete
     * @return Status message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTariffRate(@PathVariable Long id) {
        log.info("REST request to delete tariff rate: {}", id);
        boolean result = tariffService.deleteTariffRate(id);
        return result ? ResponseEntity.noContent().build() : 
                ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
