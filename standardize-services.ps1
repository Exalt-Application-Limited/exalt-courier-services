# Standardization Script for Courier Services
# This script helps standardize the courier-services domain according to requirements

# Logging function
function Log-Message {
    param (
        [string]$Message,
        [string]$Type = "INFO"
    )
    $timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    Write-Host "[$timestamp] [$Type] $Message"
}

# Set base directory
$baseDir = "C:\Users\frich\Desktop\Exalt-Application-Limited\Exalt-Application-Limited\social-ecommerce-ecosystem\courier-services"
Set-Location $baseDir

Log-Message "Starting courier-services domain standardization" "START"

# 1. Service Renaming
$renameMap = @{
    "courier-management" = "courier-management-service"
    "courier-subscription" = "courier-subscription-service"
    "international-shipping" = "international-shipping-service"
    "tracking-service" = "courier-tracking-service"
    # Add more renames as identified
}

# 2. Clean up log files and temporary files
Log-Message "Cleaning up log and temporary files" "CLEAN"
Get-ChildItem -Path $baseDir -Include "*.log", "*.tmp", "*.backup" -Recurse | ForEach-Object {
    Log-Message "Deleting file: $($_.FullName)" "DELETE"
    Remove-Item $_.FullName -Force
}

# 3. Check and create .env.template and .gitignore files
Log-Message "Checking for .env.template and .gitignore files" "CHECK"

$services = @(
    # Java Services
    "courier-management-service",
    "courier-subscription-service",
    "international-shipping-service",
    "commission-service",
    "payout-service",
    "notification-service",
    "regional-courier-service",
    "courier-tracking-service",
    # Node.js Services
    "courier-network-locations",
    "courier-events-service",
    "courier-geo-routing",
    "courier-fare-calculator",
    "courier-pickup-engine",
    "courier-location-tracker"
)

# Standard .gitignore content for Java services
$javaGitignore = @"
HELP.md
target/
!.mvn/wrapper/maven-wrapper.jar
!**/src/main/**/target/
!**/src/test/**/target/

### STS ###
.apt_generated
.classpath
.factorypath
.project
.settings
.springBeans
.sts4-cache

### IntelliJ IDEA ###
.idea
*.iws
*.iml
*.ipr

### NetBeans ###
/nbproject/private/
/nbbuild/
/dist/
/nbdist/
/.nb-gradle/
build/
!**/src/main/**/build/
!**/src/test/**/build/

### VS Code ###
.vscode/

### Logs ###
*.log

### Environment Files ###
.env
"@

# Standard .gitignore content for Node.js services
$nodeGitignore = @"
# Dependencies
node_modules/
npm-debug.log
yarn-debug.log
yarn-error.log

# Environment
.env
.env.local
.env.development.local
.env.test.local
.env.production.local

# Build output
dist/
build/
.next/
out/

# Coverage
coverage/

# Logs
logs/
*.log
npm-debug.log*
yarn-debug.log*
yarn-error.log*

# Misc
.DS_Store
.idea/
.vscode/
*.suo
*.ntvs*
*.njsproj
*.sln
*.sw?
"@

# Standard .env.template for Java services
$javaEnvTemplate = @"
# Database Configuration
DB_HOST=localhost
DB_PORT=5432
DB_NAME=courierdb
DB_USERNAME=dbuser
DB_PASSWORD=dbpass

# Application Configuration
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=dev

# Service Discovery and Configuration
EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=http://localhost:8761/eureka/
CONFIG_SERVER_URI=http://localhost:8888

# JWT Authentication
JWT_SECRET=your_jwt_secret_key_here
JWT_EXPIRATION_MS=86400000

# Logging
LOG_LEVEL=INFO
"@

# Standard .env.template for Node.js services
$nodeEnvTemplate = @"
# Server Configuration
PORT=3000
NODE_ENV=development

# Database Configuration
DB_HOST=localhost
DB_PORT=27017
DB_NAME=courier
DB_USER=dbuser
DB_PASS=dbpass

# Service Discovery
EUREKA_HOST=localhost
EUREKA_PORT=8761

# API Keys and External Services
API_KEY=your_api_key_here

# JWT Authentication
JWT_SECRET=your_jwt_secret_key_here
JWT_EXPIRATION=86400000

# Logging
LOG_LEVEL=info
"@

# Check each service
foreach ($service in $services) {
    $serviceDir = Join-Path -Path $baseDir -ChildPath $service
    
    # Check if directory exists, if not create it for missing services
    if (-not (Test-Path $serviceDir)) {
        Log-Message "Creating directory for missing service: $service" "CREATE"
        New-Item -ItemType Directory -Path $serviceDir | Out-Null
    }
    
    # Check if service is Java or Node.js based (by convention or by checking for pom.xml)
    $isJava = $service -match "-service$" -or (Test-Path (Join-Path -Path $serviceDir -ChildPath "pom.xml"))
    
    # Create .gitignore if missing
    $gitignorePath = Join-Path -Path $serviceDir -ChildPath ".gitignore"
    if (-not (Test-Path $gitignorePath)) {
        Log-Message "Creating .gitignore for $service" "CREATE"
        if ($isJava) {
            $javaGitignore | Out-File -FilePath $gitignorePath -Encoding utf8
        } else {
            $nodeGitignore | Out-File -FilePath $gitignorePath -Encoding utf8
        }
    }
    
    # Create .env.template if missing
    $envTemplatePath = Join-Path -Path $serviceDir -ChildPath ".env.template"
    if (-not (Test-Path $envTemplatePath)) {
        Log-Message "Creating .env.template for $service" "CREATE"
        if ($isJava) {
            $javaEnvTemplate | Out-File -FilePath $envTemplatePath -Encoding utf8
        } else {
            $nodeEnvTemplate | Out-File -FilePath $envTemplatePath -Encoding utf8
        }
    }
}

Log-Message "Standardization script completed" "DONE"
