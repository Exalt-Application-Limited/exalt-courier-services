package com.exalt.courier.hqadmin.service.reporting;

import com.socialecommerceecosystem.hqadmin.annotation.Traced;
import com.socialecommerceecosystem.hqadmin.model.reporting.AdvancedReport;
import com.socialecommerceecosystem.hqadmin.model.reporting.ChartData;
import com.socialecommerceecosystem.hqadmin.model.reporting.ReportMetric;
import com.socialecommerceecosystem.hqadmin.model.reporting.ReportSection;
import com.socialecommerceecosystem.hqadmin.model.reporting.TableData;
import com.socialecommerceecosystem.hqadmin.service.TracingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Generator for courier productivity reports
 */
@Slf4j
@Service
public class CourierProductivityReportGenerator implements ReportGenerator {

    private final TracingService tracingService;
    private final Random random = new Random();
    
    @Autowired
    public CourierProductivityReportGenerator(TracingService tracingService) {
        this.tracingService = tracingService;
    }

    @Override
    @Traced("CourierProductivityReportGenerator.generateReport")
    public AdvancedReport generateReport(
            LocalDateTime startDate,
            LocalDateTime endDate,
            List<String> regions,
            List<String> branches) {
        
        tracingService.addTag("startDate", startDate.toString());
        tracingService.addTag("endDate", endDate.toString());
        
        log.info("Generating courier productivity report. Start: {}, End: {}", startDate, endDate);
        
        // Create the report
        AdvancedReport report = AdvancedReport.builder()
                .reportId(UUID.randomUUID().toString())
                .title("Courier Productivity Report")
                .description("Comprehensive analysis of courier productivity metrics and improvement opportunities")
                .type(AdvancedReport.ReportType.COURIER_PRODUCTIVITY)
                .generatedAt(LocalDateTime.now())
                .startDate(startDate)
                .endDate(endDate)
                .regions(regions)
                .branches(branches)
                .sections(new ArrayList<>())
                .build();
        
        // Add report sections
        report.addSection(createExecutiveSummarySection(startDate, endDate));
        report.addSection(createProductivityOverviewSection(startDate, endDate, regions));
        report.addSection(createTimeUtilizationSection(startDate, endDate));
        report.addSection(createDeliveryEfficiencySection(startDate, endDate, regions));
        report.addSection(createPerformanceComparisonSection(startDate, endDate, branches));
        report.addSection(createRecommendationsSection());
        
        return report;
    }
    
    /**
     * Create the executive summary section
     */
    private ReportSection createExecutiveSummarySection(LocalDateTime startDate, LocalDateTime endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy");
        
        ReportSection section = ReportSection.builder()
                .id(UUID.randomUUID().toString())
                .title("Executive Summary")
                .description("Key courier productivity highlights for the period " + 
                        startDate.format(formatter) + " to " + endDate.format(formatter))
                .type(ReportSection.SectionType.SUMMARY)
                .metrics(new ArrayList<>())
                .htmlContent("<p>This report analyzes courier productivity and performance, identifying optimization " +
                        "opportunities and best practices. Key findings include:</p>" +
                        "<ul>" +
                        "<li>Overall courier productivity has increased by <strong>12.3%</strong> compared to the previous period</li>" +
                        "<li>Average deliveries per courier per day has increased by <strong>15.6%</strong></li>" +
                        "<li>Courier utilization rate has improved by <strong>8.7%</strong></li>" +
                        "<li>On-time delivery rate has improved by <strong>5.2%</strong></li>" +
                        "</ul>" +
                        "<p>Areas requiring attention:</p>" +
                        "<ul>" +
                        "<li>Courier idle time remains high at 18.5% of total shift time</li>" +
                        "<li>Significant productivity variance between top and bottom performing couriers (42.8% gap)</li>" +
                        "<li>Courier turnover rate remains elevated at 16.7% quarterly</li>" +
                        "</ul>")
                .conclusion("By implementing the recommendations in this report, we project potential productivity " +
                        "improvements of 18-22% and a reduction in courier turnover by 25-30%.")
                .build();
        
        // Add summary metrics
        section.addMetric(ReportMetric.builder()
                .name("overall_productivity")
                .displayName("Overall Productivity")
                .value(86.5)
                .unit("%")
                .changeValue(9.5)
                .changePercentage(12.3)
                .formatPrecision(1)
                .status("good")
                .build());
        
        section.addMetric(ReportMetric.builder()
                .name("avg_deliveries_per_day")
                .displayName("Avg. Deliveries per Day")
                .value(24.7)
                .unit("")
                .changeValue(3.3)
                .changePercentage(15.6)
                .formatPrecision(1)
                .status("good")
                .build());
        
        section.addMetric(ReportMetric.builder()
                .name("courier_utilization")
                .displayName("Courier Utilization")
                .value(81.5)
                .unit("%")
                .changeValue(6.5)
                .changePercentage(8.7)
                .formatPrecision(1)
                .status("good")
                .build());
        
        section.addMetric(ReportMetric.builder()
                .name("on_time_delivery_rate")
                .displayName("On-Time Delivery")
                .value(92.8)
                .unit("%")
                .changeValue(4.6)
                .changePercentage(5.2)
                .formatPrecision(1)
                .status("good")
                .build());
        
        return section;
    }
    
