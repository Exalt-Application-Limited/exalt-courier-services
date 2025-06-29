#!/bin/bash

# Courier Services Ecosystem - Automated Readiness Verification Script
# This script checks all components for essential files and structure

echo "🔍 COURIER SERVICES ECOSYSTEM - READINESS VERIFICATION"
echo "======================================================"
echo "Verification Date: $(date)"
echo ""

BASE_PATH="C:/Users/frich/Desktop/Micro-Social-Ecommerce-Ecosystems/social-ecommerce-ecosystem/courier-services"
REPORT_PATH="$BASE_PATH/readiness-reports"

# Create arrays to track different readiness levels
READY_COMPONENTS=()
MOSTLY_READY_COMPONENTS=()
NEEDS_WORK_COMPONENTS=()
CRITICAL_COMPONENTS=()

# Function to check if file exists
check_file() {
    local component=$1
    local file=$2
    if [ -f "$BASE_PATH/$component/$file" ]; then
        echo "  ✅ $file"
        return 0
    else
        echo "  ❌ $file"
        return 1
    fi
}

# Function to check if directory exists
check_dir() {
    local component=$1
    local dir=$2
    if [ -d "$BASE_PATH/$component/$dir" ]; then
        echo "  ✅ $dir/"
        return 0
    else
        echo "  ❌ $dir/"
        return 1
    fi
}

# Function to verify component readiness
verify_component() {
    local component=$1
    echo ""
    echo "🏗️  Verifying: $component"
    echo "----------------------------------------"
    
    local score=0
    local total=12
    
    # Check essential files
    check_file "$component" "README.md" && ((score++))
    check_file "$component" "pom.xml" && ((score++)) || (check_file "$component" "package.json" && ((score++)))
    check_file "$component" "Dockerfile" && ((score++))
    check_file "$component" "docker-compose.yml" && ((score++))
    
    # Check essential directories
    check_dir "$component" "src" && ((score++))
    check_dir "$component" "docs" && ((score++))
    check_dir "$component" "api-docs" && ((score++))
    check_dir "$component" ".github" && ((score++))
    
    # Check source structure (for Java projects)
    if [ -d "$BASE_PATH/$component/src/main/java" ]; then
        echo "  ✅ src/main/java/"
        ((score++))
    else
        echo "  ❌ src/main/java/"
    fi
    
    if [ -d "$BASE_PATH/$component/src/test/java" ]; then
        echo "  ✅ src/test/java/"
        ((score++))
    else
        echo "  ❌ src/test/java/"
    fi
    
    # Check configuration
    if [ -f "$BASE_PATH/$component/src/main/resources/application.yml" ] || [ -f "$BASE_PATH/$component/src/main/resources/application.properties" ]; then
        echo "  ✅ application config"
        ((score++))
    else
        echo "  ❌ application config"
    fi
    
    # Check for main application class
    if find "$BASE_PATH/$component/src/main/java" -name "*Application.java" 2>/dev/null | head -1 | read; then
        echo "  ✅ Application.java"
        ((score++))
    else
        echo "  ❌ Application.java"
    fi
    
    # Calculate percentage
    local percentage=$((score * 100 / total))
    echo ""
    echo "📊 Score: $score/$total ($percentage%)"
    
    # Categorize component
    if [ $percentage -ge 90 ]; then
        echo "🟢 Status: READY FOR GITHUB"
        READY_COMPONENTS+=("$component")
    elif [ $percentage -ge 70 ]; then
        echo "🟡 Status: MOSTLY READY"
        MOSTLY_READY_COMPONENTS+=("$component")
    elif [ $percentage -ge 50 ]; then
        echo "🟠 Status: NEEDS WORK"
        NEEDS_WORK_COMPONENTS+=("$component")
    else
        echo "🔴 Status: CRITICAL - MAJOR WORK NEEDED"
        CRITICAL_COMPONENTS+=("$component")
    fi
}

# Get list of directories (components) to verify
echo "📋 Scanning for components..."
COMPONENTS=()
for dir in "$BASE_PATH"/*; do
    if [ -d "$dir" ] && [[ "$(basename "$dir")" != "readiness-reports" ]]; then
        COMPONENTS+=("$(basename "$dir")")
    fi
done

echo "Found ${#COMPONENTS[@]} components to verify"

# Verify each component
for component in "${COMPONENTS[@]}"; do
    verify_component "$component"
done

# Generate summary report
echo ""
echo ""
echo "📊 VERIFICATION SUMMARY REPORT"
echo "=============================="
echo ""

echo "🟢 READY FOR GITHUB (${#READY_COMPONENTS[@]} components):"
for component in "${READY_COMPONENTS[@]}"; do
    echo "  ✅ $component"
done
echo ""

echo "🟡 MOSTLY READY (${#MOSTLY_READY_COMPONENTS[@]} components):"
for component in "${MOSTLY_READY_COMPONENTS[@]}"; do
    echo "  🔄 $component"
done
echo ""

echo "🟠 NEEDS WORK (${#NEEDS_WORK_COMPONENTS[@]} components):"
for component in "${NEEDS_WORK_COMPONENTS[@]}"; do
    echo "  ⚠️  $component"
done
echo ""

echo "🔴 CRITICAL ISSUES (${#CRITICAL_COMPONENTS[@]} components):"
for component in "${CRITICAL_COMPONENTS[@]}"; do
    echo "  🚨 $component"
done
echo ""

# Calculate overall statistics
TOTAL_COMPONENTS=${#COMPONENTS[@]}
READY_COUNT=${#READY_COMPONENTS[@]}
MOSTLY_READY_COUNT=${#MOSTLY_READY_COMPONENTS[@]}
DEPLOYABLE_COUNT=$((READY_COUNT + MOSTLY_READY_COUNT))

OVERALL_READINESS=$((DEPLOYABLE_COUNT * 100 / TOTAL_COMPONENTS))

echo "📈 OVERALL STATISTICS:"
echo "  Total Components: $TOTAL_COMPONENTS"
echo "  Ready for GitHub: $READY_COUNT ($((READY_COUNT * 100 / TOTAL_COMPONENTS))%)"
echo "  Mostly Ready: $MOSTLY_READY_COUNT ($((MOSTLY_READY_COUNT * 100 / TOTAL_COMPONENTS))%)"
echo "  Deployable Soon: $DEPLOYABLE_COUNT ($OVERALL_READINESS%)"
echo ""

echo "🎯 RECOMMENDATIONS:"
if [ $READY_COUNT -gt 0 ]; then
    echo "  ✅ Begin GitHub migration with $READY_COUNT ready components"
fi
if [ $MOSTLY_READY_COUNT -gt 0 ]; then
    echo "  🔄 Complete work on $MOSTLY_READY_COUNT mostly ready components (1-2 days)"
fi
if [ ${#NEEDS_WORK_COMPONENTS[@]} -gt 0 ]; then
    echo "  ⚠️  Address ${#NEEDS_WORK_COMPONENTS[@]} components needing work (3-5 days)"
fi
if [ ${#CRITICAL_COMPONENTS[@]} -gt 0 ]; then
    echo "  🚨 Priority fix needed for ${#CRITICAL_COMPONENTS[@]} critical components (1-2 weeks)"
fi

echo ""
echo "✅ Verification completed. See detailed reports in: $REPORT_PATH/"
echo "📅 Next verification recommended: $(date -d '+1 week')"
