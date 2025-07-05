package com.gogidix.courier.international.exception;

/**
 * Exception thrown when there are communication issues with the carrier.
 */
public class CarrierCommunicationException extends RuntimeException {

    public CarrierCommunicationException(String message) {
        super(message);
    }

    public CarrierCommunicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
