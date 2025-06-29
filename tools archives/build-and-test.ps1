# PowerShell script to build and test courier services
Write-Host "======================================"
Write-Host "Courier Services Build & Test Script"
Write-Host "======================================"
Write-Host ""

# Set Java and Maven paths
$env:JAVA_HOME = "C:\Program Files\Microsoft\jdk-17.0.15.10-hotspot"
$env:PATH = "$env:JAVA_HOME\bin;C:\Users\frich\Desktop\Micro-Social-Ecommerce-Ecosystems\apache-maven-3.9.9\bin;$env:PATH"

Write-Host "Java Home: $env:JAVA_HOME"
Write-Host "Checking Java version..."
java -version
Write-Host ""

# Array of services to build
$services = @(
    "branch-courier-app",
    "commission-service", 
    "courier-management",
    "routing-service",
    "tracking-service",
    "payout-service",
    "international-shipping",
    "driver-mobile-app"
)

$passed = 0
$failed = 0

foreach ($service in $services) {
    if (Test-Path $service) {
        Write-Host ""
        Write-Host "======================================"
        Write-Host "Building: $service"
        Write-Host "======================================"
        
        Push-Location $service
        
        # Run Maven clean compile
        mvn clean compile
        
        if ($LASTEXITCODE -eq 0) {
            Write-Host -ForegroundColor Green "✓ SUCCESS: $service compiled successfully"
            $passed++
        } else {
            Write-Host -ForegroundColor Red "✗ FAILED: $service compilation failed"
            $failed++
        }
        
        Pop-Location
    }
}

Write-Host ""
Write-Host "======================================"
Write-Host "BUILD SUMMARY"
Write-Host "======================================"
Write-Host -ForegroundColor Green "Passed: $passed"
Write-Host -ForegroundColor Red "Failed: $failed"
Write-Host "Total: $($passed + $failed)"
Write-Host ""

if ($failed -eq 0) {
    Write-Host -ForegroundColor Green "🎉 All services compiled successfully!"
} else {
    Write-Host -ForegroundColor Yellow "⚠️ Some services failed. Check the output above for details."
}

Write-Host ""
Write-Host "Press any key to continue..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")