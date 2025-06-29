const request = require('supertest');
const app = require('../src/app');
const EventsService = require('../src/services/EventsService');

jest.mock('../src/services/EventsService');

describe('Events Routes', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('GET /api/events', () => {
    it('should return all Events', async () => {
      const mockEvents = [
        { id: '1', name: 'Test Event 1' },
        { id: '2', name: 'Test Event 2' }
      ];

      EventsService.getAll.mockResolvedValue(mockEvents);

      const res = await request(app)
        .get('/api/events')
        .expect('Content-Type', /json/)
        .expect(200);

      expect(res.body).toHaveLength(2);
      expect(EventsService.getAll).toHaveBeenCalled();
    });

    it('should handle errors', async () => {
      EventsService.getAll.mockRejectedValue(new Error('Database error'));

      await request(app)
        .get('/api/events')
        .expect(500);
    });
  });

  describe('GET /api/events/:id', () => {
    it('should return a single Event', async () => {
      const mockEvent = { id: '1', name: 'Test Event' };
      EventsService.getById.mockResolvedValue(mockEvent);

      const res = await request(app)
        .get('/api/events/1')
        .expect('Content-Type', /json/)
        .expect(200);

      expect(res.body.id).toBe('1');
      expect(EventsService.getById).toHaveBeenCalledWith('1');
    });

    it('should return 404 if Event not found', async () => {
      EventsService.getById.mockResolvedValue(null);

      await request(app)
        .get('/api/events/999')
        .expect(404);
    });
  });

  describe('POST /api/events', () => {
    it('should create a new Event', async () => {
      const newEvent = { name: 'New Event' };
      const createdEvent = { id: '3', ...newEvent };
      
      EventsService.create.mockResolvedValue(createdEvent);

      const res = await request(app)
        .post('/api/events')
        .send(newEvent)
        .expect('Content-Type', /json/)
        .expect(201);

      expect(res.body.id).toBe('3');
      expect(EventsService.create).toHaveBeenCalledWith(newEvent);
    });
  });

  describe('PUT /api/events/:id', () => {
    it('should update an existing Event', async () => {
      const updateEvent = { id: '1', name: 'Updated Event' };
      
      EventsService.update.mockResolvedValue(updateEvent);

      const res = await request(app)
        .put('/api/events/1')
        .send(updateEvent)
        .expect('Content-Type', /json/)
        .expect(200);

      expect(res.body.name).toBe('Updated Event');
      expect(EventsService.update).toHaveBeenCalledWith('1', updateEvent);
    });
  });

  describe('DELETE /api/events/:id', () => {
    it('should delete an Event', async () => {
      EventsService.delete.mockResolvedValue(true);

      await request(app)
        .delete('/api/events/1')
        .expect(204);

      expect(EventsService.delete).toHaveBeenCalledWith('1');
    });
  });
});