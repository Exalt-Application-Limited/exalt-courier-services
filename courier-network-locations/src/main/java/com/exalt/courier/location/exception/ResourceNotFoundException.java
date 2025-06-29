package com.exalt.courier.location.exception;

import com.microecosystem.courier.shared.exception.ResourceNotFoundException;

/**
 * This class is a service-specific extension of the shared ResourceNotFoundException.
 * It allows us to maintain compatibility with existing code while using the shared exception.
 */
public class LocationResourceNotFoundException extends ResourceNotFoundException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new LocationResourceNotFoundException with the specified message.
     *
     * @param message the detail message
     */
    public LocationResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Creates a new LocationResourceNotFoundException with the specified message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public LocationResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Utility method to create a ResourceNotFoundException for a specific location resource and ID.
     *
     * @param resourceType the type of location resource that was not found
     * @param id the identifier of the resource that was not found
     * @return a new ResourceNotFoundException with an appropriate message
     */
    public static LocationResourceNotFoundException forResource(String resourceType, Object id) {
        return new LocationResourceNotFoundException(resourceType + " not found with ID: " + id);
    }
}
