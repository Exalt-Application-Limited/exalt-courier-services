package com.gogidix.courier.location.service.resilience;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

import org.springframework.stereotype.Service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service wrapper for database operations with circuit breaker, retry,
 * and timeout capabilities to improve resilience.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ResilienceService {

    /**
     * Execute a database read operation with resilience patterns.
     * 
     * @param <T> the type of data being retrieved
     * @param supplier the database operation to execute
     * @param fallback the fallback operation (e.g., cache lookup) to use if the primary fails
     * @return the result of the operation
     */
    @CircuitBreaker(name = "databaseService", fallbackMethod = "fallbackRead")
    @Retry(name = "databaseService")
    @TimeLimiter(name = "databaseService")
    public <T> List<T> executeResillientRead(Supplier<List<T>> supplier, Supplier<List<T>> fallback) {
        return supplier.get();
    }
    
    /**
     * Execute a database single-entity read operation with resilience patterns.
     * 
     * @param <T> the type of data being retrieved
     * @param supplier the database operation to execute
     * @param fallback the fallback operation (e.g., cache lookup) to use if the primary fails
     * @return the result of the operation
     */
    @CircuitBreaker(name = "databaseService", fallbackMethod = "fallbackSingleRead")
    @Retry(name = "databaseService")
    @TimeLimiter(name = "databaseService")
    public <T> Optional<T> executeResillientSingleRead(Supplier<Optional<T>> supplier, Supplier<Optional<T>> fallback) {
        return supplier.get();
    }
    
    /**
     * Execute a database write operation with resilience patterns.
     * 
     * @param <T> the type of data being written
     * @param supplier the database operation to execute
     * @return the result of the operation
     */
    @CircuitBreaker(name = "databaseService", fallbackMethod = "fallbackWrite")
    @Retry(name = "databaseService")
    @TimeLimiter(name = "databaseService")
    public <T> T executeResillientWrite(Supplier<T> supplier) {
        return supplier.get();
    }
    
    /**
     * Fallback method for read operations.
     */
    private <T> List<T> fallbackRead(Supplier<List<T>> supplier, Supplier<List<T>> fallback, Exception e) {
        log.warn("Database read operation failed, using fallback. Error: {}", e.getMessage());
        if (fallback != null) {
            return fallback.get();
        }
        log.error("No fallback available for failed database read operation", e);
        throw new RuntimeException("Database service unavailable", e);
    }
    
    /**
     * Fallback method for single-entity read operations.
     */
    private <T> Optional<T> fallbackSingleRead(Supplier<Optional<T>> supplier, Supplier<Optional<T>> fallback, Exception e) {
        log.warn("Database single read operation failed, using fallback. Error: {}", e.getMessage());
        if (fallback != null) {
            return fallback.get();
        }
        log.error("No fallback available for failed database single read operation", e);
        return Optional.empty();
    }
    
    /**
     * Fallback method for write operations.
     */
    private <T> T fallbackWrite(Supplier<T> supplier, Exception e) {
        log.error("Database write operation failed with no fallback available", e);
        if (e instanceof TimeoutException) {
            throw new RuntimeException("Database write operation timed out", e);
        }
        throw new RuntimeException("Database service unavailable for write operations", e);
    }
}
