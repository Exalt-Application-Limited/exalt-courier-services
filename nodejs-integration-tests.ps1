# Script to generate Node.js integration tests for courier services
# Creates tests for service interactions using Nock for HTTP mocking

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

# Define service dependencies for integration tests
$serviceIntegrations = @{
    "courier-geo-routing" = @(
        "courier-network-locations",
        "courier-location-tracker"
    )
    "courier-fare-calculator" = @(
        "courier-network-locations",
        "courier-geo-routing"
    )
    "courier-pickup-engine" = @(
        "courier-fare-calculator",
        "courier-location-tracker"
    )
}

# Node.js integration test template using Nock
$nodeIntegrationTestTemplate = @"
const nock = require('nock');
const {{CLIENT_SERVICE}}Client = require('../src/clients/{{CLIENT_SERVICE}}Client');

describe('{{CLIENT_SERVICE}} Integration Tests', () => {
  let client;
  
  beforeEach(() => {
    client = new {{CLIENT_SERVICE}}Client();
    // Reset nock between tests
    nock.cleanAll();
  });
  
  afterAll(() => {
    nock.restore();
  });

  describe('get{{ENTITY_NAME}}s', () => {
    it('should fetch all {{ENTITY_NAME}}s from the service', async () => {
      // Mock data
      const mock{{ENTITY_NAME}}s = [
        { id: '1', name: 'Test {{ENTITY_NAME}} 1' },
        { id: '2', name: 'Test {{ENTITY_NAME}} 2' }
      ];
      
      // Setup mock API
      nock('{{SERVICE_URL}}')
        .get('/api/{{API_PATH}}')
        .reply(200, mock{{ENTITY_NAME}}s);
        
      // Call the client
      const result = await client.get{{ENTITY_NAME}}s();
      
      // Verify the response
      expect(result).toHaveLength(2);
      expect(result[0].id).toBe('1');
    });
    
    it('should handle API errors', async () => {
      // Setup mock API error
      nock('{{SERVICE_URL}}')
        .get('/api/{{API_PATH}}')
        .reply(500, { message: 'Internal Server Error' });
        
      // Verify error handling works
      await expect(client.get{{ENTITY_NAME}}s()).rejects.toThrow();
    });
  });
  
  describe('get{{ENTITY_NAME}}ById', () => {
    it('should fetch a {{ENTITY_NAME}} by ID', async () => {
      // Mock data
      const mock{{ENTITY_NAME}} = { id: '1', name: 'Test {{ENTITY_NAME}}' };
      
      // Setup mock API
      nock('{{SERVICE_URL}}')
        .get('/api/{{API_PATH}}/1')
        .reply(200, mock{{ENTITY_NAME}});
        
      // Call the client
      const result = await client.get{{ENTITY_NAME}}ById('1');
      
      // Verify the response
      expect(result).toBeDefined();
      expect(result.id).toBe('1');
    });
  });
  
  describe('create{{ENTITY_NAME}}', () => {
    it('should create a new {{ENTITY_NAME}}', async () => {
      // Mock data
      const new{{ENTITY_NAME}} = { name: 'New {{ENTITY_NAME}}' };
      const created{{ENTITY_NAME}} = { id: '3', ...new{{ENTITY_NAME}} };
      
      // Setup mock API
      nock('{{SERVICE_URL}}')
        .post('/api/{{API_PATH}}', new{{ENTITY_NAME}})
        .reply(201, created{{ENTITY_NAME}});
        
      // Call the client
      const result = await client.create{{ENTITY_NAME}}(new{{ENTITY_NAME}});
      
      // Verify the response
      expect(result).toBeDefined();
      expect(result.id).toBe('3');
    });
  });
});
"@

# Node.js client template
$nodeClientTemplate = @"
const axios = require('axios');
const config = require('../config');

class {{CLIENT_SERVICE}}Client {
  constructor() {
    this.baseUrl = config.services.{{CLIENT_SERVICE_LOWER}}.url || '{{SERVICE_URL}}';
  }

