package com.gogidix.courier.shared.dashboard.impl;

import com.gogidix.courier.shared.dashboard.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Implementation of the DashboardDataAggregationService.
 * Uses the DashboardCommunicationService for messaging between dashboard levels.
 */
@Service
public class DashboardDataAggregationServiceImpl implements DashboardDataAggregationService {

    private static final Logger logger = LoggerFactory.getLogger(DashboardDataAggregationServiceImpl.class);
    
    @Autowired
    private DashboardCommunicationService communicationService;
    
    private final Map<String, DashboardDataProvider> dataProviders = new ConcurrentHashMap<>();
    private final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    private final Map<String, CompletableFuture<DashboardDataTransfer>> pendingRequests = new ConcurrentHashMap<>();
    
    private TaskScheduler taskScheduler;
    
    @PostConstruct
    public void init() {
        logger.info("Initializing dashboard data aggregation service");
        
        // Initialize thread pool for scheduled tasks
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(5);
        scheduler.setThreadNamePrefix("dashboard-aggregation-");
        scheduler.initialize();
        this.taskScheduler = scheduler;
        
        // Register message handler for data requests and responses
        communicationService.registerMessageHandler(this::handleCommunicationMessage);
    }
    
    @PreDestroy
    public void cleanup() {
        logger.info("Shutting down dashboard data aggregation service");
        
        // Cancel all scheduled tasks
        scheduledTasks.values().forEach(future -> future.cancel(false));
        scheduledTasks.clear();
    }
    
    @Override
    public CompletableFuture<Boolean> sendDataUp(DashboardDataTransfer data) {
        // Determine the appropriate target level
        String currentLevel = communicationService.getCurrentDashboardLevel();
        String targetLevel = getNextHigherLevel(currentLevel);
        
        if (targetLevel == null) {
            logger.warn("Cannot send data up from level: {} (already at top level)", currentLevel);
            CompletableFuture<Boolean> failedFuture = new CompletableFuture<>();
            failedFuture.complete(false);
            return failedFuture;
        }
        
        // Set source information if not already set
        if (data.getSourceLevel() == null) {
            data.setSourceLevel(currentLevel);
        }
        if (data.getSourceId() == null) {
            data.setSourceId(communicationService.getCurrentDashboardId());
        }
        
        // Set target information
        data.setTargetLevel(targetLevel);
        data.setTargetId("all"); // Send to all dashboards at the target level
        
        // Convert to message and send
        DashboardMessage message = createDataTransferMessage(data);
        return communicationService.sendMessage(message);
    }
    
    @Override
    public CompletableFuture<DashboardDataTransfer> requestAndAggregateData(
            String dataType, String filterCriteria, String targetLevel, 
            List<String> targetIds, long timeoutMs) {
        
        // Create a request ID
        String requestId = UUID.randomUUID().toString();
        
        // Create data request message
        DashboardMessage requestMessage = DashboardMessage.builder()
                .id(requestId)
                .sourceLevel(communicationService.getCurrentDashboardLevel())
                .sourceId(communicationService.getCurrentDashboardId())
                .targetLevel(targetLevel)
                .targetId(targetIds != null && !targetIds.isEmpty() ? targetIds.get(0) : "all")
                .messageType(MessageType.DATA_REQUEST)
                .subject("Data request: " + dataType)
                .content(filterCriteria)
                .requiresAcknowledgment(true)
                .priority(7) // Higher priority for data requests
                .build();
        
        // Add metadata for data type
        Map<String, String> metadata = new HashMap<>();
        metadata.put("dataType", dataType);
        requestMessage.setMetadata(metadata);
        
        // Create future for the response
        CompletableFuture<DashboardDataTransfer> responseFuture = new CompletableFuture<>();
        pendingRequests.put(requestId, responseFuture);
        
        // Set timeout
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(() -> {
            CompletableFuture<DashboardDataTransfer> future = pendingRequests.remove(requestId);
            if (future != null && !future.isDone()) {
                future.completeExceptionally(new TimeoutException("Data request timed out after " + timeoutMs + " ms"));
            }
            scheduler.shutdown();
        }, timeoutMs, TimeUnit.MILLISECONDS);
        
        // Send request to each target
        if (targetIds == null || targetIds.isEmpty()) {
            // Broadcast to all instances of the target level
            communicationService.sendMessage(requestMessage);
        } else {
            // Send to each specific target
            for (String targetId : targetIds) {
                DashboardMessage targetMessage = copyMessageWithNewTarget(requestMessage, targetLevel, targetId);
                communicationService.sendMessage(targetMessage);
            }
        }
        
        return responseFuture;
    }
    
