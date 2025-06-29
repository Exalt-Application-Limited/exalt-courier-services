# Script to specifically fix Spring Boot version issues in pom.xml files

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

Log-Message "Starting Spring Boot version fix" "START"

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

foreach ($service in $javaServices) {
    $serviceDir = Join-Path -Path $baseDir -ChildPath $service
    $pomPath = Join-Path -Path $serviceDir -ChildPath "pom.xml"
    
    if (Test-Path $pomPath) {
        Log-Message "Fixing Spring Boot version for ${service}" "PROCESS"
        
        # Read the entire pom.xml content
        $pomContent = Get-Content -Path $pomPath -Raw
        
        # Create backup
        Copy-Item -Path $pomPath -Destination "${pomPath}.bak" -Force
        
        # Insert the Spring Boot version property if it doesn't exist
        if (-not ($pomContent -match "<spring-boot\.version>3\.1\.5</spring-boot\.version>")) {
            # Find the properties section
            if ($pomContent -match "<properties>[\s\S]*?</properties>") {
                $propertiesSection = $matches[0]
                $updatedProperties = $propertiesSection -replace "</properties>", "    <spring-boot.version>3.1.5</spring-boot.version>`n    </properties>"
                $pomContent = $pomContent -replace [regex]::Escape($propertiesSection), $updatedProperties
                Log-Message "Added Spring Boot version property to pom.xml for ${service}" "FIXED"
            }
            # No properties section exists, create one
            elseif ($pomContent -match "<artifactId>[^<]+</artifactId>[\s\S]*?<version>[^<]+</version>") {
                $afterVersionTag = $pomContent -replace "(<artifactId>[^<]+</artifactId>[\s\S]*?<version>[^<]+</version>)", "`$1`n    <properties>`n        <java.version>17</java.version>`n        <spring-boot.version>3.1.5</spring-boot.version>`n    </properties>"
                $pomContent = $afterVersionTag
                Log-Message "Created properties section with Spring Boot version in pom.xml for ${service}" "FIXED"
            }
        }
        
        # Update the Spring Boot parent version
        if ($pomContent -match "<parent>[\s\S]*?<artifactId>spring-boot-starter-parent</artifactId>[\s\S]*?<version>[^<]+</version>") {
            $pomContent = $pomContent -replace "(<parent>[\s\S]*?<artifactId>spring-boot-starter-parent</artifactId>[\s\S]*?<version>)[^<]+(</version>)", "`$13.1.5`$2"
            Log-Message "Updated Spring Boot parent version in pom.xml for ${service}" "FIXED"
        }
        
        # Save the changes
        Set-Content -Path $pomPath -Value $pomContent -NoNewline
    }
}

Log-Message "Spring Boot version fix completed" "DONE"
