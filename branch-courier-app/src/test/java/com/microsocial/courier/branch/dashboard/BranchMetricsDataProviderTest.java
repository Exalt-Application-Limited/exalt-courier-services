package com.exalt.courier.courier.branch.dashboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.SettableListenableFuture;

import com.microsocial.courier.branch.dashboard.model.BranchMetricsData;
import com.microsocial.courier.branch.dashboard.model.DeliveryMetrics;
import com.microsocial.courier.branch.dashboard.model.PerformanceMetrics;
import com.microsocial.courier.branch.dashboard.model.ResourceMetrics;

import java.util.concurrent.CompletableFuture;

@ExtendWith(MockitoExtension.class)
public class BranchMetricsDataProviderTest {

    private BranchMetricsDataProvider branchMetricsDataProvider;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private BranchDataCacheService dataCacheService;

    private final String branchMetricsTopic = "test-branch-metrics";
    private final String branchId = "test-branch-id";
    private final String regionId = "test-region-id";

    @BeforeEach
    public void setup() {
        branchMetricsDataProvider = new BranchMetricsDataProvider(
                kafkaTemplate,
                branchMetricsTopic,
                branchId,
                regionId);
        
        // Using reflection to replace the dataCacheService with our mock
        try {
            java.lang.reflect.Field field = BranchMetricsDataProvider.class.getDeclaredField("dataCacheService");
            field.setAccessible(true);
            field.set(branchMetricsDataProvider, dataCacheService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mock dataCacheService", e);
        }
    }

    @Test
    public void testCollectBranchMetrics() {
        BranchMetricsData metricsData = branchMetricsDataProvider.collectBranchMetrics();

        assertNotNull(metricsData);
        assertEquals(branchId, metricsData.getBranchId());
        assertEquals(regionId, metricsData.getRegionId());
        assertNotNull(metricsData.getTimestamp());
        assertNotNull(metricsData.getDeliveryMetrics());
        assertNotNull(metricsData.getPerformanceMetrics());
        assertNotNull(metricsData.getResourceMetrics());
    }

    @Test
    public void testSendMetricsToRegional() {
        // Create test metrics
        BranchMetricsData metricsData = new BranchMetricsData();
        metricsData.setMetricsId("test-metrics-id");
        metricsData.setBranchId(branchId);
        metricsData.setRegionId(regionId);
        
        // Setup metrics component data
        DeliveryMetrics deliveryMetrics = new DeliveryMetrics();
        deliveryMetrics.setTotalDeliveriesCompleted(100);
        metricsData.setDeliveryMetrics(deliveryMetrics);
        
        PerformanceMetrics performanceMetrics = new PerformanceMetrics();
        performanceMetrics.setActiveCouriers(10);
        metricsData.setPerformanceMetrics(performanceMetrics);
        
        ResourceMetrics resourceMetrics = new ResourceMetrics();
        resourceMetrics.setVehiclesInUse(8);
        metricsData.setResourceMetrics(resourceMetrics);

        // Mock Kafka response
        CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(null);
        when(kafkaTemplate.send(anyString(), anyString(), any(BranchMetricsData.class))).thenReturn(future);

        // Call the method
        branchMetricsDataProvider.sendMetricsToRegional(metricsData);

        // Verify interactions
        verify(dataCacheService).cacheOutgoingMetrics(metricsData);
        verify(kafkaTemplate).send(eq(branchMetricsTopic), eq(branchId), eq(metricsData));
    }

    @Test
    public void testProvideMetricsOnDemand() {
        BranchMetricsData metricsData = branchMetricsDataProvider.provideMetricsOnDemand();

        assertNotNull(metricsData);
        assertEquals(branchId, metricsData.getBranchId());
        assertEquals(regionId, metricsData.getRegionId());
        assertNotNull(metricsData.getTimestamp());
    }
} 