package com.gogidix.courier.hqadmin.service.reporting;

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
 * Generator for performance summary reports
 */
@Slf4j
@Service
public class PerformanceReportGenerator implements ReportGenerator {

    private final TracingService tracingService;
    private final Random random = new Random();
    
    @Autowired
    public PerformanceReportGenerator(TracingService tracingService) {
        this.tracingService = tracingService;
    }

    @Override
    @Traced("PerformanceReportGenerator.generateReport")
    public AdvancedReport generateReport(
            LocalDateTime startDate,
            LocalDateTime endDate,
            List<String> regions,
            List<String> branches) {
        
        tracingService.addTag("startDate", startDate.toString());
        tracingService.addTag("endDate", endDate.toString());
        
        log.info("Generating performance summary report. Start: {}, End: {}", startDate, endDate);
        
        // Create the report
        AdvancedReport report = AdvancedReport.builder()
                .reportId(UUID.randomUUID().toString())
                .title("Performance Summary Report")
                .description("Comprehensive summary of key performance metrics across regions and branches")
                .type(AdvancedReport.ReportType.PERFORMANCE_SUMMARY)
                .generatedAt(LocalDateTime.now())
                .startDate(startDate)
                .endDate(endDate)
                .regions(regions)
                .branches(branches)
                .sections(new ArrayList<>())
                .build();
        
        // Add report sections
        report.addSection(createExecutiveSummarySection(startDate, endDate));
        report.addSection(createKeyMetricsSection(startDate, endDate));
        report.addSection(createRegionalPerformanceSection(startDate, endDate, regions));
        report.addSection(createBranchPerformanceSection(startDate, endDate, branches));
        report.addSection(createTrendAnalysisSection(startDate, endDate));
        
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
                .description("Key highlights and insights for the period " + 
                        startDate.format(formatter) + " to " + endDate.format(formatter))
                .type(ReportSection.SectionType.SUMMARY)
                .metrics(new ArrayList<>())
                .htmlContent("<p>During this period, the overall performance shows a <strong>7.2% improvement</strong> " +
                        "in delivery efficiency across all regions. The average delivery time has decreased by " +
                        "<strong>12 minutes</strong>, resulting in higher customer satisfaction scores.</p>" +
                        "<p>Key achievements:</p>" +
                        "<ul>" +
                        "<li>Successfully completed 92.8% of deliveries within the promised time window</li>" +
                        "<li>Reduced courier idle time by 15.3% through optimized scheduling</li>" +
                        "<li>Decreased delivery cancellations by 22.1% compared to the previous period</li>" +
                        "</ul>" +
                        "<p>Areas for improvement:</p>" +
                        "<ul>" +
                        "<li>The APAC region is experiencing higher than average delivery times</li>" +
                        "<li>Weekend staffing levels need optimization in the EUR region</li>" +
                        "<li>Vehicle maintenance costs are 12.3% above target</li>" +
                        "</ul>")
                .conclusion("Overall, the courier services are showing positive trends, with significant " +
                        "improvements in efficiency metrics. The recommendations outlined in this report " +
                        "aim to address the identified challenges and further enhance performance.")
                .build();
        
        // Add summary metrics
        section.addMetric(ReportMetric.builder()
                .name("overall_score")
                .displayName("Overall Performance Score")
                .value(87.5)
                .unit("")
                .changeValue(7.2)
                .formatPrecision(1)
                .status("good")
                .build());
        
        section.addMetric(ReportMetric.builder()
                .name("on_time_delivery")
                .displayName("On-Time Delivery Rate")
                .value(92.8)
                .unit("%")
                .changeValue(3.5)
                .formatPrecision(1)
                .status("good")
                .build());
        
        section.addMetric(ReportMetric.builder()
                .name("avg_delivery_time")
                .displayName("Avg. Delivery Time")
                .value(27.5)
                .unit("min")
                .changeValue(-12.0)
                .formatPrecision(1)
                .status("good")
                .build());
        
        section.addMetric(ReportMetric.builder()
                .name("customer_satisfaction")
                .displayName("Customer Satisfaction")
                .value(4.6)
                .unit("/5")
                .changeValue(0.4)
                .formatPrecision(1)
                .status("good")
                .build());
        
        return section;
    }
    