  async get{{ENTITY_NAME}}s() {
    try {
      const response = await axios.get(`${this.baseUrl}/api/{{API_PATH}}`);
      return response.data;
    } catch (error) {
      console.error(`Error fetching {{ENTITY_NAME}}s: ${error.message}`);
      throw new Error(`Failed to fetch {{ENTITY_NAME}}s: ${error.message}`);
    }
  }

  async get{{ENTITY_NAME}}ById(id) {
    try {
      const response = await axios.get(`${this.baseUrl}/api/{{API_PATH}}/${id}`);
      return response.data;
    } catch (error) {
      console.error(`Error fetching {{ENTITY_NAME}} ${id}: ${error.message}`);
      throw new Error(`Failed to fetch {{ENTITY_NAME}} ${id}: ${error.message}`);
    }
  }

  async create{{ENTITY_NAME}}({{ENTITY_NAME_LOWER}}) {
    try {
      const response = await axios.post(`${this.baseUrl}/api/{{API_PATH}}`, {{ENTITY_NAME_LOWER}});
      return response.data;
    } catch (error) {
      console.error(`Error creating {{ENTITY_NAME}}: ${error.message}`);
      throw new Error(`Failed to create {{ENTITY_NAME}}: ${error.message}`);
    }
  }

  async update{{ENTITY_NAME}}(id, {{ENTITY_NAME_LOWER}}) {
    try {
      const response = await axios.put(`${this.baseUrl}/api/{{API_PATH}}/${id}`, {{ENTITY_NAME_LOWER}});
      return response.data;
    } catch (error) {
      console.error(`Error updating {{ENTITY_NAME}} ${id}: ${error.message}`);
      throw new Error(`Failed to update {{ENTITY_NAME}} ${id}: ${error.message}`);
    }
  }

  async delete{{ENTITY_NAME}}(id) {
    try {
      await axios.delete(`${this.baseUrl}/api/{{API_PATH}}/${id}`);
      return true;
    } catch (error) {
      console.error(`Error deleting {{ENTITY_NAME}} ${id}: ${error.message}`);
      throw new Error(`Failed to delete {{ENTITY_NAME}} ${id}: ${error.message}`);
    }
  }
}

module.exports = {{CLIENT_SERVICE}}Client;
"@

# Config template
$configTemplate = @"
module.exports = {
  services: {
{{SERVICE_CONFIG}}
  }
};
"@

