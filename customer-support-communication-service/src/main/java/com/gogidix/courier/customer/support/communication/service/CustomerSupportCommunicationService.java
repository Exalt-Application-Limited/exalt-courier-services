package com.gogidix.courier.customer.support.communication.service;

import com.gogidix.courier.customer.support.communication.dto.*;
import com.gogidix.courier.customer.support.communication.model.SupportTicket;
import com.gogidix.courier.customer.support.communication.model.TicketStatus;
import com.gogidix.courier.customer.support.communication.model.TicketPriority;

import java.util.List;

/**
 * Service interface for Customer Support Communication operations.
 * 
 * This service provides comprehensive customer support functionality including:
 * - Multi-channel support (Live Chat, Email, Phone, Video Call)
 * - Ticket management with priority and escalation
 * - Knowledge base and FAQ management
 * - Real-time chat with agents and bots
 * - Support analytics and reporting
 * - Integration with CRM and notification systems
 * - Customer satisfaction tracking
 * - Multi-language support
 */
public interface CustomerSupportCommunicationService {

    // Ticket Management
    
    /**
     * Creates a new support ticket
     */
    SupportTicketResponse createSupportTicket(CreateSupportTicketRequest request);

    /**
     * Retrieves a support ticket by ID
     */
    SupportTicketResponse getSupportTicket(String ticketId);

    /**
     * Updates an existing support ticket
     */
    SupportTicketResponse updateSupportTicket(String ticketId, UpdateSupportTicketRequest request);

    /**
     * Assigns a ticket to an agent
     */
    void assignTicketToAgent(String ticketId, String agentId);

    /**
     * Escalates a ticket to higher priority or different department
     */
    TicketEscalationResponse escalateTicket(String ticketId, TicketEscalationRequest request);

    /**
     * Closes a support ticket
     */
    void closeTicket(String ticketId, CloseTicketRequest request);

    /**
     * Reopens a closed ticket
     */
    void reopenTicket(String ticketId, String reason);

    /**
     * Gets all tickets for a customer with filtering
     */
    List<SupportTicketResponse> getCustomerTickets(String customerId, TicketFilterRequest filter);

    /**
     * Gets all tickets assigned to an agent
     */
    List<SupportTicketResponse> getAgentTickets(String agentId, TicketFilterRequest filter);

    // Live Chat Management
    
    /**
     * Initiates a new live chat session
     */
    LiveChatSessionResponse initiateLiveChat(InitiateLiveChatRequest request);

    /**
     * Sends a message in live chat
     */
    ChatMessageResponse sendChatMessage(String sessionId, SendChatMessageRequest request);

    /**
     * Gets chat history for a session
     */
    List<ChatMessageResponse> getChatHistory(String sessionId, int page, int size);

    /**
     * Transfers chat to another agent
     */
    void transferChat(String sessionId, String newAgentId, String reason);

    /**
     * Ends a live chat session
     */
    void endChatSession(String sessionId, EndChatSessionRequest request);

    /**
     * Gets active chat sessions for an agent
     */
    List<LiveChatSessionResponse> getActiveChatSessions(String agentId);

    // Knowledge Base Management
    
    /**
     * Searches the knowledge base
     */
    List<KnowledgeBaseArticleResponse> searchKnowledgeBase(KnowledgeBaseSearchRequest request);

    /**
     * Gets FAQ categories
     */
    List<FaqCategoryResponse> getFaqCategories();

    /**
     * Gets FAQs by category
     */
    List<FaqResponse> getFaqsByCategory(String categoryId);

    /**
     * Creates a new knowledge base article
     */
    KnowledgeBaseArticleResponse createKnowledgeBaseArticle(CreateKnowledgeBaseArticleRequest request);

    /**
     * Updates a knowledge base article
     */
    KnowledgeBaseArticleResponse updateKnowledgeBaseArticle(String articleId, UpdateKnowledgeBaseArticleRequest request);

    // Communication Channels
    
    /**
     * Sends an email to customer
     */
    EmailResponse sendEmail(SendEmailRequest request);

    /**
     * Schedules a phone call
     */
    PhoneCallResponse schedulePhoneCall(SchedulePhoneCallRequest request);

    /**
     * Initiates a video call
     */
    VideoCallResponse initiateVideoCall(InitiateVideoCallRequest request);

    /**
     * Sends SMS notification
     */
    SmsResponse sendSms(SendSmsRequest request);

    // Analytics and Reporting
    
    /**
     * Gets support metrics for a date range
     */
    SupportMetricsResponse getSupportMetrics(SupportMetricsRequest request);

    /**
     * Gets agent performance metrics
     */
    AgentPerformanceResponse getAgentPerformance(String agentId, PerformanceMetricsRequest request);

    /**
     * Gets customer satisfaction ratings
     */
    CustomerSatisfactionResponse getCustomerSatisfaction(CustomerSatisfactionRequest request);

    /**
     * Generates support reports
     */
    SupportReportResponse generateSupportReport(SupportReportRequest request);

    // Customer Feedback
    
    /**
     * Submits customer feedback for a ticket
     */
    void submitCustomerFeedback(String ticketId, CustomerFeedbackRequest request);

    /**
     * Gets feedback summary for tickets
     */
    FeedbackSummaryResponse getFeedbackSummary(FeedbackSummaryRequest request);

    // Integration Services
    
    /**
     * Syncs customer data with CRM
     */
    void syncWithCrm(String customerId);

    /**
     * Creates a follow-up task
     */
    FollowUpTaskResponse createFollowUpTask(CreateFollowUpTaskRequest request);

    /**
     * Gets pending follow-up tasks
     */
    List<FollowUpTaskResponse> getPendingFollowUpTasks(String agentId);

    // Notification Management
    
    /**
     * Sends real-time notification to customer
     */
    void sendNotificationToCustomer(String customerId, CustomerNotificationRequest request);

    /**
     * Sends notification to agent
     */
    void sendNotificationToAgent(String agentId, AgentNotificationRequest request);

    /**
     * Updates customer communication preferences
     */
    void updateCommunicationPreferences(String customerId, CommunicationPreferencesRequest request);

    // Multi-language Support
    
    /**
     * Translates message to customer's preferred language
     */
    TranslationResponse translateMessage(TranslationRequest request);

    /**
     * Gets supported languages
     */
    List<LanguageResponse> getSupportedLanguages();

    // Bot Integration
    
    /**
     * Processes message through chatbot
     */
    BotResponse processBotMessage(BotMessageRequest request);

    /**
     * Transfers conversation from bot to human agent
     */
    void transferToHumanAgent(String sessionId, String reason);

    // Quality Assurance
    
    /**
     * Reviews ticket for quality assurance
     */
    QualityReviewResponse reviewTicketQuality(String ticketId, QualityReviewRequest request);

    /**
     * Gets tickets requiring quality review
     */
    List<SupportTicketResponse> getTicketsForQualityReview(QualityReviewFilterRequest filter);

    // Workflow Management
    
    /**
     * Updates ticket status with workflow validation
     */
    void updateTicketStatus(SupportTicket ticket, TicketStatus newStatus, String reason, String updatedBy);

    /**
     * Validates status transition
     */
    boolean validateStatusTransition(TicketStatus currentStatus, TicketStatus newStatus);

    /**
     * Calculates ticket priority based on rules
     */
    TicketPriority calculateTicketPriority(CalculatePriorityRequest request);

    /**
     * Auto-assigns ticket to available agent
     */
    String autoAssignTicket(String ticketId, AutoAssignmentRequest request);
}