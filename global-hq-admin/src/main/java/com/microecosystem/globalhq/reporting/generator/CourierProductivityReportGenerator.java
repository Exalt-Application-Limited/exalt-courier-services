package com.microecosystem.globalhq.reporting.generator;

import com.microecosystem.globalhq.reporting.model.AdvancedReport;
import com.microecosystem.globalhq.reporting.model.ChartData;
import com.microecosystem.globalhq.reporting.model.ReportMetric;
import com.microecosystem.globalhq.reporting.model.ReportSection;
import com.microecosystem.globalhq.repository.CourierRepository;
import com.microecosystem.globalhq.repository.DeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Generator for Courier Productivity Reports that analyze the
 * performance metrics related to courier efficiency and productivity.
 */
@Component
public class CourierProductivityReportGenerator implements ReportGenerator {

    private final CourierRepository courierRepository;
    private final DeliveryRepository deliveryRepository;

    @Autowired
    public CourierProductivityReportGenerator(
            CourierRepository courierRepository,
            DeliveryRepository deliveryRepository) {
        this.courierRepository = courierRepository;
        this.deliveryRepository = deliveryRepository;
    }

    @Override
    public AdvancedReport generateReport(LocalDate startDate, LocalDate endDate, String branchId) {
        AdvancedReport report = new AdvancedReport();
        report.setTitle("Courier Productivity Report");
        report.setGeneratedDate(LocalDateTime.now());
        report.setStartDate(startDate);
        report.setEndDate(endDate);
        report.setBranchId(branchId);
        report.setReportType("COURIER_PRODUCTIVITY");

        // Add report sections
        report.setSections(Arrays.asList(
                generateOverviewSection(startDate, endDate, branchId),
                generateDeliveryVolumeSection(startDate, endDate, branchId),
                generateTimeEfficiencySection(startDate, endDate, branchId),
                generateQualityMetricsSection(startDate, endDate, branchId),
                generateTopPerformersSection(startDate, endDate, branchId),
                generateRecommendationsSection(startDate, endDate, branchId)
        ));

        return report;
    }

    private ReportSection generateOverviewSection(LocalDate startDate, LocalDate endDate, String branchId) {
        ReportSection section = new ReportSection();
        section.setTitle("Courier Productivity Overview");
        section.setOrder(1);

        // Calculate key metrics
        double avgDeliveriesPerCourier = calculateAverageDeliveriesPerCourier(startDate, endDate, branchId);
        double avgDeliveryTimePerCourier = calculateAverageDeliveryTimePerCourier(startDate, endDate, branchId);
        double avgRatingPerCourier = calculateAverageRatingPerCourier(startDate, endDate, branchId);
        double courierUtilizationRate = calculateCourierUtilizationRate(startDate, endDate, branchId);

        // Add metrics to section
        List<ReportMetric> metrics = new ArrayList<>();
        metrics.add(new ReportMetric("Average Deliveries Per Courier", String.format("%.1f deliveries", avgDeliveriesPerCourier),
                calculatePercentChange(avgDeliveriesPerCourier, getPreviousPeriodAvgDeliveriesPerCourier(startDate, endDate, branchId))));
        metrics.add(new ReportMetric("Average Delivery Time Per Courier", String.format("%.2f minutes", avgDeliveryTimePerCourier),
                calculatePercentChange(avgDeliveryTimePerCourier, getPreviousPeriodAvgDeliveryTimePerCourier(startDate, endDate, branchId))));
        metrics.add(new ReportMetric("Average Customer Rating", String.format("%.1f / 5.0", avgRatingPerCourier),
                calculatePercentChange(avgRatingPerCourier, getPreviousPeriodAvgRatingPerCourier(startDate, endDate, branchId))));
        metrics.add(new ReportMetric("Courier Utilization Rate", String.format("%.1f%%", courierUtilizationRate),
                calculatePercentChange(courierUtilizationRate, getPreviousPeriodCourierUtilizationRate(startDate, endDate, branchId))));

        section.setMetrics(metrics);

        // Add summary text
        StringBuilder summary = new StringBuilder();
        summary.append("This section provides an overview of key courier productivity metrics for the selected period. ");

        if (avgDeliveriesPerCourier > getPreviousPeriodAvgDeliveriesPerCourier(startDate, endDate, branchId)) {
            summary.append("Courier delivery volume has increased compared to the previous period. ");
        } else {
            summary.append("Courier delivery volume has decreased compared to the previous period. ");
        }

        if (avgRatingPerCourier > getPreviousPeriodAvgRatingPerCourier(startDate, endDate, branchId)) {
            summary.append("Customer satisfaction with courier service has improved, as indicated by higher ratings. ");
        } else if (avgRatingPerCourier < getPreviousPeriodAvgRatingPerCourier(startDate, endDate, branchId)) {
            summary.append("Customer satisfaction with courier service has declined, which may require attention. ");
        } else {
            summary.append("Customer satisfaction with courier service has remained stable. ");
        }

        section.setSummary(summary.toString());

        return section;
    }