    /**
     * Create the productivity overview section
     */
    private ReportSection createProductivityOverviewSection(
            LocalDateTime startDate, 
            LocalDateTime endDate, 
            List<String> regions) {
        
        ReportSection section = ReportSection.builder()
                .id(UUID.randomUUID().toString())
                .title("Productivity Overview")
                .description("Key metrics and trends for courier productivity")
                .type(ReportSection.SectionType.ANALYSIS)
                .metrics(new ArrayList<>())
                .charts(new ArrayList<>())
                .build();
        
        // Add metrics
        section.addMetric(ReportMetric.builder()
                .name("avg_deliveries_per_shift")
                .displayName("Deliveries per Shift")
                .value(24.7)
                .unit("")
                .changeValue(3.3)
                .changePercentage(15.6)
                .formatPrecision(1)
                .status("good")
                .build());
        
        section.addMetric(ReportMetric.builder()
                .name("avg_deliveries_per_hour")
                .displayName("Deliveries per Hour")
                .value(3.2)
                .unit("")
                .changeValue(0.4)
                .changePercentage(14.3)
                .formatPrecision(1)
                .status("good")
                .build());
        
        section.addMetric(ReportMetric.builder()
                .name("avg_time_per_delivery")
                .displayName("Time per Delivery")
                .value(18.8)
                .unit("min")
                .changeValue(-2.2)
                .changePercentage(-10.5)
                .formatPrecision(1)
                .status("good")
                .build());
        
        section.addMetric(ReportMetric.builder()
                .name("first_attempt_success")
                .displayName("First Attempt Success")
                .value(87.5)
                .unit("%")
                .changeValue(5.7)
                .changePercentage(7.0)
                .formatPrecision(1)
                .status("good")
                .build());
        
        section.addMetric(ReportMetric.builder()
                .name("courier_turnover")
                .displayName("Courier Turnover")
                .value(16.7)
                .unit("%")
                .changeValue(-2.3)
                .changePercentage(-12.1)
                .formatPrecision(1)
                .status("warning")
                .build());
        
        // Add productivity trend chart
        ChartData productivityTrendChart = ChartData.builder()
                .id(UUID.randomUUID().toString())
                .title("Courier Productivity Trends")
                .type(ChartData.ChartType.LINE)
                .labels(Arrays.asList("Week 1", "Week 2", "Week 3", "Week 4", "Week 5", "Week 6", "Week 7", "Week 8"))
                .series(List.of(
                        ChartData.DataSeries.builder()
                                .name("Deliveries per Day")
                                .data(Arrays.asList(21.4, 22.2, 22.8, 23.5, 23.9, 24.2, 24.5, 24.7))
                                .borderColor("#4CAF50")
                                .backgroundColor("rgba(76, 175, 80, 0.1)")
                                .fill(true)
                                .build(),
                        ChartData.DataSeries.builder()
                                .name("Target")
                                .data(Arrays.asList(22.0, 22.0, 22.0, 22.0, 22.0, 22.0, 22.0, 22.0))
                                .borderColor("#FFC107")
                                .borderDash(Arrays.asList(5, 5))
                                .fill(false)
                                .build()
                ))
                .xAxisLabel("Week")
                .yAxisLabel("Deliveries per Day")
                .showLegend(true)
                .build();
        
        section.addChart(productivityTrendChart);
        
        // Add regional productivity comparison chart
        List<String> defaultRegions = (regions != null && !regions.isEmpty()) 
                ? regions 
                : Arrays.asList("NAM", "EUR", "APAC", "AFR", "LAT");
        
        ChartData regionalProductivityChart = ChartData.builder()
                .id(UUID.randomUUID().toString())
                .title("Regional Productivity Comparison")
                .type(ChartData.ChartType.BAR)
                .labels(defaultRegions)
                .series(List.of(
                        ChartData.DataSeries.builder()
                                .name("Deliveries per Day")
                                .data(generateRegionalProductivityData(defaultRegions))
                                .backgroundColor("#4CAF50")
                                .build(),
                        ChartData.DataSeries.builder()
                                .name("Target")
                                .data(Arrays.asList(25.0, 25.0, 25.0, 25.0, 25.0))
                                .backgroundColor("rgba(255, 193, 7, 0.5)")
                                .borderColor("#FFC107")
                                .borderWidth(1)
                                .build()
                ))
                .xAxisLabel("Region")
                .yAxisLabel("Deliveries per Day")
                .showLegend(true)
                .build();
        
        section.addChart(regionalProductivityChart);
        
        // Add productivity distribution chart
        ChartData productivityDistributionChart = ChartData.builder()
                .id(UUID.randomUUID().toString())
                .title("Courier Productivity Distribution")
                .type(ChartData.ChartType.BAR)
                .labels(Arrays.asList("<15", "15-18", "18-21", "21-24", "24-27", "27-30", ">30"))
                .series(List.of(
                        ChartData.DataSeries.builder()
                                .name("% of Couriers")
                                .data(Arrays.asList(5.8, 12.3, 21.5, 28.7, 18.9, 9.2, 3.6))
                                .backgroundColor("#2196F3")
                                .build()
                ))
                .xAxisLabel("Deliveries per Day")
                .yAxisLabel("% of Couriers")
                .showLegend(false)
                .build();
        
        section.addChart(productivityDistributionChart);
        
        return section;
    }
    
