package com.exalt.courier.shared.dashboard;

/**
 * Interface for handling incoming dashboard messages.
 */
@FunctionalInterface
public interface DashboardMessageHandler {
    
    /**
     * Handle an incoming message.
     * 
     * @param message The received message
     * @return Response message if needed, or null for no response
     */
    DashboardMessage handleMessage(DashboardMessage message);
}
