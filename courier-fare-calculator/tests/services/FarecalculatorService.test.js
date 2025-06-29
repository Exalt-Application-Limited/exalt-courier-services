const FarecalculatorService = require('../src/services/FarecalculatorService');
const FarecalculatorModel = require('../src/models/Farecalculator');

// Mock the model
jest.mock('../src/models/Farecalculator');

describe('FarecalculatorService', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('getAll', () => {
    it('should return all Farecalculators', async () => {
      const mockFarecalculators = [
        { id: '1', name: 'Test Farecalculator 1' },
        { id: '2', name: 'Test Farecalculator 2' }
      ];
      
      FarecalculatorModel.find.mockResolvedValue(mockFarecalculators);
      
      const result = await FarecalculatorService.getAll();
      
      expect(result).toHaveLength(2);
      expect(FarecalculatorModel.find).toHaveBeenCalled();
    });

    it('should handle errors', async () => {
      FarecalculatorModel.find.mockRejectedValue(new Error('Database error'));
      
      await expect(FarecalculatorService.getAll()).rejects.toThrow('Database error');
    });
  });

  describe('getById', () => {
    it('should return a single Farecalculator', async () => {
      const mockFarecalculator = { id: '1', name: 'Test Farecalculator' };
      
      FarecalculatorModel.findById.mockResolvedValue(mockFarecalculator);
      
      const result = await FarecalculatorService.getById('1');
      
      expect(result).toEqual(mockFarecalculator);
      expect(FarecalculatorModel.findById).toHaveBeenCalledWith('1');
    });

    it('should return null if Farecalculator not found', async () => {
      FarecalculatorModel.findById.mockResolvedValue(null);
      
      const result = await FarecalculatorService.getById('999');
      
      expect(result).toBeNull();
    });
  });

  describe('create', () => {
    it('should create a new Farecalculator', async () => {
      const newFarecalculator = { name: 'New Farecalculator' };
      const createdFarecalculator = { id: '3', ...newFarecalculator };
      
      FarecalculatorModel.create.mockResolvedValue(createdFarecalculator);
      
      const result = await FarecalculatorService.create(newFarecalculator);
      
      expect(result).toEqual(createdFarecalculator);
      expect(FarecalculatorModel.create).toHaveBeenCalledWith(newFarecalculator);
    });
  });

  describe('update', () => {
    it('should update an existing Farecalculator', async () => {
      const updateFarecalculator = { name: 'Updated Farecalculator' };
      const updatedFarecalculator = { id: '1', ...updateFarecalculator };
      
      FarecalculatorModel.findByIdAndUpdate.mockResolvedValue(updatedFarecalculator);
      
      const result = await FarecalculatorService.update('1', updateFarecalculator);
      
      expect(result).toEqual(updatedFarecalculator);
      expect(FarecalculatorModel.findByIdAndUpdate).toHaveBeenCalledWith(
        '1', 
        updateFarecalculator, 
        { new: true }
      );
    });
  });

  describe('delete', () => {
    it('should delete an Farecalculator', async () => {
      FarecalculatorModel.findByIdAndDelete.mockResolvedValue({ id: '1' });
      
      const result = await FarecalculatorService.delete('1');
      
      expect(result).toBe(true);
      expect(FarecalculatorModel.findByIdAndDelete).toHaveBeenCalledWith('1');
    });

    it('should return false if Farecalculator not found', async () => {
      FarecalculatorModel.findByIdAndDelete.mockResolvedValue(null);
      
      const result = await FarecalculatorService.delete('999');
      
      expect(result).toBe(false);
    });
  });
});