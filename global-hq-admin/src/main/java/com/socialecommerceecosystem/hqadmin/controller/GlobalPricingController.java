package com.gogidix.courier.hqadmin.controller;

import com.socialecommerceecosystem.hqadmin.model.GlobalPricing;
import com.socialecommerceecosystem.hqadmin.service.GlobalPricingService;
import com.socialecommerceecosystem.hqadmin.service.GlobalRegionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * REST controller for managing global pricing strategies.
 */
@RestController
@RequestMapping("/api/v1/pricing")
@RequiredArgsConstructor
@Slf4j
public class GlobalPricingController {

    private final GlobalPricingService globalPricingService;
    private final GlobalRegionService globalRegionService;

    /**
     * GET /api/v1/pricing : Get all pricing strategies
     * 
     * @return the ResponseEntity with status 200 (OK) and the list of pricing strategies
     */
    @GetMapping
    public ResponseEntity<List<GlobalPricing>> getAllPricing() {
        log.debug("REST request to get all global pricing strategies");
        return ResponseEntity.ok(globalPricingService.getAllPricing());
    }

    /**
     * GET /api/v1/pricing/active : Get all active pricing strategies
     * 
     * @return the ResponseEntity with status 200 (OK) and the list of active pricing strategies
     */
    @GetMapping("/active")
    public ResponseEntity<List<GlobalPricing>> getAllActivePricing() {
        log.debug("REST request to get all active global pricing strategies");
        return ResponseEntity.ok(globalPricingService.getAllActivePricing());
    }

    /**
     * GET /api/v1/pricing/{id} : Get a pricing strategy by id
     * 
     * @param id the id of the pricing strategy to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the pricing strategy, or with status 404 (Not Found)
     */
    @GetMapping("/{id}")
    public ResponseEntity<GlobalPricing> getPricing(@PathVariable Long id) {
        log.debug("REST request to get global pricing strategy : {}", id);
        return globalPricingService.getPricingById(id)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pricing strategy not found with id: " + id));
    }

    /**
     * GET /api/v1/pricing/code/{pricingCode} : Get a pricing strategy by code
     * 
     * @param pricingCode the code of the pricing strategy to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the pricing strategy, or with status 404 (Not Found)
     */
    @GetMapping("/code/{pricingCode}")
    public ResponseEntity<GlobalPricing> getPricingByCode(@PathVariable String pricingCode) {
        log.debug("REST request to get global pricing strategy by code: {}", pricingCode);
        return globalPricingService.getPricingByCode(pricingCode)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pricing strategy not found with code: " + pricingCode));
    }

    /**
     * GET /api/v1/pricing/service-type/{serviceType} : Get pricing strategies by service type
     * 
     * @param serviceType the service type of pricing strategies to retrieve
     * @return the ResponseEntity with status 200 (OK) and the list of pricing strategies for the service type
     */
    @GetMapping("/service-type/{serviceType}")
    public ResponseEntity<List<GlobalPricing>> getPricingByServiceType(@PathVariable String serviceType) {
        log.debug("REST request to get global pricing strategies by service type: {}", serviceType);
        return ResponseEntity.ok(globalPricingService.getPricingByServiceType(serviceType));
    }

