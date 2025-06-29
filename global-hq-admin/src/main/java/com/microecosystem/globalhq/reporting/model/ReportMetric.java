package com.microecosystem.globalhq.reporting.model;

/**
 * Represents a metric displayed in a report section.
 */
public class ReportMetric {
    private String name;
    private String value;
    private double changePercentage;
    
    public ReportMetric() {
    }
    
    public ReportMetric(String name, String value, double changePercentage) {
        this.name = name;
        this.value = value;
        this.changePercentage = changePercentage;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public double getChangePercentage() {
        return changePercentage;
    }

    public void setChangePercentage(double changePercentage) {
        this.changePercentage = changePercentage;
    }
}
