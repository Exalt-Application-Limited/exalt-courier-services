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
import java.util.stream.Collectors;

/**
 * Generator for regional comparison reports
 */
@Slf4j
@Service
public class RegionalComparisonReportGenerator implements ReportGenerator {

    private final TracingService tracingService;
    private final Random random = new Random();
    
    @Autowired
    public RegionalComparisonReportGenerator(TracingService tracingService) {
        this.tracingService = tracingService;
    }

    @Override
    @Traced("RegionalComparisonReportGenerator.generateReport")
    public AdvancedReport generateReport(
            LocalDateTime startDate,
            LocalDateTime endDate,
            List<String> regions,
            List<String> branches) {
        
        tracingService.addTag("startDate", startDate.toString());
        tracingService.addTag("endDate", endDate.toString());
        
        log.info("Generating regional comparison report. Start: {}, End: {}", startDate, endDate);
        
        // Ensure we have regions to compare
        List<String> reportRegions = (regions != null && !regions.isEmpty()) 
                ? regions 
                : Arrays.asList("NAM", "EUR", "APAC", "AFR", "LAT");
        
        // Create the report
        AdvancedReport report = AdvancedReport.builder()
                .reportId(UUID.randomUUID().toString())
                .title("Regional Comparison Report")
                .description("Comprehensive comparison of performance metrics across different regions")
                .type(AdvancedReport.ReportType.REGIONAL_COMPARISON)
                .generatedAt(LocalDateTime.now())
                .startDate(startDate)
                .endDate(endDate)
                .regions(reportRegions)
                .branches(branches)
                .sections(new ArrayList<>())
                .build();
        
        // Add report sections
        report.addSection(createExecutiveSummarySection(startDate, endDate, reportRegions));
        report.addSection(createKeyMetricsComparisonSection(reportRegions));
        report.addSection(createPerformanceAnalysisSection(reportRegions));
        report.addSection(createCostComparisonSection(reportRegions));
        report.addSection(createEfficiencyComparisonSection(reportRegions));
        report.addSection(createGrowthTrendsSection(reportRegions));
        report.addSection(createRecommendationsSection(reportRegions));
        
        return report;
    }
    
    /**
     * Create the executive summary section
     */
    private ReportSection createExecutiveSummarySection(
            LocalDateTime startDate, 
            LocalDateTime endDate,
            List<String> regions) {
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy");
        
        ReportSection section = ReportSection.builder()
                .id(UUID.randomUUID().toString())
                .title("Executive Summary")
                .description("Key performance insights across " + regions.size() + " regions for the period " + 
                        startDate.format(formatter) + " to " + endDate.format(formatter))
                .type(ReportSection.SectionType.SUMMARY)
                .metrics(new ArrayList<>())
                .build();
        
        // Find the top and bottom performing regions
        Map<String, Double> performanceScores = new HashMap<>();
        performanceScores.put("NAM", 92.5);
        performanceScores.put("EUR", 88.7);
        performanceScores.put("APAC", 85.3);
        performanceScores.put("AFR", 78.4);
        performanceScores.put("LAT", 83.6);
        
        String topRegion = regions.stream()
                .filter(performanceScores::containsKey)
                .max((r1, r2) -> Double.compare(performanceScores.getOrDefault(r1, 0.0), 
                                               performanceScores.getOrDefault(r2, 0.0)))
                .orElse(regions.get(0));
        
        String bottomRegion = regions.stream()
                .filter(performanceScores::containsKey)
                .min((r1, r2) -> Double.compare(performanceScores.getOrDefault(r1, 0.0), 
                                               performanceScores.getOrDefault(r2, 0.0)))
                .orElse(regions.get(regions.size() - 1));
        
        // Create the summary content
        section.setHtmlContent("<p>This report provides a comprehensive comparative analysis of key performance metrics " +
                "across " + regions.size() + " regions. The analysis reveals significant performance variations with " +
                "opportunities for cross-regional learning and optimization.</p>" +
                "<p><strong>Key insights:</strong></p>" +
                "<ul>" +
                "<li>The " + topRegion + " region leads in overall performance with exceptional results in delivery efficiency and customer satisfaction</li>" +
                "<li>" + getRegionWithAttribute(regions, "fastest growing", "APAC") + " shows the strongest growth trajectory with a 24.5% increase in delivery volume</li>" +
                "<li>" + getRegionWithAttribute(regions, "most cost-efficient", "APAC") + " demonstrates superior cost-efficiency with an average cost per delivery of $4.28</li>" +
                "<li>" + getRegionWithAttribute(regions, "highest customer satisfaction", "NAM") + " maintains the highest customer satisfaction score of 4.7/5</li>" +
                "</ul>" +
                "<p><strong>Areas requiring attention:</strong></p>" +
                "<ul>" +
                "<li>The " + bottomRegion + " region faces challenges with on-time delivery performance and courier utilization</li>" +
                "<li>" + getRegionWithAttribute(regions, "highest cost per delivery", "AFR") + " has the highest cost per delivery at $5.76, 19.5% above the global average</li>" +
                "<li>Significant performance disparity exists between top and bottom regions across multiple metrics</li>" +
                "</ul>");
        
        section.setConclusion("The analysis indicates significant opportunities for cross-regional knowledge sharing and " +
                "performance improvement. Adopting best practices from high-performing regions could yield a 12-15% " +
                "improvement in underperforming areas. Detailed recommendations are provided in the final section of this report.");
        
        // Add summary metrics
        section.addMetric(ReportMetric.builder()
                .name("top_region_score")
                .displayName("Top Region Score")
                .value(getRegionScore(topRegion))
                .unit("")
                .formatPrecision(1)
                .status("good")
                .build());
        
        section.addMetric(ReportMetric.builder()
                .name("bottom_region_score")
                .displayName("Bottom Region Score")
                .value(getRegionScore(bottomRegion))
                .unit("")
                .formatPrecision(1)
                .status("warning")
                .build());
        
        section.addMetric(ReportMetric.builder()
                .name("performance_gap")
                .displayName("Performance Gap")
                .value(getRegionScore(topRegion) - getRegionScore(bottomRegion))
                .unit("%")
                .formatPrecision(1)
                .status("warning")
                .build());
        
        section.addMetric(ReportMetric.builder()
                .name("improvement_potential")
                .displayName("Improvement Potential")
                .value(15.3)
                .unit("%")
                .formatPrecision(1)
                .status("info")
                .build());
        
        return section;
    }
    
