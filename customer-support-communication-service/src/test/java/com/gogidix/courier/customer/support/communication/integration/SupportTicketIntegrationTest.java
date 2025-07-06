package com.gogidix.courier.customer.support.communication.integration;

import com.gogidix.courier.customer.support.communication.dto.CreateTicketRequest;
import com.gogidix.courier.customer.support.communication.dto.SupportTicketResponse;
import com.gogidix.courier.customer.support.communication.enums.TicketCategory;
import com.gogidix.courier.customer.support.communication.enums.TicketPriority;
import com.gogidix.courier.customer.support.communication.enums.TicketStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for Support Ticket API endpoints.
 * 
 * Tests the complete flow from HTTP requests through controllers,
 * services, and repositories.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
public class SupportTicketIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateTicketWorkflow() throws Exception {
        // Arrange
        CreateTicketRequest request = CreateTicketRequest.builder()
            .customerId("customer-001")
            .customerEmail("test@example.com")
            .customerName("John Doe")
            .subject("Package delivery issue")
            .description("My package was not delivered to the correct address")
            .category(TicketCategory.DELIVERY_ISSUES)
            .priority(TicketPriority.HIGH)
            .isUrgent(true)
            .source("WEB_PORTAL")
            .build();

        // Act & Assert - Create ticket
        MvcResult createResult = mockMvc.perform(post("/api/v1/support/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerId").value("customer-001"))
                .andExpect(jsonPath("$.subject").value("Package delivery issue"))
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andExpected(jsonPath("$.category").value("DELIVERY_ISSUES"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.isUrgent").value(true))
                .andReturn();

        String responseJson = createResult.getResponse().getContentAsString();
        SupportTicketResponse createdTicket = objectMapper.readValue(responseJson, SupportTicketResponse.class);
        
        assertNotNull(createdTicket.getId());
        assertNotNull(createdTicket.getTicketReferenceId());
        assertNotNull(createdTicket.getCreatedAt());
        assertTrue(createdTicket.getTicketReferenceId().startsWith("TKT-"));

        // Act & Assert - Get ticket by ID
        mockMvc.perform(get("/api/v1/support/tickets/{ticketId}", createdTicket.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdTicket.getId().toString()))
                .andExpect(jsonPath("$.customerId").value("customer-001"))
                .andExpect(jsonPath("$.subject").value("Package delivery issue"));

        // Act & Assert - Get ticket by reference
        mockMvc.perform(get("/api/v1/support/tickets/reference/{referenceId}", 
                        createdTicket.getTicketReferenceId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ticketReferenceId").value(createdTicket.getTicketReferenceId()));

        // Act & Assert - Change ticket status
        mockMvc.perform(patch("/api/v1/support/tickets/{ticketId}/status", createdTicket.getId())
                .param("status", "IN_PROGRESS")
                .param("reason", "Agent started working on ticket")
                .param("changedBy", "agent-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));

        // Act & Assert - Assign ticket
        mockMvc.perform(patch("/api/v1/support/tickets/{ticketId}/assign", createdTicket.getId())
                .param("agentId", "agent-001")
                .param("assignedBy", "supervisor-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assignedAgentId").value("agent-001"));

        // Act & Assert - Get customer tickets
        mockMvc.perform(get("/api/v1/support/tickets/customer/{customerId}", "customer-001")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].customerId").value("customer-001"));
    }

    @Test
    public void testTicketEscalationWorkflow() throws Exception {
        // Arrange - Create a ticket first
        CreateTicketRequest request = CreateTicketRequest.builder()
            .customerId("customer-002")
            .customerEmail("urgent@example.com")
            .customerName("Jane Smith")
            .subject("Critical service outage")
            .description("Unable to access courier service application")
            .category(TicketCategory.SERVICE_DISRUPTION)
            .priority(TicketPriority.CRITICAL)
            .isUrgent(true)
            .build();

        MvcResult createResult = mockMvc.perform(post("/api/v1/support/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        SupportTicketResponse ticket = objectMapper.readValue(
            createResult.getResponse().getContentAsString(), SupportTicketResponse.class);

        // Act & Assert - Escalate ticket
        mockMvc.perform(patch("/api/v1/support/tickets/{ticketId}/escalate", ticket.getId())
                .param("escalatedTo", "senior-support-team")
                .param("reason", "Critical issue requires senior attention")
                .param("escalatedBy", "agent-002"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.escalatedTo").value("senior-support-team"))
                .andExpect(jsonPath("$.escalatedAt").exists());
    }

    @Test
    public void testTicketClosureWorkflow() throws Exception {
        // Arrange - Create a ticket
        CreateTicketRequest request = CreateTicketRequest.builder()
            .customerId("customer-003")
            .customerEmail("resolved@example.com")
            .subject("Billing inquiry")
            .description("Question about invoice charges")
            .category(TicketCategory.BILLING_INQUIRY)
            .priority(TicketPriority.NORMAL)
            .build();

        MvcResult createResult = mockMvc.perform(post("/api/v1/support/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        SupportTicketResponse ticket = objectMapper.readValue(
            createResult.getResponse().getContentAsString(), SupportTicketResponse.class);

        // Act & Assert - Close ticket
        mockMvc.perform(patch("/api/v1/support/tickets/{ticketId}/close", ticket.getId())
                .param("resolutionNotes", "Billing inquiry resolved. Customer charges explained and corrected.")
                .param("closedBy", "agent-003"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RESOLVED"))
                .andExpect(jsonPath("$.resolutionNotes").value("Billing inquiry resolved. Customer charges explained and corrected."))
                .andExpect(jsonPath("$.resolvedAt").exists());

        // Act & Assert - Reopen ticket
        mockMvc.perform(patch("/api/v1/support/tickets/{ticketId}/reopen", ticket.getId())
                .param("reason", "Customer reported issue not fully resolved")
                .param("reopenedBy", "customer-003"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andExpect(jsonPath("$.resolvedAt").doesNotExist());
    }

    @Test
    public void testTicketValidationErrors() throws Exception {
        // Test invalid ticket creation
        CreateTicketRequest invalidRequest = CreateTicketRequest.builder()
            .customerId("") // Empty customer ID should fail validation
            .customerEmail("invalid-email") // Invalid email format
            .subject("") // Empty subject should fail validation
            .description("") // Empty description should fail validation
            .build();

        mockMvc.perform(post("/api/v1/support/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testTicketNotFound() throws Exception {
        // Test getting non-existent ticket
        mockMvc.perform(get("/api/v1/support/tickets/{ticketId}", "00000000-0000-0000-0000-000000000000"))
                .andExpect(status().isNotFound());

        // Test getting ticket by non-existent reference
        mockMvc.perform(get("/api/v1/support/tickets/reference/{referenceId}", "NON-EXISTENT-REF"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testAgentOperations() throws Exception {
        // Test getting agent tickets (should return empty for non-existent agent)
        mockMvc.perform(get("/api/v1/support/tickets/agent/{agentId}", "non-existent-agent")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());

        // Test getting unassigned tickets
        mockMvc.perform(get("/api/v1/support/tickets/unassigned"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    public void testMonitoringEndpoints() throws Exception {
        // Test overdue tickets
        mockMvc.perform(get("/api/v1/support/tickets/overdue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        // Test tickets requiring first response
        mockMvc.perform(get("/api/v1/support/tickets/pending-first-response"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        // Test tickets needing escalation
        mockMvc.perform(get("/api/v1/support/tickets/needs-escalation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    public void testUtilityEndpoints() throws Exception {
        // Test ticket reference ID generation
        mockMvc.perform(get("/api/v1/support/tickets/generate-reference"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.matchesPattern("TKT-\\d{8}-\\d{4}")));
    }
}