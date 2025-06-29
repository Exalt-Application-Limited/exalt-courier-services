const request = require('supertest');
const app = require('../src/app');
const NetworklocationsService = require('../src/services/NetworklocationsService');

jest.mock('../src/services/NetworklocationsService');

describe('Networklocations Routes', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('GET /api/networklocations', () => {
    it('should return all Networklocations', async () => {
      const mockNetworklocations = [
        { id: '1', name: 'Test Networklocation 1' },
        { id: '2', name: 'Test Networklocation 2' }
      ];

      NetworklocationsService.getAll.mockResolvedValue(mockNetworklocations);

      const res = await request(app)
        .get('/api/networklocations')
        .expect('Content-Type', /json/)
        .expect(200);

      expect(res.body).toHaveLength(2);
      expect(NetworklocationsService.getAll).toHaveBeenCalled();
    });

    it('should handle errors', async () => {
      NetworklocationsService.getAll.mockRejectedValue(new Error('Database error'));

      await request(app)
        .get('/api/networklocations')
        .expect(500);
    });
  });

  describe('GET /api/networklocations/:id', () => {
    it('should return a single Networklocation', async () => {
      const mockNetworklocation = { id: '1', name: 'Test Networklocation' };
      NetworklocationsService.getById.mockResolvedValue(mockNetworklocation);

      const res = await request(app)
        .get('/api/networklocations/1')
        .expect('Content-Type', /json/)
        .expect(200);

      expect(res.body.id).toBe('1');
      expect(NetworklocationsService.getById).toHaveBeenCalledWith('1');
    });

    it('should return 404 if Networklocation not found', async () => {
      NetworklocationsService.getById.mockResolvedValue(null);

      await request(app)
        .get('/api/networklocations/999')
        .expect(404);
    });
  });

  describe('POST /api/networklocations', () => {
    it('should create a new Networklocation', async () => {
      const newNetworklocation = { name: 'New Networklocation' };
      const createdNetworklocation = { id: '3', ...newNetworklocation };
      
      NetworklocationsService.create.mockResolvedValue(createdNetworklocation);

      const res = await request(app)
        .post('/api/networklocations')
        .send(newNetworklocation)
        .expect('Content-Type', /json/)
        .expect(201);

      expect(res.body.id).toBe('3');
      expect(NetworklocationsService.create).toHaveBeenCalledWith(newNetworklocation);
    });
  });

  describe('PUT /api/networklocations/:id', () => {
    it('should update an existing Networklocation', async () => {
      const updateNetworklocation = { id: '1', name: 'Updated Networklocation' };
      
      NetworklocationsService.update.mockResolvedValue(updateNetworklocation);

      const res = await request(app)
        .put('/api/networklocations/1')
        .send(updateNetworklocation)
        .expect('Content-Type', /json/)
        .expect(200);

      expect(res.body.name).toBe('Updated Networklocation');
      expect(NetworklocationsService.update).toHaveBeenCalledWith('1', updateNetworklocation);
    });
  });

  describe('DELETE /api/networklocations/:id', () => {
    it('should delete an Networklocation', async () => {
      NetworklocationsService.delete.mockResolvedValue(true);

      await request(app)
        .delete('/api/networklocations/1')
        .expect(204);

      expect(NetworklocationsService.delete).toHaveBeenCalledWith('1');
    });
  });
});