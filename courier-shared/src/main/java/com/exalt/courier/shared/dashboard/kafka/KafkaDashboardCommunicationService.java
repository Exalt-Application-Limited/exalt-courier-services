package com.exalt.courier.shared.dashboard.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.exalt.courier.shared.dashboard.DashboardCommunicationService;
import com.exalt.courier.shared.dashboard.DashboardMessage;
import com.exalt.courier.shared.dashboard.DashboardMessageHandler;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Kafka-based implementation of the DashboardCommunicationService.
 * Uses Kafka for reliable message delivery between dashboard levels.
 */
@Service
public class KafkaDashboardCommunicationService implements DashboardCommunicationService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaDashboardCommunicationService.class);
    private static final String TOPIC_PREFIX = "dashboard-comm-";
    
    @Value("${app.dashboard.level:UNKNOWN}")
    private String currentDashboardLevel;
    
    @Value("${app.dashboard.id:unknown}")
    private String currentDashboardId;
    
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private final Map<String, DashboardMessageHandler> messageHandlers = new ConcurrentHashMap<>();
    private final Map<String, DashboardMessage> pendingMessages = new ConcurrentHashMap<>();
    
    @PostConstruct
    public void init() {
        logger.info("Initializing Kafka dashboard communication service for level: {}, id: {}", 
                currentDashboardLevel, currentDashboardId);
    }
    
    @PreDestroy
    public void cleanup() {
        logger.info("Shutting down Kafka dashboard communication service");
    }
    
    @Override
    public CompletableFuture<Boolean> sendMessage(DashboardMessage message) {
        try {
            String topicName = TOPIC_PREFIX + message.getTargetLevel().toLowerCase();
            String messageJson = objectMapper.writeValueAsString(message);
            
            CompletableFuture<Boolean> result = new CompletableFuture<>();
            
            kafkaTemplate.send(topicName, message.getTargetId(), messageJson)
                .thenAccept(sendResult -> {
                    logger.debug("Message sent successfully to {}: {}", topicName, message.getId());
                    result.complete(true);
                })
                .exceptionally(throwable -> {
                    logger.error("Failed to send message to {}: {}", topicName, throwable.getMessage());
                    result.complete(false);
                    return null;
                });
            
            return result;
        } catch (Exception e) {
            logger.error("Error sending message", e);
            CompletableFuture<Boolean> failedFuture = new CompletableFuture<>();
            failedFuture.complete(false);
            return failedFuture;
        }
    }

    @Override
    public CompletableFuture<Map<String, Boolean>> broadcastMessage(DashboardMessage message, 
                                            List<String> targetLevels, 
                                            List<String> targetIds) {
        
        Map<String, CompletableFuture<Boolean>> futures = new HashMap<>();
        
        if (targetIds == null || targetIds.isEmpty()) {
            // Broadcast to all instances of the specified levels
            for (String level : targetLevels) {
                DashboardMessage levelMessage = copyMessageWithNewTarget(message, level, "all");
                futures.put(level + ":all", sendMessage(levelMessage));
            }
        } else {
            // Send to specific target IDs
            for (int i = 0; i < Math.min(targetLevels.size(), targetIds.size()); i++) {
                String level = targetLevels.get(i);
                String id = targetIds.get(i);
                DashboardMessage targetMessage = copyMessageWithNewTarget(message, level, id);
                futures.put(level + ":" + id, sendMessage(targetMessage));
            }
        }
        
        // Combine all futures into a single result
        CompletableFuture<Map<String, Boolean>> result = new CompletableFuture<>();
        
        CompletableFuture.allOf(futures.values().toArray(new CompletableFuture[0]))
            .thenRun(() -> {
                Map<String, Boolean> resultMap = new HashMap<>();
                futures.forEach((key, future) -> {
                    try {
                        resultMap.put(key, future.get());
                    } catch (Exception e) {
                        resultMap.put(key, false);
                    }
                });
                result.complete(resultMap);
            });
        
        return result;
    }
    
    private DashboardMessage copyMessageWithNewTarget(DashboardMessage original, String targetLevel, String targetId) {
        DashboardMessage copy = DashboardMessage.builder()
                .id(UUID.randomUUID().toString())
                .sourceLevel(original.getSourceLevel())
                .sourceId(original.getSourceId())
                .targetLevel(targetLevel)
                .targetId(targetId)
                .messageType(original.getMessageType())
                .subject(original.getSubject())
                .content(original.getContent())
                .metadata(original.getMetadata())
                .requiresAcknowledgment(original.isRequiresAcknowledgment())
                .priority(original.getPriority())
                .build();
        return copy;
    }

    @Override
    public String registerMessageHandler(DashboardMessageHandler handler) {
        String registrationId = UUID.randomUUID().toString();
        messageHandlers.put(registrationId, handler);
        logger.debug("Registered message handler with ID: {}", registrationId);
        return registrationId;
    }

    @Override
    public boolean unregisterMessageHandler(String registrationId) {
        boolean removed = messageHandlers.remove(registrationId) != null;
        if (removed) {
            logger.debug("Unregistered message handler with ID: {}", registrationId);
        } else {
            logger.warn("Failed to unregister message handler with ID: {} (not found)", registrationId);
        }
        return removed;
    }

    @Override
    public boolean acknowledgeMessage(String messageId) {
        DashboardMessage message = pendingMessages.get(messageId);
        if (message == null) {
            logger.warn("Cannot acknowledge message with ID: {} (not found)", messageId);
            return false;
        }
        
        message.setAcknowledged(true);
        message.setAcknowledgedAt(LocalDateTime.now());
        pendingMessages.remove(messageId);
        
        // Send acknowledgment message back to source
        DashboardMessage ackMessage = DashboardMessage.builder()
                .id(UUID.randomUUID().toString())
                .sourceLevel(getCurrentDashboardLevel())
                .sourceId(getCurrentDashboardId())
                .targetLevel(message.getSourceLevel())
                .targetId(message.getSourceId())
                .messageType("MESSAGE_ACK")
                .subject("Acknowledgment for message " + messageId)
                .content(messageId)
                .build();
        
        sendMessage(ackMessage);
        
        logger.debug("Acknowledged message with ID: {}", messageId);
        return true;
    }

    @Override
    public List<DashboardMessage> getPendingMessages(int limit) {
        return pendingMessages.values().stream()
                .sorted(Comparator.comparingInt(DashboardMessage::getPriority).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isConnected() {
        try {
            // Try to send a test message to verify connection
            kafkaTemplate.send("dashboard-comm-test", "test-message").get(1, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            logger.warn("Kafka connection test failed", e);
            return false;
        }
    }

    @Override
    public String getCurrentDashboardLevel() {
        return currentDashboardLevel;
    }

    @Override
    public String getCurrentDashboardId() {
        return currentDashboardId;
    }
    
    /**
     * Kafka listener for dashboard messages directed to this dashboard level.
     */
    @KafkaListener(topics = "${app.dashboard.topic:dashboard-comm-${app.dashboard.level}}", 
                   groupId = "${app.dashboard.group-id:dashboard-${app.dashboard.level}-${app.dashboard.id}}")
    public void receiveMessage(@Payload String messageJson,
                              @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        try {
            DashboardMessage message = objectMapper.readValue(messageJson, DashboardMessage.class);
            
            // Check if this message is intended for this dashboard or is a broadcast
            if (!currentDashboardId.equals(message.getTargetId()) && !"all".equals(message.getTargetId())) {
                return;
            }
            
            logger.debug("Received message: {}", message.getId());
            
            // Store message if it requires acknowledgment
            if (message.isRequiresAcknowledgment()) {
                pendingMessages.put(message.getId(), message);
            }
            
            // Process with registered handlers
            for (DashboardMessageHandler handler : messageHandlers.values()) {
                try {
                    DashboardMessage response = handler.handleMessage(message);
                    
                    // Send response if provided
                    if (response != null) {
                        sendMessage(response);
                    }
                } catch (Exception e) {
                    logger.error("Error in message handler processing message {}", message.getId(), e);
                }
            }
            
            // Automatically acknowledge message if it's an ack message itself
            if ("MESSAGE_ACK".equals(message.getMessageType())) {
                String originalMessageId = message.getContent();
                pendingMessages.remove(originalMessageId);
            }
            
        } catch (Exception e) {
            logger.error("Error processing received message", e);
        }
    }
}