    /**
     * Create the time utilization section
     */
    private ReportSection createTimeUtilizationSection(LocalDateTime startDate, LocalDateTime endDate) {
        ReportSection section = ReportSection.builder()
                .id(UUID.randomUUID().toString())
                .title("Time Utilization Analysis")
                .description("Analysis of courier time utilization and efficiency")
                .type(ReportSection.SectionType.ANALYSIS)
                .metrics(new ArrayList<>())
                .charts(new ArrayList<>())
                .build();
        
        // Add metrics
        section.addMetric(ReportMetric.builder()
                .name("active_delivery_time")
                .displayName("Active Delivery Time")
                .value(67.5)
                .unit("%")
                .changeValue(5.3)
                .changePercentage(8.5)
                .formatPrecision(1)
                .status("good")
                .build());
        
        section.addMetric(ReportMetric.builder()
                .name("transit_time")
                .displayName("Transit Time")
                .value(14.0)
                .unit("%")
                .changeValue(2.5)
                .changePercentage(21.7)
                .formatPrecision(1)
                .status("good")
                .build());
        
        section.addMetric(ReportMetric.builder()
                .name("idle_time")
                .displayName("Idle Time")
                .value(18.5)
                .unit("%")
                .changeValue(-7.8)
                .changePercentage(-29.7)
                .formatPrecision(1)
                .status("warning")
                .build());
        
        section.addMetric(ReportMetric.builder()
                .name("avg_time_between_deliveries")
                .displayName("Time Between Deliveries")
                .value(8.3)
                .unit("min")
                .changeValue(-1.5)
                .changePercentage(-15.3)
                .formatPrecision(1)
                .status("good")
                .build());
        
        section.addMetric(ReportMetric.builder()
                .name("avg_service_time")
                .displayName("Service Time")
                .value(5.3)
                .unit("min")
                .changeValue(-0.6)
                .changePercentage(-10.2)
                .formatPrecision(1)
                .status("good")
                .build());
        
        // Add time utilization chart
        ChartData timeUtilizationChart = ChartData.builder()
                .id(UUID.randomUUID().toString())
                .title("Courier Time Utilization")
                .type(ChartData.ChartType.PIE)
                .labels(Arrays.asList(
                        "Active Delivery", 
                        "Transit", 
                        "Idle Time", 
                        "Loading/Unloading", 
                        "Break", 
                        "Administration"))
                .series(List.of(
                        ChartData.DataSeries.builder()
                                .name("Time Distribution")
                                .data(Arrays.asList(67.5, 14.0, 7.5, 5.0, 4.0, 2.0))
                                .backgroundColor(Arrays.asList(
                                        "#4CAF50", "#2196F3", "#F44336", 
                                        "#9C27B0", "#FF9800", "#607D8B"))
                                .build()
                ))
                .showLegend(true)
                .build();
        
        section.addChart(timeUtilizationChart);
        
        // Add comparison between top and bottom performers
        ChartData performerComparisonChart = ChartData.builder()
                .id(UUID.randomUUID().toString())
                .title("Time Utilization: Top vs Bottom Performers")
                .type(ChartData.ChartType.BAR)
                .labels(Arrays.asList("Active Delivery", "Transit", "Idle Time", "Loading/Unloading", "Admin Tasks"))
                .series(List.of(
                        ChartData.DataSeries.builder()
                                .name("Top Performers")
                                .data(Arrays.asList(78.5, 12.5, 3.2, 3.8, 2.0))
                                .backgroundColor("#4CAF50")
                                .build(),
                        ChartData.DataSeries.builder()
                                .name("Bottom Performers")
                                .data(Arrays.asList(52.8, 15.7, 21.4, 6.3, 3.8))
                                .backgroundColor("#F44336")
                                .build()
                ))
                .xAxisLabel("Activity")
                .yAxisLabel("% of Time")
                .showLegend(true)
                .build();
        
        section.addChart(performerComparisonChart);
        
        // Add time utilization trend chart
        ChartData timeUtilizationTrendChart = ChartData.builder()
                .id(UUID.randomUUID().toString())
                .title("Time Utilization Trends")
                .type(ChartData.ChartType.LINE)
                .labels(Arrays.asList("Week 1", "Week 2", "Week 3", "Week 4", "Week 5", "Week 6", "Week 7", "Week 8"))
                .series(List.of(
                        ChartData.DataSeries.builder()
                                .name("Active Delivery Time")
                                .data(Arrays.asList(62.2, 63.5, 64.8, 65.7, 66.3, 66.8, 67.2, 67.5))
                                .borderColor("#4CAF50")
                                .backgroundColor("rgba(76, 175, 80, 0.1)")
                                .fill(true)
                                .build(),
                        ChartData.DataSeries.builder()
                                .name("Idle Time")
                                .data(Arrays.asList(26.3, 24.8, 22.5, 21.2, 20.4, 19.5, 18.9, 18.5))
                                .borderColor("#F44336")
                                .backgroundColor("rgba(244, 67, 54, 0.1)")
                                .fill(true)
                                .build(),
                        ChartData.DataSeries.builder()
                                .name("Target Active Time")
                                .data(Arrays.asList(70.0, 70.0, 70.0, 70.0, 70.0, 70.0, 70.0, 70.0))
                                .borderColor("#FFC107")
                                .borderDash(Arrays.asList(5, 5))
                                .fill(false)
                                .build()
                ))
                .xAxisLabel("Week")
                .yAxisLabel("% of Time")
                .showLegend(true)
                .build();
        
        section.addChart(timeUtilizationTrendChart);
        
        return section;
    }
    
