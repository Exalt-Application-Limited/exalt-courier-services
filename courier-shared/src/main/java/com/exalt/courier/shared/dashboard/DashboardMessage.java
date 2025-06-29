package com.exalt.courier.shared.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Represents a message that can be sent between dashboard levels.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardMessage {
    
    private String id;
    private String sourceLevel;
    private String sourceId;
    private String targetLevel;
    private String targetId;
    private String messageType;
    private String subject;
    private String content;
    private Map<String, String> metadata;
    private boolean requiresAcknowledgment;
    private boolean acknowledged;
    
    @Builder.Default
    private LocalDateTime sentAt = LocalDateTime.now();
    
    private LocalDateTime acknowledgedAt;
    
    @Builder.Default
    private int priority = 5; // Default priority
}
