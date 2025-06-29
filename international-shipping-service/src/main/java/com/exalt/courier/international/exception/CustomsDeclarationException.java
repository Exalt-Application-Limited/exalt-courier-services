package com.exalt.courierservices.international-shipping.$1;

/**
 * Exception thrown when there are issues with the customs declaration.
 */
public class CustomsDeclarationException extends RuntimeException {

    public CustomsDeclarationException(String message) {
        super(message);
    }

    public CustomsDeclarationException(String message, Throwable cause) {
        super(message, cause);
    }
}
