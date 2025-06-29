package com.exalt.courier.routing.service.algorithm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Factory service for routing algorithms.
 * Manages the available routing algorithms and provides the appropriate one based on configuration or request.
 */
@Service
public class RoutingAlgorithmFactory {
    
    private static final Logger logger = LoggerFactory.getLogger(RoutingAlgorithmFactory.class);
    private static final String DEFAULT_ALGORITHM = "Nearest Neighbor";
    
    @Value("${routing.algorithm.default:Nearest Neighbor}")
    private String defaultAlgorithmName;
    
    private final Map<String, RoutingAlgorithm> algorithms = new HashMap<>();
    
    @Autowired
    private List<RoutingAlgorithm> availableAlgorithms;
    
    @PostConstruct
    public void init() {
        if (availableAlgorithms.isEmpty()) {
            logger.warn("No routing algorithms found. Factory will not be able to provide any algorithm.");
            return;
        }
        
        for (RoutingAlgorithm algorithm : availableAlgorithms) {
            algorithms.put(algorithm.getAlgorithmName(), algorithm);
            logger.info("Registered routing algorithm: {}", algorithm.getAlgorithmName());
        }
        
        if (algorithms.containsKey(defaultAlgorithmName)) {
            logger.info("Default routing algorithm set to: {}", defaultAlgorithmName);
        } else {
            logger.warn("Configured default algorithm '{}' not found. Using '{}' as fallback.",
                    defaultAlgorithmName, DEFAULT_ALGORITHM);
            defaultAlgorithmName = DEFAULT_ALGORITHM;
        }
    }
    
    /**
     * Get the default routing algorithm.
     *
     * @return The default routing algorithm
     */
    public RoutingAlgorithm getDefaultAlgorithm() {
        RoutingAlgorithm algorithm = algorithms.get(defaultAlgorithmName);
        if (algorithm == null) {
            logger.error("Default algorithm '{}' not available", defaultAlgorithmName);
            throw new IllegalStateException("Default routing algorithm not available");
        }
        return algorithm;
    }
    
    /**
     * Get a specific routing algorithm by name.
     *
     * @param algorithmName The name of the algorithm
     * @return The requested algorithm, or the default if not found
     */
    public RoutingAlgorithm getAlgorithm(String algorithmName) {
        if (algorithmName == null || algorithmName.trim().isEmpty()) {
            return getDefaultAlgorithm();
        }
        
        RoutingAlgorithm algorithm = algorithms.get(algorithmName);
        if (algorithm == null) {
            logger.warn("Requested algorithm '{}' not found. Using default algorithm '{}'.",
                    algorithmName, defaultAlgorithmName);
            return getDefaultAlgorithm();
        }
        
        return algorithm;
    }
    
    /**
     * Get all available routing algorithms.
     *
     * @return Map of algorithm names to algorithm instances
     */
    public Map<String, RoutingAlgorithm> getAllAlgorithms() {
        return new HashMap<>(algorithms);
    }
} 