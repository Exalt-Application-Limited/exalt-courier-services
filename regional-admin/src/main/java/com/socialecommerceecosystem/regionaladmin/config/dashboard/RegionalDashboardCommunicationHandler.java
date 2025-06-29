package com.exalt.courier.regionaladmin.config.dashboard;

import com.microecosystem.courier.shared.dashboard.DashboardCommunicationService;
import com.microecosystem.courier.shared.dashboard.DashboardLevel;
import com.microecosystem.courier.shared.dashboard.DashboardMessage;
import com.microecosystem.courier.shared.dashboard.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handler for incoming dashboard communications in Regional Admin.
 * Acts as a bridge between Global HQ and Branch/Courier levels.
 */
public class RegionalDashboardCommunicationHandler {

    private static final Logger logger = LoggerFactory.getLogger(RegionalDashboardCommunicationHandler.class);
    
    private final DashboardCommunicationService communicationService;
    private final Map<String, String> messageHandlerRegistrations = new ConcurrentHashMap<>();
    
    public RegionalDashboardCommunicationHandler(DashboardCommunicationService communicationService) {
        this.communicationService = communicationService;
    }
    
    @PostConstruct
    public void init() {
        logger.info("Initializing Regional Admin dashboard communication handler");
    }
    
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        logger.info("Registering message handlers after application startup");
        
