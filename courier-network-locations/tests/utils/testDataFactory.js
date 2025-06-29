/**
 * Test data factory for couriernetworklocations domain objects.
 * Use this module in tests to generate consistent test data.
 */

/**
 * Creates a test Location with random or specified data
 * @param {Object} overrides - Properties to override default values
 * @returns {Object} A test Location object
 */
function createLocation(overrides = {}) {
  return {
        id: '1',
    name: 'Test Hub Location',
    latitude: 40.7128,
    locationType: 'HUB',
    address: '123 Test Street, City, State, 12345',
    isActive: true,
    longitude: -74.0060,
    ...overrides
  };
}

/**
 * Creates multiple test Locations
 * @param {number} count - Number of objects to create
 * @param {Object} overrides - Properties to override default values
 * @returns {Array} Array of test Location objects
 */
function createMultipleLocations(count = 5, overrides = {}) {
  return Array(count).fill().map((_, index) => {
    return createLocation({
      id: String(index + 1),
      ...overrides
    });
  });
}



module.exports = {
  createLocation,
  createMultipleLocations,
  
};
