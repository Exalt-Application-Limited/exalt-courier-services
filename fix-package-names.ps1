# Script to fix Java package naming in courier-tracking-service

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

Log-Message "Starting courier-tracking-service package naming fix" "START"

$service = "courier-tracking-service"
$serviceDir = Join-Path -Path $baseDir -ChildPath $service

if (Test-Path $serviceDir) {
    Log-Message "Fixing package naming for $service" "PROCESS"
    
    # Find all Java files
    $javaFiles = Get-ChildItem -Path $serviceDir -Filter "*.java" -Recurse | Where-Object { $_.FullName -match "\\src\\main\\java\\" -or $_.FullName -match "\\src\\test\\java\\" }
    
    foreach ($file in $javaFiles) {
        $content = Get-Content -Path $file.FullName -Raw
        $modified = $false
        
        # Fix package declarations
        if ($content -match "package\s+([\w\.]+);") {
            $originalPackage = $matches[1]
            if (-not ($originalPackage -match "^com\.exalt\.courierservices\.")) {
                $newPackage = $originalPackage
                
                # Handle different package patterns
                $newPackage = $newPackage -replace "^com\.exalt\.courier", "com.exalt.courierservices"
                $newPackage = $newPackage -replace "^com\.microecosystem\.courier", "com.exalt.courierservices"
                $newPackage = $newPackage -replace "^com\.socialecommerceecosystem", "com.exalt.courierservices"
                $newPackage = $newPackage -replace "^org\.exalt\.courier", "com.exalt.courierservices"
                $newPackage = $newPackage -replace "^io\.exalt\.courier", "com.exalt.courierservices"
                
                if ($newPackage -ne $originalPackage) {
                    $content = $content -replace "package\s+$originalPackage;", "package $newPackage;"
                    $modified = $true
                    Log-Message "Fixed package declaration in $($file.Name) from $originalPackage to $newPackage" "FIXED"
                }
            }
        }
        
        # Fix import statements
        $oldImportPatterns = @(
            "com\.exalt\.courier(?!services)",
            "com\.microecosystem\.courier",
            "com\.socialecommerceecosystem",
            "org\.exalt\.courier",
            "io\.exalt\.courier"
        )
        
        foreach ($pattern in $oldImportPatterns) {
            if ($content -match "import\s+$pattern") {
                $oldImport = $matches[0]
                $newImport = $oldImport -replace $pattern, "com.exalt.courierservices"
                $content = $content -replace [regex]::Escape($oldImport), $newImport
                $modified = $true
                Log-Message "Fixed import in $($file.Name): $oldImport -> $newImport" "FIXED"
            }
        }
        
        # Save the file if modified
        if ($modified) {
            Set-Content -Path $file.FullName -Value $content -NoNewline
        }
    }
    
    # Now let's also update folder structure to match package structure
    $javaSourceDir = Join-Path -Path $serviceDir -ChildPath "src\main\java"
    $javaTestDir = Join-Path -Path $serviceDir -ChildPath "src\test\java"
    
    # Create the correct package directory structure if it doesn't exist
    $correctPackagePath = Join-Path -Path $javaSourceDir -ChildPath "com\exalt\courierservices\tracking"
    if (-not (Test-Path $correctPackagePath)) {
        New-Item -ItemType Directory -Path $correctPackagePath -Force | Out-Null
        Log-Message "Created directory structure for correct package path" "CREATE"
    }
    
    $correctTestPackagePath = Join-Path -Path $javaTestDir -ChildPath "com\exalt\courierservices\tracking"
    if (-not (Test-Path $correctTestPackagePath)) {
        New-Item -ItemType Directory -Path $correctTestPackagePath -Force | Out-Null
        Log-Message "Created directory structure for correct test package path" "CREATE"
    }
    
    # Find Java files in incorrect package structures and move them
    $oldPackagePatterns = @(
        "com\exalt\courier",
        "com\microecosystem",
        "org\exalt",
        "io\exalt"
    )
    
    foreach ($pattern in $oldPackagePatterns) {
        $oldDirs = Get-ChildItem -Path $javaSourceDir -Filter $pattern.Split('\')[0] -Directory -Recurse -ErrorAction SilentlyContinue
        
        foreach ($dir in $oldDirs) {
            # Move files to new structure
            $javaFilesInOldDir = Get-ChildItem -Path $dir.FullName -Filter "*.java" -Recurse
            
            foreach ($javaFile in $javaFilesInOldDir) {
                $content = Get-Content -Path $javaFile.FullName -Raw
                
                # Determine target package from package declaration
                if ($content -match "package\s+(com\.exalt\.courierservices\.[^;]+);") {
                    $packagePath = $matches[1].Replace(".", "\")
                    $targetDir = Join-Path -Path $javaSourceDir -ChildPath $packagePath
                    
                    # Create target directory if it doesn't exist
                    if (-not (Test-Path $targetDir)) {
                        New-Item -ItemType Directory -Path $targetDir -Force | Out-Null
                    }
                    
                    # Move the file
                    $targetFile = Join-Path -Path $targetDir -ChildPath $javaFile.Name
                    if (-not (Test-Path $targetFile)) {
                        Copy-Item -Path $javaFile.FullName -Destination $targetFile -Force
                        Log-Message "Moved $($javaFile.Name) to correct package directory" "MOVE"
                    }
                }
            }
        }
    }
}

Log-Message "Package naming fix completed" "DONE"