    /**
     * Create the key metrics section
     */
    private ReportSection createKeyMetricsSection(LocalDateTime startDate, LocalDateTime endDate) {
        ReportSection section = ReportSection.builder()
                .id(UUID.randomUUID().toString())
                .title("Key Performance Metrics")
                .description("Analysis of critical performance indicators across the delivery network")
                .type(ReportSection.SectionType.KEY_METRICS)
                .metrics(new ArrayList<>())
                .charts(new ArrayList<>())
                .build();
        
        // Add metrics
        section.addMetric(ReportMetric.builder()
                .name("total_deliveries")
                .displayName("Total Deliveries")
                .value(152487)
                .changeValue(12543)
                .changePercentage(8.9)
                .formatPrecision(0)
                .status("good")
                .build());
        
        section.addMetric(ReportMetric.builder()
                .name("successful_deliveries")
                .displayName("Successful Deliveries")
                .value(148924)
                .unit("")
                .changeValue(13298)
                .changePercentage(9.8)
                .formatPrecision(0)
                .status("good")
                .build());
        
        section.addMetric(ReportMetric.builder()
                .name("failed_deliveries")
                .displayName("Failed Deliveries")
                .value(3563)
                .unit("")
                .changeValue(-755)
                .changePercentage(-17.5)
                .formatPrecision(0)
                .status("good")
                .build());
        
        section.addMetric(ReportMetric.builder()
                .name("avg_deliveries_per_courier")
                .displayName("Avg. Deliveries per Courier")
                .value(24.7)
                .unit("per day")
                .changeValue(2.3)
                .changePercentage(10.3)
                .formatPrecision(1)
                .status("good")
                .build());
        
        section.addMetric(ReportMetric.builder()
                .name("avg_delivery_distance")
                .displayName("Avg. Delivery Distance")
                .value(4.8)
                .unit("km")
                .changeValue(-0.3)
                .changePercentage(-5.9)
                .formatPrecision(1)
                .status("good")
                .build());
        
        section.addMetric(ReportMetric.builder()
                .name("avg_route_efficiency")
                .displayName("Route Efficiency")
                .value(83.6)
                .unit("%")
                .changeValue(5.8)
                .changePercentage(7.5)
                .formatPrecision(1)
                .status("good")
                .build());
        
        section.addMetric(ReportMetric.builder()
                .name("fuel_consumption")
                .displayName("Fuel Consumption")
                .value(0.42)
                .unit("L/km")
                .changeValue(-0.03)
                .changePercentage(-6.7)
                .formatPrecision(2)
                .status("good")
                .build());
        
        section.addMetric(ReportMetric.builder()
                .name("maintenance_cost")
                .displayName("Maintenance Cost")
                .value(0.58)
                .unit("$/km")
                .changeValue(0.06)
                .changePercentage(11.5)
                .formatPrecision(2)
                .status("warning")
                .build());
        
        // Add chart for delivery success rates
        ChartData deliverySuccessChart = ChartData.builder()
                .id(UUID.randomUUID().toString())
                .title("Delivery Success Rates")
                .type(ChartData.ChartType.PIE)
                .labels(Arrays.asList("Successful", "Failed", "Rescheduled", "Returned"))
                .series(List.of(
                        ChartData.DataSeries.builder()
                                .name("Delivery Outcomes")
                                .data(Arrays.asList(97.7, 1.2, 0.8, 0.3))
                                .backgroundColor(Arrays.asList("#4CAF50", "#F44336", "#FFC107", "#2196F3"))
                                .build()
                ))
                .showLegend(true)
                .build();
        
        section.addChart(deliverySuccessChart);
        
        // Add chart for courier utilization
        ChartData courierUtilizationChart = ChartData.builder()
                .id(UUID.randomUUID().toString())
                .title("Courier Utilization")
                .type(ChartData.ChartType.BAR)
                .labels(Arrays.asList("NAM", "EUR", "APAC", "AFR", "LAT"))
                .xAxisLabel("Region")
                .yAxisLabel("Utilization %")
                .series(List.of(
                        ChartData.DataSeries.builder()
                                .name("Current Period")
                                .data(Arrays.asList(87, 82, 76, 81, 85))
                                .backgroundColor("#4CAF50")
                                .build(),
                        ChartData.DataSeries.builder()
                                .name("Previous Period")
                                .data(Arrays.asList(78, 80, 72, 75, 76))
                                .backgroundColor("#2196F3")
                                .build(),
                        ChartData.DataSeries.builder()
                                .name("Target")
                                .data(Arrays.asList(85, 85, 85, 85, 85))
                                .backgroundColor("rgba(255, 193, 7, 0.5)")
                                .borderColor("#FFC107")
                                .build()
                ))
                .showLegend(true)
                .build();
        
        section.addChart(courierUtilizationChart);
        
        return section;
    }
    
