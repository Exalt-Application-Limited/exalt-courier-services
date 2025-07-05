package com.gogidix.courier.regionaladmin.consumer;

import com.socialecommerceecosystem.regionaladmin.service.ResourceAllocationSyncService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResourceAllocationEventConsumerTest {

    @Mock
    private ResourceAllocationSyncService resourceAllocationSyncService;

    private ResourceAllocationEventConsumer resourceAllocationEventConsumer;

    @BeforeEach
    void setUp() {
        resourceAllocationEventConsumer = new ResourceAllocationEventConsumer();
        // Use reflection to set the autowired field
        try {
            java.lang.reflect.Field field = ResourceAllocationEventConsumer.class.getDeclaredField("resourceAllocationSyncService");
            field.setAccessible(true);
            field.set(resourceAllocationEventConsumer, resourceAllocationSyncService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set mock service", e);
        }
    }

    @Test
    void consumeAllocationCreatedEvent() {
        // Arrange
        Map<String, Object> allocationData = new HashMap<>();
        allocationData.put("id", 1L);
        allocationData.put("resourceType", "COURIER");
        allocationData.put("quantity", 10);
        
        // Act
        resourceAllocationEventConsumer.consumeResourceAllocationEvent(allocationData, "ALLOCATION_CREATED.1", 0, "resource-allocation-events");
        
        // Assert
        verify(resourceAllocationSyncService, times(1)).handleAllocationCreated(allocationData);
        verify(resourceAllocationSyncService, never()).handleAllocationUpdated(any());
    }

    @Test
    void consumeAllocationUpdatedEvent() {
        // Arrange
        Map<String, Object> allocationData = new HashMap<>();
        allocationData.put("id", 1L);
        allocationData.put("resourceType", "COURIER");
        allocationData.put("quantity", 15);
        
        // Act
        resourceAllocationEventConsumer.consumeResourceAllocationEvent(allocationData, "ALLOCATION_UPDATED.1", 0, "resource-allocation-events");
        
        // Assert
        verify(resourceAllocationSyncService, times(1)).handleAllocationUpdated(allocationData);
        verify(resourceAllocationSyncService, never()).handleAllocationCreated(any());
    }

    @Test
    void consumeAllocationActivatedEvent() {
        // Arrange
        Map<String, Object> allocationData = new HashMap<>();
        allocationData.put("id", 1L);
        
        // Act
        resourceAllocationEventConsumer.consumeResourceAllocationEvent(allocationData, "ALLOCATION_ACTIVATED.1", 0, "resource-allocation-events");
        
        // Assert
        verify(resourceAllocationSyncService, times(1)).handleAllocationActivated(allocationData);
    }

    @Test
    void consumeAllocationDeactivatedEvent() {
        // Arrange
        Map<String, Object> allocationData = new HashMap<>();
        allocationData.put("id", 1L);
        
        // Act
        resourceAllocationEventConsumer.consumeResourceAllocationEvent(allocationData, "ALLOCATION_DEACTIVATED.1", 0, "resource-allocation-events");
        
        // Assert
        verify(resourceAllocationSyncService, times(1)).handleAllocationDeactivated(allocationData);
    }

    @Test
    void consumeAllocationExpiredEvent() {
        // Arrange
        Map<String, Object> allocationData = new HashMap<>();
        allocationData.put("id", 1L);
        
        // Act
        resourceAllocationEventConsumer.consumeResourceAllocationEvent(allocationData, "ALLOCATION_EXPIRED.1", 0, "resource-allocation-events");
        
        // Assert
        verify(resourceAllocationSyncService, times(1)).handleAllocationExpired(allocationData);
    }

    @Test
    void consumeAllocationPlanExecutedEvent() {
        // Arrange
        Map<String, Object> planData = new HashMap<>();
        planData.put("planId", 1L);
        planData.put("planName", "Test Plan");
        
        // Act
        resourceAllocationEventConsumer.consumeResourceAllocationEvent(planData, "ALLOCATION_PLAN_EXECUTED.1", 0, "resource-allocation-events");
        
        // Assert
        verify(resourceAllocationSyncService, times(1)).handleAllocationPlanExecuted(planData);
    }

    @Test
    void consumeUnknownEventType() {
        // Arrange
        Map<String, Object> allocationData = new HashMap<>();
        allocationData.put("id", 1L);
        
        // Act
        resourceAllocationEventConsumer.consumeResourceAllocationEvent(allocationData, "UNKNOWN_EVENT.1", 0, "resource-allocation-events");
        
        // Assert
        verify(resourceAllocationSyncService, never()).handleAllocationCreated(any());
        verify(resourceAllocationSyncService, never()).handleAllocationUpdated(any());
        verify(resourceAllocationSyncService, never()).handleAllocationActivated(any());
        verify(resourceAllocationSyncService, never()).handleAllocationDeactivated(any());
        verify(resourceAllocationSyncService, never()).handleAllocationExpired(any());
        verify(resourceAllocationSyncService, never()).handleAllocationPlanExecuted(any());
    }
}
