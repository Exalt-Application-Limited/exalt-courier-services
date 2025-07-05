package com.gogidix.courier.customer.support.communication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application class for the Customer Support Communication Service.
 * 
 * This service provides integrated customer service and communication capabilities, including:
 * - Multi-channel customer support (live chat, email, phone, ticket system)
 * - Real-time communication via WebSocket connections
 * - Support ticket management and routing
 * - Knowledge base and FAQ management
 * - Customer feedback and rating collection
 * - Integration with notification service for customer communications
 * - Support agent management and performance tracking
 * - Escalation workflows and SLA monitoring
 * 
 * This service enhances customer experience for both individual and corporate customers using courier services
 */
@SpringBootApplication
@EnableFeignClients
@EnableScheduling
@EnableKafka
public class CustomerSupportCommunicationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerSupportCommunicationServiceApplication.class, args);
    }
}