    @Override
    public String registerDataProvider(String dataType, DashboardDataProvider provider) {
        String registrationId = dataType + "-" + UUID.randomUUID().toString();
        dataProviders.put(registrationId, provider);
        logger.debug("Registered data provider for type {} with ID: {}", dataType, registrationId);
        return registrationId;
    }
    
    @Override
    public boolean unregisterDataProvider(String registrationId) {
        boolean removed = dataProviders.remove(registrationId) != null;
        if (removed) {
            logger.debug("Unregistered data provider with ID: {}", registrationId);
        } else {
            logger.warn("Failed to unregister data provider with ID: {} (not found)", registrationId);
        }
        return removed;
    }
    
    @Override
    public List<String> getAvailableDataTypes(String targetLevel) {
        // Create request for available data types
        DashboardMessage request = DashboardMessage.builder()
                .id(UUID.randomUUID().toString())
                .sourceLevel(communicationService.getCurrentDashboardLevel())
                .sourceId(communicationService.getCurrentDashboardId())
                .targetLevel(targetLevel)
                .targetId("all")
                .messageType(MessageType.DATA_REQUEST)
                .subject("Available data types request")
                .content("list-available-data-types")
                .requiresAcknowledgment(true)
                .build();
        
        try {
            // Send request and wait for responses
            CompletableFuture<Boolean> sendResult = communicationService.sendMessage(request);
            if (!sendResult.get(5, TimeUnit.SECONDS)) {
                logger.error("Failed to send available data types request");
                return Collections.emptyList();
            }
            
            // Wait for responses (this is a simplified approach; in a real system, you would
            // have a more sophisticated mechanism for collecting and aggregating responses)
            Thread.sleep(1000);
            
            // Return the collected data types (placeholder implementation)
            return Arrays.asList(
                    DataType.DELIVERY_METRICS,
                    DataType.DRIVER_PERFORMANCE,
                    DataType.OPERATIONAL_METRICS
            );
        } catch (Exception e) {
            logger.error("Error getting available data types", e);
            return Collections.emptyList();
        }
    }
    
    @Override
    public DashboardDataTransfer aggregateData(List<DashboardDataTransfer> dataTransfers) {
        if (dataTransfers == null || dataTransfers.isEmpty()) {
            return null;
        }
        
        // Use the first transfer as a base for the aggregated result
        DashboardDataTransfer first = dataTransfers.get(0);
        String dataType = first.getDataType();
        
        // Validate all transfers have the same data type
        for (DashboardDataTransfer transfer : dataTransfers) {
            if (!dataType.equals(transfer.getDataType())) {
                logger.warn("Cannot aggregate data transfers with different data types");
                return null;
            }
        }
        
        // Create a new transfer for the aggregated data
        DashboardDataTransfer aggregated = DashboardDataTransfer.builder()
                .id(UUID.randomUUID().toString())
                .sourceLevel(communicationService.getCurrentDashboardLevel())
                .sourceId(communicationService.getCurrentDashboardId())
                .dataType(dataType)
                .aggregated(true)
                .build();
        
        // Create a new map for aggregated data
        Map<String, Object> aggregatedData = new HashMap<>();
        
        // Merge all data maps
        for (DashboardDataTransfer transfer : dataTransfers) {
            if (transfer.getData() != null) {
                // Basic aggregation strategy - implement more sophisticated logic based on data type
                for (Map.Entry<String, Object> entry : transfer.getData().entrySet()) {
                    aggregateDataValue(aggregatedData, entry.getKey(), entry.getValue(), dataType);
                }
            }
        }
        
        aggregated.setData(aggregatedData);
        
        // Add metadata about the aggregation
        Map<String, String> metadata = new HashMap<>();
        metadata.put("aggregatedFrom", String.valueOf(dataTransfers.size()));
        metadata.put("aggregatedAt", Instant.now().toString());
        aggregated.setMetadata(metadata);
        
        return aggregated;
    }
    
