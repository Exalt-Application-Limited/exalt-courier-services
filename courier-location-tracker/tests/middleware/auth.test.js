const auth = require('../src/middleware/auth');
const { verifyToken } = require('../src/utils/jwt');

jest.mock('../src/utils/jwt');

describe('Auth Middleware', () => {
  const mockRequest = () => {
    return {
      headers: {
        authorization: 'Bearer fake-token'
      }
    };
  };

  const mockResponse = () => {
    const res = {};
    res.status = jest.fn().mockReturnValue(res);
    res.json = jest.fn().mockReturnValue(res);
    return res;
  };

  const mockNext = jest.fn();

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should call next() when token is valid', () => {
    const req = mockRequest();
    const res = mockResponse();
    
    verifyToken.mockReturnValue({ id: 'user-id', role: 'user' });
    
    auth(req, res, mockNext);
    
    expect(req.user).toEqual({ id: 'user-id', role: 'user' });
    expect(verifyToken).toHaveBeenCalledWith('fake-token');
    expect(mockNext).toHaveBeenCalled();
  });

  it('should return 401 when no token is provided', () => {
    const req = { headers: {} };
    const res = mockResponse();
    
    auth(req, res, mockNext);
    
    expect(res.status).toHaveBeenCalledWith(401);
    expect(res.json).toHaveBeenCalledWith({ message: 'No token provided' });
    expect(mockNext).not.toHaveBeenCalled();
  });

  it('should return 401 when token is invalid', () => {
    const req = mockRequest();
    const res = mockResponse();
    
    verifyToken.mockImplementation(() => {
      throw new Error('Invalid token');
    });
    
    auth(req, res, mockNext);
    
    expect(res.status).toHaveBeenCalledWith(401);
    expect(res.json).toHaveBeenCalledWith({ message: 'Invalid token' });
    expect(mockNext).not.toHaveBeenCalled();
  });
});