/**
 * Routing service for the driver mobile app.
 * This service handles communication with the routing backend API
 * and implements circuit breaker pattern for resilience.
 */

const axios = require('axios');
const config = require('../config');
const logger = require('../utils/logger');
const { executeWithCircuitBreaker } = require('../utils/circuitBreaker');

class RoutingService {
  constructor() {
    this.baseUrl = config.api.routing.baseUrl;
    this.client = axios.create({
      baseURL: this.baseUrl,
      timeout: 10000,
      headers: {
        'Content-Type': 'application/json'
      }
    });
  }

  /**
   * Get an optimal route between two points
   * 
   * @param {number} startLat Start latitude
   * @param {number} startLng Start longitude
   * @param {number} endLat End latitude
   * @param {number} endLng End longitude
   * @returns {Promise<Array>} Array of route waypoints
   */
  async getOptimalRoute(startLat, startLng, endLat, endLng) {
    logger.info(`Getting optimal route from (${startLat},${startLng}) to (${endLat},${endLng})`);
    
    // Use circuit breaker to protect against API failures
    return executeWithCircuitBreaker(
      'routing.getOptimalRoute',
      async () => {
        const response = await this.client.get('/api/routing/external/optimal-route', {
          params: {
            startLat,
            startLng,
            endLat,
            endLng
          }
        });
        
        return response.data;
      },
      [] // Fallback value (empty route)
    );
  }

  /**
   * Get estimated travel time between two points
   * 
   * @param {number} startLat Start latitude
   * @param {number} startLng Start longitude
   * @param {number} endLat End latitude
   * @param {number} endLng End longitude
   * @returns {Promise<Object>} Object containing time estimates
   */
  async getEstimatedTravelTime(startLat, startLng, endLat, endLng) {
    logger.info(`Getting ETA from (${startLat},${startLng}) to (${endLat},${endLng})`);
    
    // Use circuit breaker pattern with fallback
    return executeWithCircuitBreaker(
      'routing.getETA',
      async () => {
        const response = await this.client.get('/api/routing/external/eta', {
          params: {
            startLat,
            startLng,
            endLat,
            endLng
          }
        });
        
        return response.data;
      },
      { 
        estimatedTimeSeconds: 0,
        estimatedTimeMinutes: 0,
        isEstimate: true 
      }
    );
  }

  /**
   * Get traffic conditions for a route
   * 
   * @param {string} routeId ID of the route
   * @returns {Promise<Object>} Traffic conditions data
   */
  async getTrafficConditions(routeId) {
    logger.info(`Getting traffic conditions for route ${routeId}`);
    
    // Use circuit breaker pattern with a more restrictive timeout
    return executeWithCircuitBreaker(
      'routing.getTrafficConditions',
      async () => {
        const response = await this.client.get('/api/routing/external/traffic', {
          params: { routeId },
          timeout: 5000 // More aggressive timeout for traffic data
        });
        
        return response.data;
      },
      { 
        congestionLevel: 'unknown',
        trafficFlow: 'normal',
        isEstimate: true
      }
    );
  }

  /**
   * Update the current location of the courier
   * 
   * @param {string} courierId The courier ID
   * @param {number} latitude Current latitude
   * @param {number} longitude Current longitude
   * @returns {Promise<Object>} Update confirmation
   */
  async updateLocation(courierId, latitude, longitude) {
    logger.info(`Updating courier ${courierId} location to (${latitude},${longitude})`);
    
    return executeWithCircuitBreaker(
      'routing.updateLocation',
      async () => {
        const response = await this.client.post('/api/routing/couriers/location', {
          courierId,
          latitude,
          longitude,
          timestamp: new Date().toISOString()
        });
        
        return response.data;
      },
      { updated: false, offline: true }
    );
  }
}

module.exports = new RoutingService(); 