# Script to generate test templates for Node.js services
# Creates Jest test files for routes, controllers, and services

function Log-Message {
    param (
        [string]$Message,
        [string]$Type = "INFO"
    )
    $timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    Write-Host "[$timestamp] [$Type] $Message"
}

$baseDir = "C:\Users\frich\Desktop\Exalt-Application-Limited\Exalt-Application-Limited\social-ecommerce-ecosystem\courier-services"
Set-Location $baseDir

# List of Node.js services
$nodeServices = @(
    "courier-network-locations",
    "courier-events-service",
    "courier-geo-routing",
    "courier-fare-calculator",
    "courier-pickup-engine",
    "courier-location-tracker"
)

# Route test template
$routeTestTemplate = @"
const request = require('supertest');
const app = require('../src/app');
const {{SERVICE_NAME}}Service = require('../src/services/{{SERVICE_NAME}}Service');

jest.mock('../src/services/{{SERVICE_NAME}}Service');

describe('{{ROUTE_NAME}} Routes', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('GET /api/{{API_PATH}}', () => {
    it('should return all {{ENTITY_NAME}}s', async () => {
      const mock{{ENTITY_NAME}}s = [
        { id: '1', name: 'Test {{ENTITY_NAME}} 1' },
        { id: '2', name: 'Test {{ENTITY_NAME}} 2' }
      ];

      {{SERVICE_NAME}}Service.getAll.mockResolvedValue(mock{{ENTITY_NAME}}s);

      const res = await request(app)
        .get('/api/{{API_PATH}}')
        .expect('Content-Type', /json/)
        .expect(200);

      expect(res.body).toHaveLength(2);
      expect({{SERVICE_NAME}}Service.getAll).toHaveBeenCalled();
    });

    it('should handle errors', async () => {
      {{SERVICE_NAME}}Service.getAll.mockRejectedValue(new Error('Database error'));

      await request(app)
        .get('/api/{{API_PATH}}')
        .expect(500);
    });
  });

  describe('GET /api/{{API_PATH}}/:id', () => {
    it('should return a single {{ENTITY_NAME}}', async () => {
      const mock{{ENTITY_NAME}} = { id: '1', name: 'Test {{ENTITY_NAME}}' };
      {{SERVICE_NAME}}Service.getById.mockResolvedValue(mock{{ENTITY_NAME}});

      const res = await request(app)
        .get('/api/{{API_PATH}}/1')
        .expect('Content-Type', /json/)
        .expect(200);

      expect(res.body.id).toBe('1');
      expect({{SERVICE_NAME}}Service.getById).toHaveBeenCalledWith('1');
    });

    it('should return 404 if {{ENTITY_NAME}} not found', async () => {
      {{SERVICE_NAME}}Service.getById.mockResolvedValue(null);

      await request(app)
        .get('/api/{{API_PATH}}/999')
        .expect(404);
    });
  });

  describe('POST /api/{{API_PATH}}', () => {
    it('should create a new {{ENTITY_NAME}}', async () => {
      const new{{ENTITY_NAME}} = { name: 'New {{ENTITY_NAME}}' };
      const created{{ENTITY_NAME}} = { id: '3', ...new{{ENTITY_NAME}} };
      
      {{SERVICE_NAME}}Service.create.mockResolvedValue(created{{ENTITY_NAME}});

      const res = await request(app)
        .post('/api/{{API_PATH}}')
        .send(new{{ENTITY_NAME}})
        .expect('Content-Type', /json/)
        .expect(201);

      expect(res.body.id).toBe('3');
      expect({{SERVICE_NAME}}Service.create).toHaveBeenCalledWith(new{{ENTITY_NAME}});
    });
  });

  describe('PUT /api/{{API_PATH}}/:id', () => {
    it('should update an existing {{ENTITY_NAME}}', async () => {
      const update{{ENTITY_NAME}} = { id: '1', name: 'Updated {{ENTITY_NAME}}' };
      
      {{SERVICE_NAME}}Service.update.mockResolvedValue(update{{ENTITY_NAME}});

      const res = await request(app)
        .put('/api/{{API_PATH}}/1')
        .send(update{{ENTITY_NAME}})
        .expect('Content-Type', /json/)
        .expect(200);

      expect(res.body.name).toBe('Updated {{ENTITY_NAME}}');
      expect({{SERVICE_NAME}}Service.update).toHaveBeenCalledWith('1', update{{ENTITY_NAME}});
    });
  });

  describe('DELETE /api/{{API_PATH}}/:id', () => {
    it('should delete an {{ENTITY_NAME}}', async () => {
      {{SERVICE_NAME}}Service.delete.mockResolvedValue(true);

      await request(app)
        .delete('/api/{{API_PATH}}/1')
        .expect(204);

      expect({{SERVICE_NAME}}Service.delete).toHaveBeenCalledWith('1');
    });
  });
});
"@

