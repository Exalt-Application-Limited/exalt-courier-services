package com.gogidix.courier.regionaladmin.config.dashboard;

import com.microecosystem.courier.shared.dashboard.DashboardDataAggregationService;
import com.microecosystem.courier.shared.dashboard.DashboardDataHandler;
import com.microecosystem.courier.shared.dashboard.DashboardDataTransfer;
import com.microecosystem.courier.shared.dashboard.DataType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test class for RegionalDashboardDataAggregationHandler.
 * Tests data aggregation and flow between dashboard levels.
 */
@ExtendWith(MockitoExtension.class)
public class RegionalDashboardDataAggregationIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(RegionalDashboardDataAggregationIntegrationTest.class);
    
    @Mock
    private DashboardDataAggregationService aggregationService;
    
    @Captor
    private ArgumentCaptor<DashboardDataHandler> handlerCaptor;
    
    @Captor
    private ArgumentCaptor<Map<String, Object>> dataCaptor;
    
    @Captor
    private ArgumentCaptor<Map<String, String>> metadataCaptor;
    
    private RegionalDashboardDataAggregationHandler aggregationHandler;
    
    @BeforeEach
    public void setup() {
        // Set up mock behavior for the aggregation service
        when(aggregationService.schedulePeriodicAggregation(
                anyString(), anyString(), anyLong(), any(DashboardDataHandler.class)))
                .thenReturn(java.util.UUID.randomUUID().toString());
        
        when(aggregationService.scheduleDataTransferTask(
                anyString(), anyString(), any(), anyLong()))
                .thenReturn(java.util.UUID.randomUUID().toString());
        
        // Create the handler
        aggregationHandler = new RegionalDashboardDataAggregationHandler(aggregationService);
        
        // Initialize the handler
        aggregationHandler.init();
    }
    
    @Test
    public void testAggregationHandlersRegistered() {
        // Trigger the application ready event to register handlers
        aggregationHandler.onApplicationReady();
        
        // Verify that aggregation handlers were scheduled for different data types
        verify(aggregationService).schedulePeriodicAggregation(
                eq(DataType.DELIVERY_METRICS), eq("BRANCH"), eq(3 * 60 * 1000L), any());
        
        verify(aggregationService).schedulePeriodicAggregation(
                eq(DataType.DRIVER_PERFORMANCE), eq("BRANCH"), eq(10 * 60 * 1000L), any());
        
        verify(aggregationService).schedulePeriodicAggregation(
                eq(DataType.FINANCIAL_METRICS), eq("BRANCH"), eq(15 * 60 * 1000L), any());
        
        verify(aggregationService).schedulePeriodicAggregation(
                eq(DataType.OPERATIONAL_METRICS), eq("BRANCH"), eq(5 * 60 * 1000L), any());
        
        verify(aggregationService).schedulePeriodicAggregation(
                eq(DataType.CUSTOMER_SATISFACTION), eq("BRANCH"), eq(30 * 60 * 1000L), any());
        
        verify(aggregationService).schedulePeriodicAggregation(
                eq(DataType.SYSTEM_HEALTH), eq("BRANCH"), eq(1 * 60 * 1000L), any());
    }
    
    @Test
    public void testForwardingTasksRegistered() {
        // Trigger the application ready event to register handlers
        aggregationHandler.onApplicationReady();
        
        // Verify that forwarding tasks were scheduled for different data types
        verify(aggregationService).scheduleDataTransferTask(
                eq(DataType.DELIVERY_METRICS), eq("GLOBAL"), eq(null), eq(5 * 60 * 1000L));
        
        verify(aggregationService).scheduleDataTransferTask(
                eq(DataType.DRIVER_PERFORMANCE), eq("GLOBAL"), eq(null), eq(15 * 60 * 1000L));
        
        verify(aggregationService).scheduleDataTransferTask(
                eq(DataType.FINANCIAL_METRICS), eq("GLOBAL"), eq(null), eq(30 * 60 * 1000L));
        
        verify(aggregationService).scheduleDataTransferTask(
                eq(DataType.OPERATIONAL_METRICS), eq("GLOBAL"), eq(null), eq(10 * 60 * 1000L));
        
        verify(aggregationService).scheduleDataTransferTask(
                eq(DataType.CUSTOMER_SATISFACTION), eq("GLOBAL"), eq(null), eq(60 * 60 * 1000L));
        
        verify(aggregationService).scheduleDataTransferTask(
                eq(DataType.SYSTEM_HEALTH), eq("GLOBAL"), eq(null), eq(2 * 60 * 1000L));
    }
    
    @Test
    public void testHandleDeliveryMetricsData() {
        // Trigger the application ready event to register handlers
        aggregationHandler.onApplicationReady();
        
        // Capture the handler registered for delivery metrics
        verify(aggregationService).schedulePeriodicAggregation(
                eq(DataType.DELIVERY_METRICS), eq("BRANCH"), eq(3 * 60 * 1000L), handlerCaptor.capture());
        
        // Create test data
        Map<String, Object> testData = new HashMap<>();
        testData.put("totalDeliveries", 12500);
        testData.put("onTimeDeliveryRate", 95.8);
        
        Map<String, String> testMetadata = new HashMap<>();
        testMetadata.put("aggregatedFrom", "5");
        testMetadata.put("timestamp", String.valueOf(System.currentTimeMillis()));
        
        DashboardDataTransfer dataTransfer = new DashboardDataTransfer(DataType.DELIVERY_METRICS, testData, testMetadata);
        
        // Invoke the captured handler
        DashboardDataHandler handler = handlerCaptor.getValue();
        handler.handleData(dataTransfer);
        
        // Verify that the data was stored for later forwarding to Global HQ
        verify(aggregationService).storeAggregatedData(
                eq(DataType.DELIVERY_METRICS), 
                dataCaptor.capture(), 
                metadataCaptor.capture());
        
        // Verify the data was correctly passed through
        assertEquals(testData, dataCaptor.getValue());
        assertEquals(testMetadata, metadataCaptor.getValue());
    }
    
    @Test
    public void testHandleSystemHealthData() {
        // Trigger the application ready event to register handlers
        aggregationHandler.onApplicationReady();
        
        // Capture the handler registered for system health
        verify(aggregationService).schedulePeriodicAggregation(
                eq(DataType.SYSTEM_HEALTH), eq("BRANCH"), eq(1 * 60 * 1000L), handlerCaptor.capture());
        
        // Create test data
        Map<String, Object> testData = new HashMap<>();
        testData.put("systemUptime", 99.95);
        testData.put("averageResponseTime", 125.3);
        
        Map<String, String> testMetadata = new HashMap<>();
        testMetadata.put("aggregatedFrom", "6");
        testMetadata.put("timestamp", String.valueOf(System.currentTimeMillis()));
        
        DashboardDataTransfer dataTransfer = new DashboardDataTransfer(DataType.SYSTEM_HEALTH, testData, testMetadata);
        
        // Invoke the captured handler
        DashboardDataHandler handler = handlerCaptor.getValue();
        handler.handleData(dataTransfer);
        
        // Verify that the data was stored for later forwarding to Global HQ
        verify(aggregationService).storeAggregatedData(
                eq(DataType.SYSTEM_HEALTH), 
                dataCaptor.capture(), 
                metadataCaptor.capture());
        
        // Verify the data was correctly passed through
        assertEquals(testData, dataCaptor.getValue());
        assertEquals(testMetadata, metadataCaptor.getValue());
    }
    
    @Test
    public void testHandleFinancialMetricsData() {
        // Trigger the application ready event to register handlers
        aggregationHandler.onApplicationReady();
        
        // Capture the handler registered for financial metrics
        verify(aggregationService).schedulePeriodicAggregation(
                eq(DataType.FINANCIAL_METRICS), eq("BRANCH"), eq(15 * 60 * 1000L), handlerCaptor.capture());
        
        // Create test data
        Map<String, Object> testData = new HashMap<>();
        testData.put("totalRevenue", 567890.45);
        testData.put("operatingCosts", 324567.89);
        
        Map<String, String> testMetadata = new HashMap<>();
        testMetadata.put("aggregatedFrom", "4");
        testMetadata.put("timestamp", String.valueOf(System.currentTimeMillis()));
        
        DashboardDataTransfer dataTransfer = new DashboardDataTransfer(DataType.FINANCIAL_METRICS, testData, testMetadata);
        
        // Invoke the captured handler
        DashboardDataHandler handler = handlerCaptor.getValue();
        handler.handleData(dataTransfer);
        
        // Verify that the data was stored for later forwarding to Global HQ
        verify(aggregationService).storeAggregatedData(
                eq(DataType.FINANCIAL_METRICS), 
                dataCaptor.capture(), 
                metadataCaptor.capture());
        
        // Verify the data was correctly passed through
        assertEquals(testData, dataCaptor.getValue());
        assertEquals(testMetadata, metadataCaptor.getValue());
    }
    
    @Test
    public void testCleanupCancelsSchedules() {
        // Setup some schedule IDs
        aggregationHandler.onApplicationReady();
        
        // Call cleanup
        aggregationHandler.cleanup();
        
        // Verify that all scheduled aggregations were cancelled
        verify(aggregationService, times(6)).cancelPeriodicAggregation(anyString());
    }
}
