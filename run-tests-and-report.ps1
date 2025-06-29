# Script to execute tests and generate reports for all courier services
# Works with both local and CI/CD environments

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

$baseDir = "C:\Users\frich\Desktop\Exalt-Application-Limited\Exalt-Application-Limited\social-ecommerce-ecosystem\courier-services"
$reportsDir = Join-Path -Path $baseDir -ChildPath "test-reports"

# Create reports directory if it doesn't exist
if (-not (Test-Path $reportsDir)) {
    New-Item -ItemType Directory -Path $reportsDir -Force | Out-Null
    Log-Message "Created test reports directory" "INFO"
}

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

# Function to run tests for a Java service
function Test-JavaService {
    param (
        [string]$ServiceName
    )
    
    $servicePath = Join-Path -Path $baseDir -ChildPath $ServiceName
    Set-Location $servicePath
    
    Log-Message "Running tests for $ServiceName..." "TEST"
    
    # Check if Maven wrapper exists, use it if available
    $mvnCmd = if (Test-Path "$servicePath\mvnw") { ".\mvnw" } else { "mvn" }
    
    # Run tests with JaCoCo coverage
    try {
        # Ensure we're using local development environment for tests (not connecting to actual services)
        $env:SPRING_PROFILES_ACTIVE = "test"
        
        # Run Maven tests with the jacoco:report goal
        $testOutput = & $mvnCmd test jacoco:report 2>&1
        $testExitCode = $LASTEXITCODE
        
        # Save test output to a log file
        $testLogPath = Join-Path -Path $reportsDir -ChildPath "$ServiceName-test.log"
        $testOutput | Out-File -FilePath $testLogPath -Encoding UTF8
        
        # Copy JaCoCo reports to central reports directory
        $jacocoReportPath = Join-Path -Path $servicePath -ChildPath "target\site\jacoco"
        if (Test-Path $jacocoReportPath) {
            $serviceReportDir = Join-Path -Path $reportsDir -ChildPath $ServiceName
            if (-not (Test-Path $serviceReportDir)) {
                New-Item -ItemType Directory -Path $serviceReportDir -Force | Out-Null
            }
            
            Copy-Item -Path "$jacocoReportPath\*" -Destination $serviceReportDir -Recurse -Force
        }
        
        # Check test results
        if ($testExitCode -eq 0) {
            Log-Message "Tests passed for $ServiceName" "SUCCESS"
            return $true
        } else {
            Log-Message "Tests failed for $ServiceName. See log for details: $testLogPath" "ERROR"
            return $false
        }
    }
    catch {
        Log-Message "Error running tests for $ServiceName: $_" "ERROR"
        return $false
    }
    finally {
        # Reset environment variables
        $env:SPRING_PROFILES_ACTIVE = $null
    }
}

# Function to run tests for a Node.js service
function Test-NodeService {
    param (
        [string]$ServiceName
    )
    
    $servicePath = Join-Path -Path $baseDir -ChildPath $ServiceName
    Set-Location $servicePath
    
    Log-Message "Running tests for $ServiceName..." "TEST"
    
    try {
        # Set test environment variables
        $env:NODE_ENV = "test"
        
        # Check if yarn.lock exists to determine whether to use yarn or npm
        $useYarn = Test-Path "$servicePath\yarn.lock"
        $testCmd = if ($useYarn) { "yarn test:coverage" } else { "npm run test:coverage" }
        
        # Run tests with coverage
        $testOutput = Invoke-Expression $testCmd 2>&1
        $testExitCode = $LASTEXITCODE
        
        # Save test output to a log file
        $testLogPath = Join-Path -Path $reportsDir -ChildPath "$ServiceName-test.log"
        $testOutput | Out-File -FilePath $testLogPath -Encoding UTF8
        
        # Copy coverage reports to central reports directory
        $coverageReportPath = Join-Path -Path $servicePath -ChildPath "coverage"
        if (Test-Path $coverageReportPath) {
            $serviceReportDir = Join-Path -Path $reportsDir -ChildPath $ServiceName
            if (-not (Test-Path $serviceReportDir)) {
                New-Item -ItemType Directory -Path $serviceReportDir -Force | Out-Null
            }
            
            Copy-Item -Path "$coverageReportPath\*" -Destination $serviceReportDir -Recurse -Force
        }
        
        # Check test results
        if ($testExitCode -eq 0) {
            Log-Message "Tests passed for $ServiceName" "SUCCESS"
            return $true
        } else {
            Log-Message "Tests failed for $ServiceName. See log for details: $testLogPath" "ERROR"
            return $false
        }
    }
    catch {
        Log-Message "Error running tests for $ServiceName: $_" "ERROR"
        return $false
    }
    finally {
        # Reset environment variables
        $env:NODE_ENV = $null
    }
}