    /**
     * Create the key metrics comparison section
     */
    private ReportSection createKeyMetricsComparisonSection(List<String> regions) {
        ReportSection section = ReportSection.builder()
                .id(UUID.randomUUID().toString())
                .title("Key Metrics Comparison")
                .description("Comparison of critical performance indicators across regions")
                .type(ReportSection.SectionType.COMPARISON)
                .charts(new ArrayList<>())
                .tables(new ArrayList<>())
                .build();
        
        // Create a comprehensive table for key metrics
        List<TableData.Column> columns = new ArrayList<>();
        
        // First column is the metric name
        columns.add(TableData.Column.builder()
                .id("metric")
                .name("Metric")
                .dataKey("metric")
                .width("20%")
                .sortable(false)
                .build());
        
        // Add a column for each region
        for (String region : regions) {
            columns.add(TableData.Column.builder()
                    .id(region.toLowerCase())
                    .name(region)
                    .dataKey(region.toLowerCase())
                    .width(String.format("%.1f%%", 80.0 / regions.size()))
                    .sortable(false)
                    .align("right")
                    .conditionalFormats(Arrays.asList(
                            TableData.ConditionalFormat.builder()
                                    .condition("metadata.highIsBetter && " +
                                               "metadata.isTopValue && " +
                                               "metadata.columnId === '" + region.toLowerCase() + "'")
                                    .backgroundColor("#E8F5E9")
                                    .textColor("#388E3C")
                                    .fontWeight("bold")
                                    .build(),
                            TableData.ConditionalFormat.builder()
                                    .condition("metadata.highIsBetter && " +
                                               "metadata.isBottomValue && " +
                                               "metadata.columnId === '" + region.toLowerCase() + "'")
                                    .backgroundColor("#FFEBEE")
                                    .textColor("#D32F2F")
                                    .build(),
                            TableData.ConditionalFormat.builder()
                                    .condition("!metadata.highIsBetter && " +
                                               "metadata.isTopValue && " +
                                               "metadata.columnId === '" + region.toLowerCase() + "'")
                                    .backgroundColor("#FFEBEE")
                                    .textColor("#D32F2F")
                                    .fontWeight("bold")
                                    .build(),
                            TableData.ConditionalFormat.builder()
                                    .condition("!metadata.highIsBetter && " +
                                               "metadata.isBottomValue && " +
                                               "metadata.columnId === '" + region.toLowerCase() + "'")
                                    .backgroundColor("#E8F5E9")
                                    .textColor("#388E3C")
                                    .build()
                    ))
                    .build());
        }
        
        // Create table rows
        List<Map<String, Object>> rows = createKeyMetricsRows(regions);
        
        // Create and add the table
        TableData metricsTable = TableData.builder()
                .id(UUID.randomUUID().toString())
                .title("Regional Performance Metrics Comparison")
                .columns(columns)
                .rows(rows)
                .showHeader(true)
                .striped(true)
                .hoverable(true)
                .bordered(true)
                .responsive(true)
                .build();
        
        section.addTable(metricsTable);
        
        // Add a spider/radar chart for visual comparison
        ChartData radarChart = ChartData.builder()
                .id(UUID.randomUUID().toString())
                .title("Regional Performance Comparison")
                .type(ChartData.ChartType.RADAR)
                .labels(Arrays.asList(
                        "Delivery Efficiency", 
                        "Cost Efficiency", 
                        "Customer Satisfaction", 
                        "Courier Productivity", 
                        "On-Time Performance", 
                        "Volume Growth"))
                .series(regions.stream()
                        .map(region -> {
                            List<Object> data = getRegionalRadarData(region);
                            return ChartData.DataSeries.builder()
                                    .name(region)
                                    .data(data)
                                    .build();
                        })
                        .collect(Collectors.toList()))
                .showLegend(true)
                .build();
        
        section.addChart(radarChart);
        
        // Add a bar chart for overall performance score
        ChartData overallScoreChart = ChartData.builder()
                .id(UUID.randomUUID().toString())
                .title("Overall Regional Performance Score")
                .type(ChartData.ChartType.BAR)
                .labels(regions)
                .series(List.of(
                        ChartData.DataSeries.builder()
                                .name("Performance Score")
                                .data(regions.stream()
                                        .map(this::getRegionScore)
                                        .collect(Collectors.toList()))
                                .backgroundColor(regions.stream()
                                        .map(region -> getRegionColor(region, regions))
                                        .collect(Collectors.toList()))
                                .build(),
                        ChartData.DataSeries.builder()
                                .name("Target")
                                .data(regions.stream()
                                        .map(region -> 90.0)
                                        .collect(Collectors.toList()))
                                .backgroundColor("rgba(255, 193, 7, 0.5)")
                                .borderColor("#FFC107")
                                .borderWidth(1)
                                .type("line")
                                .build()
                ))
                .xAxisLabel("Region")
                .yAxisLabel("Performance Score")
                .showLegend(true)
                .build();
        
        section.addChart(overallScoreChart);
        
        return section;
    }
    
    /**
     * Create the performance analysis section
     */
    private ReportSection createPerformanceAnalysisSection(List<String> regions) {
        ReportSection section = ReportSection.builder()
                .id(UUID.randomUUID().toString())
                .title("Delivery Performance Analysis")
                .description("Detailed comparison of delivery performance metrics across regions")
                .type(ReportSection.SectionType.ANALYSIS)
                .charts(new ArrayList<>())
                .build();
        
        // Add on-time delivery comparison chart
        ChartData onTimeDeliveryChart = ChartData.builder()
                .id(UUID.randomUUID().toString())
                .title("On-Time Delivery Rate by Region")
                .type(ChartData.ChartType.BAR)
                .labels(regions)
                .series(List.of(
                        ChartData.DataSeries.builder()
                                .name("On-Time Delivery Rate")
                                .data(regions.stream()
                                        .map(region -> getRegionalMetric(region, "on_time_delivery"))
                                        .collect(Collectors.toList()))
                                .backgroundColor(regions.stream()
                                        .map(region -> getRegionOnTimeDeliveryColor(region))
                                        .collect(Collectors.toList()))
                                .build(),
                        ChartData.DataSeries.builder()
                                .name("Target")
                                .data(regions.stream()
                                        .map(region -> 90.0)
                                        .collect(Collectors.toList()))
                                .backgroundColor("rgba(255, 193, 7, 0.5)")
                                .borderColor("#FFC107")
                                .borderWidth(1)
                                .type("line")
                                .build()
                ))
                .xAxisLabel("Region")
                .yAxisLabel("On-Time Delivery Rate (%)")
                .showLegend(true)
                .build();
        
        section.addChart(onTimeDeliveryChart);
        
        // Add delivery time comparison chart
        ChartData deliveryTimeChart = ChartData.builder()
                .id(UUID.randomUUID().toString())
                .title("Average Delivery Time by Region")
                .type(ChartData.ChartType.BAR)
                .labels(regions)
                .series(List.of(
                        ChartData.DataSeries.builder()
                                .name("Average Delivery Time")
                                .data(regions.stream()
                                        .map(region -> getRegionalMetric(region, "avg_delivery_time"))
                                        .collect(Collectors.toList()))
                                .backgroundColor(regions.stream()
                                        .map(region -> getRegionDeliveryTimeColor(region))
                                        .collect(Collectors.toList()))
                                .build(),
                        ChartData.DataSeries.builder()
                                .name("Target")
                                .data(regions.stream()
                                        .map(region -> 30.0)
                                        .collect(Collectors.toList()))
                                .backgroundColor("rgba(255, 193, 7, 0.5)")
                                .borderColor("#FFC107")
                                .borderWidth(1)
                                .type("line")
                                .build()
                ))
                .xAxisLabel("Region")
                .yAxisLabel("Average Delivery Time (min)")
                .showLegend(true)
                .build();
        
        section.addChart(deliveryTimeChart);
        
        // Add customer satisfaction comparison chart
        ChartData customerSatisfactionChart = ChartData.builder()
                .id(UUID.randomUUID().toString())
                .title("Customer Satisfaction by Region")
                .type(ChartData.ChartType.BAR)
                .labels(regions)
                .series(List.of(
                        ChartData.DataSeries.builder()
                                .name("Customer Satisfaction")
                                .data(regions.stream()
                                        .map(region -> getRegionalMetric(region, "customer_satisfaction"))
                                        .collect(Collectors.toList()))
                                .backgroundColor(regions.stream()
                                        .map(region -> getRegionCustomerSatisfactionColor(region))
                                        .collect(Collectors.toList()))
                                .build(),
                        ChartData.DataSeries.builder()
                                .name("Target")
                                .data(regions.stream()
                                        .map(region -> 4.5)
                                        .collect(Collectors.toList()))
                                .backgroundColor("rgba(255, 193, 7, 0.5)")
                                .borderColor("#FFC107")
                                .borderWidth(1)
                                .type("line")
                                .build()
                ))
                .xAxisLabel("Region")
                .yAxisLabel("Customer Satisfaction (1-5)")
                .showLegend(true)
                .build();
        
        section.addChart(customerSatisfactionChart);
        
        // Add delivery outcome comparison chart (stacked)
        ChartData deliveryOutcomeChart = ChartData.builder()
                .id(UUID.randomUUID().toString())
                .title("Delivery Outcomes by Region")
                .type(ChartData.ChartType.STACKED_BAR)
                .labels(regions)
                .series(List.of(
                        ChartData.DataSeries.builder()
                                .name("On-Time")
                                .data(regions.stream()
                                        .map(region -> getRegionalMetric(region, "on_time_delivery"))
                                        .collect(Collectors.toList()))
                                .backgroundColor("#4CAF50")
                                .stackGroup("outcomes")
                                .build(),
                        ChartData.DataSeries.builder()
                                .name("Late")
                                .data(regions.stream()
                                        .map(region -> {
                                            // Late delivery = 100 - on_time - failed - other
                                            double onTime = getRegionalMetric(region, "on_time_delivery");
                                            double failed = getRegionalMetric(region, "failed_delivery");
                                            double other = getRegionalMetric(region, "other_delivery_outcome");
                                            return Math.max(0, 100 - onTime - failed - other);
                                        })
                                        .collect(Collectors.toList()))
                                .backgroundColor("#FFC107")
                                .stackGroup("outcomes")
                                .build(),
                        ChartData.DataSeries.builder()
                                .name("Failed")
                                .data(regions.stream()
                                        .map(region -> getRegionalMetric(region, "failed_delivery"))
                                        .collect(Collectors.toList()))
                                .backgroundColor("#F44336")
                                .stackGroup("outcomes")
                                .build(),
                        ChartData.DataSeries.builder()
                                .name("Other")
                                .data(regions.stream()
                                        .map(region -> getRegionalMetric(region, "other_delivery_outcome"))
                                        .collect(Collectors.toList()))
                                .backgroundColor("#9E9E9E")
                                .stackGroup("outcomes")
                                .build()
                ))
                .xAxisLabel("Region")
                .yAxisLabel("Percentage of Deliveries")
                .showLegend(true)
                .build();
        
        section.addChart(deliveryOutcomeChart);
        
        return section;
    }
    
