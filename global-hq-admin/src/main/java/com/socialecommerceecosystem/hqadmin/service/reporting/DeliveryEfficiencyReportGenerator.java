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
 * Generator for delivery efficiency reports that focus on time, cost, and quality metrics
 */
@Slf4j
@Service
public class DeliveryEfficiencyReportGenerator implements ReportGenerator {

    private final TracingService tracingService;
    private final Random random = new Random();
    
    @Autowired
    public DeliveryEfficiencyReportGenerator(TracingService tracingService) {
        this.tracingService = tracingService;
    }

    @Override
    @Traced("DeliveryEfficiencyReportGenerator.generateReport")
    public AdvancedReport generateReport(
            LocalDateTime startDate,
            LocalDateTime endDate,
            List<String> regions,
            List<String> branches) {
        
        tracingService.addTag("startDate", startDate.toString());
        tracingService.addTag("endDate", endDate.toString());
        
        log.info("Generating delivery efficiency report. Start: {}, End: {}", startDate, endDate);
        
        // Create the report
        AdvancedReport report = AdvancedReport.builder()
                .reportId(UUID.randomUUID().toString())
                .title("Delivery Efficiency Report")
                .description("Comprehensive analysis of delivery efficiency metrics and optimization opportunities")
                .type(AdvancedReport.ReportType.DELIVERY_EFFICIENCY)
                .generatedAt(LocalDateTime.now())
                .startDate(startDate)
                .endDate(endDate)
                .regions(regions)
                .branches(branches)
                .sections(new ArrayList<>())
                .build();
        
        // Add report sections
        report.addSection(createExecutiveSummarySection(startDate, endDate));
        report.addSection(createTimeEfficiencySection(startDate, endDate, regions));
        report.addSection(createCostEfficiencySection(startDate, endDate, regions));
        report.addSection(createRouteOptimizationSection(startDate, endDate));
        report.addSection(createDeliveryPerformanceSection(startDate, endDate, branches));
        report.addSection(createOptimizationRecommendationsSection());
        
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
                .description("Key delivery efficiency highlights for the period " + 
                        startDate.format(formatter) + " to " + endDate.format(formatter))
                .type(ReportSection.SectionType.SUMMARY)
                .metrics(new ArrayList<>())
                .htmlContent("<p>This report analyzes delivery efficiency across the network, identifying " +
                        "optimization opportunities and bottlenecks. Key findings include:</p>" +
                        "<ul>" +
                        "<li>Overall delivery time has decreased by <strong>8.2%</strong> compared to the previous period</li>" +
                        "<li>Cost per delivery was reduced by <strong>6.3%</strong> through route optimization</li>" +
                        "<li>First-attempt delivery success rate improved by <strong>5.7%</strong></li>" +
                        "<li>Peak traffic avoidance has improved by <strong>12.4%</strong> through scheduling adjustments</li>" +
                        "</ul>" +
                        "<p>Areas needing improvement:</p>" +
                        "<ul>" +
                        "<li>High-density urban areas still face significant delivery time variance</li>" +
                        "<li>Inefficient routing in 27% of rural deliveries</li>" +
                        "<li>Weather-related delays account for 8.3% of late deliveries</li>" +
                        "</ul>")
                .conclusion("By implementing the recommendations in this report, we project a potential " +
                        "improvement of 12-15% in overall delivery efficiency and a cost reduction of 7-9%.")
                .build();
        
        // Add summary metrics
        section.addMetric(ReportMetric.builder()
                .name("avg_delivery_time")
                .displayName("Avg. Delivery Time")
                .value(28.5)
                .unit("min")
                .changeValue(-2.5)
                .changePercentage(-8.2)
                .formatPrecision(1)
                .status("good")
                .build());
        
        section.addMetric(ReportMetric.builder()
                .name("cost_per_delivery")
                .displayName("Cost per Delivery")
                .value(4.82)
                .unit("$")
                .changeValue(-0.32)
                .changePercentage(-6.3)
                .formatPrecision(2)
                .status("good")
                .build());
        
        section.addMetric(ReportMetric.builder()
                .name("delivery_success_rate")
                .displayName("First Attempt Success")
                .value(87.5)
                .unit("%")
                .changeValue(5.7)
                .changePercentage(6.9)
                .formatPrecision(1)
                .status("good")
                .build());
        
        section.addMetric(ReportMetric.builder()
                .name("route_efficiency")
                .displayName("Route Efficiency")
                .value(83.8)
                .unit("%")
                .changeValue(4.2)
                .changePercentage(5.3)
                .formatPrecision(1)
                .status("good")
                .build());
        
        return section;
    }
    
