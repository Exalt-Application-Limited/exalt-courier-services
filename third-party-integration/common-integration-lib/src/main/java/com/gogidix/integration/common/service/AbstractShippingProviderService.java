package com.gogidix.integration.common.service;

import com.gogidix.integration.common.model.ShipmentRequest;
import com.gogidix.integration.common.model.ShipmentResponse;
import com.gogidix.integration.common.model.TrackingRequest;
import com.gogidix.integration.common.model.TrackingResponse;
import com.gogidix.integration.common.model.ShipmentResponse.ShipmentError;
import com.gogidix.integration.common.model.ShipmentResponse.ShipmentError.ErrorSeverity;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Abstract base implementation of ShippingProviderService that provides common
 * functionality for all shipping provider implementations.
 * 
 * This class handles common concerns such as:
 * - Performance and SLA metrics tracking
 * - Error handling and retry mechanisms
 * - Rate limiting
 * - Logging and monitoring
 */
@Slf4j
public abstract class AbstractShippingProviderService implements ShippingProviderService {

    @Value("${integration.shipment.max-retries:3}")
    private int maxRetries;

    @Value("${integration.shipment.retry-delay-ms:2000}")
    private long retryDelayMs;

    @Value("${integration.shipment.enable-metrics:true}")
    private boolean enableMetrics;

    @Value("${integration.shipment.sla-threshold-ms:5000}")
    private long slaThresholdMs;

    private final Map<String, AtomicInteger> requestCounters = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> errorCounters = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> responseTimes = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> slaViolationCounters = new ConcurrentHashMap<>();
    
    // Health status
    private boolean operational = true;
    private LocalDateTime lastHealthCheck;
    private String lastHealthStatus;
    
    @PostConstruct
    public void initialize() {
        log.info("Initializing {} shipping provider service", getCarrierName());
        // Initialize metrics
        initializeMetrics("createShipment");
        initializeMetrics("trackShipment");
        initializeMetrics("cancelShipment");
        initializeMetrics("validateAddress");
        initializeMetrics("calculateRates");
        initializeMetrics("schedulePickup");
        
        // Perform initial health check
        checkHealth();
    }
    
    private void initializeMetrics(String operation) {
        requestCounters.put(operation, new AtomicInteger(0));
        errorCounters.put(operation, new AtomicInteger(0));
        responseTimes.put(operation, new AtomicLong(0));
        slaViolationCounters.put(operation, new AtomicInteger(0));
    }
    
