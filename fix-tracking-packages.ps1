# Script to specifically fix courier-tracking-service package structure
# This script will consolidate all Java files into the correct com.exalt.courierservices.tracking package

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
$service = "courier-tracking-service"
$serviceDir = Join-Path -Path $baseDir -ChildPath $service
$javaDir = Join-Path -Path $serviceDir -ChildPath "src\main\java"

Log-Message "Starting specific fix for courier-tracking-service package structure" "START"

# Define the source and target package paths
$sourcePackagePaths = @(
    "com\exalt\courier\tracking",
    "com\exalt\shared",
    "com\socialecommerceecosystem\tracking"
)
$targetPackagePath = "com\exalt\courierservices\tracking"
$targetPackagePathFull = Join-Path -Path $javaDir -ChildPath $targetPackagePath

# Create the target directory structure if it doesn't exist
if (-not (Test-Path $targetPackagePathFull)) {
    New-Item -ItemType Directory -Path $targetPackagePathFull -Force | Out-Null
    Log-Message "Created target package directory: $targetPackagePath" "CREATE"
}

# Find all subdirectories in source packages to recreate in target
foreach ($sourcePath in $sourcePackagePaths) {
    $sourcePathFull = Join-Path -Path $javaDir -ChildPath $sourcePath
    if (Test-Path $sourcePathFull) {
        $subdirs = Get-ChildItem -Path $sourcePathFull -Directory -Recurse
        
        foreach ($subdir in $subdirs) {
            # Create corresponding directory in target structure
            $relativePath = $subdir.FullName.Substring($sourcePathFull.Length)
            $targetSubdirPath = Join-Path -Path $targetPackagePathFull -ChildPath $relativePath
            
            if (-not (Test-Path $targetSubdirPath)) {
                New-Item -ItemType Directory -Path $targetSubdirPath -Force | Out-Null
                Log-Message "Created subdirectory: $($subdir.Name) in target package" "CREATE"
            }
        }
    }
}

# Move and fix all Java files
foreach ($sourcePath in $sourcePackagePaths) {
    $sourcePathFull = Join-Path -Path $javaDir -ChildPath $sourcePath
    if (Test-Path $sourcePathFull) {
        $javaFiles = Get-ChildItem -Path $sourcePathFull -Filter "*.java" -Recurse
        
        foreach ($file in $javaFiles) {
            # Read file content
            $content = Get-Content -Path $file.FullName -Raw
            
            # Determine the relative path from source package root
            $relativeFilePath = $file.FullName.Substring($sourcePathFull.Length)
            $targetFilePath = Join-Path -Path $targetPackagePathFull -ChildPath $relativeFilePath
            $targetDirPath = Split-Path -Path $targetFilePath -Parent
            
            # Make sure target directory exists
            if (-not (Test-Path $targetDirPath)) {
                New-Item -ItemType Directory -Path $targetDirPath -Force | Out-Null
            }
            
            # Extract old package name from file
            $oldPackage = ""
            if ($content -match "package\s+([\w\.]+);") {
                $oldPackage = $matches[1]
                
                # Determine new package based on file location
                $newPackage = "com.exalt.courierservices.tracking"
                if ($relativeFilePath -match "\\([^\\]+)\\") {
                    $subpackage = $matches[1]
                    if ($subpackage -ne "tracking") {
                        $newPackage += ".$subpackage"
                    }
                }
                
                # Update package declaration
                $content = $content -replace "package\s+$oldPackage;", "package $newPackage;"
                
                # Fix import statements
                $oldImportBase = $oldPackage -replace "\.[^\.]+$", ""
                $newImportBase = $newPackage -replace "\.[^\.]+$", ""
                if ($oldImportBase -ne $newImportBase) {
                    $content = $content -replace "import\s+$oldImportBase", "import $newImportBase"
                }
                
                # Special case for shared models
                if ($sourcePath -eq "com\exalt\shared") {
                    $content = $content -replace "import\s+com\.exalt\.shared", "import com.exalt.courierservices.tracking.shared"
                }
            }
            
            # Save to new location
            Set-Content -Path $targetFilePath -Value $content -NoNewline
            Log-Message "Moved and fixed $($file.Name) to correct package structure" "MOVE"
        }
    }
}

# Update imports in all Java files to reflect new package structure
$allJavaFiles = Get-ChildItem -Path $targetPackagePathFull -Filter "*.java" -Recurse
foreach ($file in $allJavaFiles) {
    $content = Get-Content -Path $file.FullName -Raw
    $modified = $false
    
    # Fix remaining imports
    $oldImportPatterns = @(
        "import\s+com\.exalt\.courier\.tracking",
        "import\s+com\.exalt\.shared",
        "import\s+com\.socialecommerceecosystem\.tracking"
    )
    
    $newImportPatterns = @(
        "import com.exalt.courierservices.tracking",
        "import com.exalt.courierservices.tracking.shared",
        "import com.exalt.courierservices.tracking"
    )
    
    for ($i = 0; $i -lt $oldImportPatterns.Length; $i++) {
        if ($content -match $oldImportPatterns[$i]) {
            $content = $content -replace $oldImportPatterns[$i], $newImportPatterns[$i]
            $modified = $true
        }
    }
    
    if ($modified) {
        Set-Content -Path $file.FullName -Value $content -NoNewline
        Log-Message "Updated imports in $($file.Name)" "UPDATE"
    }
}

Log-Message "Courier-tracking-service package structure fix completed" "DONE"
