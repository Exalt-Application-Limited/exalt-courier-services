package com.gogidix.courier.courier.config;

import brave.baggage.BaggageField;
import brave.baggage.CorrelationScopeConfig;
import brave.context.slf4j.MDCScopeDecorator;
import brave.propagation.CurrentTraceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for distributed tracing with Zipkin
 */
@Configuration
public class TracingConfig {

    /**
     * Configures baggage field for branch ID
     */
    @Bean
    public BaggageField branchIdField() {
        return BaggageField.create("x-branch-id");
    }

    /**
     * Configures MDC integration for baggage fields
     */
    @Bean
    public CurrentTraceContext.ScopeDecorator mdcScopeDecorator() {
        return MDCScopeDecorator.newBuilder()
                .clear()
                .add(CorrelationScopeConfig.SingleCorrelationField.newBuilder(branchIdField())
                        .flushOnUpdate()
                        .build())
                .build();
    }
}