# Script to fix remaining validation issues
# Addresses missing application-test.yml files and Spring Boot version issues

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

Log-Message "Starting validation fixes" "START"

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

# Standard application-test.yml template
$applicationTestYmlTemplate = @"
spring:
  application:
    name: SERVICE_NAME_PLACEHOLDER-test
  datasource:
    url: jdbc:h2:mem:testdb
    username: sa
    password: password
    driver-class-name: org.h2.Driver
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create-drop
    properties:
      hibernate:
        format_sql: true
        show_sql: true
  h2:
    console:
      enabled: true

# Disable Eureka client in tests
eureka:
  client:
    enabled: false
    register-with-eureka: false
    fetch-registry: false

# Disable Config Server in tests
spring.cloud.config:
  enabled: false
  discovery:
    enabled: false

# Server port for testing
server:
  port: 0
"@

foreach ($service in $javaServices) {
    $serviceDir = Join-Path -Path $baseDir -ChildPath $service
    
    if (Test-Path $serviceDir) {
        Log-Message "Fixing issues for $service" "PROCESS"
        
        # Fix application-test.yml
        $resourceDir = Join-Path -Path $serviceDir -ChildPath "src\main\resources"
        $testYmlPath = Join-Path -Path $resourceDir -ChildPath "application-test.yml"
        
        if (-not (Test-Path $resourceDir)) {
            New-Item -ItemType Directory -Path $resourceDir -Force | Out-Null
            Log-Message "Created resources directory for $service" "CREATE"
        }
        
        if (-not (Test-Path $testYmlPath)) {
            # Extract the service short name for template customization
            $serviceShortName = $service -replace '-service', ''
            $customizedTestYml = $applicationTestYmlTemplate -replace 'SERVICE_NAME_PLACEHOLDER', $serviceShortName
            
            # Write the application-test.yml file
            Set-Content -Path $testYmlPath -Value $customizedTestYml -NoNewline
            Log-Message "Created application-test.yml for $service" "FIXED"
        }
        
        # Fix Spring Boot version in pom.xml
        $pomPath = Join-Path -Path $serviceDir -ChildPath "pom.xml"
        if (Test-Path $pomPath) {
            $pomContent = Get-Content -Path $pomPath -Raw
            
            if (-not ($pomContent -match "<spring-boot\.version>3\.1\.5</spring-boot\.version>")) {
                # Try to find and replace Spring Boot version
                if ($pomContent -match "<spring-boot\.version>[^<]+</spring-boot\.version>") {
                    $pomContent = $pomContent -replace "<spring-boot\.version>[^<]+</spring-boot\.version>", "<spring-boot.version>3.1.5</spring-boot.version>"
                    Set-Content -Path $pomPath -Value $pomContent -NoNewline
                    Log-Message "Fixed Spring Boot version in pom.xml for $service" "FIXED"
                }
                elseif ($pomContent -match "<version>[^<]+</version>.*spring-boot") {
                    $pomContent = $pomContent -replace "(<version>)[^<]+(</version>.*spring-boot)", "`${1}3.1.5`${2}"
                    Set-Content -Path $pomPath -Value $pomContent -NoNewline
                    Log-Message "Fixed Spring Boot version in pom.xml for $service" "FIXED"
                }
                # If parent spring-boot-starter-parent version needs to be updated
                elseif ($pomContent -match "<parent>[\s\S]*?<artifactId>spring-boot-starter-parent</artifactId>[\s\S]*?<version>[^<]+</version>") {
                    $pomContent = $pomContent -replace "(<parent>[\s\S]*?<artifactId>spring-boot-starter-parent</artifactId>[\s\S]*?<version>)[^<]+(</version>)", "`${1}3.1.5`${2}"
                    Set-Content -Path $pomPath -Value $pomContent -NoNewline
                    Log-Message "Fixed Spring Boot parent version in pom.xml for $service" "FIXED"
                }
            }
        }
        
        # If this is courier-tracking-service, fix the Java package structure
        if ($service -eq "courier-tracking-service") {
            $javaFiles = Get-ChildItem -Path $serviceDir -Filter "*.java" -Recurse | Where-Object { $_.FullName -match "\\src\\main\\java\\" }
            
            foreach ($file in $javaFiles) {
                $content = Get-Content -Path $file.FullName -Raw
                
                if (-not ($content -match "package\s+com\.exalt\.courierservices\.")) {
                    # Extract original package name
                    if ($content -match "package\s+([\w\.]+);") {
                        $originalPackage = $matches[1]
                        $newPackage = $originalPackage -replace "com\.exalt\.courier", "com.exalt.courierservices"
                        $newPackage = $newPackage -replace "com\.microecosystem\.courier", "com.exalt.courierservices"
                        $newPackage = $newPackage -replace "com\.socialecommerceecosystem", "com.exalt.courierservices"
                        
                        # Replace package declaration
                        $content = $content -replace "package\s+$originalPackage;", "package $newPackage;"
                        
                        # Replace import statements that reference the old package
                        $content = $content -replace "import\s+$originalPackage", "import $newPackage"
                        
                        # Write updated content
                        Set-Content -Path $file.FullName -Value $content -NoNewline
                        Log-Message "Fixed package declaration in $($file.Name)" "FIXED"
                    }
                }
            }
        }
    }
}

# Create .github/workflows directory if it doesn't exist
$workflowDir = Join-Path -Path $baseDir -ChildPath ".github\workflows"
if (-not (Test-Path $workflowDir)) {
    New-Item -ItemType Directory -Path $workflowDir -Force | Out-Null
    Log-Message "Created GitHub workflows directory" "CREATE"
}

Log-Message "Validation fixes completed" "DONE"
