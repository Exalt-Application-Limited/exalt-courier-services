package com.gogidix.courier.courier.branch.dashboard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsocial.courier.branch.dashboard.model.DashboardMessage;
import com.microsocial.courier.branch.dashboard.model.MessageType;

import java.util.concurrent.CompletableFuture;

/**
 * Handles communication between the Branch level dashboard and the Regional Admin dashboard.
 * This class is responsible for sending messages to and receiving messages from the Regional Admin dashboard.
 */
@Component
public class BranchDashboardCommunicationHandler {

    private static final Logger logger = LoggerFactory.getLogger(BranchDashboardCommunicationHandler.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String branchToRegionalTopic;
    private final String regionalToBranchTopic;
    private final String branchId;
    private final String regionId;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final BranchDataCacheService dataCacheService;

    public BranchDashboardCommunicationHandler(
            KafkaTemplate<String, Object> kafkaTemplate,
            String branchToRegionalTopic,
            String regionalToBranchTopic,
            String branchId,
            String regionId) {
        this.kafkaTemplate = kafkaTemplate;
        this.branchToRegionalTopic = branchToRegionalTopic;
        this.regionalToBranchTopic = regionalToBranchTopic;
        this.branchId = branchId;
        this.regionId = regionId;
        this.dataCacheService = new BranchDataCacheService();
    }

    /**
     * Sends a message to the Regional Admin dashboard.
     *
     * @param message The message to send
     * @return CompletableFuture for async handling
     */
    public CompletableFuture<Void> sendMessageToRegional(DashboardMessage message) {
        try {
            logger.info("Sending message to Regional dashboard: {}", message);
            
            // Store a copy in cache in case of offline operation
            dataCacheService.cacheOutgoingMessage(message);
            
            return kafkaTemplate.send(branchToRegionalTopic, branchId, message)
                    .thenRun(() -> {
                        logger.info("Message sent successfully to Regional dashboard");
                        dataCacheService.markMessageDelivered(message.getMessageId());
                    })
                    .exceptionally(ex -> {
                        logger.error("Failed to send message to Regional dashboard", ex);
                        return null;
                    });
        } catch (Exception ex) {
            logger.error("Error preparing message for Regional dashboard", ex);
            return CompletableFuture.failedFuture(ex);
        }
    }

    /**
     * Processes messages from the Regional Admin dashboard.
     *
     * @param messageJson The JSON string representation of the incoming message
     */
    @KafkaListener(topics = "${dashboard.communication.topic.regional-to-branch:regional-to-branch-communication}", 
                  groupId = "${dashboard.branch.id}-consumer-group")
    public void processRegionalMessage(String messageJson) {
        try {
            DashboardMessage message = objectMapper.readValue(messageJson, DashboardMessage.class);
            
            // Verify this message is intended for this branch
            if (!message.getTargetId().equals(branchId)) {
                logger.debug("Ignoring message intended for another branch: {}", message.getTargetId());
                return;
            }
            
            logger.info("Received message from Regional dashboard: {}", message);
            
            // Process the message based on its type
            switch (message.getMessageType()) {
                case DATA_REQUEST:
                    handleDataRequest(message);
                    break;
                case CONFIGURATION_UPDATE:
                    handleConfigurationUpdate(message);
                    break;
                case ALERT:
                    handleAlert(message);
                    break;
                case COMMAND:
                    handleCommand(message);
                    break;
                default:
                    logger.warn("Unhandled message type: {}", message.getMessageType());
            }
            
            // Acknowledge receipt
            sendAcknowledgment(message);
            
        } catch (Exception ex) {
            logger.error("Error processing message from Regional dashboard", ex);
        }
    }
    
    private void handleDataRequest(DashboardMessage message) {
        logger.info("Processing data request from Regional dashboard: {}", message.getContent());
        // Implementation for handling data requests
        // This would collect the requested data and send it back to the Regional dashboard
    }
    
    private void handleConfigurationUpdate(DashboardMessage message) {
        logger.info("Processing configuration update from Regional dashboard: {}", message.getContent());
        // Implementation for handling configuration updates
        // This would update local configuration based on the message content
    }
    
    private void handleAlert(DashboardMessage message) {
        logger.info("Processing alert from Regional dashboard: {}", message.getContent());
        // Implementation for handling alerts
        // This would display alerts to branch users or trigger specific actions
    }
    
    private void handleCommand(DashboardMessage message) {
        logger.info("Processing command from Regional dashboard: {}", message.getContent());
        // Implementation for handling commands
        // This would execute commands received from the Regional dashboard
    }
    
    private void sendAcknowledgment(DashboardMessage originalMessage) {
        DashboardMessage acknowledgment = new DashboardMessage();
        acknowledgment.setMessageType(MessageType.ACKNOWLEDGMENT);
        acknowledgment.setSourceId(branchId);
        acknowledgment.setTargetId(regionId);
        acknowledgment.setReferenceId(originalMessage.getMessageId());
        acknowledgment.setContent("Message received and processed");
        
        sendMessageToRegional(acknowledgment);
    }
    
    /**
     * Checks for and sends any cached messages that failed to send previously.
     * This is typically called when connectivity is restored.
     */
    public void sendCachedMessages() {
        dataCacheService.getUndeliveredMessages().forEach(this::sendMessageToRegional);
    }
} 