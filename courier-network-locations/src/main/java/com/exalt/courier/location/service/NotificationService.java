package com.exalt.courier.location.service;

import java.util.List;
import java.util.Map;

import com.socialecommerceecosystem.location.model.ShipmentStatus;
import com.socialecommerceecosystem.location.model.WalkInCustomer;
import com.socialecommerceecosystem.location.model.WalkInShipment;

/**
 * Service interface for sending notifications related to courier network operations.
 * Provides high-level business functions for customer and staff notifications.
 */
public interface NotificationService {
    
    /**
     * Send a shipment status update notification to the customer.
     * 
     * @param shipment the shipment with updated status
     * @param oldStatus the previous status
     * @param newStatus the new status
     * @return true if the notification was sent successfully, false otherwise
     */
    boolean sendShipmentStatusUpdateNotification(WalkInShipment shipment, ShipmentStatus oldStatus, ShipmentStatus newStatus);
    
    /**
     * Send a shipment creation confirmation to the customer.
     * 
     * @param shipment the newly created shipment
     * @return true if the notification was sent successfully, false otherwise
     */
    boolean sendShipmentCreationConfirmation(WalkInShipment shipment);
    
    /**
     * Send a payment confirmation to the customer.
     * 
     * @param shipment the shipment that has been paid for
     * @param paymentDetails the payment details
     * @return true if the notification was sent successfully, false otherwise
     */
    boolean sendPaymentConfirmation(WalkInShipment shipment, Map<String, Object> paymentDetails);
    
    /**
     * Send a refund confirmation to the customer.
     * 
     * @param shipment the shipment that has been refunded
     * @param refundAmount the refund amount
     * @param reason the refund reason
     * @return true if the notification was sent successfully, false otherwise
     */
    boolean sendRefundConfirmation(WalkInShipment shipment, String refundAmount, String reason);
    
    /**
     * Send a pickup reminder to the recipient for shipments ready for pickup.
     * 
     * @param shipment the shipment that is ready for pickup
     * @return true if the notification was sent successfully, false otherwise
     */
    boolean sendPickupReminder(WalkInShipment shipment);
    
    /**
     * Send a delivery attempt notification to the recipient.
     * 
     * @param shipment the shipment with failed delivery attempt
     * @param attemptDetails the details of the delivery attempt
     * @return true if the notification was sent successfully, false otherwise
     */
    boolean sendDeliveryAttemptNotification(WalkInShipment shipment, Map<String, Object> attemptDetails);
    
    /**
     * Send a digital receipt to the customer.
     * 
     * @param shipment the shipment the receipt is for
     * @param receiptNumber the receipt number
     * @param receiptDetails the receipt details
     * @return true if the notification was sent successfully, false otherwise
     */
    boolean sendDigitalReceipt(WalkInShipment shipment, String receiptNumber, Map<String, Object> receiptDetails);
    
    /**
     * Send a marketing message to a list of customers who have consented to marketing.
     * 
     * @param customers the list of customers to send to
     * @param messageTitle the title of the marketing message
     * @param messageContent the content of the marketing message
     * @return the number of notifications successfully sent
     */
    int sendMarketingMessage(List<WalkInCustomer> customers, String messageTitle, String messageContent);
    
    /**
     * Send a custom notification to a specific customer.
     * 
     * @param customer the customer to notify
     * @param subject the notification subject
     * @param message the notification message
     * @param isHighPriority whether the notification is high priority
     * @return true if the notification was sent successfully, false otherwise
     */
    boolean sendCustomerNotification(WalkInCustomer customer, String subject, String message, boolean isHighPriority);
    
    /**
     * Send a staff notification about a high-value shipment.
     * 
     * @param shipment the high-value shipment
     * @param locationId the ID of the location
     * @return true if the notification was sent successfully, false otherwise
     */
    boolean notifyStaffAboutHighValueShipment(WalkInShipment shipment, Long locationId);
    
    /**
     * Send a staff notification about a payment issue.
     * 
     * @param shipment the shipment with payment issue
     * @param issueDetails the details of the payment issue
     * @param locationId the ID of the location
     * @return true if the notification was sent successfully, false otherwise
     */
    boolean notifyStaffAboutPaymentIssue(WalkInShipment shipment, String issueDetails, Long locationId);
    
    /**
     * Send a staff notification about high location capacity utilization.
     * 
     * @param locationId the ID of the location
     * @param utilizationPercentage the capacity utilization percentage
     * @return true if the notification was sent successfully, false otherwise
     */
    boolean notifyStaffAboutHighCapacityUtilization(Long locationId, double utilizationPercentage);
    
    /**
     * Send an exception notification to management staff.
     * 
     * @param subject the notification subject
     * @param message the notification message
     * @param details additional details
     * @return true if the notification was sent successfully, false otherwise
     */
    boolean sendExceptionNotification(String subject, String message, Map<String, Object> details);
    
    /**
     * Get notification statistics.
     * 
     * @return map of notification statistics
     */
    Map<String, Object> getNotificationStatistics();
    
    /**
     * Check if a customer can receive SMS notifications.
     * 
     * @param customer the customer to check
     * @return true if the customer can receive SMS notifications, false otherwise
     */
    boolean canReceiveSmsNotifications(WalkInCustomer customer);
    
    /**
     * Check if a customer can receive email notifications.
     * 
     * @param customer the customer to check
     * @return true if the customer can receive email notifications, false otherwise
     */
    boolean canReceiveEmailNotifications(WalkInCustomer customer);
}