    /**
     * Create the cost comparison section
     */
    private ReportSection createCostComparisonSection(List<String> regions) {
        ReportSection section = ReportSection.builder()
                .id(UUID.randomUUID().toString())
                .title("Cost Efficiency Comparison")
                .description("Analysis of cost metrics and efficiency across regions")
                .type(ReportSection.SectionType.ANALYSIS)
                .charts(new ArrayList<>())
                .tables(new ArrayList<>())
                .build();
        
        // Add cost per delivery comparison chart
        ChartData costPerDeliveryChart = ChartData.builder()
                .id(UUID.randomUUID().toString())
                .title("Cost per Delivery by Region")
                .type(ChartData.ChartType.BAR)
                .labels(regions)
                .series(List.of(
                        ChartData.DataSeries.builder()
                                .name("Cost per Delivery")
                                .data(regions.stream()
                                        .map(region -> getRegionalMetric(region, "cost_per_delivery"))
                                        .collect(Collectors.toList()))
                                .backgroundColor(regions.stream()
                                        .map(region -> getRegionCostColor(region))
                                        .collect(Collectors.toList()))
                                .build(),
                        ChartData.DataSeries.builder()
                                .name("Target")
                                .data(regions.stream()
                                        .map(region -> 4.75)
                                        .collect(Collectors.toList()))
                                .backgroundColor("rgba(255, 193, 7, 0.5)")
                                .borderColor("#FFC107")
                                .borderWidth(1)
                                .type("line")
                                .build()
                ))
                .xAxisLabel("Region")
                .yAxisLabel("Cost per Delivery ($)")
                .showLegend(true)
                .build();
        
        section.addChart(costPerDeliveryChart);
        
        // Add cost breakdown by region chart (stacked)
        ChartData costBreakdownChart = ChartData.builder()
                .id(UUID.randomUUID().toString())
                .title("Cost Breakdown by Region")
                .type(ChartData.ChartType.STACKED_BAR)
                .labels(regions)
                .series(List.of(
                        ChartData.DataSeries.builder()
                                .name("Labor Cost")
                                .data(regions.stream()
                                        .map(region -> getRegionalMetric(region, "labor_cost"))
                                        .collect(Collectors.toList()))
                                .backgroundColor("#2196F3")
                                .stackGroup("costs")
                                .build(),
                        ChartData.DataSeries.builder()
                                .name("Fuel Cost")
                                .data(regions.stream()
                                        .map(region -> getRegionalMetric(region, "fuel_cost"))
                                        .collect(Collectors.toList()))
                                .backgroundColor("#FF9800")
                                .stackGroup("costs")
                                .build(),
                        ChartData.DataSeries.builder()
                                .name("Maintenance Cost")
                                .data(regions.stream()
                                        .map(region -> getRegionalMetric(region, "maintenance_cost"))
                                        .collect(Collectors.toList()))
                                .backgroundColor("#F44336")
                                .stackGroup("costs")
                                .build(),
                        ChartData.DataSeries.builder()
                                .name("Other Costs")
                                .data(regions.stream()
                                        .map(region -> getRegionalMetric(region, "other_costs"))
                                        .collect(Collectors.toList()))
                                .backgroundColor("#9E9E9E")
                                .stackGroup("costs")
                                .build()
                ))
                .xAxisLabel("Region")
                .yAxisLabel("Cost per Delivery ($)")
                .showLegend(true)
                .build();
        
        section.addChart(costBreakdownChart);
        
        // Create table columns for regional cost comparison
        List<TableData.Column> costColumns = Arrays.asList(
                TableData.Column.builder()
                        .id("region")
                        .name("Region")
                        .dataKey("region")
                        .width("16%")
                        .sortable(true)
                        .build(),
                TableData.Column.builder()
                        .id("cost_per_delivery")
                        .name("Cost/Delivery ($)")
                        .dataKey("cost_per_delivery")
                        .width("14%")
                        .sortable(true)
                        .align("right")
                        .cellFormat("$%.2f")
                        .build(),
                TableData.Column.builder()
                        .id("labor_cost")
                        .name("Labor ($)")
                        .dataKey("labor_cost")
                        .width("14%")
                        .sortable(true)
                        .align("right")
                        .cellFormat("$%.2f")
                        .build(),
                TableData.Column.builder()
                        .id("fuel_cost")
                        .name("Fuel ($)")
                        .dataKey("fuel_cost")
                        .width("14%")
                        .sortable(true)
                        .align("right")
                        .cellFormat("$%.2f")
                        .build(),
                TableData.Column.builder()
                        .id("maintenance_cost")
                        .name("Maint. ($)")
                        .dataKey("maintenance_cost")
                        .width("14%")
                        .sortable(true)
                        .align("right")
                        .cellFormat("$%.2f")
                        .build(),
                TableData.Column.builder()
                        .id("other_costs")
                        .name("Other ($)")
                        .dataKey("other_costs")
                        .width("14%")
                        .sortable(true)
                        .align("right")
                        .cellFormat("$%.2f")
                        .build(),
                TableData.Column.builder()
                        .id("vs_target")
                        .name("vs Target")
                        .dataKey("vs_target")
                        .width("14%")
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
                        .build()
        );
        
        // Create rows for the cost table
        List<Map<String, Object>> costRows = new ArrayList<>();
        
        for (String region : regions) {
            Map<String, Object> row = createRegionalCostRow(region);
            costRows.add(row);
        }
        
        // Create and add the cost table
        TableData costTable = TableData.builder()
                .id(UUID.randomUUID().toString())
                .title("Detailed Regional Cost Breakdown")
                .columns(costColumns)
                .rows(costRows)
                .showHeader(true)
                .striped(true)
                .hoverable(true)
                .bordered(true)
                .responsive(true)
                .build();
        
        section.addTable(costTable);
        
        return section;
    }
    
