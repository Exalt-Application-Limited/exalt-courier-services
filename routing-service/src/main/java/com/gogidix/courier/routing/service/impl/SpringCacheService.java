package com.gogidix.courier.routing.service.impl;

import com.gogidix.courier.routing.model.Location;
import com.gogidix.courier.routing.model.Route;
import com.gogidix.courier.routing.service.CacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of the CacheService using Spring's cache abstraction.
 */
@Service
public class SpringCacheService implements CacheService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringCacheService.class);
    
    private static final String ROUTES_CACHE = "routes";
    private static final String TRAVEL_TIMES_CACHE = "travelTimes";
    private static final String NEARBY_COURIERS_CACHE = "nearbyCouriers";
    private static final String ETA_CACHE = "eta";
    
    private final CacheManager cacheManager;
    private final Map<String, ExpiringValue<Integer>> travelTimesCache = new ConcurrentHashMap<>();
    private final Map<String, ExpiringValue<List<String>>> courierCache = new ConcurrentHashMap<>();
    private final Map<String, ExpiringValue<LocalDateTime>> etaCache = new ConcurrentHashMap<>();
    
    public SpringCacheService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }
    
    @Override
    public void cacheRoute(Route route) {
        Cache cache = cacheManager.getCache(ROUTES_CACHE);
        if (cache != null) {
            cache.put(route.getId(), route);
            LOGGER.debug("Cached route: {}", route.getId());
        }
    }
    
    @Override
    @Cacheable(cacheNames = ROUTES_CACHE, key = "#routeId", unless = "#result == null")
    public Optional<Route> getCachedRoute(Long routeId) {
        Cache cache = cacheManager.getCache(ROUTES_CACHE);
        if (cache != null) {
            Cache.ValueWrapper wrapper = cache.get(routeId);
            if (wrapper != null) {
                Route route = (Route) wrapper.get();
                LOGGER.debug("Cache hit for route: {}", routeId);
                return Optional.of(route);
            }
        }
        LOGGER.debug("Cache miss for route: {}", routeId);
        return Optional.empty();
    }
    
    @Override
    public void cacheTravelTime(double originLat, double originLng, 
                              double destLat, double destLng, 
                              int travelTimeSeconds, int expirationMinutes) {
        String key = createLocationKey(originLat, originLng, destLat, destLng);
        travelTimesCache.put(key, new ExpiringValue<>(
                travelTimeSeconds, 
                System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(expirationMinutes)
        ));
        LOGGER.debug("Cached travel time for path {}: {} seconds", key, travelTimeSeconds);
    }
    
    @Override
    public int getCachedTravelTime(double originLat, double originLng, 
                                 double destLat, double destLng) {
        String key = createLocationKey(originLat, originLng, destLat, destLng);
        ExpiringValue<Integer> value = travelTimesCache.get(key);
        
        if (value != null && !value.isExpired()) {
            LOGGER.debug("Cache hit for travel time: {}", key);
            return value.getValue();
        }
        
        LOGGER.debug("Cache miss for travel time: {}", key);
        return -1;
    }
    
    @Override
    public void cacheNearbyCouriers(Location location, double radiusKm, 
                                  List<String> courierIds, int expirationSeconds) {
        String key = createLocationRadiusKey(location, radiusKm);
        courierCache.put(key, new ExpiringValue<>(
                new ArrayList<>(courierIds), 
                System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(expirationSeconds)
        ));
        LOGGER.debug("Cached {} couriers near {}", courierIds.size(), key);
    }
    
    @Override
    public List<String> getCachedNearbyCouriers(Location location, double radiusKm) {
        String key = createLocationRadiusKey(location, radiusKm);
        ExpiringValue<List<String>> value = courierCache.get(key);
        
        if (value != null && !value.isExpired()) {
            LOGGER.debug("Cache hit for nearby couriers: {}", key);
            return value.getValue();
        }
        
        LOGGER.debug("Cache miss for nearby couriers: {}", key);
        return Collections.emptyList();
    }
    
    @Override
    public void cacheEta(String shipmentId, LocalDateTime eta, int expirationMinutes) {
        etaCache.put(shipmentId, new ExpiringValue<>(
                eta, 
                System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(expirationMinutes)
        ));
        LOGGER.debug("Cached ETA for shipment {}: {}", shipmentId, eta);
    }
    
    @Override
    public LocalDateTime getCachedEta(String shipmentId) {
        ExpiringValue<LocalDateTime> value = etaCache.get(shipmentId);
        
        if (value != null && !value.isExpired()) {
            LOGGER.debug("Cache hit for ETA: {}", shipmentId);
            return value.getValue();
        }
        
        LOGGER.debug("Cache miss for ETA: {}", shipmentId);
        return null;
    }
    
    @Override
    @CacheEvict(cacheNames = ROUTES_CACHE, key = "#routeId")
    public void evictRoute(Long routeId) {
        LOGGER.debug("Evicted route from cache: {}", routeId);
    }
    
    @Override
    @CacheEvict(cacheNames = { ROUTES_CACHE, TRAVEL_TIMES_CACHE, NEARBY_COURIERS_CACHE, ETA_CACHE }, 
               allEntries = true)
    public void clearAllCaches() {
        travelTimesCache.clear();
        courierCache.clear();
        etaCache.clear();
        LOGGER.info("Cleared all caches");
    }
    
    private String createLocationKey(double originLat, double originLng, 
                                   double destLat, double destLng) {
        return String.format("%.5f:%.5f-%.5f:%.5f", originLat, originLng, destLat, destLng);
    }
    
    private String createLocationRadiusKey(Location location, double radiusKm) {
        return String.format("%.5f:%.5f:%.2f", location.getLatitude(), location.getLongitude(), radiusKm);
    }
    
    /**
     * A simple class to store a value with an expiration time.
     */
    private static class ExpiringValue<T> {
        private final T value;
        private final long expirationTime;
        
        public ExpiringValue(T value, long expirationTime) {
            this.value = value;
            this.expirationTime = expirationTime;
        }
        
        public T getValue() {
            return value;
        }
        
        public boolean isExpired() {
            return System.currentTimeMillis() > expirationTime;
        }
    }
}
