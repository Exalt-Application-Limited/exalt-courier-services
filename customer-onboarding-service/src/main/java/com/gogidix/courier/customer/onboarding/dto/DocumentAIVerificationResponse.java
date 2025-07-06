package com.gogidix.courier.customer.onboarding.dto;

import java.time.LocalDateTime;

/**
 * Response DTO for AI verification initiation.
 * 
 * @param documentReferenceId Reference ID of the document being verified
 * @param verificationInitiated Whether AI verification was successfully started
 * @param message Status or error message
 * @param initiatedAt When AI verification was started
 * @param estimatedCompletionTime Expected time for AI verification completion
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
public record DocumentAIVerificationResponse(
    String documentReferenceId,
    Boolean verificationInitiated,
    String message,
    LocalDateTime initiatedAt,
    String estimatedCompletionTime
) {
    
    /**
     * Creates a successful AI verification response.
     */
    public static DocumentAIVerificationResponse success(String documentReferenceId) {
        return new DocumentAIVerificationResponse(
            documentReferenceId,
            true,
            "AI verification initiated successfully",
            LocalDateTime.now(),
            "2-5 minutes"
        );
    }
    
    /**
     * Creates a failed AI verification response.
     */
    public static DocumentAIVerificationResponse failed(String documentReferenceId, String reason) {
        return new DocumentAIVerificationResponse(
            documentReferenceId,
            false,
            "AI verification failed: " + reason,
            LocalDateTime.now(),
            "Manual review required"
        );
    }
    
    /**
     * Creates a response when document is not suitable for AI verification.
     */
    public static DocumentAIVerificationResponse notSuitable(String documentReferenceId, String reason) {
        return new DocumentAIVerificationResponse(
            documentReferenceId,
            false,
            "Document not suitable for AI verification: " + reason,
            LocalDateTime.now(),
            "Manual review required"
        );
    }
}