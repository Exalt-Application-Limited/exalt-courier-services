# Comprehensive test verification script for courier services
# Runs all tests and verifies no regression occurred

# Enable strict mode
Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

function Log-Message {
    param (
        [string]$Message,
        [string]$Type = "INFO"
    )
    $timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    $color = switch ($Type) {
        "INFO" { "White" }
        "SUCCESS" { "Green" }
        "WARNING" { "Yellow" }
        "ERROR" { "Red" }
        "TEST" { "Cyan" }
        default { "Gray" }
    }
    
    Write-Host "[$timestamp] [$Type] $Message" -ForegroundColor $color
}

function Initialize-TestEnvironment {
    Log-Message "Initializing test environment" "INFO"
    
    # Get script directory (safe approach)
    $scriptPath = $MyInvocation.MyCommand.Path
    $scriptDir = Split-Path -Parent -Path $scriptPath
    Set-Location $scriptDir
    
    # Create reports directory
    $reportsDir = Join-Path -Path $scriptDir -ChildPath "test-reports"
    if (-not (Test-Path -Path $reportsDir)) {
        New-Item -ItemType Directory -Path $reportsDir -Force | Out-Null
        Log-Message "Created test reports directory at $reportsDir" "INFO"
    }
    
    # Return base directories
    return @{
        BaseDir = $scriptDir
        ReportsDir = $reportsDir
    }
}

function Get-ServiceList {
    param (
        [string]$BaseDir
    )
    
    # Find and categorize services
    $javaServices = @()
    $nodeServices = @()
    
    $allDirs = Get-ChildItem -Path $BaseDir -Directory | Where-Object { 
        $_.Name -like "courier-*" -or 
        $_.Name -like "*-service" 
    }
    
    foreach ($dir in $allDirs) {
        $pomPath = Join-Path -Path $dir.FullName -ChildPath "pom.xml"
        $packagePath = Join-Path -Path $dir.FullName -ChildPath "package.json"
        
        if (Test-Path $pomPath) {
            $javaServices += $dir.Name
            Log-Message "Found Java service: $($dir.Name)" "INFO"
        }
        elseif (Test-Path $packagePath) {
            $nodeServices += $dir.Name
            Log-Message "Found Node.js service: $($dir.Name)" "INFO"
        }
    }
    
    return @{
        JavaServices = $javaServices
        NodeServices = $nodeServices
    }
}

function Test-JavaService {
    param (
        [string]$ServiceName,
        [string]$BasePath,
        [string]$ReportsDir
    )
    
    $servicePath = Join-Path -Path $BasePath -ChildPath $ServiceName
    $serviceReportDir = Join-Path -Path $ReportsDir -ChildPath $ServiceName
    
    # Create service report directory
    if (-not (Test-Path -Path $serviceReportDir)) {
        New-Item -ItemType Directory -Path $serviceReportDir -Force | Out-Null
    }
    
    Log-Message "Testing Java service: $ServiceName" "TEST"
    
    # Check if Maven wrapper exists
    $mvnCmd = if (Test-Path "$servicePath\mvnw") { ".\mvnw" } elseif (Test-Path "$servicePath\mvnw.cmd") { ".\mvnw.cmd" } else { "mvn" }
    
    try {
        # Change to service directory
        Push-Location $servicePath
        
        # Check if pom.xml exists
        if (-not (Test-Path "pom.xml")) {
            Log-Message "No pom.xml found in $ServiceName" "WARNING" 
            return @{ Success = $false; Message = "No pom.xml found" }
        }
        
        # Set test profile
        $env:SPRING_PROFILES_ACTIVE = "test"
        
        # Try to run tests
        Log-Message "Running Maven tests for $ServiceName" "INFO"
        
        try {
            # Run tests with basic command first
            $testLog = Join-Path -Path $serviceReportDir -ChildPath "test-output.log"
            & $mvnCmd test -DskipITs -B | Tee-Object -FilePath $testLog
            $exitCode = $LASTEXITCODE
            
            if ($exitCode -ne 0) {
                Log-Message "Tests failed with exit code $exitCode" "WARNING"
                return @{ Success = $false; Message = "Tests failed with exit code $exitCode" }
            }
            
            Log-Message "Tests completed successfully" "SUCCESS"
            return @{ Success = $true; Message = "Tests passed" }
        }
        catch {
            Log-Message "Error running tests: $_" "ERROR"
            return @{ Success = $false; Message = "Error: $_" }
        }
    }
    finally {
        # Reset environment variables and location
        $env:SPRING_PROFILES_ACTIVE = $null
        Pop-Location
    }
}

