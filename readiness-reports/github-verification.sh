#!/bin/bash

# GitHub Repository Verification Script
# Run this script to verify all 103 repositories in your organization

echo "🔍 GITHUB REPOSITORY VERIFICATION SCRIPT"
echo "Organization: Micro-Services-Social-Ecommerce-App"
echo "======================================================="

# Configuration
ORG_NAME="Micro-Services-Social-Ecommerce-App"
OUTPUT_FILE="repository_verification_report.md"

# Initialize report
cat > $OUTPUT_FILE << EOF
# GitHub Repository Verification Report
**Date:** $(date)
**Organization:** $ORG_NAME
**Total Repositories:** 103

## Verification Results

EOF

# Function to check repository readiness
check_repository() {
    local repo_name=$1
    local repo_url="https://github.com/$ORG_NAME/$repo_name"
    
    echo "📋 Checking: $repo_name"
    
    # Clone repository temporarily for analysis
    if git clone $repo_url temp_$repo_name 2>/dev/null; then
        cd temp_$repo_name
        
        # Check essential files
        local score=0
        local total=10
        local issues=()
        
        # Check for README
        if [ -f "README.md" ] || [ -f "readme.md" ]; then
            ((score++))
        else
            issues+=("Missing README.md")
        fi
        
        # Check for build configuration
        if [ -f "pom.xml" ] || [ -f "package.json" ] || [ -f "build.gradle" ]; then
            ((score++))
        else
            issues+=("Missing build configuration")
        fi
        
        # Check for Dockerfile
        if [ -f "Dockerfile" ]; then
            ((score++))
        else
            issues+=("Missing Dockerfile")
        fi
        
        # Check for .gitignore
        if [ -f ".gitignore" ]; then
            ((score++))
        else
            issues+=("Missing .gitignore")
        fi
        
        # Check for source directory
        if [ -d "src" ]; then
            ((score++))
        else
            issues+=("Missing src directory")
        fi
        
        # Check for CI/CD configuration
        if [ -d ".github/workflows" ]; then
            ((score++))
        else
            issues+=("Missing GitHub Actions workflows")
        fi
        
        # Check for documentation
        if [ -d "docs" ] || [ -d "documentation" ]; then
            ((score++))
        else
            issues+=("Missing documentation directory")
        fi
        
        # Check for configuration files
        if [ -f "application.yml" ] || [ -f "application.properties" ] || [ -f "config.json" ]; then
            ((score++))
        else
            issues+=("Missing configuration files")
        fi
        
        # Check for tests
        if [ -d "test" ] || [ -d "tests" ] || find . -name "*Test.java" -o -name "*.test.js" | head -1 | read; then
            ((score++))
        else
            issues+=("Missing tests")
        fi
        
        # Check for API documentation
        if [ -d "api-docs" ] || [ -f "openapi.yaml" ] || [ -f "swagger.json" ]; then
            ((score++))
        else
            issues+=("Missing API documentation")
        fi
        
        # Calculate percentage
        local percentage=$((score * 100 / total))
        
        # Determine status
        local status
        if [ $percentage -ge 90 ]; then
            status="✅ READY"
        elif [ $percentage -ge 70 ]; then
            status="🔄 MOSTLY READY"
        elif [ $percentage -ge 50 ]; then
            status="⚠️ NEEDS WORK"
        else
            status="🚨 CRITICAL"
        fi
        
        # Check branch structure
        local branches=$(git branch -a | grep -E "(main|master|develop)" | wc -l)
        local branch_status="✅ Good"
        if [ $branches -lt 2 ]; then
            branch_status="⚠️ Limited branches"
            issues+=("Limited branch structure")
        fi
        
        # Write to report
        cat >> ../$OUTPUT_FILE << EOF
### $repo_name [$status - $percentage%]
- **Branches:** $branch_status
- **Score:** $score/$total
- **Issues:** ${#issues[@]}
EOF
        
        if [ ${#issues[@]} -gt 0 ]; then
            echo "  - **Problems found:**" >> ../$OUTPUT_FILE
            for issue in "${issues[@]}"; do
                echo "    - $issue" >> ../$OUTPUT_FILE
            done
        fi
        
        echo "" >> ../$OUTPUT_FILE
        
        cd ..
        rm -rf temp_$repo_name
        
        echo "  Status: $status ($percentage%)"
        if [ ${#issues[@]} -gt 0 ]; then
            echo "  Issues: ${issues[*]}"
        fi
        echo ""
        
    else
        echo "  ❌ Failed to clone repository"
        echo "### $repo_name [❌ FAILED]" >> $OUTPUT_FILE
        echo "- **Error:** Failed to clone repository" >> $OUTPUT_FILE
        echo "" >> $OUTPUT_FILE
    fi
}

# Get list of repositories using GitHub CLI (if available)
if command -v gh &> /dev/null; then
    echo "📡 Fetching repository list using GitHub CLI..."
    
    # Login check
    if ! gh auth status &> /dev/null; then
        echo "❌ Please login to GitHub CLI first: gh auth login"
        exit 1
    fi
    
    # Get repositories
    repos=$(gh repo list $ORG_NAME --limit 200 --json name -q '.[].name')
    
    echo "Found repositories:"
    echo "$repos" | head -10
    if [ $(echo "$repos" | wc -l) -gt 10 ]; then
        echo "... and $(($(echo "$repos" | wc -l) - 10)) more"
    fi
    echo ""
    
    # Check each repository
    total_repos=$(echo "$repos" | wc -l)
    current=0
    
    for repo in $repos; do
        ((current++))
        echo "[$current/$total_repos] Processing $repo..."
        check_repository $repo
    done
    
else
    echo "❌ GitHub CLI not found. Please install it: https://cli.github.com/"
    echo "Or manually provide repository list below."
    exit 1
fi

# Generate summary
cat >> $OUTPUT_FILE << EOF

## Summary Statistics

**Verification completed:** $(date)
**Total repositories checked:** $total_repos

EOF

echo ""
echo "✅ Verification completed!"
echo "📄 Report saved to: $OUTPUT_FILE"
echo ""
echo "📋 Next steps:"
echo "1. Review the report for any critical issues"
echo "2. Address repositories marked as 'CRITICAL' or 'NEEDS WORK'"
echo "3. Ensure all repositories have proper branch protection"
echo "4. Verify CI/CD workflows are functional"