    /**
     * Create the delivery efficiency section
     */
    private ReportSection createDeliveryEfficiencySection(
            LocalDateTime startDate, 
            LocalDateTime endDate,
            List<String> regions) {
        
        ReportSection section = ReportSection.builder()
                .id(UUID.randomUUID().toString())
                .title("Delivery Efficiency Analysis")
                .description("Analysis of delivery efficiency and service quality metrics")
                .type(ReportSection.SectionType.ANALYSIS)
                .metrics(new ArrayList<>())
                .charts(new ArrayList<>())
                .tables(new ArrayList<>())
                .build();
        
        // Add metrics
        section.addMetric(ReportMetric.builder()
                .name("on_time_delivery")
                .displayName("On-Time Delivery")
                .value(92.8)
                .unit("%")
                .changeValue(4.6)
                .changePercentage(5.2)
                .formatPrecision(1)
                .status("good")
                .build());
        
        section.addMetric(ReportMetric.builder()
                .name("first_attempt_success")
                .displayName("First Attempt Success")
                .value(87.5)
                .unit("%")
                .changeValue(5.7)
                .changePercentage(7.0)
                .formatPrecision(1)
                .status("good")
                .build());
        
        section.addMetric(ReportMetric.builder()
                .name("avg_stops_per_hour")
                .displayName("Stops per Hour")
                .value(3.2)
                .unit("")
                .changeValue(0.4)
                .changePercentage(14.3)
                .formatPrecision(1)
                .status("good")
                .build());
        
        section.addMetric(ReportMetric.builder()
                .name("customer_satisfaction")
                .displayName("Customer Satisfaction")
                .value(4.6)
                .unit("/5")
                .changeValue(0.3)
                .changePercentage(7.0)
                .formatPrecision(1)
                .status("good")
                .build());
        
        section.addMetric(ReportMetric.builder()
                .name("route_adherence")
                .displayName("Route Adherence")
                .value(84.5)
                .unit("%")
                .changeValue(7.3)
                .changePercentage(9.5)
                .formatPrecision(1)
                .status("good")
                .build());
        
        // Add efficiency by delivery type chart
        ChartData deliveryTypeChart = ChartData.builder()
                .id(UUID.randomUUID().toString())
                .title("Efficiency by Delivery Type")
                .type(ChartData.ChartType.BAR)
                .labels(Arrays.asList("Standard", "Express", "Same-Day", "Scheduled", "Special Handling"))
                .series(List.of(
                        ChartData.DataSeries.builder()
                                .name("Deliveries per Hour")
                                .data(Arrays.asList(3.5, 2.8, 2.2, 3.0, 1.5))
                                .backgroundColor("#4CAF50")
                                .build(),
                        ChartData.DataSeries.builder()
                                .name("On-Time %")
                                .data(Arrays.asList(94.3, 91.8, 87.5, 95.2, 89.4))
                                .backgroundColor("#2196F3")
                                .build()
                ))
                .xAxisLabel("Delivery Type")
                .yAxisLabel("Value")
                .showLegend(true)
                .build();
        
        section.addChart(deliveryTypeChart);
        
        // Create table columns for regional efficiency
        List<TableData.Column> efficiencyColumns = Arrays.asList(
                TableData.Column.builder()
                        .id("region")
                        .name("Region")
                        .dataKey("region")
                        .width("15%")
                        .sortable(true)
                        .build(),
                TableData.Column.builder()
                        .id("deliveries_per_day")
                        .name("Deliveries/Day")
                        .dataKey("deliveries_per_day")
                        .width("15%")
                        .sortable(true)
                        .align("right")
                        .cellFormat("%.1f")
                        .build(),
                TableData.Column.builder()
                        .id("stops_per_hour")
                        .name("Stops/Hour")
                        .dataKey("stops_per_hour")
                        .width("15%")
                        .sortable(true)
                        .align("right")
                        .cellFormat("%.1f")
                        .build(),
                TableData.Column.builder()
                        .id("on_time_rate")
                        .name("On-Time %")
                        .dataKey("on_time_rate")
                        .width("15%")
                        .sortable(true)
                        .align("right")
                        .cellFormat("%.1f%%")
                        .conditionalFormats(Arrays.asList(
                                TableData.ConditionalFormat.builder()
                                        .condition("value < 85")
                                        .backgroundColor("#FFEBEE")
                                        .textColor("#D32F2F")
                                        .build(),
                                TableData.ConditionalFormat.builder()
                                        .condition("value >= 85 && value < 90")
                                        .backgroundColor("#FFF8E1")
                                        .textColor("#F57C00")
                                        .build(),
                                TableData.ConditionalFormat.builder()
                                        .condition("value >= 90")
                                        .backgroundColor("#E8F5E9")
                                        .textColor("#388E3C")
                                        .build()
                        ))
                        .build(),
                TableData.Column.builder()
                        .id("first_attempt_success")
                        .name("First Attempt %")
                        .dataKey("first_attempt_success")
                        .width("15%")
                        .sortable(true)
                        .align("right")
                        .cellFormat("%.1f%%")
                        .build(),
                TableData.Column.builder()
                        .id("customer_rating")
                        .name("Cust. Rating")
                        .dataKey("customer_rating")
                        .width("15%")
                        .sortable(true)
                        .align("right")
                        .cellFormat("%.1f")
                        .build()
        );
        
        // Create rows for regional efficiency table
        List<Map<String, Object>> efficiencyRows = new ArrayList<>();
        
        // Generate data for default regions if none provided
        List<String> defaultRegions = (regions != null && !regions.isEmpty()) 
                ? regions 
                : Arrays.asList("NAM", "EUR", "APAC", "AFR", "LAT");
        
        for (String region : defaultRegions) {
            Map<String, Object> row = generateRegionalEfficiencyRow(region);
            efficiencyRows.add(row);
        }
        
        // Create the regional efficiency table
        TableData efficiencyTable = TableData.builder()
                .id(UUID.randomUUID().toString())
                .title("Regional Efficiency Metrics")
                .columns(efficiencyColumns)
                .rows(efficiencyRows)
                .showHeader(true)
                .striped(true)
                .hoverable(true)
                .responsive(true)
                .build();
        
        section.addTable(efficiencyTable);
        
        return section;
    }
    
