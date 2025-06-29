/**
 * Test data factory for courierfarecalculator domain objects.
 * Use this module in tests to generate consistent test data.
 */

/**
 * Creates a test Fare with random or specified data
 * @param {Object} overrides - Properties to override default values
 * @returns {Object} A test Fare object
 */
function createFare(overrides = {}) {
  return {
        id: '1',
    currency: 'USD',
    surgeMultiplier: 1.0,
    baseFare: 3.0,
    perMileFare: 1.25,
    distance: 5.2,
    totalFare: 9.5,
    ...overrides
  };
}

/**
 * Creates multiple test Fares
 * @param {number} count - Number of objects to create
 * @param {Object} overrides - Properties to override default values
 * @returns {Array} Array of test Fare objects
 */
function createMultipleFares(count = 5, overrides = {}) {
  return Array(count).fill().map((_, index) => {
    return createFare({
      id: String(index + 1),
      ...overrides
    });
  });
}



module.exports = {
  createFare,
  createMultipleFares,
  
};
