package com.exalt.courier.shared.dashboard;

/**
 * Enumeration of message types used in dashboard communication.
 */
public class MessageType {
    
    public static final String DATA_REQUEST = "DATA_REQUEST";
    public static final String DATA_RESPONSE = "DATA_RESPONSE";
    public static final String MESSAGE_ACK = "MESSAGE_ACK";
    public static final String HEALTH_CHECK = "HEALTH_CHECK";
    public static final String CONFIGURATION_UPDATE = "CONFIGURATION_UPDATE";
    public static final String ALERT = "ALERT";
    public static final String NOTIFICATION = "NOTIFICATION";
    
    private MessageType() {
        // Utility class - prevent instantiation
    }
}
