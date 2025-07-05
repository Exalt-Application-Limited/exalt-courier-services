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
 * Response DTO for ticket message data (used in ticket responses).
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketMessageResponse {

    private UUID id;
    private UUID ticketId;
    private MessageType messageType;
    
    // Message content
    private String content;
    private String subject;
    private String channel;
    
    // Sender information
    private String senderId;
    private String senderName;
    private String senderEmail;
    private String senderRole;
    
    // Message properties
    private Boolean isInternal;
    private Boolean isAutomated;
    private Boolean isHighPriority;
    
    // Timestamps
    private LocalDateTime sentAt;
    private LocalDateTime readAt;
    
    // Status
    private String deliveryStatus;
    private Boolean hasAttachments;
    private Integer attachmentCount;
    
    // Quick display helpers
    private String formattedSentTime;
    private String messageSummary;
    private String senderDisplayName;
}