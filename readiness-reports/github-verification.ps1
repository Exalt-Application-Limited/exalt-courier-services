# GitHub Repository Verification Script (PowerShell)
# Run this in PowerShell to verify all repositories

param(
    [string]$Organization = "Micro-Services-Social-Ecommerce-App",
    [string]$OutputFile = "repository_verification_report.html"
)

Write-Host "🔍 GITHUB REPOSITORY VERIFICATION" -ForegroundColor Cyan
Write-Host "Organization: $Organization" -ForegroundColor Yellow
Write-Host "=" * 60

# Check if GitHub CLI is installed
if (-not (Get-Command gh -ErrorAction SilentlyContinue)) {
    Write-Host "❌ GitHub CLI not found. Please install it first:" -ForegroundColor Red
    Write-Host "   winget install GitHub.cli" -ForegroundColor Yellow
    exit 1
}

# Check if logged in to GitHub CLI
try {
    gh auth status 2>$null
} catch {
    Write-Host "❌ Please login to GitHub CLI first:" -ForegroundColor Red
    Write-Host "   gh auth login" -ForegroundColor Yellow
    exit 1
}

# Get list of repositories
Write-Host "📡 Fetching repository list..." -ForegroundColor Green
$repos = gh repo list $Organization --limit 200 --json name,isPrivate,updatedAt | ConvertFrom-Json

Write-Host "Found $($repos.Count) repositories" -ForegroundColor Green