    private ReportSection generateDeliveryVolumeSection(LocalDate startDate, LocalDate endDate, String branchId) {
        ReportSection section = new ReportSection();
        section.setTitle("Delivery Volume Analysis");
        section.setOrder(2);

        // Prepare data for delivery volume chart
        ChartData deliveryVolumeChart = new ChartData();
        deliveryVolumeChart.setChartType("BAR");
        deliveryVolumeChart.setTitle("Daily Delivery Volume");
        deliveryVolumeChart.setXAxisLabel("Date");
        deliveryVolumeChart.setYAxisLabel("Number of Deliveries");

        // Generate data for each day in the period
        Map<String, Object> chartData = generateDailyDeliveryVolumeData(startDate, endDate, branchId);
        deliveryVolumeChart.setData(chartData);

        // Add chart to section
        section.setCharts(Collections.singletonList(deliveryVolumeChart));

        // Add volume analysis insights
        section.setSummary("This section analyzes the delivery volume trends over the selected period. " +
                "The chart shows the number of deliveries completed each day, helping identify peak delivery days " +
                "and potential capacity planning opportunities. High volume days may require additional staffing, " +
                "while low volume days might indicate opportunities for promotional activities.");

        // Add volume metrics
        List<ReportMetric> metrics = new ArrayList<>();
        metrics.add(new ReportMetric("Highest Volume Day",
                identifyHighestVolumeDay(startDate, endDate, branchId), 0.0));
        metrics.add(new ReportMetric("Lowest Volume Day",
                identifyLowestVolumeDay(startDate, endDate, branchId), 0.0));
        metrics.add(new ReportMetric("Average Daily Volume",
                String.format("%.1f deliveries", calculateAverageDailyVolume(startDate, endDate, branchId)), 0.0));
        metrics.add(new ReportMetric("Volume Variability",
                String.format("%.1f%%", calculateVolumeVariability(startDate, endDate, branchId)), 0.0));

        section.setMetrics(metrics);

        return section;
    }

    private ReportSection generateTimeEfficiencySection(LocalDate startDate, LocalDate endDate, String branchId) {
        ReportSection section = new ReportSection();
        section.setTitle("Time Efficiency Analysis");
        section.setOrder(3);

        // Prepare data for time efficiency chart
        ChartData timeEfficiencyChart = new ChartData();
        timeEfficiencyChart.setChartType("SCATTER");
        timeEfficiencyChart.setTitle("Courier Delivery Speed vs. Volume");
        timeEfficiencyChart.setXAxisLabel("Average Deliveries Per Day");
        timeEfficiencyChart.setYAxisLabel("Average Delivery Time (min)");

        // Generate courier efficiency data
        Map<String, Object> chartData = generateCourierEfficiencyData(startDate, endDate, branchId);
        timeEfficiencyChart.setData(chartData);

        // Add chart to section
        section.setCharts(Collections.singletonList(timeEfficiencyChart));

        // Add time efficiency insights
        section.setSummary("This section examines the relationship between courier delivery volume and speed. " +
                "Each point represents a courier, with their average daily delivery volume on the x-axis and " +
                "their average delivery time on the y-axis. Ideal couriers appear in the bottom-right quadrant, " +
                "indicating high volume and fast delivery times. This analysis helps identify high performers " +
                "and couriers who may need additional training or support.");

        // Add time efficiency metrics
        List<ReportMetric> metrics = new ArrayList<>();
        metrics.add(new ReportMetric("Fastest Courier",
                identifyFastestCourier(startDate, endDate, branchId), 0.0));
        metrics.add(new ReportMetric("Most Efficient Courier",
                identifyMostEfficientCourier(startDate, endDate, branchId), 0.0));
        metrics.add(new ReportMetric("Average Delivery Time Variance",
                String.format("%.2f minutes", calculateDeliveryTimeVariance(startDate, endDate, branchId)), 0.0));
        metrics.add(new ReportMetric("Efficiency Score Range",
                String.format("%.1f - %.1f", getMinEfficiencyScore(startDate, endDate, branchId),
                        getMaxEfficiencyScore(startDate, endDate, branchId)), 0.0));

        section.setMetrics(metrics);

        return section;
    }

