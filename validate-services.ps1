# Script to validate all services against standardization requirements
# Checks Java and Node.js services for required files, configurations, and proper versioning

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

Log-Message "Starting service validation" "START"

# List of Java services
$javaServices = @(
    "courier-management-service",
    "courier-subscription-service",
    "international-shipping-service",
    "commission-service",
    "payout-service",
    "notification-service",
    "regional-courier-service",
    "courier-tracking-service"
)

# List of Node.js services
$nodejsServices = @(
    "courier-network-locations",
    "courier-events-service",
    "courier-geo-routing",
    "courier-fare-calculator",
    "courier-pickup-engine",
    "courier-location-tracker"
)

# Counter for validation issues
$javaIssues = 0
$nodeIssues = 0

# Validate Java services
foreach ($service in $javaServices) {
    $serviceDir = Join-Path -Path $baseDir -ChildPath $service
    Log-Message "Validating Java service: $service" "CHECK"
    
    # Check if directory exists
    if (-not (Test-Path $serviceDir)) {
        Log-Message "Directory not found for $service" "ERROR"
        $javaIssues++
        continue
    }
    
    # Check for pom.xml
    $pomFile = Join-Path -Path $serviceDir -ChildPath "pom.xml"
    if (-not (Test-Path $pomFile)) {
        Log-Message "pom.xml not found for $service" "ERROR"
        $javaIssues++
    }
    else {
        # Check pom.xml content for required dependencies and versions
        $pomContent = Get-Content -Path $pomFile -Raw
        
        if (-not ($pomContent -match "<java.version>17</java.version>")) {
            Log-Message "${service}: Java version should be 17" "WARNING"
            $javaIssues++
        }
        
        if (-not ($pomContent -match "<spring-boot.version>3.1.5</spring-boot.version>" -or $pomContent -match "<version>3.1.5</version>.*spring-boot")) {
            Log-Message "${service}: Spring Boot version should be 3.1.5" "WARNING"
            $javaIssues++
        }
        
        if (-not ($pomContent -match "<groupId>org.projectlombok</groupId>")) {
            Log-Message "${service}: Missing Lombok dependency" "WARNING"
            $javaIssues++
        }
        
        if (-not ($pomContent -match "<groupId>org.springframework.cloud</groupId>")) {
            Log-Message "${service}: Missing Spring Cloud dependency" "WARNING"
            $javaIssues++
        }
    }
    
    # Check for application.yml
    $appYml = Join-Path -Path $serviceDir -ChildPath "src\main\resources\application.yml"
    if (-not (Test-Path $appYml)) {
        Log-Message "${service}: application.yml not found" "ERROR"
        $javaIssues++
    }
    
    # Check for application-test.yml
    $testYml = Join-Path -Path $serviceDir -ChildPath "src\main\resources\application-test.yml"
    if (-not (Test-Path $testYml)) {
        Log-Message "${service}: application-test.yml not found" "ERROR"
        $javaIssues++
    }
    
    # Check for .gitignore
    $gitignore = Join-Path -Path $serviceDir -ChildPath ".gitignore"
    if (-not (Test-Path $gitignore)) {
        Log-Message "${service}: .gitignore not found" "ERROR"
        $javaIssues++
    }
    
    # Check for .env.template
    $envTemplate = Join-Path -Path $serviceDir -ChildPath ".env.template"
    if (-not (Test-Path $envTemplate)) {
        Log-Message "${service}: .env.template not found" "ERROR"
        $javaIssues++
    }
    
    # Check Java package structure in source files
    $javaFiles = Get-ChildItem -Path $serviceDir -Filter "*.java" -Recurse | Where-Object { $_.FullName -match "\\src\\main\\java\\" }
    $packageCheckFailed = $false
    foreach ($file in $javaFiles) {
        $content = Get-Content -Path $file.FullName -Raw
        if (-not ($content -match "package\s+com\.exalt\.courierservices\.")) {
            if (-not $packageCheckFailed) {
                Log-Message "${service}: Java packages should follow pattern com.exalt.courierservices.*" "WARNING"
                $packageCheckFailed = $true
                $javaIssues++
            }
        }
    }
}

