package com.gogidix.courier.shared.resilience;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of the circuit breaker factory for the courier services domain.
 * This class creates and manages circuit breakers with configurations appropriate for courier services.
 */
@Component
public class CourierCircuitBreakerFactory {

    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final Map<String, CircuitBreakerConfig> configurations;

    /**
     * Constructs a new instance of CourierCircuitBreakerFactory with default configuration.
     */
    public CourierCircuitBreakerFactory() {
        this.configurations = new ConcurrentHashMap<>();
        this.circuitBreakerRegistry = CircuitBreakerRegistry.of(getDefaultConfig());
        configureDefaults();
    }

    /**
     * Creates or retrieves a circuit breaker with the specified configuration name.
     *
     * @param configName the name of the configuration to use
     * @return the circuit breaker instance
     */
    public CircuitBreaker create(String configName) {
        CircuitBreakerConfig config = configurations.getOrDefault(configName, getDefaultConfig());
        return circuitBreakerRegistry.circuitBreaker(configName, config);
    }

    /**
     * Gets the default circuit breaker configuration for courier services.
     *
     * @return the default circuit breaker configuration
     */
    private CircuitBreakerConfig getDefaultConfig() {
        return CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofSeconds(30))
                .slidingWindowSize(100)
                .minimumNumberOfCalls(10)
                .build();
    }

    /**
     * Configures the default circuit breaker settings specific to courier services.
     * These settings are tailored for the resilience requirements of delivery operations.
     */
    private void configureDefaults() {
        // Configure circuit breaker for courier service operations
        CircuitBreakerConfig courierConfig = CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofSeconds(30))
                .slidingWindowSize(100)
                .minimumNumberOfCalls(5)
                .build();
        
        configurations.put("default", courierConfig);
        
        // Configure circuit breaker for external provider calls with more lenient settings
        CircuitBreakerConfig externalProviderConfig = CircuitBreakerConfig.custom()
                .failureRateThreshold(30)
                .waitDurationInOpenState(Duration.ofSeconds(60))
                .slidingWindowSize(50)
                .minimumNumberOfCalls(3)
                .build();
        
        configurations.put("externalProvider", externalProviderConfig);
        
        // Configure circuit breaker for tracking operations
        CircuitBreakerConfig trackingConfig = CircuitBreakerConfig.custom()
                .failureRateThreshold(40)
                .waitDurationInOpenState(Duration.ofSeconds(15))
                .slidingWindowSize(100)
                .minimumNumberOfCalls(10)
                .build();
        
        configurations.put("tracking", trackingConfig);
    }
}