    /**
     * Create the time efficiency section
     */
    private ReportSection createTimeEfficiencySection(
            LocalDateTime startDate, 
            LocalDateTime endDate,
            List<String> regions) {
        
        ReportSection section = ReportSection.builder()
                .id(UUID.randomUUID().toString())
                .title("Time Efficiency Analysis")
                .description("Analysis of delivery time metrics and opportunities for optimization")
                .type(ReportSection.SectionType.ANALYSIS)
                .metrics(new ArrayList<>())
                .charts(new ArrayList<>())
                .build();
        
        // Add metrics
        section.addMetric(ReportMetric.builder()
                .name("avg_pickup_time")
                .displayName("Avg. Pickup Time")
                .value(4.8)
                .unit("min")
                .changeValue(-0.7)
                .changePercentage(-12.7)
                .formatPrecision(1)
                .status("good")
                .build());
        
        section.addMetric(ReportMetric.builder()
                .name("avg_transit_time")
                .displayName("Avg. Transit Time")
                .value(18.4)
                .unit("min")
                .changeValue(-1.2)
                .changePercentage(-6.1)
                .formatPrecision(1)
                .status("good")
                .build());
        
        section.addMetric(ReportMetric.builder()
                .name("avg_delivery_time")
                .displayName("Avg. Delivery Time")
                .value(5.3)
                .unit("min")
                .changeValue(-0.6)
                .changePercentage(-10.2)
                .formatPrecision(1)
                .status("good")
                .build());
        
        section.addMetric(ReportMetric.builder()
                .name("avg_wait_time")
                .displayName("Avg. Wait Time")
                .value(2.7)
                .unit("min")
                .changeValue(-0.9)
                .changePercentage(-25.0)
                .formatPrecision(1)
                .status("good")
                .build());
        
        section.addMetric(ReportMetric.builder()
                .name("peak_avoidance")
                .displayName("Peak Traffic Avoidance")
                .value(75.4)
                .unit("%")
                .changeValue(8.3)
                .changePercentage(12.4)
                .formatPrecision(1)
                .status("good")
                .build());
        
        // Add time breakdown chart
        ChartData timeBreakdownChart = ChartData.builder()
                .id(UUID.randomUUID().toString())
                .title("Delivery Time Breakdown")
                .type(ChartData.ChartType.PIE)
                .labels(Arrays.asList("Pickup", "Transit", "Delivery", "Wait", "Other"))
                .series(List.of(
                        ChartData.DataSeries.builder()
                                .name("Time Components")
                                .data(Arrays.asList(15.5, 59.3, 17.1, 5.8, 2.3))
                                .backgroundColor(Arrays.asList(
                                        "#4CAF50", "#2196F3", "#FF9800", "#9C27B0", "#607D8B"))
                                .build()
                ))
                .showLegend(true)
                .build();
        
        section.addChart(timeBreakdownChart);
        
        // Add regional time comparison chart
        List<String> defaultRegions = (regions != null && !regions.isEmpty()) 
                ? regions 
                : Arrays.asList("NAM", "EUR", "APAC", "AFR", "LAT");
        
        ChartData regionalTimeChart = ChartData.builder()
                .id(UUID.randomUUID().toString())
                .title("Regional Time Efficiency Comparison")
                .type(ChartData.ChartType.BAR)
                .labels(defaultRegions)
                .series(List.of(
                        ChartData.DataSeries.builder()
                                .name("Current Period")
                                .data(generateRegionalTimeData(defaultRegions, false))
                                .backgroundColor("#4CAF50")
                                .build(),
                        ChartData.DataSeries.builder()
                                .name("Previous Period")
                                .data(generateRegionalTimeData(defaultRegions, true))
                                .backgroundColor("#2196F3")
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
                .yAxisLabel("Avg. Delivery Time (min)")
                .showLegend(true)
                .build();
        
        section.addChart(regionalTimeChart);
        
        // Add delivery time distribution chart
        ChartData timeDistributionChart = ChartData.builder()
                .id(UUID.randomUUID().toString())
                .title("Delivery Time Distribution")
                .type(ChartData.ChartType.BAR)
                .labels(Arrays.asList("<15 min", "15-20 min", "20-25 min", "25-30 min", "30-40 min", "40-60 min", ">60 min"))
                .series(List.of(
                        ChartData.DataSeries.builder()
                                .name("Percentage of Deliveries")
                                .data(Arrays.asList(12.5, 22.8, 29.3, 18.7, 9.5, 5.8, 1.4))
                                .backgroundColor(Arrays.asList(
                                        "#4CAF50", "#8BC34A", "#CDDC39", "#FFEB3B", 
                                        "#FFC107", "#FF9800", "#F44336"))
                                .build()
                ))
                .xAxisLabel("Time Range")
                .yAxisLabel("% of Deliveries")
                .showLegend(false)
                .build();
        
        section.addChart(timeDistributionChart);
        
        return section;
    }
    
    /**
     * Create the cost efficiency section
     */
    private ReportSection createCostEfficiencySection(
            LocalDateTime startDate, 
            LocalDateTime endDate,
            List<String> regions) {
        
        ReportSection section = ReportSection.builder()
                .id(UUID.randomUUID().toString())
                .title("Cost Efficiency Analysis")
                .description("Analysis of delivery cost components and cost reduction opportunities")
                .type(ReportSection.SectionType.ANALYSIS)
                .metrics(new ArrayList<>())
                .charts(new ArrayList<>())
                .build();
        
        // Add metrics
        section.addMetric(ReportMetric.builder()
                .name("cost_per_delivery")
                .displayName("Cost per Delivery")
                .value(4.82)
                .unit("$")
                .changeValue(-0.32)
                .changePercentage(-6.3)
                .formatPrecision(2)
                .status("good")
                .build());
        
        section.addMetric(ReportMetric.builder()
                .name("fuel_cost")
                .displayName("Fuel Cost")
                .value(1.28)
                .unit("$/delivery")
                .changeValue(-0.12)
                .changePercentage(-8.6)
                .formatPrecision(2)
                .status("good")
                .build());
        
        section.addMetric(ReportMetric.builder()
                .name("labor_cost")
                .displayName("Labor Cost")
                .value(2.67)
                .unit("$/delivery")
                .changeValue(-0.15)
                .changePercentage(-5.3)
                .formatPrecision(2)
                .status("good")
                .build());
        
        section.addMetric(ReportMetric.builder()
                .name("maintenance_cost")
                .displayName("Maintenance Cost")
                .value(0.58)
                .unit("$/delivery")
                .changeValue(0.04)
                .changePercentage(7.4)
                .formatPrecision(2)
                .status("warning")
                .build());
        
        section.addMetric(ReportMetric.builder()
                .name("other_costs")
                .displayName("Other Costs")
                .value(0.29)
                .unit("$/delivery")
                .changeValue(-0.09)
                .changePercentage(-23.7)
                .formatPrecision(2)
                .status("good")
                .build());
        
        // Add cost breakdown chart
        ChartData costBreakdownChart = ChartData.builder()
                .id(UUID.randomUUID().toString())
                .title("Delivery Cost Breakdown")
                .type(ChartData.ChartType.PIE)
                .labels(Arrays.asList("Labor", "Fuel", "Maintenance", "Insurance", "Equipment", "Other"))
                .series(List.of(
                        ChartData.DataSeries.builder()
                                .name("Cost Components")
                                .data(Arrays.asList(55.4, 26.6, 12.0, 2.8, 1.9, 1.3))
                                .backgroundColor(Arrays.asList(
                                        "#2196F3", "#FF9800", "#F44336", "#9C27B0", "#00BCD4", "#607D8B"))
                                .build()
                ))
                .showLegend(true)
                .build();
        
        section.addChart(costBreakdownChart);
        
        // Add regional cost comparison chart
        List<String> defaultRegions = (regions != null && !regions.isEmpty()) 
                ? regions 
                : Arrays.asList("NAM", "EUR", "APAC", "AFR", "LAT");
        
        ChartData regionalCostChart = ChartData.builder()
                .id(UUID.randomUUID().toString())
                .title("Regional Cost Efficiency Comparison")
                .type(ChartData.ChartType.BAR)
                .labels(defaultRegions)
                .series(List.of(
                        ChartData.DataSeries.builder()
                                .name("Current Period")
                                .data(generateRegionalCostData(defaultRegions, false))
                                .backgroundColor("#4CAF50")
                                .build(),
                        ChartData.DataSeries.builder()
                                .name("Previous Period")
                                .data(generateRegionalCostData(defaultRegions, true))
                                .backgroundColor("#2196F3")
                                .build(),
                        ChartData.DataSeries.builder()
                                .name("Target")
                                .data(Arrays.asList(4.75, 4.75, 4.75, 4.75, 4.75))
                                .backgroundColor("rgba(255, 193, 7, 0.5)")
                                .borderColor("#FFC107")
                                .borderWidth(1)
                                .build()
                ))
                .xAxisLabel("Region")
                .yAxisLabel("Cost per Delivery ($)")
                .showLegend(true)
                .build();
        
        section.addChart(regionalCostChart);
        
        // Create table columns for cost efficiency
        List<TableData.Column> costColumns = Arrays.asList(
                TableData.Column.builder()
                        .id("region")
                        .name("Region")
                        .dataKey("region")
                        .width("15%")
                        .sortable(true)
                        .build(),
                TableData.Column.builder()
                        .id("cost_per_delivery")
                        .name("Cost/Delivery ($)")
                        .dataKey("cost_per_delivery")
                        .width("15%")
                        .sortable(true)
                        .align("right")
                        .cellFormat("$%.2f")
                        .build(),
                TableData.Column.builder()
                        .id("labor_cost")
                        .name("Labor Cost ($)")
                        .dataKey("labor_cost")
                        .width("15%")
                        .sortable(true)
                        .align("right")
                        .cellFormat("$%.2f")
                        .build(),
                TableData.Column.builder()
                        .id("fuel_cost")
                        .name("Fuel Cost ($)")
                        .dataKey("fuel_cost")
                        .width("15%")
                        .sortable(true)
                        .align("right")
                        .cellFormat("$%.2f")
                        .build(),
                TableData.Column.builder()
                        .id("cost_change")
                        .name("Change (%)")
                        .dataKey("cost_change")
                        .width("15%")
                        .sortable(true)
                        .align("right")
                        .cellFormat("%+.1f%%")
                        .conditionalFormats(Arrays.asList(
                                TableData.ConditionalFormat.builder()
                                        .condition("value < 0")
                                        .textColor("#388E3C")
                                        .build(),
                                TableData.ConditionalFormat.builder()
                                        .condition("value > 0")
                                        .textColor("#D32F2F")
                                        .build()
                        ))
                        .build(),
                TableData.Column.builder()
                        .id("efficiency_score")
                        .name("Efficiency Score")
                        .dataKey("efficiency_score")
                        .width("15%")
                        .sortable(true)
                        .align("right")
                        .cellFormat("%.1f")
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
                        .build()
        );
        
        // Create rows for cost efficiency table
        List<Map<String, Object>> costRows = new ArrayList<>();
        
        // Generate data for default regions if none provided
        for (String region : defaultRegions) {
            Map<String, Object> row = generateRegionalCostRow(region);
            costRows.add(row);
        }
        
        // Create the cost efficiency table
        TableData costTable = TableData.builder()
                .id(UUID.randomUUID().toString())
                .title("Regional Cost Efficiency Details")
                .columns(costColumns)
                .rows(costRows)
                .showHeader(true)
                .striped(true)
                .hoverable(true)
                .responsive(true)
                .build();
        
        section.addTable(costTable);
        
        return section;
    }
    
    /**
     * Create the route optimization section
     */
    private ReportSection createRouteOptimizationSection(LocalDateTime startDate, LocalDateTime endDate) {
        ReportSection section = ReportSection.builder()
                .id(UUID.randomUUID().toString())
                .title("Route Optimization Analysis")
                .description("Analysis of routing efficiency and optimization opportunities")
                .type(ReportSection.SectionType.ANALYSIS)
                .metrics(new ArrayList<>())
                .charts(new ArrayList<>())
                .build();
        
        // Add metrics
        section.addMetric(ReportMetric.builder()
                .name("route_efficiency")
                .displayName("Route Efficiency")
                .value(83.8)
                .unit("%")
                .changeValue(4.2)
                .changePercentage(5.3)
                .formatPrecision(1)
                .status("good")
                .build());
        
        section.addMetric(ReportMetric.builder()
                .name("avg_stops_per_route")
                .displayName("Avg. Stops per Route")
                .value(16.8)
                .unit("")
                .changeValue(2.1)
                .changePercentage(14.3)
                .formatPrecision(1)
                .status("good")
                .build());
        
        section.addMetric(ReportMetric.builder()
                .name("avg_distance_per_stop")
                .displayName("Avg. Distance per Stop")
                .value(1.2)
                .unit("km")
                .changeValue(-0.3)
                .changePercentage(-20.0)
                .formatPrecision(1)
                .status("good")
                .build());
        
        section.addMetric(ReportMetric.builder()
                .name("optimal_route_adherence")
                .displayName("Optimal Route Adherence")
                .value(78.5)
                .unit("%")
                .changeValue(5.8)
                .changePercentage(8.0)
                .formatPrecision(1)
                .status("good")
                .build());
        
        section.addMetric(ReportMetric.builder()
                .name("route_consolidation")
                .displayName("Route Consolidation")
                .value(73.2)
                .unit("%")
                .changeValue(7.4)
                .changePercentage(11.2)
                .formatPrecision(1)
                .status("good")
                .build());
        
        // Add route optimization chart
        ChartData routeOptimizationChart = ChartData.builder()
                .id(UUID.randomUUID().toString())
                .title("Route Optimization Improvements")
                .type(ChartData.ChartType.BAR)
                .labels(Arrays.asList("Distance", "Time", "Fuel", "Stops per Hour", "Failed Deliveries"))
                .series(List.of(
                        ChartData.DataSeries.builder()
                                .name("Improvement %")
                                .data(Arrays.asList(15.3, 12.8, 8.7, 14.3, -28.5))
                                .backgroundColor(Arrays.asList(
                                        "#4CAF50", "#4CAF50", "#4CAF50", "#4CAF50", "#4CAF50"))
                                .build()
                ))
                .xAxisLabel("Metric")
                .yAxisLabel("Improvement %")
                .showLegend(false)
                .build();
        
        section.addChart(routeOptimizationChart);
        
        // Add route density map placeholder (would be a geo chart in a real implementation)
        section.addHtmlContent("<div class=\"chart-placeholder\" style=\"height: 300px; background-color: #f5f5f5; " +
                "border-radius: 4px; display: flex; align-items: center; justify-content: center; margin-top: 20px;\">" +
                "<p style=\"text-align: center; color: #616161;\">" +
                "Route Density Heat Map<br/><small>This heat map would show delivery density and optimal routing " +
                "in a real implementation.</small></p></div>");
        
        // Add traffic avoidance chart
        ChartData trafficAvoidanceChart = ChartData.builder()
                .id(UUID.randomUUID().toString())
                .title("Peak Traffic Avoidance by Hour")
                .type(ChartData.ChartType.LINE)
                .labels(Arrays.asList("6am", "7am", "8am", "9am", "10am", "11am", "12pm", 
                        "1pm", "2pm", "3pm", "4pm", "5pm", "6pm", "7pm", "8pm"))
                .series(List.of(
                        ChartData.DataSeries.builder()
                                .name("Delivery Volume")
                                .data(Arrays.asList(5.2, 3.8, 2.7, 4.8, 8.3, 9.7, 10.2, 
                                        9.5, 8.7, 7.8, 6.3, 5.8, 7.2, 6.8, 3.2))
                                .borderColor("#2196F3")
                                .backgroundColor("rgba(33, 150, 243, 0.1)")
                                .fill(true)
                                .build(),
                        ChartData.DataSeries.builder()
                                .name("Traffic Congestion")
                                .data(Arrays.asList(2.5, 7.8, 8.9, 6.5, 5.3, 6.2, 7.8, 
                                        7.2, 6.5, 6.8, 8.3, 9.2, 8.7, 7.5, 4.3))
                                .borderColor("#F44336")
                                .backgroundColor("rgba(244, 67, 54, 0.1)")
                                .fill(true)
                                .build()
                ))
                .xAxisLabel("Hour of Day")
                .yAxisLabel("Scale (0-10)")
                .showLegend(true)
                .build();
        
        section.addChart(trafficAvoidanceChart);
        
        return section;
    }
    
    /**
     * Create the delivery performance section
     */
    private ReportSection createDeliveryPerformanceSection(
            LocalDateTime startDate, 
            LocalDateTime endDate,
            List<String> branches) {
        
        ReportSection section = ReportSection.builder()
                .id(UUID.randomUUID().toString())
                .title("Delivery Performance Analysis")
                .description("Analysis of delivery success rates and performance metrics")
                .type(ReportSection.SectionType.ANALYSIS)
                .metrics(new ArrayList<>())
                .charts(new ArrayList<>())
                .build();
        
        // Add metrics
        section.addMetric(ReportMetric.builder()
                .name("on_time_delivery")
                .displayName("On-Time Delivery")
                .value(92.8)
                .unit("%")
                .changeValue(3.5)
                .changePercentage(3.9)
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
                .name("delivery_accuracy")
                .displayName("Delivery Accuracy")
                .value(99.2)
                .unit("%")
                .changeValue(0.3)
                .changePercentage(0.3)
                .formatPrecision(1)
                .status("good")
                .build());
        
        section.addMetric(ReportMetric.builder()
                .name("customer_wait_time")
                .displayName("Customer Wait Time")
                .value(42.8)
                .unit("min")
                .changeValue(-8.3)
                .changePercentage(-16.2)
                .formatPrecision(1)
                .status("good")
                .build());
        
        // Add delivery outcome chart
        ChartData deliveryOutcomeChart = ChartData.builder()
                .id(UUID.randomUUID().toString())
                .title("Delivery Outcomes")
                .type(ChartData.ChartType.PIE)
                .labels(Arrays.asList(
                        "Delivered On-Time", 
                        "Delivered Late", 
                        "Failed - Customer Unavailable", 
                        "Failed - Address Issue", 
                        "Rescheduled", 
                        "Returned to Sender"))
                .series(List.of(
                        ChartData.DataSeries.builder()
                                .name("Percentage")
                                .data(Arrays.asList(92.8, 4.9, 1.2, 0.5, 0.4, 0.2))
                                .backgroundColor(Arrays.asList(
                                        "#4CAF50", "#FFC107", "#F44336", 
                                        "#9C27B0", "#2196F3", "#607D8B"))
                                .build()
                ))
                .showLegend(true)
                .build();
        
        section.addChart(deliveryOutcomeChart);
        
        // Add delivery success trend chart
        ChartData deliverySuccessTrendChart = ChartData.builder()
                .id(UUID.randomUUID().toString())
                .title("Delivery Success Trend")
                .type(ChartData.ChartType.LINE)
                .labels(Arrays.asList("Week 1", "Week 2", "Week 3", "Week 4", "Week 5", "Week 6", "Week 7", "Week 8"))
                .series(List.of(
                        ChartData.DataSeries.builder()
                                .name("On-Time Delivery %")
                                .data(Arrays.asList(88.3, 89.5, 90.2, 91.5, 91.7, 92.3, 92.5, 92.8))
                                .borderColor("#4CAF50")
                                .backgroundColor("rgba(76, 175, 80, 0.1)")
                                .fill(true)
                                .build(),
                        ChartData.DataSeries.builder()
                                .name("First Attempt Success %")
                                .data(Arrays.asList(81.2, 82.8, 84.1, 85.3, 86.0, 86.5, 87.0, 87.5))
                                .borderColor("#2196F3")
                                .backgroundColor("rgba(33, 150, 243, 0.1)")
                                .fill(true)
                                .build(),
                        ChartData.DataSeries.builder()
                                .name("Target %")
                                .data(Arrays.asList(90.0, 90.0, 90.0, 90.0, 90.0, 90.0, 90.0, 90.0))
                                .borderColor("#FFC107")
                                .borderDash(Arrays.asList(5, 5))
                                .fill(false)
                                .build()
                ))
                .xAxisLabel("Week")
                .yAxisLabel("Success Rate (%)")
                .showLegend(true)
                .build();
        
        section.addChart(deliverySuccessTrendChart);
        
        // Create late delivery reasons chart
        ChartData lateReasonChart = ChartData.builder()
                .id(UUID.randomUUID().toString())
                .title("Reasons for Late Deliveries")
                .type(ChartData.ChartType.HORIZONTAL_BAR)
                .labels(Arrays.asList(
                        "Traffic Congestion", 
                        "Weather Conditions", 
                        "Customer Unavailable", 
                        "Vehicle Issues", 
                        "Incorrect Address", 
                        "Package Preparation", 
                        "Other"))
                .series(List.of(
                        ChartData.DataSeries.builder()
                                .name("Percentage")
                                .data(Arrays.asList(32.7, 18.5, 14.2, 12.8, 10.5, 6.8, 4.5))
                                .backgroundColor("#F44336")
                                .build()
                ))
                .xAxisLabel("Percentage (%)")
                .showLegend(false)
                .build();
        
        section.addChart(lateReasonChart);
        
        return section;
    }
    
    /**
     * Create the optimization recommendations section
     */
    private ReportSection createOptimizationRecommendationsSection() {
        ReportSection section = ReportSection.builder()
                .id(UUID.randomUUID().toString())
                .title("Optimization Recommendations")
                .description("Key recommendations to improve delivery efficiency")
                .type(ReportSection.SectionType.RECOMMENDATIONS)
                .build();
        
        // Add recommendation content
        section.setHtmlContent(
                "<div class=\"recommendations-container\">" +
                "<h4>Short-Term Recommendations (1-2 Months)</h4>" +
                "<ol>" +
                "<li><strong>Dynamic Route Optimization</strong><br/>" +
                "Implement real-time route adjustments based on traffic conditions. This could reduce transit time by an estimated 8-12%.</li>" +
                "<li><strong>Delivery Time Window Adjustments</strong><br/>" +
                "Adjust delivery windows to avoid peak traffic hours in urban centers, particularly in the APAC region.</li>" +
                "<li><strong>Package Grouping Optimization</strong><br/>" +
                "Improve package batching algorithms to increase the number of deliveries per route by 15-20%.</li>" +
                "</ol>" +
                "<h4>Medium-Term Recommendations (3-6 Months)</h4>" +
                "<ol>" +
                "<li><strong>Microdepot Implementation</strong><br/>" +
                "Establish microdepots in high-density urban areas to reduce last-mile delivery distance by up to 30%.</li>" +
                "<li><strong>Delivery Performance Incentives</strong><br/>" +
                "Implement a courier incentive program focused on first-attempt delivery success.</li>" +
                "<li><strong>Address Validation Improvements</strong><br/>" +
                "Enhance address validation systems to reduce delivery failures due to address issues by up to 60%.</li>" +
                "</ol>" +
                "<h4>Long-Term Recommendations (6-12 Months)</h4>" +
                "<ol>" +
                "<li><strong>Fleet Optimization</strong><br/>" +
                "Diversify the delivery fleet with smaller vehicles for urban centers and more fuel-efficient options for suburban areas.</li>" +
                "<li><strong>Predictive Delivery System</strong><br/>" +
                "Develop a machine learning system to predict optimal delivery times based on historical data.</li>" +
                "<li><strong>Customer Communication Enhancement</strong><br/>" +
                "Implement a more advanced customer notification system with narrow time windows and real-time tracking.</li>" +
                "</ol>" +
                "</div>"
        );
        
        // Add expected impact content
        section.setConclusion(
                "Implementing these recommendations is projected to yield the following improvements:\n\n" +
                "• 12-15% reduction in overall delivery time\n" +
                "• 7-9% reduction in delivery costs\n" +
                "• 8-10% improvement in first-attempt delivery success\n" +
                "• 15-18% increase in courier productivity\n\n" +
                "We recommend prioritizing the dynamic route optimization and delivery time window adjustments for immediate impact."
        );
        
        return section;
    }
    
    /**
     * Generate regional time data for charts
     */
    private List<Object> generateRegionalTimeData(List<String> regions, boolean previous) {
        List<Object> data = new ArrayList<>();
        
        for (String region : regions) {
            double value;
            
            // Generate region-specific data
            switch (region) {
                case "NAM":
                    value = previous ? 27.8 : 25.3;
                    break;
                case "EUR":
                    value = previous ? 29.3 : 27.1;
                    break;
                case "APAC":
                    value = previous ? 34.9 : 32.7;
                    break;
                case "AFR":
                    value = previous ? 37.2 : 35.4;
                    break;
                case "LAT":
                    value = previous ? 33.5 : 30.8;
                    break;
                default:
                    // Random data for other regions
                    value = previous ? 
                            30.0 + random.nextDouble() * 10.0 : 
                            28.0 + random.nextDouble() * 9.0;
                    break;
            }
            
            data.add(value);
        }
        
        return data;
    }
    
    /**
     * Generate regional cost data for charts
     */
    private List<Object> generateRegionalCostData(List<String> regions, boolean previous) {
        List<Object> data = new ArrayList<>();
        
        for (String region : regions) {
            double value;
            
            // Generate region-specific data
            switch (region) {
                case "NAM":
                    value = previous ? 5.14 : 4.82;
                    break;
                case "EUR":
                    value = previous ? 5.38 : 5.12;
                    break;
                case "APAC":
                    value = previous ? 4.52 : 4.28;
                    break;
                case "AFR":
                    value = previous ? 6.05 : 5.76;
                    break;
                case "LAT":
                    value = previous ? 5.24 : 4.95;
                    break;
                default:
                    // Random data for other regions
                    value = previous ? 
                            5.00 + random.nextDouble() * 1.5 : 
                            4.75 + random.nextDouble() * 1.25;
                    break;
            }
            
            data.add(value);
        }
        
        return data;
    }
    
    /**
     * Generate a row for the regional cost table
     */
    private Map<String, Object> generateRegionalCostRow(String region) {
        Map<String, Object> row = new HashMap<>();
        row.put("region", region);
        
        // Generate region-specific data
        switch (region) {
            case "NAM":
                row.put("cost_per_delivery", 4.82);
                row.put("labor_cost", 2.67);
                row.put("fuel_cost", 1.28);
                row.put("cost_change", -6.2);
                row.put("efficiency_score", 92.5);
                break;
            case "EUR":
                row.put("cost_per_delivery", 5.12);
                row.put("labor_cost", 3.28);
                row.put("fuel_cost", 1.08);
                row.put("cost_change", -4.8);
                row.put("efficiency_score", 88.7);
                break;
            case "APAC":
                row.put("cost_per_delivery", 4.28);
                row.put("labor_cost", 2.15);
                row.put("fuel_cost", 1.45);
                row.put("cost_change", -5.3);
                row.put("efficiency_score", 90.2);
                break;
            case "AFR":
                row.put("cost_per_delivery", 5.76);
                row.put("labor_cost", 2.82);
                row.put("fuel_cost", 1.96);
                row.put("cost_change", -4.8);
                row.put("efficiency_score", 79.8);
                break;
            case "LAT":
                row.put("cost_per_delivery", 4.95);
                row.put("labor_cost", 2.54);
                row.put("fuel_cost", 1.67);
                row.put("cost_change", -5.5);
                row.put("efficiency_score", 85.3);
                break;
            default:
                // Random data for other regions
                row.put("cost_per_delivery", 4.50 + random.nextDouble() * 1.50);
                row.put("labor_cost", 2.25 + random.nextDouble() * 1.00);
                row.put("fuel_cost", 1.25 + random.nextDouble() * 0.75);
                row.put("cost_change", -5.0 + random.nextDouble() * 2.0);
                row.put("efficiency_score", 80.0 + random.nextDouble() * 15.0);
                break;
        }
        
        return row;
    }
}