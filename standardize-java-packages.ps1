# Script to standardize Java package structures
# Updates package declarations from various patterns to com.exalt.courierservices.*

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

Log-Message "Starting Java package standardization" "START"

# List of Java services to standardize
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
    $srcDir = Join-Path -Path $serviceDir -ChildPath "src"
    
    if (Test-Path $srcDir) {
        Log-Message "Standardizing Java packages for $service" "PROCESS"
        
        # Extract service name without -service suffix for package naming
        $serviceName = $service.Replace("-service", "").Replace("courier-", "")
        
        # Convert paths like courier-management to management
        $serviceName = $serviceName.Replace("courier-", "")
        
        # Find all Java files
        $javaFiles = Get-ChildItem -Path $srcDir -Filter "*.java" -Recurse
        
        foreach ($file in $javaFiles) {
            $content = Get-Content -Path $file.FullName -Raw
            
            # Patterns to replace for different existing package structures
            $patterns = @(
                "package com\.exalt\.courier\.(.*);",
                "package com\.microecosystem\.courier\.(.*);",
                "package com\.microsocial\.courier\.(.*);",
                "package org\.courier\.(.*);",
                "package io\.courier\.(.*);",
                "package com\.exalteco\.courier\.(.*);",
                "package com\.exalt\.social\.courier\.(.*);",
                "package com\.exalt\.commerce\.courier\.(.*);",
                "package com\.social\.ecommerce\.courier\.(.*);",
                "package com\.exalt\..*"
            )
            
            $newContent = $content
            
            foreach ($pattern in $patterns) {
                # Replace imports and packages with new structure
                if ($newContent -match $pattern) {
                    Log-Message "Updating package structure in $($file.FullName)" "UPDATE"
                    
                    # Update package declarations
                    $newContent = $newContent -replace $pattern, "package com.exalt.courierservices.$serviceName.`$1;"
                    
                    # Update imports for the same packages
                    foreach ($p in $patterns) {
                        $importPattern = "import $p"
                        $newContent = $newContent -replace $importPattern, "import com.exalt.courierservices.$serviceName.`$1;"
                    }
                }
            }
            
            # Only write to file if changes were made
            if ($newContent -ne $content) {
                Set-Content -Path $file.FullName -Value $newContent -NoNewline
                Log-Message "Updated package in $($file.Name)" "UPDATED"
            }
        }
    } else {
        Log-Message "Source directory not found for $service" "WARNING"
    }
}

Log-Message "Java package standardization completed" "FINISH"
