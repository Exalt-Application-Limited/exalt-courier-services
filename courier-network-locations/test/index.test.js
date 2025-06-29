const request = require('supertest');
const app = require('../src/index');

describe('courier-network-locations API', () => {
  it('should return service info on GET /', async () => {
    const res = await request(app).get('/');
    expect(res.statusCode).toEqual(200);
    expect(res.body).toHaveProperty('service');
    expect(res.body.service).toEqual('courier-network-locations');
  });

  it('should return health status on GET /health', async () => {
    const res = await request(app).get('/health');
    expect(res.statusCode).toEqual(200);
    expect(res.body).toHaveProperty('status');
    expect(res.body.status).toEqual('UP');
  });
});
