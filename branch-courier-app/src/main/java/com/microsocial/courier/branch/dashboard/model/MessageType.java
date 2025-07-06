package com.gogidix.courier.courier.branch.dashboard.model;

/**
 * Enum defining different types of messages that can be exchanged between dashboards.
 */
public enum MessageType {
    
    // Request for data/metrics
    DATA_REQUEST,
    
    // Response to a data request
    DATA_RESPONSE,
    
    // Configuration update notification
    CONFIGURATION_UPDATE,
    
    // Alert/warning message
    ALERT,
    
    // Notification message (informational)
    NOTIFICATION,
    
    // Command to be executed
    COMMAND,
    
    // Acknowledgment of message receipt
    ACKNOWLEDGMENT,
    
    // Status update message
    STATUS_UPDATE,
    
    // Error notification
    ERROR,
    
    // Heartbeat/ping message
    HEARTBEAT
} 