package com.exalt.integration.common.service.impl;

import com.exalt.integration.common.exception.IntegrationException;
import com.exalt.integration.common.model.Provider;
import com.exalt.integration.common.service.ProviderService;
import com.exalt.integration.common.service.ShippingProviderAdapter;
import com.exalt.integration.common.service.ShippingProviderAdapterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Implementation of ShippingProviderAdapterRegistry that manages shipping provider adapters.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ShippingProviderAdapterRegistryImpl implements ShippingProviderAdapterRegistry {

    private final Map<String, ShippingProviderAdapter> adapterMap = new ConcurrentHashMap<>();
    private final ProviderService providerService;
    private final List<ShippingProviderAdapter> adapters;

    /**
     * Initialize the registry with all available adapters
     */
    @PostConstruct
    public void init() {
        // Register all available adapters
        if (adapters != null && !adapters.isEmpty()) {
            adapters.forEach(this::registerAdapter);
            log.info("Initialized ShippingProviderAdapterRegistry with {} adapters", adapterMap.size());
        } else {
            log.warn("No ShippingProviderAdapter implementations found during initialization");
        }
    }

    @Override
    public void registerAdapter(ShippingProviderAdapter adapter) {
        String providerCode = adapter.getProviderCode().toUpperCase();
        adapterMap.put(providerCode, adapter);
        log.info("Registered shipping provider adapter for: {}", providerCode);
    }

    @Override
    public Optional<ShippingProviderAdapter> getAdapter(String providerCode) {
        if (providerCode == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(adapterMap.get(providerCode.toUpperCase()));
    }

    @Override
    public Optional<ShippingProviderAdapter> getAdapter(Provider provider) {
        if (provider == null || provider.getProviderCode() == null) {
            return Optional.empty();
        }
        return getAdapter(provider.getProviderCode());
    }

    @Override
    public List<ShippingProviderAdapter> getAllAdapters() {
        return List.copyOf(adapterMap.values());
    }

    @Override
    public List<ShippingProviderAdapter> getAdaptersSupportingFeature(ShippingProviderAdapter.ProviderFeature feature) {
        return adapterMap.values().stream()
                .filter(adapter -> adapter.supportsFeature(feature))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ShippingProviderAdapter> getDefaultAdapter() throws IntegrationException {
        // Get the default provider from the provider service
        Optional<Provider> defaultProvider = providerService.getDefaultProvider();
        
        if (defaultProvider.isEmpty()) {
            log.warn("No default provider configured");
            return Optional.empty();
        }
        
        String providerCode = defaultProvider.get().getProviderCode();
        Optional<ShippingProviderAdapter> adapter = getAdapter(providerCode);
        
        if (adapter.isEmpty()) {
            log.error("No adapter found for default provider: {}", providerCode);
            throw new IntegrationException("No adapter found for default provider: " + providerCode);
        }
        
        return adapter;
    }

    @Override
    public boolean hasAdapter(String providerCode) {
        if (providerCode == null) {
            return false;
        }
        return adapterMap.containsKey(providerCode.toUpperCase());
    }

    @Override
    public boolean unregisterAdapter(String providerCode) {
        if (providerCode == null) {
            return false;
        }
        
        ShippingProviderAdapter removed = adapterMap.remove(providerCode.toUpperCase());
        if (removed != null) {
            log.info("Unregistered shipping provider adapter for: {}", providerCode);
            return true;
        }
        
        return false;
    }
}