    private ReportSection generateQualityMetricsSection(LocalDate startDate, LocalDate endDate, String branchId) {
        ReportSection section = new ReportSection();
        section.setTitle("Service Quality Analysis");
        section.setOrder(4);

        // Prepare data for quality metrics chart
        ChartData qualityChart = new ChartData();
        qualityChart.setChartType("HORIZONTALBAR");
        qualityChart.setTitle("Courier Quality Metrics");
        qualityChart.setXAxisLabel("Score");
        qualityChart.setYAxisLabel("Metric");

        // Generate quality metrics data
        Map<String, Object> chartData = generateQualityMetricsData(startDate, endDate, branchId);
        qualityChart.setData(chartData);

        // Add chart to section
        section.setCharts(Collections.singletonList(qualityChart));

        // Add quality insights
        section.setSummary("This section analyzes the quality of service provided by couriers. " +
                "The chart shows average scores across various quality metrics, including customer ratings, " +
                "on-time delivery percentage, order accuracy, and professionalism. These metrics provide a " +
                "comprehensive view of courier service quality and highlight areas for improvement.");

        // Add quality metrics
        List<ReportMetric> metrics = new ArrayList<>();
        metrics.add(new ReportMetric("Highest Rated Courier",
                identifyHighestRatedCourier(startDate, endDate, branchId), 0.0));
        metrics.add(new ReportMetric("Most Improved Courier",
                identifyMostImprovedCourier(startDate, endDate, branchId), 0.0));
        metrics.add(new ReportMetric("Average Customer Feedback Response Rate",
                String.format("%.1f%%", calculateFeedbackResponseRate(startDate, endDate, branchId)), 0.0));
        metrics.add(new ReportMetric("Common Positive Feedback",
                identifyCommonPositiveFeedback(startDate, endDate, branchId), 0.0));

        section.setMetrics(metrics);

        return section;
    }

    private ReportSection generateTopPerformersSection(LocalDate startDate, LocalDate endDate, String branchId) {
        ReportSection section = new ReportSection();
        section.setTitle("Top Performing Couriers");
        section.setOrder(5);

        // Prepare data for top performers chart
        ChartData topPerformersChart = new ChartData();
        topPerformersChart.setChartType("BAR");
        topPerformersChart.setTitle("Overall Courier Performance Scores");
        topPerformersChart.setXAxisLabel("Courier");
        topPerformersChart.setYAxisLabel("Performance Score");

        // Generate top performers data
        Map<String, Object> chartData = generateTopPerformersData(startDate, endDate, branchId);
        topPerformersChart.setData(chartData);

        // Add chart to section
        section.setCharts(Collections.singletonList(topPerformersChart));

        // Add top performers insights
        section.setSummary("This section highlights the top-performing couriers based on a composite performance score. " +
                "The score takes into account delivery volume, time efficiency, customer ratings, and order accuracy. " +
                "Recognizing top performers can help motivate the team and identify best practices that can be shared with others.");

        // Generate top performers metrics
        List<ReportMetric> metrics = new ArrayList<>();
        metrics.add(new ReportMetric("Top Overall Performer",
                identifyTopOverallPerformer(startDate, endDate, branchId), 0.0));
        metrics.add(new ReportMetric("Top New Courier",
                identifyTopNewCourier(startDate, endDate, branchId), 0.0));
        metrics.add(new ReportMetric("Most Consistent Performer",
                identifyMostConsistentPerformer(startDate, endDate, branchId), 0.0));
        metrics.add(new ReportMetric("Performance Score Distribution",
                String.format("%.1f%% above target", calculatePerformanceScoreDistribution(startDate, endDate, branchId)), 0.0));

        section.setMetrics(metrics);

        return section;
    }

