package com.gogidix.courier.customer.support.communication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for ticket attachment data (used in ticket responses).
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketAttachmentResponse {

    private UUID id;
    private UUID ticketId;
    private UUID messageId;
    
    // File information
    private String fileName;
    private String fileUrl;
    private String fileType;
    private Long fileSizeBytes;
    private String description;
    
    // Security and access
    private Boolean isInternal;
    private Boolean isVirusScanPassed;
    private String downloadUrl;
    private String thumbnailUrl;
    
    // Metadata
    private LocalDateTime uploadedAt;
    private String uploadedBy;
    private String uploadedByName;
    private Integer downloadCount;
    
    // Display helpers
    private String formattedFileSize;
    private String fileIcon;
    private Boolean isImage;
    private Boolean isPdf;
    private Boolean isDocument;
}