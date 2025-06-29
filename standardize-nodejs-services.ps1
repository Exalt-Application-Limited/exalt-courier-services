# Script to standardize Node.js services
# This script ensures proper package.json, ESLint and Prettier configs for Node.js services

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

Log-Message "Starting Node.js service standardization" "START"

# List of Node.js services
$nodejsServices = @(
    "courier-network-locations",
    "courier-events-service",
    "courier-geo-routing",
    "courier-fare-calculator",
    "courier-pickup-engine",
    "courier-location-tracker"
)

# Standard package.json
$standardPackageJson = @'
{
  "name": "SERVICE_NAME_PLACEHOLDER",
  "version": "1.0.0",
  "description": "SERVICE_DESCRIPTION_PLACEHOLDER",
  "main": "src/index.js",
  "scripts": {
    "start": "node src/index.js",
    "dev": "nodemon src/index.js",
    "test": "jest --coverage",
    "lint": "eslint .",
    "lint:fix": "eslint . --fix",
    "format": "prettier --write \"src/**/*.js\""
  },
  "keywords": [
    "courier",
    "microservice",
    "exalt"
  ],
  "author": "Exalt Application Limited",
  "license": "UNLICENSED",
  "dependencies": {
    "axios": "^1.5.0",
    "cors": "^2.8.5",
    "dotenv": "^16.3.1",
    "eureka-js-client": "^4.5.0",
    "express": "^4.18.2",
    "express-validator": "^7.0.1",
    "helmet": "^7.0.0",
    "joi": "^17.10.1",
    "mongoose": "^7.5.1",
    "morgan": "^1.10.0",
    "winston": "^3.10.0"
  },
  "devDependencies": {
    "eslint": "^8.49.0",
    "eslint-config-prettier": "^9.0.0",
    "eslint-plugin-jest": "^27.2.3",
    "eslint-plugin-prettier": "^5.0.0",
    "jest": "^29.6.4",
    "nodemon": "^3.0.1",
    "prettier": "^3.0.3",
    "supertest": "^6.3.3"
  },
  "engines": {
    "node": ">=16.0.0"
  }
}
'@

# Standard .eslintrc.js
$standardEslintrc = @'
module.exports = {
  env: {
    node: true,
    es2021: true,
    jest: true
  },
  extends: ["eslint:recommended", "plugin:prettier/recommended"],
  parserOptions: {
    ecmaVersion: 12
  },
  rules: {
    "no-console": "warn",
    "prettier/prettier": "error"
  }
};
'@

# Standard .prettierrc
$standardPrettierrc = @'
{
  "printWidth": 100,
  "tabWidth": 2,
  "useTabs": false,
  "semi": true,
  "singleQuote": true,
  "trailingComma": "none",
  "bracketSpacing": true
}
'@

# Standard index.js
$standardIndexJs = @'
require("dotenv").config();
const express = require("express");
const cors = require("cors");
const helmet = require("helmet");
const morgan = require("morgan");
const { eurekaClient } = require("./eureka-client");

// Initialize Express app
const app = express();
const PORT = process.env.PORT || 3000;

// Middleware
app.use(helmet());
app.use(cors());
app.use(express.json());
app.use(morgan("dev"));

// Health check endpoint
app.get("/health", (req, res) => {
  res.status(200).json({ status: "UP" });
});

// Routes
app.get("/", (req, res) => {
  res.status(200).json({
    service: "SERVICE_NAME_PLACEHOLDER",
    version: "1.0.0",
    status: "running"
  });
});

// Error handling middleware
app.use((err, req, res, next) => {
  console.error(err.stack);
  res.status(500).json({
    error: "Internal Server Error",
    message: err.message
  });
});

// Start server
app.listen(PORT, () => {
  console.log(`SERVICE_NAME_PLACEHOLDER service running on port ${PORT}`);
  // Register with Eureka if in production
  if (process.env.NODE_ENV === "production") {
    eurekaClient.start();
  }
});

module.exports = app; // For testing
'@

# Standard eureka-client.js
$standardEurekaClient = @'
const Eureka = require("eureka-js-client").Eureka;

const eurekaHost = process.env.EUREKA_HOST || "localhost";
const eurekaPort = process.env.EUREKA_PORT || 8761;
const hostName = process.env.HOSTNAME || "localhost";
const ipAddr = process.env.IP_ADDR || "127.0.0.1";
const serviceName = process.env.SERVICE_NAME || "SERVICE_NAME_PLACEHOLDER";

