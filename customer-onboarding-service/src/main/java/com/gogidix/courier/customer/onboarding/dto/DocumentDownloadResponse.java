package com.gogidix.courier.customer.onboarding.dto;

/**
 * Response DTO for document download operations.
 * 
 * @param fileName Original filename of the document
 * @param mimeType MIME type of the file
 * @param fileContent Binary content of the file
 * @param fileSize Size of the file in bytes
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
public record DocumentDownloadResponse(
    String fileName,
    String mimeType,
    byte[] fileContent,
    Long fileSize
) {
    
    /**
     * Get the file extension.
     */
    public String getFileExtension() {
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        }
        return "unknown";
    }
    
    /**
     * Format file size for display.
     */
    public String getFormattedFileSize() {
        if (fileSize == null) return "Unknown";
        
        if (fileSize < 1024) {
            return fileSize + " B";
        } else if (fileSize < 1024 * 1024) {
            return String.format("%.1f KB", fileSize / 1024.0);
        } else {
            return String.format("%.1f MB", fileSize / (1024.0 * 1024.0));
        }
    }
    
    /**
     * Check if the file is an image.
     */
    public Boolean isImage() {
        return mimeType != null && mimeType.startsWith("image/");
    }
    
    /**
     * Check if the file is a PDF.
     */
    public Boolean isPdf() {
        return "application/pdf".equals(mimeType);
    }
}