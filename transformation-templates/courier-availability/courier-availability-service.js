/**
 * Courier Availability Service
 * 
 * Implementation example showing how to use feature flags for safe transformation
 * Part of the Zero Regression Strategy for Courier Services Domain
 */

const express = require('express');
const bodyParser = require('body-parser');
const { MongoClient } = require('mongodb');
const featureFlagClient = require('../../feature-flags/nodejs-client/featureFlagClient');
const logger = require('./logger');

// Constants
const PORT = process.env.PORT || 3000;
const MONGO_URI = process.env.MONGO_URI || 'mongodb://localhost:27017/courier-services';
const FEATURE_FLAG_NAME = 'use_new_courier_availability_service';

// Initialize the app
const app = express();
app.use(bodyParser.json());

// Database connection
let db;
MongoClient.connect(MONGO_URI)
  .then(client => {
    db = client.db();
    logger.info('Connected to MongoDB');
  })
  .catch(err => {
    logger.error('Failed to connect to MongoDB:', err);
    process.exit(1);
  });

/**
 * Old implementation of courier availability service
 */
class OldCourierAvailabilityService {
  async findAvailableCouriers(location, packageSize) {
    logger.info('Using OLD implementation to find available couriers', { location, packageSize });
    
    try {
      // Query couriers from database
      const couriers = await db.collection('couriers').find({
        'availableAreas': { $near: { $geometry: location } },
        'active': true,
        'availableForDelivery': true,
        'supportedPackageSizes': packageSize
      }).limit(10).toArray();
      
      return couriers.map(courier => ({
        id: courier._id,
        name: courier.name,
        rating: courier.rating,
        estimatedArrivalMinutes: this._calculateArrivalTime(courier, location),
        contactInfo: courier.contactInfo
      }));
    } catch (error) {
      logger.error('Error finding available couriers in old implementation', error);
      return [];
    }
  }
  
  _calculateArrivalTime(courier, location) {
    // Simple calculation based on distance
    const distance = this._calculateDistance(courier.currentLocation, location);
    return Math.round(distance * 2); // 2 minutes per kilometer
  }
  
  _calculateDistance(point1, point2) {
    // Simple Euclidean distance for demonstration
    const latDiff = point1.coordinates[1] - point2.coordinates[1];
    const lngDiff = point1.coordinates[0] - point2.coordinates[0];
    return Math.sqrt(latDiff * latDiff + lngDiff * lngDiff) * 111; // Convert to km
  }
}

/**
 * New implementation of courier availability service
 * 
 * Improvements:
 * - Uses courier speed and traffic data for more accurate ETA
 * - Considers courier ratings and customer preferences
 * - Implements caching for frequently requested areas
 * - Adds support for priority deliveries
 */
class NewCourierAvailabilityService {
  constructor() {
    this.cache = new Map(); // Simple in-memory cache
    this.cacheExpiryMs = 60000; // 1 minute cache expiry
  }
  
  async findAvailableCouriers(location, packageSize, options = {}) {
    logger.info('Using NEW implementation to find available couriers', { location, packageSize, options });
    
    try {
      // Try cache first for non-priority requests
      if (!options.isPriority) {
        const cacheKey = this._getCacheKey(location, packageSize);
        const cached = this._getFromCache(cacheKey);
        if (cached) {
          logger.debug('Cache hit for courier availability request');
          return cached;
        }
      }
      
      // Enhanced query with better geospatial indexing
      const query = {
        'availableAreas': { 
          $geoWithin: { 
            $centerSphere: [
              [location.coordinates[0], location.coordinates[1]], 
              5 / 6378.1 // 5km radius
            ] 
          } 
        },
        'active': true,
        'availableForDelivery': true,
        'supportedPackageSizes': packageSize
      };
      
      // Add filters for priority deliveries
      if (options.isPriority) {
        query.priorityEnabled = true;
        query.rating = { $gte: 4.5 };
      }
      
      // Query with advanced sorting
      const couriers = await db.collection('couriers')
        .find(query)
        .sort({
          'rating': -1,
          'deliveriesCompleted': -1
        })
        .limit(options.isPriority ? 5 : 10)
        .toArray();
      
      // Get traffic data for better ETA calculation
      const trafficData = await this._getTrafficData(location);
      
      // Transform results with enhanced data
      const result = couriers.map(courier => ({
        id: courier._id,
        name: courier.name,
        rating: courier.rating,
        estimatedArrivalMinutes: this._calculateArrivalTimeWithTraffic(
          courier, 
          location,
          trafficData
        ),
        contactInfo: courier.contactInfo,
        deliveryCapabilities: courier.deliveryCapabilities || [],
        pictureUrl: courier.pictureUrl,
        totalDeliveries: courier.deliveriesCompleted
      }));
      
      // Cache results for non-priority requests
      if (!options.isPriority) {
        const cacheKey = this._getCacheKey(location, packageSize);
        this._addToCache(cacheKey, result);
      }
      
      return result;
    } catch (error) {
      logger.error('Error finding available couriers in new implementation', error);
      throw error; // Let the feature flag system handle the fallback
    }
  }
  
