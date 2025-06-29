package com.microecosystem.courier.driver.app.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microecosystem.courier.driver.app.service.security.SecurityService;
import com.microecosystem.courier.driver.app.service.sync.OfflineSyncService;
import com.microecosystem.courier.driver.app.service.sync.OfflineSyncService.SyncOperation;
import com.microecosystem.courier.driver.app.service.sync.OfflineSyncService.SyncResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OfflineSyncController.class)
public class OfflineSyncControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OfflineSyncService offlineSyncService;

    @MockBean
    private SecurityService securityService;

    private SyncResult successResult;
    private SyncResult partialSuccessResult;
    private Map<String, Object> syncStats;

    @BeforeEach
    void setUp() {
        // Configure security service to allow access for testing
        when(securityService.isAuthorizedDevice(anyString())).thenReturn(true);

        // Setup mock responses
        successResult = new SyncResult();
        successResult.setDeviceId("device-001");
        successResult.setTotalOperations(10);
        successResult.setSuccessCount(10);
        successResult.setFailureCount(0);
        successResult.setSuccess(true);

        partialSuccessResult = new SyncResult();
        partialSuccessResult.setDeviceId("device-001");
        partialSuccessResult.setTotalOperations(10);
        partialSuccessResult.setSuccessCount(8);
        partialSuccessResult.setFailureCount(2);
        partialSuccessResult.setSuccess(true);
        List<SyncOperation> failedOps = new ArrayList<>();
        SyncOperation op1 = new SyncOperation();
        op1.setId(1);
        op1.setType("UPDATE");
        failedOps.add(op1);
        SyncOperation op2 = new SyncOperation();
        op2.setId(2);
        op2.setType("DELETE");
        failedOps.add(op2);
        partialSuccessResult.setFailedOperations(failedOps);

        syncStats = new HashMap<>();
        syncStats.put("deviceId", "device-001");
        syncStats.put("lastSyncTime", System.currentTimeMillis() - 3600000);
        syncStats.put("totalSyncOperations", 256);
        syncStats.put("pendingOperations", 3);
        syncStats.put("syncErrors", 1);

        // Configure service mocks
        when(offlineSyncService.processSyncBatch(eq("device-001"), anyList())).thenReturn(successResult);
        when(offlineSyncService.processSyncBatch(eq("device-002"), anyList())).thenReturn(partialSuccessResult);
        when(offlineSyncService.clearPendingOperations(anyString())).thenReturn(true);
        when(offlineSyncService.getSyncStats(anyString())).thenReturn(syncStats);
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    void syncBatch_ShouldReturnSuccess() throws Exception {
        List<SyncOperation> operations = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            SyncOperation op = new SyncOperation();
            op.setId(i);
            op.setType(i % 2 == 0 ? "CREATE" : "UPDATE");
            op.setEntityType("TASK");
            op.setEntityId("task-" + i);
            op.setTimestamp(System.currentTimeMillis());
            operations.add(op);
        }

        mockMvc.perform(post("/api/v1/sync/batch/device-001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(operations)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.deviceId").value("device-001"))
                .andExpect(jsonPath("$.totalOperations").value(10))
                .andExpect(jsonPath("$.successCount").value(10))
                .andExpect(jsonPath("$.failureCount").value(0))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    void syncBatch_WithPartialFailures_ShouldReturnPartialSuccess() throws Exception {
        List<SyncOperation> operations = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            SyncOperation op = new SyncOperation();
            op.setId(i);
            op.setType(i % 2 == 0 ? "CREATE" : "UPDATE");
            op.setEntityType("TASK");
            op.setEntityId("task-" + i);
            op.setTimestamp(System.currentTimeMillis());
            operations.add(op);
        }

        mockMvc.perform(post("/api/v1/sync/batch/device-002")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(operations)))
                .andExpect(status().is(207)) // Multi-Status
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.deviceId").value("device-001"))
                .andExpect(jsonPath("$.totalOperations").value(10))
                .andExpect(jsonPath("$.successCount").value(8))
                .andExpect(jsonPath("$.failureCount").value(2))
                .andExpect(jsonPath("$.failedOperations").isArray())
                .andExpect(jsonPath("$.failedOperations.length()").value(2))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    void getSyncStatus_ShouldReturnStatus() throws Exception {
        mockMvc.perform(get("/api/v1/sync/status/device-001")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.deviceId").value("device-001"))
                .andExpect(jsonPath("$.lastSyncTime").exists())
                .andExpect(jsonPath("$.pendingOperations").exists())
                .andExpect(jsonPath("$.syncStatus").exists());
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    void clearPendingOperations_ShouldReturnSuccess() throws Exception {
        mockMvc.perform(delete("/api/v1/sync/pending/device-001")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.deviceId").value("device-001"));
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    void getSyncStats_ShouldReturnStats() throws Exception {
        mockMvc.perform(get("/api/v1/sync/stats/device-001")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.deviceId").value("device-001"))
                .andExpect(jsonPath("$.lastSyncTime").exists())
                .andExpect(jsonPath("$.totalSyncOperations").exists())
                .andExpect(jsonPath("$.pendingOperations").exists())
                .andExpect(jsonPath("$.syncErrors").exists());
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    void syncBatchUnauthorizedDevice_ShouldReturnForbidden() throws Exception {
        // Override the security check to return false
        when(securityService.isAuthorizedDevice(anyString())).thenReturn(false);

        List<SyncOperation> operations = new ArrayList<>();
        SyncOperation op = new SyncOperation();
        op.setId(1);
        op.setType("CREATE");
        operations.add(op);

        mockMvc.perform(post("/api/v1/sync/batch/unauthorized-device")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(operations)))
                .andExpect(status().isForbidden());
    }
}
