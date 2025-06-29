package com.exalt.courier.shared.resilience;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

/**
 * Utility class for applying circuit breaker pattern in courier services.
 * This class simplifies the usage of circuit breakers by providing standardized methods
 * for executing code with circuit breaker protection.
 */
@Component
public class CircuitBreakerUtil {
    private static final Logger logger = LoggerFactory.getLogger(CircuitBreakerUtil.class);
    
    private final CourierCircuitBreakerFactory circuitBreakerFactory;
    
    @Autowired
    public CircuitBreakerUtil(CourierCircuitBreakerFactory circuitBreakerFactory) {
        this.circuitBreakerFactory = circuitBreakerFactory;
    }
    
    /**
     * Executes a supplier function with circuit breaker protection using the default configuration.
     *
     * @param <T> the type of result returned by the supplier
     * @param operation a description of the operation for logging purposes
     * @param supplier the code to execute with circuit breaker protection
     * @return the result of the supplier
     * @throws CallNotPermittedException if the circuit is open
     */
    public <T> T executeWithCircuitBreaker(String operation, Supplier<T> supplier) throws CallNotPermittedException {
        return executeWithCircuitBreaker("default", operation, supplier, null);
    }
    
    /**
     * Executes a supplier function with circuit breaker protection using the default configuration
     * and provides a fallback value if the circuit is open.
     *
     * @param <T> the type of result returned by the supplier
     * @param operation a description of the operation for logging purposes
     * @param supplier the code to execute with circuit breaker protection
     * @param fallback the fallback value to return if the circuit is open
     * @return the result of the supplier or the fallback value
     */
    public <T> T executeWithCircuitBreaker(String operation, Supplier<T> supplier, T fallback) {
        return executeWithCircuitBreaker("default", operation, supplier, fallback);
    }
    
    /**
     * Executes a supplier function with circuit breaker protection using a specific configuration.
     *
     * @param <T> the type of result returned by the supplier
     * @param configName the name of the circuit breaker configuration to use
     * @param operation a description of the operation for logging purposes
     * @param supplier the code to execute with circuit breaker protection
     * @param fallback the fallback value to return if the circuit is open (can be null)
     * @return the result of the supplier or the fallback value
     */
    public <T> T executeWithCircuitBreaker(String configName, String operation, Supplier<T> supplier, T fallback) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create(configName);
        
        try {
            logger.debug("Executing operation '{}' with circuit breaker '{}'", operation, configName);
            return circuitBreaker.executeSupplier(supplier);
        } catch (CallNotPermittedException e) {
            logger.warn("Circuit '{}' is open for operation '{}'. Using fallback.", configName, operation);
            if (fallback != null) {
                return fallback;
            }
            throw e;
        }
    }
    
    /**
     * Executes a runnable with circuit breaker protection using the default configuration.
     *
     * @param operation a description of the operation for logging purposes
     * @param runnable the code to execute with circuit breaker protection
     * @throws CallNotPermittedException if the circuit is open
     */
    public void executeWithCircuitBreaker(String operation, Runnable runnable) throws CallNotPermittedException {
        executeWithCircuitBreaker("default", operation, runnable, false);
    }
    
    /**
     * Executes a runnable with circuit breaker protection using a specific configuration.
     *
     * @param configName the name of the circuit breaker configuration to use
     * @param operation a description of the operation for logging purposes
     * @param runnable the code to execute with circuit breaker protection
     * @param suppressException whether to suppress CircuitOpenException
     * @throws CallNotPermittedException if the circuit is open and suppressException is false
     */
    public void executeWithCircuitBreaker(String configName, String operation, Runnable runnable, boolean suppressException) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create(configName);
        
        try {
            logger.debug("Executing operation '{}' with circuit breaker '{}'", operation, configName);
            circuitBreaker.executeRunnable(runnable);
        } catch (CallNotPermittedException e) {
            logger.warn("Circuit '{}' is open for operation '{}'. Skipping execution.", configName, operation);
            if (!suppressException) {
                throw e;
            }
        }
    }
} 