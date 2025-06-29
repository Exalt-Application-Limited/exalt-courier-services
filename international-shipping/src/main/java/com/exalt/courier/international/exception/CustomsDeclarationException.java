package com.exalt.courier.international.exception;

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
