package com.gogidix.courier.customer.support.communication.dto;

import com.gogidix.courier.customer.support.communication.enums.MessageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Request DTO for creating communication/messages in support tickets.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunicationRequest {

    @NotNull(message = "Ticket ID is required")
    private UUID ticketId;

    @NotNull(message = "Message type is required")
    private MessageType messageType;

    @NotBlank(message = "Message content is required")
    @Size(max = 10000, message = "Message content must not exceed 10000 characters")
    private String content;

    @Size(max = 50, message = "Sender ID must not exceed 50 characters")
    private String senderId;

    @Size(max = 100, message = "Sender name must not exceed 100 characters")
    private String senderName;

    @Size(max = 100, message = "Sender email must not exceed 100 characters")
    private String senderEmail;

    @Size(max = 30, message = "Sender role must not exceed 30 characters")
    private String senderRole; // CUSTOMER, AGENT, SYSTEM

    @Size(max = 200, message = "Channel must not exceed 200 characters")
    private String channel; // EMAIL, SMS, CHAT, PORTAL, PHONE

    @Size(max = 200, message = "Subject must not exceed 200 characters")
    private String subject; // For email communications

    private Boolean isInternal = false; // Internal agent notes vs customer-visible

    private Boolean isAutomated = false; // System-generated messages

    private Boolean requiresResponse = false; // Does this message require a response?

    private Boolean isHighPriority = false; // Priority message flag

    @Size(max = 500, message = "Tags must not exceed 500 characters")
    private String tags;

    @Size(max = 1000, message = "Additional context must not exceed 1000 characters")
    private String additionalContext; // Extra context for agents

    // Attachments
    private List<CommunicationAttachmentRequest> attachments;

    // Template-based messaging
    @Size(max = 100, message = "Template ID must not exceed 100 characters")
    private String templateId;

    private List<TemplateParameter> templateParameters;

    // Notification preferences
    private NotificationPreferences notificationPreferences;

    // Scheduling (for future messages)
    private Boolean isScheduled = false;
    private String scheduledDateTime; // ISO format

    // Response tracking
    @Size(max = 100, message = "In reply to message ID must not exceed 100 characters")
    private String inReplyToMessageId;

    private Boolean autoCloseAfterResponse = false;

    /**
     * Nested class for attachment requests.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommunicationAttachmentRequest {
        
        @NotBlank(message = "File name is required")
        @Size(max = 255, message = "File name must not exceed 255 characters")
        private String fileName;

        @NotBlank(message = "File URL is required")
        @Size(max = 500, message = "File URL must not exceed 500 characters")
        private String fileUrl;

        @Size(max = 100, message = "File type must not exceed 100 characters")
        private String fileType;

        private Long fileSizeBytes;

        @Size(max = 500, message = "Description must not exceed 500 characters")
        private String description;

        private Boolean isInternal = false; // Only visible to agents
    }

    /**
     * Nested class for template parameters.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TemplateParameter {
        
        @NotBlank(message = "Parameter name is required")
        @Size(max = 100, message = "Parameter name must not exceed 100 characters")
        private String name;

        @Size(max = 1000, message = "Parameter value must not exceed 1000 characters")
        private String value;
    }

    /**
     * Nested class for notification preferences.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationPreferences {
        
        private Boolean sendEmail = true;
        private Boolean sendSms = false;
        private Boolean sendPushNotification = true;
        private Boolean sendInAppNotification = true;

        @Size(max = 100, message = "Preferred language must not exceed 100 characters")
        private String preferredLanguage = "en";

        private Boolean useCustomerPreferences = true; // Override with customer's saved preferences
    }

    /**
     * Check if this is a customer-facing message.
     */
    public boolean isCustomerFacing() {
        return !isInternal;
    }

    /**
     * Check if this message has attachments.
     */
    public boolean hasAttachments() {
        return attachments != null && !attachments.isEmpty();
    }

    /**
     * Check if this is a template-based message.
     */
    public boolean isTemplateBased() {
        return templateId != null && !templateId.trim().isEmpty();
    }
}