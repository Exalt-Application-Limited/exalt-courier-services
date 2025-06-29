package com.exalt.courier.shared.dashboard;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Interface for dashboard communication service that enables
 * sending and receiving messages between different dashboard levels.
 */
public interface DashboardCommunicationService {
    
    /**
     * Send a message to another dashboard level.
     * 
     * @param message The message to send
     * @return CompletableFuture with true if sent successfully, false otherwise
     */
    CompletableFuture<Boolean> sendMessage(DashboardMessage message);
    
    /**
     * Send a message to multiple dashboard levels or instances.
     * 
     * @param message The message to send
     * @param targetLevels List of target dashboard levels
     * @param targetIds List of target dashboard IDs (can be null for broadcast to all instances of a level)
     * @return Map of target IDs to success/failure status
     */
    CompletableFuture<Map<String, Boolean>> broadcastMessage(DashboardMessage message, 
                                                           List<String> targetLevels,
                                                           List<String> targetIds);
    
    /**
     * Register a callback handler for receiving messages.
     * 
     * @param handler The message handler to register
     * @return The registration ID
     */
    String registerMessageHandler(DashboardMessageHandler handler);
    
    /**
     * Unregister a previously registered message handler.
     * 
     * @param registrationId The registration ID returned from registerMessageHandler
     * @return True if unregistered successfully, false otherwise
     */
    boolean unregisterMessageHandler(String registrationId);
    
    /**
     * Acknowledge receipt of a message.
     * 
     * @param messageId The ID of the message to acknowledge
     * @return True if acknowledged successfully, false otherwise
     */
    boolean acknowledgeMessage(String messageId);
    
    /**
     * Get pending messages for the current dashboard.
     * 
     * @param limit Maximum number of messages to retrieve
     * @return List of pending messages
     */
    List<DashboardMessage> getPendingMessages(int limit);
    
    /**
     * Check if the service is connected to the messaging infrastructure.
     * 
     * @return True if connected, false otherwise
     */
    boolean isConnected();
    
    /**
     * Get the current dashboard level.
     * 
     * @return The dashboard level (GLOBAL, REGIONAL, LOCAL)
     */
    String getCurrentDashboardLevel();
    
    /**
     * Get the current dashboard ID.
     * 
     * @return The dashboard ID
     */
    String getCurrentDashboardId();
}
