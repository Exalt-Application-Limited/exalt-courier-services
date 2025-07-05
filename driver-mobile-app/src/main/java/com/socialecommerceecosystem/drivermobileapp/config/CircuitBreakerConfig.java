package com.gogidix.courier.drivermobileapp.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Configuration for circuit breaker patterns to handle service failures gracefully.
 */
@Configuration
public class CircuitBreakerConfig {

    /**
     * Configure default circuit breaker settings.
     *
     * @return circuit breaker customizer
     */
    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .timeLimiterConfig(TimeLimiterConfig.custom()
                        .timeoutDuration(Duration.ofSeconds(4))
                        .build())
                .circuitBreakerConfig(io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.custom()
                        .failureRateThreshold(50)
                        .waitDurationInOpenState(Duration.ofMillis(1000))
                        .slidingWindowSize(10)
                        .permittedNumberOfCallsInHalfOpenState(3)
                        .minimumNumberOfCalls(5)
                        .build())
                .build());
    }
    
    /**
     * Configure circuit breaker for courier management service.
     *
     * @return circuit breaker customizer
     */
    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> courierManagementServiceCustomizer() {
        return factory -> factory.configure(builder -> builder
                .timeLimiterConfig(TimeLimiterConfig.custom()
                        .timeoutDuration(Duration.ofSeconds(5))
                        .build())
                .circuitBreakerConfig(io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.custom()
                        .failureRateThreshold(40)
                        .waitDurationInOpenState(Duration.ofSeconds(10))
                        .slidingWindowSize(10)
                        .permittedNumberOfCallsInHalfOpenState(5)
                        .minimumNumberOfCalls(5)
                        .build()), "courierManagementService");
    }
    
    /**
     * Configure circuit breaker for tracking service.
     *
     * @return circuit breaker customizer
     */
    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> trackingServiceCustomizer() {
        return factory -> factory.configure(builder -> builder
                .timeLimiterConfig(TimeLimiterConfig.custom()
                        .timeoutDuration(Duration.ofSeconds(3))
                        .build())
                .circuitBreakerConfig(io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.custom()
                        .failureRateThreshold(50)
                        .waitDurationInOpenState(Duration.ofSeconds(5))
                        .slidingWindowSize(8)
                        .permittedNumberOfCallsInHalfOpenState(3)
                        .minimumNumberOfCalls(5)
                        .build()), "trackingService");
    }
    
    /**
     * Configure circuit breaker for routing service.
     *
     * @return circuit breaker customizer
     */
    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> routingServiceCustomizer() {
        return factory -> factory.configure(builder -> builder
                .timeLimiterConfig(TimeLimiterConfig.custom()
                        .timeoutDuration(Duration.ofSeconds(4))
                        .build())
                .circuitBreakerConfig(io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.custom()
                        .failureRateThreshold(50)
                        .waitDurationInOpenState(Duration.ofSeconds(8))
                        .slidingWindowSize(10)
                        .permittedNumberOfCallsInHalfOpenState(4)
                        .minimumNumberOfCalls(5)
                        .build()), "routingService");
    }
}