    /**
     * Template method for executing provider operations with metrics and error handling.
     *
     * @param operation the operation name for metrics tracking
     * @param callable the operation to execute
     * @param <T> the return type
     * @return the operation result
     */
    protected <T> T executeWithMetrics(String operation, ProviderCallable<T> callable) {
        if (enableMetrics) {
            requestCounters.get(operation).incrementAndGet();
        }
        
        LocalDateTime start = LocalDateTime.now();
        int retryCount = 0;
        Exception lastException = null;
        
        while (retryCount <= maxRetries) {
            try {
                T result = callable.call();
                
                if (enableMetrics) {
                    Duration duration = Duration.between(start, LocalDateTime.now());
                    long durationMs = duration.toMillis();
                    responseTimes.get(operation).addAndGet(durationMs);
                    
                    // Check SLA violation
                    if (durationMs > slaThresholdMs) {
                        slaViolationCounters.get(operation).incrementAndGet();
                        log.warn("SLA violation in {} operation: {}ms (threshold: {}ms)", 
                                operation, durationMs, slaThresholdMs);
                    }
                }
                
                return result;
            } catch (Exception e) {
                lastException = e;
                retryCount++;
                
                if (enableMetrics) {
                    errorCounters.get(operation).incrementAndGet();
                }
                
                if (retryCount <= maxRetries) {
                    log.warn("Error in {} operation, retrying ({}/{}): {}", 
                            operation, retryCount, maxRetries, e.getMessage());
                    try {
                        Thread.sleep(retryDelayMs * retryCount); // Exponential backoff
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                } else {
                    log.error("Maximum retries reached for {} operation: {}", 
                            operation, e.getMessage(), e);
                }
            }
        }
        
        throw new ShippingProviderException("Failed to execute " + operation + " after " + 
                maxRetries + " retries", lastException);
    }
    
    /**
     * Check the health of the shipping provider service.
     * This method should be called periodically to update the operational status.
     */
    protected void checkHealth() {
        try {
            boolean previousStatus = operational;
            operational = isProviderAvailable();
            lastHealthCheck = LocalDateTime.now();
            lastHealthStatus = operational ? "OK" : "UNAVAILABLE";
            
            if (previousStatus != operational) {
                if (operational) {
                    log.info("{} shipping provider service is now OPERATIONAL", getCarrierName());
                } else {
                    log.warn("{} shipping provider service is now UNAVAILABLE", getCarrierName());
                }
            }
        } catch (Exception e) {
            operational = false;
            lastHealthStatus = "ERROR: " + e.getMessage();
            log.error("Error checking health of {} shipping provider service: {}", 
                    getCarrierName(), e.getMessage(), e);
        }
    }
    
    /**
     * Perform a provider-specific health check.
     * This method should be implemented by each provider.
     *
     * @return true if the provider is available, false otherwise
     */
    protected abstract boolean isProviderAvailable();
    
    /**
     * Create a shipment with error handling and metrics.
     */
    @Override
    public ShipmentResponse createShipment(ShipmentRequest request) {
        return executeWithMetrics("createShipment", () -> {
            validateShipmentRequest(request);
            return createShipmentInternal(request);
        });
    }
    
    /**
     * Track a shipment with error handling and metrics.
     */
    @Override
    public TrackingResponse trackShipment(TrackingRequest request) {
        return executeWithMetrics("trackShipment", () -> {
            validateTrackingRequest(request);
            return trackShipmentInternal(request);
        });
    }
    
    /**
     * Cancel a shipment with error handling and metrics.
     */
    @Override
    public boolean cancelShipment(String shipmentId) {
        return executeWithMetrics("cancelShipment", () -> {
            if (shipmentId == null || shipmentId.trim().isEmpty()) {
                throw new IllegalArgumentException("Shipment ID cannot be null or empty");
            }
            return cancelShipmentInternal(shipmentId);
        });
    }
    
    /**
     * Validate an address with error handling and metrics.
     */
    @Override
    public boolean validateAddress(Map<String, String> addressData) {
        return executeWithMetrics("validateAddress", () -> {
            if (addressData == null || addressData.isEmpty()) {
                throw new IllegalArgumentException("Address data cannot be null or empty");
            }
            return validateAddressInternal(addressData);
        });
    }
    
    /**
     * Calculate shipping rates with error handling and metrics.
     */
    @Override
    public Map<String, Object> calculateRates(ShipmentRequest request) {
        return executeWithMetrics("calculateRates", () -> {
            validateShipmentRequest(request);
            return calculateRatesInternal(request);
        });
    }
    
    /**
     * Schedule a pickup with error handling and metrics.
     */
    @Override
    public Map<String, Object> schedulePickup(Map<String, Object> pickupData) {
        return executeWithMetrics("schedulePickup", () -> {
            if (pickupData == null || pickupData.isEmpty()) {
                throw new IllegalArgumentException("Pickup data cannot be null or empty");
            }
            return schedulePickupInternal(pickupData);
        });
    }
    
    /**
     * Get SLA metrics.
     */
    @Override
    public Map<String, Object> getSlaMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        metrics.put("carrier", getCarrierId());
        metrics.put("carrierName", getCarrierName());
        metrics.put("operational", operational);
        metrics.put("lastHealthCheck", lastHealthCheck);
        metrics.put("lastHealthStatus", lastHealthStatus);
        
        if (enableMetrics) {
            Map<String, Object> operationMetrics = new HashMap<>();
            
            for (String operation : requestCounters.keySet()) {
                Map<String, Object> opMetrics = new HashMap<>();
                int requests = requestCounters.get(operation).get();
                opMetrics.put("requestCount", requests);
                opMetrics.put("errorCount", errorCounters.get(operation).get());
                opMetrics.put("slaViolationCount", slaViolationCounters.get(operation).get());
                
                // Calculate average response time
                if (requests > 0) {
                    opMetrics.put("averageResponseTimeMs", 
                            (double) responseTimes.get(operation).get() / requests);
                } else {
                    opMetrics.put("averageResponseTimeMs", 0);
                }
                
                operationMetrics.put(operation, opMetrics);
            }
            
            metrics.put("operations", operationMetrics);
        }
        
        return metrics;
    }
    
    /**
     * Check if the provider is available and operational.
     */
    @Override
    public boolean isOperational() {
        // Refresh health check if it's been more than a certain period
        LocalDateTime now = LocalDateTime.now();
        if (lastHealthCheck == null || 
                Duration.between(lastHealthCheck, now).toMinutes() >= 5) {
            checkHealth();
        }
        return operational;
    }
    
    /**
     * Get cost optimization suggestions.
     * Default implementation returns empty suggestions.
     */
    @Override
    public Map<String, Object> getCostOptimizationSuggestions(Map<String, Object> shipmentData) {
        Map<String, Object> suggestions = new HashMap<>();
        suggestions.put("carrierId", getCarrierId());
        suggestions.put("carrierName", getCarrierName());
        suggestions.put("suggestions", Collections.emptyList());
        return suggestions;
    }
    
