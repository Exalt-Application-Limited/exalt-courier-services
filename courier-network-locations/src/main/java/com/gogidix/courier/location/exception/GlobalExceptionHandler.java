package com.gogidix.courier.location.exception;

import com.microecosystem.courier.shared.exception.GlobalExceptionHandlerBase;

import org.springframework.web.bind.annotation.ControllerAdvice;

import lombok.extern.slf4j.Slf4j;

/**
 * Global exception handler for the Courier Network Locations Service.
 * Extends the shared GlobalExceptionHandlerBase for common exception handling.
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends GlobalExceptionHandlerBase {
    
    // Add any service-specific exception handlers here if needed
}