exports.eurekaClient = new Eureka({
  instance: {
    app: serviceName,
    hostName: hostName,
    ipAddr: ipAddr,
    port: {
      $: process.env.PORT || 3000,
      "@enabled": true
    },
    vipAddress: serviceName,
    dataCenterInfo: {
      "@class": "com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo",
      name: "MyOwn"
    },
    registerWithEureka: true,
    fetchRegistry: true
  },
  eureka: {
    host: eurekaHost,
    port: eurekaPort,
    servicePath: "/eureka/apps/"
  }
});
'@

# Standard jest config
$standardJestConfig = @'
module.exports = {
  testEnvironment: "node",
  coveragePathIgnorePatterns: ["/node_modules/"],
  coverageThreshold: {
    global: {
      branches: 80,
      functions: 80,
      lines: 80,
      statements: 80
    }
  }
};
'@

foreach ($service in $nodejsServices) {
    $serviceDir = Join-Path -Path $baseDir -ChildPath $service
    
    # Create directory if it doesn't exist
    if (-not (Test-Path $serviceDir)) {
        New-Item -ItemType Directory -Path $serviceDir -Force | Out-Null
        Log-Message "Created directory for $service" "CREATE"
    }
    
    # Customize package.json
    $customPackageJson = $standardPackageJson.Replace("SERVICE_NAME_PLACEHOLDER", $service).Replace("SERVICE_DESCRIPTION_PLACEHOLDER", "Exalt Courier Services - $service")
    $packageJsonPath = Join-Path -Path $serviceDir -ChildPath "package.json"
    Set-Content -Path $packageJsonPath -Value $customPackageJson -Force
    Log-Message "Created/Updated package.json for $service" "UPDATE"
    
    # Create ESLint config
    $eslintPath = Join-Path -Path $serviceDir -ChildPath ".eslintrc.js"
    Set-Content -Path $eslintPath -Value $standardEslintrc -Force
    Log-Message "Created/Updated .eslintrc.js for $service" "UPDATE"
    
    # Create Prettier config
    $prettierPath = Join-Path -Path $serviceDir -ChildPath ".prettierrc"
    Set-Content -Path $prettierPath -Value $standardPrettierrc -Force
    Log-Message "Created/Updated .prettierrc for $service" "UPDATE"
    
    # Create src directory
    $srcDir = Join-Path -Path $serviceDir -ChildPath "src"
    if (-not (Test-Path $srcDir)) {
        New-Item -ItemType Directory -Path $srcDir -Force | Out-Null
    }
    
    # Create index.js if it doesn't exist
    $indexPath = Join-Path -Path $srcDir -ChildPath "index.js"
    if (-not (Test-Path $indexPath)) {
        $customIndexJs = $standardIndexJs.Replace("SERVICE_NAME_PLACEHOLDER", $service)
        Set-Content -Path $indexPath -Value $customIndexJs -Force
        Log-Message "Created index.js for $service" "CREATE"
    }
    
    # Create eureka-client.js if it doesn't exist
    $eurekaPath = Join-Path -Path $srcDir -ChildPath "eureka-client.js"
    if (-not (Test-Path $eurekaPath)) {
        $customEurekaClient = $standardEurekaClient.Replace("SERVICE_NAME_PLACEHOLDER", $service)
        Set-Content -Path $eurekaPath -Value $customEurekaClient -Force
        Log-Message "Created eureka-client.js for $service" "CREATE"
    }
    
    # Create test directory
    $testDir = Join-Path -Path $serviceDir -ChildPath "test"
    if (-not (Test-Path $testDir)) {
        New-Item -ItemType Directory -Path $testDir -Force | Out-Null
    }
    
    # Create jest.config.js
    $jestConfigPath = Join-Path -Path $serviceDir -ChildPath "jest.config.js"
    Set-Content -Path $jestConfigPath -Value $standardJestConfig -Force
    Log-Message "Created/Updated jest.config.js for $service" "UPDATE"
    
    # Create basic test file
    $testFilePath = Join-Path -Path $testDir -ChildPath "index.test.js"
    if (-not (Test-Path $testFilePath)) {
        $testContent = @"
const request = require('supertest');
const app = require('../src/index');

describe('$service API', () => {
  it('should return service info on GET /', async () => {
    const res = await request(app).get('/');
    expect(res.statusCode).toEqual(200);
    expect(res.body).toHaveProperty('service');
    expect(res.body.service).toEqual('$service');
  });

  it('should return health status on GET /health', async () => {
    const res = await request(app).get('/health');
    expect(res.statusCode).toEqual(200);
    expect(res.body).toHaveProperty('status');
    expect(res.body.status).toEqual('UP');
  });
});
"@
        Set-Content -Path $testFilePath -Value $testContent -Force
        Log-Message "Created basic test file for $service" "CREATE"
    }
}

Log-Message "Node.js service standardization completed" "DONE"