    /**
     * Create the performance comparison section
     */
    private ReportSection createPerformanceComparisonSection(
            LocalDateTime startDate, 
            LocalDateTime endDate,
            List<String> branches) {
        
        ReportSection section = ReportSection.builder()
                .id(UUID.randomUUID().toString())
                .title("Courier Performance Comparison")
                .description("Comparative analysis of courier performance across different dimensions")
                .type(ReportSection.SectionType.COMPARISON)
                .tables(new ArrayList<>())
                .charts(new ArrayList<>())
                .build();
        
        // Create table columns for top couriers
        List<TableData.Column> courierColumns = Arrays.asList(
                TableData.Column.builder()
                        .id("rank")
                        .name("Rank")
                        .dataKey("rank")
                        .width("10%")
                        .align("center")
                        .build(),
                TableData.Column.builder()
                        .id("courier_id")
                        .name("Courier ID")
                        .dataKey("courier_id")
                        .width("15%")
                        .build(),
                TableData.Column.builder()
                        .id("region")
                        .name("Region")
                        .dataKey("region")
                        .width("15%")
                        .build(),
                TableData.Column.builder()
                        .id("deliveries_per_day")
                        .name("Deliveries/Day")
                        .dataKey("deliveries_per_day")
                        .width("15%")
                        .align("right")
                        .cellFormat("%.1f")
                        .build(),
                TableData.Column.builder()
                        .id("on_time_rate")
                        .name("On-Time %")
                        .dataKey("on_time_rate")
                        .width("15%")
                        .align("right")
                        .cellFormat("%.1f%%")
                        .build(),
                TableData.Column.builder()
                        .id("customer_rating")
                        .name("Rating")
                        .dataKey("customer_rating")
                        .width("15%")
                        .align("right")
                        .cellFormat("%.1f")
                        .build(),
                TableData.Column.builder()
                        .id("performance_score")
                        .name("Score")
                        .dataKey("performance_score")
                        .width("15%")
                        .align("right")
                        .cellFormat("%.1f")
                        .build()
        );
        
        // Create rows for top couriers
        List<Map<String, Object>> topRows = new ArrayList<>();
        
        // Default data if no branches provided
        Object[][] topCourierData = {
                {1, "C-7842", "NAM", 38.4, 98.5, 4.9, 96.7},
                {2, "C-5623", "EUR", 36.7, 97.2, 4.8, 95.5},
                {3, "C-8201", "NAM", 35.3, 98.7, 4.7, 94.8},
                {4, "C-4198", "APAC", 34.9, 95.8, 4.8, 94.2},
                {5, "C-6572", "EUR", 34.2, 96.5, 4.9, 93.7}
        };
        
        for (Object[] data : topCourierData) {
            Map<String, Object> row = new HashMap<>();
            row.put("rank", data[0]);
            row.put("courier_id", data[1]);
            row.put("region", data[2]);
            row.put("deliveries_per_day", data[3]);
            row.put("on_time_rate", data[4]);
            row.put("customer_rating", data[5]);
            row.put("performance_score", data[6]);
            topRows.add(row);
        }
        
        // Create table for top couriers
        TableData topCouriersTable = TableData.builder()
                .id(UUID.randomUUID().toString())
                .title("Top Performing Couriers")
                .columns(courierColumns)
                .rows(topRows)
                .showHeader(true)
                .striped(true)
                .hoverable(true)
                .responsive(true)
                .build();
        
        section.addTable(topCouriersTable);
        
        // Create table for bottom couriers (using same columns)
        List<Map<String, Object>> bottomRows = new ArrayList<>();
        
        // Default data for bottom couriers
        Object[][] bottomCourierData = {
                {1, "C-3157", "AFR", 16.3, 78.2, 3.5, 68.4},
                {2, "C-2984", "APAC", 17.8, 82.5, 3.7, 70.2},
                {3, "C-5427", "LAT", 18.5, 83.7, 3.9, 72.5},
                {4, "C-7015", "APAC", 19.2, 84.5, 3.8, 73.8},
                {5, "C-4231", "AFR", 19.7, 85.2, 3.9, 74.3}
        };
        
        for (Object[] data : bottomCourierData) {
            Map<String, Object> row = new HashMap<>();
            row.put("rank", data[0]);
            row.put("courier_id", data[1]);
            row.put("region", data[2]);
            row.put("deliveries_per_day", data[3]);
            row.put("on_time_rate", data[4]);
            row.put("customer_rating", data[5]);
            row.put("performance_score", data[6]);
            bottomRows.add(row);
        }
        
        // Create table for bottom couriers
        TableData bottomCouriersTable = TableData.builder()
                .id(UUID.randomUUID().toString())
                .title("Couriers Needing Improvement")
                .columns(courierColumns)
                .rows(bottomRows)
                .showHeader(true)
                .striped(true)
                .hoverable(true)
                .responsive(true)
                .build();
        
        section.addTable(bottomCouriersTable);
        
        // Add radar chart for top vs bottom performer comparison
        ChartData performanceRadarChart = ChartData.builder()
                .id(UUID.randomUUID().toString())
                .title("Top vs Bottom Performer Comparison")
                .type(ChartData.ChartType.RADAR)
                .labels(Arrays.asList(
                        "Deliveries per Day", 
                        "On-Time Rate", 
                        "First Attempt Success", 
                        "Customer Rating", 
                        "Route Adherence", 
                        "Active Time %"))
                .series(List.of(
                        ChartData.DataSeries.builder()
                                .name("Top Performers")
                                .data(Arrays.asList(95, 98, 97, 96, 92, 94))
                                .backgroundColor("rgba(76, 175, 80, 0.2)")
                                .borderColor("#4CAF50")
                                .pointBackgroundColor("#4CAF50")
                                .build(),
                        ChartData.DataSeries.builder()
                                .name("Bottom Performers")
                                .data(Arrays.asList(60, 78, 72, 74, 68, 63))
                                .backgroundColor("rgba(244, 67, 54, 0.2)")
                                .borderColor("#F44336")
                                .pointBackgroundColor("#F44336")
                                .build(),
                        ChartData.DataSeries.builder()
                                .name("Average")
                                .data(Arrays.asList(80, 92, 87, 86, 84, 82))
                                .backgroundColor("rgba(33, 150, 243, 0.2)")
                                .borderColor("#2196F3")
                                .pointBackgroundColor("#2196F3")
                                .build()
                ))
                .showLegend(true)
                .build();
        
        section.addChart(performanceRadarChart);
        
        // Add experience vs performance chart
        ChartData experienceChart = ChartData.builder()
                .id(UUID.randomUUID().toString())
                .title("Experience vs Performance")
                .type(ChartData.ChartType.SCATTER)
                .labels(null) // Scatter plots don't use labels in the same way
                .series(List.of(
                        ChartData.DataSeries.builder()
                                .name("Couriers")
                                .data(Arrays.asList(
                                        Map.of("x", 1, "y", 72),
                                        Map.of("x", 2, "y", 78),
                                        Map.of("x", 3, "y", 81),
                                        Map.of("x", 5, "y", 85),
                                        Map.of("x", 6, "y", 87),
                                        Map.of("x", 8, "y", 89),
                                        Map.of("x", 12, "y", 91),
                                        Map.of("x", 18, "y", 92),
                                        Map.of("x", 24, "y", 93),
                                        Map.of("x", 30, "y", 94),
                                        Map.of("x", 36, "y", 94),
                                        Map.of("x", 42, "y", 95),
                                        Map.of("x", 48, "y", 95)
                                ))
                                .backgroundColor("rgba(33, 150, 243, 0.7)")
                                .borderColor("#2196F3")
                                .showPoint(true)
                                .build(),
                        ChartData.DataSeries.builder()
                                .name("Trend Line")
                                .data(Arrays.asList(
                                        Map.of("x", 0, "y", 70),
                                        Map.of("x", 48, "y", 95)
                                ))
                                .borderColor("#FF9800")
                                .borderDash(Arrays.asList(5, 5))
                                .showPoint(false)
                                .fill(false)
                                .build()
                ))
                .xAxisLabel("Experience (Months)")
                .yAxisLabel("Performance Score")
                .showLegend(true)
                .build();
        
        section.addChart(experienceChart);
        
        return section;
    }
    
