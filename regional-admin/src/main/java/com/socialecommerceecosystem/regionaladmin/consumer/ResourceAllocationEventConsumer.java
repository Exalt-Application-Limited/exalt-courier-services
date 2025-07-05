package com.gogidix.courier.regionaladmin.consumer;

import com.socialecommerceecosystem.regionaladmin.service.ResourceAllocationSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Kafka consumer for resource allocation events from HQ Admin.
 * Listens for resource allocation creation, update, and status change events
 * and processes them accordingly.
 */
@Component
public class ResourceAllocationEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ResourceAllocationEventConsumer.class);

    @Autowired
    private ResourceAllocationSyncService resourceAllocationSyncService;

    /**
     * Listens for resource allocation events from the resource-allocation-events topic.
     * 
     * @param allocationData The resource allocation event data
     * @param key The message key
     * @param partition The partition the message was received from
     * @param topic The topic the message was received from
     */
    @KafkaListener(topics = "${kafka.topics.resource-allocation-events:resource-allocation-events}",
                  groupId = "${spring.kafka.consumer.group-id:regional-admin-group}")
    public void consumeResourceAllocationEvent(@Payload Map<String, Object> allocationData,
                                             @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
                                             @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                             @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        
        logger.info("Received resource allocation event: key={}, topic={}, partition={}", key, topic, partition);
        logger.debug("Resource allocation event data: {}", allocationData);
        
        try {
            // Extract event type from the message key
            String eventType = key.split("\\.")[0]; // e.g., "ALLOCATION_CREATED.123" -> "ALLOCATION_CREATED"
            
            switch (eventType) {
                case "ALLOCATION_CREATED":
                    resourceAllocationSyncService.handleAllocationCreated(allocationData);
                    break;
                case "ALLOCATION_UPDATED":
                    resourceAllocationSyncService.handleAllocationUpdated(allocationData);
                    break;
                case "ALLOCATION_ACTIVATED":
                    resourceAllocationSyncService.handleAllocationActivated(allocationData);
                    break;
                case "ALLOCATION_DEACTIVATED":
                    resourceAllocationSyncService.handleAllocationDeactivated(allocationData);
                    break;
                case "ALLOCATION_EXPIRED":
                    resourceAllocationSyncService.handleAllocationExpired(allocationData);
                    break;
                case "ALLOCATION_PLAN_EXECUTED":
                    resourceAllocationSyncService.handleAllocationPlanExecuted(allocationData);
                    break;
                default:
                    logger.warn("Unknown resource allocation event type: {}", eventType);
            }
        } catch (Exception e) {
            logger.error("Error processing resource allocation event: {}", e.getMessage(), e);
            // Note: In a production system, consider using a Dead Letter Queue (DLQ)
            // to handle failed messages, or implement a retry mechanism
        }
    }
}
