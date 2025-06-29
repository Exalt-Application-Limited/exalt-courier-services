package com.microecosystem.courier.driver.app.integration;

import com.microecosystem.courier.driver.app.dto.assignment.OfflineSyncResponseDTO;
import com.microecosystem.courier.driver.app.dto.assignment.SyncResultDTO;
import com.microecosystem.courier.driver.app.dto.sync.SyncStateDTO;
import com.microecosystem.courier.driver.app.service.sync.OfflineSyncService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for OfflineSyncController API endpoints.
 * 
 * @author Courier Services Migration Team
 * @version 1.0
 * @since 2025-05-25
 */
@DisplayName("Offline Sync Controller Integration Tests")
class OfflineSyncControllerIntegrationTest extends BaseIntegrationTest {

    @MockBean
    private OfflineSyncService offlineSyncService;

    @Test
    @WithMockUser(roles = "DRIVER")
    @DisplayName("Should successfully sync batch operations")
    void shouldSyncBatchOperations() throws Exception {
        // Given
        OfflineSyncResponseDTO mockResponse = createMockSyncResponse();
        when(offlineSyncService.syncBatch(anyString(), any()))
                .thenReturn(mockResponse);

        String requestBody = """
                {
                    "operations": [
                        {
                            "type": "UPDATE_TASK_STATUS",
                            "taskId": 1,
                            "status": "COMPLETED",
                            "timestamp": "2025-05-25T10:30:00"
                        },
                        {
                            "type": "UPDATE_LOCATION",
                            "lat": 40.7128,
                            "lng": -74.0060,
                            "timestamp": "2025-05-25T10:31:00"
                        }
                    ],
                    "deviceInfo": {
                        "deviceId": "test-device-123",
                        "appVersion": "1.0.0",
                        "osVersion": "Android 11"
                    }
                }
                """;

        // When & Then
        mockMvc.perform(post("/api/v1/sync/batch/{deviceId}", "test-device-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.deviceId").value("test-device-123"))
                .andExpect(jsonPath("$.syncResults").isArray())
                .andExpect(jsonPath("$.syncResults[0].operationId").exists())
                .andExpect(jsonPath("$.syncResults[0].success").value(true))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    @DisplayName("Should retrieve sync status for device")
    void shouldRetrieveSyncStatus() throws Exception {
        // Given
        SyncStateDTO mockSyncState = createMockSyncState();
        when(offlineSyncService.getSyncStatus(anyString()))
                .thenReturn(mockSyncState);

        // When & Then
        mockMvc.perform(get("/api/v1/sync/status/{deviceId}", "test-device-123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.deviceId").value("test-device-123"))
                .andExpect(jsonPath("$.lastSyncTime").exists())
                .andExpect(jsonPath("$.pendingOperations").value(5))
                .andExpect(jsonPath("$.totalOperations").value(25));
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    @DisplayName("Should clear pending operations for device")
    void shouldClearPendingOperations() throws Exception {
        // Given
        doNothing().when(offlineSyncService).clearPendingOperations(anyString());

        // When & Then
        mockMvc.perform(delete("/api/v1/sync/pending/{deviceId}", "test-device-123"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Pending operations cleared successfully"))
                .andExpect(jsonPath("$.deviceId").value("test-device-123"));
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    @DisplayName("Should retrieve sync statistics for device")
    void shouldRetrieveSyncStatistics() throws Exception {
        // Given
        when(offlineSyncService.getSyncStatistics(anyString()))
                .thenReturn(createMockSyncStatistics());

        // When & Then
        mockMvc.perform(get("/api/v1/sync/stats/{deviceId}", "test-device-123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.deviceId").value("test-device-123"))
                .andExpect(jsonPath("$.totalSyncs").value(100))
                .andExpect(jsonPath("$.successfulSyncs").value(95))
                .andExpect(jsonPath("$.failedSyncs").value(5))
                .andExpect(jsonPath("$.successRate").value(95.0));
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    @DisplayName("Should handle sync conflicts gracefully")
    void shouldHandleSyncConflicts() throws Exception {
        // Given
        OfflineSyncResponseDTO conflictResponse = createMockConflictResponse();
        when(offlineSyncService.syncBatch(anyString(), any()))
                .thenReturn(conflictResponse);

        String requestBody = """
                {
                    "operations": [
                        {
                            "type": "UPDATE_TASK_STATUS",
                            "taskId": 1,
                            "status": "COMPLETED",
                            "timestamp": "2025-05-25T10:30:00"
                        }
                    ]
                }
                """;

        // When & Then
        mockMvc.perform(post("/api/v1/sync/batch/{deviceId}", "test-device-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.syncResults[0].success").value(false))
                .andExpect(jsonPath("$.syncResults[0].errorMessage").value("Conflict: Task already completed"))
                .andExpect(jsonPath("$.syncResults[0].requiresManualResolution").value(true));
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    @DisplayName("Should validate sync request payload")
    void shouldValidateSyncRequest() throws Exception {
        // Given - Invalid request with missing required fields
        String invalidRequestBody = """
                {
                    "operations": []
                }
                """;

        // When & Then
        mockMvc.perform(post("/api/v1/sync/batch/{deviceId}", "test-device-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should require authentication for sync endpoints")
    void shouldRequireAuthentication() throws Exception {
        mockMvc.perform(post("/api/v1/sync/batch/test-device-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Helper method to create mock sync response
     */
    private OfflineSyncResponseDTO createMockSyncResponse() {
        OfflineSyncResponseDTO response = new OfflineSyncResponseDTO();
        response.setDeviceId("test-device-123");
        response.setTimestamp(LocalDateTime.now());
        
        SyncResultDTO result1 = new SyncResultDTO();
        result1.setOperationId("op-1");
        result1.setSuccess(true);
        result1.setMessage("Task status updated successfully");
        
        SyncResultDTO result2 = new SyncResultDTO();
        result2.setOperationId("op-2");
        result2.setSuccess(true);
        result2.setMessage("Location updated successfully");
        
        response.setSyncResults(Arrays.asList(result1, result2));
        return response;
    }

    /**
     * Helper method to create mock sync state
     */
    private SyncStateDTO createMockSyncState() {
        SyncStateDTO syncState = new SyncStateDTO();
        syncState.setDeviceId("test-device-123");
        syncState.setLastSyncTime(LocalDateTime.now().minusMinutes(30));
        syncState.setPendingOperations(5);
        syncState.setTotalOperations(25);
        syncState.setIsOnline(true);
        return syncState;
    }

    /**
     * Helper method to create mock sync statistics
     */
    private Object createMockSyncStatistics() {
        return new Object() {
            public final String deviceId = "test-device-123";
            public final int totalSyncs = 100;
            public final int successfulSyncs = 95;
            public final int failedSyncs = 5;
            public final double successRate = 95.0;
            public final LocalDateTime lastSuccessfulSync = LocalDateTime.now().minusMinutes(10);
        };
    }

    /**
     * Helper method to create mock conflict response
     */
    private OfflineSyncResponseDTO createMockConflictResponse() {
        OfflineSyncResponseDTO response = new OfflineSyncResponseDTO();
        response.setDeviceId("test-device-123");
        response.setTimestamp(LocalDateTime.now());
        
        SyncResultDTO conflictResult = new SyncResultDTO();
        conflictResult.setOperationId("op-1");
        conflictResult.setSuccess(false);
        conflictResult.setErrorMessage("Conflict: Task already completed");
        conflictResult.setRequiresManualResolution(true);
        
        response.setSyncResults(Arrays.asList(conflictResult));
        return response;
    }
}
