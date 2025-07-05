package com.gogidix.courier.customer.support.communication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Response DTO for support ticket metrics and KPI data.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketMetricsResponse {

    // Time period for metrics
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
    private String periodDescription;

    // Volume metrics
    private Long totalTicketsCreated;
    private Long totalTicketsResolved;
    private Long totalTicketsClosed;
    private Long totalTicketsOpen;
    private Long totalTicketsOverdue;
    private Long totalTicketsEscalated;

    // Performance metrics
    private Double averageResponseTimeHours;
    private Double averageResolutionTimeHours;
    private Double averageCustomerSatisfactionRating;
    private Double firstCallResolutionRate;
    private Double slaComplianceRate;
    private Double escalationRate;

    // Agent metrics
    private Long totalActiveAgents;
    private Double averageTicketsPerAgent;
    private Double averageAgentUtilization;
    private Double averageAgentResponseTimeHours;

    // Category breakdown
    private Map<String, Long> ticketsByCategory;
    private Map<String, Double> resolutionTimeByCategory;
    private Map<String, Double> satisfactionByCategory;

    // Priority breakdown
    private Map<String, Long> ticketsByPriority;
    private Map<String, Double> resolutionTimeByPriority;
    private Map<String, Long> overdueTicketsByPriority;

    // Status breakdown
    private Map<String, Long> ticketsByStatus;
    private Long pendingCustomerResponse;
    private Long pendingAgentResponse;
    private Long awaitingEscalation;

    // Channel metrics
    private Map<String, Long> ticketsByChannel;
    private Map<String, Double> responseTimeByChannel;
    private Map<String, Double> satisfactionByChannel;

    // Time-based trends
    private List<DailyMetrics> dailyTrends;
    private List<HourlyMetrics> hourlyTrends;
    private List<WeeklyMetrics> weeklyTrends;

    // SLA metrics
    private SLAMetrics slaMetrics;

    // Quality metrics
    private QualityMetrics qualityMetrics;

    // Customer metrics
    private CustomerMetrics customerMetrics;

    // Top performers
    private List<AgentPerformanceMetric> topPerformingAgents;
    private List<String> mostCommonIssues;
    private List<String> longestResolutionCategories;

    // Predictive metrics
    private Double predictedVolumeNextWeek;
    private Double predictedAverageResponseTime;
    private List<String> riskFactors;

    /**
     * Nested class for daily metrics.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyMetrics {
        private LocalDateTime date;
        private Long ticketsCreated;
        private Long ticketsResolved;
        private Double averageResponseTimeHours;
        private Double customerSatisfactionAverage;
        private Double slaComplianceRate;
    }

    /**
     * Nested class for hourly metrics.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HourlyMetrics {
        private Integer hour; // 0-23
        private Long ticketsCreated;
        private Long ticketsResolved;
        private Double averageResponseTimeMinutes;
        private Double agentUtilization;
    }

    /**
     * Nested class for weekly metrics.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeeklyMetrics {
        private LocalDateTime weekStart;
        private LocalDateTime weekEnd;
        private Long ticketsCreated;
        private Long ticketsResolved;
        private Double weekOverWeekGrowth;
        private Double averageResolutionTimeHours;
    }

    /**
     * Nested class for SLA metrics.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SLAMetrics {
        private Double responseTimeSLAComplianceRate;
        private Double resolutionTimeSLAComplianceRate;
        private Long totalSLABreaches;
        private Long responseSLABreaches;
        private Long resolutionSLABreaches;
        private Double averageSLABreachTimeHours;
        private List<SLABreachDetail> recentBreaches;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class SLABreachDetail {
            private String ticketId;
            private String category;
            private String priority;
            private Double breachTimeHours;
            private String breachType; // RESPONSE, RESOLUTION
            private LocalDateTime breachTime;
        }
    }

    /**
     * Nested class for quality metrics.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QualityMetrics {
        private Double overallQualityScore;
        private Double averageFirstContactResolution;
        private Double customerReturnRate;
        private Double agentAccuracyRate;
        private Double escalationPreventionRate;
        private Long totalQualityReviews;
        private Double averageReviewScore;
        private List<String> commonQualityIssues;
    }

    /**
     * Nested class for customer metrics.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerMetrics {
        private Long totalUniqueCustomers;
        private Long newCustomers;
        private Long returningCustomers;
        private Double averageTicketsPerCustomer;
        private Double customerRetentionRate;
        private Double netPromoterScore;
        private Double customerEffortScore;
        private Map<String, Long> customersBySegment;
    }

    /**
     * Nested class for agent performance metrics.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AgentPerformanceMetric {
        private String agentId;
        private String agentName;
        private String team;
        private Long ticketsHandled;
        private Double averageResponseTimeHours;
        private Double averageResolutionTimeHours;
        private Double customerSatisfactionAverage;
        private Double utilizationRate;
        private Long escalationsCreated;
        private Double qualityScore;
        private Integer rank;
    }

    /**
     * Calculate overall performance score (0-100).
     */
    public Double calculateOverallPerformanceScore() {
        double score = 0.0;
        int factors = 0;

        if (slaComplianceRate != null) {
            score += slaComplianceRate * 30; // 30% weight
            factors++;
        }

        if (averageCustomerSatisfactionRating != null) {
            score += (averageCustomerSatisfactionRating / 5.0) * 25; // 25% weight
            factors++;
        }

        if (firstCallResolutionRate != null) {
            score += firstCallResolutionRate * 20; // 20% weight
            factors++;
        }

        if (escalationRate != null) {
            score += (1.0 - escalationRate) * 15; // 15% weight (lower escalation is better)
            factors++;
        }

        if (averageAgentUtilization != null) {
            score += averageAgentUtilization * 10; // 10% weight
            factors++;
        }

        return factors > 0 ? score / factors : 0.0;
    }

    /**
     * Get performance trend direction.
     */
    public String getPerformanceTrend() {
        // This would typically compare with previous period
        Double currentScore = calculateOverallPerformanceScore();
        
        if (currentScore >= 85) return "EXCELLENT";
        if (currentScore >= 75) return "GOOD";
        if (currentScore >= 65) return "AVERAGE";
        if (currentScore >= 50) return "NEEDS_IMPROVEMENT";
        return "POOR";
    }

    /**
     * Get key insights based on metrics.
     */
    public List<String> getKeyInsights() {
        return List.of(
            String.format("SLA compliance: %.1f%%", slaComplianceRate != null ? slaComplianceRate * 100 : 0),
            String.format("Average customer satisfaction: %.1f/5.0", averageCustomerSatisfactionRating != null ? averageCustomerSatisfactionRating : 0),
            String.format("First call resolution: %.1f%%", firstCallResolutionRate != null ? firstCallResolutionRate * 100 : 0),
            String.format("Average response time: %.1f hours", averageResponseTimeHours != null ? averageResponseTimeHours : 0),
            String.format("Total tickets handled: %d", totalTicketsCreated != null ? totalTicketsCreated : 0)
        );
    }

    /**
     * Check if metrics indicate performance issues.
     */
    public boolean hasPerformanceIssues() {
        return (slaComplianceRate != null && slaComplianceRate < 0.8) ||
               (averageCustomerSatisfactionRating != null && averageCustomerSatisfactionRating < 3.5) ||
               (escalationRate != null && escalationRate > 0.15) ||
               (totalTicketsOverdue != null && totalTicketsOverdue > 0);
    }

    /**
     * Get recommended actions based on metrics.
     */
    public List<String> getRecommendedActions() {
        List<String> actions = new java.util.ArrayList<>();
        
        if (slaComplianceRate != null && slaComplianceRate < 0.8) {
            actions.add("Review and optimize response time processes");
        }
        
        if (averageCustomerSatisfactionRating != null && averageCustomerSatisfactionRating < 3.5) {
            actions.add("Implement customer satisfaction improvement program");
        }
        
        if (escalationRate != null && escalationRate > 0.15) {
            actions.add("Provide additional training to reduce escalations");
        }
        
        if (totalTicketsOverdue != null && totalTicketsOverdue > 0) {
            actions.add("Address overdue tickets and workload distribution");
        }
        
        return actions;
    }
}