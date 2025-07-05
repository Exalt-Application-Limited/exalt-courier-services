package com.gogidix.courier.customer.onboarding.dto;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Response DTO for document verification statistics and metrics.
 * 
 * @param totalDocuments Total number of documents in the system
 * @param pendingDocuments Number of documents pending review
 * @param approvedDocuments Number of approved documents
 * @param rejectedDocuments Number of rejected documents
 * @param averageProcessingTime Average time to process documents (in hours)
 * @param documentsByType Count of documents by type
 * @param documentsByStatus Count of documents by status
 * @param aiVerificationRate Percentage of documents processed by AI
 * @param manualReviewRate Percentage requiring manual review
 * @param approvalRate Overall approval rate (percentage)
 * @param generatedAt When these statistics were generated
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
public record DocumentVerificationStatistics(
    Long totalDocuments,
    Long pendingDocuments,
    Long approvedDocuments,
    Long rejectedDocuments,
    Double averageProcessingTime,
    Map<String, Long> documentsByType,
    Map<String, Long> documentsByStatus,
    Double aiVerificationRate,
    Double manualReviewRate,
    Double approvalRate,
    LocalDateTime generatedAt
) {
    
    /**
     * Get completion rate (approved + rejected / total).
     */
    public Double getCompletionRate() {
        if (totalDocuments == null || totalDocuments == 0) {
            return 0.0;
        }
        
        long completed = (approvedDocuments != null ? approvedDocuments : 0) + 
                        (rejectedDocuments != null ? rejectedDocuments : 0);
        return (completed * 100.0) / totalDocuments;
    }
    
    /**
     * Get pending rate (pending / total).
     */
    public Double getPendingRate() {
        if (totalDocuments == null || totalDocuments == 0) {
            return 0.0;
        }
        
        return ((pendingDocuments != null ? pendingDocuments : 0) * 100.0) / totalDocuments;
    }
    
    /**
     * Check if system is processing efficiently (< 80% pending).
     */
    public Boolean isProcessingEfficiently() {
        return getPendingRate() < 80.0;
    }
    
    /**
     * Get the most common document type.
     */
    public String getMostCommonDocumentType() {
        if (documentsByType == null || documentsByType.isEmpty()) {
            return "N/A";
        }
        
        return documentsByType.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("N/A");
    }
    
    /**
     * Format average processing time for display.
     */
    public String getFormattedAverageProcessingTime() {
        if (averageProcessingTime == null) {
            return "N/A";
        }
        
        if (averageProcessingTime < 1.0) {
            return String.format("%.0f minutes", averageProcessingTime * 60);
        } else if (averageProcessingTime < 24.0) {
            return String.format("%.1f hours", averageProcessingTime);
        } else {
            return String.format("%.1f days", averageProcessingTime / 24);
        }
    }
}