    /**
     * Create the efficiency comparison section
     */
    private ReportSection createEfficiencyComparisonSection(List<String> regions) {
        ReportSection section = ReportSection.builder()
                .id(UUID.randomUUID().toString())
                .title("Operational Efficiency Comparison")
                .description("Comparison of courier and operational efficiency metrics across regions")
                .type(ReportSection.SectionType.ANALYSIS)
                .charts(new ArrayList<>())
                .build();
        
        // Add deliveries per courier comparison chart
        ChartData deliveriesPerCourierChart = ChartData.builder()
                .id(UUID.randomUUID().toString())
                .title("Deliveries per Courier by Region")
                .type(ChartData.ChartType.BAR)
                .labels(regions)
                .series(List.of(
                        ChartData.DataSeries.builder()
                                .name("Deliveries per Courier")
                                .data(regions.stream()
                                        .map(region -> getRegionalMetric(region, "deliveries_per_courier"))
                                        .collect(Collectors.toList()))
                                .backgroundColor(regions.stream()
                                        .map(region -> getRegionEfficiencyColor(region))
                                        .collect(Collectors.toList()))
                                .build(),
                        ChartData.DataSeries.builder()
                                .name("Target")
                                .data(regions.stream()
                                        .map(region -> 25.0)
                                        .collect(Collectors.toList()))
                                .backgroundColor("rgba(255, 193, 7, 0.5)")
                                .borderColor("#FFC107")
                                .borderWidth(1)
                                .type("line")
                                .build()
                ))
                .xAxisLabel("Region")
                .yAxisLabel("Deliveries per Courier per Day")
                .showLegend(true)
                .build();
        
        section.addChart(deliveriesPerCourierChart);
        
        // Add courier utilization comparison chart
        ChartData courierUtilizationChart = ChartData.builder()
                .id(UUID.randomUUID().toString())
                .title("Courier Utilization by Region")
                .type(ChartData.ChartType.BAR)
                .labels(regions)
                .series(List.of(
                        ChartData.DataSeries.builder()
                                .name("Courier Utilization")
                                .data(regions.stream()
                                        .map(region -> getRegionalMetric(region, "courier_utilization"))
                                        .collect(Collectors.toList()))
                                .backgroundColor(regions.stream()
                                        .map(region -> getRegionUtilizationColor(region))
                                        .collect(Collectors.toList()))
                                .build(),
                        ChartData.DataSeries.builder()
                                .name("Target")
                                .data(regions.stream()
                                        .map(region -> 85.0)
                                        .collect(Collectors.toList()))
                                .backgroundColor("rgba(255, 193, 7, 0.5)")
                                .borderColor("#FFC107")
                                .borderWidth(1)
                                .type("line")
                                .build()
                ))
                .xAxisLabel("Region")
                .yAxisLabel("Courier Utilization (%)")
                .showLegend(true)
                .build();
        
        section.addChart(courierUtilizationChart);
        
        // Add route efficiency comparison chart
        ChartData routeEfficiencyChart = ChartData.builder()
                .id(UUID.randomUUID().toString())
                .title("Route Efficiency by Region")
                .type(ChartData.ChartType.BAR)
                .labels(regions)
                .series(List.of(
                        ChartData.DataSeries.builder()
                                .name("Route Efficiency")
                                .data(regions.stream()
                                        .map(region -> getRegionalMetric(region, "route_efficiency"))
                                        .collect(Collectors.toList()))
                                .backgroundColor(regions.stream()
                                        .map(region -> getRegionRouteEfficiencyColor(region))
                                        .collect(Collectors.toList()))
                                .build(),
                        ChartData.DataSeries.builder()
                                .name("Target")
                                .data(regions.stream()
                                        .map(region -> 85.0)
                                        .collect(Collectors.toList()))
                                .backgroundColor("rgba(255, 193, 7, 0.5)")
                                .borderColor("#FFC107")
                                .borderWidth(1)
                                .type("line")
                                .build()
                ))
                .xAxisLabel("Region")
                .yAxisLabel("Route Efficiency (%)")
                .showLegend(true)
                .build();
        
        section.addChart(routeEfficiencyChart);
        
        // Add first attempt success comparison chart
        ChartData firstAttemptSuccessChart = ChartData.builder()
                .id(UUID.randomUUID().toString())
                .title("First Attempt Success by Region")
                .type(ChartData.ChartType.BAR)
                .labels(regions)
                .series(List.of(
                        ChartData.DataSeries.builder()
                                .name("First Attempt Success")
                                .data(regions.stream()
                                        .map(region -> getRegionalMetric(region, "first_attempt_success"))
                                        .collect(Collectors.toList()))
                                .backgroundColor(regions.stream()
                                        .map(region -> getRegionFirstAttemptColor(region))
                                        .collect(Collectors.toList()))
                                .build(),
                        ChartData.DataSeries.builder()
                                .name("Target")
                                .data(regions.stream()
                                        .map(region -> 90.0)
                                        .collect(Collectors.toList()))
                                .backgroundColor("rgba(255, 193, 7, 0.5)")
                                .borderColor("#FFC107")
                                .borderWidth(1)
                                .type("line")
                                .build()
                ))
                .xAxisLabel("Region")
                .yAxisLabel("First Attempt Success (%)")
                .showLegend(true)
                .build();
        
        section.addChart(firstAttemptSuccessChart);
        
        return section;
    }
    
    /**
     * Create the growth trends section
     */
    private ReportSection createGrowthTrendsSection(List<String> regions) {
        ReportSection section = ReportSection.builder()
                .id(UUID.randomUUID().toString())
                .title("Regional Growth Trends")
                .description("Comparison of growth metrics and trends across regions")
                .type(ReportSection.SectionType.TREND_ANALYSIS)
                .charts(new ArrayList<>())
                .build();
        
        // Add volume growth comparison chart
        ChartData volumeGrowthChart = ChartData.builder()
                .id(UUID.randomUUID().toString())
                .title("Delivery Volume Growth by Region")
                .type(ChartData.ChartType.BAR)
                .labels(regions)
                .series(List.of(
                        ChartData.DataSeries.builder()
                                .name("Delivery Volume Growth")
                                .data(regions.stream()
                                        .map(region -> getRegionalMetric(region, "volume_growth"))
                                        .collect(Collectors.toList()))
                                .backgroundColor(regions.stream()
                                        .map(region -> getRegionGrowthColor(region))
                                        .collect(Collectors.toList()))
                                .build(),
                        ChartData.DataSeries.builder()
                                .name("Target")
                                .data(regions.stream()
                                        .map(region -> 15.0)
                                        .collect(Collectors.toList()))
                                .backgroundColor("rgba(255, 193, 7, 0.5)")
                                .borderColor("#FFC107")
                                .borderWidth(1)
                                .type("line")
                                .build()
                ))
                .xAxisLabel("Region")
                .yAxisLabel("Volume Growth (%)")
                .showLegend(true)
                .build();
        
        section.addChart(volumeGrowthChart);
        
        // Add performance improvement comparison chart
        ChartData performanceImprovementChart = ChartData.builder()
                .id(UUID.randomUUID().toString())
                .title("Performance Improvement by Region")
                .type(ChartData.ChartType.BAR)
                .labels(regions)
                .series(List.of(
                        ChartData.DataSeries.builder()
                                .name("On-Time Delivery Improvement")
                                .data(regions.stream()
                                        .map(region -> getRegionalMetric(region, "on_time_improvement"))
                                        .collect(Collectors.toList()))
                                .backgroundColor("#4CAF50")
                                .build(),
                        ChartData.DataSeries.builder()
                                .name("Cost Efficiency Improvement")
                                .data(regions.stream()
                                        .map(region -> getRegionalMetric(region, "cost_improvement"))
                                        .collect(Collectors.toList()))
                                .backgroundColor("#2196F3")
                                .build(),
                        ChartData.DataSeries.builder()
                                .name("Customer Satisfaction Improvement")
                                .data(regions.stream()
                                        .map(region -> getRegionalMetric(region, "satisfaction_improvement"))
                                        .collect(Collectors.toList()))
                                .backgroundColor("#9C27B0")
                                .build()
                ))
                .xAxisLabel("Region")
                .yAxisLabel("Improvement (%)")
                .showLegend(true)
                .build();
        
        section.addChart(performanceImprovementChart);
        
        // Add trend over time for key regions (line chart)
        // This would show trend data for a subset of metrics over time
        List<String> months = Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun");
        
        ChartData volumeTrendChart = ChartData.builder()
                .id(UUID.randomUUID().toString())
                .title("Delivery Volume Trend Over Time")
                .type(ChartData.ChartType.LINE)
                .labels(months)
                .series(regions.stream()
                        .map(region -> {
                            List<Object> data = generateMonthlyTrendData(region, "volume");
                            return ChartData.DataSeries.builder()
                                    .name(region)
                                    .data(data)
                                    .build();
                        })
                        .collect(Collectors.toList()))
                .xAxisLabel("Month")
                .yAxisLabel("Relative Volume (Base 100)")
                .showLegend(true)
                .build();
        
        section.addChart(volumeTrendChart);
        
        ChartData efficiencyTrendChart = ChartData.builder()
                .id(UUID.randomUUID().toString())
                .title("On-Time Delivery Trend Over Time")
                .type(ChartData.ChartType.LINE)
                .labels(months)
                .series(regions.stream()
                        .map(region -> {
                            List<Object> data = generateMonthlyTrendData(region, "on_time");
                            return ChartData.DataSeries.builder()
                                    .name(region)
                                    .data(data)
                                    .build();
                        })
                        .collect(Collectors.toList()))
                .xAxisLabel("Month")
                .yAxisLabel("On-Time Delivery Rate (%)")
                .showLegend(true)
                .build();
        
        section.addChart(efficiencyTrendChart);
        
        return section;
    }
    
