package com.exalt.courier.courier.branch.dashboard;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * Configuration for the Branch-level dashboard communication.
 * This manages the configuration for communicating with the Regional Admin Dashboard.
 */
@Configuration
public class BranchDashboardConfig {

    @Value("${dashboard.communication.topic.branch-to-regional:branch-to-regional-communication}")
    private String branchToRegionalTopic;

    @Value("${dashboard.communication.topic.regional-to-branch:regional-to-branch-communication}")
    private String regionalToBranchTopic;

    @Value("${dashboard.data.topic.branch-metrics:branch-metrics-data}")
    private String branchMetricsTopic;

    @Value("${dashboard.branch.id}")
    private String branchId;

    @Value("${dashboard.region.id}")
    private String regionId;

    @Bean
    public BranchDashboardCommunicationHandler branchDashboardCommunicationHandler(
            KafkaTemplate<String, Object> kafkaTemplate) {
        return new BranchDashboardCommunicationHandler(
                kafkaTemplate, 
                branchToRegionalTopic, 
                regionalToBranchTopic,
                branchId,
                regionId);
    }

    @Bean
    public BranchMetricsDataProvider branchMetricsDataProvider(
            KafkaTemplate<String, Object> kafkaTemplate) {
        return new BranchMetricsDataProvider(
                kafkaTemplate,
                branchMetricsTopic,
                branchId,
                regionId);
    }

    @Bean
    public BranchDataCacheService branchDataCacheService() {
        return new BranchDataCacheService();
    }
} 