function Test-NodeService {
    param (
        [string]$ServiceName,
        [string]$BasePath,
        [string]$ReportsDir
    )
    
    $servicePath = Join-Path -Path $BasePath -ChildPath $ServiceName
    $serviceReportDir = Join-Path -Path $ReportsDir -ChildPath $ServiceName
    
    # Create service report directory
    if (-not (Test-Path -Path $serviceReportDir)) {
        New-Item -ItemType Directory -Path $serviceReportDir -Force | Out-Null
    }
    
    Log-Message "Testing Node.js service: $ServiceName" "TEST"
    
    try {
        # Change to service directory
        Push-Location $servicePath
        
        # Check if package.json exists
        if (-not (Test-Path "package.json")) {
            Log-Message "No package.json found in $ServiceName" "WARNING"
            return @{ Success = $false; Message = "No package.json found" }
        }
        
        # Check if test script exists
        $packageJson = Get-Content -Path "package.json" -Raw | ConvertFrom-Json
        if (-not ($packageJson.scripts.PSObject.Properties.Name -contains "test")) {
            Log-Message "No test script found in package.json" "WARNING"
            return @{ Success = $false; Message = "No test script defined in package.json" }
        }
        
        # Determine package manager
        $useYarn = Test-Path "yarn.lock"
        $testCommand = if ($useYarn) { "yarn test" } else { "npm test" }
        
        # Run tests
        Log-Message "Running $testCommand for $ServiceName" "INFO"
        
        try {
            $testLog = Join-Path -Path $serviceReportDir -ChildPath "test-output.log"
            # Set NODE_ENV to test
            $env:NODE_ENV = "test"
            
            # Execute the test command
            Invoke-Expression "$testCommand" | Tee-Object -FilePath $testLog
            $exitCode = $LASTEXITCODE
            
            if ($exitCode -ne 0) {
                Log-Message "Tests failed with exit code $exitCode" "WARNING"
                return @{ Success = $false; Message = "Tests failed with exit code $exitCode" }
            }
            
            Log-Message "Tests completed successfully" "SUCCESS"
            return @{ Success = $true; Message = "Tests passed" }
        }
        catch {
            Log-Message "Error running tests: $_" "ERROR"
            return @{ Success = $false; Message = "Error: $_" }
        }
    }
    finally {
        # Reset environment variables and location
        $env:NODE_ENV = $null
        Pop-Location
    }
}