    /**
     * Create the recommendations section
     */
    private ReportSection createRecommendationsSection(List<String> regions) {
        ReportSection section = ReportSection.builder()
                .id(UUID.randomUUID().toString())
                .title("Cross-Regional Recommendations")
                .description("Recommendations for performance improvement based on regional comparisons")
                .type(ReportSection.SectionType.RECOMMENDATIONS)
                .build();
        
        // Find top performers in different categories
        String topPerformanceRegion = getTopRegionForMetric(regions, "overall_performance");
        String topCostRegion = getTopRegionForMetric(regions, "cost_efficiency");
        String topSatisfactionRegion = getTopRegionForMetric(regions, "customer_satisfaction");
        String topGrowthRegion = getTopRegionForMetric(regions, "volume_growth");
        
        // Find bottom performers
        String bottomPerformanceRegion = getBottomRegionForMetric(regions, "overall_performance");
        String bottomCostRegion = getBottomRegionForMetric(regions, "cost_efficiency");
        
        // Add recommendation content
        section.setHtmlContent(
                "<div class=\"recommendations-container\">" +
                "<h4>Knowledge Sharing Opportunities</h4>" +
                "<p>Our analysis reveals significant opportunities for cross-regional learning:</p>" +
                "<ol>" +
                "<li><strong>Delivery Excellence Program</strong><br/>" +
                "Establish a structured knowledge transfer program from the " + topPerformanceRegion + " region to share " +
                "best practices in route planning, courier scheduling, and on-time delivery techniques.</li>" +
                "<li><strong>Cost Optimization Framework</strong><br/>" +
                "Implement the cost management approach used in " + topCostRegion + " across all regions, with a particular " +
                "focus on " + bottomCostRegion + ", which has the highest cost per delivery.</li>" +
                "<li><strong>Customer Experience Master Class</strong><br/>" +
                "Create a training program based on " + topSatisfactionRegion + "'s customer service model, focusing on " +
                "communication protocols, issue resolution processes, and customer engagement techniques.</li>" +
                "<li><strong>Growth Strategy Replication</strong><br/>" +
                "Analyze and replicate the successful market penetration and customer acquisition strategies from " + 
                topGrowthRegion + " across other regions with lower growth rates.</li>" +
                "</ol>" +
                "<h4>Implementation Strategy</h4>" +
                "<p>We recommend a phased implementation approach:</p>" +
                "<ol>" +
                "<li><strong>Phase 1: Assessment and Benchmarking (1-2 Months)</strong><br/>" +
                "Conduct detailed on-site assessments in top-performing regions to document processes and identify transferable practices.</li>" +
                "<li><strong>Phase 2: Pilot Programs (2-3 Months)</strong><br/>" +
                "Implement pilot programs in underperforming regions, starting with " + bottomPerformanceRegion + " for delivery excellence " +
                "and " + bottomCostRegion + " for cost optimization.</li>" +
                "<li><strong>Phase 3: Full Implementation (3-6 Months)</strong><br/>" +
                "Roll out comprehensive knowledge transfer programs across all regions, with detailed tracking and regular performance reviews.</li>" +
                "<li><strong>Phase 4: Ongoing Optimization (6+ Months)</strong><br/>" +
                "Establish a continuous improvement framework with regular cross-regional performance reviews and best practice sharing.</li>" +
                "</ol>" +
                "<h4>Regional-Specific Recommendations</h4>" +
                "<table class=\"table table-bordered\" style=\"width:100%; margin-top:15px;\">" +
                "<thead><tr><th>Region</th><th>Priority Focus Areas</th></tr></thead>" +
                "<tbody>" +
                generateRegionSpecificRecommendations(regions) +
                "</tbody></table>" +
                "</div>"
        );
        
        // Add expected impact content
        section.setConclusion(
                "Implementing these cross-regional knowledge sharing initiatives is projected to yield the following improvements:\n\n" +
                "• 12-15% improvement in overall performance in bottom quartile regions\n" +
                "• 8-10% reduction in cost per delivery for regions above the cost target\n" +
                "• 10-12% improvement in customer satisfaction in underperforming regions\n" +
                "• Reduction in performance gap between top and bottom regions by 40-50%\n\n" +
                "The estimated implementation cost is significantly outweighed by the projected annual savings and revenue improvements."
        );
        
        return section;
    }
    
    /**
     * Generate region-specific recommendation rows for the table
     */
    private String generateRegionSpecificRecommendations(List<String> regions) {
        StringBuilder sb = new StringBuilder();
        
        for (String region : regions) {
            List<String> recommendations = new ArrayList<>();
            
            double costPerDelivery = getRegionalMetric(region, "cost_per_delivery");
            double onTimeRate = getRegionalMetric(region, "on_time_delivery");
            double customerSat = getRegionalMetric(region, "customer_satisfaction");
            double routeEfficiency = getRegionalMetric(region, "route_efficiency");
            double courierUtilization = getRegionalMetric(region, "courier_utilization");
            
            // Add recommendations based on metrics
            if (costPerDelivery > 5.0) {
                recommendations.add("Cost optimization (currently $" + String.format("%.2f", costPerDelivery) + " per delivery)");
            }
            
            if (onTimeRate < 90.0) {
                recommendations.add("On-time delivery improvement (currently " + String.format("%.1f", onTimeRate) + "%)");
            }
            
            if (customerSat < 4.3) {
                recommendations.add("Customer satisfaction enhancement (currently " + String.format("%.1f", customerSat) + "/5)");
            }
            
            if (routeEfficiency < 82.0) {
                recommendations.add("Route optimization (currently " + String.format("%.1f", routeEfficiency) + "% efficient)");
            }
            
            if (courierUtilization < 80.0) {
                recommendations.add("Courier utilization improvement (currently " + String.format("%.1f", courierUtilization) + "%)");
            }
            
            // If no specific issues, add generic improvement
            if (recommendations.isEmpty()) {
                recommendations.add("Maintain performance and share best practices with other regions");
            }
            
            // Format as bullet points
            String formattedRecommendations = recommendations.stream()
                    .map(rec -> "• " + rec)
                    .collect(Collectors.joining("<br/>"));
            
            // Add row
            sb.append("<tr><td><strong>").append(region).append("</strong></td><td>")
              .append(formattedRecommendations).append("</td></tr>");
        }
        
        return sb.toString();
    }
    
