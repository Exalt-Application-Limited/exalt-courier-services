# Script to set up git hooks for courier services repositories
# Implements pre-commit and pre-push hooks to prevent regression

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

$baseDir = "C:\Users\frich\Desktop\Exalt-Application-Limited\Exalt-Application-Limited\social-ecommerce-ecosystem\courier-services"
Set-Location $baseDir

# Create pre-commit hook for Java services
$javaPreCommitHook = @'
#!/bin/sh
# Pre-commit hook for Java services
set -e

echo "Running Java pre-commit checks..."

# Check for current Java service
if [ -f "pom.xml" ]; then
    echo "Java service detected, running checks..."

    # Get service name from directory
    SERVICE_NAME=$(basename $(pwd))
    echo "Service: $SERVICE_NAME"

    # Verify code formatting
    echo "Checking code formatting..."
    ./mvnw spotless:check || { 
        echo "Error: Code formatting issues found. Run './mvnw spotless:apply' to fix automatically."
        exit 1
    }

    # Run unit tests (quick tests only)
    echo "Running unit tests..."
    ./mvnw test -DskipITs || {
        echo "Error: Unit tests failed. Fix failing tests before committing."
        exit 1
    }

    # Run checkstyle
    echo "Running checkstyle..."
    ./mvnw checkstyle:check || {
        echo "Error: Checkstyle violations found. Please fix code style issues."
        exit 1
    }

    echo "All Java pre-commit checks passed!"
fi

exit 0
'@

# Create pre-commit hook for Node.js services
$nodePreCommitHook = @'
#!/bin/sh
# Pre-commit hook for Node.js services
set -e

echo "Running Node.js pre-commit checks..."

# Check for current Node.js service
if [ -f "package.json" ]; then
    echo "Node.js service detected, running checks..."

    # Get service name from directory
    SERVICE_NAME=$(basename $(pwd))
    echo "Service: $SERVICE_NAME"

    # Determine package manager (npm or yarn)
    if [ -f "yarn.lock" ]; then
        PM="yarn"
    else
        PM="npm"
    fi
    echo "Using package manager: $PM"

    # Run linting
    echo "Running ESLint..."
    if [ "$PM" = "yarn" ]; then
        yarn lint || {
            echo "Error: ESLint issues found. Run 'yarn lint --fix' to attempt automatic fixes."
            exit 1
        }
    else
        npm run lint || {
            echo "Error: ESLint issues found. Run 'npm run lint -- --fix' to attempt automatic fixes."
            exit 1
        }
    fi

    # Run unit tests (quick tests only)
    echo "Running unit tests..."
    if [ "$PM" = "yarn" ]; then
        yarn test || {
            echo "Error: Tests failed. Fix failing tests before committing."
            exit 1
        }
    else
        npm test || {
            echo "Error: Tests failed. Fix failing tests before committing."
            exit 1
        }
    fi

    echo "All Node.js pre-commit checks passed!"
fi

exit 0
'@

# Create pre-push hook common for all services
$prePushHook = @'
#!/bin/sh
# Pre-push hook for all courier services
set -e

echo "Running pre-push validation checks..."

# Get current branch name
BRANCH_NAME=$(git symbolic-ref --short HEAD 2>/dev/null)
echo "Branch: $BRANCH_NAME"

# Special handling for main/master branches
if [ "$BRANCH_NAME" = "main" ] || [ "$BRANCH_NAME" = "master" ]; then
    echo "Pushing to main/master branch - running full validation..."
    
    # For Java services
    if [ -f "pom.xml" ]; then
        echo "Running full test suite for Java service..."
        ./mvnw verify || {
            echo "Error: Full test suite failed. Cannot push to main/master."
            exit 1
        }
        
        echo "Running JaCoCo coverage check..."
        ./mvnw jacoco:check || {
            echo "Error: Test coverage below threshold. Increase coverage before pushing to main/master."
            exit 1
        }
    fi
    
    # For Node.js services
    if [ -f "package.json" ]; then
        # Determine package manager
        if [ -f "yarn.lock" ]; then
            PM="yarn"
        else
            PM="npm"
        fi
        
        echo "Running full test suite for Node.js service..."
        if [ "$PM" = "yarn" ]; then
            yarn test:coverage || {
                echo "Error: Full test suite with coverage failed. Cannot push to main/master."
                exit 1
            }
        else
            npm run test:coverage || {
                echo "Error: Full test suite with coverage failed. Cannot push to main/master."
                exit 1
            }
        fi
    fi
fi

echo "All pre-push checks passed!"
exit 0
'@

# Function to install git hooks for a service
function Install-GitHooks {
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
    Log-Message "Installing git hooks for $serviceName ($ServiceType)" "INFO"
    
    # Create .git/hooks directory if it doesn't exist
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
        
        # If still not found, create the directory
        if (-not (Test-Path $hooksDir)) {
            New-Item -ItemType Directory -Path $hooksDir -Force | Out-Null
            if (-not $?) {
                Log-Message "Failed to create hooks directory for $serviceName" "ERROR"
                return
            }
        }
    }
    
    # Create pre-commit hook
    $preCommitPath = Join-Path -Path $hooksDir -ChildPath "pre-commit"
    if ($ServiceType -eq "java") {
        Set-Content -Path $preCommitPath -Value $javaPreCommitHook -NoNewline
    } else {
        Set-Content -Path $preCommitPath -Value $nodePreCommitHook -NoNewline
    }
    
    # Create pre-push hook
    $prePushPath = Join-Path -Path $hooksDir -ChildPath "pre-push"
    Set-Content -Path $prePushPath -Value $prePushHook -NoNewline
    
    # Ensure hooks are executable on Unix-like systems
    if ($IsLinux -or $IsMacOS) {
        chmod +x $preCommitPath
        chmod +x $prePushPath
    } else {
        # For Windows, use git update-index
        Push-Location $ServicePath
        git update-index --chmod=+x .git/hooks/pre-commit
        git update-index --chmod=+x .git/hooks/pre-push
        Pop-Location
    }
    
    Log-Message "Git hooks installed successfully for $serviceName" "SUCCESS"
}

# Find all services in the courier-services directory
$services = Get-ChildItem -Path $baseDir -Directory | Where-Object {
    $dir = $_.FullName
    (Test-Path (Join-Path -Path $dir -ChildPath "pom.xml")) -or
    (Test-Path (Join-Path -Path $dir -ChildPath "package.json"))
}

# Install hooks for each service
foreach ($service in $services) {
    $servicePath = $service.FullName
    
    # Determine service type
    if (Test-Path (Join-Path -Path $servicePath -ChildPath "pom.xml")) {
        Install-GitHooks -ServicePath $servicePath -ServiceType "java"
    } elseif (Test-Path (Join-Path -Path $servicePath -ChildPath "package.json")) {
        Install-GitHooks -ServicePath $servicePath -ServiceType "node"
    } else {
        Log-Message "Unknown service type for $($service.Name), skipping" "WARNING"
    }
}

Log-Message "Git hooks setup completed" "SUCCESS"
Log-Message "Pre-commit hooks will verify tests and formatting before commits" "INFO"
Log-Message "Pre-push hooks will enforce additional checks for main/master branches" "INFO"