foreach ($serviceKey in $serviceIntegrations.Keys) {
    $serviceDir = Join-Path -Path $baseDir -ChildPath $serviceKey
    $clientServices = $serviceIntegrations[$serviceKey]
    
    # Create Node.js test directories if they don't exist
    $integrationTestDir = Join-Path -Path $serviceDir -ChildPath "tests\integration"
    New-Item -ItemType Directory -Path $integrationTestDir -Force | Out-Null
    
    # Create client directory
    $clientDir = Join-Path -Path $serviceDir -ChildPath "src\clients"
    New-Item -ItemType Directory -Path $clientDir -Force | Out-Null
    
    # Add nock dependency if not exist
    $packageJsonPath = Join-Path -Path $serviceDir -ChildPath "package.json"
    
    if (Test-Path $packageJsonPath) {
        $packageJson = Get-Content -Path $packageJsonPath -Raw | ConvertFrom-Json
        
        if (-not ($packageJson.devDependencies.PSObject.Properties.Name -contains "nock")) {
            if (-not $packageJson.devDependencies) {
                $packageJson | Add-Member -Type NoteProperty -Name "devDependencies" -Value @{}
            }
            
            $packageJson.devDependencies | Add-Member -Type NoteProperty -Name "nock" -Value "^13.2.9" -Force
        }
        
        if (-not ($packageJson.dependencies.PSObject.Properties.Name -contains "axios")) {
            if (-not $packageJson.dependencies) {
                $packageJson | Add-Member -Type NoteProperty -Name "dependencies" -Value @{}
            }
            
            $packageJson.dependencies | Add-Member -Type NoteProperty -Name "axios" -Value "^1.2.0" -Force
        }
        
        $packageJson | ConvertTo-Json -Depth 10 | Set-Content -Path $packageJsonPath -Encoding UTF8
        Log-Message "Added nock and axios dependencies to $serviceKey" "UPDATE"
    }
    
    # Create config directory if it doesn't exist
    $configDir = Join-Path -Path $serviceDir -ChildPath "src\config"
    New-Item -ItemType Directory -Path $configDir -Force | Out-Null
    
    # Create service config entries
    $serviceConfig = ""
    
    # Create client and integration tests for each dependent service
    foreach ($clientService in $clientServices) {
        $clientServiceName = $clientService.Replace("-", "")
        $clientServiceClass = $clientServiceName.Substring(0, 1).ToUpper() + $clientServiceName.Substring(1)
        
        # Determine domain entity based on the client service
        $entityName = switch ($clientServiceName) {
            "couriernetworklocations" { "Location" }
            "courierlocationtracker" { "TrackingData" }
            "couriergeorouting" { "Route" }
            "courierfareCalculator" { "Fare" }
            default { $clientServiceName.Replace("courier", "") }
        }
        
        # Ensure first letter of entity is uppercase
        $entityName = $entityName.Substring(0, 1).ToUpper() + $entityName.Substring(1)
        $entityNameLower = $entityName.ToLower()
        
        $apiPath = $entityNameLower + "s"
        $serviceUrl = "http://$clientService:3000"
        
        # Add to service config
        $serviceConfig += "    $($clientServiceName.ToLower()): {`n      url: process.env.${clientServiceName.ToUpper()}_URL || '$serviceUrl'`n    },`n"
        
        # Create client file
        $clientPath = Join-Path -Path $clientDir -ChildPath "${clientServiceClass}Client.js"
        
        $clientContent = $nodeClientTemplate.Replace("{{CLIENT_SERVICE}}", $clientServiceClass)
        $clientContent = $clientContent.Replace("{{CLIENT_SERVICE_LOWER}}", $clientServiceName.ToLower())
        $clientContent = $clientContent.Replace("{{ENTITY_NAME}}", $entityName)
        $clientContent = $clientContent.Replace("{{ENTITY_NAME_LOWER}}", $entityNameLower)
        $clientContent = $clientContent.Replace("{{API_PATH}}", $apiPath)
        $clientContent = $clientContent.Replace("{{SERVICE_URL}}", $serviceUrl)
        
        Set-Content -Path $clientPath -Value $clientContent -Encoding UTF8
        Log-Message "Created client for $clientServiceClass in $serviceKey" "CREATE"
        
        # Create integration test
        $testPath = Join-Path -Path $integrationTestDir -ChildPath "${clientServiceClass}Integration.test.js"
        
        $testContent = $nodeIntegrationTestTemplate.Replace("{{CLIENT_SERVICE}}", $clientServiceClass)
        $testContent = $testContent.Replace("{{ENTITY_NAME}}", $entityName)
        $testContent = $testContent.Replace("{{API_PATH}}", $apiPath)
        $testContent = $testContent.Replace("{{SERVICE_URL}}", $serviceUrl)
        
        Set-Content -Path $testPath -Value $testContent -Encoding UTF8
        Log-Message "Created integration test for $clientServiceClass in $serviceKey" "CREATE"
    }
    
    # Trim the trailing comma from service config
    $serviceConfig = $serviceConfig.TrimEnd(",`n") + "`n"
    
    # Create config file
    $configPath = Join-Path -Path $configDir -ChildPath "index.js"
    $configContent = $configTemplate.Replace("{{SERVICE_CONFIG}}", $serviceConfig)
    Set-Content -Path $configPath -Value $configContent -Encoding UTF8
    Log-Message "Created service config for $serviceKey" "CREATE"
}

Log-Message "Node.js integration test generation completed" "DONE"
