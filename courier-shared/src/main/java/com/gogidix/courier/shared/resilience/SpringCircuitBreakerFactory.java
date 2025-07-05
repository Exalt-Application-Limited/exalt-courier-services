package com.gogidix.courier.shared.resilience;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration class for setting up Spring Cloud Circuit Breaker with Resilience4J.
 * Provides circuit breaker configurations for various services with appropriate timeouts and failure thresholds.
 */
@Configuration
public class SpringCircuitBreakerFactory {
    
    private static final Logger logger = LoggerFactory.getLogger(SpringCircuitBreakerFactory.class);
    
    /**
     * Default circuit breaker configuration customizer for Spring Cloud Circuit Breaker.
     * This creates the base circuit breaker with sensible defaults.
     *
     * @return A customizer for Resilience4JCircuitBreakerFactory
     */
    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .timeLimiterConfig(TimeLimiterConfig.custom()
                        .timeoutDuration(Duration.ofSeconds(3))
                        .build())
                .circuitBreakerConfig(CircuitBreakerConfig.custom()
                        .failureRateThreshold(50)
                        .waitDurationInOpenState(Duration.ofSeconds(10))
                        .slidingWindowSize(10)
                        .minimumNumberOfCalls(5)
                        .permittedNumberOfCallsInHalfOpenState(3)
                        .build())
                .build());
    }
    
    /**
     * Circuit breaker configuration specifically for routing service with longer timeout.
     *
     * @return A customizer for Resilience4JCircuitBreakerFactory
     */
    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> routingServiceCustomizer() {
        return factory -> factory.configure(builder -> builder
                .timeLimiterConfig(TimeLimiterConfig.custom()
                        .timeoutDuration(Duration.ofSeconds(5))
                        .build())
                .circuitBreakerConfig(CircuitBreakerConfig.custom()
                        .failureRateThreshold(40)
                        .waitDurationInOpenState(Duration.ofSeconds(15))
                        .slidingWindowSize(10)
                        .build()), "routingService");
    }
    
    /**
     * Circuit breaker configuration for the tracking service.
     *
     * @return A customizer for Resilience4JCircuitBreakerFactory
     */
    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> trackingServiceCustomizer() {
        return factory -> factory.configure(builder -> builder
                .timeLimiterConfig(TimeLimiterConfig.custom()
                        .timeoutDuration(Duration.ofSeconds(2))
                        .build())
                .circuitBreakerConfig(CircuitBreakerConfig.custom()
                        .failureRateThreshold(30)
                        .waitDurationInOpenState(Duration.ofSeconds(10))
                        .slidingWindowSize(10)
                        .build()), "trackingService");
    }
    
    /**
     * Circuit breaker configuration for payment services with more strict thresholds.
     *
     * @return A customizer for Resilience4JCircuitBreakerFactory
     */
    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> paymentServiceCustomizer() {
        return factory -> factory.configure(builder -> builder
                .timeLimiterConfig(TimeLimiterConfig.custom()
                        .timeoutDuration(Duration.ofSeconds(4))
                        .build())
                .circuitBreakerConfig(CircuitBreakerConfig.custom()
                        .failureRateThreshold(20) // More sensitive threshold for payment services
                        .waitDurationInOpenState(Duration.ofSeconds(20))
                        .slidingWindowSize(15)
                        .build()), "paymentService");
    }
    
    /**
     * Circuit breaker configuration for third-party integrations with longer timeouts.
     *
     * @return A customizer for Resilience4JCircuitBreakerFactory
     */
    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> thirdPartyIntegrationCustomizer() {
        return factory -> factory.configure(builder -> builder
                .timeLimiterConfig(TimeLimiterConfig.custom()
                        .timeoutDuration(Duration.ofSeconds(8)) // Longer timeout for external services
                        .build())
                .circuitBreakerConfig(CircuitBreakerConfig.custom()
                        .failureRateThreshold(50)
                        .waitDurationInOpenState(Duration.ofSeconds(30))
                        .slidingWindowSize(20)
                        .build()), "thirdPartyIntegration");
    }
    
    /**
     * Creates a registry of named circuit breakers with associated metrics for monitoring.
     * This allows for runtime inspection of circuit breaker states.
     *
     * @return The CircuitBreakerRegistry for application use
     */
    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        Map<String, CircuitBreakerConfig> configs = new HashMap<>();
        
        // Add configurations for different services
        configs.put("routingService", CircuitBreakerConfig.custom()
                .failureRateThreshold(40)
                .waitDurationInOpenState(Duration.ofSeconds(15))
                .slidingWindowSize(10)
                .build());
        
        configs.put("trackingService", CircuitBreakerConfig.custom()
                .failureRateThreshold(30)
                .waitDurationInOpenState(Duration.ofSeconds(10))
                .slidingWindowSize(10)
                .build());
        
        configs.put("paymentService", CircuitBreakerConfig.custom()
                .failureRateThreshold(20)
                .waitDurationInOpenState(Duration.ofSeconds(20))
                .slidingWindowSize(15)
                .build());
        
        configs.put("thirdPartyIntegration", CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofSeconds(30))
                .slidingWindowSize(20)
                .build());
        
        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(configs);
        
        // Pre-create the circuit breakers
        configs.keySet().forEach(name -> {
            CircuitBreaker circuitBreaker = registry.circuitBreaker(name);
            logger.info("Created circuit breaker: {} with state: {}", name, circuitBreaker.getState());
        });
        
        return registry;
    }
}
