package com.exalt.courierservices.international-shipping.$1;

import com.exalt.courier.international.model.TariffRate;
import com.exalt.courier.international.repository.TariffRateRepository;
import com.exalt.courier.international.service.TariffRateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Implementation of the TariffRateService interface.
 * Provides functionality for managing tariff rates and calculating duties and taxes.
 */
@Service
@Slf4j
public class TariffRateServiceImpl implements TariffRateService {

    private final TariffRateRepository tariffRateRepository;
    private final RestTemplate restTemplate;

    @Autowired
    public TariffRateServiceImpl(TariffRateRepository tariffRateRepository, RestTemplate restTemplate) {
        this.tariffRateRepository = tariffRateRepository;
        this.restTemplate = restTemplate;
    }

    @Override
    @Transactional
    public TariffRate createTariffRate(TariffRate tariffRate) {
        log.info("Creating tariff rate for {} and HS code {}", 
                tariffRate.getDestinationCountryCode(), tariffRate.getHsCode());
        
        // Check if a tariff rate already exists for this country and HS code
        Optional<TariffRate> existingRate = tariffRateRepository.findByDestinationCountryCodeAndHsCode(
                tariffRate.getDestinationCountryCode(), tariffRate.getHsCode());
        
        if (existingRate.isPresent()) {
            throw new IllegalArgumentException("Tariff rate already exists for country " + 
                    tariffRate.getDestinationCountryCode() + " and HS code " + tariffRate.getHsCode());
        }
        
        // Set timestamps if not provided
        if (tariffRate.getLastUpdated() == null) {
            tariffRate.setLastUpdated(LocalDateTime.now());
        }
        
        if (tariffRate.getValidFrom() == null) {
            tariffRate.setValidFrom(LocalDateTime.now());
        }
        
        return tariffRateRepository.save(tariffRate);
    }

    @Override
    @Transactional
    public TariffRate updateTariffRate(Long id, TariffRate tariffRate) {
        log.info("Updating tariff rate: {}", id);
        
        TariffRate existingRate = tariffRateRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Tariff rate not found with ID: " + id));
        
        // Update fields
        existingRate.setDescription(tariffRate.getDescription());
        existingRate.setDutyRate(tariffRate.getDutyRate());
        existingRate.setTaxRate(tariffRate.getTaxRate());
        existingRate.setTaxType(tariffRate.getTaxType());
        existingRate.setDutyFreeThreshold(tariffRate.getDutyFreeThreshold());
        existingRate.setThresholdCurrencyCode(tariffRate.getThresholdCurrencyCode());
        existingRate.setRestricted(tariffRate.isRestricted());
        existingRate.setRestrictionNotes(tariffRate.getRestrictionNotes());
        existingRate.setOfficialReferenceUrl(tariffRate.getOfficialReferenceUrl());
        existingRate.setValidUntil(tariffRate.getValidUntil());
        existingRate.setDataSource(tariffRate.getDataSource());
        
        // Update last updated timestamp
        existingRate.setLastUpdated(LocalDateTime.now());
        
        return tariffRateRepository.save(existingRate);
    }

    @Override
    public Optional<TariffRate> getTariffRateById(Long id) {
        log.info("Getting tariff rate by ID: {}", id);
        return tariffRateRepository.findById(id);
    }

    @Override
    public Optional<TariffRate> getTariffRateByCountryAndHsCode(String destinationCountryCode, String hsCode) {
        log.info("Getting tariff rate for country {} and HS code {}", destinationCountryCode, hsCode);
        return tariffRateRepository.findByDestinationCountryCodeAndHsCode(destinationCountryCode, hsCode);
    }

    @Override
    public List<TariffRate> getTariffRatesByCountry(String destinationCountryCode) {
        log.info("Getting all tariff rates for country: {}", destinationCountryCode);
        return tariffRateRepository.findByDestinationCountryCode(destinationCountryCode);
    }

    @Override
    public List<TariffRate> getTariffRatesByHsCode(String hsCode) {
        log.info("Getting all tariff rates for HS code: {}", hsCode);
        return tariffRateRepository.findByHsCode(hsCode);
    }

