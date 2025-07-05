package com.gogidix.courier.location.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Service for monitoring system health and sending alerts on critical failures.
 * Uses Micrometer metrics and custom logic to detect and alert on service issues.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class AlertService {

    private final MeterRegistry meterRegistry;
    private final NotificationSender notificationSender;
    
    @Value("${monitoring.error.threshold:5}")
    private int errorThreshold;
    
    @Value("${monitoring.error.timeWindowSeconds:60}")
    private int errorTimeWindowSeconds;
    
    @Value("${monitoring.slow.request.threshold:500}")
    private int slowRequestThresholdMs;
    
    @Value("${monitoring.slow.request.percentage:10}")
    private int slowRequestPercentage;
    
    private final Map<String, ErrorTracker> errorTrackers = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> errorCounters = new ConcurrentHashMap<>();
    
    private Counter totalErrorsCounter;
    private Counter criticalErrorsCounter;
    private Counter serviceFailuresCounter;
    
    @PostConstruct
    public void initMetrics() {
        // Initialize metrics counters
        totalErrorsCounter = Counter.builder("errors.total")
                .description("Total number of application errors")
                .register(meterRegistry);
        
        criticalErrorsCounter = Counter.builder("errors.critical")
                .description("Number of critical application errors")
                .register(meterRegistry);
        
        serviceFailuresCounter = Counter.builder("service.failures")
                .description("Number of service failures requiring intervention")
                .register(meterRegistry);
        
        log.info("Alert service initialized with thresholds - Errors: {} in {}s, Slow Requests: {}ms ({}%)",
                errorThreshold, errorTimeWindowSeconds, slowRequestThresholdMs, slowRequestPercentage);
    }
    
    /**
     * Track an application error.
     * 
     * @param errorType the type of error
     * @param errorMessage the error message
     * @param critical whether the error is critical
     */
    public void trackError(String errorType, String errorMessage, boolean critical) {
        totalErrorsCounter.increment();
        
        if (critical) {
            criticalErrorsCounter.increment();
        }
        
        // Track errors by type
        errorCounters.computeIfAbsent(errorType, k -> new AtomicInteger(0)).incrementAndGet();
        
        // Get or create error tracker for this type
        ErrorTracker tracker = errorTrackers.computeIfAbsent(errorType, k -> new ErrorTracker(errorTimeWindowSeconds));
        
        // Add error to tracker
        boolean thresholdExceeded = tracker.addError();
        
        // Log the error
        if (critical) {
            log.error("CRITICAL ERROR - Type: {}, Message: {}", errorType, errorMessage);
        } else {
            log.warn("Error - Type: {}, Message: {}", errorType, errorMessage);
        }
        
        // Check if we need to send an alert
        if (thresholdExceeded || critical) {
            int count = tracker.getRecentErrorCount();
            sendErrorAlert(errorType, errorMessage, count, critical);
        }
    }
    
    /**
     * Track a database error.
     * 
     * @param operation the database operation that failed
     * @param entity the entity being operated on
     * @param exception the exception that occurred
     */
    public void trackDatabaseError(String operation, String entity, Exception exception) {
        String errorType = "DATABASE_" + operation.toUpperCase();
        String message = String.format("Database error during %s operation on %s: %s", 
                operation, entity, exception.getMessage());
        
        trackError(errorType, message, true);
    }
    
    /**
     * Track an integration error with external services.
     * 
     * @param service the external service name
     * @param operation the operation being performed
     * @param exception the exception that occurred
     */
    public void trackIntegrationError(String service, String operation, Exception exception) {
        String errorType = "INTEGRATION_" + service.toUpperCase();
        String message = String.format("Integration error with %s during %s: %s", 
                service, operation, exception.getMessage());
        
        trackError(errorType, message, true);
    }
    
    /**
     * Track a service failure.
     * 
     * @param serviceName the name of the service that failed
     * @param operation the operation that failed
     * @param reason the reason for the failure
     */
    public void trackServiceFailure(String serviceName, String operation, String reason) {
        String errorType = "SERVICE_" + serviceName.toUpperCase();
        String message = String.format("Service %s failed during %s: %s", 
                serviceName, operation, reason);
        
        serviceFailuresCounter.increment();
        trackError(errorType, message, true);
    }
    
    /**
     * Track a slow request.
     * 
     * @param endpoint the API endpoint
     * @param durationMs the request duration in milliseconds
     */
    public void trackSlowRequest(String endpoint, long durationMs) {
        if (durationMs > slowRequestThresholdMs) {
            log.warn("Slow request detected at {} - took {}ms", endpoint, durationMs);
            
            // In a real implementation, we would track the percentage of slow requests
            // and alert if it exceeds the threshold
        }
    }
    
    /**
     * Send an alert for an error condition.
     * 
     * @param errorType the type of error
     * @param errorMessage the error message
     * @param count the number of recent errors of this type
     * @param critical whether the error is critical
     */
    private void sendErrorAlert(String errorType, String errorMessage, int count, boolean critical) {
        String severity = critical ? "CRITICAL" : "WARNING";
        String subject = String.format("%s: %s error detected (%d occurrences)", severity, errorType, count);
        
        String body = String.format(
                "Alert Details:\n" +
                "- Error Type: %s\n" +
                "- Severity: %s\n" +
                "- Recent Count: %d\n" +
                "- Latest Message: %s\n" +
                "- Timestamp: %s\n\n" +
                "Please check system logs for more details.",
                errorType, severity, count, errorMessage, java.time.LocalDateTime.now());
        
        // Send the alert notification
        notificationSender.sendAlertNotification(subject, body, critical);
        
        log.info("Sent {} alert for {} error (count: {})", severity, errorType, count);
    }
    
    /**
     * Helper class to track errors over time.
     */
    private static class ErrorTracker {
        private final int timeWindowSeconds;
        private final Map<Long, Integer> errorsByTimeSlot = new HashMap<>();
        
        public ErrorTracker(int timeWindowSeconds) {
            this.timeWindowSeconds = timeWindowSeconds;
        }
        
        /**
         * Add an error and check if the threshold is exceeded.
         * 
         * @return true if the error threshold is exceeded
         */
        public synchronized boolean addError() {
            long now = System.currentTimeMillis() / 1000;
            
            // Add the error to the current time slot
            errorsByTimeSlot.merge(now, 1, Integer::sum);
            
            // Clean up old time slots
            long cutoff = now - timeWindowSeconds;
            errorsByTimeSlot.entrySet().removeIf(entry -> entry.getKey() < cutoff);
            
            // Sum up recent errors
            int recentErrors = errorsByTimeSlot.values().stream().mapToInt(Integer::intValue).sum();
            
            // Return whether the threshold is exceeded
            return recentErrors >= 5;
        }
        
        /**
         * Get the count of recent errors.
         * 
         * @return the number of errors in the time window
         */
        public synchronized int getRecentErrorCount() {
            long now = System.currentTimeMillis() / 1000;
            long cutoff = now - timeWindowSeconds;
            
            return errorsByTimeSlot.entrySet().stream()
                    .filter(entry -> entry.getKey() >= cutoff)
                    .mapToInt(Map.Entry::getValue)
                    .sum();
        }
    }
}