        // Register handlers for different message types
        registerPolicyUpdateHandler();
        registerConfigChangeHandler();
        registerStatusUpdateHandler();
        registerAlertHandler();
        registerMetricReportHandler();
        registerPerformanceIssueHandler();
        registerBranchRequestHandler();
        registerGlobalRequestHandler();
    }
    
    /**
     * Register handler for policy update messages from Global HQ.
     * These are forwarded to branch level after regional processing.
     */
    private void registerPolicyUpdateHandler() {
        String registrationId = communicationService.registerMessageHandler(message -> {
            if (MessageType.POLICY_UPDATE.equals(message.getMessageType()) && 
                DashboardLevel.GLOBAL.equals(message.getSourceLevel())) {
                logger.info("Received policy update from Global HQ: {}", message.getSubject());
                
                // Process policy update message at regional level
                // (implementation would include updating regional policy data,
                // database records, etc. based on the message content)
                
                // Forward policy update to all branches in this region
                forwardMessageToBranches(message);
                
                // Return acknowledgment to Global HQ
                return createAcknowledgmentMessage(message);
            }
            return null;
        });
        
        messageHandlerRegistrations.put("policyUpdateHandler", registrationId);
        logger.debug("Registered policy update handler with ID: {}", registrationId);
    }
    
    /**
     * Register handler for configuration change messages from Global HQ.
     */
    private void registerConfigChangeHandler() {
        String registrationId = communicationService.registerMessageHandler(message -> {
            if (MessageType.CONFIG_CHANGE.equals(message.getMessageType()) && 
                DashboardLevel.GLOBAL.equals(message.getSourceLevel())) {
                logger.info("Received configuration change from Global HQ: {}", message.getSubject());
                
                // Process configuration change message at regional level
                // (implementation would include updating regional configuration settings,
                // reloading services, etc. based on the message content)
                
                // Forward configuration change to branches if needed
                if (isConfigChangeRelevantForBranches(message)) {
                    forwardMessageToBranches(message);
                }
                
                // Return acknowledgment to Global HQ
                return createAcknowledgmentMessage(message);
            }
            return null;
        });
        
        messageHandlerRegistrations.put("configChangeHandler", registrationId);
        logger.debug("Registered configuration change handler with ID: {}", registrationId);
    }
    
    /**
     * Register handler for status update messages from both Global HQ and Branches.
     */
    private void registerStatusUpdateHandler() {
        String registrationId = communicationService.registerMessageHandler(message -> {
            if (MessageType.STATUS_UPDATE.equals(message.getMessageType())) {
                if (DashboardLevel.GLOBAL.equals(message.getSourceLevel())) {
                    // Status update from Global HQ
                    logger.info("Received status update from Global HQ: {}", message.getSubject());
                    
                    // Process Global status update at regional level
                    
                    // No response needed for status updates
                } else if (DashboardLevel.BRANCH.equals(message.getSourceLevel()) || 
                           DashboardLevel.LOCAL.equals(message.getSourceLevel())) {
                    // Status update from Branch/Local level
                    logger.info("Received status update from Branch {}: {}", 
                            message.getSourceId(), message.getSubject());
                    
                    // Process Branch status update at regional level
                    
                    // Forward aggregated status to Global HQ if relevant
                    if (isStatusUpdateRelevantForGlobal(message)) {
                        forwardStatusUpdateToGlobal(message);
                    }
                }
            }
            return null;
        });
        
        messageHandlerRegistrations.put("statusUpdateHandler", registrationId);
        logger.debug("Registered status update handler with ID: {}", registrationId);
    }
    
    /**
     * Register handler for alert messages from branches.
     */
    private void registerAlertHandler() {
        String registrationId = communicationService.registerMessageHandler(message -> {
            if ((MessageType.ALERT.equals(message.getMessageType()) || 
                 MessageType.CRITICAL_ALERT.equals(message.getMessageType())) && 
                (DashboardLevel.BRANCH.equals(message.getSourceLevel()) || 
                 DashboardLevel.LOCAL.equals(message.getSourceLevel()))) {
                
                boolean isCritical = MessageType.CRITICAL_ALERT.equals(message.getMessageType());
                logger.info("Received {} alert from Branch {}: {}", 
                        isCritical ? "CRITICAL" : "standard",
                        message.getSourceId(), message.getSubject());
                
                // Process alert message at regional level
                // (implementation would include triggering appropriate alerts,
                // notifications, remediation actions, etc. based on the severity)
                
                // Forward critical alerts to Global HQ
                if (isCritical || isAlertRelevantForGlobal(message)) {
                    forwardAlertToGlobal(message);
                }
                
                // Return acknowledgment for critical alerts
                if (isCritical) {
                    return createAcknowledgmentMessage(message);
                }
            }
            return null;
        });
        
        messageHandlerRegistrations.put("alertHandler", registrationId);
        logger.debug("Registered alert handler with ID: {}", registrationId);
    }
    
    /**
     * Register handler for metric report messages from branches.
     */
    private void registerMetricReportHandler() {
        String registrationId = communicationService.registerMessageHandler(message -> {
            if (MessageType.METRIC_REPORT.equals(message.getMessageType()) && 
                (DashboardLevel.BRANCH.equals(message.getSourceLevel()) || 
                 DashboardLevel.LOCAL.equals(message.getSourceLevel()))) {
                logger.info("Received metric report from Branch {}: {}", 
                        message.getSourceId(), message.getSubject());
                
                // Process metric report message
                // (implementation would include updating regional metrics, dashboards,
                // reports, etc. based on the message content)
                
                // No response needed for metric reports
            }
            return null;
        });
        
        messageHandlerRegistrations.put("metricReportHandler", registrationId);
        logger.debug("Registered metric report handler with ID: {}", registrationId);
    }
    
    /**
     * Register handler for performance issue messages from branches.
     */
    private void registerPerformanceIssueHandler() {
        String registrationId = communicationService.registerMessageHandler(message -> {
            if (MessageType.PERFORMANCE_ISSUE.equals(message.getMessageType()) && 
                (DashboardLevel.BRANCH.equals(message.getSourceLevel()) || 
                 DashboardLevel.LOCAL.equals(message.getSourceLevel()))) {
                logger.info("Received performance issue from Branch {}: {}", 
                        message.getSourceId(), message.getSubject());
                
                // Process performance issue message at regional level
                // (implementation would include triggering appropriate alerts,
                // notifications, beginning remediation, etc. based on the message content)
                
                // Forward significant performance issues to Global HQ
                if (isPerformanceIssueSignificant(message)) {
                    forwardPerformanceIssueToGlobal(message);
                }
                
                // Return acknowledgment
                return createAcknowledgmentMessage(message);
            }
            return null;
        });
        
        messageHandlerRegistrations.put("performanceIssueHandler", registrationId);
        logger.debug("Registered performance issue handler with ID: {}", registrationId);
    }
    
    /**
     * Register handler for request messages from branches.
     */
    private void registerBranchRequestHandler() {
        String registrationId = communicationService.registerMessageHandler(message -> {
            if (MessageType.ACTION_REQUEST.equals(message.getMessageType()) && 
                (DashboardLevel.BRANCH.equals(message.getSourceLevel()) || 
                 DashboardLevel.LOCAL.equals(message.getSourceLevel()))) {
                logger.info("Received action request from Branch {}: {}", 
                        message.getSourceId(), message.getSubject());
                
                // Process action request message at regional level
                // (implementation would include performing the requested action
                // if it can be handled at the regional level)
                
                // If the request needs to be handled by Global HQ, forward it
                if (isRequestForGlobalHQ(message)) {
                    forwardRequestToGlobal(message);
                    // Response will come from Global HQ
                    return null;
                }
                
                // Return response for requests handled at regional level
                return createActionResponseMessage(message, "Action completed by Regional Admin");
            }
            return null;
        });
        
        messageHandlerRegistrations.put("branchRequestHandler", registrationId);
        logger.debug("Registered branch request handler with ID: {}", registrationId);
    }
    
    /**
     * Register handler for request messages from Global HQ.
     */
    private void registerGlobalRequestHandler() {
        String registrationId = communicationService.registerMessageHandler(message -> {
            if (MessageType.ACTION_REQUEST.equals(message.getMessageType()) && 
                DashboardLevel.GLOBAL.equals(message.getSourceLevel())) {
                logger.info("Received action request from Global HQ: {}", message.getSubject());
                
                // Process action request message from Global HQ
                // (implementation would include performing the requested action
                // at the regional level)
                
                // Return response
                return createActionResponseMessage(message, "Action completed by Regional Admin");
            }
            return null;
        });
        
        messageHandlerRegistrations.put("globalRequestHandler", registrationId);
        logger.debug("Registered global request handler with ID: {}", registrationId);
    }
    
    /**
     * Forward a message from Global HQ to all branches in this region.
     */
    private void forwardMessageToBranches(DashboardMessage originalMessage) {
        // Create a new message based on the original, but from the regional level
        DashboardMessage forwardedMessage = new DashboardMessage.Builder()
                .withId(java.util.UUID.randomUUID().toString())
                .from(DashboardLevel.REGIONAL, communicationService.getCurrentDashboardId())
                .to(DashboardLevel.BRANCH, null) // null ID means broadcast to all branches
                .ofType(originalMessage.getMessageType())
                .withSubject(originalMessage.getSubject())
                .withContent(originalMessage.getContent())
                .withMetadata(getForwardingMetadata(originalMessage))
                .build();
        
        // Send the message to all branches
        communicationService.broadcastMessage(forwardedMessage, 
                Arrays.asList(DashboardLevel.BRANCH, DashboardLevel.LOCAL), null)
                .thenAccept(result -> {
                    logger.debug("Forwarded message to {} branches", result.size());
                });
    }
    
    /**
     * Forward a status update from branches to Global HQ.
     */
    private void forwardStatusUpdateToGlobal(DashboardMessage originalMessage) {
        // Create a new aggregated status message to Global HQ
        DashboardMessage aggregatedMessage = new DashboardMessage.Builder()
                .withId(java.util.UUID.randomUUID().toString())
                .from(DashboardLevel.REGIONAL, communicationService.getCurrentDashboardId())
                .to(DashboardLevel.GLOBAL, null) // null ID means broadcast to all global instances
                .ofType(MessageType.STATUS_UPDATE)
                .withSubject("Regional Status Update: " + originalMessage.getSubject())
                .withContent("Aggregated status update from region")
                .withMetadata(getForwardingMetadata(originalMessage))
                .build();
        
        // Send the message to Global HQ
        communicationService.sendMessage(aggregatedMessage)
                .thenAccept(result -> {
                    logger.debug("Forwarded status update to Global HQ: {}", result);
                });
    }
    
    /**
     * Forward an alert from branches to Global HQ.
     */
    private void forwardAlertToGlobal(DashboardMessage originalMessage) {
        // Create a new alert message to Global HQ
        DashboardMessage forwardedAlert = new DashboardMessage.Builder()
                .withId(java.util.UUID.randomUUID().toString())
                .from(DashboardLevel.REGIONAL, communicationService.getCurrentDashboardId())
                .to(DashboardLevel.GLOBAL, null) // null ID means broadcast to all global instances
                .ofType(originalMessage.getMessageType()) // Keep the same type (ALERT or CRITICAL_ALERT)
                .withSubject("Regional Alert: " + originalMessage.getSubject())
                .withContent(originalMessage.getContent())
                .withMetadata(getForwardingMetadata(originalMessage))
                .build();
        
        // Send the message to Global HQ
        communicationService.sendMessage(forwardedAlert)
                .thenAccept(result -> {
                    logger.debug("Forwarded alert to Global HQ: {}", result);
                });
    }
    
    /**
     * Forward a performance issue from branches to Global HQ.
     */
    private void forwardPerformanceIssueToGlobal(DashboardMessage originalMessage) {
        // Create a new performance issue message to Global HQ
        DashboardMessage forwardedIssue = new DashboardMessage.Builder()
                .withId(java.util.UUID.randomUUID().toString())
                .from(DashboardLevel.REGIONAL, communicationService.getCurrentDashboardId())
                .to(DashboardLevel.GLOBAL, null) // null ID means broadcast to all global instances
                .ofType(MessageType.PERFORMANCE_ISSUE)
                .withSubject("Regional Performance Issue: " + originalMessage.getSubject())
                .withContent(originalMessage.getContent())
                .withMetadata(getForwardingMetadata(originalMessage))
                .build();
        
        // Send the message to Global HQ
        communicationService.sendMessage(forwardedIssue)
                .thenAccept(result -> {
                    logger.debug("Forwarded performance issue to Global HQ: {}", result);
                });
    }
    
    /**
     * Forward a request from branches to Global HQ.
     */
    private void forwardRequestToGlobal(DashboardMessage originalMessage) {
        // Create a new request message to Global HQ
        DashboardMessage forwardedRequest = new DashboardMessage.Builder()
                .withId(java.util.UUID.randomUUID().toString())
                .from(DashboardLevel.REGIONAL, communicationService.getCurrentDashboardId())
                .to(DashboardLevel.GLOBAL, null) // null ID means broadcast to all global instances
                .ofType(MessageType.ACTION_REQUEST)
                .withSubject("Forwarded Request: " + originalMessage.getSubject())
                .withContent(originalMessage.getContent())
                .withMetadata(getForwardingMetadata(originalMessage))
                .build();
        
        // Send the message to Global HQ
        communicationService.sendMessage(forwardedRequest)
                .thenAccept(result -> {
                    logger.debug("Forwarded request to Global HQ: {}", result);
                });
    }
    
    /**
     * Create metadata for forwarded messages.
     */
    private Map<String, String> getForwardingMetadata(DashboardMessage originalMessage) {
        Map<String, String> metadata = new HashMap<>(originalMessage.getMetadata());
        metadata.put("originalSourceLevel", originalMessage.getSourceLevel());
        metadata.put("originalSourceId", originalMessage.getSourceId());
        metadata.put("originalMessageId", originalMessage.getId());
        metadata.put("forwardedBy", communicationService.getCurrentDashboardId());
        metadata.put("forwardedTimestamp", String.valueOf(System.currentTimeMillis()));
        return metadata;
    }
    
    /**
     * Check if a configuration change is relevant for branches.
     */
    private boolean isConfigChangeRelevantForBranches(DashboardMessage message) {
        // In a real implementation, this would check the configuration type
        // or other criteria to determine if it should be forwarded to branches
        
        // For this example, forward all configuration changes to branches
        return true;
    }
    
    /**
     * Check if a status update from a branch is relevant for Global HQ.
     */
    private boolean isStatusUpdateRelevantForGlobal(DashboardMessage message) {
        // In a real implementation, this would check the status type, severity,
        // or other criteria to determine if it should be forwarded to Global HQ
        
        // For this example, check if there's a metadata flag indicating global relevance
        String globalRelevance = message.getMetadata().get("globalRelevance");
        if (globalRelevance != null && globalRelevance.equals("true")) {
            return true;
        }
        
        // Example condition: Forward if status contains "critical" or "warning"
        String content = message.getContent().toLowerCase();
        return content.contains("critical") || content.contains("warning");
    }
    
    /**
     * Check if an alert is relevant for Global HQ.
     */
    private boolean isAlertRelevantForGlobal(DashboardMessage message) {
        // In a real implementation, this would check the alert type, severity,
        // or other criteria to determine if it should be forwarded to Global HQ
        
        // For this example, check the severity level in metadata
        String severity = message.getMetadata().get("severity");
        if (severity != null) {
            // Forward high and critical severity alerts
            return severity.equals("high") || severity.equals("critical");
        }
        
        // Default: only forward alerts with specific keywords
        String content = message.getContent().toLowerCase();
        return content.contains("critical") || content.contains("major") || 
               content.contains("emergency") || content.contains("outage");
    }
    
    /**
     * Check if a performance issue is significant enough to forward to Global HQ.
     */
    private boolean isPerformanceIssueSignificant(DashboardMessage message) {
        // In a real implementation, this would check the performance metrics,
        // duration, impact, or other criteria to determine significance
        
        // For this example, check the impact level in metadata
        String impact = message.getMetadata().get("impact");
        if (impact != null) {
            // Forward high and severe impact issues
            return impact.equals("high") || impact.equals("severe");
        }
        
        // Default: only forward issues with specific keywords
        String content = message.getContent().toLowerCase();
        return content.contains("severe") || content.contains("major") || 
               content.contains("significant") || content.contains("prolonged");
    }
    
    /**
     * Check if a request from a branch needs to be handled by Global HQ.
     */
    private boolean isRequestForGlobalHQ(DashboardMessage message) {
        // In a real implementation, this would check the request type, scope,
        // or other criteria to determine if it should be forwarded to Global HQ
        
        // For this example, check if there's a metadata flag indicating global handling
        String globalHandling = message.getMetadata().get("requiresGlobalHandling");
        if (globalHandling != null && globalHandling.equals("true")) {
            return true;
        }
        
        // Example condition: Forward if request contains specific keywords
        String content = message.getContent().toLowerCase();
        return content.contains("policy override") || content.contains("global config") || 
               content.contains("system-wide") || content.contains("headquarters");
    }
    
    /**
     * Create an acknowledgment message.
     */
    private DashboardMessage createAcknowledgmentMessage(DashboardMessage originalMessage) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("originalMessageId", originalMessage.getId());
        
        return new DashboardMessage.Builder()
                .withId(java.util.UUID.randomUUID().toString())
                .from(DashboardLevel.REGIONAL, communicationService.getCurrentDashboardId())
                .to(originalMessage.getSourceLevel(), originalMessage.getSourceId())
                .ofType("MESSAGE_ACK")
                .withSubject("Acknowledgment: " + originalMessage.getSubject())
                .withContent("Message received and processed by Regional Admin")
                .withMetadata(metadata)
                .build();
    }
    
    /**
     * Create an action response message.
     */
    private DashboardMessage createActionResponseMessage(DashboardMessage requestMessage, String result) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("originalMessageId", requestMessage.getId());
        
        return new DashboardMessage.Builder()
                .withId(java.util.UUID.randomUUID().toString())
                .from(DashboardLevel.REGIONAL, communicationService.getCurrentDashboardId())
                .to(requestMessage.getSourceLevel(), requestMessage.getSourceId())
                .ofType(MessageType.ACTION_RESPONSE)
                .withSubject("Response: " + requestMessage.getSubject())
                .withContent(result)
                .withMetadata(metadata)
                .build();
    }
}
