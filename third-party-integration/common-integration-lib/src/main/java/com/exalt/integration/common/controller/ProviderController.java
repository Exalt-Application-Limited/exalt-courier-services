package com.exalt.integration.common.controller;

import com.exalt.integration.common.model.Provider;
import com.exalt.integration.common.service.ProviderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * REST controller for managing shipping providers
 */
@RestController
@RequestMapping("/api/v1/providers")
@RequiredArgsConstructor
@Slf4j
public class ProviderController {

    private final ProviderService providerService;

    /**
     * Get all providers
     * @return List of all providers
     */
    @GetMapping
    public ResponseEntity<List<Provider>> getAllProviders() {
        log.info("REST request to get all providers");
        return ResponseEntity.ok(providerService.getAllProviders());
    }

    /**
     * Get a provider by ID
     * @param id Provider ID
     * @return Provider if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Provider> getProviderById(@PathVariable Long id) {
        log.info("REST request to get provider with id: {}", id);
        return providerService.getProviderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get a provider by code
     * @param code Provider code
     * @return Provider if found
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<Provider> getProviderByCode(@PathVariable String code) {
        log.info("REST request to get provider with code: {}", code);
        return providerService.getProviderByCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all enabled providers
     * @return List of enabled providers
     */
    @GetMapping("/enabled")
    public ResponseEntity<List<Provider>> getEnabledProviders() {
        log.info("REST request to get all enabled providers");
        return ResponseEntity.ok(providerService.getEnabledProviders());
    }

    /**
     * Create a new provider
     * @param provider Provider to create
     * @return Created provider
     */
    @PostMapping
    public ResponseEntity<Provider> createProvider(@Valid @RequestBody Provider provider) {
        log.info("REST request to create provider: {}", provider.getProviderCode());
        
        if (provider.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new provider cannot already have an ID").build();
        }
        
        Provider result = providerService.createProvider(provider);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * Update an existing provider
     * @param id Provider ID
     * @param provider Updated provider data
     * @return Updated provider
     */
    @PutMapping("/{id}")
    public ResponseEntity<Provider> updateProvider(
            @PathVariable Long id,
            @Valid @RequestBody Provider provider) {
        log.info("REST request to update provider with id: {}", id);
        
        if (provider.getId() == null) {
            return ResponseEntity.badRequest().header("Failure", "ID must be provided for update").build();
        }
        
        if (!id.equals(provider.getId())) {
            return ResponseEntity.badRequest().header("Failure", "ID in path must match ID in request body").build();
        }
        
        Provider result = providerService.updateProvider(id, provider);
        return ResponseEntity.ok(result);
    }

    /**
     * Delete a provider
     * @param id Provider ID
     * @return No content if successful
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProvider(@PathVariable Long id) {
        log.info("REST request to delete provider with id: {}", id);
        providerService.deleteProvider(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Set a provider as the default
     * @param id Provider ID
     * @return Updated provider
     */
    @PutMapping("/{id}/default")
    public ResponseEntity<Provider> setDefaultProvider(@PathVariable Long id) {
        log.info("REST request to set provider with id {} as default", id);
        Provider result = providerService.setDefaultProvider(id);
        return ResponseEntity.ok(result);
    }

    /**
     * Get providers by type
     * @param type Provider type
     * @return List of providers of the specified type
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Provider>> getProvidersByType(@PathVariable Provider.ProviderType type) {
        log.info("REST request to get providers of type: {}", type);
        return ResponseEntity.ok(providerService.getProvidersByType(type));
    }

    /**
     * Get providers supporting a specific country
     * @param countryCode ISO country code
     * @return List of providers supporting the country
     */
    @GetMapping("/country/{countryCode}")
    public ResponseEntity<List<Provider>> getProvidersSupportingCountry(@PathVariable String countryCode) {
        log.info("REST request to get providers supporting country: {}", countryCode);
        return ResponseEntity.ok(providerService.getProvidersSupportingCountry(countryCode));
    }

    /**
     * Update service mappings for a provider
     * @param id Provider ID
     * @param mappings Map of service type to provider-specific service code
     * @return Updated provider
     */
    @PutMapping("/{id}/service-mappings")
    public ResponseEntity<Provider> updateServiceMappings(
            @PathVariable Long id,
            @RequestBody Map<String, String> mappings) {
        log.info("REST request to update service mappings for provider with id: {}", id);
        Provider result = providerService.updateServiceMappings(id, mappings);
        return ResponseEntity.ok(result);
    }

    /**
     * Update supported countries for a provider
     * @param id Provider ID
     * @param countries Map of country codes to country names
     * @return Updated provider
     */
    @PutMapping("/{id}/supported-countries")
    public ResponseEntity<Provider> updateSupportedCountries(
            @PathVariable Long id,
            @RequestBody Map<String, String> countries) {
        log.info("REST request to update supported countries for provider with id: {}", id);
        Provider result = providerService.updateSupportedCountries(id, countries);
        return ResponseEntity.ok(result);
    }

    /**
     * Enable or disable a provider
     * @param id Provider ID
     * @param enabled true to enable, false to disable
     * @return Updated provider
     */
    @PutMapping("/{id}/enabled")
    public ResponseEntity<Provider> setProviderEnabled(
            @PathVariable Long id,
            @RequestParam boolean enabled) {
        log.info("REST request to set provider with id {} enabled status to: {}", id, enabled);
        Provider result = providerService.setProviderEnabled(id, enabled);
        return ResponseEntity.ok(result);
    }
}
