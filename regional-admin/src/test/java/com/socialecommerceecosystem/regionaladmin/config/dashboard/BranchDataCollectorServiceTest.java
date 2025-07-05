package com.gogidix.courier.regionaladmin.config.dashboard;

import com.microecosystem.courier.shared.dashboard.DashboardCommunicationService;
import com.microecosystem.courier.shared.dashboard.DashboardDataAggregationService;
import com.microecosystem.courier.shared.dashboard.DashboardLevel;
import com.microecosystem.courier.shared.dashboard.DashboardMessage;
import com.microecosystem.courier.shared.dashboard.DataType;
import com.microecosystem.courier.shared.dashboard.MessageType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test class for BranchDataCollectorService.
 * Tests active data collection from branch/courier level.
 */
@ExtendWith(MockitoExtension.class)
public class BranchDataCollectorServiceTest {

    @Mock
    private DashboardCommunicationService communicationService;
    
    @Mock
    private DashboardDataAggregationService aggregationService;
    
    @Captor
    private ArgumentCaptor<DashboardMessage> messageCaptor;
    
    private BranchDataCollectorService dataCollectorService;
    
    @BeforeEach
    public void setup() {
        // Set up mock behavior for the communication service
        when(communicationService.getCurrentDashboardId()).thenReturn("test-regional-admin");
        
        // Mock the message sending to return a successful future
        when(communicationService.sendMessage(any())).thenReturn(CompletableFuture.completedFuture(true));
        
        // Mock the broadcast sending to return a map with successful results
        Map<String, Boolean> successMap = new HashMap<>();
        successMap.put("branch-1", true);
        successMap.put("branch-2", true);
        when(communicationService.broadcastMessage(any(), anyList(), any()))
                .thenReturn(CompletableFuture.completedFuture(successMap));
        
        // Create the service
        dataCollectorService = new BranchDataCollectorService(communicationService, aggregationService);
        
        // Initialize the service
        dataCollectorService.init();
    }
    
    @Test
    public void testRequestDataFromBranches() {
        // Call the method to request data from branches
        dataCollectorService.requestDataFromBranches(DataType.DELIVERY_METRICS);
        
        // Verify that a broadcast message was sent
        verify(communicationService).broadcastMessage(
                messageCaptor.capture(),
                eq(Arrays.asList(DashboardLevel.BRANCH, DashboardLevel.LOCAL)),
                eq(null)
        );
        
        // Verify the message properties
        DashboardMessage message = messageCaptor.getValue();
        assertNotNull(message);
        assertEquals(DashboardLevel.REGIONAL, message.getSourceLevel());
        assertEquals("test-regional-admin", message.getSourceId());
        assertEquals(DashboardLevel.BRANCH, message.getTargetLevel());
        assertEquals(null, message.getTargetId()); // Broadcast to all branches
        assertEquals(MessageType.DATA_REQUEST, message.getMessageType());
        assertTrue(message.getSubject().contains("Request for"));
        assertTrue(message.getSubject().contains("DELIVERY_METRICS"));
        
        // Verify the message metadata
        Map<String, String> metadata = message.getMetadata();
        assertEquals(DataType.DELIVERY_METRICS, metadata.get("dataType"));
        assertNotNull(metadata.get("requestId"));
        assertEquals("normal", metadata.get("priority"));
    }
    
    @Test
    public void testRequestDataFromSpecificBranch() {
        String branchId = "branch-office-123";
        
        // Call the method to request data from a specific branch
        dataCollectorService.requestDataFromBranch(branchId, DataType.SYSTEM_HEALTH);
        
        // Verify that a message was sent
        verify(communicationService).sendMessage(messageCaptor.capture());
        
        // Verify the message properties
        DashboardMessage message = messageCaptor.getValue();
        assertNotNull(message);
        assertEquals(DashboardLevel.REGIONAL, message.getSourceLevel());
        assertEquals("test-regional-admin", message.getSourceId());
        assertEquals(DashboardLevel.BRANCH, message.getTargetLevel());
        assertEquals(branchId, message.getTargetId());
        assertEquals(MessageType.DATA_REQUEST, message.getMessageType());
        assertTrue(message.getSubject().contains("Request for"));
        assertTrue(message.getSubject().contains("SYSTEM_HEALTH"));
        
        // Verify the message metadata
        Map<String, String> metadata = message.getMetadata();
        assertEquals(DataType.SYSTEM_HEALTH, metadata.get("dataType"));
        assertNotNull(metadata.get("requestId"));
        assertEquals("high", metadata.get("priority"));
    }
    
    @Test
    public void testCheckBranchStatus() {
        String branchId = "branch-office-123";
        
        // Call the method to check status of a specific branch
        dataCollectorService.checkBranchStatus(branchId);
        
        // Verify that a message was sent
        verify(communicationService).sendMessage(messageCaptor.capture());
        
        // Verify the message properties
        DashboardMessage message = messageCaptor.getValue();
        assertNotNull(message);
        assertEquals(DashboardLevel.REGIONAL, message.getSourceLevel());
        assertEquals("test-regional-admin", message.getSourceId());
        assertEquals(DashboardLevel.BRANCH, message.getTargetLevel());
        assertEquals(branchId, message.getTargetId());
        assertEquals(MessageType.ACTION_REQUEST, message.getMessageType());
        assertEquals("Status check request", message.getSubject());
        
        // Verify the message metadata
        Map<String, String> metadata = message.getMetadata();
        assertNotNull(metadata.get("requestId"));
        assertEquals("high", metadata.get("priority"));
    }
    
    @Test
    public void testForceSyncAllBranchData() {
        // Call the method to force synchronization of all branch data
        dataCollectorService.forceSyncAllBranchData();
        
        // Verify that a broadcast message was sent
        verify(communicationService).broadcastMessage(
                messageCaptor.capture(),
                eq(Arrays.asList(DashboardLevel.BRANCH, DashboardLevel.LOCAL)),
                eq(null)
        );
        
        // Verify the message properties
        DashboardMessage message = messageCaptor.getValue();
        assertNotNull(message);
        assertEquals(DashboardLevel.REGIONAL, message.getSourceLevel());
        assertEquals("test-regional-admin", message.getSourceId());
        assertEquals(DashboardLevel.BRANCH, message.getTargetLevel());
        assertEquals(null, message.getTargetId()); // Broadcast to all branches
        assertEquals(MessageType.SYNC_REQUEST, message.getMessageType());
        assertEquals("Full data synchronization request", message.getSubject());
        
        // Verify the message metadata
        Map<String, String> metadata = message.getMetadata();
        assertNotNull(metadata.get("requestId"));
        assertEquals("high", metadata.get("priority"));
        assertEquals("true", metadata.get("fullSync"));
    }
}
