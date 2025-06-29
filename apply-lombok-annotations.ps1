# Script to apply Lombok annotations to Java model classes
# This reduces boilerplate code as per requirements

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

Log-Message "Starting Lombok annotation application" "START"

# List of Java services
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
        Log-Message "Processing service: $service" "PROCESS"
        
        # Find all Java files
        $javaFiles = Get-ChildItem -Path $srcDir -Filter "*.java" -Recurse
        
        foreach ($file in $javaFiles) {
            $content = Get-Content -Path $file.FullName -Raw
            $modified = $false
            
            # Skip files that already have Lombok annotations
            if ($content -match "@Data" -or $content -match "@Getter" -or $content -match "@Setter" -or 
                $content -match "@Builder" -or $content -match "@NoArgsConstructor" -or 
                $content -match "@AllArgsConstructor" -or $content -match "@Slf4j") {
                Log-Message "File already has Lombok annotations: $($file.FullName)" "SKIP"
                continue
            }
            
            # Check if file is a model/entity class (has fields and getters/setters)
            if ($content -match "class\s+\w+" -and 
               ($content -match "private\s+\w+\s+\w+;" -or $content -match "public\s+\w+\s+get\w+\(\)" -or 
                $content -match "public\s+void\s+set\w+\(")) {
                
                Log-Message "Applying Lombok annotations to: $($file.FullName)" "APPLY"
                
                # Add import for Lombok annotations if needed
                if (-not ($content -match "import\s+lombok\.")) {
                    $content = $content -replace "(package\s+[\w\.]+;)", "`$1`n`nimport lombok.Data;`nimport lombok.Builder;`nimport lombok.NoArgsConstructor;`nimport lombok.AllArgsConstructor;"
                    $modified = $true
                }
                
                # Add annotations before class definition
                if ($content -match "public\s+class\s+(\w+)") {
                    $content = $content -replace "public\s+class\s+(\w+)", "@Data`n@Builder`n@NoArgsConstructor`n@AllArgsConstructor`npublic class `$1"
                    $modified = $true
                }
                
                # Remove getters and setters
                $content = [regex]::Replace($content, "public\s+\w+\s+get\w+\(\)\s*\{[\s\S]*?\}", "", [System.Text.RegularExpressions.RegexOptions]::Multiline)
                $content = [regex]::Replace($content, "public\s+void\s+set\w+\([^)]*\)\s*\{[\s\S]*?\}", "", [System.Text.RegularExpressions.RegexOptions]::Multiline)
                $modified = $true
                
                # Remove default constructor with no parameters
                $content = [regex]::Replace($content, "public\s+\w+\(\)\s*\{[\s\S]*?\}", "", [System.Text.RegularExpressions.RegexOptions]::Multiline)
                
                # Remove constructor with all parameters (complex, simplistic approach)
                if ($content -match "public\s+\w+\([^)]+\)\s*\{") {
                    $content = [regex]::Replace($content, "public\s+\w+\([^)]+\)\s*\{[\s\S]*?\}", "", [System.Text.RegularExpressions.RegexOptions]::Multiline)
                    $modified = $true
                }
                
                # Remove toString method
                $content = [regex]::Replace($content, "public\s+String\s+toString\(\)\s*\{[\s\S]*?\}", "", [System.Text.RegularExpressions.RegexOptions]::Multiline)
                
                # Remove equals and hashCode methods
                $content = [regex]::Replace($content, "public\s+boolean\s+equals\([^)]*\)\s*\{[\s\S]*?\}", "", [System.Text.RegularExpressions.RegexOptions]::Multiline)
                $content = [regex]::Replace($content, "public\s+int\s+hashCode\(\)\s*\{[\s\S]*?\}", "", [System.Text.RegularExpressions.RegexOptions]::Multiline)
                
                # Clean up any double blank lines
                $content = $content -replace "\n{3,}", "`n`n"
                
                if ($modified) {
                    Set-Content -Path $file.FullName -Value $content
                    Log-Message "Applied Lombok annotations to $($file.Name)" "UPDATED"
                }
            }
            
            # Check if file is a service class (should have @Slf4j)
            if ($content -match "class\s+\w+.+(Service|Repository|Controller)" -and -not ($content -match "@Slf4j")) {
                Log-Message "Adding @Slf4j to service class: $($file.FullName)" "APPLY"
                
                # Add import for Lombok Slf4j if needed
                if (-not ($content -match "import\s+lombok\.extern\.slf4j\.Slf4j")) {
                    $content = $content -replace "(package\s+[\w\.]+;)", "`$1`n`nimport lombok.extern.slf4j.Slf4j;"
                }
                
                # Add @Slf4j annotation before class definition
                $content = $content -replace "(public\s+class\s+\w+)", "@Slf4j`n`$1"
                
                # Remove any logger instantiation
                $content = $content -replace "private\s+(?:static\s+)?final\s+Logger\s+\w+\s*=\s*LoggerFactory\.getLogger\([^)]+\);", ""
                
                # Clean up any double blank lines
                $content = $content -replace "\n{3,}", "`n`n"
                
                Set-Content -Path $file.FullName -Value $content
                Log-Message "Added @Slf4j to $($file.Name)" "UPDATED"
            }
        }
    }
}

Log-Message "Lombok annotation application completed" "DONE"