# Function to generate an HTML summary report
function Generate-SummaryReport {
    param (
        [hashtable]$TestResults
    )
    
    $successCount = ($TestResults.Values | Where-Object { $_ -eq $true }).Count
    $totalCount = $TestResults.Count
    $successRate = [math]::Round(($successCount / $totalCount) * 100, 2)
    
    $htmlReport = @"
<!DOCTYPE html>
<html>
<head>
    <title>Courier Services Test Report</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 0; padding: 20px; }
        h1, h2 { color: #333; }
        .summary { margin: 20px 0; padding: 10px; background-color: #f5f5f5; border-radius: 5px; }
        .progress-bar { background-color: #e0e0e0; height: 25px; border-radius: 5px; margin: 10px 0; }
        .progress { background-color: #4CAF50; height: 100%; border-radius: 5px; text-align: center; color: white; line-height: 25px; }
        table { width: 100%; border-collapse: collapse; margin: 20px 0; }
        th, td { text-align: left; padding: 8px; border-bottom: 1px solid #ddd; }
        th { background-color: #f2f2f2; }
        .passed { color: green; }
        .failed { color: red; }
    </style>
</head>
<body>
    <h1>Courier Services Test Report</h1>
    <div class="summary">
        <h2>Summary</h2>
        <p>Test Date: $(Get-Date -Format "yyyy-MM-dd HH:mm:ss")</p>
        <p>Services Tested: $totalCount</p>
        <p>Services Passed: $successCount</p>
        <p>Success Rate: $successRate%</p>
        <div class="progress-bar">
            <div class="progress" style="width: $successRate%;">$successRate%</div>
        </div>
    </div>
    
    <h2>Test Results</h2>
    <table>
        <tr>
            <th>Service</th>
            <th>Status</th>
            <th>Log</th>
            <th>Coverage Report</th>
        </tr>
"@

    foreach ($service in $TestResults.Keys | Sort-Object) {
        $passed = $TestResults[$service]
        $status = if ($passed) { "PASSED" } else { "FAILED" }
        $statusClass = if ($passed) { "passed" } else { "failed" }
        $logPath = "./$($service)-test.log"
        $coveragePath = "./$service/index.html"
        
        $htmlReport += @"
        <tr>
            <td>$service</td>
            <td class="$statusClass">$status</td>
            <td><a href="$logPath">View Log</a></td>
            <td>$(if ($passed) { "<a href='$coveragePath'>View Coverage</a>" } else { "N/A" })</td>
        </tr>
"@
    }
    
    $htmlReport += @"
    </table>
</body>
</html>
"@

    $reportPath = Join-Path -Path $reportsDir -ChildPath "test-summary.html"
    Set-Content -Path $reportPath -Value $htmlReport -NoNewline
    
    Log-Message "Summary report generated at $reportPath" "SUCCESS"
    return $reportPath
}

# Main execution
Log-Message "Starting test execution for all courier services" "INFO"

$startTime = Get-Date
$testResults = @{}

# Run Java tests
Log-Message "Testing Java services..." "INFO"
foreach ($service in $javaServices) {
    $servicePath = Join-Path -Path $baseDir -ChildPath $service
    
    if (Test-Path $servicePath) {
        $testResults[$service] = Test-JavaService -ServiceName $service
    } else {
        Log-Message "Service directory not found: $service" "WARNING"
        $testResults[$service] = $false
    }
}

# Run Node.js tests
Log-Message "Testing Node.js services..." "INFO"
foreach ($service in $nodeServices) {
    $servicePath = Join-Path -Path $baseDir -ChildPath $service
    
    if (Test-Path $servicePath) {
        $testResults[$service] = Test-NodeService -ServiceName $service
    } else {
        Log-Message "Service directory not found: $service" "WARNING"
        $testResults[$service] = $false
    }
}

# Generate summary report
$reportPath = Generate-SummaryReport -TestResults $testResults

# Calculate total execution time
$endTime = Get-Date
$executionTime = $endTime - $startTime
$formattedTime = "{0:D2}:{1:D2}:{2:D2}" -f $executionTime.Hours, $executionTime.Minutes, $executionTime.Seconds

# Print final summary
$successCount = ($testResults.Values | Where-Object { $_ -eq $true }).Count
$totalCount = $testResults.Count
$successRate = [math]::Round(($successCount / $totalCount) * 100, 2)

Log-Message "Test Execution Complete" "INFO"
Log-Message "------------------------" "INFO"
Log-Message "Total services tested: $totalCount" "INFO"
Log-Message "Services passed: $successCount" "INFO"
Log-Message "Success rate: $successRate%" "INFO"
Log-Message "Execution time: $formattedTime" "INFO"
Log-Message "Summary report: $reportPath" "INFO"

# Open the report if we're not in CI/CD environment
if (-not $env:CI) {
    if (Test-Path $reportPath) {
        Start-Process $reportPath
    }
}

# Set exit code based on test success
if ($successRate -lt 100) {
    exit 1
} else {
    exit 0
}
