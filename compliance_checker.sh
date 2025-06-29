#!/bin/bash

# Courier Services Compliance Checker
# Checks all services for required directory structure and files

BASE_DIR="/mnt/c/Users/frich/Desktop/Exalt-Application-Limited/CLEAN-SOCIAL-ECOMMERCE-ECOSYSTEM/courier-services"
REPORT_FILE="${BASE_DIR}/COMPLIANCE_REPORT.md"

# Define required files and directories
REQUIRED_FILES=(".env.template" ".gitignore" "Dockerfile" "README.md" "sonar-project.properties")
REQUIRED_DIRS=(".github/workflows" "k8s" "src" "tests")
JAVA_REQUIRED=("pom.xml" "src/main/java" "src/test/java" "src/main/resources/application.yml")
NODE_REQUIRED=("package.json" "src/index.js" "test")

# Service list (actual services only)
SERVICES=(
    "branch-courier-app"
    "commission-service"
    "corporate-admin"
    "courier-events-service"
    "courier-fare-calculator"
    "courier-geo-routing"
    "courier-location-tracker"
    "courier-management"
    "courier-management-service"
    "courier-network-locations"
    "courier-onboarding"
    "courier-pickup-engine"
    "courier-subscription-service"
    "courier-tracking-service"
    "driver-mobile-app"
    "global-hq-admin"
    "international-shipping-service"
    "notification-service"
    "payout-service"
    "regional-admin"
    "regional-admin-system"
    "regional-courier-service"
    "routing-service"
    "tracking-service"
    "user-mobile-app"
)

# Initialize report
echo "# Courier Services Compliance Report" > "$REPORT_FILE"
echo "Generated on: $(date)" >> "$REPORT_FILE"
echo "" >> "$REPORT_FILE"

check_service_compliance() {
    local service_name=$1
    local service_path="${BASE_DIR}/${service_name}"
    local compliance_score=0
    local total_checks=0
    local issues=()
    
    echo "## ${service_name}" >> "$REPORT_FILE"
    echo "" >> "$REPORT_FILE"
    
    if [ ! -d "$service_path" ]; then
        echo "❌ **CRITICAL**: Service directory does not exist" >> "$REPORT_FILE"
        echo "" >> "$REPORT_FILE"
        return
    fi
    
    # Check if it's a Java or Node.js service
    local is_java=false
    local is_node=false
    
    if [ -f "${service_path}/pom.xml" ]; then
        is_java=true
    fi
    
    if [ -f "${service_path}/package.json" ]; then
        is_node=true
    fi
    
    echo "**Service Type**: " >> "$REPORT_FILE"
    if [ "$is_java" = true ]; then
        echo "Java (Spring Boot)" >> "$REPORT_FILE"
    elif [ "$is_node" = true ]; then
        echo "Node.js" >> "$REPORT_FILE"
    else
        echo "Unknown/Undefined" >> "$REPORT_FILE"
    fi
    echo "" >> "$REPORT_FILE"
    
    # Check required files
    echo "### Required Files:" >> "$REPORT_FILE"
    for file in "${REQUIRED_FILES[@]}"; do
        total_checks=$((total_checks + 1))
        if [ -f "${service_path}/${file}" ]; then
            echo "✅ ${file}" >> "$REPORT_FILE"
            compliance_score=$((compliance_score + 1))
        else
            echo "❌ ${file}" >> "$REPORT_FILE"
            issues+=("Missing: ${file}")
        fi
    done
    
    # Check required directories
    echo "" >> "$REPORT_FILE"
    echo "### Required Directories:" >> "$REPORT_FILE"
    for dir in "${REQUIRED_DIRS[@]}"; do
        total_checks=$((total_checks + 1))
        if [ -d "${service_path}/${dir}" ]; then
            echo "✅ ${dir}/" >> "$REPORT_FILE"
            compliance_score=$((compliance_score + 1))
        else
            echo "❌ ${dir}/" >> "$REPORT_FILE"
            issues+=("Missing: ${dir}/")
        fi
    done
    
    # Java-specific checks
    if [ "$is_java" = true ]; then
        echo "" >> "$REPORT_FILE"
        echo "### Java-Specific Requirements:" >> "$REPORT_FILE"
        for req in "${JAVA_REQUIRED[@]}"; do
            total_checks=$((total_checks + 1))
            if [ -f "${service_path}/${req}" ] || [ -d "${service_path}/${req}" ]; then
                echo "✅ ${req}" >> "$REPORT_FILE"
                compliance_score=$((compliance_score + 1))
            else
                echo "❌ ${req}" >> "$REPORT_FILE"
                issues+=("Missing: ${req}")
            fi
        done
    fi
    
    # Node.js-specific checks
    if [ "$is_node" = true ]; then
        echo "" >> "$REPORT_FILE"
        echo "### Node.js-Specific Requirements:" >> "$REPORT_FILE"
        for req in "${NODE_REQUIRED[@]}"; do
            total_checks=$((total_checks + 1))
            if [ -f "${service_path}/${req}" ] || [ -d "${service_path}/${req}" ]; then
                echo "✅ ${req}" >> "$REPORT_FILE"
                compliance_score=$((compliance_score + 1))
            else
                echo "❌ ${req}" >> "$REPORT_FILE"
                issues+=("Missing: ${req}")
            fi
        done
    fi
    
    # Calculate compliance percentage
    local compliance_percentage=$((compliance_score * 100 / total_checks))
    
    echo "" >> "$REPORT_FILE"
    echo "**Compliance Score**: ${compliance_score}/${total_checks} (${compliance_percentage}%)" >> "$REPORT_FILE"
    
    if [ ${#issues[@]} -gt 0 ]; then
        echo "" >> "$REPORT_FILE"
        echo "### Issues Found:" >> "$REPORT_FILE"
        for issue in "${issues[@]}"; do
            echo "- ${issue}" >> "$REPORT_FILE"
        done
    fi
    
    echo "" >> "$REPORT_FILE"
    echo "---" >> "$REPORT_FILE"
    echo "" >> "$REPORT_FILE"
}

# Main execution
echo "Starting compliance check for ${#SERVICES[@]} services..."

for service in "${SERVICES[@]}"; do
    echo "Checking: $service"
    check_service_compliance "$service"
done

echo "Compliance report generated: $REPORT_FILE"