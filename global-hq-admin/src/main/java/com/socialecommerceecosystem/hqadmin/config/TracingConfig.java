package com.gogidix.courier.hqadmin.config;

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
     * Configures baggage field for tenant ID
     */
    @Bean
    public BaggageField tenantIdField() {
        return BaggageField.create("x-tenant-id");
    }

    /**
     * Configures MDC integration for baggage fields
     */
    @Bean
    public CurrentTraceContext.ScopeDecorator mdcScopeDecorator() {
        return MDCScopeDecorator.newBuilder()
                .clear()
                .add(CorrelationScopeConfig.SingleCorrelationField.newBuilder(tenantIdField())
                        .flushOnUpdate()
                        .build())
                .build();
    }
}