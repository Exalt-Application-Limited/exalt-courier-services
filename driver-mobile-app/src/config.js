/**
 * Configuration for the driver mobile app.
 * Loads environment variables with sensible defaults.
 */

// Load environment variables from .env file if present
require('dotenv').config();

const config = {
  // Server configuration
  server: {
    port: process.env.PORT || 3000,
    host: process.env.HOST || '0.0.0.0'
  },
  
  // API endpoints
  api: {
    // Routing service configuration
    routing: {
      baseUrl: process.env.ROUTING_API_URL || 'http://routing-service:8080',
      timeout: parseInt(process.env.ROUTING_API_TIMEOUT || '10000', 10)
    },
    
    // Tracking service configuration
    tracking: {
      baseUrl: process.env.TRACKING_API_URL || 'http://tracking-service:8081',
      timeout: parseInt(process.env.TRACKING_API_TIMEOUT || '5000', 10)
    },
    
    // Courier management service configuration
    courier: {
      baseUrl: process.env.COURIER_API_URL || 'http://courier-management:8082',
      timeout: parseInt(process.env.COURIER_API_TIMEOUT || '10000', 10)
    },
    
    // Payout service configuration
    payout: {
      baseUrl: process.env.PAYOUT_API_URL || 'http://payout-service:8083',
      timeout: parseInt(process.env.PAYOUT_API_TIMEOUT || '15000', 10)
    }
  },
  
  // Authentication configuration
  auth: {
    jwtSecret: process.env.JWT_SECRET || 'your-secret-key',
    tokenExpiration: process.env.TOKEN_EXPIRATION || '24h'
  },
  
  // Feature flags
  features: {
    enableOfflineMode: process.env.ENABLE_OFFLINE_MODE === 'true',
    enablePushNotifications: process.env.ENABLE_PUSH_NOTIFICATIONS !== 'false',
    enableLocationTracking: process.env.ENABLE_LOCATION_TRACKING !== 'false',
    enableDebugMode: process.env.ENABLE_DEBUG_MODE === 'true'
  },
  
  // Logging configuration
  logging: {
    level: process.env.LOG_LEVEL || 'info',
    filePath: process.env.LOG_FILE_PATH || './logs',
    maxSize: process.env.LOG_MAX_SIZE || '10m',
    maxFiles: parseInt(process.env.LOG_MAX_FILES || '5', 10)
  },
  
  // Circuit breaker configuration
  circuitBreaker: {
    // Default settings for all services
    default: {
      failureThreshold: parseInt(process.env.CB_DEFAULT_FAILURE_THRESHOLD || '5', 10),
      failureRateThreshold: parseInt(process.env.CB_DEFAULT_FAILURE_RATE || '50', 10),
      resetTimeout: parseInt(process.env.CB_DEFAULT_RESET_TIMEOUT || '30000', 10),
      halfOpenSuccessThreshold: parseInt(process.env.CB_DEFAULT_HALF_OPEN_SUCCESS || '2', 10)
    },
    
    // Custom settings for routing service
    routing: {
      failureThreshold: parseInt(process.env.CB_ROUTING_FAILURE_THRESHOLD || '3', 10),
      failureRateThreshold: parseInt(process.env.CB_ROUTING_FAILURE_RATE || '30', 10),
      resetTimeout: parseInt(process.env.CB_ROUTING_RESET_TIMEOUT || '10000', 10)
    }
  }
};

module.exports = config; 