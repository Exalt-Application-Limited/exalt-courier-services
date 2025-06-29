# Script to validate that code changes don't introduce regression
# Performs API compatibility checks and snapshot testing

function Log-Message {
    param (
        [string]$Message,
        [string]$Type = "INFO"
    )
    $timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    Write-Host "[$timestamp] [$Type] $Message" -ForegroundColor $(
        switch ($Type) {
            "INFO" { "White" }
            "SUCCESS" { "Green" }
            "WARNING" { "Yellow" }
            "ERROR" { "Red" }
            "COMPARE" { "Cyan" }
            default { "Gray" }
        }
    )
}

$baseDir = "C:\Users\frich\Desktop\Exalt-Application-Limited\Exalt-Application-Limited\social-ecommerce-ecosystem\courier-services"
Set-Location $baseDir

# Java services list
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

# Node.js services list
$nodeServices = @(
    "courier-network-locations",
    "courier-events-service",
    "courier-geo-routing",
    "courier-fare-calculator",
    "courier-pickup-engine",
    "courier-location-tracker"
)

# Function to generate API snapshot for a Java service
function Generate-JavaApiSnapshot {
    param (
        [string]$ServicePath,
        [string]$SnapshotDir
    )
    
    # Find all controller classes
    $controllerFiles = Get-ChildItem -Path "$ServicePath\src\main\java" -Filter "*Controller.java" -Recurse
    
    # Create API snapshot directory
    $serviceSnapshotDir = Join-Path -Path $SnapshotDir -ChildPath (Split-Path -Leaf $ServicePath)
    New-Item -ItemType Directory -Path $serviceSnapshotDir -Force | Out-Null
    
    foreach ($controllerFile in $controllerFiles) {
        $controllerContent = Get-Content -Path $controllerFile.FullName -Raw
        $endpointInfo = @()
        
        # Extract request mappings
        if ($controllerContent -match '@RequestMapping\s*\(\s*(?:value\s*=\s*)?[''"]([^''"]+)[''"]') {
            $baseMapping = $matches[1]
        } else {
            $baseMapping = ""
        }
        
        # Find all endpoint definitions
        $endpoints = [regex]::Matches($controllerContent, '@(Get|Post|Put|Delete|Patch)Mapping\s*\(\s*(?:value\s*=\s*)?[''"]?([^)''"]+)[''"]?\s*\)')
        foreach ($endpoint in $endpoints) {
            $httpMethod = $endpoint.Groups[1].Value
            $path = $endpoint.Groups[2].Value
            
            # Combine base mapping with endpoint path
            $fullPath = $baseMapping + $path
            
            # Find the method that corresponds to this endpoint
            $methodMatch = [regex]::Match($controllerContent.Substring($endpoint.Index), 'public\s+([^\s\(]+)\s+([^\s\(]+)\s*\(([^\)]*)\)')
            if ($methodMatch.Success) {
                $returnType = $methodMatch.Groups[1].Value
                $methodName = $methodMatch.Groups[2].Value
                $parameters = $methodMatch.Groups[3].Value
                
                $endpointInfo += [PSCustomObject]@{
                    Method = $httpMethod
                    Path = $fullPath
                    ReturnType = $returnType
                    FunctionName = $methodName
                    Parameters = $parameters.Trim()
                }
            }
        }
        
        # Save endpoint info to snapshot file
        $className = $controllerFile.BaseName
        $snapshotPath = Join-Path -Path $serviceSnapshotDir -ChildPath "$className-api.json"
        $endpointInfo | ConvertTo-Json -Depth 10 | Set-Content -Path $snapshotPath -NoNewline
    }
}

