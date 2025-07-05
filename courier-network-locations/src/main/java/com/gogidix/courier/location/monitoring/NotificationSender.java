package com.gogidix.courier.location.monitoring;

/**
 * Interface for sending alert notifications through various channels.
 */
public interface NotificationSender {
    
    /**
     * Send an alert notification.
     * 
     * @param subject the alert subject
     * @param body the alert body
     * @param critical whether the alert is critical
     */
    void sendAlertNotification(String subject, String body, boolean critical);
    
    /**
     * Send a system status notification.
     * 
     * @param subject the notification subject
     * @param body the notification body
     */
    void sendStatusNotification(String subject, String body);
}
