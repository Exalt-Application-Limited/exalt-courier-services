package com.exalt.courier.regionaladmin.config.dashboard;

import com.microecosystem.courier.shared.dashboard.DashboardCommunicationService;
import com.microecosystem.courier.shared.dashboard.DashboardDataAggregationService;
import com.microecosystem.courier.shared.dashboard.DashboardLevel;
import com.microecosystem.courier.shared.dashboard.DashboardMessage;
import com.microecosystem.courier.shared.dashboard.MessageType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test class for RegionalDashboardCommunicationHandler.
 * Tests the integration between dashboard levels.
 */
@ExtendWith(MockitoExtension.class)
public class RegionalDashboardCommunicationIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(RegionalDashboardCommunicationIntegrationTest.class);
    
    @Mock
    private DashboardCommunicationService communicationService;
    
    private RegionalDashboardCommunicationHandler communicationHandler;
    
    @BeforeEach
    public void setup() {
        // Set up mock behavior for the communication service
        when(communicationService.getCurrentDashboardId()).thenReturn("test-regional-admin");
        when(communicationService.getCurrentDashboardLevel()).thenReturn(DashboardLevel.REGIONAL);
        
        // Mock the message handler registration to return a UUID
        when(communicationService.registerMessageHandler(any())).thenReturn(java.util.UUID.randomUUID().toString());
        
        // Mock the message sending to return a successful future
        when(communicationService.sendMessage(any())).thenReturn(CompletableFuture.completedFuture(true));
        
        // Mock the broadcast sending to return a map with successful results
        Map<String, Boolean> successMap = new HashMap<>();
        successMap.put("branch-1", true);
        successMap.put("branch-2", true);
        when(communicationService.broadcastMessage(any(), anyList(), any()))
                .thenReturn(CompletableFuture.completedFuture(successMap));
        
        // Create the handler
        communicationHandler = new RegionalDashboardCommunicationHandler(communicationService);
        
        // Initialize the handler
        communicationHandler.init();
    }
    
    @Test
    public void testHandlePolicyUpdateFromGlobal() {
        // Trigger the application ready event to register handlers
        communicationHandler.onApplicationReady();
        
        // Verify that handlers were registered
        verify(communicationService, times(8)).registerMessageHandler(any());
        
        // Create a policy update message from global HQ
        DashboardMessage policyUpdateMessage = new DashboardMessage.Builder()
                .withId(java.util.UUID.randomUUID().toString())
                .from(DashboardLevel.GLOBAL, "global-hq-admin")
                .to(DashboardLevel.REGIONAL, "test-regional-admin")
                .ofType(MessageType.POLICY_UPDATE)
                .withSubject("Updated Delivery Policy")
                .withContent("New delivery policy for express packages")
                .withMetadata(new HashMap<>())
                .build();
        
        // Capture and invoke the handler
        verify(communicationService, times(8)).registerMessageHandler(Mockito.argThat(handler -> {
            DashboardMessage response = handler.handleMessage(policyUpdateMessage);
            
            // For policy updates from global, we expect:
            // 1. An acknowledgment message back to global
            // 2. A broadcast to all branches
            if (response != null) {
                assertEquals("MESSAGE_ACK", response.getMessageType());
                assertEquals(DashboardLevel.REGIONAL, response.getSourceLevel());
                assertEquals("test-regional-admin", response.getSourceId());
                assertEquals(DashboardLevel.GLOBAL, response.getTargetLevel());
                assertEquals("global-hq-admin", response.getTargetId());
                assertTrue(response.getSubject().contains("Acknowledgment"));
                
                // Also verify the broadcast to branches
                verify(communicationService).broadcastMessage(
                        Mockito.argThat(msg -> 
                            MessageType.POLICY_UPDATE.equals(msg.getMessageType()) &&
                            DashboardLevel.REGIONAL.equals(msg.getSourceLevel()) &&
                            DashboardLevel.BRANCH.equals(msg.getTargetLevel())
                        ),
                        eq(Arrays.asList(DashboardLevel.BRANCH, DashboardLevel.LOCAL)),
                        eq(null)
                );
                
                return true;
            }
            return false;
        }));
    }
    
    @Test
    public void testHandleAlertFromBranch() {
        // Trigger the application ready event to register handlers
        communicationHandler.onApplicationReady();
        
        // Create an alert message from a branch
        Map<String, String> metadata = new HashMap<>();
        metadata.put("severity", "critical");
        
        DashboardMessage criticalAlertMessage = new DashboardMessage.Builder()
                .withId(java.util.UUID.randomUUID().toString())
                .from(DashboardLevel.BRANCH, "branch-office-123")
                .to(DashboardLevel.REGIONAL, "test-regional-admin")
                .ofType(MessageType.CRITICAL_ALERT)
                .withSubject("System Outage")
                .withContent("Critical: Branch system is down due to network failure")
                .withMetadata(metadata)
                .build();
        
        // Capture and invoke the handler
        verify(communicationService, times(8)).registerMessageHandler(Mockito.argThat(handler -> {
            DashboardMessage response = handler.handleMessage(criticalAlertMessage);
            
            // For critical alerts from branches, we expect:
            // 1. An acknowledgment message back to the branch
            // 2. A forwarded message to global HQ
            if (response != null) {
                assertEquals("MESSAGE_ACK", response.getMessageType());
                assertEquals(DashboardLevel.REGIONAL, response.getSourceLevel());
                assertEquals("test-regional-admin", response.getSourceId());
                assertEquals(DashboardLevel.BRANCH, response.getTargetLevel());
                assertEquals("branch-office-123", response.getTargetId());
                assertTrue(response.getSubject().contains("Acknowledgment"));
                
                // Also verify the forward to global HQ
                verify(communicationService).sendMessage(
                        Mockito.argThat(msg -> 
                            MessageType.CRITICAL_ALERT.equals(msg.getMessageType()) &&
                            DashboardLevel.REGIONAL.equals(msg.getSourceLevel()) &&
                            DashboardLevel.GLOBAL.equals(msg.getTargetLevel()) &&
                            msg.getSubject().contains("Regional Alert")
                        )
                );
                
                return true;
            }
            return false;
        }));
    }
    
    @Test
    public void testHandleRequestFromBranch() {
        // Trigger the application ready event to register handlers
        communicationHandler.onApplicationReady();
        
        // Create a request message from a branch that should be handled at regional level
        DashboardMessage branchRequestMessage = new DashboardMessage.Builder()
                .withId(java.util.UUID.randomUUID().toString())
                .from(DashboardLevel.BRANCH, "branch-office-123")
                .to(DashboardLevel.REGIONAL, "test-regional-admin")
                .ofType(MessageType.ACTION_REQUEST)
                .withSubject("Request Additional Resources")
                .withContent("Need additional driver allocation for peak hours")
                .withMetadata(new HashMap<>())
                .build();
        
        // Capture and invoke the handler
        verify(communicationService, times(8)).registerMessageHandler(Mockito.argThat(handler -> {
            DashboardMessage response = handler.handleMessage(branchRequestMessage);
            
            // For action requests from branches that can be handled regionally:
            // 1. An action response message back to the branch
            if (response != null) {
                assertEquals(MessageType.ACTION_RESPONSE, response.getMessageType());
                assertEquals(DashboardLevel.REGIONAL, response.getSourceLevel());
                assertEquals("test-regional-admin", response.getSourceId());
                assertEquals(DashboardLevel.BRANCH, response.getTargetLevel());
                assertEquals("branch-office-123", response.getTargetId());
                assertTrue(response.getSubject().contains("Response"));
                assertTrue(response.getContent().contains("Action completed by Regional Admin"));
                
                return true;
            }
            return false;
        }));
    }
    
    @Test
    public void testHandleRequestFromGlobal() {
        // Trigger the application ready event to register handlers
        communicationHandler.onApplicationReady();
        
        // Create a request message from global HQ
        DashboardMessage globalRequestMessage = new DashboardMessage.Builder()
                .withId(java.util.UUID.randomUUID().toString())
                .from(DashboardLevel.GLOBAL, "global-hq-admin")
                .to(DashboardLevel.REGIONAL, "test-regional-admin")
                .ofType(MessageType.ACTION_REQUEST)
                .withSubject("Generate Regional Report")
                .withContent("Please generate a detailed performance report for your region")
                .withMetadata(new HashMap<>())
                .build();
        
        // Capture and invoke the handler
        verify(communicationService, times(8)).registerMessageHandler(Mockito.argThat(handler -> {
            DashboardMessage response = handler.handleMessage(globalRequestMessage);
            
            // For action requests from global HQ:
            // 1. An action response message back to global HQ
            if (response != null) {
                assertEquals(MessageType.ACTION_RESPONSE, response.getMessageType());
                assertEquals(DashboardLevel.REGIONAL, response.getSourceLevel());
                assertEquals("test-regional-admin", response.getSourceId());
                assertEquals(DashboardLevel.GLOBAL, response.getTargetLevel());
                assertEquals("global-hq-admin", response.getTargetId());
                assertTrue(response.getSubject().contains("Response"));
                assertTrue(response.getContent().contains("Action completed by Regional Admin"));
                
                return true;
            }
            return false;
        }));
    }
}
