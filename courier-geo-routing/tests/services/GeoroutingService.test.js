const GeoroutingService = require('../src/services/GeoroutingService');
const GeoroutingModel = require('../src/models/Georouting');

// Mock the model
jest.mock('../src/models/Georouting');

describe('GeoroutingService', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('getAll', () => {
    it('should return all Georoutings', async () => {
      const mockGeoroutings = [
        { id: '1', name: 'Test Georouting 1' },
        { id: '2', name: 'Test Georouting 2' }
      ];
      
      GeoroutingModel.find.mockResolvedValue(mockGeoroutings);
      
      const result = await GeoroutingService.getAll();
      
      expect(result).toHaveLength(2);
      expect(GeoroutingModel.find).toHaveBeenCalled();
    });

    it('should handle errors', async () => {
      GeoroutingModel.find.mockRejectedValue(new Error('Database error'));
      
      await expect(GeoroutingService.getAll()).rejects.toThrow('Database error');
    });
  });

  describe('getById', () => {
    it('should return a single Georouting', async () => {
      const mockGeorouting = { id: '1', name: 'Test Georouting' };
      
      GeoroutingModel.findById.mockResolvedValue(mockGeorouting);
      
      const result = await GeoroutingService.getById('1');
      
      expect(result).toEqual(mockGeorouting);
      expect(GeoroutingModel.findById).toHaveBeenCalledWith('1');
    });

    it('should return null if Georouting not found', async () => {
      GeoroutingModel.findById.mockResolvedValue(null);
      
      const result = await GeoroutingService.getById('999');
      
      expect(result).toBeNull();
    });
  });

  describe('create', () => {
    it('should create a new Georouting', async () => {
      const newGeorouting = { name: 'New Georouting' };
      const createdGeorouting = { id: '3', ...newGeorouting };
      
      GeoroutingModel.create.mockResolvedValue(createdGeorouting);
      
      const result = await GeoroutingService.create(newGeorouting);
      
      expect(result).toEqual(createdGeorouting);
      expect(GeoroutingModel.create).toHaveBeenCalledWith(newGeorouting);
    });
  });

  describe('update', () => {
    it('should update an existing Georouting', async () => {
      const updateGeorouting = { name: 'Updated Georouting' };
      const updatedGeorouting = { id: '1', ...updateGeorouting };
      
      GeoroutingModel.findByIdAndUpdate.mockResolvedValue(updatedGeorouting);
      
      const result = await GeoroutingService.update('1', updateGeorouting);
      
      expect(result).toEqual(updatedGeorouting);
      expect(GeoroutingModel.findByIdAndUpdate).toHaveBeenCalledWith(
        '1', 
        updateGeorouting, 
        { new: true }
      );
    });
  });

  describe('delete', () => {
    it('should delete an Georouting', async () => {
      GeoroutingModel.findByIdAndDelete.mockResolvedValue({ id: '1' });
      
      const result = await GeoroutingService.delete('1');
      
      expect(result).toBe(true);
      expect(GeoroutingModel.findByIdAndDelete).toHaveBeenCalledWith('1');
    });

    it('should return false if Georouting not found', async () => {
      GeoroutingModel.findByIdAndDelete.mockResolvedValue(null);
      
      const result = await GeoroutingService.delete('999');
      
      expect(result).toBe(false);
    });
  });
});