package com.gogidix.integration.common.exception;

/**
 * Exception thrown for errors during integration with third-party shipping providers.
 * This provides a unified way to handle errors across different provider integrations.
 */
public class IntegrationException extends Exception {

    private final String providerCode;
    private final String errorCode;
    private final ErrorSeverity severity;

    /**
     * Create a new integration exception with default severity (ERROR)
     * 
     * @param providerCode The code of the provider where the error occurred
     * @param message Error message
     */
    public IntegrationException(String providerCode, String message) {
        this(providerCode, null, message, null, ErrorSeverity.ERROR);
    }

    /**
     * Create a new integration exception with default severity (ERROR)
     * 
     * @param providerCode The code of the provider where the error occurred
     * @param message Error message
     * @param cause The cause of the exception
     */
    public IntegrationException(String providerCode, String message, Throwable cause) {
        this(providerCode, null, message, cause, ErrorSeverity.ERROR);
    }

    /**
     * Create a new integration exception with specific error code and default severity (ERROR)
     * 
     * @param providerCode The code of the provider where the error occurred
     * @param errorCode Provider-specific error code
     * @param message Error message
     */
    public IntegrationException(String providerCode, String errorCode, String message) {
        this(providerCode, errorCode, message, null, ErrorSeverity.ERROR);
    }

    /**
     * Create a new integration exception with specific error code and severity
     * 
     * @param providerCode The code of the provider where the error occurred
     * @param errorCode Provider-specific error code
     * @param message Error message
     * @param severity Error severity level
     */
    public IntegrationException(String providerCode, String errorCode, String message, ErrorSeverity severity) {
        this(providerCode, errorCode, message, null, severity);
    }

    /**
     * Create a new integration exception with all details
     * 
     * @param providerCode The code of the provider where the error occurred
     * @param errorCode Provider-specific error code
     * @param message Error message
     * @param cause The cause of the exception
     * @param severity Error severity level
     */
    public IntegrationException(String providerCode, String errorCode, String message, Throwable cause, ErrorSeverity severity) {
        super(message, cause);
        this.providerCode = providerCode;
        this.errorCode = errorCode;
        this.severity = severity;
    }

    /**
     * Get the provider code where the error occurred
     * 
     * @return Provider code
     */
    public String getProviderCode() {
        return providerCode;
    }

    /**
     * Get the provider-specific error code
     * 
     * @return Error code or null if not available
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Get the severity level of the error
     * 
     * @return Error severity
     */
    public ErrorSeverity getSeverity() {
        return severity;
    }

    /**
     * Severity levels for integration errors
     */
    public enum ErrorSeverity {
        /**
         * Informational messages that don't impact the overall operation
         */
        INFO,
        
        /**
         * Warnings that might require attention but don't prevent the operation
         */
        WARNING,
        
        /**
         * Errors that prevented the current operation but don't impact system stability
         */
        ERROR,
        
        /**
         * Severe errors that might impact system stability and require immediate attention
         */
        FATAL
    }
}