    private ReportSection generateRecommendationsSection(LocalDate startDate, LocalDate endDate, String branchId) {
        ReportSection section = new ReportSection();
        section.setTitle("Recommendations for Improvement");
        section.setOrder(6);

        // Generate recommendations based on the analysis
        List<String> recommendations = generateRecommendations(startDate, endDate, branchId);

        StringBuilder summaryBuilder = new StringBuilder();
        summaryBuilder.append("Based on the courier productivity analysis, the following recommendations are provided to improve performance:\n\n");

        for (int i = 0; i < recommendations.size(); i++) {
            summaryBuilder.append(i + 1).append(". ").append(recommendations.get(i)).append("\n");
        }

        section.setSummary(summaryBuilder.toString());

        return section;
    }

    // Helper methods for calculations

    private double calculateAverageDeliveriesPerCourier(LocalDate startDate, LocalDate endDate, String branchId) {
        // Implementation would query the repositories for actual data
        return 28.7; // Average deliveries per courier during the period
    }

    private double calculateAverageDeliveryTimePerCourier(LocalDate startDate, LocalDate endDate, String branchId) {
        // Implementation would query the repositories for actual data
        return 23.5; // Average delivery time in minutes
    }

    private double calculateAverageRatingPerCourier(LocalDate startDate, LocalDate endDate, String branchId) {
        // Implementation would query the repositories for actual data
        return 4.7; // Average rating out of 5.0
    }

    private double calculateCourierUtilizationRate(LocalDate startDate, LocalDate endDate, String branchId) {
        // Implementation would query the repositories for actual data
        return 87.3; // Utilization rate as percentage
    }

    private double calculatePercentChange(double current, double previous) {
        if (previous == 0) return 0.0;
        return ((current - previous) / previous) * 100;
    }

    private double getPreviousPeriodAvgDeliveriesPerCourier(LocalDate startDate, LocalDate endDate, String branchId) {
        // Calculate the same period length for the previous period
        int days = (int) (endDate.toEpochDay() - startDate.toEpochDay() + 1);
        LocalDate prevPeriodEnd = startDate.minusDays(1);
        LocalDate prevPeriodStart = prevPeriodEnd.minusDays(days - 1);

        // Query the repositories for the previous period data
        return 25.9; // Placeholder value
    }

    private double getPreviousPeriodAvgDeliveryTimePerCourier(LocalDate startDate, LocalDate endDate, String branchId) {
        // Similar implementation as getPreviousPeriodAvgDeliveriesPerCourier
        return 25.2; // Placeholder value
    }

    private double getPreviousPeriodAvgRatingPerCourier(LocalDate startDate, LocalDate endDate, String branchId) {
        // Similar implementation as getPreviousPeriodAvgDeliveriesPerCourier
        return 4.5; // Placeholder value
    }

    private double getPreviousPeriodCourierUtilizationRate(LocalDate startDate, LocalDate endDate, String branchId) {
        // Similar implementation as getPreviousPeriodAvgDeliveriesPerCourier
        return 82.7; // Placeholder value
    }

    private Map<String, Object> generateDailyDeliveryVolumeData(LocalDate startDate, LocalDate endDate, String branchId) {
        // Generate daily delivery volume data for the chart
        Map<String, Object> chartData = new HashMap<>();

        List<String> labels = new ArrayList<>();
        List<Integer> data = new ArrayList<>();

        // Format for date display
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd");

        // Generate data for each day in the period
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            labels.add(date.format(formatter));

            // Query repository for actual data - using random placeholder here
            int dailyVolume = 150 + (int)(Math.random() * 100); // Random value between 150-250
            data.add(dailyVolume);
        }

