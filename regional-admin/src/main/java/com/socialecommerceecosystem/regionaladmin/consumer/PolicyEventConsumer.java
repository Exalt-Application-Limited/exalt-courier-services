package com.gogidix.courier.regionaladmin.consumer;

import com.socialecommerceecosystem.regionaladmin.service.PolicySyncService;
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
 * Kafka consumer for policy events from HQ Admin.
 * Listens for policy creation, update, and deletion events
 * and processes them accordingly.
 */
@Component
public class PolicyEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(PolicyEventConsumer.class);

    @Autowired
    private PolicySyncService policySyncService;

    /**
     * Listens for policy events from the policy-events topic.
     * 
     * @param policyData The policy event data
     * @param key The message key
     * @param partition The partition the message was received from
     * @param topic The topic the message was received from
     */
    @KafkaListener(topics = "${kafka.topics.policy-events:policy-events}",
                  groupId = "${spring.kafka.consumer.group-id:regional-admin-group}")
    public void consumePolicyEvent(@Payload Map<String, Object> policyData,
                                  @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
                                  @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                  @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        
        logger.info("Received policy event: key={}, topic={}, partition={}", key, topic, partition);
        logger.debug("Policy event data: {}", policyData);
        
        try {
            // Extract event type from the message key
            String eventType = key.split("\\.")[0]; // e.g., "POLICY_CREATED.123" -> "POLICY_CREATED"
            
            switch (eventType) {
                case "POLICY_CREATED":
                    policySyncService.handlePolicyCreated(policyData);
                    break;
                case "POLICY_UPDATED":
                    policySyncService.handlePolicyUpdated(policyData);
                    break;
                case "POLICY_DELETED":
                    policySyncService.handlePolicyDeleted(policyData);
                    break;
                case "POLICY_ACTIVATED":
                    policySyncService.handlePolicyActivated(policyData);
                    break;
                case "POLICY_DEACTIVATED":
                    policySyncService.handlePolicyDeactivated(policyData);
                    break;
                default:
                    logger.warn("Unknown policy event type: {}", eventType);
            }
        } catch (Exception e) {
            logger.error("Error processing policy event: {}", e.getMessage(), e);
            // Note: In a production system, consider using a Dead Letter Queue (DLQ)
            // to handle failed messages, or implement a retry mechanism
        }
    }
}
