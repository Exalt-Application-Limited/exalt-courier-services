# Migration Script for Courier Services
# This script moves code from old service directories to new standardized ones

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

Log-Message "Starting code migration process" "START"

# Service migration mapping (old -> new)
$migrationMap = @{
    "courier-management" = "courier-management-service"
    "courier-subscription" = "courier-subscription-service"
    "international-shipping" = "international-shipping-service"
    "tracking-service" = "courier-tracking-service"
    # Add more migrations as needed
}

foreach ($sourceService in $migrationMap.Keys) {
    $targetService = $migrationMap[$sourceService]
    $sourceDir = Join-Path -Path $baseDir -ChildPath $sourceService
    $targetDir = Join-Path -Path $baseDir -ChildPath $targetService
    
    if ((Test-Path $sourceDir) -and (Test-Path $targetDir)) {
        Log-Message "Migrating code from $sourceService to $targetService" "MIGRATE"
        
        # Copy essential files and directories
        $itemsToCopy = @("src", "pom.xml", "README.md", "api-docs", "database", "docker-compose.yml", "docs", "k8s", "scripts", "Dockerfile")
        
        foreach ($item in $itemsToCopy) {
            $sourcePath = Join-Path -Path $sourceDir -ChildPath $item
            $targetPath = Join-Path -Path $targetDir -ChildPath $item
            
            if (Test-Path $sourcePath) {
                Log-Message "Copying $item to new location" "COPY"
                
                if (Test-Path -PathType Container $sourcePath) {
                    # If it's a directory, copy recursively
                    Copy-Item -Path $sourcePath -Destination $targetDir -Recurse -Force
                } else {
                    # If it's a file, just copy the file
                    Copy-Item -Path $sourcePath -Destination $targetPath -Force
                }
            }
        }
        
        Log-Message "Migration completed for $sourceService to $targetService" "DONE"
    } else {
        if (-not (Test-Path $sourceDir)) {
            Log-Message "Source directory $sourceDir does not exist!" "ERROR"
        }
        if (-not (Test-Path $targetDir)) {
            Log-Message "Target directory $targetDir does not exist!" "ERROR"
        }
    }
}

Log-Message "Code migration process completed" "FINISH"