    /**
     * Create key metrics rows for the comparison table
     */
    private List<Map<String, Object>> createKeyMetricsRows(List<String> regions) {
        List<Map<String, Object>> rows = new ArrayList<>();
        
        // Define metrics to include
        List<String> metricKeys = Arrays.asList(
                "overall_performance",
                "on_time_delivery",
                "avg_delivery_time",
                "cost_per_delivery",
                "customer_satisfaction",
                "courier_utilization",
                "route_efficiency",
                "first_attempt_success",
                "deliveries_per_courier",
                "volume_growth"
        );
        
        Map<String, String> metricNames = Map.of(
                "overall_performance", "Overall Performance Score",
                "on_time_delivery", "On-Time Delivery Rate (%)",
                "avg_delivery_time", "Avg. Delivery Time (min)",
                "cost_per_delivery", "Cost per Delivery ($)",
                "customer_satisfaction", "Customer Satisfaction (1-5)",
                "courier_utilization", "Courier Utilization (%)",
                "route_efficiency", "Route Efficiency (%)",
                "first_attempt_success", "First Attempt Success (%)",
                "deliveries_per_courier", "Deliveries per Courier/Day",
                "volume_growth", "Volume Growth YoY (%)"
        );
        
        Map<String, String> formats = Map.of(
                "overall_performance", "%.1f",
                "on_time_delivery", "%.1f%%",
                "avg_delivery_time", "%.1f",
                "cost_per_delivery", "$%.2f",
                "customer_satisfaction", "%.1f",
                "courier_utilization", "%.1f%%",
                "route_efficiency", "%.1f%%",
                "first_attempt_success", "%.1f%%",
                "deliveries_per_courier", "%.1f",
                "volume_growth", "%.1f%%"
        );
        
        Map<String, Boolean> highIsBetter = Map.of(
                "overall_performance", true,
                "on_time_delivery", true,
                "avg_delivery_time", false,
                "cost_per_delivery", false,
                "customer_satisfaction", true,
                "courier_utilization", true,
                "route_efficiency", true,
                "first_attempt_success", true,
                "deliveries_per_courier", true,
                "volume_growth", true
        );
        
        // Create a row for each metric
        for (String metricKey : metricKeys) {
            Map<String, Object> row = new HashMap<>();
            row.put("metric", metricNames.get(metricKey));
            
            // For each region, get the metric value and add formatting metadata
            Map<String, Double> regionValues = new HashMap<>();
            for (String region : regions) {
                double value = getRegionalMetric(region, metricKey);
                regionValues.put(region.toLowerCase(), value);
                
                // Store the raw value for comparison
                row.put(region.toLowerCase(), formatValue(value, formats.get(metricKey)));
            }
            
            // Find top and bottom values for conditional formatting
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("highIsBetter", highIsBetter.get(metricKey));
            
            // Find the top region for this metric
            String topRegion = regions.stream()
                    .min((r1, r2) -> {
                        double v1 = regionValues.getOrDefault(r1.toLowerCase(), 0.0);
                        double v2 = regionValues.getOrDefault(r2.toLowerCase(), 0.0);
                        // If lower is better, reverse the comparison
                        return highIsBetter.get(metricKey) 
                                ? Double.compare(v2, v1)  // Higher is better
                                : Double.compare(v1, v2); // Lower is better
                    })
                    .orElse(regions.get(0));
            
            // Find the bottom region for this metric
            String bottomRegion = regions.stream()
                    .min((r1, r2) -> {
                        double v1 = regionValues.getOrDefault(r1.toLowerCase(), 0.0);
                        double v2 = regionValues.getOrDefault(r2.toLowerCase(), 0.0);
                        // If lower is better, reverse the comparison
                        return highIsBetter.get(metricKey) 
                                ? Double.compare(v1, v2)  // Higher is better
                                : Double.compare(v2, v1); // Lower is better
                    })
                    .orElse(regions.get(regions.size() - 1));
            
            // Add metadata for conditional formatting
            for (String region : regions) {
                Map<String, Object> regionMetadata = new HashMap<>(metadata);
                regionMetadata.put("columnId", region.toLowerCase());
                regionMetadata.put("isTopValue", region.equalsIgnoreCase(topRegion));
                regionMetadata.put("isBottomValue", region.equalsIgnoreCase(bottomRegion));
                
                row.put(region.toLowerCase() + "_metadata", regionMetadata);
            }
            
            rows.add(row);
        }
        
        return rows;
    }
    
    /**
     * Create a row for the regional cost table
     */
    private Map<String, Object> createRegionalCostRow(String region) {
        Map<String, Object> row = new HashMap<>();
        row.put("region", region);
        
        // Set region-specific data
        double costPerDelivery = getRegionalMetric(region, "cost_per_delivery");
        double laborCost = getRegionalMetric(region, "labor_cost");
        double fuelCost = getRegionalMetric(region, "fuel_cost");
        double maintenanceCost = getRegionalMetric(region, "maintenance_cost");
        double otherCosts = getRegionalMetric(region, "other_costs");
        
        row.put("cost_per_delivery", costPerDelivery);
        row.put("labor_cost", laborCost);
        row.put("fuel_cost", fuelCost);
        row.put("maintenance_cost", maintenanceCost);
        row.put("other_costs", otherCosts);
        
        // Calculate vs target (target is $4.75)
        double vsTarget = ((costPerDelivery / 4.75) - 1.0) * 100.0;
        row.put("vs_target", vsTarget);
        
        return row;
    }
    
