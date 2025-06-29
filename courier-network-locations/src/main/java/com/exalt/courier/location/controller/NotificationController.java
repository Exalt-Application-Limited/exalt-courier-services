package com.exalt.courier.location.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.socialecommerceecosystem.location.model.ShipmentStatus;
import com.socialecommerceecosystem.location.model.WalkInCustomer;
import com.socialecommerceecosystem.location.model.WalkInShipment;
import com.socialecommerceecosystem.location.service.NotificationService;
import com.socialecommerceecosystem.location.service.ShipmentProcessingService;
import com.socialecommerceecosystem.location.service.WalkInCustomerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for notification operations in the courier network.
 * Provides endpoints for sending various types of notifications to customers and staff.
 */
@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notification Management", description = "API for managing notifications in the courier network")
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;
    private final ShipmentProcessingService shipmentService;
    private final WalkInCustomerService customerService;

    @Autowired
    public NotificationController(
            NotificationService notificationService,
            ShipmentProcessingService shipmentService,
            WalkInCustomerService customerService) {
        this.notificationService = notificationService;
        this.shipmentService = shipmentService;
        this.customerService = customerService;
    }

    @PostMapping("/shipment-status")
    @Operation(summary = "Send shipment status update notification", 
            description = "Sends a status update notification for a shipment")
    @ApiResponse(responseCode = "200", description = "Notification sent successfully")
    @ApiResponse(responseCode = "404", description = "Shipment not found")
    public ResponseEntity<Map<String, Object>> sendShipmentStatusUpdateNotification(
            @Parameter(description = "ID of the shipment", required = true) @RequestParam Long shipmentId,
            @Parameter(description = "Old status", required = true) @RequestParam ShipmentStatus oldStatus,
            @Parameter(description = "New status", required = true) @RequestParam ShipmentStatus newStatus) {
        log.debug("REST request to send status update notification for shipment with ID: {}", shipmentId);
        
        try {
            WalkInShipment shipment = shipmentService.getShipmentById(shipmentId)
                    .orElseThrow(() -> new IllegalArgumentException("Shipment not found with ID: " + shipmentId));
            
            boolean sent = notificationService.sendShipmentStatusUpdateNotification(
                    shipment, oldStatus, newStatus);
            
            Map<String, Object> response = Map.of(
                    "success", sent,
                    "message", sent ? "Status update notification sent successfully" : 
                            "Failed to send status update notification"
            );
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Shipment not found with ID: {}", shipmentId);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/shipment-creation")
    @Operation(summary = "Send shipment creation confirmation", 
            description = "Sends a creation confirmation notification for a shipment")
    @ApiResponse(responseCode = "200", description = "Notification sent successfully")
    @ApiResponse(responseCode = "404", description = "Shipment not found")
    public ResponseEntity<Map<String, Object>> sendShipmentCreationConfirmation(
            @Parameter(description = "ID of the shipment", required = true) @RequestParam Long shipmentId) {
        log.debug("REST request to send creation confirmation for shipment with ID: {}", shipmentId);
        
        try {
            WalkInShipment shipment = shipmentService.getShipmentById(shipmentId)
                    .orElseThrow(() -> new IllegalArgumentException("Shipment not found with ID: " + shipmentId));
            
            boolean sent = notificationService.sendShipmentCreationConfirmation(shipment);
            
            Map<String, Object> response = Map.of(
                    "success", sent,
                    "message", sent ? "Creation confirmation sent successfully" : 
                            "Failed to send creation confirmation"
            );
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Shipment not found with ID: {}", shipmentId);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/payment-confirmation")
    @Operation(summary = "Send payment confirmation", 
            description = "Sends a payment confirmation notification for a shipment")
    @ApiResponse(responseCode = "200", description = "Notification sent successfully")
    @ApiResponse(responseCode = "404", description = "Shipment not found")
    public ResponseEntity<Map<String, Object>> sendPaymentConfirmation(
            @Parameter(description = "ID of the shipment", required = true) @RequestParam Long shipmentId,
            @Parameter(description = "Payment details", required = true) 
            @RequestBody Map<String, Object> paymentDetails) {
        log.debug("REST request to send payment confirmation for shipment with ID: {}", shipmentId);
        
        try {
            WalkInShipment shipment = shipmentService.getShipmentById(shipmentId)
                    .orElseThrow(() -> new IllegalArgumentException("Shipment not found with ID: " + shipmentId));
            
            boolean sent = notificationService.sendPaymentConfirmation(shipment, paymentDetails);
            
            Map<String, Object> response = Map.of(
                    "success", sent,
                    "message", sent ? "Payment confirmation sent successfully" : 
                            "Failed to send payment confirmation"
            );
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Shipment not found with ID: {}", shipmentId);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/refund-confirmation")
    @Operation(summary = "Send refund confirmation", 
            description = "Sends a refund confirmation notification for a shipment")
    @ApiResponse(responseCode = "200", description = "Notification sent successfully")
    @ApiResponse(responseCode = "404", description = "Shipment not found")
    public ResponseEntity<Map<String, Object>> sendRefundConfirmation(
            @Parameter(description = "ID of the shipment", required = true) @RequestParam Long shipmentId,
            @Parameter(description = "Refund amount", required = true) @RequestParam String refundAmount,
            @Parameter(description = "Refund reason", required = true) @RequestParam String reason) {
        log.debug("REST request to send refund confirmation for shipment with ID: {}", shipmentId);
        
        try {
            WalkInShipment shipment = shipmentService.getShipmentById(shipmentId)
                    .orElseThrow(() -> new IllegalArgumentException("Shipment not found with ID: " + shipmentId));
            
            boolean sent = notificationService.sendRefundConfirmation(shipment, refundAmount, reason);
            
            Map<String, Object> response = Map.of(
                    "success", sent,
                    "message", sent ? "Refund confirmation sent successfully" : 
                            "Failed to send refund confirmation"
            );
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Shipment not found with ID: {}", shipmentId);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/pickup-reminder")
    @Operation(summary = "Send pickup reminder", 
            description = "Sends a pickup reminder notification for a shipment")
    @ApiResponse(responseCode = "200", description = "Notification sent successfully")
    @ApiResponse(responseCode = "404", description = "Shipment not found")
    public ResponseEntity<Map<String, Object>> sendPickupReminder(
            @Parameter(description = "ID of the shipment", required = true) @RequestParam Long shipmentId) {
        log.debug("REST request to send pickup reminder for shipment with ID: {}", shipmentId);
        
        try {
            WalkInShipment shipment = shipmentService.getShipmentById(shipmentId)
                    .orElseThrow(() -> new IllegalArgumentException("Shipment not found with ID: " + shipmentId));
            
            boolean sent = notificationService.sendPickupReminder(shipment);
            
            Map<String, Object> response = Map.of(
                    "success", sent,
                    "message", sent ? "Pickup reminder sent successfully" : 
                            "Failed to send pickup reminder"
            );
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Shipment not found with ID: {}", shipmentId);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/delivery-attempt")
    @Operation(summary = "Send delivery attempt notification", 
            description = "Sends a delivery attempt notification for a shipment")
    @ApiResponse(responseCode = "200", description = "Notification sent successfully")
    @ApiResponse(responseCode = "404", description = "Shipment not found")
    public ResponseEntity<Map<String, Object>> sendDeliveryAttemptNotification(
            @Parameter(description = "ID of the shipment", required = true) @RequestParam Long shipmentId,
            @Parameter(description = "Delivery attempt details", required = true) 
            @RequestBody Map<String, Object> attemptDetails) {
        log.debug("REST request to send delivery attempt notification for shipment with ID: {}", shipmentId);
        
        try {
            WalkInShipment shipment = shipmentService.getShipmentById(shipmentId)
                    .orElseThrow(() -> new IllegalArgumentException("Shipment not found with ID: " + shipmentId));
            
            boolean sent = notificationService.sendDeliveryAttemptNotification(shipment, attemptDetails);
            
            Map<String, Object> response = Map.of(
                    "success", sent,
                    "message", sent ? "Delivery attempt notification sent successfully" : 
                            "Failed to send delivery attempt notification"
            );
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Shipment not found with ID: {}", shipmentId);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/digital-receipt")
    @Operation(summary = "Send digital receipt", 
            description = "Sends a digital receipt for a shipment")
    @ApiResponse(responseCode = "200", description = "Receipt sent successfully")
    @ApiResponse(responseCode = "404", description = "Shipment not found")
    public ResponseEntity<Map<String, Object>> sendDigitalReceipt(
            @Parameter(description = "ID of the shipment", required = true) @RequestParam Long shipmentId,
            @Parameter(description = "Receipt number", required = true) @RequestParam String receiptNumber,
            @Parameter(description = "Receipt details", required = true) 
            @RequestBody Map<String, Object> receiptDetails) {
        log.debug("REST request to send digital receipt for shipment with ID: {}", shipmentId);
        
        try {
            WalkInShipment shipment = shipmentService.getShipmentById(shipmentId)
                    .orElseThrow(() -> new IllegalArgumentException("Shipment not found with ID: " + shipmentId));
            
            boolean sent = notificationService.sendDigitalReceipt(shipment, receiptNumber, receiptDetails);
            
            Map<String, Object> response = Map.of(
                    "success", sent,
                    "message", sent ? "Digital receipt sent successfully" : 
                            "Failed to send digital receipt"
            );
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Shipment not found with ID: {}", shipmentId);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/marketing")
    @Operation(summary = "Send marketing message", 
            description = "Sends a marketing message to multiple customers")
    public ResponseEntity<Map<String, Object>> sendMarketingMessage(
            @Parameter(description = "List of customer IDs", required = true) 
            @RequestBody List<Long> customerIds,
            @Parameter(description = "Message title", required = true) @RequestParam String title,
            @Parameter(description = "Message content", required = true) @RequestParam String content) {
        log.debug("REST request to send marketing message to {} customers", customerIds.size());
        
        List<WalkInCustomer> customers = customerService.getCustomersByIds(customerIds);
        int sentCount = notificationService.sendMarketingMessage(customers, title, content);
        
        Map<String, Object> response = Map.of(
                "totalCustomers", customerIds.size(),
                "sentCount", sentCount,
                "failedCount", customerIds.size() - sentCount,
                "message", sentCount > 0 ? "Marketing message sent successfully to " + sentCount + " customers" : 
                        "Failed to send marketing message"
        );
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/customer")
    @Operation(summary = "Send custom customer notification", 
            description = "Sends a custom notification to a specific customer")
    @ApiResponse(responseCode = "200", description = "Notification sent successfully")
    @ApiResponse(responseCode = "404", description = "Customer not found")
    public ResponseEntity<Map<String, Object>> sendCustomerNotification(
            @Parameter(description = "ID of the customer", required = true) @RequestParam Long customerId,
            @Parameter(description = "Notification subject", required = true) @RequestParam String subject,
            @Parameter(description = "Notification message", required = true) @RequestParam String message,
            @Parameter(description = "High priority flag") 
            @RequestParam(required = false, defaultValue = "false") boolean highPriority) {
        log.debug("REST request to send custom notification to customer with ID: {}", customerId);
        
        try {
            WalkInCustomer customer = customerService.getCustomerById(customerId)
                    .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + customerId));
            
            boolean sent = notificationService.sendCustomerNotification(customer, subject, message, highPriority);
            
            Map<String, Object> response = Map.of(
                    "success", sent,
                    "message", sent ? "Customer notification sent successfully" : 
                            "Failed to send customer notification"
            );
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Customer not found with ID: {}", customerId);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/high-value-shipment")
    @Operation(summary = "Notify staff about high-value shipment", 
            description = "Sends a notification to staff about a high-value shipment")
    @ApiResponse(responseCode = "200", description = "Notification sent successfully")
    @ApiResponse(responseCode = "404", description = "Shipment not found")
    public ResponseEntity<Map<String, Object>> notifyStaffAboutHighValueShipment(
            @Parameter(description = "ID of the shipment", required = true) @RequestParam Long shipmentId,
            @Parameter(description = "ID of the location", required = true) @RequestParam Long locationId) {
        log.debug("REST request to notify staff about high-value shipment with ID: {}", shipmentId);
        
        try {
            WalkInShipment shipment = shipmentService.getShipmentById(shipmentId)
                    .orElseThrow(() -> new IllegalArgumentException("Shipment not found with ID: " + shipmentId));
            
            boolean sent = notificationService.notifyStaffAboutHighValueShipment(shipment, locationId);
            
            Map<String, Object> response = Map.of(
                    "success", sent,
                    "message", sent ? "Staff notification sent successfully" : 
                            "Failed to send staff notification"
            );
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Shipment not found with ID: {}", shipmentId);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/payment-issue")
    @Operation(summary = "Notify staff about payment issue", 
            description = "Sends a notification to staff about a payment issue")
    @ApiResponse(responseCode = "200", description = "Notification sent successfully")
    @ApiResponse(responseCode = "404", description = "Shipment not found")
    public ResponseEntity<Map<String, Object>> notifyStaffAboutPaymentIssue(
            @Parameter(description = "ID of the shipment", required = true) @RequestParam Long shipmentId,
            @Parameter(description = "Issue details", required = true) @RequestParam String issueDetails,
            @Parameter(description = "ID of the location", required = true) @RequestParam Long locationId) {
        log.debug("REST request to notify staff about payment issue for shipment with ID: {}", shipmentId);
        
        try {
            WalkInShipment shipment = shipmentService.getShipmentById(shipmentId)
                    .orElseThrow(() -> new IllegalArgumentException("Shipment not found with ID: " + shipmentId));
            
            boolean sent = notificationService.notifyStaffAboutPaymentIssue(shipment, issueDetails, locationId);
            
            Map<String, Object> response = Map.of(
                    "success", sent,
                    "message", sent ? "Staff notification sent successfully" : 
                            "Failed to send staff notification"
            );
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Shipment not found with ID: {}", shipmentId);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/high-capacity")
    @Operation(summary = "Notify staff about high capacity utilization", 
            description = "Sends a notification to staff about high capacity utilization")
    @ApiResponse(responseCode = "200", description = "Notification sent successfully")
    public ResponseEntity<Map<String, Object>> notifyStaffAboutHighCapacityUtilization(
            @Parameter(description = "ID of the location", required = true) @RequestParam Long locationId,
            @Parameter(description = "Utilization percentage", required = true) 
            @RequestParam double utilizationPercentage) {
        log.debug("REST request to notify staff about high capacity utilization at location with ID: {}", 
                locationId);
        
        boolean sent = notificationService.notifyStaffAboutHighCapacityUtilization(
                locationId, utilizationPercentage);
        
        Map<String, Object> response = Map.of(
                "success", sent,
                "message", sent ? "Staff notification sent successfully" : 
                        "Failed to send staff notification"
        );
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/exception")
    @Operation(summary = "Send exception notification", 
            description = "Sends an exception notification to management staff")
    public ResponseEntity<Map<String, Object>> sendExceptionNotification(
            @Parameter(description = "Notification subject", required = true) @RequestParam String subject,
            @Parameter(description = "Notification message", required = true) @RequestParam String message,
            @Parameter(description = "Additional details", required = true) 
            @RequestBody Map<String, Object> details) {
        log.debug("REST request to send exception notification: {}", subject);
        
        boolean sent = notificationService.sendExceptionNotification(subject, message, details);
        
        Map<String, Object> response = Map.of(
                "success", sent,
                "message", sent ? "Exception notification sent successfully" : 
                        "Failed to send exception notification"
        );
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get notification statistics", 
            description = "Retrieves statistics about notifications")
    public ResponseEntity<Map<String, Object>> getNotificationStatistics() {
        log.debug("REST request to get notification statistics");
        Map<String, Object> statistics = notificationService.getNotificationStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/can-receive/sms/{customerId}")
    @Operation(summary = "Check if customer can receive SMS", 
            description = "Checks if a customer can receive SMS notifications")
    @ApiResponse(responseCode = "200", description = "Check successful")
    @ApiResponse(responseCode = "404", description = "Customer not found")
    public ResponseEntity<Boolean> canReceiveSmsNotifications(
            @Parameter(description = "ID of the customer", required = true) @PathVariable Long customerId) {
        log.debug("REST request to check if customer with ID: {} can receive SMS", customerId);
        
        try {
            WalkInCustomer customer = customerService.getCustomerById(customerId)
                    .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + customerId));
            
            boolean canReceive = notificationService.canReceiveSmsNotifications(customer);
            return ResponseEntity.ok(canReceive);
        } catch (IllegalArgumentException e) {
            log.error("Customer not found with ID: {}", customerId);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/can-receive/email/{customerId}")
    @Operation(summary = "Check if customer can receive email", 
            description = "Checks if a customer can receive email notifications")
    @ApiResponse(responseCode = "200", description = "Check successful")
    @ApiResponse(responseCode = "404", description = "Customer not found")
    public ResponseEntity<Boolean> canReceiveEmailNotifications(
            @Parameter(description = "ID of the customer", required = true) @PathVariable Long customerId) {
        log.debug("REST request to check if customer with ID: {} can receive email", customerId);
        
        try {
            WalkInCustomer customer = customerService.getCustomerById(customerId)
                    .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + customerId));
            
            boolean canReceive = notificationService.canReceiveEmailNotifications(customer);
            return ResponseEntity.ok(canReceive);
        } catch (IllegalArgumentException e) {
            log.error("Customer not found with ID: {}", customerId);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/notification-status/{customerId}")
    @Operation(summary = "Get customer notification status", 
            description = "Gets the notification status for a customer")
    @ApiResponse(responseCode = "200", description = "Status retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Customer not found")
    public ResponseEntity<Map<String, Boolean>> getCustomerNotificationStatus(
            @Parameter(description = "ID of the customer", required = true) @PathVariable Long customerId) {
        log.debug("REST request to get notification status for customer with ID: {}", customerId);
        
        try {
            WalkInCustomer customer = customerService.getCustomerById(customerId)
                    .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + customerId));
            
            Map<String, Boolean> status = new HashMap<>();
            status.put("canReceiveSms", notificationService.canReceiveSmsNotifications(customer));
            status.put("canReceiveEmail", notificationService.canReceiveEmailNotifications(customer));
            
            return ResponseEntity.ok(status);
        } catch (IllegalArgumentException e) {
            log.error("Customer not found with ID: {}", customerId);
            return ResponseEntity.notFound().build();
        }
    }
}
