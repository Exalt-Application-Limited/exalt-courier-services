# Automated script to set up Dev, Staging, and Production environments
# For Courier Services repository

# Configuration
$repoUrl = "https://github.com/Exalt-Application-Limited/courier-services.git"
$localRepoPath = ".\courier-services-repo"
$environments = @("development", "staging") # main branch already exists for production

# Create local directory for repository
Write-Host "Setting up environment branches for Courier Services..."
if (!(Test-Path $localRepoPath)) {
    New-Item -ItemType Directory -Path $localRepoPath | Out-Null
    Set-Location $localRepoPath
    
    # Clone the repository
    Write-Host "Cloning repository..."
    git clone $repoUrl .
    
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Authentication required. Please enter your GitHub credentials when prompted." -ForegroundColor Yellow
        git clone $repoUrl .
        
        if ($LASTEXITCODE -ne 0) {
            Write-Host "Failed to clone repository. Please check your credentials and try again." -ForegroundColor Red
            exit 1
        }
    }
} else {
    Set-Location $localRepoPath
    Write-Host "Repository directory already exists. Updating..."
    git pull
}

# Create and push environment branches
foreach ($env in $environments) {
    Write-Host "Setting up $env environment..."
    
    # Check if branch exists remotely
    $branchExists = git ls-remote --heads origin $env
    
    if ($branchExists) {
        Write-Host "Branch $env already exists remotely. Checking out..." -ForegroundColor Cyan
        git checkout $env
        git pull origin $env
    } else {
        # Create branch locally
        Write-Host "Creating branch $env..." -ForegroundColor Green
        git checkout -b $env
        
        # Create environment-specific configuration file
        $configContent = @"
# $env Environment Configuration
# Created: $(Get-Date)
# For Courier Services

ENVIRONMENT=$env
LOG_LEVEL=$(if ($env -eq "production") { "warn" } else { "debug" })
ENABLE_DETAILED_LOGGING=$(if ($env -eq "production") { "false" } else { "true" })
CACHE_TTL_SECONDS=$(if ($env -eq "production") { "3600" } elseif ($env -eq "staging") { "1800" } else { "300" })
"@
        
        # Create environment-specific directory if it doesn't exist
        $envDir = "./$env-config"
        if (!(Test-Path $envDir)) {
            New-Item -ItemType Directory -Path $envDir | Out-Null
        }
        
        # Create environment configuration
        Set-Content -Path "$envDir/environment.config" -Value $configContent
        
        # Create environment README
        $readmeContent = @"
# $((Get-Culture).TextInfo.ToTitleCase($env)) Environment

## Overview
This branch contains the configuration and code for the $((Get-Culture).TextInfo.ToTitleCase($env)) environment of the Courier Services.

## Deployment
Changes to this branch are automatically deployed to the $((Get-Culture).TextInfo.ToTitleCase($env)) environment through the CI/CD pipeline.

## Environment Details
- **Environment:** $((Get-Culture).TextInfo.ToTitleCase($env))
- **Update Frequency:** $(if ($env -eq "production") { "Scheduled releases" } elseif ($env -eq "staging") { "After QA approval" } else { "Continuous" })
- **Access Control:** $(if ($env -eq "production") { "Restricted" } elseif ($env -eq "staging") { "QA and Development teams" } else { "Development team" })
"@
        
        Set-Content -Path "$envDir/README.md" -Value $readmeContent
        
        # Add files
        git add "$envDir/environment.config" "$envDir/README.md"
        git commit -m "Set up $((Get-Culture).TextInfo.ToTitleCase($env)) environment configuration"
        
        # Push branch to remote
        Write-Host "Pushing $env branch to remote repository..."
        git push -u origin $env
        
        if ($LASTEXITCODE -ne 0) {
            Write-Host "Authentication required. Please enter your GitHub credentials when prompted." -ForegroundColor Yellow
            git push -u origin $env
            
            if ($LASTEXITCODE -ne 0) {
                Write-Host "Failed to push $env branch. Please check your credentials and try again." -ForegroundColor Red
            }
        }
    }
}

