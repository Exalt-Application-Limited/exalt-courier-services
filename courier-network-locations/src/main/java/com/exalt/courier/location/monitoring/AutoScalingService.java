package com.exalt.courier.location.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Service for auto-scaling support and monitoring.
 * Exposes metrics for Kubernetes HPA (Horizontal Pod Autoscaler) and
 * provides pre-warming capabilities to improve performance during scaling events.
 */
@Component
@RefreshScope
@Slf4j
@RequiredArgsConstructor
public class AutoScalingService {

    private final MeterRegistry meterRegistry;
    
    @Value("${autoscaling.threshold.cpu:70}")
    private int cpuThreshold;
    
    @Value("${autoscaling.threshold.memory:80}")
    private int memoryThreshold;
    
    @Value("${autoscaling.threshold.requests:500}")
    private int requestsThreshold;
    
    private final AtomicInteger activeRequests = new AtomicInteger(0);
    private final AtomicLong requestCount = new AtomicLong(0);
    private final AtomicLong requestsLastMinute = new AtomicLong(0);
    
    private Counter totalRequestsCounter;
    private Timer requestLatencyTimer;
    
    @PostConstruct
    public void initMetrics() {
        // Register prometheus metrics
        totalRequestsCounter = Counter.builder("http.requests.total")
                .description("Total number of HTTP requests processed")
                .register(meterRegistry);
        
        requestLatencyTimer = Timer.builder("http.request.latency")
                .description("HTTP request latency in milliseconds")
                .register(meterRegistry);
        
        // Gauge for active requests - used by HPA
        Gauge.builder("http.requests.active", activeRequests::get)
                .description("Number of currently active HTTP requests")
                .register(meterRegistry);
        
        // Gauge for requests per minute - used by HPA
        Gauge.builder("http.requests.per.minute", requestsLastMinute::get)
                .description("Number of HTTP requests per minute")
                .register(meterRegistry);
        
        log.info("Auto-scaling metrics initialized with thresholds - CPU: {}%, Memory: {}%, Requests: {}/s",
                cpuThreshold, memoryThreshold, requestsThreshold);
    }
    
    /**
     * Track the start of a request.
     * Call this at the beginning of request processing.
     * 
     * @return the current timestamp for latency calculation
     */
    public long requestStarted() {
        activeRequests.incrementAndGet();
        requestCount.incrementAndGet();
        totalRequestsCounter.increment();
        return System.currentTimeMillis();
    }
    
    /**
     * Track the completion of a request.
     * Call this at the end of request processing.
     * 
     * @param startTimeMillis the timestamp from requestStarted()
     */
    public void requestCompleted(long startTimeMillis) {
        activeRequests.decrementAndGet();
        long duration = System.currentTimeMillis() - startTimeMillis;
        requestLatencyTimer.record(duration, TimeUnit.MILLISECONDS);
    }
    
    /**
     * Reset and calculate requests per minute metric.
     * Scheduled to run every minute.
     */
    @Scheduled(fixedRate = 60000)
    public void calculateRequestsPerMinute() {
        long count = requestCount.getAndSet(0);
        requestsLastMinute.set(count);
        log.debug("Requests in the last minute: {}", count);
        
        // Check if approaching scaling threshold
        if (count > (requestsThreshold * 0.8)) {
            log.info("Approaching request scaling threshold: {} requests/min (threshold: {})", 
                    count, requestsThreshold);
            preWarmResources();
        }
    }
    
    /**
     * Pre-warm resources in anticipation of scaling.
     * This can help reduce coldstart times for new pods.
     */
    public void preWarmResources() {
        log.info("Pre-warming resources in anticipation of scaling event");
        
        // Implement pre-warming logic here, such as:
        // - Warming up connection pools
        // - Pre-loading frequently accessed cache data
        // - Initializing expensive components
    }
    
    /**
     * Check if the service is near a scaling threshold.
     * 
     * @return true if approaching a scaling threshold
     */
    public boolean isNearScalingThreshold() {
        return requestsLastMinute.get() > (requestsThreshold * 0.8);
    }
    
    /**
     * Get the current load factor (0.0 - 1.0) based on request rates.
     * 
     * @return load factor as a percentage of threshold
     */
    public double getLoadFactor() {
        return (double) requestsLastMinute.get() / requestsThreshold;
    }
}
