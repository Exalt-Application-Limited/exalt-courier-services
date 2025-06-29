const { createLocation, createMultipleLocations } = require('../utils/testDataFactory');

describe('Test Data Factory Usage Sample', () => {
  it('should create a single test entity', () => {
    const entity = createLocation();
    expect(entity).toBeDefined();
    expect(entity.id).toBe('1');
  });

  it('should create multiple test entities', () => {
    const entities = createMultipleLocations(3);
    expect(entities).toHaveLength(3);
    expect(entities[0].id).toBe('1');
    expect(entities[1].id).toBe('2');
    expect(entities[2].id).toBe('3');
  });

  it('should override default values', () => {
    const entity = createLocation({ name: 'Custom Name' });
    expect(entity.name).toBe('Custom Name');
  });
});