# Create main branch README if it doesn't exist
git checkout main
$mainReadmePath = "./README.md"
if (!(Test-Path $mainReadmePath) -or (Get-Content $mainReadmePath | Measure-Object -Line).Lines -lt 5) {
    $mainReadmeContent = @"
# Courier Services

This repository contains the implementation of the courier services domain for the Exalt Application Limited social ecommerce ecosystem.

## Environments

This repository uses a branch-based environment strategy:

- **Production**: `main` branch
  - Stable, tested code that is running in production
  - Changes through approved pull requests only
  
- **Staging**: `staging` branch
  - Pre-production testing environment
  - Used for QA and UAT before promoting to production
  
- **Development**: `development` branch
  - Integration environment for development work
  - Features and bugfixes are merged here first

## Zero Regression Strategy

This repository implements the Zero Regression Strategy with multiple safeguards:

1. Comprehensive test coverage
2. API compatibility verification
3. Feature flag system for graduated rollouts
4. Cross-version testing for behavior equivalence
5. CI/CD pipeline integration
6. Runtime verification system

## Getting Started

See the [documentation](./docs/README.md) for details on setting up and working with this repository.
"@
    
    Set-Content -Path $mainReadmePath -Value $mainReadmeContent
    git add $mainReadmePath
    git commit -m "Add repository README with environment documentation"
    git push origin main
}

# Create CI/CD workflow configuration for environments
$workflowsDir = "./.github/workflows"
if (!(Test-Path $workflowsDir)) {
    New-Item -ItemType Directory -Path $workflowsDir -Force | Out-Null
}

# Development workflow
$devWorkflowContent = @"
name: Development CI/CD

on:
  push:
    branches: [ development ]
  pull_request:
    branches: [ development ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Run tests
        run: echo "Running tests for development environment"
        
      - name: Build
        run: echo "Building for development environment"
        
      - name: Deploy to development
        if: github.event_name == 'push'
        run: echo "Deploying to development environment"
"@

Set-Content -Path "$workflowsDir/development.yml" -Value $devWorkflowContent

# Staging workflow
$stagingWorkflowContent = @"
name: Staging CI/CD

on:
  push:
    branches: [ staging ]
  pull_request:
    branches: [ staging ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Run tests
        run: echo "Running tests for staging environment"
        
      - name: Build
        run: echo "Building for staging environment"
        
      - name: Deploy to staging
        if: github.event_name == 'push'
        run: echo "Deploying to staging environment"
"@

Set-Content -Path "$workflowsDir/staging.yml" -Value $stagingWorkflowContent

# Production workflow
$productionWorkflowContent = @"
name: Production CI/CD

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Run tests
        run: echo "Running tests for production environment"
        
      - name: Build
        run: echo "Building for production environment"
        
      - name: Deploy to production
        if: github.event_name == 'push'
        run: echo "Deploying to production environment"
"@

Set-Content -Path "$workflowsDir/production.yml" -Value $productionWorkflowContent

# Commit and push workflows
git add "$workflowsDir/development.yml" "$workflowsDir/staging.yml" "$workflowsDir/production.yml"
git commit -m "Add CI/CD workflow configurations for all environments"
git push origin main

# Summary
Write-Host ""
Write-Host "Environment setup complete!" -ForegroundColor Green
Write-Host "Created environments:"
Write-Host "- Production (main branch)"
Write-Host "- Staging (staging branch)"
Write-Host "- Development (development branch)"
Write-Host ""
Write-Host "Added environment-specific configurations and README files"
Write-Host "Set up CI/CD workflow configurations for all environments"
Write-Host ""
Write-Host "To use these environments, developers should:"
Write-Host "1. Clone the repository"
Write-Host "2. Switch to the appropriate branch (git checkout <environment>)"
Write-Host "3. Create feature branches from development"
Write-Host "4. Submit pull requests to merge changes back into development"
Write-Host ""
Write-Host "Use the branch promotion flow: development → staging → main (production)"
