# API Compatibility Checker for Courier Services
# Part of the Zero Regression Strategy for Domain Transformation
# This script validates API changes for compatibility

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
            default { "Gray" }
        }
    )
}

# Set base directory
$baseDir = "C:\Users\frich\Desktop\Exalt-Application-Limited\Exalt-Application-Limited\social-ecommerce-ecosystem\courier-services"
$snapshotsDir = Join-Path -Path $baseDir -ChildPath "api-snapshots"

# Ensure snapshots directory exists
if (-not (Test-Path $snapshotsDir)) {
    New-Item -ItemType Directory -Path $snapshotsDir -Force | Out-Null
    Log-Message "Created API snapshots directory: $snapshotsDir" "INFO"
}

function Get-ApiChanges {
    param (
        [string]$ServicePath,
        [string]$ServiceType  # "java" or "node"
    )
    
    # Get service name
    $serviceName = Split-Path -Leaf $ServicePath
    Log-Message "Analyzing API changes for $serviceName ($ServiceType)" "INFO"
    
    # Store paths to API definition files
    $apiFiles = @()
    
    # Find API definition files based on service type
    if ($ServiceType -eq "java") {
        # For Java services, look for Spring @RestController classes
        $apiFiles = Get-ChildItem -Path $ServicePath -Include "*.java" -Recurse | 
            Where-Object { 
                $content = Get-Content $_ -Raw
                $content -match "@RestController" -or 
                $content -match "@Controller" -or 
                $content -match "@FeignClient"
            }
        
        # Also look for OpenAPI spec files
        $apiFiles += Get-ChildItem -Path $ServicePath -Include "*.yaml", "*.yml", "*.json" -Recurse | 
            Where-Object { 
                $content = Get-Content $_ -Raw
                $content -match "openapi:" -or 
                $content -match '"swagger":'
            }
    } 
    elseif ($ServiceType -eq "node") {
        # For Node.js services, look for route definitions
        $apiFiles = Get-ChildItem -Path $ServicePath -Include "*.js", "*.ts" -Recurse | 
            Where-Object { 
                $content = Get-Content $_ -Raw
                $content -match "router\.(get|post|put|delete|patch)" -or
                $content -match "app\.(get|post|put|delete|patch)" -or
                $content -match "express\.Router\(\)"
            }
        
        # Also look for OpenAPI spec files
        $apiFiles += Get-ChildItem -Path $ServicePath -Include "*.yaml", "*.yml", "*.json" -Recurse | 
            Where-Object { 
                $content = Get-Content $_ -Raw
                $content -match "openapi:" -or 
                $content -match '"swagger":'
            }
    }
    
    # If no API files found, return
    if ($apiFiles.Count -eq 0) {
        Log-Message "No API definition files found for $serviceName" "WARNING"
        return $false
    }
    
    Log-Message "Found $($apiFiles.Count) API definition files for $serviceName" "INFO"
    
    # Analyze each API file for changes
    $hasBreakingChanges = $false
    foreach ($file in $apiFiles) {
        $relativePath = $file.FullName.Replace($ServicePath, "").TrimStart("\")
        $snapshotFile = Join-Path -Path $snapshotsDir -ChildPath "$serviceName-$($relativePath -replace '[\\/:*?"<>|]', '_').snapshot"
        
        if (Test-Path $snapshotFile) {
            # Compare with previous snapshot
            $previousContent = Get-Content $snapshotFile -Raw
            $currentContent = Get-Content $file.FullName -Raw
            
            # Skip if no changes
            if ($previousContent -eq $currentContent) {
                Log-Message "No changes detected in $relativePath" "INFO"
                continue
            }
            
            # Perform compatibility analysis
            $breakingChanges = Test-ApiCompatibility -PreviousContent $previousContent -CurrentContent $currentContent -FileType $file.Extension
            
            if ($breakingChanges.Count -gt 0) {
                Log-Message "⚠️ Potential breaking changes detected in $relativePath:" "WARNING"
                foreach ($change in $breakingChanges) {
                    Log-Message "  - $change" "WARNING"
                }
                $hasBreakingChanges = $true
            } else {
                Log-Message "✅ Non-breaking changes detected in $relativePath" "SUCCESS"
                # Update snapshot with current content
                Set-Content -Path $snapshotFile -Value $currentContent -NoNewline
            }
        } else {
            # Create new snapshot file
            Log-Message "Creating initial API snapshot for $relativePath" "INFO"
            Set-Content -Path $snapshotFile -Value (Get-Content $file.FullName -Raw) -NoNewline
        }
    }
    
    return $hasBreakingChanges
}