    /**
     * GET /api/v1/pricing/region/{regionId} : Get pricing strategies for a region
     * 
     * @param regionId the id of the region to filter by
     * @return the ResponseEntity with status 200 (OK) and the list of pricing strategies for the region
     */
    @GetMapping("/region/{regionId}")
    public ResponseEntity<List<GlobalPricing>> getPricingByRegion(@PathVariable Long regionId) {
        log.debug("REST request to get global pricing strategies for region: {}", regionId);
        
        try {
            List<GlobalPricing> pricing = globalPricingService.getPricingByRegion(regionId);
            return ResponseEntity.ok(pricing);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * GET /api/v1/pricing/service-type/{serviceType}/region/{regionId} : Get pricing strategies for a service type and region
     * 
     * @param serviceType the service type to filter by
     * @param regionId the id of the region to filter by
     * @return the ResponseEntity with status 200 (OK) and the list of pricing strategies for the service type and region
     */
    @GetMapping("/service-type/{serviceType}/region/{regionId}")
    public ResponseEntity<List<GlobalPricing>> getPricingByServiceTypeAndRegion(
            @PathVariable String serviceType,
            @PathVariable Long regionId) {
        log.debug("REST request to get global pricing strategies for service type: {} and region: {}", serviceType, regionId);
        
        try {
            List<GlobalPricing> pricing = globalPricingService.getPricingByServiceTypeAndRegion(serviceType, regionId);
            return ResponseEntity.ok(pricing);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * GET /api/v1/pricing/with-overrides : Get pricing strategies that allow regional overrides
     * 
     * @return the ResponseEntity with status 200 (OK) and the list of pricing strategies that allow regional overrides
     */
    @GetMapping("/with-overrides")
    public ResponseEntity<List<GlobalPricing>> getPricingWithRegionalOverrides() {
        log.debug("REST request to get global pricing strategies with regional overrides");
        return ResponseEntity.ok(globalPricingService.getPricingWithRegionalOverrides());
    }

    /**
     * GET /api/v1/pricing/global-defaults : Get global default pricing strategies
     * 
     * @return the ResponseEntity with status 200 (OK) and the list of global default pricing strategies
     */
    @GetMapping("/global-defaults")
    public ResponseEntity<List<GlobalPricing>> getGlobalDefaultPricing() {
        log.debug("REST request to get global default pricing strategies");
        return ResponseEntity.ok(globalPricingService.getGlobalDefaultPricing());
    }

    /**
     * GET /api/v1/pricing/search : Search pricing strategies by name
     * 
     * @param searchText the text to search for in pricing strategy names
     * @return the ResponseEntity with status 200 (OK) and the list of matching pricing strategies
     */
    @GetMapping("/search")
    public ResponseEntity<List<GlobalPricing>> searchPricingByName(@RequestParam String searchText) {
        log.debug("REST request to search global pricing strategies by name containing: {}", searchText);
        return ResponseEntity.ok(globalPricingService.searchPricingByName(searchText));
    }

    /**
     * GET /api/v1/pricing/currency/{currencyCode} : Get pricing strategies by currency code
     * 
     * @param currencyCode the currency code of pricing strategies to retrieve
     * @return the ResponseEntity with status 200 (OK) and the list of pricing strategies using the specified currency
     */
    @GetMapping("/currency/{currencyCode}")
    public ResponseEntity<List<GlobalPricing>> getPricingByCurrencyCode(@PathVariable String currencyCode) {
        log.debug("REST request to get global pricing strategies by currency code: {}", currencyCode);
        return ResponseEntity.ok(globalPricingService.getPricingByCurrencyCode(currencyCode));
    }

    /**
     * GET /api/v1/pricing/below-price/{maxPrice} : Get pricing strategies below a specific base price
     * 
     * @param maxPrice the maximum base price
     * @return the ResponseEntity with status 200 (OK) and the list of pricing strategies below the specified price
     */
    @GetMapping("/below-price/{maxPrice}")
    public ResponseEntity<List<GlobalPricing>> getPricingBelowBasePrice(@PathVariable BigDecimal maxPrice) {
        log.debug("REST request to get global pricing strategies below base price: {}", maxPrice);
        return ResponseEntity.ok(globalPricingService.getPricingBelowBasePrice(maxPrice));
    }

    /**
     * GET /api/v1/pricing/effective-on/{date} : Get pricing strategies effective on a specific date
     * 
     * @param date the date to check
     * @return the ResponseEntity with status 200 (OK) and the list of pricing strategies effective on the specified date
     */
    @GetMapping("/effective-on/{date}")
    public ResponseEntity<List<GlobalPricing>> getPricingEffectiveOn(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.debug("REST request to get global pricing strategies effective on date: {}", date);
        return ResponseEntity.ok(globalPricingService.getPricingEffectiveOn(date));
    }

    /**
     * GET /api/v1/pricing/count-by-service-type : Count pricing strategies by service type
     * 
     * @return the ResponseEntity with status 200 (OK) and the list of count results by service type
     */
    @GetMapping("/count-by-service-type")
    public ResponseEntity<List<Object[]>> countPricingByServiceType() {
        log.debug("REST request to count global pricing strategies by service type");
        return ResponseEntity.ok(globalPricingService.countPricingByServiceType());
    }

    /**
     * POST /api/v1/pricing : Create a new pricing strategy
     * 
     * @param pricing the pricing strategy to create
     * @return the ResponseEntity with status 201 (Created) and with body the new pricing strategy
     */
    @PostMapping
    public ResponseEntity<GlobalPricing> createPricing(@Valid @RequestBody GlobalPricing pricing) {
        log.debug("REST request to save global pricing strategy : {}", pricing);
        if (pricing.getId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A new pricing strategy cannot already have an ID");
        }
        
        try {
            GlobalPricing result = globalPricingService.createPricing(pricing);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * PUT /api/v1/pricing/{id} : Update an existing pricing strategy
     * 
     * @param id the id of the pricing strategy to update
     * @param pricing the pricing strategy to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated pricing strategy
     */
    @PutMapping("/{id}")
    public ResponseEntity<GlobalPricing> updatePricing(
            @PathVariable Long id, 
            @Valid @RequestBody GlobalPricing pricing) {
        log.debug("REST request to update global pricing strategy : {}", pricing);
        if (pricing.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pricing strategy ID must not be null");
        }
        if (!id.equals(pricing.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "IDs don't match");
        }
        
        try {
            GlobalPricing result = globalPricingService.updatePricing(id, pricing);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * DELETE /api/v1/pricing/{id} : Delete a pricing strategy
     * 
     * @param id the id of the pricing strategy to delete
     * @return the ResponseEntity with status 204 (NO_CONTENT)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePricing(@PathVariable Long id) {
        log.debug("REST request to delete global pricing strategy : {}", id);
        try {
            globalPricingService.deletePricing(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * POST /api/v1/pricing/{pricingId}/apply-to-region/{regionId} : Apply a global pricing strategy to a region
     * 
     * @param pricingId the id of the global pricing strategy to apply
     * @param regionId the id of the region to apply the pricing strategy to
     * @return the ResponseEntity with status 200 (OK) and with body the newly created regional pricing strategy
     */
    @PostMapping("/{pricingId}/apply-to-region/{regionId}")
    public ResponseEntity<GlobalPricing> applyGlobalPricingToRegion(
            @PathVariable Long pricingId,
            @PathVariable Long regionId) {
        log.debug("REST request to apply global pricing strategy {} to region {}", pricingId, regionId);
        
        try {
            GlobalPricing result = globalPricingService.applyGlobalPricingToRegion(pricingId, regionId);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