    @Override
    public String schedulePeriodicAggregation(String dataType, String targetLevel, 
                                            long intervalMs, DashboardDataHandler handler) {
        String scheduleId = dataType + "-" + targetLevel + "-" + UUID.randomUUID().toString();
        
        // Schedule the task
        ScheduledFuture<?> future = taskScheduler.scheduleAtFixedRate(() -> {
            logger.debug("Executing scheduled data aggregation: {}", scheduleId);
            
            requestAndAggregateData(dataType, null, targetLevel, null, 5000)
                .thenAccept(handler::handleData)
                .exceptionally(ex -> {
                    logger.error("Error in scheduled data aggregation: {}", scheduleId, ex);
                    return null;
                });
            
        }, Duration.ofMillis(intervalMs));
        
        scheduledTasks.put(scheduleId, future);
        logger.info("Scheduled periodic data aggregation with ID: {}", scheduleId);
        
        return scheduleId;
    }
    
    @Override
    public boolean cancelPeriodicAggregation(String scheduleId) {
        ScheduledFuture<?> future = scheduledTasks.remove(scheduleId);
        if (future != null) {
            future.cancel(false);
            logger.info("Cancelled periodic data aggregation with ID: {}", scheduleId);
            return true;
        } else {
            logger.warn("Failed to cancel periodic data aggregation with ID: {} (not found)", scheduleId);
            return false;
        }
    }
    
    /**
     * Handle messages received through the communication service.
     */
    private DashboardMessage handleCommunicationMessage(DashboardMessage message) {
        // Check if this is a data request
        if (MessageType.DATA_REQUEST.equals(message.getMessageType())) {
            return handleDataRequest(message);
        }
        
        // Check if this is a data response
        if (MessageType.DATA_RESPONSE.equals(message.getMessageType())) {
            handleDataResponse(message);
        }
        
        // No response needed for other message types
        return null;
    }
    
    /**
     * Handle a data request message.
     */
    private DashboardMessage handleDataRequest(DashboardMessage requestMessage) {
        String dataType = requestMessage.getMetadata().get("dataType");
        String filterCriteria = requestMessage.getContent();
        
        // Special handling for available data types request
        if ("list-available-data-types".equals(filterCriteria)) {
            return createAvailableDataTypesResponse(requestMessage);
        }
        
        if (dataType == null) {
            logger.warn("Received data request without data type");
            return null;
        }
        
        // Collect data from all registered providers for this data type
        Map<String, Object> collectedData = new HashMap<>();
        for (DashboardDataProvider provider : dataProviders.values()) {
            try {
                Map<String, Object> providerData = provider.provideData(dataType, filterCriteria);
                if (providerData != null) {
                    collectedData.putAll(providerData);
                }
            } catch (Exception e) {
                logger.error("Error collecting data from provider for type: {}", dataType, e);
            }
        }
        
        // Create data transfer object
        DashboardDataTransfer dataTransfer = DashboardDataTransfer.builder()
                .id(UUID.randomUUID().toString())
                .sourceLevel(communicationService.getCurrentDashboardLevel())
                .sourceId(communicationService.getCurrentDashboardId())
                .targetLevel(requestMessage.getSourceLevel())
                .targetId(requestMessage.getSourceId())
                .dataType(dataType)
                .data(collectedData)
                .filtered(filterCriteria != null)
                .filterCriteria(filterCriteria)
                .build();
        
        // Create response message
        return createDataResponseMessage(requestMessage, dataTransfer);
    }
    