    /**
     * Create the recommendations section
     */
    private ReportSection createRecommendationsSection() {
        ReportSection section = ReportSection.builder()
                .id(UUID.randomUUID().toString())
                .title("Recommendations for Productivity Improvement")
                .description("Key recommendations to enhance courier productivity and performance")
                .type(ReportSection.SectionType.RECOMMENDATIONS)
                .build();
        
        // Add recommendation content
        section.setHtmlContent(
                "<div class=\"recommendations-container\">" +
                "<h4>Immediate Recommendations (1-2 Months)</h4>" +
                "<ol>" +
                "<li><strong>Route Optimization Enhancement</strong><br/>" +
                "Implement improved route planning algorithms that account for historical delivery times and traffic patterns. " +
                "This could increase deliveries per day by 10-15%.</li>" +
                "<li><strong>Courier Skill Development Program</strong><br/>" +
                "Develop a training program based on best practices from top-performing couriers, focusing on time management " +
                "and customer interaction skills.</li>" +
                "<li><strong>Performance Incentive System</strong><br/>" +
                "Implement a tiered incentive structure based on deliveries per day and customer satisfaction scores.</li>" +
                "</ol>" +
                "<h4>Medium-Term Recommendations (3-6 Months)</h4>" +
                "<ol>" +
                "<li><strong>Delivery Density Optimization</strong><br/>" +
                "Reorganize delivery zones to increase delivery density and reduce transit time between stops.</li>" +
                "<li><strong>Mobile App Enhancement</strong><br/>" +
                "Upgrade the courier mobile app to provide real-time traffic alerts and optimal routing suggestions.</li>" +
                "<li><strong>Customer Communication Improvement</strong><br/>" +
                "Enhance the delivery notification system to reduce failed delivery attempts due to customer unavailability.</li>" +
                "</ol>" +
                "<h4>Long-Term Recommendations (6-12 Months)</h4>" +
                "<ol>" +
                "<li><strong>Advanced Analytics Program</strong><br/>" +
                "Implement predictive analytics to anticipate delivery challenges and optimize courier scheduling.</li>" +
                "<li><strong>Courier Career Pathway</strong><br/>" +
                "Develop a structured career progression plan for couriers to reduce turnover and increase retention of experienced staff.</li>" +
                "<li><strong>Cross-Regional Knowledge Sharing</strong><br/>" +
                "Establish a formal program for sharing best practices between high-performing and lower-performing regions.</li>" +
                "</ol>" +
                "</div>"
        );
        
        // Add expected impact content
        section.setConclusion(
                "Implementing these recommendations is projected to yield the following improvements:\n\n" +
                "• 18-22% increase in overall courier productivity\n" +
                "• 25-30% reduction in courier turnover rate\n" +
                "• 15-18% reduction in idle time\n" +
                "• 8-10% improvement in first-attempt delivery success\n\n" +
                "We recommend beginning with the route optimization enhancements and courier skill development program for the most immediate impact."
        );
        
        return section;
    }
    