    /**
     * Validate a shipment request.
     * 
     * @param request the request to validate
     * @throws IllegalArgumentException if the request is invalid
     */
    protected void validateShipmentRequest(ShipmentRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Shipment request cannot be null");
        }
        
        List<String> validationErrors = new ArrayList<>();
        
        if (request.getReferenceId() == null || request.getReferenceId().trim().isEmpty()) {
            validationErrors.add("Reference ID is required");
        }
        
        if (request.getSender() == null) {
            validationErrors.add("Sender address is required");
        }
        
        if (request.getRecipient() == null) {
            validationErrors.add("Recipient address is required");
        }
        
        if (request.getPackages() == null || request.getPackages().isEmpty()) {
            validationErrors.add("At least one package is required");
        }
        
        if (request.getServiceType() == null) {
            validationErrors.add("Service type is required");
        }
        
        if (request.getShipmentDate() == null) {
            validationErrors.add("Shipment date is required");
        }
        
        if (!validationErrors.isEmpty()) {
            throw new IllegalArgumentException("Shipment request validation failed: " + 
                    String.join(", ", validationErrors));
        }
    }
    
    /**
     * Validate a tracking request.
     *
     * @param request the request to validate
     * @throws IllegalArgumentException if the request is invalid
     */
    protected void validateTrackingRequest(TrackingRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Tracking request cannot be null");
        }
        
        List<String> validationErrors = new ArrayList<>();
        
        if (request.getTrackingNumber() == null || request.getTrackingNumber().trim().isEmpty()) {
            validationErrors.add("Tracking number is required");
        }
        
        if (request.getCarrierId() == null) {
            validationErrors.add("Carrier ID is required");
        }
        
        if (!validationErrors.isEmpty()) {
            throw new IllegalArgumentException("Tracking request validation failed: " + 
                    String.join(", ", validationErrors));
        }
    }
    
    /**
     * Create a standardized error response.
     *
     * @param message the error message
     * @param severity the error severity
     * @return a list of errors
     */
    protected List<ShipmentError> createErrorResponse(String message, ErrorSeverity severity) {
        ShipmentError error = ShipmentError.builder()
                .code("PROVIDER_ERROR")
                .message(message)
                .severity(severity)
                .build();
        
        return Collections.singletonList(error);
    }
    
    /**
     * Provider-specific implementation of createShipment.
     *
     * @param request the shipment request
     * @return the shipment response
     * @throws Exception if an error occurs
     */
    protected abstract ShipmentResponse createShipmentInternal(ShipmentRequest request) throws Exception;
    
    /**
     * Provider-specific implementation of trackShipment.
     *
     * @param request the tracking request
     * @return the tracking response
     * @throws Exception if an error occurs
     */
    protected abstract TrackingResponse trackShipmentInternal(TrackingRequest request) throws Exception;
    
    /**
     * Provider-specific implementation of cancelShipment.
     *
     * @param shipmentId the shipment ID
     * @return true if cancelled successfully
     * @throws Exception if an error occurs
     */
    protected abstract boolean cancelShipmentInternal(String shipmentId) throws Exception;
    
    /**
     * Provider-specific implementation of validateAddress.
     *
     * @param addressData the address data
     * @return true if the address is valid
     * @throws Exception if an error occurs
     */
    protected abstract boolean validateAddressInternal(Map<String, String> addressData) throws Exception;
    
    /**
     * Provider-specific implementation of calculateRates.
     *
     * @param request the shipment request
     * @return map with rate options
     * @throws Exception if an error occurs
     */
    protected abstract Map<String, Object> calculateRatesInternal(ShipmentRequest request) throws Exception;
    
    /**
     * Provider-specific implementation of schedulePickup.
     *
     * @param pickupData the pickup data
     * @return map with pickup confirmation details
     * @throws Exception if an error occurs
     */
    protected abstract Map<String, Object> schedulePickupInternal(Map<String, Object> pickupData) throws Exception;
    
    /**
     * Functional interface for provider operations.
     *
     * @param <T> the return type
     */
    @FunctionalInterface
    protected interface ProviderCallable<T> {
        T call() throws Exception;
    }
    
    /**
     * Exception thrown when a shipping provider operation fails.
     */
    public static class ShippingProviderException extends RuntimeException {
        private static final long serialVersionUID = 1L;
        
        public ShippingProviderException(String message) {
            super(message);
        }
        
        public ShippingProviderException(String message, Throwable cause) {
            super(message, cause);
        }
    }
} 
