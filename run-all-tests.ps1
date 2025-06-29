# Simplified script to run all tests across courier services
# Designed to work with current environment setup

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
            "TEST" { "Cyan" }
            default { "Gray" }
        }
    )
}

# Base directory using relative path to avoid path issues
$scriptPath = $MyInvocation.MyCommand.Path
$baseDir = Split-Path -Parent $scriptPath
Set-Location $baseDir

# Create reports directory
$reportsDir = Join-Path -Path $baseDir -ChildPath "test-reports"
New-Item -ItemType Directory -Path $reportsDir -Force | Out-Null

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

# Track results
$testResults = @{}
$startTime = Get-Date

# Test Java services
Log-Message "Testing Java services..." "INFO"

foreach ($service in $javaServices) {
    $servicePath = Join-Path -Path $baseDir -ChildPath $service
    
    if (Test-Path $servicePath) {
        Log-Message "Running tests for $service..." "TEST"
        
        # Check if Maven wrapper exists
        $mvnCmd = if (Test-Path (Join-Path -Path $servicePath -ChildPath "mvnw")) { 
            ".\mvnw" 
        } else { 
            "mvn" 
        }
        
        try {
            Set-Location $servicePath
            
            # Set test environment
            $env:SPRING_PROFILES_ACTIVE = "test"
            
            # Run Maven test (with simplified options)
            $testSuccess = $false
            
            # Check if pom.xml exists before running Maven
            if (Test-Path (Join-Path -Path $servicePath -ChildPath "pom.xml")) {
                $testOutput = Invoke-Expression "$mvnCmd test -DskipITs" 2>&1
                $testExitCode = $LASTEXITCODE
                
                # Save test output
                $testLogPath = Join-Path -Path $reportsDir -ChildPath "$service-test.log"
                $testOutput | Out-File -FilePath $testLogPath -Encoding UTF8
                
                $testSuccess = ($testExitCode -eq 0)
            } else {
                Log-Message "No pom.xml found for $service" "WARNING"
                $testSuccess = $false
            }
            
            $testResults[$service] = $testSuccess
            
            # Report status
            if ($testSuccess) {
                Log-Message "Tests PASSED for $service" "SUCCESS"
            } else {
                Log-Message "Tests FAILED for $service (or no tests run)" "ERROR"
            }
        }
        catch {
            Log-Message "Error testing $service: $_" "ERROR"
            $testResults[$service] = $false
        }
        finally {
            # Reset environment and location
            $env:SPRING_PROFILES_ACTIVE = $null
            Set-Location $baseDir
        }
    }
    else {
        Log-Message "Service directory not found: $service" "WARNING"
        $testResults[$service] = $false
    }
}

# Test Node.js services
Log-Message "Testing Node.js services..." "INFO"

foreach ($service in $nodeServices) {
    $servicePath = Join-Path -Path $baseDir -ChildPath $service
    
    if (Test-Path $servicePath) {
        Log-Message "Running tests for $service..." "TEST"
        
        try {
            Set-Location $servicePath
            
            # Set test environment
            $env:NODE_ENV = "test"
            
            # Determine package manager
            $useYarn = Test-Path (Join-Path -Path $servicePath -ChildPath "yarn.lock")
            $testSuccess = $false
            
            # Check if package.json exists before running npm/yarn
            if (Test-Path (Join-Path -Path $servicePath -ChildPath "package.json")) {
                if ($useYarn) {
                    $testCmd = "yarn test"
                } else {
                    $testCmd = "npm test"
                }
                
                $testOutput = Invoke-Expression $testCmd 2>&1
                $testExitCode = $LASTEXITCODE
                
                # Save test output
                $testLogPath = Join-Path -Path $reportsDir -ChildPath "$service-test.log"
                $testOutput | Out-File -FilePath $testLogPath -Encoding UTF8
                
                $testSuccess = ($testExitCode -eq 0)
            } else {
                Log-Message "No package.json found for $service" "WARNING"
                $testSuccess = $false
            }
            
            $testResults[$service] = $testSuccess
            
            # Report status
            if ($testSuccess) {
                Log-Message "Tests PASSED for $service" "SUCCESS"
            } else {
                Log-Message "Tests FAILED for $service (or no tests run)" "ERROR"
            }
        }
        catch {
            Log-Message "Error testing $service: $_" "ERROR"
            $testResults[$service] = $false
        }
        finally {
            # Reset environment and location
            $env:NODE_ENV = $null
            Set-Location $baseDir
        }
    }
    else {
        Log-Message "Service directory not found: $service" "WARNING"
        $testResults[$service] = $false
    }
}

# Calculate results
$endTime = Get-Date
$executionTime = $endTime - $startTime
$formattedTime = "{0:D2}:{1:D2}:{2:D2}" -f $executionTime.Hours, $executionTime.Minutes, $executionTime.Seconds

$passedCount = ($testResults.GetEnumerator() | Where-Object { $_.Value -eq $true }).Count
$totalCount = $testResults.Count
$passRate = if ($totalCount -gt 0) { [math]::Round(($passedCount / $totalCount) * 100, 2) } else { 0 }

# Generate simple HTML report
$reportContent = @"
<!DOCTYPE html>
<html>
<head>
    <title>Courier Services Test Results</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .success { color: green; }
        .failure { color: red; }
        table { border-collapse: collapse; width: 100%; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
    </style>
</head>
<body>
    <h1>Courier Services Test Results</h1>
    <p>Test run: $(Get-Date -Format "yyyy-MM-dd HH:mm:ss")</p>
    <p>Duration: $formattedTime</p>
    <p>Pass rate: $passRate% ($passedCount/$totalCount services)</p>
    
    <h2>Results by Service</h2>
    <table>
        <tr>
            <th>Service</th>
            <th>Status</th>
        </tr>
"@

foreach ($result in $testResults.GetEnumerator() | Sort-Object Name) {
    $statusClass = if ($result.Value) { "success" } else { "failure" }
    $statusText = if ($result.Value) { "PASS" } else { "FAIL" }
    
    $reportContent += @"
        <tr>
            <td>$($result.Name)</td>
            <td class="$statusClass">$statusText</td>
        </tr>
"@
}

$reportContent += @"
    </table>
</body>
</html>
"@

$reportPath = Join-Path -Path $reportsDir -ChildPath "test-summary.html"
Set-Content -Path $reportPath -Value $reportContent

# Final summary
Log-Message "Test execution completed in $formattedTime" "INFO"
Log-Message "Services passed: $passedCount out of $totalCount ($passRate%)" "INFO"
Log-Message "Results saved to $reportPath" "INFO"

# Try to open the report
try {
    Start-Process $reportPath
} catch {
    Log-Message "Report generated at: $reportPath" "INFO"
}

# Return proper exit code
if ($passedCount -lt $totalCount) {
    exit 1
} else {
    exit 0
}
