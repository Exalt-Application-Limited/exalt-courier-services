/**
 * Circuit Breaker implementation for the driver mobile app.
 * This utility helps prevent cascading failures when external services are down,
 * by failing fast and providing fallback mechanisms.
 */

const { CircuitBreaker } = require('@microecosystem/circuit-breaker');
const logger = require('./logger');

// Define the default circuit breaker configurations
const defaultConfig = {
  failureThreshold: 5,
  failureRateThreshold: 50,
  resetTimeout: 30000, // 30 seconds
  halfOpenSuccessThreshold: 2
};

// Custom configurations for different service types
const serviceConfigs = {
  tracking: {
    failureThreshold: 8,
    failureRateThreshold: 40,
    resetTimeout: 15000, // 15 seconds
    halfOpenSuccessThreshold: 3
  },
  routing: {
    failureThreshold: 3,
    failureRateThreshold: 30,
    resetTimeout: 10000, // 10 seconds
    halfOpenSuccessThreshold: 2
  },
  external: {
    failureThreshold: 2,
    failureRateThreshold: 50,
    resetTimeout: 60000, // 1 minute
    halfOpenSuccessThreshold: 1
  }
};

// Store circuit breaker instances by service name
const breakers = new Map();

/**
 * Get or create a circuit breaker for a specific service
 * 
 * @param {string} serviceName The name of the service to create/get circuit breaker for
 * @param {object} customConfig Optional custom configuration
 * @returns {CircuitBreaker} The circuit breaker instance
 */
function getBreaker(serviceName, customConfig = {}) {
  if (breakers.has(serviceName)) {
    return breakers.get(serviceName);
  }

  // Determine which config to use (service-specific, custom, or default)
  const serviceType = serviceName.split('.')[0]; // e.g., "routing.getOptimalPath" -> "routing"
  const config = {
    ...defaultConfig,
    ...(serviceConfigs[serviceType] || {}),
    ...customConfig
  };

  // Create the circuit breaker
  const breaker = new CircuitBreaker(config);
  
  // Add event listeners for logging and monitoring
  breaker.on('open', () => {
    logger.warn(`Circuit breaker for ${serviceName} is now OPEN`);
  });
  
  breaker.on('close', () => {
    logger.info(`Circuit breaker for ${serviceName} is now CLOSED`);
  });
  
  breaker.on('halfOpen', () => {
    logger.info(`Circuit breaker for ${serviceName} is now HALF-OPEN`);
  });
  
  breaker.on('fallback', (err) => {
    logger.info(`Using fallback for ${serviceName}: ${err.message}`);
  });

  // Store the breaker instance
  breakers.set(serviceName, breaker);
  return breaker;
}

/**
 * Execute a function with circuit breaker protection
 * 
 * @param {string} serviceName The name of the service being called
 * @param {Function} fn The function to execute
 * @param {any} fallbackValue Value to return if circuit is open
 * @returns {Promise<any>} Result of the function or fallback value
 */
async function executeWithCircuitBreaker(serviceName, fn, fallbackValue = null) {
  const breaker = getBreaker(serviceName);
  
  try {
    return await breaker.fire(fn);
  } catch (error) {
    if (error.name === 'CircuitBreakerOpenError') {
      logger.warn(`Circuit is open for ${serviceName}, using fallback`);
      return fallbackValue;
    }
    
    // For non-circuit breaker errors, log and rethrow
    logger.error(`Error executing ${serviceName}: ${error.message}`);
    throw error;
  }
}

/**
 * Reset all circuit breakers to closed state
 */
function resetAllBreakers() {
  for (const [serviceName, breaker] of breakers.entries()) {
    logger.info(`Resetting circuit breaker for ${serviceName}`);
    breaker.reset();
  }
}

module.exports = {
  executeWithCircuitBreaker,
  getBreaker,
  resetAllBreakers
}; 