const EventsService = require('../src/services/EventsService');
const EventModel = require('../src/models/Event');

// Mock the model
jest.mock('../src/models/Event');

describe('EventsService', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('getAll', () => {
    it('should return all Events', async () => {
      const mockEvents = [
        { id: '1', name: 'Test Event 1' },
        { id: '2', name: 'Test Event 2' }
      ];
      
      EventModel.find.mockResolvedValue(mockEvents);
      
      const result = await EventsService.getAll();
      
      expect(result).toHaveLength(2);
      expect(EventModel.find).toHaveBeenCalled();
    });

    it('should handle errors', async () => {
      EventModel.find.mockRejectedValue(new Error('Database error'));
      
      await expect(EventsService.getAll()).rejects.toThrow('Database error');
    });
  });

  describe('getById', () => {
    it('should return a single Event', async () => {
      const mockEvent = { id: '1', name: 'Test Event' };
      
      EventModel.findById.mockResolvedValue(mockEvent);
      
      const result = await EventsService.getById('1');
      
      expect(result).toEqual(mockEvent);
      expect(EventModel.findById).toHaveBeenCalledWith('1');
    });

    it('should return null if Event not found', async () => {
      EventModel.findById.mockResolvedValue(null);
      
      const result = await EventsService.getById('999');
      
      expect(result).toBeNull();
    });
  });

  describe('create', () => {
    it('should create a new Event', async () => {
      const newEvent = { name: 'New Event' };
      const createdEvent = { id: '3', ...newEvent };
      
      EventModel.create.mockResolvedValue(createdEvent);
      
      const result = await EventsService.create(newEvent);
      
      expect(result).toEqual(createdEvent);
      expect(EventModel.create).toHaveBeenCalledWith(newEvent);
    });
  });

  describe('update', () => {
    it('should update an existing Event', async () => {
      const updateEvent = { name: 'Updated Event' };
      const updatedEvent = { id: '1', ...updateEvent };
      
      EventModel.findByIdAndUpdate.mockResolvedValue(updatedEvent);
      
      const result = await EventsService.update('1', updateEvent);
      
      expect(result).toEqual(updatedEvent);
      expect(EventModel.findByIdAndUpdate).toHaveBeenCalledWith(
        '1', 
        updateEvent, 
        { new: true }
      );
    });
  });

  describe('delete', () => {
    it('should delete an Event', async () => {
      EventModel.findByIdAndDelete.mockResolvedValue({ id: '1' });
      
      const result = await EventsService.delete('1');
      
      expect(result).toBe(true);
      expect(EventModel.findByIdAndDelete).toHaveBeenCalledWith('1');
    });

    it('should return false if Event not found', async () => {
      EventModel.findByIdAndDelete.mockResolvedValue(null);
      
      const result = await EventsService.delete('999');
      
      expect(result).toBe(false);
    });
  });
});