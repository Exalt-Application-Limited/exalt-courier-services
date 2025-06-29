const LocationtrackerService = require('../src/services/LocationtrackerService');
const LocationtrackerModel = require('../src/models/Locationtracker');

// Mock the model
jest.mock('../src/models/Locationtracker');

describe('LocationtrackerService', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('getAll', () => {
    it('should return all Locationtrackers', async () => {
      const mockLocationtrackers = [
        { id: '1', name: 'Test Locationtracker 1' },
        { id: '2', name: 'Test Locationtracker 2' }
      ];
      
      LocationtrackerModel.find.mockResolvedValue(mockLocationtrackers);
      
      const result = await LocationtrackerService.getAll();
      
      expect(result).toHaveLength(2);
      expect(LocationtrackerModel.find).toHaveBeenCalled();
    });

    it('should handle errors', async () => {
      LocationtrackerModel.find.mockRejectedValue(new Error('Database error'));
      
      await expect(LocationtrackerService.getAll()).rejects.toThrow('Database error');
    });
  });

  describe('getById', () => {
    it('should return a single Locationtracker', async () => {
      const mockLocationtracker = { id: '1', name: 'Test Locationtracker' };
      
      LocationtrackerModel.findById.mockResolvedValue(mockLocationtracker);
      
      const result = await LocationtrackerService.getById('1');
      
      expect(result).toEqual(mockLocationtracker);
      expect(LocationtrackerModel.findById).toHaveBeenCalledWith('1');
    });

    it('should return null if Locationtracker not found', async () => {
      LocationtrackerModel.findById.mockResolvedValue(null);
      
      const result = await LocationtrackerService.getById('999');
      
      expect(result).toBeNull();
    });
  });

  describe('create', () => {
    it('should create a new Locationtracker', async () => {
      const newLocationtracker = { name: 'New Locationtracker' };
      const createdLocationtracker = { id: '3', ...newLocationtracker };
      
      LocationtrackerModel.create.mockResolvedValue(createdLocationtracker);
      
      const result = await LocationtrackerService.create(newLocationtracker);
      
      expect(result).toEqual(createdLocationtracker);
      expect(LocationtrackerModel.create).toHaveBeenCalledWith(newLocationtracker);
    });
  });

  describe('update', () => {
    it('should update an existing Locationtracker', async () => {
      const updateLocationtracker = { name: 'Updated Locationtracker' };
      const updatedLocationtracker = { id: '1', ...updateLocationtracker };
      
      LocationtrackerModel.findByIdAndUpdate.mockResolvedValue(updatedLocationtracker);
      
      const result = await LocationtrackerService.update('1', updateLocationtracker);
      
      expect(result).toEqual(updatedLocationtracker);
      expect(LocationtrackerModel.findByIdAndUpdate).toHaveBeenCalledWith(
        '1', 
        updateLocationtracker, 
        { new: true }
      );
    });
  });

  describe('delete', () => {
    it('should delete an Locationtracker', async () => {
      LocationtrackerModel.findByIdAndDelete.mockResolvedValue({ id: '1' });
      
      const result = await LocationtrackerService.delete('1');
      
      expect(result).toBe(true);
      expect(LocationtrackerModel.findByIdAndDelete).toHaveBeenCalledWith('1');
    });

    it('should return false if Locationtracker not found', async () => {
      LocationtrackerModel.findByIdAndDelete.mockResolvedValue(null);
      
      const result = await LocationtrackerService.delete('999');
      
      expect(result).toBe(false);
    });
  });
});