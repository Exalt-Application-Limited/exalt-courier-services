# Courier Services Test Orchestration System
# This script orchestrates the entire test standardization framework
# Runs all test components in the appropriate sequence for comprehensive validation

param (
    [switch]$SkipUnitTests,
    [switch]$SkipIntegrationTests,
    [switch]$SkipWorkflowTests,
    [switch]$SkipRegressionChecks,
    [switch]$GenerateReports = $true
)

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
            "STEP" { "Blue" }
            default { "Gray" }
        }
    )
}

function Initialize-TestEnvironment {
    Log-Message "Initializing test environment" "STEP"
    
    # Get script directory
    $scriptPath = $MyInvocation.MyCommand.Path
    $baseDir = Split-Path -Parent $scriptPath
    
    # Create master report directory
    $masterReportDir = Join-Path -Path $baseDir -ChildPath "master-test-reports"
    if (-not (Test-Path $masterReportDir)) {
        New-Item -ItemType Directory -Path $masterReportDir -Force | Out-Null
    }
    
    # Create run-specific directory with timestamp
    $timestamp = Get-Date -Format "yyyy-MM-dd_HH-mm-ss"
    $runReportDir = Join-Path -Path $masterReportDir -ChildPath "test-run-$timestamp"
    New-Item -ItemType Directory -Path $runReportDir -Force | Out-Null
    
    return @{
        BaseDir = $baseDir
        ReportDir = $runReportDir
        Timestamp = $timestamp
    }
}

function Execute-TestPhase {
    param (
        [string]$PhaseName,
        [string]$ScriptPath,
        [string]$BaseDir,
        [string]$ReportDir,
        [string[]]$ScriptArguments = @()
    )
    
    Log-Message "Starting test phase: $PhaseName" "STEP"
    
    # Create phase-specific log file
    $phaseLogFile = Join-Path -Path $ReportDir -ChildPath "$PhaseName.log"
    
    try {
        # Change to base directory
        Push-Location $BaseDir
        
        # Check if script exists
        if (-not (Test-Path $ScriptPath)) {
            Log-Message "Script not found: $ScriptPath" "ERROR"
            return $false
        }
        
        # Execute script and capture output
        $scriptCommand = "& '$ScriptPath' $($ScriptArguments -join ' ')"
        Log-Message "Executing: $scriptCommand" "INFO"
        
        $output = Invoke-Expression $scriptCommand 2>&1
        $exitCode = $LASTEXITCODE
        
        # Save output to log file
        $output | Out-File -FilePath $phaseLogFile -Encoding UTF8
        
        # Check result
        if ($exitCode -eq 0) {
            Log-Message "Phase completed successfully: $PhaseName" "SUCCESS"
            return $true
        } else {
            Log-Message "Phase failed: $PhaseName (Exit Code: $exitCode)" "ERROR"
            return $false
        }
    }
    catch {
        Log-Message "Error executing phase $PhaseName: $_" "ERROR"
        $_ | Out-File -FilePath $phaseLogFile -Encoding UTF8 -Append
        return $false
    }
    finally {
        # Return to original directory
        Pop-Location
    }
}

function Generate-MasterReport {
    param (
        [hashtable]$PhaseResults,
        [string]$ReportDir,
        [string]$Timestamp
    )
    
    Log-Message "Generating master test report" "STEP"
    
    $totalPhases = $PhaseResults.Count
    $passedPhases = ($PhaseResults.GetEnumerator() | Where-Object { $_.Value -eq $true }).Count
    $failedPhases = $totalPhases - $passedPhases
    
    $reportContent = @"
# Courier Services Test Orchestration Report

**Execution Date:** $(Get-Date -Format "yyyy-MM-dd HH:mm:ss")  
**Run ID:** $Timestamp

## Summary

- **Total Test Phases:** $totalPhases
- **Passed Phases:** $passedPhases
- **Failed Phases:** $failedPhases
- **Overall Status:** $(if ($failedPhases -eq 0) { "✅ SUCCESS" } else { "❌ FAILURE" })

## Phase Results

| Phase | Status | Log |
|-------|--------|-----|
"@
    
    foreach ($phase in $PhaseResults.GetEnumerator() | Sort-Object Name) {
        $status = if ($phase.Value) { "✅ PASSED" } else { "❌ FAILED" }
        $logLink = "./$(Split-Path -Leaf $phase.Name).log"
        $reportContent += "`n| $($phase.Name) | $status | [View Log]($logLink) |"
    }
    
    $reportContent += @"

## Next Steps

1. Review any failed phases and address issues
2. Update test data and assertions as needed
3. Re-run failed tests to confirm fixes
4. Check coverage reports to identify gaps
5. Review workflow validations to ensure business logic is properly tested

"@
    
    # Save report
    $reportPath = Join-Path -Path $ReportDir -ChildPath "master-report.md"
    $reportContent | Set-Content -Path $reportPath -Encoding UTF8
    
    Log-Message "Master report saved to: $reportPath" "SUCCESS"
    return $reportPath
}

