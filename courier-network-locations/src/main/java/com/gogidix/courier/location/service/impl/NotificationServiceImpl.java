package com.gogidix.courier.location.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.socialecommerceecosystem.location.model.LocationStaff;
import com.socialecommerceecosystem.location.model.PhysicalLocation;
import com.socialecommerceecosystem.location.model.ShipmentStatus;
import com.socialecommerceecosystem.location.model.WalkInCustomer;
import com.socialecommerceecosystem.location.model.WalkInShipment;
import com.socialecommerceecosystem.location.repository.LocationStaffRepository;
import com.socialecommerceecosystem.location.repository.PhysicalLocationRepository;
import com.socialecommerceecosystem.location.service.NotificationService;

import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of the NotificationService interface.
 * Provides business logic for sending notifications to customers and staff.
 */
@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final LocationStaffRepository staffRepository;
    private final PhysicalLocationRepository locationRepository;
    private JavaMailSender emailSender;
    
    // For testing/development, we track metrics here. In production, use a proper metrics service
    private final Map<String, AtomicLong> notificationCounts = new ConcurrentHashMap<>();
    
    @Value("${notification.email.enabled:false}")
    private boolean emailEnabled;
    
    @Value("${notification.sms.enabled:false}")
    private boolean smsEnabled;
    
    @Value("${notification.email.from:courier-network@example.com}")
    private String emailFrom;

    @Autowired
    public NotificationServiceImpl(
            LocationStaffRepository staffRepository,
            PhysicalLocationRepository locationRepository,
            JavaMailSender emailSender) {
        this.staffRepository = staffRepository;
        this.locationRepository = locationRepository;
        this.emailSender = emailSender;
        
        // Initialize notification counters
        notificationCounts.put("email", new AtomicLong(0));
        notificationCounts.put("sms", new AtomicLong(0));
        notificationCounts.put("shipment_status", new AtomicLong(0));
        notificationCounts.put("payment", new AtomicLong(0));
        notificationCounts.put("staff", new AtomicLong(0));
        notificationCounts.put("exception", new AtomicLong(0));
        
        log.info("NotificationServiceImpl initialized");
        
    }
    
    @Override
    @Async
    public boolean sendShipmentStatusUpdateNotification(WalkInShipment shipment, ShipmentStatus oldStatus, ShipmentStatus newStatus) {
        log.debug("Sending shipment status update notification for shipment ID: {}, status change from {} to {}", 
                 shipment.getId(), oldStatus, newStatus);
        
        if (shipment == null || shipment.getCustomer() == null) {
            log.warn("Cannot send shipment status update notification: shipment or customer is null");
            return false;
        }
        
        // Increment counter
        notificationCounts.get("shipment_status").incrementAndGet();
        
        WalkInCustomer customer = shipment.getCustomer();
        PhysicalLocation location = getLocationForShipment(shipment);
        String locationName = formatLocation(location);
        
        // Prepare notification content
        String subject = "Shipment Status Update - " + shipment.getTrackingNumber();
        String message = String.format(
                "Dear %s,\n\n" +
                "Your shipment with tracking number %s has changed status from %s to %s.\n\n" +
                "Current location: %s\n\n" +
                "For more details, please visit our website or contact the courier location.\n\n" +
                "Thank you for using our services.\n\n" +
                "Best regards,\nCourier Network Team",
                customer.getName(),
                shipment.getTrackingNumber(),
                oldStatus,
                newStatus,
                locationName);
        
        // Send notifications based on customer preferences
        boolean emailSent = false;
        boolean smsSent = false;
        
        if (canReceiveEmailNotifications(customer) && customer.getEmail() != null) {
            emailSent = sendEmail(customer.getEmail(), subject, message);
            if (emailSent) {
                notificationCounts.get("email").incrementAndGet();
            }
        }
        
        if (canReceiveSmsNotifications(customer) && customer.getPhone() != null) {
            // Shorter message for SMS
            String smsMessage = String.format(
                    "Your shipment %s status changed from %s to %s. Location: %s",
                    shipment.getTrackingNumber(), oldStatus, newStatus, locationName);
            smsSent = sendSms(customer.getPhone(), smsMessage);
            if (smsSent) {
                notificationCounts.get("sms").incrementAndGet();
            }
        }
        
        log.info("Shipment status update notification sent for shipment ID: {}, email: {}, SMS: {}", 
                shipment.getId(), emailSent, smsSent);
                
        return emailSent || smsSent;
    }
    
    @Override
    @Async
    public boolean sendShipmentCreationConfirmation(WalkInShipment shipment) {
        log.debug("Sending shipment creation confirmation for shipment ID: {}", shipment.getId());
        
        if (shipment == null || shipment.getCustomer() == null) {
            log.warn("Cannot send shipment creation confirmation: shipment or customer is null");
            return false;
        }
        
        // Increment counter
        notificationCounts.get("shipment_status").incrementAndGet();
        
        WalkInCustomer customer = shipment.getCustomer();
        PhysicalLocation location = getLocationForShipment(shipment);
        String locationName = formatLocation(location);
        
        // Prepare notification content
        String subject = "Shipment Confirmation - " + shipment.getTrackingNumber();
        String message = String.format(
                "Dear %s,\n\n" +
                "Thank you for using our courier services. Your shipment has been created successfully.\n\n" +
                "Tracking Number: %s\n" +
                "Origin Location: %s\n" +
                "Destination: %s\n" +
                "Estimated Delivery: %s\n\n" +
                "You can track your shipment status using the tracking number on our website.\n\n" +
                "Thank you for your trust.\n\n" +
                "Best regards,\nCourier Network Team",
                customer.getName(),
                shipment.getTrackingNumber(),
                locationName,
                shipment.getDestinationAddress(),
                shipment.getEstimatedDeliveryDate());
        
        // Send notifications based on customer preferences
        boolean emailSent = false;
        boolean smsSent = false;
        
        if (canReceiveEmailNotifications(customer) && customer.getEmail() != null) {
            emailSent = sendEmail(customer.getEmail(), subject, message);
            if (emailSent) {
                notificationCounts.get("email").incrementAndGet();
            }
        }
        
        if (canReceiveSmsNotifications(customer) && customer.getPhone() != null) {
            // Shorter message for SMS
            String smsMessage = String.format(
                    "Your shipment has been created. Tracking #: %s, Est. Delivery: %s",
                    shipment.getTrackingNumber(), shipment.getEstimatedDeliveryDate());
            smsSent = sendSms(customer.getPhone(), smsMessage);
            if (smsSent) {
                notificationCounts.get("sms").incrementAndGet();
            }
        }
        
        log.info("Shipment creation confirmation sent for shipment ID: {}, email: {}, SMS: {}", 
                shipment.getId(), emailSent, smsSent);
                
        return emailSent || smsSent;
    }
    
    @Override
    @Async
    public boolean sendPaymentConfirmation(WalkInShipment shipment, Map<String, Object> paymentDetails) {
        log.debug("Sending payment confirmation for shipment ID: {}", shipment.getId());
        
        if (shipment == null || shipment.getCustomer() == null) {
            log.warn("Cannot send payment confirmation: shipment or customer is null");
            return false;
        }
        
        if (paymentDetails == null || paymentDetails.isEmpty()) {
            log.warn("Cannot send payment confirmation: payment details are null or empty");
            return false;
        }
        
        // Increment counter
        notificationCounts.get("payment").incrementAndGet();
        
        WalkInCustomer customer = shipment.getCustomer();
        
        // Extract payment details
        String amount = paymentDetails.containsKey("amount") ? paymentDetails.get("amount").toString() : "N/A";
        String method = paymentDetails.containsKey("method") ? paymentDetails.get("method").toString() : "N/A";
        String transactionId = paymentDetails.containsKey("transactionId") ? paymentDetails.get("transactionId").toString() : "N/A";
        String date = paymentDetails.containsKey("date") ? paymentDetails.get("date").toString() : "N/A";
        
        // Prepare notification content
        String subject = "Payment Confirmation - Shipment " + shipment.getTrackingNumber();
        String message = String.format(
                "Dear %s,\n\n" +
                "We have received your payment for shipment with tracking number %s.\n\n" +
                "Payment Details:\n" +
                "Amount: %s\n" +
                "Payment Method: %s\n" +
                "Transaction ID: %s\n" +
                "Date: %s\n\n" +
                "Your shipment will be processed accordingly.\n\n" +
                "Thank you for using our services.\n\n" +
                "Best regards,\nCourier Network Team",
                customer.getName(),
                shipment.getTrackingNumber(),
                amount,
                method,
                transactionId,
                date);
        
        // Send notifications based on customer preferences
        boolean emailSent = false;
        boolean smsSent = false;
        
        if (canReceiveEmailNotifications(customer) && customer.getEmail() != null) {
            emailSent = sendEmail(customer.getEmail(), subject, message);
            if (emailSent) {
                notificationCounts.get("email").incrementAndGet();
            }
        }
        
        if (canReceiveSmsNotifications(customer) && customer.getPhone() != null) {
            // Shorter message for SMS
            String smsMessage = String.format(
                    "Payment of %s received for shipment %s. Transaction ID: %s",
                    amount, shipment.getTrackingNumber(), transactionId);
            smsSent = sendSms(customer.getPhone(), smsMessage);
            if (smsSent) {
                notificationCounts.get("sms").incrementAndGet();
            }
        }
        
        log.info("Payment confirmation sent for shipment ID: {}, email: {}, SMS: {}", 
                shipment.getId(), emailSent, smsSent);
                
        return emailSent || smsSent;
    }
    
    @Override
    @Async
    public boolean sendRefundConfirmation(WalkInShipment shipment, String refundAmount, String reason) {
        log.debug("Sending refund confirmation for shipment ID: {}, amount: {}", shipment.getId(), refundAmount);
        
        if (shipment == null || shipment.getCustomer() == null) {
            log.warn("Cannot send refund confirmation: shipment or customer is null");
            return false;
        }
        
        // Increment counter
        notificationCounts.get("payment").incrementAndGet();
        
        WalkInCustomer customer = shipment.getCustomer();
        
        // Prepare notification content
        String subject = "Refund Confirmation - Shipment " + shipment.getTrackingNumber();
        String message = String.format(
                "Dear %s,\n\n" +
                "We have processed a refund for your shipment with tracking number %s.\n\n" +
                "Refund Details:\n" +
                "Amount: %s\n" +
                "Reason: %s\n" +
                "Date: %s\n\n" +
                "The refund should be reflected in your account within 3-5 business days, depending on your payment provider.\n\n" +
                "If you have any questions regarding this refund, please contact our customer service.\n\n" +
                "Thank you for your understanding.\n\n" +
                "Best regards,\nCourier Network Team",
                customer.getName(),
                shipment.getTrackingNumber(),
                refundAmount,
                reason,
                java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ISO_DATE));
        
        // Send notifications based on customer preferences
        boolean emailSent = false;
        boolean smsSent = false;
        
        if (canReceiveEmailNotifications(customer) && customer.getEmail() != null) {
            emailSent = sendEmail(customer.getEmail(), subject, message);
            if (emailSent) {
                notificationCounts.get("email").incrementAndGet();
            }
        }
        
        if (canReceiveSmsNotifications(customer) && customer.getPhone() != null) {
            // Shorter message for SMS
            String smsMessage = String.format(
                    "A refund of %s has been processed for your shipment %s. Reason: %s",
                    refundAmount, shipment.getTrackingNumber(), reason);
            smsSent = sendSms(customer.getPhone(), smsMessage);
            if (smsSent) {
                notificationCounts.get("sms").incrementAndGet();
            }
        }
        
        log.info("Refund confirmation sent for shipment ID: {}, email: {}, SMS: {}", 
                shipment.getId(), emailSent, smsSent);
                
        return emailSent || smsSent;
    }
    
    @Override
    @Async
    public boolean sendPickupReadyNotification(WalkInShipment shipment) {
        log.debug("Sending pickup ready notification for shipment ID: {}", shipment.getId());
        
        if (shipment == null || shipment.getCustomer() == null) {
            log.warn("Cannot send pickup ready notification: shipment or customer is null");
            return false;
        }
        
        // Increment counter
        notificationCounts.get("shipment_status").incrementAndGet();
        
        WalkInCustomer customer = shipment.getCustomer();
        PhysicalLocation location = getLocationForShipment(shipment);
        String locationName = formatLocation(location);
        
        // Prepare notification content
        String subject = "Shipment Ready for Pickup - " + shipment.getTrackingNumber();
        String message = String.format(
                "Dear %s,\n\n" +
                "Your shipment with tracking number %s is now ready for pickup.\n\n" +
                "Pickup Details:\n" +
                "Location: %s\n" +
                "Operating Hours: %s\n" +
                "Reference Number: %s\n\n" +
                "Please bring a valid ID when collecting your shipment.\n\n" +
                "Thank you for using our services.\n\n" +
                "Best regards,\nCourier Network Team",
                customer.getName(),
                shipment.getTrackingNumber(),
                locationName,
                location != null ? location.getOperatingHours() : "Please check our website",
                shipment.getTrackingNumber());
        
        // Send notifications based on customer preferences
        boolean emailSent = false;
        boolean smsSent = false;
        
        if (canReceiveEmailNotifications(customer) && customer.getEmail() != null) {
            emailSent = sendEmail(customer.getEmail(), subject, message);
            if (emailSent) {
                notificationCounts.get("email").incrementAndGet();
            }
        }
        
        if (canReceiveSmsNotifications(customer) && customer.getPhone() != null) {
            // Shorter message for SMS
            String smsMessage = String.format(
                    "Your shipment %s is ready for pickup at %s. Please bring ID.",
                    shipment.getTrackingNumber(), locationName);
            smsSent = sendSms(customer.getPhone(), smsMessage);
            if (smsSent) {
                notificationCounts.get("sms").incrementAndGet();
            }
        }
        
        log.info("Pickup ready notification sent for shipment ID: {}, email: {}, SMS: {}", 
                shipment.getId(), emailSent, smsSent);
                
        return emailSent || smsSent;
    }
    
    @Override
    @Async
    public boolean sendDeliveryConfirmation(WalkInShipment shipment) {
        log.debug("Sending delivery confirmation for shipment ID: {}", shipment.getId());
        
        if (shipment == null || shipment.getCustomer() == null) {
            log.warn("Cannot send delivery confirmation: shipment or customer is null");
            return false;
        }
        
        // Increment counter
        notificationCounts.get("shipment_status").incrementAndGet();
        
        WalkInCustomer customer = shipment.getCustomer();
        
        // Prepare notification content
        String subject = "Shipment Delivered - " + shipment.getTrackingNumber();
        String message = String.format(
                "Dear %s,\n\n" +
                "Your shipment with tracking number %s has been delivered successfully.\n\n" +
                "Delivery Details:\n" +
                "Date: %s\n" +
                "Location: %s\n" +
                "Received By: %s\n\n" +
                "We hope our service met your expectations. If you have any feedback, please let us know.\n\n" +
                "Thank you for choosing our services.\n\n" +
                "Best regards,\nCourier Network Team",
                customer.getName(),
                shipment.getTrackingNumber(),
                java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ISO_DATE),
                shipment.getDestinationAddress(),
                shipment.getRecipientName() != null ? shipment.getRecipientName() : "Recipient");
        
        // Send notifications based on customer preferences
        boolean emailSent = false;
        boolean smsSent = false;
        
        if (canReceiveEmailNotifications(customer) && customer.getEmail() != null) {
            emailSent = sendEmail(customer.getEmail(), subject, message);
            if (emailSent) {
                notificationCounts.get("email").incrementAndGet();
            }
        }
        
        if (canReceiveSmsNotifications(customer) && customer.getPhone() != null) {
            // Shorter message for SMS
            String smsMessage = String.format(
                    "Your shipment %s has been delivered. Thank you for using our services!",
                    shipment.getTrackingNumber());
            smsSent = sendSms(customer.getPhone(), smsMessage);
            if (smsSent) {
                notificationCounts.get("sms").incrementAndGet();
            }
        }
        
        log.info("Delivery confirmation sent for shipment ID: {}, email: {}, SMS: {}", 
                shipment.getId(), emailSent, smsSent);
                
        return emailSent || smsSent;
    }
    
    @Override
    @Async
    public boolean notifyStaffAboutNewShipment(WalkInShipment shipment, Long locationId) {
        log.debug("Notifying staff about new shipment ID: {} at location ID: {}", shipment.getId(), locationId);
        
        // Increment counter
        notificationCounts.get("staff").incrementAndGet();
        
        // Find staff at specified location
        List<LocationStaff> staffList = staffRepository.findByLocationId(locationId);
        if (staffList.isEmpty()) {
            log.warn("No staff found at location ID: {}", locationId);
            return false;
        }
        
        boolean notificationSent = false;
        PhysicalLocation location = locationRepository.findById(locationId).orElse(null);
        
        for (LocationStaff staff : staffList) {
            // Prepare notification content
            String subject = "New Shipment Alert - " + shipment.getTrackingNumber();
            String message = String.format(
                    "A new shipment has been created at %s.\n\n" +
                    "Shipment Details:\n" +
                    "Tracking Number: %s\n" +
                    "Customer: %s\n" +
                    "Type: %s\n" +
                    "Weight: %s\n" +
                    "Dimensions: %s\n\n" +
                    "Please process this shipment according to standard procedures.",
                    location != null ? location.getName() : "your location",
                    shipment.getTrackingNumber(),
                    shipment.getCustomer() != null ? shipment.getCustomer().getName() : "N/A",
                    shipment.getShipmentType(),
                    shipment.getWeight(),
                    shipment.getDimensions());
            
            // Send email notification to staff
            if (staff.getEmail() != null && !staff.getEmail().isEmpty()) {
                boolean sent = sendEmail(staff.getEmail(), subject, message);
                if (sent) {
                    notificationSent = true;
                    notificationCounts.get("email").incrementAndGet();
                }
            }
        }
        
        if (notificationSent) {
            log.info("Staff notification sent for new shipment ID: {} at location ID: {}", shipment.getId(), locationId);
        } else {
            log.warn("Failed to send staff notification for new shipment ID: {} at location ID: {}", shipment.getId(), locationId);
        }
        
        return notificationSent;
    }
    
    @Override
    @Async
    public boolean notifyStaffAboutUrgentShipment(WalkInShipment shipment, Long locationId) {
        log.debug("Notifying staff about urgent shipment ID: {} at location ID: {}", shipment.getId(), locationId);
        
        // Increment counter
        notificationCounts.get("staff").incrementAndGet();
        
        // Find staff at specified location
        List<LocationStaff> staffList = staffRepository.findByLocationId(locationId);
        if (staffList.isEmpty()) {
            log.warn("No staff found at location ID: {}", locationId);
            return false;
        }
        
        boolean notificationSent = false;
        PhysicalLocation location = locationRepository.findById(locationId).orElse(null);
        
        for (LocationStaff staff : staffList) {
            // Prepare notification content
            String subject = "URGENT Shipment Alert - " + shipment.getTrackingNumber();
            String message = String.format(
                    "URGENT: A shipment requiring immediate attention has been received at %s.\n\n" +
                    "Urgent Shipment Details:\n" +
                    "Tracking Number: %s\n" +
                    "Customer: %s\n" +
                    "Reason for Urgency: %s\n" +
                    "Required Action: Process immediately and prioritize\n\n" +
                    "Please expedite the processing of this shipment.",
                    location != null ? location.getName() : "your location",
                    shipment.getTrackingNumber(),
                    shipment.getCustomer() != null ? shipment.getCustomer().getName() : "N/A",
                    shipment.getPriority() != null ? shipment.getPriority() : "High priority");
            
            // Send email notification to staff
            if (staff.getEmail() != null && !staff.getEmail().isEmpty()) {
                boolean sent = sendEmail(staff.getEmail(), subject, message);
                if (sent) {
                    notificationSent = true;
                    notificationCounts.get("email").incrementAndGet();
                }
            }
            
            // Send SMS for urgent notifications if phone is available
            if (staff.getPhone() != null && !staff.getPhone().isEmpty()) {
                String smsMessage = String.format(
                        "URGENT: Shipment %s requires immediate processing at %s",
                        shipment.getTrackingNumber(),
                        location != null ? location.getName() : "your location");
                boolean sent = sendSms(staff.getPhone(), smsMessage);
                if (sent) {
                    notificationSent = true;
                    notificationCounts.get("sms").incrementAndGet();
                }
            }
        }
        
        if (notificationSent) {
            log.info("Staff notification sent for urgent shipment ID: {} at location ID: {}", shipment.getId(), locationId);
        } else {
            log.warn("Failed to send staff notification for urgent shipment ID: {} at location ID: {}", shipment.getId(), locationId);
        }
        
        return notificationSent;
    }
    
    @Override
    @Async
    public boolean notifyStaffAboutHighValueShipment(WalkInShipment shipment, Long locationId) {
        log.debug("Notifying staff about high value shipment ID: {} at location ID: {}", shipment.getId(), locationId);
        
        // Increment counter
        notificationCounts.get("staff").incrementAndGet();
        
        // Find staff at specified location
        List<LocationStaff> staffList = staffRepository.findByLocationId(locationId);
        if (staffList.isEmpty()) {
            log.warn("No staff found at location ID: {}", locationId);
            return false;
        }
        
        boolean notificationSent = false;
        PhysicalLocation location = locationRepository.findById(locationId).orElse(null);
        
        for (LocationStaff staff : staffList) {
            // Prepare notification content
            String subject = "High Value Shipment Alert - " + shipment.getTrackingNumber();
            String message = String.format(
                    "ATTENTION: A high value shipment has been received at %s.\n\n" +
                    "High Value Shipment Details:\n" +
                    "Tracking Number: %s\n" +
                    "Customer: %s\n" +
                    "Declared Value: %s\n" +
                    "Security Requirements: Enhanced handling procedures required\n\n" +
                    "Please follow secure handling procedures for high value items.",
                    location != null ? location.getName() : "your location",
                    shipment.getTrackingNumber(),
                    shipment.getCustomer() != null ? shipment.getCustomer().getName() : "N/A",
                    shipment.getDeclaredValue() != null ? shipment.getDeclaredValue() : "High value");
            
            // Send email notification to staff
            if (staff.getEmail() != null && !staff.getEmail().isEmpty()) {
                boolean sent = sendEmail(staff.getEmail(), subject, message);
                if (sent) {
                    notificationSent = true;
                    notificationCounts.get("email").incrementAndGet();
                }
            }
        }
        
        if (notificationSent) {
            log.info("Staff notification sent for high value shipment ID: {} at location ID: {}", shipment.getId(), locationId);
        } else {
            log.warn("Failed to send staff notification for high value shipment ID: {} at location ID: {}", shipment.getId(), locationId);
        }
        
        return notificationSent;
    }
    
    @Override
    @Async
    public boolean notifyStaffAboutPaymentIssue(WalkInShipment shipment, String issueDetails, Long locationId) {
        log.debug("Notifying staff about payment issue for shipment ID: {} at location ID: {}", shipment.getId(), locationId);
        
        // Increment counter
        notificationCounts.get("staff").incrementAndGet();
        
        // Find staff at specified location
        List<LocationStaff> staffList = staffRepository.findByLocationId(locationId);
        if (staffList.isEmpty()) {
            log.warn("No staff found at location ID: {}", locationId);
            return false;
        }
        
        boolean notificationSent = false;
        PhysicalLocation location = locationRepository.findById(locationId).orElse(null);
        
        for (LocationStaff staff : staffList) {
            // Prepare notification content
            String subject = "Payment Issue Alert - Shipment " + shipment.getTrackingNumber();
            String message = String.format(
                    "ATTENTION: A payment issue has been identified for a shipment at %s.\n\n" +
                    "Shipment Details:\n" +
                    "Tracking Number: %s\n" +
                    "Customer: %s\n" +
                    "Issue Details: %s\n\n" +
                    "Please resolve this payment issue as soon as possible.",
                    location != null ? location.getName() : "your location",
                    shipment.getTrackingNumber(),
                    shipment.getCustomer() != null ? shipment.getCustomer().getName() : "N/A",
                    issueDetails);
            
            // Send email notification to staff
            if (staff.getEmail() != null && !staff.getEmail().isEmpty()) {
                boolean sent = sendEmail(staff.getEmail(), subject, message);
                if (sent) {
                    notificationSent = true;
                    notificationCounts.get("email").incrementAndGet();
                }
            }
        }
        
        if (notificationSent) {
            log.info("Staff notification sent for payment issue for shipment ID: {} at location ID: {}", shipment.getId(), locationId);
        } else {
            log.warn("Failed to send staff notification for payment issue for shipment ID: {} at location ID: {}", shipment.getId(), locationId);
        }
        
        return notificationSent;
    }
    
    @Override
    @Async
    public boolean notifyStaffAboutHighCapacityUtilization(Long locationId, double utilizationPercentage) {
        log.debug("Notifying staff about high capacity utilization at location ID: {}, utilization: {}%", locationId, utilizationPercentage);
        
        // Increment counter
        notificationCounts.get("staff").incrementAndGet();
        
        // Find staff at specified location
        List<LocationStaff> staffList = staffRepository.findByLocationId(locationId);
        if (staffList.isEmpty()) {
            log.warn("No staff found at location ID: {}", locationId);
            return false;
        }
        
        boolean notificationSent = false;
        PhysicalLocation location = locationRepository.findById(locationId).orElse(null);
        String locationName = location != null ? location.getName() : "your location";
        
        // Determine alert level based on utilization percentage
        String alertLevel;
        if (utilizationPercentage >= 90) {
            alertLevel = "CRITICAL";
        } else if (utilizationPercentage >= 80) {
            alertLevel = "HIGH";
        } else {
            alertLevel = "MODERATE";
        }
        
        for (LocationStaff staff : staffList) {
            // Prepare notification content
            String subject = alertLevel + " Capacity Alert - " + locationName;
            String message = String.format(
                    "%s CAPACITY ALERT: Location %s is at %.1f%% capacity utilization.\n\n" +
                    "This is a %s level alert. Please take appropriate actions to manage capacity:\n" +
                    "- Review pending shipments and expedite processing\n" +
                    "- Consider rerouting new shipments if possible\n" +
                    "- Consult capacity management procedures\n\n" +
                    "Current Timestamp: %s",
                    alertLevel,
                    locationName,
                    utilizationPercentage,
                    alertLevel.toLowerCase(),
                    java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ISO_DATE_TIME));
            
            // Send email notification to staff
            if (staff.getEmail() != null && !staff.getEmail().isEmpty()) {
                boolean sent = sendEmail(staff.getEmail(), subject, message);
                if (sent) {
                    notificationSent = true;
                    notificationCounts.get("email").incrementAndGet();
                }
            }
            
            // Send SMS for critical alerts
            if (alertLevel.equals("CRITICAL") && staff.getPhone() != null && !staff.getPhone().isEmpty()) {
                String smsMessage = String.format(
                        "CRITICAL ALERT: %s at %.1f%% capacity. Immediate action required.",
                        locationName, utilizationPercentage);
                boolean sent = sendSms(staff.getPhone(), smsMessage);
                if (sent) {
                    notificationSent = true;
                    notificationCounts.get("sms").incrementAndGet();
                }
            }
        }
        
        if (notificationSent) {
            log.info("Staff notification sent for high capacity utilization at location ID: {}, utilization: {}%", locationId, utilizationPercentage);
        } else {
            log.warn("Failed to send staff notification for high capacity utilization at location ID: {}", locationId);
        }
        
        return notificationSent;
    }
    
    @Override
    @Async
    public boolean sendExceptionNotification(String subject, String message, Map<String, Object> details) {
        log.debug("Sending exception notification with subject: {}", subject);
        
        // Increment counter
        notificationCounts.get("exception").incrementAndGet();
        
        if (subject == null || subject.isEmpty() || message == null || message.isEmpty()) {
            log.warn("Cannot send exception notification: subject or message is null or empty");
            return false;
        }
        
        // Build detailed message with any provided details
        StringBuilder detailedMessage = new StringBuilder(message);
        detailedMessage.append("\n\nException Details:\n");
        
        if (details != null && !details.isEmpty()) {
            for (Map.Entry<String, Object> entry : details.entrySet()) {
                detailedMessage.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
        } else {
            detailedMessage.append("No additional details provided\n");
        }
        
        detailedMessage.append("\nTimestamp: ").append(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ISO_DATE_TIME));
        
        // Get management staff emails from environment or configuration
        // For this example, we'll use a hardcoded admin email address
        String adminEmail = "admin@example.com";
        
        boolean emailSent = sendEmail(adminEmail, "EXCEPTION: " + subject, detailedMessage.toString());
        
        if (emailSent) {
            log.info("Exception notification sent with subject: {}", subject);
            notificationCounts.get("email").incrementAndGet();
        } else {
            log.warn("Failed to send exception notification with subject: {}", subject);
        }
        
        return emailSent;
    }

    @Autowired(required = false)
    public void setEmailSender(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }
    
    @Override
    public Map<String, Object> getNotificationStatistics() {
        log.debug("Getting notification statistics");
        
        Map<String, Object> statistics = new HashMap<>();
        
        // Add counter data
        for (Map.Entry<String, AtomicLong> entry : notificationCounts.entrySet()) {
            statistics.put(entry.getKey() + "_count", entry.getValue().get());
        }
        
        // Add configuration information
        statistics.put("email_enabled", emailEnabled);
        statistics.put("sms_enabled", smsEnabled);
        
        return statistics;
    }
    
    @Override
    public boolean canReceiveSmsNotifications(WalkInCustomer customer) {
        if (customer == null) {
            return false;
        }
        
        // Check if SMS notifications are enabled globally
        if (!smsEnabled) {
            return false;
        }
        
        // Check if customer has a valid phone number
        if (customer.getPhone() == null || customer.getPhone().isEmpty()) {
            return false;
        }
        
        // Check if customer has opted in for SMS notifications
        return customer.isSmsNotificationsEnabled();
    }
    
    @Override
    public boolean canReceiveEmailNotifications(WalkInCustomer customer) {
        if (customer == null) {
            return false;
        }
        
        // Check if email notifications are enabled globally
        if (!emailEnabled || emailSender == null) {
            return false;
        }
        
        // Check if customer has a valid email address
        if (customer.getEmail() == null || customer.getEmail().isEmpty()) {
            return false;
        }
        
        // Check if customer has opted in for email notifications
        return customer.isEmailNotificationsEnabled();
    }
    
    /**
     * Get the location for a shipment.
     * 
     * @param shipment the shipment
     * @return the location
     */
    private PhysicalLocation getLocationForShipment(WalkInShipment shipment) {
        if (shipment == null || shipment.getLocationId() == null) {
            return null;
        }
        
        return locationRepository.findById(shipment.getLocationId()).orElse(null);
    }
    
    /**
     * Initialize notification counters.
     */
    private void initCounters() {
        notificationCounts.putIfAbsent("email", new AtomicLong(0));
        notificationCounts.putIfAbsent("sms", new AtomicLong(0));
        notificationCounts.putIfAbsent("shipment_status", new AtomicLong(0));
        notificationCounts.putIfAbsent("payment", new AtomicLong(0));
        notificationCounts.putIfAbsent("staff", new AtomicLong(0));
        notificationCounts.putIfAbsent("exception", new AtomicLong(0));
    }
    
    /**
     * Send a notification to a payment service for successful payment.
     * 
     * @param payment the payment
     * @return true if the notification was sent successfully, false otherwise
     */
    public boolean sendPaymentNotification(WalkInPayment payment) {
        log.debug("Sending payment notification for payment ID: {}", payment.getId());
        // Implementation would integrate with the payment service
        // This is a stub implementation
        return true;
    }
    
    /**
     * Send a payment status update notification.
     * 
     * @param payment the payment with updated status
     * @param oldStatus the previous status
     * @return true if the notification was sent successfully, false otherwise
     */
    public boolean sendPaymentStatusUpdateNotification(WalkInPayment payment, PaymentStatus oldStatus) {
        log.debug("Sending payment status update notification for payment ID: {}, from status {} to {}", 
                 payment.getId(), oldStatus, payment.getStatus());
        // Implementation would integrate with the payment service
        // This is a stub implementation
        return true;
    }
    
    /**
     * Send a refund notification to the payment service.
     * 
     * @param payment the payment with refund
     * @param refundAmount the refund amount
     * @return true if the notification was sent successfully, false otherwise
     */
    public boolean sendRefundNotification(WalkInPayment payment, java.math.BigDecimal refundAmount) {
        log.debug("Sending refund notification for payment ID: {}, refund amount: {}", payment.getId(), refundAmount);
        // Implementation would integrate with the payment service
        // This is a stub implementation
        return true;
    }

    @Override
    @Async
    public boolean sendShipmentStatusUpdateNotification(
            WalkInShipment shipment, ShipmentStatus oldStatus, ShipmentStatus newStatus) {
        log.info("Sending shipment status update notification for shipment ID: {}, old status: {}, new status: {}", 
                shipment.getId(), oldStatus, newStatus);
        
        WalkInCustomer customer = shipment.getCustomer();
        if (customer == null) {
            log.warn("Cannot send status update notification: customer is null for shipment ID: {}", 
                    shipment.getId());
            return false;
        }
        
        String subject = "Shipment Update: " + shipment.getTrackingNumber();
        String message = String.format(
                "Dear %s,\n\nYour shipment with tracking number %s has been updated from %s to %s.\n\n" +
                "To track your shipment, visit our website or mobile app.\n\n" +
                "Thank you for choosing our courier service.\n\nBest regards,\nCourier Network",
                customer.getName(), shipment.getTrackingNumber(), oldStatus, newStatus);
        
        return sendNotification(customer, subject, message, false);
    }

    @Override
    @Async
    public boolean sendShipmentCreationConfirmation(WalkInShipment shipment) {
        log.info("Sending shipment creation confirmation for shipment ID: {}", shipment.getId());
        
        WalkInCustomer customer = shipment.getCustomer();
        if (customer == null) {
            log.warn("Cannot send creation confirmation: customer is null for shipment ID: {}", 
                    shipment.getId());
            return false;
        }
        
        String subject = "Shipment Confirmed: " + shipment.getTrackingNumber();
        String message = String.format(
                "Dear %s,\n\nThank you for using our courier service. Your shipment has been successfully created.\n\n" +
                "Tracking Number: %s\n" +
                "Origin: %s\n" +
                "Destination: %s\n" +
                "Expected Delivery: %s\n\n" +
                "You can track your shipment using the tracking number above on our website or mobile app.\n\n" +
                "Best regards,\nCourier Network",
                customer.getName(), shipment.getTrackingNumber(),
                formatLocation(shipment.getOrigin()), shipment.getDestinationAddress(),
                shipment.getExpectedDeliveryDate());
        
        return sendNotification(customer, subject, message, false);
    }

    @Override
    @Async
    public boolean sendPaymentConfirmation(WalkInShipment shipment, Map<String, Object> paymentDetails) {
        log.info("Sending payment confirmation for shipment ID: {}", shipment.getId());
        
        WalkInCustomer customer = shipment.getCustomer();
        if (customer == null) {
            log.warn("Cannot send payment confirmation: customer is null for shipment ID: {}", 
                    shipment.getId());
            return false;
        }
        
        String subject = "Payment Confirmation: " + shipment.getTrackingNumber();
        
        // Extract payment details
        String amount = (String) paymentDetails.getOrDefault("amount", "N/A");
        String paymentMethod = (String) paymentDetails.getOrDefault("paymentMethod", "N/A");
        String receiptNumber = (String) paymentDetails.getOrDefault("receiptNumber", "N/A");
        
        String message = String.format(
                "Dear %s,\n\nThank you for your payment for shipment %s.\n\n" +
                "Receipt Number: %s\n" +
                "Amount: %s\n" +
                "Payment Method: %s\n" +
                "Date: %s\n\n" +
                "Please keep this receipt for your records.\n\n" +
                "Best regards,\nCourier Network",
                customer.getName(), shipment.getTrackingNumber(),
                receiptNumber, amount, paymentMethod, 
                java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ISO_DATE));
        
        return sendNotification(customer, subject, message, false);
    }

    @Override
    @Async
    public boolean sendRefundConfirmation(WalkInShipment shipment, String refundAmount, String reason) {
        log.info("Sending refund confirmation for shipment ID: {}", shipment.getId());
        
        WalkInCustomer customer = shipment.getCustomer();
        if (customer == null) {
            log.warn("Cannot send refund confirmation: customer is null for shipment ID: {}", 
                    shipment.getId());
            return false;
        }
        
        String subject = "Refund Confirmation: " + shipment.getTrackingNumber();
        String message = String.format(
                "Dear %s,\n\nA refund of %s has been processed for your shipment %s.\n\n" +
                "Reason: %s\n" +
                "Refund Date: %s\n\n" +
                "The refund will be credited to your original payment method and may take 3-5 business days to appear.\n\n" +
                "If you have any questions, please contact our customer support.\n\n" +
                "Best regards,\nCourier Network",
                customer.getName(), refundAmount, shipment.getTrackingNumber(),
                reason, java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ISO_DATE));
        
        return sendNotification(customer, subject, message, false);
    }

    @Override
    @Async
    public boolean sendPickupReminder(WalkInShipment shipment) {
        log.info("Sending pickup reminder for shipment ID: {}", shipment.getId());
        
        WalkInCustomer customer = shipment.getCustomer();
        if (customer == null) {
            log.warn("Cannot send pickup reminder: customer is null for shipment ID: {}", 
                    shipment.getId());
            return false;
        }
        
        String subject = "Ready for Pickup: " + shipment.getTrackingNumber();
        String message = String.format(
                "Dear %s,\n\nYour shipment with tracking number %s is ready for pickup at our location.\n\n" +
                "Pickup Location: %s\n" +
                "Operating Hours: %s\n" +
                "Pickup Reference: %s\n\n" +
                "Please bring a valid ID when collecting your shipment.\n\n" +
                "Best regards,\nCourier Network",
                customer.getName(), shipment.getTrackingNumber(),
                formatLocation(shipment.getCurrentLocation()),
                "Monday-Friday: 9AM-6PM, Saturday: 10AM-2PM", // Should be fetched from location operating hours
                shipment.getTrackingNumber());
        
        return sendNotification(customer, subject, message, true);
    }

    @Override
    @Async
    public boolean sendDeliveryAttemptNotification(WalkInShipment shipment, Map<String, Object> attemptDetails) {
        log.info("Sending delivery attempt notification for shipment ID: {}", shipment.getId());
        
        WalkInCustomer customer = shipment.getCustomer();
        if (customer == null) {
            log.warn("Cannot send delivery attempt notification: customer is null for shipment ID: {}", 
                    shipment.getId());
            return false;
        }
        
        // Extract attempt details
        String attemptDate = (String) attemptDetails.getOrDefault("attemptDate", "today");
        String attemptTime = (String) attemptDetails.getOrDefault("attemptTime", "earlier");
        String reason = (String) attemptDetails.getOrDefault("reason", "recipient not available");
        String nextAttempt = (String) attemptDetails.getOrDefault("nextAttempt", "tomorrow");
        
        String subject = "Delivery Attempt: " + shipment.getTrackingNumber();
        String message = String.format(
                "Dear %s,\n\nWe attempted to deliver your shipment with tracking number %s on %s at %s, but %s.\n\n" +
                "We will attempt delivery again on %s. If you would like to reschedule or arrange pickup, " +
                "please contact our customer service.\n\n" +
                "Best regards,\nCourier Network",
                customer.getName(), shipment.getTrackingNumber(),
                attemptDate, attemptTime, reason, nextAttempt);
        
        return sendNotification(customer, subject, message, true);
    }

    @Override
    @Async
    public boolean sendDigitalReceipt(WalkInShipment shipment, String receiptNumber, Map<String, Object> receiptDetails) {
        log.info("Sending digital receipt for shipment ID: {}", shipment.getId());
        
        WalkInCustomer customer = shipment.getCustomer();
        if (customer == null) {
            log.warn("Cannot send digital receipt: customer is null for shipment ID: {}", 
                    shipment.getId());
            return false;
        }
        
        String subject = "Your Receipt: " + receiptNumber;
        
        // Extract receipt details
        String amount = (String) receiptDetails.getOrDefault("amount", "N/A");
        String paymentMethod = (String) receiptDetails.getOrDefault("paymentMethod", "N/A");
        String date = (String) receiptDetails.getOrDefault("date", 
                java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ISO_DATE));
        
        String message = String.format(
                "Dear %s,\n\nThank you for using our courier service. Here is your receipt:\n\n" +
                "Receipt Number: %s\n" +
                "Tracking Number: %s\n" +
                "Date: %s\n" +
                "Amount: %s\n" +
                "Payment Method: %s\n" +
                "Service: Shipping\n\n" +
                "This is an official receipt for your records.\n\n" +
                "Best regards,\nCourier Network",
                customer.getName(), receiptNumber, shipment.getTrackingNumber(),
                date, amount, paymentMethod);
        
        return sendNotification(customer, subject, message, false);
    }

    @Override
    @Async
    public int sendMarketingMessage(List<WalkInCustomer> customers, String messageTitle, String messageContent) {
        log.info("Sending marketing message to {} customers: {}", customers.size(), messageTitle);
        
        int successCount = 0;
        for (WalkInCustomer customer : customers) {
            if (!canReceiveMarketingNotifications(customer)) {
                log.debug("Customer ID: {} has not consented to marketing messages, skipping", 
                        customer.getId());
                continue;
            }
            
            String subject = "Courier Network: " + messageTitle;
            
            if (sendNotification(customer, subject, messageContent, false)) {
                successCount++;
            }
        }
        
        log.info("Successfully sent marketing message to {}/{} customers", successCount, customers.size());
        return successCount;
    }

    @Override
    @Async
    public boolean sendCustomerNotification(WalkInCustomer customer, String subject, String message, boolean isHighPriority) {
        log.info("Sending custom notification to customer ID: {}, subject: {}, high priority: {}", 
                customer.getId(), subject, isHighPriority);
        
        return sendNotification(customer, subject, message, isHighPriority);
    }

    @Override
    @Async
    public boolean notifyStaffAboutHighValueShipment(WalkInShipment shipment, Long locationId) {
        log.info("Notifying staff about high-value shipment ID: {} at location ID: {}", 
                shipment.getId(), locationId);
        
        // Get all staff at the location
        List<LocationStaff> staff = staffRepository.findByLocationId(locationId);
        
        if (staff.isEmpty()) {
            log.warn("No staff found at location ID: {} for high-value shipment notification", locationId);
            return false;
        }
        
        int successCount = 0;
        String subject = "High-Value Shipment Alert: " + shipment.getTrackingNumber();
        String message = String.format(
                "A high-value shipment has been received at your location.\n\n" +
                "Tracking Number: %s\n" +
                "Origin: %s\n" +
                "Declared Value: %s\n\n" +
                "Please follow special handling procedures for this shipment.",
                shipment.getTrackingNumber(), formatLocation(shipment.getOrigin()), 
                shipment.getDeclaredValue());
        
        for (LocationStaff staffMember : staff) {
            if (sendStaffNotification(staffMember, subject, message, true)) {
                successCount++;
            }
        }
        
        log.info("Successfully notified {}/{} staff members about high-value shipment", 
                successCount, staff.size());
        return successCount > 0;
    }

    @Override
    @Async
    public boolean notifyStaffAboutPaymentIssue(WalkInShipment shipment, String issueDetails, Long locationId) {
        log.info("Notifying staff about payment issue for shipment ID: {} at location ID: {}", 
                shipment.getId(), locationId);
        
        // Get all staff at the location
        List<LocationStaff> staff = staffRepository.findByLocationId(locationId);
        
        if (staff.isEmpty()) {
            log.warn("No staff found at location ID: {} for payment issue notification", locationId);
            return false;
        }
        
        int successCount = 0;
        String subject = "Payment Issue Alert: " + shipment.getTrackingNumber();
        String message = String.format(
                "A payment issue has been detected for shipment %s:\n\n" +
                "Issue: %s\n" +
                "Customer: %s\n" +
                "Amount: %s\n\n" +
                "Please investigate and resolve this issue promptly.",
                shipment.getTrackingNumber(), issueDetails, 
                shipment.getCustomer() != null ? shipment.getCustomer().getName() : "N/A", 
                shipment.getShippingFee());
        
        for (LocationStaff staffMember : staff) {
            if (sendStaffNotification(staffMember, subject, message, true)) {
                successCount++;
            }
        }
        
        log.info("Successfully notified {}/{} staff members about payment issue", 
                successCount, staff.size());
        return successCount > 0;
    }

    @Override
    @Async
    public boolean notifyStaffAboutHighCapacityUtilization(Long locationId, double utilizationPercentage) {
        log.info("Notifying staff about high capacity utilization at location ID: {}: {}%", 
                locationId, utilizationPercentage);
        
        // Get all staff at the location
        List<LocationStaff> staff = staffRepository.findByLocationId(locationId);
        
        if (staff.isEmpty()) {
            log.warn("No staff found at location ID: {} for capacity utilization notification", locationId);
            return false;
        }
        
        int successCount = 0;
        String subject = "High Capacity Alert: Location " + locationId;
        String message = String.format(
                "The capacity utilization at your location has reached %.1f%%.\n\n" +
                "Please take necessary actions to manage capacity:\n" +
                "- Arrange for expedited processing of pending shipments\n" +
                "- Contact regional office if additional resources are needed\n" +
                "- Update customers about potential delays\n\n" +
                "This is an automated notification.",
                utilizationPercentage);
        
        for (LocationStaff staffMember : staff) {
            if (sendStaffNotification(staffMember, subject, message, true)) {
                successCount++;
            }
        }
        
        log.info("Successfully notified {}/{} staff members about high capacity utilization", 
                successCount, staff.size());
        return successCount > 0;
    }

    @Override
    @Async
    public boolean sendExceptionNotification(String subject, String message, Map<String, Object> details) {
        log.info("Sending exception notification: {}", subject);
        
        // Get all management staff across all locations
        List<LocationStaff> managementStaff = staffRepository.findByRoleName("MANAGER");
        
        if (managementStaff.isEmpty()) {
            log.warn("No management staff found for exception notification");
            return false;
        }
        
        // Append details to the message
        StringBuilder fullMessage = new StringBuilder(message);
        fullMessage.append("\n\nDetails:\n");
        
        for (Map.Entry<String, Object> entry : details.entrySet()) {
            fullMessage.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        
        fullMessage.append("\nTimestamp: ").append(java.time.LocalDateTime.now());
        
        int successCount = 0;
        for (LocationStaff manager : managementStaff) {
            if (sendStaffNotification(manager, "ALERT: " + subject, fullMessage.toString(), true)) {
                successCount++;
            }
        }
        
        log.info("Successfully sent exception notification to {}/{} managers", 
                successCount, managementStaff.size());
        return successCount > 0;
    }

    @Override
    public Map<String, Object> getNotificationStatistics() {
        log.debug("Getting notification statistics");
        
        Map<String, Object> statistics = new HashMap<>();
        for (Map.Entry<String, AtomicLong> entry : notificationCounts.entrySet()) {
            statistics.put(entry.getKey(), entry.getValue().get());
        }
        
        return statistics;
    }

    @Override
    public boolean canReceiveSmsNotifications(WalkInCustomer customer) {
        if (customer == null) {
            return false;
        }
        
        return smsEnabled && 
               customer.getPhone() != null && 
               !customer.getPhone().isEmpty() && 
               customer.isOptInSms();
    }

    @Override
    public boolean canReceiveEmailNotifications(WalkInCustomer customer) {
        if (customer == null) {
            return false;
        }
        
        return emailEnabled && 
               customer.getEmail() != null && 
               !customer.getEmail().isEmpty() && 
               customer.isOptInEmail();
    }
    
    /**
     * Check if a customer has consented to marketing notifications.
     * 
     * @param customer the customer to check
     * @return true if the customer can receive marketing notifications, false otherwise
     */
    private boolean canReceiveMarketingNotifications(WalkInCustomer customer) {
        if (customer == null) {
            return false;
        }
        
        return (emailEnabled && customer.isOptInMarketing() && customer.isOptInEmail()) ||
               (smsEnabled && customer.isOptInMarketing() && customer.isOptInSms());
    }
    
    /**
     * Send a notification to a customer via available channels.
     * 
     * @param customer the customer to notify
     * @param subject the notification subject
     * @param message the notification message
     * @param isHighPriority whether the notification is high priority
     * @return true if the notification was sent successfully via at least one channel, false otherwise
     */
    private boolean sendNotification(WalkInCustomer customer, String subject, String message, boolean isHighPriority) {
        boolean emailSent = false;
        boolean smsSent = false;
        
        // Try email first
        if (canReceiveEmailNotifications(customer)) {
            emailSent = sendEmail(customer.getEmail(), subject, message);
            notificationCounts.get("email").incrementAndGet();
        }
        
        // If high priority or email failed or not available, try SMS
        if ((isHighPriority || !emailSent) && canReceiveSmsNotifications(customer)) {
            smsSent = sendSms(customer.getPhone(), subject + ": " + message);
            notificationCounts.get("sms").incrementAndGet();
        }
        
        boolean success = emailSent || smsSent;
        if (success) {
            notificationCounts.get("success").incrementAndGet();
            log.debug("Successfully sent notification to customer ID: {}, email: {}, SMS: {}", 
                    customer.getId(), emailSent, smsSent);
        } else {
            notificationCounts.get("failure").incrementAndGet();
            log.warn("Failed to send notification to customer ID: {}", customer.getId());
        }
        
        return success;
    }
    
    /**
     * Send a notification to a staff member.
     * 
     * @param staff the staff member to notify
     * @param subject the notification subject
     * @param message the notification message
     * @param isHighPriority whether the notification is high priority
     * @return true if the notification was sent successfully, false otherwise
     */
    private boolean sendStaffNotification(LocationStaff staff, String subject, String message, boolean isHighPriority) {
        boolean emailSent = false;
        boolean smsSent = false;
        
        // Always try email for staff
        if (emailEnabled && staff.getEmail() != null && !staff.getEmail().isEmpty()) {
            emailSent = sendEmail(staff.getEmail(), subject, message);
            notificationCounts.get("email").incrementAndGet();
        }
        
        // If high priority, also send SMS if available
        if (isHighPriority && smsEnabled && staff.getPhone() != null && !staff.getPhone().isEmpty()) {
            smsSent = sendSms(staff.getPhone(), subject + ": " + message);
            notificationCounts.get("sms").incrementAndGet();
        }
        
        boolean success = emailSent || smsSent;
        if (success) {
            notificationCounts.get("success").incrementAndGet();
            log.debug("Successfully sent notification to staff ID: {}, email: {}, SMS: {}", 
                    staff.getId(), emailSent, smsSent);
        } else {
            notificationCounts.get("failure").incrementAndGet();
            log.warn("Failed to send notification to staff ID: {}", staff.getId());
        }
        
        return success;
    }
    
    /**
     * Send an email.
     * 
     * @param to the recipient email address
     * @param subject the email subject
     * @param message the email message
     * @return true if the email was sent successfully, false otherwise
     */
    private boolean sendEmail(String to, String subject, String message) {
        if (!emailEnabled || emailSender == null) {
            log.debug("Email notifications are disabled");
            return false;
        }
        
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(emailFrom);
            mailMessage.setTo(to);
            mailMessage.setSubject(subject);
            mailMessage.setText(message);
            
            emailSender.send(mailMessage);
            log.debug("Email sent successfully to: {}", to);
            return true;
        } catch (Exception e) {
            log.error("Failed to send email to: {}", to, e);
            return false;
        }
    }
    
    /**
     * Send an SMS.
     * 
     * @param to the recipient phone number
     * @param message the SMS message
     * @return true if the SMS was sent successfully, false otherwise
     */
    private boolean sendSms(String to, String message) {
        if (!smsEnabled) {
            log.debug("SMS notifications are disabled");
            return false;
        }
        
        // In a real implementation, this would call an SMS API service
        // For simulation, always return true (success)
        log.debug("SMS sent successfully to: {}", to);
        return true;
    }
    
    /**
     * Format a location for display in notifications.
     * 
     * @param location the location to format
     * @return the formatted location string
     */
    private String formatLocation(PhysicalLocation location) {
        if (location == null) {
            return "Unknown Location";
        }
        
        return String.format("%s, %s, %s", 
                location.getName(), location.getAddress(), location.getCity());
    }
}