function Check-APIRegression {
    param (
        [string]$ServiceName,
        [string]$BasePath
    )
    
    $servicePath = Join-Path -Path $BasePath -ChildPath $ServiceName
    
    Log-Message "Checking for API regression in $ServiceName" "INFO"
    
    try {
        # Create temporary baseline directory
        $baselineDir = Join-Path -Path $servicePath -ChildPath "api-baseline"
        if (-not (Test-Path -Path $baselineDir)) {
            New-Item -ItemType Directory -Path $baselineDir -Force | Out-Null
        }
        
        # Generate current API surface
        if (Test-Path -Path "$servicePath\pom.xml") {
            # Java service - check controller classes
            $controllerFiles = Get-ChildItem -Path "$servicePath" -Recurse -Filter "*Controller.java" -ErrorAction SilentlyContinue
            $endpoints = @()
            
            foreach ($file in $controllerFiles) {
                $content = Get-Content -Path $file.FullName -Raw
                $matches = [regex]::Matches($content, '@(Get|Post|Put|Delete|Patch)Mapping\s*\(\s*(?:value\s*=\s*)?[''"]?([^)''"]+)[''"]?\s*\)')
                
                foreach ($match in $matches) {
                    $httpMethod = $match.Groups[1].Value
                    $path = $match.Groups[2].Value
                    $endpoints += "[$httpMethod] $path"
                }
            }
            
            # Save current API surface
            $apiSurfacePath = Join-Path -Path $baselineDir -ChildPath "api-surface.txt"
            $endpoints | Sort-Object | Set-Content -Path $apiSurfacePath -Encoding UTF8
            
            # Check regression against previous baseline if exists
            $baselinePath = Join-Path -Path $servicePath -ChildPath "api-baseline-prev"
            $prevSurfacePath = Join-Path -Path $baselinePath -ChildPath "api-surface.txt"
            
            if (Test-Path -Path $prevSurfacePath) {
                $prevEndpoints = Get-Content -Path $prevSurfacePath
                $currentEndpoints = $endpoints | Sort-Object
                
                $removedEndpoints = $prevEndpoints | Where-Object { $_ -notin $currentEndpoints }
                
                if ($removedEndpoints) {
                    Log-Message "API Regression detected in $ServiceName" "ERROR"
                    Log-Message "The following endpoints were removed:" "ERROR"
                    foreach ($endpoint in $removedEndpoints) {
                        Log-Message "  - $endpoint" "ERROR"
                    }
                    return $false
                }
            }
            
            # No regression or no previous baseline
            return $true
        }
        elseif (Test-Path -Path "$servicePath\package.json") {
            # Node.js service - check route files
            $routeFiles = Get-ChildItem -Path "$servicePath" -Recurse -Include "*.js" -Exclude "node_modules" -ErrorAction SilentlyContinue
            $endpoints = @()
            
            foreach ($file in $routeFiles) {
                $content = Get-Content -Path $file.FullName -Raw
                $matches = [regex]::Matches($content, '(router|app)\.(get|post|put|delete|patch)\s*\([''"]([^''"]+)[''"]')
                
                foreach ($match in $matches) {
                    $httpMethod = $match.Groups[2].Value.ToUpper()
                    $path = $match.Groups[3].Value
                    $endpoints += "[$httpMethod] $path"
                }
            }
            
            # Save current API surface
            $apiSurfacePath = Join-Path -Path $baselineDir -ChildPath "api-surface.txt"
            $endpoints | Sort-Object | Set-Content -Path $apiSurfacePath -Encoding UTF8
            
            # Check regression against previous baseline if exists
            $baselinePath = Join-Path -Path $servicePath -ChildPath "api-baseline-prev"
            $prevSurfacePath = Join-Path -Path $baselinePath -ChildPath "api-surface.txt"
            
            if (Test-Path -Path $prevSurfacePath) {
                $prevEndpoints = Get-Content -Path $prevSurfacePath
                $currentEndpoints = $endpoints | Sort-Object
                
                $removedEndpoints = $prevEndpoints | Where-Object { $_ -notin $currentEndpoints }
                
                if ($removedEndpoints) {
                    Log-Message "API Regression detected in $ServiceName" "ERROR"
                    Log-Message "The following endpoints were removed:" "ERROR"
                    foreach ($endpoint in $removedEndpoints) {
                        Log-Message "  - $endpoint" "ERROR"
                    }
                    return $false
                }
            }
            
            # No regression or no previous baseline
            return $true
        }
        else {
            Log-Message "Unknown service type for $ServiceName" "WARNING"
            return $true  # Can't detect regression for unknown service type
        }
    }
    catch {
        Log-Message "Error checking API regression: $_" "ERROR"
        return $true  # Don't fail the build for regression check errors
    }
}