# Service test template
$serviceTestTemplate = @"
const {{SERVICE_NAME}}Service = require('../src/services/{{SERVICE_NAME}}Service');
const {{ENTITY_NAME}}Model = require('../src/models/{{ENTITY_NAME}}');

// Mock the model
jest.mock('../src/models/{{ENTITY_NAME}}');

describe('{{SERVICE_NAME}}Service', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('getAll', () => {
    it('should return all {{ENTITY_NAME}}s', async () => {
      const mock{{ENTITY_NAME}}s = [
        { id: '1', name: 'Test {{ENTITY_NAME}} 1' },
        { id: '2', name: 'Test {{ENTITY_NAME}} 2' }
      ];
      
      {{ENTITY_NAME}}Model.find.mockResolvedValue(mock{{ENTITY_NAME}}s);
      
      const result = await {{SERVICE_NAME}}Service.getAll();
      
      expect(result).toHaveLength(2);
      expect({{ENTITY_NAME}}Model.find).toHaveBeenCalled();
    });

    it('should handle errors', async () => {
      {{ENTITY_NAME}}Model.find.mockRejectedValue(new Error('Database error'));
      
      await expect({{SERVICE_NAME}}Service.getAll()).rejects.toThrow('Database error');
    });
  });

  describe('getById', () => {
    it('should return a single {{ENTITY_NAME}}', async () => {
      const mock{{ENTITY_NAME}} = { id: '1', name: 'Test {{ENTITY_NAME}}' };
      
      {{ENTITY_NAME}}Model.findById.mockResolvedValue(mock{{ENTITY_NAME}});
      
      const result = await {{SERVICE_NAME}}Service.getById('1');
      
      expect(result).toEqual(mock{{ENTITY_NAME}});
      expect({{ENTITY_NAME}}Model.findById).toHaveBeenCalledWith('1');
    });

    it('should return null if {{ENTITY_NAME}} not found', async () => {
      {{ENTITY_NAME}}Model.findById.mockResolvedValue(null);
      
      const result = await {{SERVICE_NAME}}Service.getById('999');
      
      expect(result).toBeNull();
    });
  });

  describe('create', () => {
    it('should create a new {{ENTITY_NAME}}', async () => {
      const new{{ENTITY_NAME}} = { name: 'New {{ENTITY_NAME}}' };
      const created{{ENTITY_NAME}} = { id: '3', ...new{{ENTITY_NAME}} };
      
      {{ENTITY_NAME}}Model.create.mockResolvedValue(created{{ENTITY_NAME}});
      
      const result = await {{SERVICE_NAME}}Service.create(new{{ENTITY_NAME}});
      
      expect(result).toEqual(created{{ENTITY_NAME}});
      expect({{ENTITY_NAME}}Model.create).toHaveBeenCalledWith(new{{ENTITY_NAME}});
    });
  });

  describe('update', () => {
    it('should update an existing {{ENTITY_NAME}}', async () => {
      const update{{ENTITY_NAME}} = { name: 'Updated {{ENTITY_NAME}}' };
      const updated{{ENTITY_NAME}} = { id: '1', ...update{{ENTITY_NAME}} };
      
      {{ENTITY_NAME}}Model.findByIdAndUpdate.mockResolvedValue(updated{{ENTITY_NAME}});
      
      const result = await {{SERVICE_NAME}}Service.update('1', update{{ENTITY_NAME}});
      
      expect(result).toEqual(updated{{ENTITY_NAME}});
      expect({{ENTITY_NAME}}Model.findByIdAndUpdate).toHaveBeenCalledWith(
        '1', 
        update{{ENTITY_NAME}}, 
        { new: true }
      );
    });
  });

  describe('delete', () => {
    it('should delete an {{ENTITY_NAME}}', async () => {
      {{ENTITY_NAME}}Model.findByIdAndDelete.mockResolvedValue({ id: '1' });
      
      const result = await {{SERVICE_NAME}}Service.delete('1');
      
      expect(result).toBe(true);
      expect({{ENTITY_NAME}}Model.findByIdAndDelete).toHaveBeenCalledWith('1');
    });

    it('should return false if {{ENTITY_NAME}} not found', async () => {
      {{ENTITY_NAME}}Model.findByIdAndDelete.mockResolvedValue(null);
      
      const result = await {{SERVICE_NAME}}Service.delete('999');
      
      expect(result).toBe(false);
    });
  });
});
"@