  async _getTrafficData(location) {
    try {
      // In a real implementation, this would call a traffic service
      return {
        congestionLevel: Math.random() > 0.7 ? 'high' : 'normal',
        averageSpeedKmh: Math.random() > 0.7 ? 15 : 30
      };
    } catch (error) {
      logger.warn('Failed to get traffic data, using defaults', error);
      return { congestionLevel: 'normal', averageSpeedKmh: 25 };
    }
  }
  
  _calculateArrivalTimeWithTraffic(courier, location, trafficData) {
    const distance = this._calculateDistance(courier.currentLocation, location);
    const baseTime = distance / (trafficData.averageSpeedKmh / 60);
    
    // Apply traffic multiplier
    const trafficMultiplier = trafficData.congestionLevel === 'high' ? 1.5 : 1.0;
    
    // Add pickup time
    const pickupTime = 3; // 3 minutes for pickup
    
    return Math.round((baseTime * trafficMultiplier) + pickupTime);
  }
  
  _calculateDistance(point1, point2) {
    // Haversine formula for more accurate distance calculation
    const R = 6371; // Earth's radius in km
    const lat1 = point1.coordinates[1] * Math.PI / 180;
    const lat2 = point2.coordinates[1] * Math.PI / 180;
    const latDiff = (point2.coordinates[1] - point1.coordinates[1]) * Math.PI / 180;
    const lngDiff = (point2.coordinates[0] - point1.coordinates[0]) * Math.PI / 180;
    
    const a = Math.sin(latDiff/2) * Math.sin(latDiff/2) +
              Math.cos(lat1) * Math.cos(lat2) *
              Math.sin(lngDiff/2) * Math.sin(lngDiff/2);
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    
    return R * c;
  }
  
  _getCacheKey(location, packageSize) {
    // Round coordinates to reduce cache variations
    const lat = Math.round(location.coordinates[1] * 100) / 100;
    const lng = Math.round(location.coordinates[0] * 100) / 100;
    return `${lat}:${lng}:${packageSize}`;
  }
  
  _getFromCache(key) {
    if (!this.cache.has(key)) {
      return null;
    }
    
    const cached = this.cache.get(key);
    if (Date.now() - cached.timestamp > this.cacheExpiryMs) {
      // Expired
      this.cache.delete(key);
      return null;
    }
    
    return cached.data;
  }
  
  _addToCache(key, data) {
    this.cache.set(key, {
      timestamp: Date.now(),
      data: data
    });
  }
}

// Create service instances
const oldService = new OldCourierAvailabilityService();
const newService = new NewCourierAvailabilityService();

// Route to find available couriers
app.post('/api/couriers/availability', async (req, res) => {
  try {
    const { location, packageSize, userId, options } = req.body;
    
    if (!location || !packageSize) {
      return res.status(400).json({ 
        error: 'Missing required parameters: location and packageSize are required' 
      });
    }
    
    // Feature flag check with user-specific targeting if userId is provided
    const useNewImplementation = userId 
      ? featureFlagClient.isEnabledForUser(FEATURE_FLAG_NAME, userId)
      : featureFlagClient.isEnabled(FEATURE_FLAG_NAME);
    
    // Log which implementation is being used
    logger.info(`Using ${useNewImplementation ? 'new' : 'old'} courier availability implementation`, { userId });
    
    // Execute with feature flag safety
    const availableCouriers = await featureFlagClient.executeWithFeatureFlag(
      FEATURE_FLAG_NAME,
      () => newService.findAvailableCouriers(location, packageSize, options),
      () => oldService.findAvailableCouriers(location, packageSize)
    );
    
    return res.json({ 
      couriers: availableCouriers,
      count: availableCouriers.length,
      implementation: useNewImplementation ? 'new' : 'old'
    });
  } catch (error) {
    logger.error('Error processing courier availability request', error);
    return res.status(500).json({ error: 'Failed to find available couriers' });
  }
});

// Health check endpoint
app.get('/health', (req, res) => {
  res.json({
    status: 'healthy',
    timestamp: new Date().toISOString(),
    version: process.env.SERVICE_VERSION || '1.0.0'
  });
});

// Metrics endpoint
app.get('/metrics', (req, res) => {
  res.json({
    timestamp: new Date().toISOString(),
    errorRate: 0.01, // Example metrics
    latencyP95: 120,
    rpm: 250,
    successRate: 0.995,
    cpuUsage: 0.35,
    memoryUsage: 0.4
  });
});

// Start the server
app.listen(PORT, () => {
  logger.info(`Courier Availability Service listening on port ${PORT}`);
});
