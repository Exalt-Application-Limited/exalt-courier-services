package com.gogidix.courier.branch.dashboard;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogidix.courier.branch.dashboard.model.DashboardMessage;
import com.gogidix.courier.branch.dashboard.model.MessagePriority;
import com.gogidix.courier.branch.dashboard.model.MessageType;

import java.util.concurrent.CompletableFuture;

@ExtendWith(MockitoExtension.class)
public class BranchDashboardCommunicationHandlerTest {

    private BranchDashboardCommunicationHandler communicationHandler;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private BranchDataCacheService dataCacheService;

    private final String branchToRegionalTopic = "test-branch-to-regional";
    private final String regionalToBranchTopic = "test-regional-to-branch";
    private final String branchId = "test-branch-id";
    private final String regionId = "test-region-id";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        communicationHandler = new BranchDashboardCommunicationHandler(
                kafkaTemplate,
                branchToRegionalTopic,
                regionalToBranchTopic,
                branchId,
                regionId);
        
        // Using reflection to replace the dataCacheService with our mock
        try {
            java.lang.reflect.Field field = BranchDashboardCommunicationHandler.class.getDeclaredField("dataCacheService");
            field.setAccessible(true);
            field.set(communicationHandler, dataCacheService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mock dataCacheService", e);
        }
    }

    @Test
    public void testSendMessageToRegional() {
        // Create test message
        DashboardMessage message = new DashboardMessage();
        message.setMessageId("test-message-id");
        message.setMessageType(MessageType.STATUS_UPDATE);
        message.setSourceId(branchId);
        message.setTargetId(regionId);
        message.setContent("Test status update");
        message.setPriority(MessagePriority.NORMAL);

        // Mock Kafka response
        CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(null);
        when(kafkaTemplate.send(anyString(), anyString(), any(DashboardMessage.class))).thenReturn(future);

        // Call the method
        communicationHandler.sendMessageToRegional(message);

        // Verify interactions
        verify(dataCacheService).cacheOutgoingMessage(message);
        verify(kafkaTemplate).send(eq(branchToRegionalTopic), eq(branchId), eq(message));
    }

    @Test
    public void testProcessRegionalMessage_DataRequest() throws JsonProcessingException {
        // Create test message
        DashboardMessage message = new DashboardMessage();
        message.setMessageId("test-message-id");
        message.setMessageType(MessageType.DATA_REQUEST);
        message.setSourceId(regionId);
        message.setTargetId(branchId);
        message.setContent("Request for delivery metrics");
        message.setPriority(MessagePriority.HIGH);

        // Serialize the message to JSON
        String messageJson = objectMapper.writeValueAsString(message);

        // Mock Kafka response for acknowledgment
        CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(null);
        when(kafkaTemplate.send(anyString(), anyString(), any(DashboardMessage.class))).thenReturn(future);

        // Call the method
        communicationHandler.processRegionalMessage(messageJson);

        // Verify acknowledgment was sent
        verify(kafkaTemplate).send(
                eq(branchToRegionalTopic),
                eq(branchId),
                any(DashboardMessage.class));
    }

    @Test
    public void testProcessRegionalMessage_Alert() throws JsonProcessingException {
        // Create test message
        DashboardMessage message = new DashboardMessage();
        message.setMessageId("test-alert-id");
        message.setMessageType(MessageType.ALERT);
        message.setSourceId(regionId);
        message.setTargetId(branchId);
        message.setContent("Critical weather alert");
        message.setPriority(MessagePriority.CRITICAL);

        // Serialize the message to JSON
        String messageJson = objectMapper.writeValueAsString(message);

        // Mock Kafka response for acknowledgment
        CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(null);
        when(kafkaTemplate.send(anyString(), anyString(), any(DashboardMessage.class))).thenReturn(future);

        // Call the method
        communicationHandler.processRegionalMessage(messageJson);

        // Verify acknowledgment was sent
        verify(kafkaTemplate).send(
                eq(branchToRegionalTopic),
                eq(branchId),
                any(DashboardMessage.class));
    }

    @Test
    public void testSendCachedMessages() {
        // Setup cached messages
        DashboardMessage message1 = new DashboardMessage(MessageType.STATUS_UPDATE, branchId, regionId, "Cached message 1");
        DashboardMessage message2 = new DashboardMessage(MessageType.STATUS_UPDATE, branchId, regionId, "Cached message 2");
        
        when(dataCacheService.getUndeliveredMessages()).thenReturn(java.util.Arrays.asList(message1, message2));
        
        // Mock Kafka response
        CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(null);
        when(kafkaTemplate.send(anyString(), anyString(), any(DashboardMessage.class))).thenReturn(future);

        // Call the method
        communicationHandler.sendCachedMessages();

        // Verify interactions
        verify(dataCacheService).getUndeliveredMessages();
        verify(kafkaTemplate, times(2)).send(eq(branchToRegionalTopic), eq(branchId), any(DashboardMessage.class));
    }
} 