# Main execution
try {
    Log-Message "Starting Courier Services Test Orchestration" "INFO"
    Log-Message "====================================" "INFO"
    
    # Initialize environment
    $env = Initialize-TestEnvironment
    $baseDir = $env.BaseDir
    $reportDir = $env.ReportDir
    $timestamp = $env.Timestamp
    
    # Track results of each phase
    $phaseResults = @{}
    
    # Phase 1: Generate Test Data
    if (-not $SkipUnitTests) {
        $scriptPath = Join-Path -Path $baseDir -ChildPath "generate-test-data.ps1"
        $phaseResults["1-Test-Data-Generation"] = Execute-TestPhase -PhaseName "1-Test-Data-Generation" -ScriptPath $scriptPath -BaseDir $baseDir -ReportDir $reportDir
    }
    
    # Phase 2: Configure Test Coverage
    if (-not $SkipUnitTests) {
        $scriptPath = Join-Path -Path $baseDir -ChildPath "configure-test-coverage.ps1"
        $phaseResults["2-Coverage-Configuration"] = Execute-TestPhase -PhaseName "2-Coverage-Configuration" -ScriptPath $scriptPath -BaseDir $baseDir -ReportDir $reportDir
    }
    
    # Phase 3: Run Unit Tests for Java Services
    if (-not $SkipUnitTests) {
        $scriptPath = Join-Path -Path $baseDir -ChildPath "verify-all-tests.ps1"
        $phaseResults["3-Unit-Tests"] = Execute-TestPhase -PhaseName "3-Unit-Tests" -ScriptPath $scriptPath -BaseDir $baseDir -ReportDir $reportDir
    }
    
    # Phase 4: Run Cross-Service Integration Tests
    if (-not $SkipIntegrationTests) {
        $scriptPath = Join-Path -Path $baseDir -ChildPath "run-cross-service-tests.ps1"
        $phaseResults["4-Integration-Tests"] = Execute-TestPhase -PhaseName "4-Integration-Tests" -ScriptPath $scriptPath -BaseDir $baseDir -ReportDir $reportDir
    }
    
    # Phase 5: Validate Business Workflows
    if (-not $SkipWorkflowTests) {
        $scriptPath = Join-Path -Path $baseDir -ChildPath "validate-business-workflows.ps1"
        $phaseResults["5-Business-Workflows"] = Execute-TestPhase -PhaseName "5-Business-Workflows" -ScriptPath $scriptPath -BaseDir $baseDir -ReportDir $reportDir
    }
    
    # Phase 6: Check for API Regression
    if (-not $SkipRegressionChecks) {
        $scriptPath = Join-Path -Path $baseDir -ChildPath "validate-no-regression.ps1"
        $phaseResults["6-API-Regression-Checks"] = Execute-TestPhase -PhaseName "6-API-Regression-Checks" -ScriptPath $scriptPath -BaseDir $baseDir -ReportDir $reportDir
    }
    
    # Phase 7: Setup Git Hooks (only if not skipping other phases)
    if (-not ($SkipUnitTests -and $SkipIntegrationTests -and $SkipWorkflowTests -and $SkipRegressionChecks)) {
        $scriptPath = Join-Path -Path $baseDir -ChildPath "setup-git-hooks.ps1"
        $phaseResults["7-Git-Hooks-Setup"] = Execute-TestPhase -PhaseName "7-Git-Hooks-Setup" -ScriptPath $scriptPath -BaseDir $baseDir -ReportDir $reportDir
    }
    
    # Generate master report
    if ($GenerateReports) {
        $masterReportPath = Generate-MasterReport -PhaseResults $phaseResults -ReportDir $reportDir -Timestamp $timestamp
    }
    
    # Calculate pass rate
    $totalPhases = $phaseResults.Count
    $passedPhases = ($phaseResults.GetEnumerator() | Where-Object { $_.Value -eq $true }).Count
    $passRate = [math]::Round(($passedPhases / $totalPhases) * 100, 2)
    
    # Final summary
    Log-Message "====================================" "INFO"
    Log-Message "Test Orchestration Complete" "INFO"
    Log-Message "Phases executed: $totalPhases" "INFO"
    Log-Message "Phases passed: $passedPhases" "INFO"
    Log-Message "Pass rate: $passRate%" "INFO"
    
    if ($passedPhases -eq $totalPhases) {
        Log-Message "✅ ALL TEST PHASES COMPLETED SUCCESSFULLY" "SUCCESS"
        if ($GenerateReports) {
            Log-Message "Master report available at: $masterReportPath" "INFO"
        }
        exit 0
    } else {
        Log-Message "❌ SOME TEST PHASES FAILED" "ERROR"
        if ($GenerateReports) {
            Log-Message "Review the master report for details: $masterReportPath" "INFO"
        }
        exit 1
    }
} 
catch {
    Log-Message "Fatal error in test orchestration: $_" "ERROR"
    exit 1
}
