const NetworklocationsService = require('../src/services/NetworklocationsService');
const NetworklocationModel = require('../src/models/Networklocation');

// Mock the model
jest.mock('../src/models/Networklocation');

describe('NetworklocationsService', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('getAll', () => {
    it('should return all Networklocations', async () => {
      const mockNetworklocations = [
        { id: '1', name: 'Test Networklocation 1' },
        { id: '2', name: 'Test Networklocation 2' }
      ];
      
      NetworklocationModel.find.mockResolvedValue(mockNetworklocations);
      
      const result = await NetworklocationsService.getAll();
      
      expect(result).toHaveLength(2);
      expect(NetworklocationModel.find).toHaveBeenCalled();
    });

    it('should handle errors', async () => {
      NetworklocationModel.find.mockRejectedValue(new Error('Database error'));
      
      await expect(NetworklocationsService.getAll()).rejects.toThrow('Database error');
    });
  });

  describe('getById', () => {
    it('should return a single Networklocation', async () => {
      const mockNetworklocation = { id: '1', name: 'Test Networklocation' };
      
      NetworklocationModel.findById.mockResolvedValue(mockNetworklocation);
      
      const result = await NetworklocationsService.getById('1');
      
      expect(result).toEqual(mockNetworklocation);
      expect(NetworklocationModel.findById).toHaveBeenCalledWith('1');
    });

    it('should return null if Networklocation not found', async () => {
      NetworklocationModel.findById.mockResolvedValue(null);
      
      const result = await NetworklocationsService.getById('999');
      
      expect(result).toBeNull();
    });
  });

  describe('create', () => {
    it('should create a new Networklocation', async () => {
      const newNetworklocation = { name: 'New Networklocation' };
      const createdNetworklocation = { id: '3', ...newNetworklocation };
      
      NetworklocationModel.create.mockResolvedValue(createdNetworklocation);
      
      const result = await NetworklocationsService.create(newNetworklocation);
      
      expect(result).toEqual(createdNetworklocation);
      expect(NetworklocationModel.create).toHaveBeenCalledWith(newNetworklocation);
    });
  });

  describe('update', () => {
    it('should update an existing Networklocation', async () => {
      const updateNetworklocation = { name: 'Updated Networklocation' };
      const updatedNetworklocation = { id: '1', ...updateNetworklocation };
      
      NetworklocationModel.findByIdAndUpdate.mockResolvedValue(updatedNetworklocation);
      
      const result = await NetworklocationsService.update('1', updateNetworklocation);
      
      expect(result).toEqual(updatedNetworklocation);
      expect(NetworklocationModel.findByIdAndUpdate).toHaveBeenCalledWith(
        '1', 
        updateNetworklocation, 
        { new: true }
      );
    });
  });

  describe('delete', () => {
    it('should delete an Networklocation', async () => {
      NetworklocationModel.findByIdAndDelete.mockResolvedValue({ id: '1' });
      
      const result = await NetworklocationsService.delete('1');
      
      expect(result).toBe(true);
      expect(NetworklocationModel.findByIdAndDelete).toHaveBeenCalledWith('1');
    });

    it('should return false if Networklocation not found', async () => {
      NetworklocationModel.findByIdAndDelete.mockResolvedValue(null);
      
      const result = await NetworklocationsService.delete('999');
      
      expect(result).toBe(false);
    });
  });
});