    /**
     * Get regional metric values based on region and metric
     */
    private double getRegionalMetric(String region, String metric) {
        // This would typically come from a database or service
        // Using realistic hardcoded values for the example
        
        switch (metric) {
            case "overall_performance":
                switch (region) {
                    case "NAM": return 92.5;
                    case "EUR": return 88.7;
                    case "APAC": return 85.3;
                    case "AFR": return 78.4;
                    case "LAT": return 83.6;
                    default: return 80.0 + random.nextDouble() * 15.0;
                }
                
            case "on_time_delivery":
                switch (region) {
                    case "NAM": return 94.5;
                    case "EUR": return 92.8;
                    case "APAC": return 87.3;
                    case "AFR": return 84.9;
                    case "LAT": return 89.2;
                    default: return 85.0 + random.nextDouble() * 10.0;
                }
                
            case "avg_delivery_time":
                switch (region) {
                    case "NAM": return 25.3;
                    case "EUR": return 27.1;
                    case "APAC": return 32.7;
                    case "AFR": return 35.4;
                    case "LAT": return 30.8;
                    default: return 25.0 + random.nextDouble() * 10.0;
                }
                
            case "cost_per_delivery":
                switch (region) {
                    case "NAM": return 4.82;
                    case "EUR": return 5.12;
                    case "APAC": return 4.28;
                    case "AFR": return 5.76;
                    case "LAT": return 4.95;
                    default: return 4.50 + random.nextDouble() * 1.50;
                }
                
            case "labor_cost":
                switch (region) {
                    case "NAM": return 2.67;
                    case "EUR": return 3.28;
                    case "APAC": return 2.15;
                    case "AFR": return 2.82;
                    case "LAT": return 2.54;
                    default: return 2.25 + random.nextDouble() * 1.00;
                }
                
            case "fuel_cost":
                switch (region) {
                    case "NAM": return 1.28;
                    case "EUR": return 1.08;
                    case "APAC": return 1.45;
                    case "AFR": return 1.96;
                    case "LAT": return 1.67;
                    default: return 1.25 + random.nextDouble() * 0.75;
                }
                
            case "maintenance_cost":
                switch (region) {
                    case "NAM": return 0.58;
                    case "EUR": return 0.52;
                    case "APAC": return 0.48;
                    case "AFR": return 0.67;
                    case "LAT": return 0.54;
                    default: return 0.50 + random.nextDouble() * 0.20;
                }
                
            case "other_costs":
                switch (region) {
                    case "NAM": return 0.29;
                    case "EUR": return 0.24;
                    case "APAC": return 0.20;
                    case "AFR": return 0.31;
                    case "LAT": return 0.20;
                    default: return 0.20 + random.nextDouble() * 0.15;
                }
                
            case "customer_satisfaction":
                switch (region) {
                    case "NAM": return 4.7;
                    case "EUR": return 4.5;
                    case "APAC": return 4.2;
                    case "AFR": return 4.0;
                    case "LAT": return 4.3;
                    default: return 4.0 + random.nextDouble() * 0.8;
                }
                
            case "courier_utilization":
                switch (region) {
                    case "NAM": return 87.0;
                    case "EUR": return 82.0;
                    case "APAC": return 76.0;
                    case "AFR": return 81.0;
                    case "LAT": return 85.0;
                    default: return 75.0 + random.nextDouble() * 15.0;
                }
                
            case "route_efficiency":
                switch (region) {
                    case "NAM": return 85.3;
                    case "EUR": return 83.2;
                    case "APAC": return 80.5;
                    case "AFR": return 78.4;
                    case "LAT": return 81.6;
                    default: return 75.0 + random.nextDouble() * 15.0;
                }
                
            case "first_attempt_success":
                switch (region) {
                    case "NAM": return 89.7;
                    case "EUR": return 87.5;
                    case "APAC": return 83.2;
                    case "AFR": return 80.5;
                    case "LAT": return 85.8;
                    default: return 80.0 + random.nextDouble() * 10.0;
                }
                
            case "deliveries_per_courier":
                switch (region) {
                    case "NAM": return 27.8;
                    case "EUR": return 25.3;
                    case "APAC": return 23.5;
                    case "AFR": return 21.2;
                    case "LAT": return 22.7;
                    default: return 20.0 + random.nextDouble() * 8.0;
                }
                
            case "volume_growth":
                switch (region) {
                    case "NAM": return 18.3;
                    case "EUR": return 15.6;
                    case "APAC": return 24.5;
                    case "AFR": return 12.8;
                    case "LAT": return 19.2;
                    default: return 10.0 + random.nextDouble() * 15.0;
                }
                
            case "failed_delivery":
                switch (region) {
                    case "NAM": return 1.2;
                    case "EUR": return 1.8;
                    case "APAC": return 3.5;
                    case "AFR": return 4.2;
                    case "LAT": return 2.4;
                    default: return 1.0 + random.nextDouble() * 3.5;
                }
                
            case "other_delivery_outcome":
                switch (region) {
                    case "NAM": return 0.5;
                    case "EUR": return 0.7;
                    case "APAC": return 1.0;
                    case "AFR": return 1.5;
                    case "LAT": return 0.8;
                    default: return 0.5 + random.nextDouble() * 1.0;
                }
                
            case "on_time_improvement":
                switch (region) {
                    case "NAM": return 5.2;
                    case "EUR": return 4.8;
                    case "APAC": return 6.5;
                    case "AFR": return 4.2;
                    case "LAT": return 5.8;
                    default: return 4.0 + random.nextDouble() * 3.0;
                }
                
            case "cost_improvement":
                switch (region) {
                    case "NAM": return 6.2;
                    case "EUR": return 4.8;
                    case "APAC": return 7.5;
                    case "AFR": return 3.2;
                    case "LAT": return 5.5;
                    default: return 3.0 + random.nextDouble() * 5.0;
                }
                
            case "satisfaction_improvement":
                switch (region) {
                    case "NAM": return 4.3;
                    case "EUR": return 3.8;
                    case "APAC": return 5.2;
                    case "AFR": return 3.5;
                    case "LAT": return 4.7;
                    default: return 3.0 + random.nextDouble() * 3.0;
                }
                
            default:
                return 50.0 + random.nextDouble() * 50.0;
        }
    }
    
    /**
     * Get the region score for metrics
     */
    private double getRegionScore(String region) {
        switch (region) {
            case "NAM": return 92.5;
            case "EUR": return 88.7;
            case "APAC": return 85.3;
            case "AFR": return 78.4;
            case "LAT": return 83.6;
            default: return 80.0 + random.nextDouble() * 15.0;
        }
    }
    
    /**
     * Get color for region based on its performance
     */
    private String getRegionColor(String region, List<String> regions) {
        // Color based on performance ranking
        List<String> sortedRegions = regions.stream()
                .sorted((r1, r2) -> Double.compare(getRegionScore(r2), getRegionScore(r1)))
                .collect(Collectors.toList());
        
        int index = sortedRegions.indexOf(region);
        
        if (index == 0) {
            return "#4CAF50"; // Top performer - green
        } else if (index == sortedRegions.size() - 1) {
            return "#F44336"; // Bottom performer - red
        } else if (index < sortedRegions.size() / 2) {
            return "#8BC34A"; // Above average - light green
        } else {
            return "#FF9800"; // Below average - orange
        }
    }
    
    /**
     * Get specific color for on-time delivery metric
     */
    private String getRegionOnTimeDeliveryColor(String region) {
        double value = getRegionalMetric(region, "on_time_delivery");
        
        if (value >= 90.0) {
            return "#4CAF50"; // Green
        } else if (value >= 85.0) {
            return "#8BC34A"; // Light green
        } else if (value >= 80.0) {
            return "#FFC107"; // Amber
        } else {
            return "#F44336"; // Red
        }
    }
    
    /**
     * Get specific color for delivery time metric
     */
    private String getRegionDeliveryTimeColor(String region) {
        double value = getRegionalMetric(region, "avg_delivery_time");
        
        if (value <= 26.0) {
            return "#4CAF50"; // Green
        } else if (value <= 30.0) {
            return "#8BC34A"; // Light green
        } else if (value <= 33.0) {
            return "#FFC107"; // Amber
        } else {
            return "#F44336"; // Red
        }
    }
    
    /**
     * Get specific color for customer satisfaction metric
     */
    private String getRegionCustomerSatisfactionColor(String region) {
        double value = getRegionalMetric(region, "customer_satisfaction");
        
        if (value >= 4.5) {
            return "#4CAF50"; // Green
        } else if (value >= 4.3) {
            return "#8BC34A"; // Light green
        } else if (value >= 4.0) {
            return "#FFC107"; // Amber
        } else {
            return "#F44336"; // Red
        }
    }
    
    /**
     * Get specific color for cost metric
     */
    private String getRegionCostColor(String region) {
        double value = getRegionalMetric(region, "cost_per_delivery");
        
        if (value <= 4.50) {
            return "#4CAF50"; // Green
        } else if (value <= 5.00) {
            return "#8BC34A"; // Light green
        } else if (value <= 5.50) {
            return "#FFC107"; // Amber
        } else {
            return "#F44336"; // Red
        }
    }
    
    /**
     * Get specific color for efficiency metric
     */
    private String getRegionEfficiencyColor(String region) {
        double value = getRegionalMetric(region, "deliveries_per_courier");
        
        if (value >= 25.0) {
            return "#4CAF50"; // Green
        } else if (value >= 23.0) {
            return "#8BC34A"; // Light green
        } else if (value >= 21.0) {
            return "#FFC107"; // Amber
        } else {
            return "#F44336"; // Red
        }
    }
    
