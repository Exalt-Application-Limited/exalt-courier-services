package com.microecosystem.courier.driver.app.controller.api;

import com.microecosystem.courier.driver.app.service.security.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DataSyncController.class)
public class DataSyncControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SecurityService securityService;

    @BeforeEach
    void setUp() {
        // Configure security service to allow access for testing
        when(securityService.isCurrentDriver(anyString())).thenReturn(true);
        when(securityService.canAccessAssignment(anyString())).thenReturn(true);
        when(securityService.canUpdateTask(anyString())).thenReturn(true);
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    void getAssignments_ShouldReturnAssignmentsList() throws Exception {
        mockMvc.perform(get("/api/v1/data/assignments/driver-123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.driverId").value("driver-123"))
                .andExpect(jsonPath("$.assignments").isArray());
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    void getAssignmentsWithIncludeCompleted_ShouldReturnAllAssignments() throws Exception {
        mockMvc.perform(get("/api/v1/data/assignments/driver-123?includeCompleted=true")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.driverId").value("driver-123"))
                .andExpect(jsonPath("$.assignments").isArray());
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    void getAssignmentDetail_ShouldReturnAssignmentDetails() throws Exception {
        mockMvc.perform(get("/api/v1/data/assignments/detail/A1-driver-123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("A1-driver-123"));
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    void updateTaskStatus_ShouldUpdateAndReturnSuccess() throws Exception {
        String requestBody = "{\"status\": \"COMPLETED\"}";

        mockMvc.perform(put("/api/v1/data/tasks/T1-A1-driver-123/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.taskId").value("T1-A1-driver-123"))
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    void getConfiguration_ShouldReturnConfigSettings() throws Exception {
        mockMvc.perform(get("/api/v1/data/config/device-001")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.deviceId").value("device-001"))
                .andExpect(jsonPath("$.config").exists())
                .andExpect(jsonPath("$.config.syncIntervalMinutes").exists())
                .andExpect(jsonPath("$.config.features").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAssignmentsWithWrongRole_ShouldBeForbidden() throws Exception {
        // Override the security check to return false
        when(securityService.isCurrentDriver(anyString())).thenReturn(false);
        
        mockMvc.perform(get("/api/v1/data/assignments/driver-123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
