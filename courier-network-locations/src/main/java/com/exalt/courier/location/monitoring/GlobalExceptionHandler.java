package com.exalt.courier.location.monitoring;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import jakarta.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler that integrates with the alerting system.
 * Catches exceptions, logs them, sends alerts, and returns appropriate responses.
 */
@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final AlertService alertService;
    
    /**
     * Handle entity not found exceptions.
     * 
     * @param ex the exception
     * @param request the web request
     * @return the response entity
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException ex, WebRequest request) {
        String message = ex.getMessage();
        log.warn("Entity not found: {}", message);
        
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Not Found");
        body.put("message", message);
        
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }
    
    /**
     * Handle illegal argument exceptions.
     * 
     * @param ex the exception
     * @param request the web request
     * @return the response entity
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException ex, WebRequest request) {
        String message = ex.getMessage();
        log.warn("Invalid request argument: {}", message);
        
        // Track the error but don't alert (not critical)
        alertService.trackError("INVALID_ARGUMENT", message, false);
        
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Bad Request");
        body.put("message", message);
        
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handle database exceptions.
     * 
     * @param ex the exception
     * @param request the web request
     * @return the response entity
     */
    @ExceptionHandler(javax.persistence.PersistenceException.class)
    public ResponseEntity<Object> handleDatabaseError(javax.persistence.PersistenceException ex, WebRequest request) {
        String message = ex.getMessage();
        log.error("Database error: {}", message, ex);
        
        // Track the database error and alert (critical)
        alertService.trackDatabaseError("OPERATION", "ENTITY", ex);
        
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Internal Server Error");
        body.put("message", "A database error occurred. Please try again later.");
        
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    /**
     * Handle all other exceptions.
     * 
     * @param ex the exception
     * @param request the web request
     * @return the response entity
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex, WebRequest request) {
        String message = ex.getMessage();
        log.error("Unhandled exception: {}", message, ex);
        
        // Track the error and alert (critical)
        alertService.trackError("UNHANDLED_EXCEPTION", message, true);
        
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Internal Server Error");
        body.put("message", "An unexpected error occurred. Please try again later.");
        
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
