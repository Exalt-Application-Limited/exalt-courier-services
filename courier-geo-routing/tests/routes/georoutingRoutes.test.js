const request = require('supertest');
const app = require('../src/app');
const GeoroutingService = require('../src/services/GeoroutingService');

jest.mock('../src/services/GeoroutingService');

describe('Georouting Routes', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('GET /api/georouting', () => {
    it('should return all Georoutings', async () => {
      const mockGeoroutings = [
        { id: '1', name: 'Test Georouting 1' },
        { id: '2', name: 'Test Georouting 2' }
      ];

      GeoroutingService.getAll.mockResolvedValue(mockGeoroutings);

      const res = await request(app)
        .get('/api/georouting')
        .expect('Content-Type', /json/)
        .expect(200);

      expect(res.body).toHaveLength(2);
      expect(GeoroutingService.getAll).toHaveBeenCalled();
    });

    it('should handle errors', async () => {
      GeoroutingService.getAll.mockRejectedValue(new Error('Database error'));

      await request(app)
        .get('/api/georouting')
        .expect(500);
    });
  });

  describe('GET /api/georouting/:id', () => {
    it('should return a single Georouting', async () => {
      const mockGeorouting = { id: '1', name: 'Test Georouting' };
      GeoroutingService.getById.mockResolvedValue(mockGeorouting);

      const res = await request(app)
        .get('/api/georouting/1')
        .expect('Content-Type', /json/)
        .expect(200);

      expect(res.body.id).toBe('1');
      expect(GeoroutingService.getById).toHaveBeenCalledWith('1');
    });

    it('should return 404 if Georouting not found', async () => {
      GeoroutingService.getById.mockResolvedValue(null);

      await request(app)
        .get('/api/georouting/999')
        .expect(404);
    });
  });

  describe('POST /api/georouting', () => {
    it('should create a new Georouting', async () => {
      const newGeorouting = { name: 'New Georouting' };
      const createdGeorouting = { id: '3', ...newGeorouting };
      
      GeoroutingService.create.mockResolvedValue(createdGeorouting);

      const res = await request(app)
        .post('/api/georouting')
        .send(newGeorouting)
        .expect('Content-Type', /json/)
        .expect(201);

      expect(res.body.id).toBe('3');
      expect(GeoroutingService.create).toHaveBeenCalledWith(newGeorouting);
    });
  });

  describe('PUT /api/georouting/:id', () => {
    it('should update an existing Georouting', async () => {
      const updateGeorouting = { id: '1', name: 'Updated Georouting' };
      
      GeoroutingService.update.mockResolvedValue(updateGeorouting);

      const res = await request(app)
        .put('/api/georouting/1')
        .send(updateGeorouting)
        .expect('Content-Type', /json/)
        .expect(200);

      expect(res.body.name).toBe('Updated Georouting');
      expect(GeoroutingService.update).toHaveBeenCalledWith('1', updateGeorouting);
    });
  });

  describe('DELETE /api/georouting/:id', () => {
    it('should delete an Georouting', async () => {
      GeoroutingService.delete.mockResolvedValue(true);

      await request(app)
        .delete('/api/georouting/1')
        .expect(204);

      expect(GeoroutingService.delete).toHaveBeenCalledWith('1');
    });
  });
});