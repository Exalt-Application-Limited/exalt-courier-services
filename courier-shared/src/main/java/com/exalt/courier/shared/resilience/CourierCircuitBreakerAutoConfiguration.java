package com.exalt.courier.shared.resilience;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Auto-configuration class for the courier services circuit breaker.
 * This class ensures that the circuit breaker components are properly initialized
 * and registered in the Spring application context.
 */
@Configuration
@AutoConfiguration
public class CourierCircuitBreakerAutoConfiguration {

    /**
     * Creates a CourierCircuitBreakerFactory bean if one doesn't already exist.
     *
     * @return a new instance of CourierCircuitBreakerFactory
     */
    @Bean
    @ConditionalOnMissingBean
    public CourierCircuitBreakerFactory courierCircuitBreakerFactory() {
        return new CourierCircuitBreakerFactory();
    }

    /**
     * Creates a CircuitBreakerUtil bean if one doesn't already exist.
     *
     * @param circuitBreakerFactory the CourierCircuitBreakerFactory to use
     * @return a new instance of CircuitBreakerUtil
     */
    @Bean
    @ConditionalOnMissingBean
    public CircuitBreakerUtil circuitBreakerUtil(CourierCircuitBreakerFactory circuitBreakerFactory) {
        return new CircuitBreakerUtil(circuitBreakerFactory);
    }
} 