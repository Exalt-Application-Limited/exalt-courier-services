package com.exalt.courier.shared.exception;

/**
 * Exception thrown when a resource is not found.
 * This exception is used across all courier services to indicate
 * that a requested resource does not exist.
 */
public class ResourceNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new ResourceNotFoundException with the specified message.
     *
     * @param message the detail message
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Creates a new ResourceNotFoundException with the specified message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Utility method to create a ResourceNotFoundException for a specific resource type and identifier.
     *
     * @param resourceType the type of resource that was not found
     * @param id the identifier of the resource that was not found
     * @return a new ResourceNotFoundException with an appropriate message
     */
    public static ResourceNotFoundException forResource(String resourceType, Object id) {
        return new ResourceNotFoundException(resourceType + " not found with ID: " + id);
    }
}
