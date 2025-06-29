/**
 * Test data factory for couriergeorouting domain objects.
 * Use this module in tests to generate consistent test data.
 */

/**
 * Creates a test Route with random or specified data
 * @param {Object} overrides - Properties to override default values
 * @returns {Object} A test Route object
 */
function createRoute(overrides = {}) {
  return {
        id: '1',
    destination: { lat: 40.7589, lng: -73.9851 },
    waypoints: [{ lat: 40.7308, lng: -73.9973 }],
    duration: 15,
    createdAt: new Date(),
    distance: 4.5,
    origin: { lat: 40.7128, lng: -74.0060 },
    ...overrides
  };
}

/**
 * Creates multiple test Routes
 * @param {number} count - Number of objects to create
 * @param {Object} overrides - Properties to override default values
 * @returns {Array} Array of test Route objects
 */
function createMultipleRoutes(count = 5, overrides = {}) {
  return Array(count).fill().map((_, index) => {
    return createRoute({
      id: String(index + 1),
      ...overrides
    });
  });
}



module.exports = {
  createRoute,
  createMultipleRoutes,
  
};