    /**
     * Get specific color for utilization metric
     */
    private String getRegionUtilizationColor(String region) {
        double value = getRegionalMetric(region, "courier_utilization");
        
        if (value >= 85.0) {
            return "#4CAF50"; // Green
        } else if (value >= 80.0) {
            return "#8BC34A"; // Light green
        } else if (value >= 75.0) {
            return "#FFC107"; // Amber
        } else {
            return "#F44336"; // Red
        }
    }
    
    /**
     * Get specific color for route efficiency metric
     */
    private String getRegionRouteEfficiencyColor(String region) {
        double value = getRegionalMetric(region, "route_efficiency");
        
        if (value >= 85.0) {
            return "#4CAF50"; // Green
        } else if (value >= 80.0) {
            return "#8BC34A"; // Light green
        } else if (value >= 75.0) {
            return "#FFC107"; // Amber
        } else {
            return "#F44336"; // Red
        }
    }
    
    /**
     * Get specific color for first attempt success metric
     */
    private String getRegionFirstAttemptColor(String region) {
        double value = getRegionalMetric(region, "first_attempt_success");
        
        if (value >= 88.0) {
            return "#4CAF50"; // Green
        } else if (value >= 85.0) {
            return "#8BC34A"; // Light green
        } else if (value >= 80.0) {
            return "#FFC107"; // Amber
        } else {
            return "#F44336"; // Red
        }
    }
    
    /**
     * Get specific color for growth metric
     */
    private String getRegionGrowthColor(String region) {
        double value = getRegionalMetric(region, "volume_growth");
        
        if (value >= 20.0) {
            return "#4CAF50"; // Green
        } else if (value >= 15.0) {
            return "#8BC34A"; // Light green
        } else if (value >= 10.0) {
            return "#FFC107"; // Amber
        } else {
            return "#F44336"; // Red
        }
    }
    
    /**
     * Get the top region for a specific metric
     */
    private String getTopRegionForMetric(List<String> regions, String metric) {
        boolean highIsBetter = !metric.equals("cost_per_delivery") && !metric.equals("avg_delivery_time");
        
        return regions.stream()
                .max((r1, r2) -> {
                    double v1 = getRegionalMetric(r1, metric);
                    double v2 = getRegionalMetric(r2, metric);
                    return highIsBetter 
                            ? Double.compare(v1, v2) // Higher is better
                            : Double.compare(v2, v1); // Lower is better
                })
                .orElse(regions.get(0));
    }
    
    /**
     * Get the bottom region for a specific metric
     */
    private String getBottomRegionForMetric(List<String> regions, String metric) {
        boolean highIsBetter = !metric.equals("cost_per_delivery") && !metric.equals("avg_delivery_time");
        
        return regions.stream()
                .min((r1, r2) -> {
                    double v1 = getRegionalMetric(r1, metric);
                    double v2 = getRegionalMetric(r2, metric);
                    return highIsBetter 
                            ? Double.compare(v1, v2) // Higher is better
                            : Double.compare(v2, v1); // Lower is better
                })
                .orElse(regions.get(regions.size() - 1));
    }
    
    /**
     * Find a region with a specific attribute
     */
    private String getRegionWithAttribute(List<String> regions, String attribute, String defaultRegion) {
        switch (attribute) {
            case "fastest growing":
                return getTopRegionForMetric(regions, "volume_growth");
            case "most cost-efficient":
                return getTopRegionForMetric(regions, "cost_per_delivery");
            case "highest customer satisfaction":
                return getTopRegionForMetric(regions, "customer_satisfaction");
            case "highest cost per delivery":
                return getBottomRegionForMetric(regions, "cost_per_delivery");
            default:
                return defaultRegion;
        }
    }
    
    /**
     * Format a value with a specific format
     */
    private String formatValue(double value, String format) {
        return String.format(format, value);
    }
    
    /**
     * Generate radar chart data for a region
     */
    private List<Object> getRegionalRadarData(String region) {
        List<Object> data = new ArrayList<>();
        
        // Generate scaled values (0-100) for radar chart
        switch (region) {
            case "NAM":
                data.add(94);
                data.add(85);
                data.add(96);
                data.add(92);
                data.add(95);
                data.add(87);
                break;
            case "EUR":
                data.add(92);
                data.add(80);
                data.add(92);
                data.add(87);
                data.add(93);
                data.add(80);
                break;
            case "APAC":
                data.add(85);
                data.add(94);
                data.add(86);
                data.add(84);
                data.add(87);
                data.add(96);
                break;
            case "AFR":
                data.add(78);
                data.add(72);
                data.add(82);
                data.add(75);
                data.add(85);
                data.add(75);
                break;
            case "LAT":
                data.add(86);
                data.add(82);
                data.add(88);
                data.add(82);
                data.add(89);
                data.add(90);
                break;
            default:
                data.add(80 + random.nextDouble() * 15);
                data.add(75 + random.nextDouble() * 20);
                data.add(80 + random.nextDouble() * 15);
                data.add(75 + random.nextDouble() * 15);
                data.add(80 + random.nextDouble() * 15);
                data.add(75 + random.nextDouble() * 20);
                break;
        }
        
        return data;
    }
    
    /**
     * Generate monthly trend data for a region
     */
    private List<Object> generateMonthlyTrendData(String region, String metricType) {
        List<Object> data = new ArrayList<>();
        
        // Different trend patterns based on the region
        switch (region) {
            case "NAM":
                if ("volume".equals(metricType)) {
                    data.add(100.0);
                    data.add(103.5);
                    data.add(107.2);
                    data.add(111.8);
                    data.add(115.4);
                    data.add(118.3);
                } else { // on_time
                    data.add(89.3);
                    data.add(90.5);
                    data.add(91.8);
                    data.add(92.7);
                    data.add(93.8);
                    data.add(94.5);
                }
                break;
            case "EUR":
                if ("volume".equals(metricType)) {
                    data.add(100.0);
                    data.add(102.8);
                    data.add(105.3);
                    data.add(108.7);
                    data.add(112.2);
                    data.add(115.6);
                } else { // on_time
                    data.add(88.0);
                    data.add(89.2);
                    data.add(90.4);
                    data.add(91.5);
                    data.add(92.2);
                    data.add(92.8);
                }
                break;
            case "APAC":
                if ("volume".equals(metricType)) {
                    data.add(100.0);
                    data.add(104.8);
                    data.add(109.5);
                    data.add(114.7);
                    data.add(119.8);
                    data.add(124.5);
                } else { // on_time
                    data.add(80.8);
                    data.add(82.5);
                    data.add(84.2);
                    data.add(85.7);
                    data.add(86.5);
                    data.add(87.3);
                }
                break;
            case "AFR":
                if ("volume".equals(metricType)) {
                    data.add(100.0);
                    data.add(102.2);
                    data.add(104.5);
                    data.add(106.8);
                    data.add(109.5);
                    data.add(112.8);
                } else { // on_time
                    data.add(80.7);
                    data.add(81.5);
                    data.add(82.4);
                    data.add(83.5);
                    data.add(84.2);
                    data.add(84.9);
                }
                break;
            case "LAT":
                if ("volume".equals(metricType)) {
                    data.add(100.0);
                    data.add(103.8);
                    data.add(107.5);
                    data.add(111.3);
                    data.add(115.2);
                    data.add(119.2);
                } else { // on_time
                    data.add(84.5);
                    data.add(85.8);
                    data.add(87.2);
                    data.add(88.1);
                    data.add(88.7);
                    data.add(89.2);
                }
                break;
            default:
                // Generate random increasing trend
                double value = "volume".equals(metricType) ? 100.0 : 85.0;
                data.add(value);
                for (int i = 1; i < 6; i++) {
                    value += "volume".equals(metricType) ? 
                            2.0 + random.nextDouble() * 3.0 : 
                            0.5 + random.nextDouble() * 1.0;
                    data.add(value);
                }
                break;
        }
        
        return data;
    }
}