# Function to generate API snapshot for a Node.js service
function Generate-NodeApiSnapshot {
    param (
        [string]$ServicePath,
        [string]$SnapshotDir
    )
    
    # Find all route files
    $routeFiles = Get-ChildItem -Path "$ServicePath\src\routes" -Filter "*.js" -Recurse -ErrorAction SilentlyContinue
    
    # If no routes found in src/routes, check for different directory structures
    if (-not $routeFiles) {
        $routeFiles = Get-ChildItem -Path "$ServicePath\routes" -Filter "*.js" -Recurse -ErrorAction SilentlyContinue
    }
    
    # If still no routes, check for routes in app.js or server.js
    if (-not $routeFiles) {
        $routeFiles = Get-ChildItem -Path $ServicePath -Filter "app.js" -Recurse -ErrorAction SilentlyContinue
        if (-not $routeFiles) {
            $routeFiles = Get-ChildItem -Path $ServicePath -Filter "server.js" -Recurse -ErrorAction SilentlyContinue
        }
    }
    
    # Create API snapshot directory
    $serviceSnapshotDir = Join-Path -Path $SnapshotDir -ChildPath (Split-Path -Leaf $ServicePath)
    New-Item -ItemType Directory -Path $serviceSnapshotDir -Force | Out-Null
    
    foreach ($routeFile in $routeFiles) {
        $routeContent = Get-Content -Path $routeFile.FullName -Raw
        $endpointInfo = @()
        
        # Find router base path (may be in exports or direct routes)
        if ($routeContent -match 'router\.use\s*\([''"]([^''"]+)[''"]') {
            $basePath = $matches[1]
        } elseif ($routeContent -match 'app\.use\s*\([''"]([^''"]+)[''"]') {
            $basePath = $matches[1]
        } else {
            $basePath = ""
        }
        
        # Find all endpoint definitions
        $endpoints = [regex]::Matches($routeContent, '(router|app)\.(get|post|put|delete|patch)\s*\([''"]([^''"]+)[''"]')
        
        foreach ($endpoint in $endpoints) {
            $routerType = $endpoint.Groups[1].Value
            $httpMethod = $endpoint.Groups[2].Value.ToUpper()
            $path = $endpoint.Groups[3].Value
            
            # Combine base path with endpoint path if needed
            $fullPath = if ($routerType -eq "router" -and $basePath) { $basePath + $path } else { $path }
            
            # Extract function or middleware info (simplified)
            $functionBlock = $routeContent.Substring($endpoint.Index + $endpoint.Length)
            if ($functionBlock -match 'function\s*\(([^\)]*)\)') {
                $parameters = $matches[1].Trim()
                
                $endpointInfo += [PSCustomObject]@{
                    Method = $httpMethod
                    Path = $fullPath
                    Parameters = $parameters
                    File = $routeFile.Name
                }
            }
        }
        
        # Save endpoint info to snapshot file
        $fileName = $routeFile.BaseName
        $snapshotPath = Join-Path -Path $serviceSnapshotDir -ChildPath "$fileName-api.json"
        $endpointInfo | ConvertTo-Json -Depth 10 | Set-Content -Path $snapshotPath -NoNewline
    }
}

# Function to compare API snapshots and detect breaking changes
function Compare-ApiSnapshots {
    param (
        [string]$OldSnapshotPath,
        [string]$NewSnapshotPath,
        [string]$ServiceName
    )
    
    if (-not (Test-Path $OldSnapshotPath)) {
        Log-Message "No previous snapshot found for $ServiceName, creating baseline" "INFO"
        return $true
    }
    
    $oldFiles = Get-ChildItem -Path $OldSnapshotPath -Filter "*-api.json"
    $newFiles = Get-ChildItem -Path $NewSnapshotPath -Filter "*-api.json"
    
    # Check for removed API files
    $oldFileNames = $oldFiles | ForEach-Object { $_.BaseName }
    $newFileNames = $newFiles | ForEach-Object { $_.BaseName }
    
    $removedFiles = $oldFileNames | Where-Object { $_ -notin $newFileNames }
    if ($removedFiles) {
        Log-Message "BREAKING CHANGE: The following API controllers/files were removed in $ServiceName:" "ERROR"
        foreach ($file in $removedFiles) {
            Log-Message "- $file" "ERROR"
        }
        return $false
    }
    
    $hasBreakingChanges = $false
    
    # Compare existing files
    foreach ($oldFile in $oldFiles) {
        $fileName = $oldFile.BaseName
        $newFile = $newFiles | Where-Object { $_.BaseName -eq $fileName } | Select-Object -First 1
        
        if (-not $newFile) {
            # Already handled in removed files check
            continue
        }
        
        $oldEndpoints = Get-Content -Path $oldFile.FullName | ConvertFrom-Json
        $newEndpoints = Get-Content -Path $newFile.FullName | ConvertFrom-Json
        
        # Handle single endpoint case
        if ($oldEndpoints -isnot [Array]) {
            $oldEndpoints = @($oldEndpoints)
        }
        if ($newEndpoints -isnot [Array]) {
            $newEndpoints = @($newEndpoints)
        }
        
        # Check for removed endpoints
        foreach ($oldEndpoint in $oldEndpoints) {
            $matchingNewEndpoint = $newEndpoints | Where-Object {
                $_.Method -eq $oldEndpoint.Method -and $_.Path -eq $oldEndpoint.Path
            } | Select-Object -First 1
            
            if (-not $matchingNewEndpoint) {
                Log-Message "BREAKING CHANGE: Endpoint removed in $ServiceName - $($oldEndpoint.Method) $($oldEndpoint.Path)" "ERROR"
                $hasBreakingChanges = $true
                continue
            }
            
            # For Java services with return types and parameters
            if ($oldEndpoint.PSObject.Properties.Name -contains "ReturnType") {
                # Check for return type changes
                if ($matchingNewEndpoint.ReturnType -ne $oldEndpoint.ReturnType) {
                    Log-Message "BREAKING CHANGE: Return type changed in $ServiceName - $($oldEndpoint.Method) $($oldEndpoint.Path)" "ERROR"
                    Log-Message "  Old: $($oldEndpoint.ReturnType)" "COMPARE"
                    Log-Message "  New: $($matchingNewEndpoint.ReturnType)" "COMPARE"
                    $hasBreakingChanges = $true
                }
                
                # Check for parameter changes (simplified - just checking if any were removed)
                $oldParams = $oldEndpoint.Parameters -split ','
                $newParams = $matchingNewEndpoint.Parameters -split ','
                
                foreach ($oldParam in $oldParams) {
                    if ($oldParam -and -not ($newParams -like "*$oldParam*")) {
                        Log-Message "BREAKING CHANGE: Parameter removed or changed in $ServiceName - $($oldEndpoint.Method) $($oldEndpoint.Path)" "ERROR"
                        Log-Message "  Missing parameter: $oldParam" "COMPARE"
                        $hasBreakingChanges = $true
                    }
                }
            }
        }
    }
    
    if (-not $hasBreakingChanges) {
        Log-Message "No API breaking changes detected in $ServiceName" "SUCCESS"
    }
    
    return (-not $hasBreakingChanges)
}