        chartData.put("labels", labels);
        chartData.put("datasets", Collections.singletonList(
                Map.of(
                        "label", "Number of Deliveries",
                        "data", data,
                        "backgroundColor", "#4e73df"
                )
        ));

        return chartData;
    }

    private String identifyHighestVolumeDay(LocalDate startDate, LocalDate endDate, String branchId) {
        // Implementation would identify the highest volume day from repository data
        return "May 12, 2025 (247 deliveries)"; // Placeholder value
    }

    private String identifyLowestVolumeDay(LocalDate startDate, LocalDate endDate, String branchId) {
        // Implementation would identify the lowest volume day from repository data
        return "May 7, 2025 (156 deliveries)"; // Placeholder value
    }

    private double calculateAverageDailyVolume(LocalDate startDate, LocalDate endDate, String branchId) {
        // Implementation would calculate average daily volume from repository data
        return 195.3; // Placeholder value
    }

    private double calculateVolumeVariability(LocalDate startDate, LocalDate endDate, String branchId) {
        // Implementation would calculate volume variability (coefficient of variation)
        return 22.7; // Placeholder value as percentage
    }

    private Map<String, Object> generateCourierEfficiencyData(LocalDate startDate, LocalDate endDate, String branchId) {
        // Generate courier efficiency data for the scatter chart
        Map<String, Object> chartData = new HashMap<>();

        // Generate random data for demonstration
        // In a real implementation, this would be calculated from courier performance data
        List<Map<String, Object>> datasets = new ArrayList<>();
        Map<String, Object> dataset = new HashMap<>();
        dataset.put("label", "Courier Efficiency");
        
        List<Map<String, Object>> data = new ArrayList<>();
        // Generate 20 random data points representing couriers
        for (int i = 0; i < 20; i++) {
            Map<String, Object> point = new HashMap<>();
            // x: average deliveries per day (10-30)
            point.put("x", 10 + Math.random() * 20);
            // y: average delivery time in minutes (18-35)
            point.put("y", 18 + Math.random() * 17);
            // r: bubble size based on customer rating (proportional to rating 3.5-5.0)
            point.put("r", 5 + (3.5 + Math.random() * 1.5) * 2);
            data.add(point);
        }
        
        dataset.put("data", data);
        dataset.put("backgroundColor", "rgba(78, 115, 223, 0.5)");
        dataset.put("borderColor", "rgba(78, 115, 223, 1)");
        
        datasets.add(dataset);
        chartData.put("datasets", datasets);

        return chartData;
    }

    private String identifyFastestCourier(LocalDate startDate, LocalDate endDate, String branchId) {
        // Implementation would identify the fastest courier from repository data
        return "Mark Johnson (avg. 18.7 min)"; // Placeholder value
    }

    private String identifyMostEfficientCourier(LocalDate startDate, LocalDate endDate, String branchId) {
        // Implementation would identify the most efficient courier (best speed-to-volume ratio)
        return "Sarah Chen (27.3 del/day, 20.5 min avg)"; // Placeholder value
    }

    private double calculateDeliveryTimeVariance(LocalDate startDate, LocalDate endDate, String branchId) {
        // Implementation would calculate the variance in delivery times across couriers
        return 15.8; // Placeholder value in minutes
    }

    private double getMinEfficiencyScore(LocalDate startDate, LocalDate endDate, String branchId) {
        // Implementation would calculate the minimum efficiency score across couriers
        return 65.7; // Placeholder value
    }

    private double getMaxEfficiencyScore(LocalDate startDate, LocalDate endDate, String branchId) {
        // Implementation would calculate the maximum efficiency score across couriers
        return 94.2; // Placeholder value
    }

    private Map<String, Object> generateQualityMetricsData(LocalDate startDate, LocalDate endDate, String branchId) {
        // Generate quality metrics data for the horizontal bar chart
        Map<String, Object> chartData = new HashMap<>();

        List<String> labels = Arrays.asList(
                "Customer Rating",
                "On-Time Delivery",
                "Order Accuracy",
                "Professionalism",
                "Issue Resolution"
        );

        List<Double> data = Arrays.asList(4.7, 92.3, 97.8, 4.5, 4.2); // Placeholder values

        chartData.put("labels", labels);
        chartData.put("datasets", Collections.singletonList(
                Map.of(
                        "label", "Average Score",
                        "data", data,
                        "backgroundColor", Arrays.asList("#4e73df", "#1cc88a", "#36b9cc", "#f6c23e", "#e74a3b")
                )
        ));

        return chartData;
    }

    private String identifyHighestRatedCourier(LocalDate startDate, LocalDate endDate, String branchId) {
        // Implementation would identify the highest rated courier from repository data
        return "David Williams (4.95/5.0)"; // Placeholder value
    }

    private String identifyMostImprovedCourier(LocalDate startDate, LocalDate endDate, String branchId) {
        // Implementation would identify the most improved courier compared to previous period
        return "Ahmed Hassan (+0.7 points)"; // Placeholder value
    }

    private double calculateFeedbackResponseRate(LocalDate startDate, LocalDate endDate, String branchId) {
        // Implementation would calculate the feedback response rate
        return 38.5; // Placeholder value as percentage
    }

    private String identifyCommonPositiveFeedback(LocalDate startDate, LocalDate endDate, String branchId) {
        // Implementation would identify common positive feedback themes
        return "Friendly service, timely delivery"; // Placeholder value
    }

    private Map<String, Object> generateTopPerformersData(LocalDate startDate, LocalDate endDate, String branchId) {
        // Generate top performers data for the bar chart
        Map<String, Object> chartData = new HashMap<>();

        // In a real implementation, this would be the top 10 couriers by performance score
        List<String> labels = Arrays.asList(
                "Sarah C.", "David W.", "Mark J.", "Lisa T.", "Ahmed H.", 
                "Michael R.", "Jessica L.", "James B.", "Emily K.", "John D."
        );

        List<Double> data = Arrays.asList(94.2, 92.7, 90.3, 89.5, 87.6, 85.9, 84.2, 83.1, 82.4, 81.8); // Placeholder values

        chartData.put("labels", labels);
        chartData.put("datasets", Collections.singletonList(
                Map.of(
                        "label", "Performance Score",
                        "data", data,
                        "backgroundColor", "#1cc88a"
                )
        ));

        return chartData;
    }

    private String identifyTopOverallPerformer(LocalDate startDate, LocalDate endDate, String branchId) {
        // Implementation would identify the top overall performer
        return "Sarah Chen (94.2 points)"; // Placeholder value
    }

    private String identifyTopNewCourier(LocalDate startDate, LocalDate endDate, String branchId) {
        // Implementation would identify the top performing new courier (< 3 months)
        return "Lisa Torres (89.5 points)"; // Placeholder value
    }

    private String identifyMostConsistentPerformer(LocalDate startDate, LocalDate endDate, String branchId) {
        // Implementation would identify the most consistent performer (lowest variability)
        return "David Williams (σ=2.3)"; // Placeholder value
    }

    private double calculatePerformanceScoreDistribution(LocalDate startDate, LocalDate endDate, String branchId) {
        // Implementation would calculate the percentage of couriers above the target score
        return 68.4; // Placeholder value as percentage
    }

    private List<String> generateRecommendations(LocalDate startDate, LocalDate endDate, String branchId) {
        // Generate recommendations based on the analysis
        // This would typically involve complex logic based on the metrics
        List<String> recommendations = new ArrayList<>();

        // Add placeholder recommendations
        recommendations.add("Implement a mentorship program pairing top performers with underperforming couriers.");
        recommendations.add("Provide additional training on order handling for couriers with below-average order accuracy.");
        recommendations.add("Review courier scheduling to better match capacity with peak delivery periods.");
        recommendations.add("Create a recognition program to highlight and reward top-performing couriers.");
        recommendations.add("Implement a structured feedback mechanism for customers to provide more detailed courier performance insights.");

        return recommendations;
    }
}
