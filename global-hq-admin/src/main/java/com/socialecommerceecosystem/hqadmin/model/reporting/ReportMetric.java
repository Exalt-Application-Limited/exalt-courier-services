package com.gogidix.courier.hqadmin.model.reporting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model for a metric in a report section
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportMetric {
    private String name;
    private String displayName;
    private String description;
    private Object value;
    private String unit;
    private Double changeValue;
    private Double changePercentage;
    private String trend;
    private String status;
    private String category;
    private String backgroundColor;
    private String textColor;
    private Integer formatPrecision;
    
    /**
     * Gets the formatted value as a string with the unit
     */
    public String getFormattedValue() {
        if (value == null) {
            return "";
        }
        
        String formattedValue;
        if (value instanceof Number) {
            if (formatPrecision != null) {
                formattedValue = String.format("%." + formatPrecision + "f", ((Number) value).doubleValue());
            } else {
                formattedValue = value.toString();
            }
        } else {
            formattedValue = value.toString();
        }
        
        if (unit != null && !unit.isEmpty()) {
            return formattedValue + " " + unit;
        } else {
            return formattedValue;
        }
    }
    
    /**
     * Gets the formatted change value with a sign
     */
    public String getFormattedChange() {
        if (changeValue == null) {
            return "";
        }
        
        String sign = changeValue >= 0 ? "+" : "";
        
        if (formatPrecision != null) {
            return sign + String.format("%." + formatPrecision + "f", changeValue) + 
                    (unit != null && !unit.isEmpty() ? " " + unit : "");
        } else {
            return sign + changeValue + 
                    (unit != null && !unit.isEmpty() ? " " + unit : "");
        }
    }
    
    /**
     * Gets the formatted change percentage with a sign
     */
    public String getFormattedChangePercentage() {
        if (changePercentage == null) {
            return "";
        }
        
        String sign = changePercentage >= 0 ? "+" : "";
        
        if (formatPrecision != null) {
            return sign + String.format("%." + formatPrecision + "f", changePercentage) + "%";
        } else {
            return sign + changePercentage + "%";
        }
    }
}