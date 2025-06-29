package com.exalt.courier.courier.branch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main entry point for the Branch Courier Application.
 * This application provides branch-level courier operations and dashboard integration.
 */
@SpringBootApplication
@EnableKafka
@EnableScheduling
public class BranchCourierApplication {

    public static void main(String[] args) {
        SpringApplication.run(BranchCourierApplication.class, args);
    }
} 