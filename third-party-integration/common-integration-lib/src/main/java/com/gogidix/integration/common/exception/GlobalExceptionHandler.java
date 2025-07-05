package com.gogidix.integration.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception handler for consistent error responses across the application.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handle integration exceptions
     */
    @ExceptionHandler(IntegrationException.class)
    public ResponseEntity<Map<String, Object>> handleIntegrationException(IntegrationException ex) {
        log.error("Integration exception occurred: {}", ex.getMessage(), ex);
        
        Map<String, Object> errorResponse = createErrorResponse(
                ex.getMessage(), 
                "INTEGRATION_ERROR", 
                ex.getProviderCode(), 
                ex.getErrorCode());
        
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        
        // Determine HTTP status based on error severity
        if (ex.getSeverity() == IntegrationException.ErrorSeverity.INFO) {
            status = HttpStatus.OK;
        } else if (ex.getSeverity() == IntegrationException.ErrorSeverity.WARNING) {
            status = HttpStatus.BAD_REQUEST;
        } else if (ex.getSeverity() == IntegrationException.ErrorSeverity.FATAL) {
            status = HttpStatus.SERVICE_UNAVAILABLE;
        }
        
        return ResponseEntity.status(status).body(errorResponse);
    }
    
    /**
     * Handle validation exceptions (request body validation)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("Validation exception occurred: {}", ex.getMessage());
        
        // Collect all field errors
        Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> error.getDefaultMessage() == null ? "Invalid value" : error.getDefaultMessage(),
                        (existing, replacement) -> existing + "; " + replacement
                ));
        
        Map<String, Object> errorResponse = createErrorResponse(
                "Validation failed for request",
                "VALIDATION_ERROR",
                null,
                null);
        errorResponse.put("fieldErrors", fieldErrors);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    /**
     * Handle constraint violation exceptions (parameter validation)
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolationException(ConstraintViolationException ex) {
        log.error("Constraint violation exception occurred: {}", ex.getMessage());
        
        // Collect all constraint violations
        Map<String, String> violations = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        violation -> violation.getMessage(),
                        (existing, replacement) -> existing + "; " + replacement
                ));
        
        Map<String, Object> errorResponse = createErrorResponse(
                "Constraint violations detected",
                "CONSTRAINT_VIOLATION",
                null,
                null);
        errorResponse.put("violations", violations);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    /**
     * Handle entity not found exceptions
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleEntityNotFoundException(EntityNotFoundException ex) {
        log.error("Entity not found exception occurred: {}", ex.getMessage());
        
        Map<String, Object> errorResponse = createErrorResponse(
                ex.getMessage(),
                "ENTITY_NOT_FOUND",
                null,
                null);
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    
    /**
     * Handle all other unhandled exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        log.error("Unhandled exception occurred: {}", ex.getMessage(), ex);
        
        Map<String, Object> errorResponse = createErrorResponse(
                "An unexpected error occurred",
                "INTERNAL_SERVER_ERROR",
                null,
                null);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
    
    /**
     * Create standard error response structure
     */
    private Map<String, Object> createErrorResponse(String message, String errorType, 
                                                   String providerCode, String errorCode) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("message", message);
        errorResponse.put("errorType", errorType);
        
        if (providerCode != null) {
            errorResponse.put("providerCode", providerCode);
        }
        
        if (errorCode != null) {
            errorResponse.put("errorCode", errorCode);
        }
        
        return errorResponse;
    }
}
