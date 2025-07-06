package com.gogidix.courierservices.customer.support.communication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Main application class for Customer Support Communication Service
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class CustomerSupportCommunicationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerSupportCommunicationServiceApplication.class, args);
    }
}