package com.gogidix.courier.customer.support.communication.dto;

import com.gogidix.courier.customer.support.communication.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for communication/message data.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunicationResponse {

    private UUID id;
    private UUID ticketId;
    private String ticketReferenceId;

    // Message details
    private MessageType messageType;
    private String content;
    private String subject;
    private String channel;

    // Sender information
    private String senderId;
    private String senderName;
    private String senderEmail;
    private String senderRole;

    // Message metadata
    private Boolean isInternal;
    private Boolean isAutomated;
    private Boolean requiresResponse;
    private Boolean isHighPriority;
    private String tags;
    private String additionalContext;

    // Timestamps
    private LocalDateTime sentAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime readAt;
    private LocalDateTime respondedAt;

    // Status tracking
    private String deliveryStatus; // PENDING, DELIVERED, FAILED, READ
    private String failureReason;
    private Integer retryCount;
    private LocalDateTime nextRetryAt;

    // Response tracking
    private String inReplyToMessageId;
    private Boolean hasBeenResponded;
    private Integer responseCount;
    private LocalDateTime lastResponseAt;

    // Template information
    private String templateId;
    private String templateName;
    private List<TemplateParameterResponse> templateParameters;

    // Attachments
    private List<CommunicationAttachmentResponse> attachments;

    // Notification tracking
    private NotificationStatusResponse notificationStatus;

    // Scheduling information
    private Boolean isScheduled;
    private LocalDateTime scheduledDateTime;
    private String schedulingStatus; // SCHEDULED, SENT, CANCELLED

    // Performance metrics
    private Long processingTimeMs;
    private Long deliveryTimeMs;
    private Long responseTimeMs;

    // Audit fields
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    // Calculated fields
    private Boolean isOverdue; // If requires response and past SLA
    private Long hoursUntilOverdue;
    private Boolean isRecentMessage; // Within last 24 hours
    private String formattedSentTime; // User-friendly format

    /**
     * Nested class for template parameter responses.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TemplateParameterResponse {
        private String name;
        private String value;
        private String displayName;
    }

    /**
     * Nested class for attachment responses.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommunicationAttachmentResponse {
        private UUID id;
        private String fileName;
        private String fileUrl;
        private String fileType;
        private Long fileSizeBytes;
        private String description;
        private Boolean isInternal;
        private LocalDateTime uploadedAt;
        private String uploadedBy;
        private String downloadUrl;
        private Integer downloadCount;
        private String thumbnailUrl;
        private Boolean isVirusScanPassed;
    }

    /**
     * Nested class for notification status.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationStatusResponse {
        private Boolean emailSent;
        private Boolean smsSent;
        private Boolean pushNotificationSent;
        private Boolean inAppNotificationSent;
        
        private LocalDateTime emailSentAt;
        private LocalDateTime smsSentAt;
        private LocalDateTime pushNotificationSentAt;
        private LocalDateTime inAppNotificationSentAt;
        
        private String emailStatus;
        private String smsStatus;
        private String pushNotificationStatus;
        private String inAppNotificationStatus;
        
        private String emailFailureReason;
        private String smsFailureReason;
        private String pushNotificationFailureReason;
        private String inAppNotificationFailureReason;
    }

    /**
     * Check if this message was successfully delivered.
     */
    public boolean isDelivered() {
        return "DELIVERED".equals(deliveryStatus) || "READ".equals(deliveryStatus);
    }

    /**
     * Check if this message failed to deliver.
     */
    public boolean isDeliveryFailed() {
        return "FAILED".equals(deliveryStatus);
    }

    /**
     * Check if this message has been read by recipient.
     */
    public boolean isRead() {
        return readAt != null;
    }

    /**
     * Check if this message needs a response and is overdue.
     */
    public boolean isOverdueForResponse() {
        return requiresResponse && Boolean.TRUE.equals(isOverdue);
    }

    /**
     * Get a summary of the message for display.
     */
    public String getMessageSummary() {
        if (content == null) return "No content";
        return content.length() > 100 ? content.substring(0, 97) + "..." : content;
    }

    /**
     * Get sender display name.
     */
    public String getSenderDisplayName() {
        if (senderName != null && !senderName.trim().isEmpty()) {
            return senderName;
        }
        if (senderEmail != null && !senderEmail.trim().isEmpty()) {
            return senderEmail;
        }
        return senderId != null ? senderId : "Unknown Sender";
    }

    /**
     * Check if message has attachments.
     */
    public boolean hasAttachments() {
        return attachments != null && !attachments.isEmpty();
    }

    /**
     * Get attachment count.
     */
    public int getAttachmentCount() {
        return attachments != null ? attachments.size() : 0;
    }
}