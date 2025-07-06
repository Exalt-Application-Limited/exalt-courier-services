package com.gogidix.courier.courier.service;

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
    private final BaggageField branchIdField;

    @Autowired
    public TracingService(Tracer tracer, BaggageField branchIdField) {
        this.tracer = tracer;
        this.branchIdField = branchIdField;
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
     * Sets the branch ID in the current trace context
     * 
     * @param branchId Branch ID
     */
    public void setBranchId(String branchId) {
        branchIdField.updateValue(branchId);
    }
    
    /**
     * Gets the current branch ID from the trace context
     * 
     * @return Current branch ID or null if not set
     */
    public String getBranchId() {
        return branchIdField.getValue();
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