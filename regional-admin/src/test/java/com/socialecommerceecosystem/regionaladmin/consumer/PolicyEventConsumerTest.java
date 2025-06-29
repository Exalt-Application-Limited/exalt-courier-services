package com.exalt.courier.regionaladmin.consumer;

import com.socialecommerceecosystem.regionaladmin.service.PolicySyncService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.MessageHeaders;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PolicyEventConsumerTest {

    @Mock
    private PolicySyncService policySyncService;

    private PolicyEventConsumer policyEventConsumer;

    @BeforeEach
    void setUp() {
        policyEventConsumer = new PolicyEventConsumer();
        // Use reflection to set the autowired field
        try {
            java.lang.reflect.Field field = PolicyEventConsumer.class.getDeclaredField("policySyncService");
            field.setAccessible(true);
            field.set(policyEventConsumer, policySyncService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set mock service", e);
        }
    }

    @Test
    void consumePolicyCreatedEvent() {
        // Arrange
        Map<String, Object> policyData = new HashMap<>();
        policyData.put("id", 1L);
        policyData.put("policyType", "DELIVERY_SLA");
        
        // Act
        policyEventConsumer.consumePolicyEvent(policyData, "POLICY_CREATED.1", 0, "policy-events");
        
        // Assert
        verify(policySyncService, times(1)).handlePolicyCreated(policyData);
        verify(policySyncService, never()).handlePolicyUpdated(any());
        verify(policySyncService, never()).handlePolicyDeleted(any());
    }

    @Test
    void consumePolicyUpdatedEvent() {
        // Arrange
        Map<String, Object> policyData = new HashMap<>();
        policyData.put("id", 1L);
        policyData.put("policyType", "DELIVERY_SLA");
        
        // Act
        policyEventConsumer.consumePolicyEvent(policyData, "POLICY_UPDATED.1", 0, "policy-events");
        
        // Assert
        verify(policySyncService, times(1)).handlePolicyUpdated(policyData);
        verify(policySyncService, never()).handlePolicyCreated(any());
        verify(policySyncService, never()).handlePolicyDeleted(any());
    }

    @Test
    void consumePolicyDeletedEvent() {
        // Arrange
        Map<String, Object> policyData = new HashMap<>();
        policyData.put("id", 1L);
        
        // Act
        policyEventConsumer.consumePolicyEvent(policyData, "POLICY_DELETED.1", 0, "policy-events");
        
        // Assert
        verify(policySyncService, times(1)).handlePolicyDeleted(policyData);
        verify(policySyncService, never()).handlePolicyCreated(any());
        verify(policySyncService, never()).handlePolicyUpdated(any());
    }

    @Test
    void consumePolicyActivatedEvent() {
        // Arrange
        Map<String, Object> policyData = new HashMap<>();
        policyData.put("id", 1L);
        
        // Act
        policyEventConsumer.consumePolicyEvent(policyData, "POLICY_ACTIVATED.1", 0, "policy-events");
        
        // Assert
        verify(policySyncService, times(1)).handlePolicyActivated(policyData);
    }

    @Test
    void consumePolicyDeactivatedEvent() {
        // Arrange
        Map<String, Object> policyData = new HashMap<>();
        policyData.put("id", 1L);
        
        // Act
        policyEventConsumer.consumePolicyEvent(policyData, "POLICY_DEACTIVATED.1", 0, "policy-events");
        
        // Assert
        verify(policySyncService, times(1)).handlePolicyDeactivated(policyData);
    }

    @Test
    void consumeUnknownEventType() {
        // Arrange
        Map<String, Object> policyData = new HashMap<>();
        policyData.put("id", 1L);
        
        // Act
        policyEventConsumer.consumePolicyEvent(policyData, "UNKNOWN_EVENT.1", 0, "policy-events");
        
        // Assert
        verify(policySyncService, never()).handlePolicyCreated(any());
        verify(policySyncService, never()).handlePolicyUpdated(any());
        verify(policySyncService, never()).handlePolicyDeleted(any());
        verify(policySyncService, never()).handlePolicyActivated(any());
        verify(policySyncService, never()).handlePolicyDeactivated(any());
    }
}