function Test-ApiCompatibility {
    param (
        [string]$PreviousContent,
        [string]$CurrentContent,
        [string]$FileType
    )
    
    $breakingChanges = @()
    
    # Simple, generic breaking change detection
    # In a real implementation, this would be much more sophisticated using parsers specific to the file type
    
    # Check for removed lines (potentially removed endpoints or parameters)
    $previousLines = $PreviousContent -split "`n"
    $currentLines = $CurrentContent -split "`n"
    
    # Look for removed method annotations or route definitions
    foreach ($line in $previousLines) {
        $trimmedLine = $line.Trim()
        
        # Skip comments and empty lines
        if ($trimmedLine -match "^(//|#|/\*|\*|$)") {
            continue
        }
        
        # Check for removed lines that might indicate API changes
        if ($FileType -match "\.(java|kt)$") {
            # Java/Kotlin specific checks
            if ($trimmedLine -match "@(GetMapping|PostMapping|PutMapping|DeleteMapping|RequestMapping|PatchMapping)" -and
                -not ($CurrentContent -match [regex]::Escape($trimmedLine))) {
                $breakingChanges += "Removed API endpoint: $trimmedLine"
            }
            elseif ($trimmedLine -match "@PathVariable|@RequestParam|@RequestBody" -and
                   -not ($CurrentContent -match [regex]::Escape($trimmedLine))) {
                $breakingChanges += "Removed API parameter: $trimmedLine"
            }
        }
        elseif ($FileType -match "\.(js|ts)$") {
            # JavaScript/TypeScript specific checks
            if ($trimmedLine -match "\.(get|post|put|delete|patch)\('([^']+)'," -and
                -not ($CurrentContent -match [regex]::Escape($trimmedLine))) {
                $breakingChanges += "Removed API endpoint: $trimmedLine"
            }
        }
        elseif ($FileType -match "\.(yaml|yml|json)$") {
            # OpenAPI spec specific checks
            if ($trimmedLine -match "^\s*/([\w-]+):" -and
                -not ($CurrentContent -match [regex]::Escape($trimmedLine))) {
                $breakingChanges += "Removed API path: $trimmedLine"
            }
            elseif ($trimmedLine -match "required:\s*true" -and
                   -not ($CurrentContent -match [regex]::Escape($trimmedLine))) {
                $breakingChanges += "Changed required parameter: $trimmedLine"
            }
        }
    }
    
    return $breakingChanges
}