# Middleware test template
$middlewareTestTemplate = @"
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
"@

foreach ($service in $nodeServices) {
    $serviceDir = Join-Path -Path $baseDir -ChildPath $service
    $testsDir = Join-Path -Path $serviceDir -ChildPath "tests"
    
    if (-not (Test-Path $testsDir)) {
        New-Item -ItemType Directory -Path $testsDir -Force | Out-Null
        Log-Message "Created tests directory for $service" "CREATE"
    }
    
    # Derive entity name from service name
    $serviceName = $service -replace "courier-", ""
    $serviceName = $serviceName -replace "-service", ""
    $serviceName = $serviceName -replace "-", ""
    
    # Uppercase first letter
    $serviceName = $serviceName.Substring(0, 1).ToUpper() + $serviceName.Substring(1)
    
    # Determine entity name
    $entityName = $serviceName
    if ($entityName -match "s$") {
        $entityName = $entityName -replace "s$", ""
    }
    
    # Create directories for test organization
    $routeTestsDir = Join-Path -Path $testsDir -ChildPath "routes"
    $serviceTestsDir = Join-Path -Path $testsDir -ChildPath "services"
    $middlewareTestsDir = Join-Path -Path $testsDir -ChildPath "middleware"
    $utilsTestsDir = Join-Path -Path $testsDir -ChildPath "utils"
    
    # Create directories
    $testDirs = @($routeTestsDir, $serviceTestsDir, $middlewareTestsDir, $utilsTestsDir)
    foreach ($dir in $testDirs) {
        if (-not (Test-Path $dir)) {
            New-Item -ItemType Directory -Path $dir -Force | Out-Null
        }
    }
    
    # Create route tests
    $routeName = $serviceName.ToLower()
    $apiPath = $routeName
    $routeTestFilePath = Join-Path -Path $routeTestsDir -ChildPath "${routeName}Routes.test.js"
    
    # Generate content for route test
    $testContent = $routeTestTemplate.Replace("{{SERVICE_NAME}}", $serviceName)
    $testContent = $testContent.Replace("{{ROUTE_NAME}}", $serviceName)
    $testContent = $testContent.Replace("{{ENTITY_NAME}}", $entityName)
    $testContent = $testContent.Replace("{{API_PATH}}", $apiPath)
    
    Set-Content -Path $routeTestFilePath -Value $testContent -NoNewline
    Log-Message "Generated route test for $serviceName" "CREATE"
    
    # Create service tests
    $serviceTestFilePath = Join-Path -Path $serviceTestsDir -ChildPath "${serviceName}Service.test.js"
    
    # Generate content for service test
    $testContent = $serviceTestTemplate.Replace("{{SERVICE_NAME}}", $serviceName)
    $testContent = $testContent.Replace("{{ENTITY_NAME}}", $entityName)
    
    Set-Content -Path $serviceTestFilePath -Value $testContent -NoNewline
    Log-Message "Generated service test for $serviceName" "CREATE"
    
    # Create middleware test
    $middlewareTestFilePath = Join-Path -Path $middlewareTestsDir -ChildPath "auth.test.js"
    
    # Generate content for middleware test
    Set-Content -Path $middlewareTestFilePath -Value $middlewareTestTemplate -NoNewline
    Log-Message "Generated middleware test for $service" "CREATE"
}

Log-Message "Node.js test template generation completed" "DONE"
