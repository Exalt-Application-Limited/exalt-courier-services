const request = require('supertest');
const app = require('../src/app');
const FarecalculatorService = require('../src/services/FarecalculatorService');

jest.mock('../src/services/FarecalculatorService');

describe('Farecalculator Routes', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('GET /api/farecalculator', () => {
    it('should return all Farecalculators', async () => {
      const mockFarecalculators = [
        { id: '1', name: 'Test Farecalculator 1' },
        { id: '2', name: 'Test Farecalculator 2' }
      ];

      FarecalculatorService.getAll.mockResolvedValue(mockFarecalculators);

      const res = await request(app)
        .get('/api/farecalculator')
        .expect('Content-Type', /json/)
        .expect(200);

      expect(res.body).toHaveLength(2);
      expect(FarecalculatorService.getAll).toHaveBeenCalled();
    });

    it('should handle errors', async () => {
      FarecalculatorService.getAll.mockRejectedValue(new Error('Database error'));

      await request(app)
        .get('/api/farecalculator')
        .expect(500);
    });
  });

  describe('GET /api/farecalculator/:id', () => {
    it('should return a single Farecalculator', async () => {
      const mockFarecalculator = { id: '1', name: 'Test Farecalculator' };
      FarecalculatorService.getById.mockResolvedValue(mockFarecalculator);

      const res = await request(app)
        .get('/api/farecalculator/1')
        .expect('Content-Type', /json/)
        .expect(200);

      expect(res.body.id).toBe('1');
      expect(FarecalculatorService.getById).toHaveBeenCalledWith('1');
    });

    it('should return 404 if Farecalculator not found', async () => {
      FarecalculatorService.getById.mockResolvedValue(null);

      await request(app)
        .get('/api/farecalculator/999')
        .expect(404);
    });
  });

  describe('POST /api/farecalculator', () => {
    it('should create a new Farecalculator', async () => {
      const newFarecalculator = { name: 'New Farecalculator' };
      const createdFarecalculator = { id: '3', ...newFarecalculator };
      
      FarecalculatorService.create.mockResolvedValue(createdFarecalculator);

      const res = await request(app)
        .post('/api/farecalculator')
        .send(newFarecalculator)
        .expect('Content-Type', /json/)
        .expect(201);

      expect(res.body.id).toBe('3');
      expect(FarecalculatorService.create).toHaveBeenCalledWith(newFarecalculator);
    });
  });

  describe('PUT /api/farecalculator/:id', () => {
    it('should update an existing Farecalculator', async () => {
      const updateFarecalculator = { id: '1', name: 'Updated Farecalculator' };
      
      FarecalculatorService.update.mockResolvedValue(updateFarecalculator);

      const res = await request(app)
        .put('/api/farecalculator/1')
        .send(updateFarecalculator)
        .expect('Content-Type', /json/)
        .expect(200);

      expect(res.body.name).toBe('Updated Farecalculator');
      expect(FarecalculatorService.update).toHaveBeenCalledWith('1', updateFarecalculator);
    });
  });

  describe('DELETE /api/farecalculator/:id', () => {
    it('should delete an Farecalculator', async () => {
      FarecalculatorService.delete.mockResolvedValue(true);

      await request(app)
        .delete('/api/farecalculator/1')
        .expect(204);

      expect(FarecalculatorService.delete).toHaveBeenCalledWith('1');
    });
  });
});