    /**
     * Generate regional productivity data for charts
     */
    private List<Object> generateRegionalProductivityData(List<String> regions) {
        List<Object> data = new ArrayList<>();
        
        for (String region : regions) {
            double value;
            
            // Generate region-specific data
            switch (region) {
                case "NAM":
                    value = 27.8;
                    break;
                case "EUR":
                    value = 25.3;
                    break;
                case "APAC":
                    value = 23.5;
                    break;
                case "AFR":
                    value = 21.2;
                    break;
                case "LAT":
                    value = 22.7;
                    break;
                default:
                    // Random data for other regions
                    value = 20.0 + random.nextDouble() * 8.0;
                    break;
            }
            
            data.add(value);
        }
        
        return data;
    }
    
    /**
     * Generate a row for the regional efficiency table
     */
    private Map<String, Object> generateRegionalEfficiencyRow(String region) {
        Map<String, Object> row = new HashMap<>();
        row.put("region", region);
        
        // Generate region-specific data
        switch (region) {
            case "NAM":
                row.put("deliveries_per_day", 27.8);
                row.put("stops_per_hour", 3.5);
                row.put("on_time_rate", 94.5);
                row.put("first_attempt_success", 89.7);
                row.put("customer_rating", 4.7);
                break;
            case "EUR":
                row.put("deliveries_per_day", 25.3);
                row.put("stops_per_hour", 3.2);
                row.put("on_time_rate", 92.8);
                row.put("first_attempt_success", 87.5);
                row.put("customer_rating", 4.5);
                break;
            case "APAC":
                row.put("deliveries_per_day", 23.5);
                row.put("stops_per_hour", 3.0);
                row.put("on_time_rate", 87.3);
                row.put("first_attempt_success", 83.2);
                row.put("customer_rating", 4.2);
                break;
            case "AFR":
                row.put("deliveries_per_day", 21.2);
                row.put("stops_per_hour", 2.7);
                row.put("on_time_rate", 84.9);
                row.put("first_attempt_success", 80.5);
                row.put("customer_rating", 4.0);
                break;
            case "LAT":
                row.put("deliveries_per_day", 22.7);
                row.put("stops_per_hour", 2.9);
                row.put("on_time_rate", 89.2);
                row.put("first_attempt_success", 85.8);
                row.put("customer_rating", 4.3);
                break;
            default:
                // Random data for other regions
                row.put("deliveries_per_day", 20.0 + random.nextDouble() * 8.0);
                row.put("stops_per_hour", 2.5 + random.nextDouble() * 1.0);
                row.put("on_time_rate", 80.0 + random.nextDouble() * 15.0);
                row.put("first_attempt_success", 80.0 + random.nextDouble() * 10.0);
                row.put("customer_rating", 4.0 + random.nextDouble() * 0.8);
                break;
        }
        
        return row;
    }
}