    /**
     * Create a response with available data types.
     */
    private DashboardMessage createAvailableDataTypesResponse(DashboardMessage requestMessage) {
        // Collect all unique data types from providers
        Set<String> availableTypes = new HashSet<>(Arrays.asList(
                DataType.DELIVERY_METRICS,
                DataType.DRIVER_PERFORMANCE,
                DataType.OPERATIONAL_METRICS,
                DataType.CUSTOMER_SATISFACTION
        ));
        
        // Create data map
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("availableDataTypes", new ArrayList<>(availableTypes));
        
        // Create data transfer
        DashboardDataTransfer dataTransfer = DashboardDataTransfer.builder()
                .id(UUID.randomUUID().toString())
                .sourceLevel(communicationService.getCurrentDashboardLevel())
                .sourceId(communicationService.getCurrentDashboardId())
                .targetLevel(requestMessage.getSourceLevel())
                .targetId(requestMessage.getSourceId())
                .dataType("available-data-types")
                .data(dataMap)
                .build();
        
        // Create response message
        return createDataResponseMessage(requestMessage, dataTransfer);
    }
    
    /**
     * Handle a data response message.
     */
    private void handleDataResponse(DashboardMessage responseMessage) {
        String requestId = responseMessage.getMetadata().get("requestId");
        if (requestId == null) {
            logger.warn("Received data response without request ID");
            return;
        }
        
        // Check if this is a response to a pending request
        CompletableFuture<DashboardDataTransfer> future = pendingRequests.get(requestId);
        if (future == null) {
            logger.warn("Received data response for unknown request ID: {}", requestId);
            return;
        }
        
        try {
            // Extract data transfer from response
            DashboardDataTransfer dataTransfer = extractDataTransferFromMessage(responseMessage);
            if (dataTransfer != null) {
                future.complete(dataTransfer);
            } else {
                future.completeExceptionally(new IllegalArgumentException("Invalid data in response"));
            }
        } catch (Exception e) {
            future.completeExceptionally(e);
        }
    }
    
    /**
     * Create a message from a data transfer object.
     */
    private DashboardMessage createDataTransferMessage(DashboardDataTransfer dataTransfer) {
        // Convert to JSON or another suitable format for the message content
        String content = "DATA_TRANSFER:" + dataTransfer.getId();
        
        DashboardMessage message = DashboardMessage.builder()
                .id(UUID.randomUUID().toString())
                .sourceLevel(dataTransfer.getSourceLevel())
                .sourceId(dataTransfer.getSourceId())
                .targetLevel(dataTransfer.getTargetLevel())
                .targetId(dataTransfer.getTargetId())
                .messageType(MessageType.DATA_RESPONSE)
                .subject("Data transfer: " + dataTransfer.getDataType())
                .content(content)
                .build();
        
        // Add data transfer details to metadata
        Map<String, String> metadata = new HashMap<>();
        metadata.put("dataType", dataTransfer.getDataType());
        metadata.put("transferId", dataTransfer.getId());
        message.setMetadata(metadata);
        
        return message;
    }
    