function Update-GitHookWithApiCheck {
    param (
        [string]$ServicePath,
        [string]$ServiceType  # "java" or "node"
    )
    
    if (-not (Test-Path $ServicePath)) {
        Log-Message "Service path not found: $ServicePath" "WARNING"
        return
    }
    
    # Get service name
    $serviceName = Split-Path -Leaf $ServicePath
    Log-Message "Updating git hooks for $serviceName to include API compatibility checks" "INFO"
    
    # Locate the hooks directory
    $hooksDir = Join-Path -Path $ServicePath -ChildPath ".git\hooks"
    if (-not (Test-Path $hooksDir)) {
        # Check if we're in a git submodule
        $gitDir = Join-Path -Path $ServicePath -ChildPath ".git"
        if ((Test-Path $gitDir) -and (Get-Item $gitDir).Length -lt 100) {
            # This might be a git submodule, need to parse the gitdir reference
            $gitDirContent = Get-Content $gitDir -Raw
            if ($gitDirContent -match "gitdir: (.+)") {
                $actualGitDir = $matches[1]
                if (-not [System.IO.Path]::IsPathRooted($actualGitDir)) {
                    $actualGitDir = Join-Path -Path $ServicePath -ChildPath $actualGitDir
                }
                $hooksDir = Join-Path -Path $actualGitDir -ChildPath "hooks"
            }
        }
        
        # If still not found, exit
        if (-not (Test-Path $hooksDir)) {
            Log-Message "Git hooks directory not found for $serviceName" "ERROR"
            return
        }
    }
    
    # Update pre-push hook to include API compatibility check
    $prePushPath = Join-Path -Path $hooksDir -ChildPath "pre-push"
    if (Test-Path $prePushPath) {
        $prePushContent = Get-Content $prePushPath -Raw
        
        # Check if the API compatibility check is already included
        if ($prePushContent -match "API compatibility check") {
            Log-Message "API compatibility check is already included in pre-push hook for $serviceName" "INFO"
            return
        }
        
        # Add the API compatibility check
        $apiCheckScript = @"

# API compatibility check
echo "Running API compatibility check..."
powershell.exe -ExecutionPolicy Bypass -File '$baseDir\api-compatibility-checker.ps1' -ServicePath '$(Resolve-Path $ServicePath)' -ServiceType '$ServiceType'
if [ \$? -ne 0 ]; then
    echo "Error: API compatibility check failed. Review breaking changes before pushing."
    exit 1
fi

"@
        
        # Find a good position to insert the API check (before the exit 0 at the end)
        $modifiedContent = $prePushContent -replace "echo `"All pre-push checks passed!`"\n+exit 0", "${apiCheckScript}`necho `"All pre-push checks passed!`"`nexit 0"
        
        # Write back the updated content
        Set-Content -Path $prePushPath -Value $modifiedContent -NoNewline
        
        Log-Message "Updated pre-push hook for $serviceName with API compatibility check" "SUCCESS"
    } else {
        Log-Message "Pre-push hook not found for $serviceName" "WARNING"
    }
}

# Function to check a specific service
function Check-ServiceApiCompatibility {
    param (
        [Parameter(Mandatory=$true)]
        [string]$ServicePath,
        
        [Parameter(Mandatory=$true)]
        [ValidateSet("java", "node")]
        [string]$ServiceType
    )
    
    $hasBreakingChanges = Get-ApiChanges -ServicePath $ServicePath -ServiceType $ServiceType
    
    if ($hasBreakingChanges) {
        Log-Message "⚠️ Breaking API changes detected in $(Split-Path -Leaf $ServicePath)" "WARNING"
        Log-Message "Review the changes carefully and update clients accordingly" "WARNING"
        return $false
    } else {
        Log-Message "✅ No breaking API changes detected in $(Split-Path -Leaf $ServicePath)" "SUCCESS"
        return $true
    }
}

# Main execution block when script is run directly
if ($MyInvocation.InvocationName -ne ".") {
    # Parse command line parameters
    param (
        [string]$ServicePath,
        [string]$ServiceType
    )
    
    if ($ServicePath -and $ServiceType) {
        # Run check for specific service
        $result = Check-ServiceApiCompatibility -ServicePath $ServicePath -ServiceType $ServiceType
        if (-not $result) {
            exit 1
        }
    } else {
        # Find all services in the courier-services directory and update their git hooks
        $services = Get-ChildItem -Path $baseDir -Directory | Where-Object {
            $dir = $_.FullName
            (Test-Path (Join-Path -Path $dir -ChildPath "pom.xml")) -or
            (Test-Path (Join-Path -Path $dir -ChildPath "package.json"))
        }
        
        foreach ($service in $services) {
            $servicePath = $service.FullName
            
            # Determine service type
            if (Test-Path (Join-Path -Path $servicePath -ChildPath "pom.xml")) {
                Update-GitHookWithApiCheck -ServicePath $servicePath -ServiceType "java"
            } elseif (Test-Path (Join-Path -Path $servicePath -ChildPath "package.json")) {
                Update-GitHookWithApiCheck -ServicePath $servicePath -ServiceType "node"
            }
        }
        
        Log-Message "API compatibility checks have been added to all services' git hooks" "SUCCESS"
    }
}