# Validate Node.js services
foreach ($service in $nodejsServices) {
    $serviceDir = Join-Path -Path $baseDir -ChildPath $service
    Log-Message "Validating Node.js service: $service" "CHECK"
    
    # Check if directory exists
    if (-not (Test-Path $serviceDir)) {
        Log-Message "Directory not found for $service" "ERROR"
        $nodeIssues++
        continue
    }
    
    # Check for package.json
    $packageJson = Join-Path -Path $serviceDir -ChildPath "package.json"
    if (-not (Test-Path $packageJson)) {
        Log-Message "${service}: package.json not found" "ERROR"
        $nodeIssues++
    }
    else {
        # Check package.json content
        $packageContent = Get-Content -Path $packageJson -Raw | ConvertFrom-Json
        
        # Check engines
        if (-not $packageContent.engines -or -not $packageContent.engines.node) {
            Log-Message "${service}: Missing Node.js version specification in package.json" "WARNING"
            $nodeIssues++
        }
        
        # Check required scripts
        $requiredScripts = @("start", "dev", "test", "lint")
        foreach ($script in $requiredScripts) {
            if (-not $packageContent.scripts.$script) {
                Log-Message "${service}: Missing '$script' script in package.json" "WARNING"
                $nodeIssues++
            }
        }
        
        # Check required dependencies
        $requiredDeps = @("express", "eureka-js-client", "dotenv")
        foreach ($dep in $requiredDeps) {
            if (-not $packageContent.dependencies.$dep) {
                Log-Message "${service}: Missing '$dep' dependency in package.json" "WARNING"
                $nodeIssues++
            }
        }
        
        # Check required dev dependencies
        $requiredDevDeps = @("eslint", "jest")
        foreach ($dep in $requiredDevDeps) {
            if (-not $packageContent.devDependencies.$dep) {
                Log-Message "${service}: Missing '$dep' dev dependency in package.json" "WARNING"
                $nodeIssues++
            }
        }
    }
    
    # Check for .eslintrc.js
    $eslintrc = Join-Path -Path $serviceDir -ChildPath ".eslintrc.js"
    if (-not (Test-Path $eslintrc)) {
        Log-Message "${service}: .eslintrc.js not found" "ERROR"
        $nodeIssues++
    }
    
    # Check for .prettierrc
    $prettierrc = Join-Path -Path $serviceDir -ChildPath ".prettierrc"
    if (-not (Test-Path $prettierrc)) {
        Log-Message "${service}: .prettierrc not found" "ERROR"
        $nodeIssues++
    }
    
    # Check for index.js
    $indexJs = Join-Path -Path $serviceDir -ChildPath "src\index.js"
    if (-not (Test-Path $indexJs)) {
        Log-Message "${service}: src/index.js not found" "ERROR"
        $nodeIssues++
    }
    
    # Check for .gitignore
    $gitignore = Join-Path -Path $serviceDir -ChildPath ".gitignore"
    if (-not (Test-Path $gitignore)) {
        Log-Message "${service}: .gitignore not found" "ERROR"
        $nodeIssues++
    }
    
    # Check for .env.template
    $envTemplate = Join-Path -Path $serviceDir -ChildPath ".env.template"
    if (-not (Test-Path $envTemplate)) {
        Log-Message "${service}: .env.template not found" "ERROR"
        $nodeIssues++
    }
}

# Check for GitHub workflow file
$workflowDir = Join-Path -Path $baseDir -ChildPath ".github\workflows"
$cicdYml = Join-Path -Path $workflowDir -ChildPath "ci-cd.yml"
if (-not (Test-Path $cicdYml)) {
    Log-Message "GitHub Actions workflow file (ci-cd.yml) not found" "ERROR"
    $javaIssues++
}

# Report summary
Log-Message "Validation complete" "DONE"
Log-Message "Java services: $($javaServices.Count) services checked, $javaIssues issues found" "SUMMARY"
Log-Message "Node.js services: $($nodejsServices.Count) services checked, $nodeIssues issues found" "SUMMARY"

if (($javaIssues -eq 0) -and ($nodeIssues -eq 0)) {
    Log-Message "All services pass validation checks and are ready for deployment! 🎉" "SUCCESS"
}
else {
    Log-Message "Services have issues that need to be addressed before deployment" "WARNING"
}
