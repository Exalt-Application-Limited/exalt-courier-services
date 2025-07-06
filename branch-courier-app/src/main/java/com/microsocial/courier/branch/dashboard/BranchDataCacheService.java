package com.gogidix.courier.courier.branch.dashboard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.microsocial.courier.branch.dashboard.model.BranchMetricsData;
import com.microsocial.courier.branch.dashboard.model.DashboardMessage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Service for caching dashboard messages and metrics when offline.
 * This allows the branch application to function in disconnected environments
 * and sync data when connectivity is restored.
 */
@Service
public class BranchDataCacheService {

    private static final Logger logger = LoggerFactory.getLogger(BranchDataCacheService.class);
    
    // Cache for messages that haven't been delivered yet
    private final Map<String, CachedItem<DashboardMessage>> messageCache = new ConcurrentHashMap<>();
    
    // Cache for metrics that haven't been delivered yet
    private final Map<String, CachedItem<BranchMetricsData>> metricsCache = new ConcurrentHashMap<>();
    
    // Maximum age for cached items in hours before they're considered stale
    private static final int MAX_CACHE_AGE_HOURS = 24;
    
    /**
     * Caches an outgoing message that may fail to deliver due to connectivity issues.
     *
     * @param message The message to cache
     */
    public void cacheOutgoingMessage(DashboardMessage message) {
        logger.debug("Caching outgoing message with ID: {}", message.getMessageId());
        messageCache.put(message.getMessageId(), new CachedItem<>(message));
        
        // Clean up old cache entries
        cleanupCache();
    }
    
    /**
     * Caches outgoing metrics that may fail to deliver due to connectivity issues.
     *
     * @param metrics The metrics data to cache
     */
    public void cacheOutgoingMetrics(BranchMetricsData metrics) {
        logger.debug("Caching outgoing metrics with ID: {}", metrics.getMetricsId());
        metricsCache.put(metrics.getMetricsId(), new CachedItem<>(metrics));
        
        // Clean up old cache entries
        cleanupCache();
    }
    
    /**
     * Marks a message as successfully delivered, removing it from the cache.
     *
     * @param messageId The ID of the message to mark as delivered
     */
    public void markMessageDelivered(String messageId) {
        logger.debug("Marking message as delivered: {}", messageId);
        messageCache.remove(messageId);
    }
    
    /**
     * Marks metrics as successfully delivered, removing them from the cache.
     *
     * @param metricsId The ID of the metrics to mark as delivered
     */
    public void markMetricsDelivered(String metricsId) {
        logger.debug("Marking metrics as delivered: {}", metricsId);
        metricsCache.remove(metricsId);
    }
    
    /**
     * Gets all undelivered messages from the cache.
     *
     * @return List of undelivered messages
     */
    public List<DashboardMessage> getUndeliveredMessages() {
        logger.info("Retrieving {} undelivered messages from cache", messageCache.size());
        return messageCache.values().stream()
                .map(CachedItem::getItem)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets all undelivered metrics from the cache.
     *
     * @return List of undelivered metrics
     */
    public List<BranchMetricsData> getUndeliveredMetrics() {
        logger.info("Retrieving {} undelivered metrics from cache", metricsCache.size());
        return metricsCache.values().stream()
                .map(CachedItem::getItem)
                .collect(Collectors.toList());
    }
    
    /**
     * Cleans up old cache entries to prevent memory issues.
     */
    private void cleanupCache() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(MAX_CACHE_AGE_HOURS);
        
        // Clean up old messages
        List<String> oldMessageIds = messageCache.entrySet().stream()
                .filter(entry -> entry.getValue().getTimestamp().isBefore(cutoffTime))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        
        if (!oldMessageIds.isEmpty()) {
            logger.info("Cleaning up {} stale message cache entries", oldMessageIds.size());
            oldMessageIds.forEach(messageCache::remove);
        }
        
        // Clean up old metrics
        List<String> oldMetricsIds = metricsCache.entrySet().stream()
                .filter(entry -> entry.getValue().getTimestamp().isBefore(cutoffTime))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        
        if (!oldMetricsIds.isEmpty()) {
            logger.info("Cleaning up {} stale metrics cache entries", oldMetricsIds.size());
            oldMetricsIds.forEach(metricsCache::remove);
        }
    }
    
    /**
     * Inner class to store cached items with timestamps.
     */
    private static class CachedItem<T> {
        private final T item;
        private final LocalDateTime timestamp;
        
        public CachedItem(T item) {
            this.item = item;
            this.timestamp = LocalDateTime.now();
        }
        
        public T getItem() {
            return item;
        }
        
        public LocalDateTime getTimestamp() {
            return timestamp;
        }
    }
} 