    @Override
    public Map<String, Map<String, Double>> calculateDutiesAndTaxes(
            String destinationCountryCode, Map<String, Double> items, String currencyCode) {
        log.info("Calculating duties and taxes for shipment to {}", destinationCountryCode);
        
        Map<String, Map<String, Double>> result = new HashMap<>();
        double totalValue = items.values().stream().mapToDouble(Double::doubleValue).sum();
        
        // Check if the total value is below the duty-free threshold for the country
        boolean isBelowThreshold = isBelowDutyFreeThreshold(destinationCountryCode, totalValue, currencyCode);
        
        for (Map.Entry<String, Double> entry : items.entrySet()) {
            String hsCode = entry.getKey();
            Double declaredValue = entry.getValue();
            
            Optional<TariffRate> rateOpt = tariffRateRepository.findByDestinationCountryCodeAndHsCode(
                    destinationCountryCode, hsCode);
            
            if (rateOpt.isPresent()) {
                TariffRate rate = rateOpt.get();
                Map<String, Double> calculation = new HashMap<>();
                
                // If the total shipment value is below the duty-free threshold, no duties apply
                if (isBelowThreshold) {
                    calculation.put("dutyAmount", 0.0);
                } else {
                    calculation.put("dutyAmount", rate.calculateDuty(declaredValue));
                }
                
                calculation.put("taxAmount", rate.calculateTax(
                        declaredValue, calculation.get("dutyAmount")));
                calculation.put("totalCharges", 
                        calculation.get("dutyAmount") + calculation.get("taxAmount"));
                
                calculation.put("dutyRate", rate.getDutyRate());
                if (rate.getTaxRate() != null) {
                    calculation.put("taxRate", rate.getTaxRate());
                }
                
                result.put(hsCode, calculation);
            } else {
                // If no specific rate is found, use default rates (e.g., 0% duty, 0% tax)
                Map<String, Double> calculation = new HashMap<>();
                calculation.put("dutyAmount", 0.0);
                calculation.put("taxAmount", 0.0);
                calculation.put("totalCharges", 0.0);
                calculation.put("dutyRate", 0.0);
                calculation.put("taxRate", 0.0);
                
                result.put(hsCode, calculation);
            }
        }
        
        return result;
    }

    @Override
    public boolean isProductRestricted(String destinationCountryCode, String hsCode) {
        log.info("Checking if product with HS code {} is restricted for country: {}", 
                hsCode, destinationCountryCode);
        
        Optional<TariffRate> rateOpt = tariffRateRepository.findByDestinationCountryCodeAndHsCode(
                destinationCountryCode, hsCode);
        
        return rateOpt.isPresent() && rateOpt.get().isRestricted();
    }

    @Override
    public List<TariffRate> getTariffRatesWithDutyFreeThreshold() {
        log.info("Getting all tariff rates with duty-free thresholds");
        return tariffRateRepository.findWithDutyFreeThreshold();
    }

    @Override
    public String getRestrictionNotes(String destinationCountryCode, String hsCode) {
        log.info("Getting restriction notes for HS code {} in country: {}", 
                hsCode, destinationCountryCode);
        
        Optional<TariffRate> rateOpt = tariffRateRepository.findByDestinationCountryCodeAndHsCode(
                destinationCountryCode, hsCode);
        
        if (rateOpt.isPresent() && rateOpt.get().isRestricted() && rateOpt.get().getRestrictionNotes() != null) {
            return rateOpt.get().getRestrictionNotes();
        }
        
        return "";
    }

    @Override
    public List<TariffRate> searchTariffRatesByKeyword(String keyword) {
        log.info("Searching tariff rates by keyword: {}", keyword);
        return tariffRateRepository.searchByDescriptionKeyword(keyword);
    }

    @Override
    public List<TariffRate> getHighDutyRates(Double threshold) {
        log.info("Getting tariff rates with duty rates above: {}", threshold);
        return tariffRateRepository.findByDutyRateGreaterThan(threshold);
    }

    @Override
    public boolean isBelowDutyFreeThreshold(String destinationCountryCode, Double totalValue, String currencyCode) {
        log.info("Checking if value {} {} is below duty-free threshold for country: {}", 
                totalValue, currencyCode, destinationCountryCode);
        
        // Get all tariff rates for the country to find the general duty-free threshold
        List<TariffRate> rates = tariffRateRepository.findByDestinationCountryCode(destinationCountryCode);
        
        // Look for a general threshold applicable to all products
        for (TariffRate rate : rates) {
            if (rate.getDutyFreeThreshold() != null && 
                    currencyCode.equals(rate.getThresholdCurrencyCode())) {
                
                // Found a threshold in the same currency
                return totalValue <= rate.getDutyFreeThreshold();
            }
        }
        
        // If no country-specific threshold is found, return false (assume duties apply)
        return false;
    }

    @Override
    @Transactional
    public boolean deleteTariffRate(Long id) {
        log.info("Deleting tariff rate: {}", id);
        
        if (!tariffRateRepository.existsById(id)) {
            return false;
        }
        
        tariffRateRepository.deleteById(id);
        return true;
    }

    @Override
    @Transactional
    public int importTariffRatesFromExternalSource(String sourceUrl, String dataSource) {
        log.info("Importing tariff rates from source: {}", sourceUrl);
        
        try {
            // In a real implementation, this would fetch data from an external source
            // For now, we'll just return a dummy success value
            return 0;
        } catch (Exception e) {
            log.error("Error importing tariff rates", e);
            throw new IllegalStateException("Error importing tariff rates: " + e.getMessage());
        }
    }

    @Override
    public List<TariffRate> getCurrentlyValidTariffRates() {
        log.info("Getting currently valid tariff rates");
        return tariffRateRepository.findCurrentlyValidRates(LocalDateTime.now());
    }
}