    /**
     * Create the regional performance section
     */
    private ReportSection createRegionalPerformanceSection(
            LocalDateTime startDate, 
            LocalDateTime endDate, 
            List<String> regions) {
        
        ReportSection section = ReportSection.builder()
                .id(UUID.randomUUID().toString())
                .title("Regional Performance Analysis")
                .description("Comparison of key metrics across different regions")
                .type(ReportSection.SectionType.COMPARISON)
                .tables(new ArrayList<>())
                .charts(new ArrayList<>())
                .build();
        
        // Create table columns
        List<TableData.Column> columns = Arrays.asList(
                TableData.Column.builder()
                        .id("region")
                        .name("Region")
                        .dataKey("region")
                        .sortable(true)
                        .build(),
                TableData.Column.builder()
                        .id("deliveries")
                        .name("Total Deliveries")
                        .dataKey("deliveries")
                        .sortable(true)
                        .align("right")
                        .build(),
                TableData.Column.builder()
                        .id("on_time_rate")
                        .name("On-Time Rate (%)")
                        .dataKey("on_time_rate")
                        .sortable(true)
                        .align("right")
                        .cellFormat("%.1f%%")
                        .conditionalFormats(Arrays.asList(
                                TableData.ConditionalFormat.builder()
                                        .condition("value < 80")
                                        .backgroundColor("#FFEBEE")
                                        .textColor("#D32F2F")
                                        .build(),
                                TableData.ConditionalFormat.builder()
                                        .condition("value >= 80 && value < 90")
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
                        .id("avg_time")
                        .name("Avg. Delivery Time (min)")
                        .dataKey("avg_time")
                        .sortable(true)
                        .align("right")
                        .cellFormat("%.1f")
                        .build(),
                TableData.Column.builder()
                        .id("cost_per_delivery")
                        .name("Cost per Delivery ($)")
                        .dataKey("cost_per_delivery")
                        .sortable(true)
                        .align("right")
                        .cellFormat("$%.2f")
                        .build(),
                TableData.Column.builder()
                        .id("customer_satisfaction")
                        .name("Customer Satisfaction")
                        .dataKey("customer_satisfaction")
                        .sortable(true)
                        .align("right")
                        .cellFormat("%.1f/5")
                        .build()
        );
        
        // Create table rows
        List<Map<String, Object>> rows = new ArrayList<>();
        
        // Default regions if none provided
        if (regions == null || regions.isEmpty()) {
            regions = Arrays.asList("NAM", "EUR", "APAC", "AFR", "LAT");
        }
        
        // Generate data for each region
        for (String region : regions) {
            Map<String, Object> row = new HashMap<>();
            row.put("region", region);
            
            // Generate realistic but randomized data
            switch (region) {
                case "NAM":
                    row.put("deliveries", 42678);
                    row.put("on_time_rate", 94.5);
                    row.put("avg_time", 25.3);
                    row.put("cost_per_delivery", 4.82);
                    row.put("customer_satisfaction", 4.7);
                    break;
                case "EUR":
                    row.put("deliveries", 38452);
                    row.put("on_time_rate", 92.8);
                    row.put("avg_time", 27.1);
                    row.put("cost_per_delivery", 5.12);
                    row.put("customer_satisfaction", 4.5);
                    break;
                case "APAC":
                    row.put("deliveries", 45219);
                    row.put("on_time_rate", 87.3);
                    row.put("avg_time", 32.7);
                    row.put("cost_per_delivery", 4.28);
                    row.put("customer_satisfaction", 4.2);
                    break;
                case "AFR":
                    row.put("deliveries", 12754);
                    row.put("on_time_rate", 84.9);
                    row.put("avg_time", 35.4);
                    row.put("cost_per_delivery", 5.76);
                    row.put("customer_satisfaction", 4.0);
                    break;
                case "LAT":
                    row.put("deliveries", 13384);
                    row.put("on_time_rate", 89.2);
                    row.put("avg_time", 30.8);
                    row.put("cost_per_delivery", 4.95);
                    row.put("customer_satisfaction", 4.3);
                    break;
                default:
                    // Generate random data for any other region
                    row.put("deliveries", 10000 + random.nextInt(40000));
                    row.put("on_time_rate", 80.0 + random.nextDouble() * 15.0);
                    row.put("avg_time", 25.0 + random.nextDouble() * 15.0);
                    row.put("cost_per_delivery", 4.0 + random.nextDouble() * 2.0);
                    row.put("customer_satisfaction", 3.5 + random.nextDouble() * 1.5);
                    break;
            }
            
            rows.add(row);
        }
        
        // Create the table
        TableData regionalTable = TableData.builder()
                .id(UUID.randomUUID().toString())
                .title("Regional Performance Metrics")
                .columns(columns)
                .rows(rows)
                .showHeader(true)
                .striped(true)
                .hoverable(true)
                .bordered(true)
                .responsive(true)
                .build();
        
        section.addTable(regionalTable);
        
        // Add radar chart for regional comparison
        ChartData regionalRadarChart = ChartData.builder()
                .id(UUID.randomUUID().toString())
                .title("Regional Performance Comparison")
                .type(ChartData.ChartType.RADAR)
                .labels(Arrays.asList("On-Time Rate", "Cost Efficiency", "Delivery Speed", "Customer Satisfaction", "Courier Utilization"))
                .series(
                        regions.stream()
                                .map(region -> {
                                    // Values scaled from 0-100 for radar chart
                                    List<Object> data;
                                    switch (region) {
                                        case "NAM":
                                            data = Arrays.asList(94, 84, 90, 94, 87);
                                            break;
                                        case "EUR":
                                            data = Arrays.asList(93, 80, 85, 90, 82);
                                            break;
                                        case "APAC":
                                            data = Arrays.asList(87, 92, 75, 84, 76);
                                            break;
                                        case "AFR":
                                            data = Arrays.asList(85, 72, 72, 80, 81);
                                            break;
                                        case "LAT":
                                            data = Arrays.asList(89, 82, 78, 86, 85);
                                            break;
                                        default:
                                            data = Arrays.asList(
                                                    80 + random.nextInt(15),
                                                    70 + random.nextInt(20),
                                                    75 + random.nextInt(15),
                                                    80 + random.nextInt(15),
                                                    75 + random.nextInt(15)
                                            );
                                            break;
                                    }
                                    
                                    return ChartData.DataSeries.builder()
                                            .name(region)
                                            .data(data)
                                            .build();
                                })
                                .toList()
                )
                .showLegend(true)
                .build();
        
        section.addChart(regionalRadarChart);
        
        return section;
    }
    
    /**
     * Create the branch performance section
     */
    private ReportSection createBranchPerformanceSection(
            LocalDateTime startDate, 
            LocalDateTime endDate, 
            List<String> branches) {
        
        ReportSection section = ReportSection.builder()
                .id(UUID.randomUUID().toString())
                .title("Branch Performance Analysis")
                .description("Top and bottom performing branches across key metrics")
                .type(ReportSection.SectionType.COMPARISON)
                .tables(new ArrayList<>())
                .charts(new ArrayList<>())
                .build();
        
        // Create table columns for top branches
        List<TableData.Column> topColumns = Arrays.asList(
                TableData.Column.builder()
                        .id("rank")
                        .name("Rank")
                        .dataKey("rank")
                        .width("10%")
                        .align("center")
                        .build(),
                TableData.Column.builder()
                        .id("branch")
                        .name("Branch")
                        .dataKey("branch")
                        .width("20%")
                        .build(),
                TableData.Column.builder()
                        .id("region")
                        .name("Region")
                        .dataKey("region")
                        .width("15%")
                        .build(),
                TableData.Column.builder()
                        .id("performance_score")
                        .name("Performance Score")
                        .dataKey("performance_score")
                        .width("15%")
                        .align("right")
                        .cellFormat("%.1f")
                        .build(),
                TableData.Column.builder()
                        .id("change")
                        .name("Change")
                        .dataKey("change")
                        .width("15%")
                        .align("right")
                        .cellFormat("%+.1f%%")
                        .conditionalFormats(Arrays.asList(
                                TableData.ConditionalFormat.builder()
                                        .condition("value < 0")
                                        .textColor("#D32F2F")
                                        .build(),
                                TableData.ConditionalFormat.builder()
                                        .condition("value > 0")
                                        .textColor("#388E3C")
                                        .build()
                        ))
                        .build(),
                TableData.Column.builder()
                        .id("key_strength")
                        .name("Key Strength")
                        .dataKey("key_strength")
                        .width("25%")
                        .build()
        );
        
        // Create rows for top branches
        List<Map<String, Object>> topRows = new ArrayList<>();
        
        // Default data if no branches provided
        String[][] topBranchData = {
                {"1", "New York Central", "NAM", "95.8", "+7.2", "Delivery Speed"},
                {"2", "London West", "EUR", "94.6", "+5.8", "Customer Satisfaction"},
                {"3", "Toronto Downtown", "NAM", "93.9", "+6.1", "Route Optimization"},
                {"4", "Sydney CBD", "APAC", "93.1", "+8.5", "Cost Efficiency"},
                {"5", "Berlin Central", "EUR", "92.4", "+4.9", "Courier Utilization"}
        };
        
        for (String[] data : topBranchData) {
            Map<String, Object> row = new HashMap<>();
            row.put("rank", data[0]);
            row.put("branch", data[1]);
            row.put("region", data[2]);
            row.put("performance_score", Double.parseDouble(data[3]));
            row.put("change", Double.parseDouble(data[4]));
            row.put("key_strength", data[5]);
            topRows.add(row);
        }
        
        // Create table for top branches
        TableData topBranchesTable = TableData.builder()
                .id(UUID.randomUUID().toString())
                .title("Top Performing Branches")
                .columns(topColumns)
                .rows(topRows)
                .showHeader(true)
                .striped(true)
                .hoverable(true)
                .bordered(true)
                .responsive(true)
                .build();
        
        section.addTable(topBranchesTable);
        
        // Create columns for bottom branches
        List<TableData.Column> bottomColumns = topColumns;
        
        // Create rows for bottom branches
        List<Map<String, Object>> bottomRows = new ArrayList<>();
        
        // Default data for bottom branches
        String[][] bottomBranchData = {
                {"1", "Lagos South", "AFR", "75.2", "-1.3", "Vehicle Maintenance"},
                {"2", "Mumbai East", "APAC", "76.8", "+2.1", "Traffic Congestion"},
                {"3", "Mexico City Central", "LAT", "78.5", "+3.2", "Staff Turnover"},
                {"4", "Jakarta North", "APAC", "79.7", "+0.8", "Weather Adaptability"},
                {"5", "Cairo Downtown", "AFR", "80.2", "+2.5", "Address Accuracy"}
        };
        
        for (String[] data : bottomBranchData) {
            Map<String, Object> row = new HashMap<>();
            row.put("rank", data[0]);
            row.put("branch", data[1]);
            row.put("region", data[2]);
            row.put("performance_score", Double.parseDouble(data[3]));
            row.put("change", Double.parseDouble(data[4]));
            row.put("key_strength", data[5]);
            bottomRows.add(row);
        }
        
        // Create table for bottom branches
        TableData bottomBranchesTable = TableData.builder()
                .id(UUID.randomUUID().toString())
                .title("Branches Needing Improvement")
                .columns(bottomColumns)
                .rows(bottomRows)
                .showHeader(true)
                .striped(true)
                .hoverable(true)
                .bordered(true)
                .responsive(true)
                .build();
        
        section.addTable(bottomBranchesTable);
        
        // Add horizontal bar chart for branch comparison
        ChartData branchComparisonChart = ChartData.builder()
                .id(UUID.randomUUID().toString())
                .title("Top vs. Bottom Branches - Performance Metrics")
                .type(ChartData.ChartType.HORIZONTAL_BAR)
                .labels(Arrays.asList("On-Time %", "Avg Time (min)", "Cost Efficiency", "Courier Utilization", "Customer Rating"))
                .series(List.of(
                        ChartData.DataSeries.builder()
                                .name("Top Branch Avg")
                                .data(Arrays.asList(94.5, 23.5, 92.8, 91.2, 4.8))
                                .backgroundColor("#4CAF50")
                                .build(),
                        ChartData.DataSeries.builder()
                                .name("Bottom Branch Avg")
                                .data(Arrays.asList(78.6, 37.8, 75.4, 77.2, 3.9))
                                .backgroundColor("#F44336")
                                .build(),
                        ChartData.DataSeries.builder()
                                .name("Target")
                                .data(Arrays.asList(90.0, 25.0, 85.0, 85.0, 4.5))
                                .backgroundColor("rgba(255, 193, 7, 0.5)")
                                .borderColor("#FFC107")
                                .build()
                ))
                .xAxisLabel("Score")
                .showLegend(true)
                .build();
        
        section.addChart(branchComparisonChart);
        
        return section;
    }
    
    /**
     * Create the trend analysis section
     */
    private ReportSection createTrendAnalysisSection(LocalDateTime startDate, LocalDateTime endDate) {
        ReportSection section = ReportSection.builder()
                .id(UUID.randomUUID().toString())
                .title("Performance Trend Analysis")
                .description("Analysis of key performance metrics over time")
                .type(ReportSection.SectionType.TREND_ANALYSIS)
                .charts(new ArrayList<>())
                .build();
        
        // Generate realistic month labels
        List<String> monthLabels = List.of("Jan", "Feb", "Mar", "Apr", "May", "Jun");
        
        // Add line chart for on-time delivery trends
        ChartData onTimeDeliveryChart = ChartData.builder()
                .id(UUID.randomUUID().toString())
                .title("On-Time Delivery Trends")
                .type(ChartData.ChartType.LINE)
                .labels(monthLabels)
                .series(List.of(
                        ChartData.DataSeries.builder()
                                .name("NAM")
                                .data(Arrays.asList(88.5, 89.2, 91.5, 92.8, 93.4, 94.5))
                                .borderColor("#2196F3")
                                .backgroundColor("rgba(33, 150, 243, 0.1)")
                                .fill(true)
                                .build(),
                        ChartData.DataSeries.builder()
                                .name("EUR")
                                .data(Arrays.asList(87.2, 88.0, 89.6, 90.5, 91.8, 92.8))
                                .borderColor("#4CAF50")
                                .backgroundColor("rgba(76, 175, 80, 0.1)")
                                .fill(true)
                                .build(),
                        ChartData.DataSeries.builder()
                                .name("APAC")
                                .data(Arrays.asList(81.5, 82.4, 83.8, 85.1, 86.2, 87.3))
                                .borderColor("#FFC107")
                                .backgroundColor("rgba(255, 193, 7, 0.1)")
                                .fill(true)
                                .build()
                ))
                .xAxisLabel("Month")
                .yAxisLabel("On-Time Delivery (%)")
                .showLegend(true)
                .build();
        
        section.addChart(onTimeDeliveryChart);
        
        // Add line chart for average delivery time trends
        ChartData deliveryTimeChart = ChartData.builder()
                .id(UUID.randomUUID().toString())
                .title("Average Delivery Time Trends")
                .type(ChartData.ChartType.LINE)
                .labels(monthLabels)
                .series(List.of(
                        ChartData.DataSeries.builder()
                                .name("NAM")
                                .data(Arrays.asList(30.2, 29.1, 27.8, 26.5, 25.7, 25.3))
                                .borderColor("#2196F3")
                                .build(),
                        ChartData.DataSeries.builder()
                                .name("EUR")
                                .data(Arrays.asList(32.4, 31.2, 30.1, 29.0, 28.1, 27.1))
                                .borderColor("#4CAF50")
                                .build(),
                        ChartData.DataSeries.builder()
                                .name("APAC")
                                .data(Arrays.asList(38.7, 37.5, 36.2, 34.9, 33.5, 32.7))
                                .borderColor("#FFC107")
                                .build(),
                        ChartData.DataSeries.builder()
                                .name("Target")
                                .data(Arrays.asList(30.0, 30.0, 30.0, 30.0, 30.0, 30.0))
                                .borderColor("#F44336")
                                .dashStyle("dash")
                                .build()
                ))
                .xAxisLabel("Month")
                .yAxisLabel("Average Delivery Time (min)")
                .showLegend(true)
                .build();
        
        section.addChart(deliveryTimeChart);
        
        // Add stacked bar chart for delivery volumes
        ChartData deliveryVolumeChart = ChartData.builder()
                .id(UUID.randomUUID().toString())
                .title("Delivery Volume Trends")
                .type(ChartData.ChartType.STACKED_BAR)
                .labels(monthLabels)
                .series(List.of(
                        ChartData.DataSeries.builder()
                                .name("NAM")
                                .data(Arrays.asList(6428, 6721, 7213, 7845, 8126, 8345))
                                .backgroundColor("#2196F3")
                                .stackGroup("region")
                                .build(),
                        ChartData.DataSeries.builder()
                                .name("EUR")
                                .data(Arrays.asList(5842, 6054, 6321, 6578, 6892, 7215))
                                .backgroundColor("#4CAF50")
                                .stackGroup("region")
                                .build(),
                        ChartData.DataSeries.builder()
                                .name("APAC")
                                .data(Arrays.asList(6984, 7245, 7612, 7948, 8256, 8674))
                                .backgroundColor("#FFC107")
                                .stackGroup("region")
                                .build(),
                        ChartData.DataSeries.builder()
                                .name("AFR")
                                .data(Arrays.asList(1845, 1932, 2054, 2138, 2245, 2342))
                                .backgroundColor("#9C27B0")
                                .stackGroup("region")
                                .build(),
                        ChartData.DataSeries.builder()
                                .name("LAT")
                                .data(Arrays.asList(1986, 2087, 2145, 2254, 2351, 2458))
                                .backgroundColor("#FF5722")
                                .stackGroup("region")
                                .build()
                ))
                .xAxisLabel("Month")
                .yAxisLabel("Deliveries")
                .showLegend(true)
                .build();
        
        section.addChart(deliveryVolumeChart);
        
        return section;
    }
}