# Initialize HTML report
$htmlReport = @"
<!DOCTYPE html>
<html>
<head>
    <title>Repository Verification Report</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .ready { color: green; }
        .mostly-ready { color: orange; }
        .needs-work { color: red; }
        .critical { color: darkred; font-weight: bold; }
        table { border-collapse: collapse; width: 100%; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
        .summary { background-color: #f9f9f9; padding: 15px; margin: 20px 0; }
    </style>
</head>
<body>
    <h1>🔍 GitHub Repository Verification Report</h1>
    <div class="summary">
        <h2>Summary</h2>
        <p><strong>Date:</strong> $(Get-Date)</p>
        <p><strong>Organization:</strong> $Organization</p>
        <p><strong>Total Repositories:</strong> $($repos.Count)</p>
    </div>
    <table>
        <tr>
            <th>Repository</th>
            <th>Status</th>
            <th>Score</th>
            <th>Issues</th>
            <th>Last Updated</th>
        </tr>
"@

# Function to check repository
function Test-Repository {
    param($repoName)
    
    Write-Host "📋 Checking: $repoName" -ForegroundColor Cyan
    
    $tempDir = "temp_$repoName"
    $score = 0
    $total = 12
    $issues = @()
    
    try {
        # Clone repository
        git clone "https://github.com/$Organization/$repoName" $tempDir 2>$null
        if (-not $?) {
            throw "Failed to clone repository"
        }
        
        Set-Location $tempDir
        
        # Check essential files
        if (Test-Path "README.md" -or Test-Path "readme.md") { $score++ } else { $issues += "Missing README.md" }
        if (Test-Path "pom.xml" -or Test-Path "package.json" -or Test-Path "build.gradle") { $score++ } else { $issues += "Missing build configuration" }
        if (Test-Path "Dockerfile") { $score++ } else { $issues += "Missing Dockerfile" }
        if (Test-Path ".gitignore") { $score++ } else { $issues += "Missing .gitignore" }
        if (Test-Path "src") { $score++ } else { $issues += "Missing src directory" }
        if (Test-Path ".github/workflows") { $score++ } else { $issues += "Missing GitHub Actions" }
        if (Test-Path "docs" -or Test-Path "documentation") { $score++ } else { $issues += "Missing documentation" }
        if (Test-Path "application.yml" -or Test-Path "application.properties" -or Test-Path "config.json") { $score++ } else { $issues += "Missing configuration" }
        
        # Check for tests
        $testFiles = Get-ChildItem -Recurse -Include "*Test.java", "*.test.js", "*test.py" -ErrorAction SilentlyContinue
        if ($testFiles) { $score++ } else { $issues += "Missing tests" }
        
        # Check for API docs
        if (Test-Path "api-docs" -or Test-Path "openapi.yaml" -or Test-Path "swagger.json") { $score++ } else { $issues += "Missing API docs" }
        
        # Check docker-compose
        if (Test-Path "docker-compose.yml") { $score++ } else { $issues += "Missing docker-compose.yml" }
        
        # Check for k8s configs
        if (Test-Path "k8s" -or Test-Path "kubernetes") { $score++ } else { $issues += "Missing Kubernetes configs" }
        
        # Calculate percentage and status
        $percentage = [math]::Round(($score / $total) * 100)
        
        $status = switch ($percentage) {
            {$_ -ge 90} { "✅ READY"; "ready" }
            {$_ -ge 70} { "🔄 MOSTLY READY"; "mostly-ready" }
            {$_ -ge 50} { "⚠️ NEEDS WORK"; "needs-work" }
            default { "🚨 CRITICAL"; "critical" }
        }
        
        Set-Location ..
        Remove-Item $tempDir -Recurse -Force -ErrorAction SilentlyContinue
        
        return @{
            Name = $repoName
            Status = $status[0]
            CssClass = $status[1]
            Score = "$score/$total ($percentage%)"
            Issues = $issues -join ", "
            Percentage = $percentage
        }
        
    } catch {
        Set-Location .. -ErrorAction SilentlyContinue
        Remove-Item $tempDir -Recurse -Force -ErrorAction SilentlyContinue
        
        return @{
            Name = $repoName
            Status = "❌ ERROR"
            CssClass = "critical"
            Score = "0/12 (0%)"
            Issues = $_.Exception.Message
            Percentage = 0
        }
    }
}

# Process each repository
$results = @()
$counter = 0

foreach ($repo in $repos) {
    $counter++
    Write-Progress -Activity "Verifying Repositories" -Status "Processing $($repo.name)" -PercentComplete (($counter / $repos.Count) * 100)
    
    $result = Test-Repository $repo.name
    $results += $result
    
    # Add to HTML report
    $htmlReport += @"
        <tr>
            <td>$($result.Name)</td>
            <td class="$($result.CssClass)">$($result.Status)</td>
            <td>$($result.Score)</td>
            <td>$($result.Issues)</td>
            <td>$($repo.updatedAt)</td>
        </tr>
"@
}

# Generate summary statistics
$readyCount = ($results | Where-Object { $_.Percentage -ge 90 }).Count
$mostlyReadyCount = ($results | Where-Object { $_.Percentage -ge 70 -and $_.Percentage -lt 90 }).Count
$needsWorkCount = ($results | Where-Object { $_.Percentage -ge 50 -and $_.Percentage -lt 70 }).Count
$criticalCount = ($results | Where-Object { $_.Percentage -lt 50 }).Count

# Complete HTML report
$htmlReport += @"
    </table>
    
    <div class="summary">
        <h2>📊 Summary Statistics</h2>
        <ul>
            <li><span class="ready">✅ Ready for Production (90%+):</span> $readyCount repositories</li>
            <li><span class="mostly-ready">🔄 Mostly Ready (70-89%):</span> $mostlyReadyCount repositories</li>
            <li><span class="needs-work">⚠️ Needs Work (50-69%):</span> $needsWorkCount repositories</li>
            <li><span class="critical">🚨 Critical Issues (<50%):</span> $criticalCount repositories</li>
        </ul>
        
        <h3>🎯 Deployment Recommendations</h3>
        <ul>
            <li><strong>Deploy Immediately:</strong> $readyCount repositories</li>
            <li><strong>Deploy This Week:</strong> $mostlyReadyCount repositories (after minor fixes)</li>
            <li><strong>Deploy Next Week:</strong> $needsWorkCount repositories (after moderate work)</li>
            <li><strong>Requires Major Work:</strong> $criticalCount repositories</li>
        </ul>
    </div>
</body>
</html>
"@

# Save report
$htmlReport | Out-File -FilePath $OutputFile -Encoding UTF8

Write-Host "`n✅ Verification completed!" -ForegroundColor Green
Write-Host "📄 Report saved to: $OutputFile" -ForegroundColor Yellow
Write-Host "`n📊 Summary:" -ForegroundColor Cyan
Write-Host "   Ready: $readyCount" -ForegroundColor Green
Write-Host "   Mostly Ready: $mostlyReadyCount" -ForegroundColor Yellow
Write-Host "   Needs Work: $needsWorkCount" -ForegroundColor Red
Write-Host "   Critical: $criticalCount" -ForegroundColor DarkRed

# Open report in default browser
Start-Process $OutputFile
