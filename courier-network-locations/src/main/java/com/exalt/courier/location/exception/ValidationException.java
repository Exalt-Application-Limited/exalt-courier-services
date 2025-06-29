package com.exalt.courier.location.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when validation fails.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ValidationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new ValidationException with the specified detail message.
     * 
     * @param message the detail message
     */
    public ValidationException(String message) {
        super(message);
    }

    /**
     * Constructs a new ValidationException with the specified detail message and cause.
     * 
     * @param message the detail message
     * @param cause the cause
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Helper method to create a ValidationException for a specific field.
     * 
     * @param field the field name
     * @param problem the validation problem
     * @return a new ValidationException with a formatted message
     */
    public static ValidationException forField(String field, String problem) {
        return new ValidationException("Invalid " + field + ": " + problem);
    }
    
    /**
     * Helper method to create a ValidationException for a specific operation.
     * 
     * @param operation the operation name
     * @param problem the validation problem
     * @return a new ValidationException with a formatted message
     */
    public static ValidationException forOperation(String operation, String problem) {
        return new ValidationException("Cannot " + operation + ": " + problem);
    }
}
