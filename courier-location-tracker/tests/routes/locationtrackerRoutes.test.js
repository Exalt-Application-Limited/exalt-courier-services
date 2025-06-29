const request = require('supertest');
const app = require('../src/app');
const LocationtrackerService = require('../src/services/LocationtrackerService');

jest.mock('../src/services/LocationtrackerService');

describe('Locationtracker Routes', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('GET /api/locationtracker', () => {
    it('should return all Locationtrackers', async () => {
      const mockLocationtrackers = [
        { id: '1', name: 'Test Locationtracker 1' },
        { id: '2', name: 'Test Locationtracker 2' }
      ];

      LocationtrackerService.getAll.mockResolvedValue(mockLocationtrackers);

      const res = await request(app)
        .get('/api/locationtracker')
        .expect('Content-Type', /json/)
        .expect(200);

      expect(res.body).toHaveLength(2);
      expect(LocationtrackerService.getAll).toHaveBeenCalled();
    });

    it('should handle errors', async () => {
      LocationtrackerService.getAll.mockRejectedValue(new Error('Database error'));

      await request(app)
        .get('/api/locationtracker')
        .expect(500);
    });
  });

  describe('GET /api/locationtracker/:id', () => {
    it('should return a single Locationtracker', async () => {
      const mockLocationtracker = { id: '1', name: 'Test Locationtracker' };
      LocationtrackerService.getById.mockResolvedValue(mockLocationtracker);

      const res = await request(app)
        .get('/api/locationtracker/1')
        .expect('Content-Type', /json/)
        .expect(200);

      expect(res.body.id).toBe('1');
      expect(LocationtrackerService.getById).toHaveBeenCalledWith('1');
    });

    it('should return 404 if Locationtracker not found', async () => {
      LocationtrackerService.getById.mockResolvedValue(null);

      await request(app)
        .get('/api/locationtracker/999')
        .expect(404);
    });
  });

  describe('POST /api/locationtracker', () => {
    it('should create a new Locationtracker', async () => {
      const newLocationtracker = { name: 'New Locationtracker' };
      const createdLocationtracker = { id: '3', ...newLocationtracker };
      
      LocationtrackerService.create.mockResolvedValue(createdLocationtracker);

      const res = await request(app)
        .post('/api/locationtracker')
        .send(newLocationtracker)
        .expect('Content-Type', /json/)
        .expect(201);

      expect(res.body.id).toBe('3');
      expect(LocationtrackerService.create).toHaveBeenCalledWith(newLocationtracker);
    });
  });

  describe('PUT /api/locationtracker/:id', () => {
    it('should update an existing Locationtracker', async () => {
      const updateLocationtracker = { id: '1', name: 'Updated Locationtracker' };
      
      LocationtrackerService.update.mockResolvedValue(updateLocationtracker);

      const res = await request(app)
        .put('/api/locationtracker/1')
        .send(updateLocationtracker)
        .expect('Content-Type', /json/)
        .expect(200);

      expect(res.body.name).toBe('Updated Locationtracker');
      expect(LocationtrackerService.update).toHaveBeenCalledWith('1', updateLocationtracker);
    });
  });

  describe('DELETE /api/locationtracker/:id', () => {
    it('should delete an Locationtracker', async () => {
      LocationtrackerService.delete.mockResolvedValue(true);

      await request(app)
        .delete('/api/locationtracker/1')
        .expect(204);

      expect(LocationtrackerService.delete).toHaveBeenCalledWith('1');
    });
  });
});