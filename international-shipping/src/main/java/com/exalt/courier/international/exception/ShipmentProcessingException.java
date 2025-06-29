package com.exalt.courier.international.exception;

/**
 * Exception thrown when there are issues processing a shipment.
 */
public class ShipmentProcessingException extends RuntimeException {

    public ShipmentProcessingException(String message) {
        super(message);
    }

    public ShipmentProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
