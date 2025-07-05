package com.gogidix.courier.customer.onboarding.dto;

import com.gogidix.courier.customer.onboarding.model.CustomerVerificationDocument.DocumentType;

import java.util.List;
import java.util.Map;

/**
 * Response DTO showing the completion status of required documents for KYC.
 * 
 * @param applicationReferenceId The customer application reference ID
 * @param isComplete Whether all required documents are approved
 * @param completionPercentage Percentage of required documents completed (0-100)
 * @param requiredDocuments List of all required document types
 * @param completedDocuments List of document types that are approved
 * @param pendingDocuments List of document types that are still pending
 * @param rejectedDocuments List of document types that were rejected
 * @param missingDocuments List of document types that haven't been uploaded
 * @param documentStatusMap Map of document type to current status
 * @param nextActions List of actions the customer needs to take
 * @param estimatedCompletionTime Estimated time to complete remaining requirements
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
public record DocumentCompletionStatus(
    String applicationReferenceId,
    Boolean isComplete,
    Integer completionPercentage,
    List<RequiredDocumentInfo> requiredDocuments,
    List<DocumentType> completedDocuments,
    List<DocumentType> pendingDocuments,
    List<DocumentType> rejectedDocuments,
    List<DocumentType> missingDocuments,
    Map<DocumentType, String> documentStatusMap,
    List<String> nextActions,
    String estimatedCompletionTime
) {
    
    /**
     * Information about a required document type.
     */
    public record RequiredDocumentInfo(
        DocumentType documentType,
        String displayName,
        String description,
        Boolean isRequired,
        Boolean isOptional,
        List<String> acceptedFormats,
        String maxFileSize,
        String instructions
    ) {}
    
    /**
     * Creates a completion status for an incomplete application.
     */
    public static DocumentCompletionStatus incomplete(
            String applicationReferenceId,
            List<RequiredDocumentInfo> requiredDocuments,
            List<DocumentType> completed,
            List<DocumentType> pending,
            List<DocumentType> rejected,
            List<DocumentType> missing) {
        
        int totalRequired = requiredDocuments.size();
        int completedCount = completed.size();
        int percentage = totalRequired > 0 ? (completedCount * 100) / totalRequired : 0;
        
        List<String> nextActions = generateNextActions(pending, rejected, missing);
        String estimatedTime = calculateEstimatedTime(pending, rejected, missing);
        
        Map<DocumentType, String> statusMap = createStatusMap(completed, pending, rejected, missing);
        
        return new DocumentCompletionStatus(
            applicationReferenceId,
            false,
            percentage,
            requiredDocuments,
            completed,
            pending,
            rejected,
            missing,
            statusMap,
            nextActions,
            estimatedTime
        );
    }
    
    /**
     * Creates a completion status for a complete application.
     */
    public static DocumentCompletionStatus complete(
            String applicationReferenceId,
            List<RequiredDocumentInfo> requiredDocuments,
            List<DocumentType> completed) {
        
        return new DocumentCompletionStatus(
            applicationReferenceId,
            true,
            100,
            requiredDocuments,
            completed,
            List.of(),
            List.of(),
            List.of(),
            createStatusMap(completed, List.of(), List.of(), List.of()),
            List.of("All required documents have been verified. Your KYC process is complete!"),
            "Complete"
        );
    }
    
    private static List<String> generateNextActions(List<DocumentType> pending, 
                                                  List<DocumentType> rejected, 
                                                  List<DocumentType> missing) {
        List<String> actions = new java.util.ArrayList<>();
        
        if (!missing.isEmpty()) {
            actions.add("Upload missing documents: " + missing.stream()
                .map(type -> type.name().replace('_', ' '))
                .reduce((a, b) -> a + ", " + b).orElse(""));
        }
        
        if (!rejected.isEmpty()) {
            actions.add("Resubmit rejected documents with corrections: " + rejected.stream()
                .map(type -> type.name().replace('_', ' '))
                .reduce((a, b) -> a + ", " + b).orElse(""));
        }
        
        if (!pending.isEmpty()) {
            actions.add("Wait for verification of pending documents: " + pending.stream()
                .map(type -> type.name().replace('_', ' '))
                .reduce((a, b) -> a + ", " + b).orElse(""));
        }
        
        return actions;
    }
    
    private static String calculateEstimatedTime(List<DocumentType> pending, 
                                               List<DocumentType> rejected, 
                                               List<DocumentType> missing) {
        int totalActions = pending.size() + rejected.size() + missing.size();
        
        if (totalActions == 0) {
            return "Complete";
        } else if (totalActions <= 2) {
            return "1-2 business days";
        } else {
            return "2-5 business days";
        }
    }
    
    private static Map<DocumentType, String> createStatusMap(List<DocumentType> completed,
                                                          List<DocumentType> pending,
                                                          List<DocumentType> rejected,
                                                          List<DocumentType> missing) {
        Map<DocumentType, String> statusMap = new java.util.HashMap<>();
        
        completed.forEach(type -> statusMap.put(type, "APPROVED"));
        pending.forEach(type -> statusMap.put(type, "PENDING"));
        rejected.forEach(type -> statusMap.put(type, "REJECTED"));
        missing.forEach(type -> statusMap.put(type, "MISSING"));
        
        return statusMap;
    }
}