# Create snapshot directories
$currentDir = Join-Path -Path $baseDir -ChildPath "api-snapshots\current"
$previousDir = Join-Path -Path $baseDir -ChildPath "api-snapshots\previous"

# Check if current snapshots exist and move them to previous
if (Test-Path $currentDir) {
    # If previous exists, remove it
    if (Test-Path $previousDir) {
        Remove-Item -Path $previousDir -Recurse -Force
    }
    # Move current to previous
    New-Item -ItemType Directory -Path $previousDir -Force | Out-Null
    Get-ChildItem -Path $currentDir -Directory | ForEach-Object {
        $serviceName = $_.Name
        $serviceDir = Join-Path -Path $previousDir -ChildPath $serviceName
        Copy-Item -Path $_.FullName -Destination $serviceDir -Recurse -Force
    }
}

# Create current snapshots directory
if (-not (Test-Path $currentDir)) {
    New-Item -ItemType Directory -Path $currentDir -Force | Out-Null
}

# Generate snapshots and check for breaking changes
$regressionFound = $false

# Process Java services
Log-Message "Processing Java services..." "INFO"
foreach ($service in $javaServices) {
    $servicePath = Join-Path -Path $baseDir -ChildPath $service
    
    if (Test-Path $servicePath) {
        Log-Message "Generating API snapshot for $service" "INFO"
        Generate-JavaApiSnapshot -ServicePath $servicePath -SnapshotDir $currentDir
        
        # Compare with previous snapshot
        $oldSnapshotPath = Join-Path -Path $previousDir -ChildPath $service
        $newSnapshotPath = Join-Path -Path $currentDir -ChildPath $service
        
        if (-not (Compare-ApiSnapshots -OldSnapshotPath $oldSnapshotPath -NewSnapshotPath $newSnapshotPath -ServiceName $service)) {
            $regressionFound = $true
        }
    }
    else {
        Log-Message "Service directory not found: $service" "WARNING"
    }
}

# Process Node.js services
Log-Message "Processing Node.js services..." "INFO"
foreach ($service in $nodeServices) {
    $servicePath = Join-Path -Path $baseDir -ChildPath $service
    
    if (Test-Path $servicePath) {
        Log-Message "Generating API snapshot for $service" "INFO"
        Generate-NodeApiSnapshot -ServicePath $servicePath -SnapshotDir $currentDir
        
        # Compare with previous snapshot
        $oldSnapshotPath = Join-Path -Path $previousDir -ChildPath $service
        $newSnapshotPath = Join-Path -Path $currentDir -ChildPath $service
        
        if (-not (Compare-ApiSnapshots -OldSnapshotPath $oldSnapshotPath -NewSnapshotPath $newSnapshotPath -ServiceName $service)) {
            $regressionFound = $true
        }
    }
    else {
        Log-Message "Service directory not found: $service" "WARNING"
    }
}

# Final report
if ($regressionFound) {
    Log-Message "⚠️ API REGRESSION DETECTED - Review the changes to fix breaking changes" "ERROR"
    exit 1
} else {
    Log-Message "✅ No API regression detected - All changes are backward compatible" "SUCCESS"
    exit 0
}