    /**
     * Create a data response message.
     */
    private DashboardMessage createDataResponseMessage(DashboardMessage requestMessage, DashboardDataTransfer dataTransfer) {
        DashboardMessage response = DashboardMessage.builder()
                .id(UUID.randomUUID().toString())
                .sourceLevel(communicationService.getCurrentDashboardLevel())
                .sourceId(communicationService.getCurrentDashboardId())
                .targetLevel(requestMessage.getSourceLevel())
                .targetId(requestMessage.getSourceId())
                .messageType(MessageType.DATA_RESPONSE)
                .subject("Data response: " + dataTransfer.getDataType())
                .content("DATA_RESPONSE")
                .build();
        
        // Add metadata
        Map<String, String> metadata = new HashMap<>();
        metadata.put("dataType", dataTransfer.getDataType());
        metadata.put("requestId", requestMessage.getId());
        metadata.put("transferId", dataTransfer.getId());
        response.setMetadata(metadata);
        
        return response;
    }
    
    /**
     * Extract a data transfer object from a message.
     */
    private DashboardDataTransfer extractDataTransferFromMessage(DashboardMessage message) {
        // In a real implementation, you would deserialize the data transfer from the message content
        // This is a simplified placeholder implementation
        
        String dataType = message.getMetadata().get("dataType");
        String transferId = message.getMetadata().get("transferId");
        
        if (dataType == null || transferId == null) {
            return null;
        }
        
        // Create a placeholder data transfer
        return DashboardDataTransfer.builder()
                .id(transferId)
                .sourceLevel(message.getSourceLevel())
                .sourceId(message.getSourceId())
                .targetLevel(message.getTargetLevel())
                .targetId(message.getTargetId())
                .dataType(dataType)
                .build();
    }
    
    /**
     * Determine the next higher level in the dashboard hierarchy.
     */
    private String getNextHigherLevel(String currentLevel) {
        if (DashboardLevel.LOCAL.equals(currentLevel) || DashboardLevel.BRANCH.equals(currentLevel)) {
            return DashboardLevel.REGIONAL;
        } else if (DashboardLevel.REGIONAL.equals(currentLevel)) {
            return DashboardLevel.GLOBAL;
        } else {
            return null; // No higher level
        }
    }
    
    /**
     * Create a copy of a message with a new target.
     */
    private DashboardMessage copyMessageWithNewTarget(DashboardMessage original, String targetLevel, String targetId) {
        DashboardMessage copy = DashboardMessage.builder()
                .id(UUID.randomUUID().toString())
                .sourceLevel(original.getSourceLevel())
                .sourceId(original.getSourceId())
                .targetLevel(targetLevel)
                .targetId(targetId)
                .messageType(original.getMessageType())
                .subject(original.getSubject())
                .content(original.getContent())
                .metadata(original.getMetadata())
                .requiresAcknowledgment(original.isRequiresAcknowledgment())
                .priority(original.getPriority())
                .build();
        return copy;
    }
    
    /**
     * Aggregate a value into the aggregated data map.
     */
    private void aggregateDataValue(Map<String, Object> aggregatedData, String key, Object value, String dataType) {
        if (aggregatedData.containsKey(key)) {
            // Key already exists, need to aggregate
            Object existingValue = aggregatedData.get(key);
            
            if (existingValue instanceof Number && value instanceof Number) {
                // Sum numeric values
                double sum = ((Number) existingValue).doubleValue() + ((Number) value).doubleValue();
                aggregatedData.put(key, sum);
            } else if (existingValue instanceof List && value instanceof List) {
                // Merge lists
                ((List) existingValue).addAll((List) value);
            } else if (existingValue instanceof Map && value instanceof Map) {
                // Recursively merge maps
                Map<String, Object> mergedMap = new HashMap<>((Map<String, Object>) existingValue);
                for (Map.Entry<String, Object> entry : ((Map<String, Object>) value).entrySet()) {
                    aggregateDataValue(mergedMap, entry.getKey(), entry.getValue(), dataType);
                }
                aggregatedData.put(key, mergedMap);
            } else {
                // For other types, prefer the newer value
                aggregatedData.put(key, value);
            }
        } else {
            // Key doesn't exist yet, just add it
            aggregatedData.put(key, value);
        }
    }
}
