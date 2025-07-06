package com.gogidix.courier.courier.hqadmin.service;

import brave.Span;
import brave.Tracer;
import brave.baggage.BaggageField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for managing distributed tracing and spans in business operations
 */
@Service
public class TracingService {

    private final Tracer tracer;
    private final BaggageField tenantIdField;

    @Autowired
    public TracingService(Tracer tracer, BaggageField tenantIdField) {
        this.tracer = tracer;
        this.tenantIdField = tenantIdField;
    }

    /**
     * Creates a new span for a business operation
     * 
     * @param operationName Name of the operation
     * @return New span
     */
    public Span startSpan(String operationName) {
        return tracer.nextSpan().name(operationName).start();
    }
    
    /**
     * Adds a tag to the current span
     * 
     * @param key Tag key
     * @param value Tag value
     */
    public void addTag(String key, String value) {
        Span currentSpan = tracer.currentSpan();
        if (currentSpan != null) {
            currentSpan.tag(key, value);
        }
    }
    
    /**
     * Sets the tenant ID in the current trace context
     * 
     * @param tenantId Tenant ID
     */
    public void setTenantId(String tenantId) {
        tenantIdField.updateValue(tenantId);
    }
    
    /**
     * Gets the current tenant ID from the trace context
     * 
     * @return Current tenant ID or null if not set
     */
    public String getTenantId() {
        return tenantIdField.getValue();
    }
    
    /**
     * Records an error in the current span
     * 
     * @param error The error to record
     */
    public void recordError(Throwable error) {
        Span currentSpan = tracer.currentSpan();
        if (currentSpan != null) {
            currentSpan.error(error);
        }
    }
}