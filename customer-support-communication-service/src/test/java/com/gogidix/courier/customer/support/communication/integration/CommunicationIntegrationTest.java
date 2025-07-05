package com.gogidix.courier.customer.support.communication.integration;

import com.gogidix.courier.customer.support.communication.dto.*;
import com.gogidix.courier.customer.support.communication.enums.MessageType;
import com.gogidix.courier.customer.support.communication.enums.TicketCategory;
import com.gogidix.courier.customer.support.communication.enums.TicketPriority;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for Communication API endpoints.
 * 
 * Tests messaging, notifications, and communication workflows.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
public class CommunicationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private SupportTicketResponse testTicket;

    @BeforeEach
    public void setUp() throws Exception {
        // Create a test ticket for communication tests
        CreateTicketRequest ticketRequest = CreateTicketRequest.builder()
            .customerId("comm-test-customer")
            .customerEmail("commtest@example.com")
            .customerName("Communication Test User")
            .subject("Test ticket for communication")
            .description("This ticket is used for testing communication features")
            .category(TicketCategory.GENERAL_INQUIRY)
            .priority(TicketPriority.NORMAL)
            .source("TEST")
            .build();

        MvcResult createResult = mockMvc.perform(post("/api/v1/support/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ticketRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        testTicket = objectMapper.readValue(
            createResult.getResponse().getContentAsString(), SupportTicketResponse.class);
    }

    @Test
    public void testAddMessageToTicket() throws Exception {
        // Arrange
        CommunicationRequest messageRequest = CommunicationRequest.builder()
            .ticketId(testTicket.getId())
            .messageType(MessageType.CUSTOMER_INQUIRY)
            .content("I need additional information about my shipment status")
            .senderId("comm-test-customer")
            .senderName("Communication Test User")
            .senderEmail("commtest@example.com")
            .senderRole("CUSTOMER")
            .channel("PORTAL")
            .isInternal(false)
            .requiresResponse(true)
            .build();

        // Act & Assert
        mockMvc.perform(post("/api/v1/support/communications/tickets/{ticketId}/messages", testTicket.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(messageRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ticketId").value(testTicket.getId().toString()))
                .andExpect(jsonPath("$.content").value("Message added"))
                .andExpect(jsonPath("$.sentAt").exists());
    }

    @Test
    public void testGetTicketMessages() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/support/communications/tickets/{ticketId}/messages", testTicket.getId())
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.pageable").exists());
    }

    @Test
    public void testMarkMessageAsRead() throws Exception {
        // Arrange - First add a message
        UUID messageId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(patch("/api/v1/support/communications/messages/{messageId}/read", messageId)
                .param("readBy", "agent-001"))
                .andExpect(status().isOk());
    }

    @Test
    public void testEditMessage() throws Exception {
        // Arrange
        UUID messageId = UUID.randomUUID();
        String newContent = "Updated message content with additional details";
        String editReason = "Added clarification based on customer request";

        // Act & Assert
        mockMvc.perform(put("/api/v1/support/communications/messages/{messageId}", messageId)
                .param("newContent", newContent)
                .param("editReason", editReason)
                .param("editedBy", "agent-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(messageId.toString()))
                .andExpect(jsonPath("$.content").value(newContent));
    }

    @Test
    public void testSendCommunication() throws Exception {
        // Arrange
        CommunicationRequest commRequest = CommunicationRequest.builder()
            .ticketId(testTicket.getId())
            .messageType(MessageType.AGENT_RESPONSE)
            .content("Thank you for contacting us. We are investigating your shipment status.")
            .senderId("agent-001")
            .senderName("Support Agent")
            .senderRole("AGENT")
            .channel("EMAIL")
            .isInternal(false)
            .requiresResponse(false)
            .build();

        // Act & Assert
        mockMvc.perform(post("/api/v1/support/communications/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ticketId").value(testTicket.getId().toString()))
                .andExpect(jsonPath("$.messageType").value("AGENT_RESPONSE"))
                .andExpect(jsonPath("$.deliveryStatus").value("DELIVERED"))
                .andExpect(jsonPath("$.sentAt").exists());
    }

    @Test
    public void testCreateEscalation() throws Exception {
        // Arrange
        EscalationRequest escalationRequest = EscalationRequest.builder()
            .ticketId(testTicket.getId())
            .escalationType(com.gogidix.courier.customer.support.communication.enums.EscalationType.HIGH)
            .escalationReason("Customer requires urgent response due to business impact")
            .escalatedByAgentId("agent-001")
            .escalatedByAgentName("Primary Support Agent")
            .escalatedToTeam("senior-support-team")
            .markAsUrgent(true)
            .notifyCustomer(true)
            .customerNotificationMessage("Your ticket has been escalated for priority handling")
            .build();

        // Act & Assert
        mockMvc.perform(post("/api/v1/support/communications/escalations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(escalationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.escalatedTo").exists())
                .andExpect(jsonPath("$.escalatedAt").exists());
    }

    @Test
    public void testBulkTicketOperations() throws Exception {
        // Arrange - Create additional test tickets
        List<UUID> ticketIds = List.of(testTicket.getId());

        BulkTicketOperationRequest bulkRequest = BulkTicketOperationRequest.builder()
            .operationType(BulkTicketOperationRequest.BulkOperationType.UPDATE_STATUS)
            .ticketIds(ticketIds)
            .performedByAgentId("agent-supervisor")
            .performedByAgentName("Supervisor Agent")
            .operationReason("Bulk status update for processing efficiency")
            .newStatus(com.gogidix.courier.customer.support.communication.enums.TicketStatus.IN_PROGRESS)
            .statusChangeReason("Assigned to agent team for processing")
            .validateBeforeOperation(true)
            .notifyCustomers(true)
            .sendSummaryReport(true)
            .build();

        // Act & Assert
        mockMvc.perform(post("/api/v1/support/communications/bulk-operations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bulkRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").exists())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    public void testCustomerSatisfactionFeedback() throws Exception {
        // Arrange
        CustomerSatisfactionRequest satisfactionRequest = CustomerSatisfactionRequest.builder()
            .ticketId(testTicket.getId())
            .customerId("comm-test-customer")
            .overallRating(4)
            .responseTimeRating(5)
            .resolutionQualityRating(4)
            .agentProfessionalismRating(5)
            .communicationClarityRating(4)
            .easeOfProcessRating(4)
            .generalFeedback("The support was helpful and professional. Response time was excellent.")
            .whatWentWell("Quick response and clear communication from the agent")
            .whatCouldBeImproved("Could provide more detailed status updates")
            .wouldRecommendService(true)
            .issueFullyResolved(true)
            .agentWasHelpful(true)
            .processWasEasy(true)
            .wouldUseServiceAgain(true)
            .customerEffortScore(2) // Easy
            .netPromoterScore(9) // Promoter
            .primaryAgentId("agent-001")
            .allowFollowUpContact(false)
            .surveySource("EMAIL")
            .surveyCompleted(true)
            .build();

        // Act & Assert
        mockMvc.perform(post("/api/v1/support/communications/feedback")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(satisfactionRequest)))
                .andExpected(status().isCreated())
                .andExpect(jsonPath("$.ticketId").value(testTicket.getId().toString()))
                .andExpect(jsonPath("$.overallRating").value(4))
                .andExpect(jsonPath("$.sentiment").exists())
                .andExpect(jsonPath("$.npsCategory").exists())
                .andExpect(jsonPath("$.processed").value(true));
    }

    @Test
    public void testNotificationEndpoints() throws Exception {
        // Test customer notification
        mockMvc.perform(post("/api/v1/support/communications/notifications/customer")
                .param("ticketId", testTicket.getId().toString())
                .param("message", "Your ticket has been updated with new information")
                .param("channel", "EMAIL"))
                .andExpected(status().isOk());

        // Test agent notification
        mockMvc.perform(post("/api/v1/support/communications/notifications/agent")
                .param("agentId", "agent-001")
                .param("message", "New high priority ticket assigned")
                .param("ticketId", testTicket.getId().toString()))
                .andExpected(status().isOk());
    }

    @Test
    public void testCommunicationValidationErrors() throws Exception {
        // Test invalid communication request
        CommunicationRequest invalidRequest = CommunicationRequest.builder()
            .ticketId(null) // Missing ticket ID
            .messageType(null) // Missing message type
            .content("") // Empty content
            .build();

        mockMvc.perform(post("/api/v1/support/communications/tickets/{ticketId}/messages", UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpected(status().isBadRequest());
    }

    @Test
    public void testEscalationValidation() throws Exception {
        // Test invalid escalation request
        EscalationRequest invalidEscalation = EscalationRequest.builder()
            .ticketId(null) // Missing ticket ID
            .escalationType(null) // Missing escalation type
            .escalationReason("") // Empty reason
            .build();

        mockMvc.perform(post("/api/v1/support/communications/escalations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidEscalation)))
                .andExpected(status().isBadRequest());
    }

    @Test
    public void testBulkOperationValidation() throws Exception {
        // Test bulk operation with no ticket IDs
        BulkTicketOperationRequest invalidBulkRequest = BulkTicketOperationRequest.builder()
            .operationType(BulkTicketOperationRequest.BulkOperationType.UPDATE_STATUS)
            .ticketIds(List.of()) // Empty ticket list
            .performedByAgentId("agent-001")
            .build();

        mockMvc.perform(post("/api/v1/support/communications/bulk-operations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidBulkRequest)))
                .andExpected(status().isBadRequest());
    }

    @Test
    public void testCustomerSatisfactionValidation() throws Exception {
        // Test invalid satisfaction request
        CustomerSatisfactionRequest invalidSatisfaction = CustomerSatisfactionRequest.builder()
            .ticketId(null) // Missing ticket ID
            .customerId("") // Empty customer ID
            .overallRating(0) // Invalid rating
            .build();

        mockMvc.perform(post("/api/v1/support/communications/feedback")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidSatisfaction)))
                .andExpected(status().isBadRequest());
    }
}