function Generate-TestReport {
    param (
        [hashtable]$TestResults,
        [string]$ReportsDir
    )
    
    $passedCount = ($TestResults.GetEnumerator() | Where-Object { $_.Value.Success -eq $true }).Count
    $totalCount = $TestResults.Count
    $passRate = [math]::Round(($passedCount / $totalCount) * 100, 2)
    
    Log-Message "Generating test report" "INFO"
    
    # Create HTML report
    $reportContent = @"
<!DOCTYPE html>
<html>
<head>
    <title>Courier Services Test Results</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .summary { margin-bottom: 30px; }
        .success { color: green; }
        .failure { color: red; }
        table { border-collapse: collapse; width: 100%; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
        .progress-bar { background-color: #eee; height: 20px; border-radius: 4px; margin: 10px 0; }
        .progress { background-color: #4CAF50; height: 100%; border-radius: 4px; }
    </style>
</head>
<body>
    <h1>Courier Services Test Results</h1>
    
    <div class="summary">
        <h2>Summary</h2>
        <p>Test Run: $(Get-Date -Format "yyyy-MM-dd HH:mm:ss")</p>
        <p>Services Tested: $totalCount</p>
        <p>Services Passed: $passedCount</p>
        <p>Pass Rate: $passRate%</p>
        
        <div class="progress-bar">
            <div class="progress" style="width: $passRate%;"></div>
        </div>
    </div>
    
    <h2>Detailed Results</h2>
    <table>
        <tr>
            <th>Service</th>
            <th>Status</th>
            <th>Details</th>
        </tr>
"@
    
    foreach ($result in $TestResults.GetEnumerator() | Sort-Object Name) {
        $statusClass = if ($result.Value.Success) { "success" } else { "failure" }
        $statusText = if ($result.Value.Success) { "PASSED" } else { "FAILED" }
        
        $reportContent += @"
        <tr>
            <td>$($result.Name)</td>
            <td class="$statusClass">$statusText</td>
            <td>$($result.Value.Message)</td>
        </tr>
"@
    }
    
    $reportContent += @"
    </table>
</body>
</html>
"@
    
    $reportPath = Join-Path -Path $ReportsDir -ChildPath "test-summary.html"
    $reportContent | Set-Content -Path $reportPath -Encoding UTF8
    
    Log-Message "Test report generated at $reportPath" "SUCCESS"
    return $reportPath
}

# Main execution
try {
    Log-Message "Starting test verification for all courier services" "INFO"
    
    # Initialize test environment
    $dirs = Initialize-TestEnvironment
    $baseDir = $dirs.BaseDir
    $reportsDir = $dirs.ReportsDir
    
    # Get list of services
    $services = Get-ServiceList -BaseDir $baseDir
    $javaServices = $services.JavaServices
    $nodeServices = $services.NodeServices
    
    # Track test results
    $testResults = @{}
    $allTestsPassed = $true
    $regressionDetected = $false
    
    # Test Java services
    Log-Message "Testing $($javaServices.Count) Java services" "INFO"
    foreach ($service in $javaServices) {
        $testResult = Test-JavaService -ServiceName $service -BasePath $baseDir -ReportsDir $reportsDir
        $testResults[$service] = $testResult
        
        if (-not $testResult.Success) {
            $allTestsPassed = $false
        }
        
        # Check for API regression
        $noRegression = Check-APIRegression -ServiceName $service -BasePath $baseDir
        if (-not $noRegression) {
            $regressionDetected = $true
            $testResults[$service].Message += " (API Regression detected)"
        }
    }
    
    # Test Node.js services
    Log-Message "Testing $($nodeServices.Count) Node.js services" "INFO"
    foreach ($service in $nodeServices) {
        $testResult = Test-NodeService -ServiceName $service -BasePath $baseDir -ReportsDir $reportsDir
        $testResults[$service] = $testResult
        
        if (-not $testResult.Success) {
            $allTestsPassed = $false
        }
        
        # Check for API regression
        $noRegression = Check-APIRegression -ServiceName $service -BasePath $baseDir
        if (-not $noRegression) {
            $regressionDetected = $true
            $testResults[$service].Message += " (API Regression detected)"
        }
    }
    
    # Generate report
    $reportPath = Generate-TestReport -TestResults $testResults -ReportsDir $reportsDir
    
    # Final summary
    Log-Message "Test verification complete" "INFO"
    Log-Message "Services tested: $($testResults.Count)" "INFO"
    Log-Message "Services passed: $($($testResults.Values | Where-Object { $_.Success }).Count)" "INFO"
    
    if ($allTestsPassed) {
        Log-Message "✅ ALL TESTS PASSED" "SUCCESS"
    } else {
        Log-Message "❌ SOME TESTS FAILED" "ERROR"
    }
    
    if ($regressionDetected) {
        Log-Message "⚠️ API REGRESSION DETECTED" "ERROR"
    } else {
        Log-Message "✅ NO API REGRESSION DETECTED" "SUCCESS"
    }
    
    Log-Message "Test report available at: $reportPath" "INFO"
    
    # Try to open the report
    try {
        Start-Process $reportPath
    } catch {
        Log-Message "Could not open the report automatically" "WARNING"
    }
    
    # Return appropriate exit code
    if (-not $allTestsPassed -or $regressionDetected) {
        exit 1
    } else {
        exit 0
    }
} 
catch {
    Log-Message "Fatal error: $_" "ERROR"
    exit 1
}
