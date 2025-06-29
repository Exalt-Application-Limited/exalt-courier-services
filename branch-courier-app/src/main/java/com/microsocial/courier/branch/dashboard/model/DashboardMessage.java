package com.exalt.courier.courier.branch.dashboard.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a message exchanged between different levels of dashboards.
 * This class is used for communication between Branch, Regional, and Global dashboards.
 * 
 * Converted to use Lombok annotations for reduced boilerplate.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardMessage {

    // Unique identifier for the message
    @Builder.Default
    private String messageId = UUID.randomUUID().toString();
    
    // Type of the message (e.g., DATA_REQUEST, ALERT, etc.)
    private MessageType messageType;
    
    // ID of the source dashboard/component
    private String sourceId;
    
    // ID of the target dashboard/component
    private String targetId;
    
    // Optional reference to another message (for acknowledgments, replies, etc.)
    private String referenceId;
    
    // Timestamp when the message was created
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    
    // The actual content/payload of the message
    private String content;
    
    // Priority level of the message
    @Builder.Default
    private MessagePriority priority = MessagePriority.NORMAL;
    
    // Flag indicating if the message requires an acknowledgment
    @Builder.Default
    private boolean requiresAcknowledgment = true;
    
    /**
     * Convenience constructor with basic fields.
     * 
     * @param messageType Type of the message
     * @param sourceId ID of the source dashboard/component
     * @param targetId ID of the target dashboard/component
     * @param content The actual content/payload of the message
     * @return A new dashboard message
     */
    public static DashboardMessage create(MessageType messageType, String sourceId, String targetId, String content) {
        return DashboardMessage.builder()
                .messageType(messageType)
                .sourceId(sourceId)
                .targetId(targetId)
                .content(content)
                .build();
    }
}