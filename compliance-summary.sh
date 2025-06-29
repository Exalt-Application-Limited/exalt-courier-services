#!/bin/bash

# Script to verify compliance structure for all courier services

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "=== Courier Services Compliance Check ==="
echo

check_service_compliance() {
    local service=$1
    local service_type=$2
    
    echo "Checking $service..."
    
    # Check required directories
    local missing_dirs=""
    for dir in ".github/workflows" "tests" "k8s" "api-docs" "docs" "scripts" "i18n"; do
        if [ ! -d "$service/$dir" ]; then
            missing_dirs="$missing_dirs $dir"
        fi
    done
    
    # Check database directories for Java services
    if [ "$service_type" = "java" ] && [ ! -d "$service/database" ]; then
        missing_dirs="$missing_dirs database"
    fi
    
    # Check required files
    local missing_files=""
    for file in "README.md" "Dockerfile" ".gitignore"; do
        if [ ! -f "$service/$file" ]; then
            missing_files="$missing_files $file"
        fi
    done
    
    # Check CI/CD workflows
    local workflow_count=$(ls -1 "$service/.github/workflows/"*.yml 2>/dev/null | wc -l)
    
    if [ -z "$missing_dirs" ] && [ -z "$missing_files" ] && [ "$workflow_count" -ge 3 ]; then
        echo "  ✓ Fully compliant"
    else
        [ -n "$missing_dirs" ] && echo "  ✗ Missing directories:$missing_dirs"
        [ -n "$missing_files" ] && echo "  ✗ Missing files:$missing_files"
        [ "$workflow_count" -lt 3 ] && echo "  ✗ Only $workflow_count workflow files (need at least 3)"
    fi
    echo
}

echo "Main Java Backend Services:"
echo "==========================="
for service in commission-service courier-management courier-onboarding courier-subscription \
               international-shipping payout-service routing-service tracking-service \
               courier-network-locations regional-admin-system; do
    if [ -d "$service" ]; then
        check_service_compliance "$service" "java"
    fi
done

echo
echo "Additional Java Modules:"
echo "========================"
for service in courier-production courier-shared courier-staging; do
    if [ -d "$service" ]; then
        check_service_compliance "$service" "java"
    fi
done

echo
echo "Frontend Services:"
echo "=================="
for service in corporate-admin driver-mobile-app global-hq-admin regional-admin user-mobile-app; do
    if [ -d "$service" ]; then
        check_service_compliance "$service" "frontend"
    fi
done

echo
echo "Hybrid/Special Services:"
echo "========================"
for service in "Corporate Courier Branch app" "Courier Branch App" readiness-reports infrastructure; do
    if [ -d "$service" ]; then
        check_service_compliance "$service" "java"
    fi
done

echo
echo "=== Compliance Check Complete ==="
echo
echo "Summary of all services:"
total_services=0
compliant_services=0

for service in commission-service courier-management courier-onboarding courier-subscription \
               international-shipping payout-service routing-service tracking-service \
               courier-network-locations regional-admin-system courier-production courier-shared \
               courier-staging corporate-admin driver-mobile-app global-hq-admin regional-admin \
               user-mobile-app "Corporate Courier Branch app" "Courier Branch App" readiness-reports \
               infrastructure; do
    if [ -d "$service" ]; then
        total_services=$((total_services + 1))
        
        # Quick compliance check
        missing_count=0
        for dir in ".github/workflows" "tests" "k8s" "api-docs" "docs" "scripts" "i18n"; do
            [ ! -d "$service/$dir" ] && missing_count=$((missing_count + 1))
        done
        for file in "README.md" "Dockerfile" ".gitignore"; do
            [ ! -f "$service/$file" ] && missing_count=$((missing_count + 1))
        done
        workflow_count=$(ls -1 "$service/.github/workflows/"*.yml 2>/dev/null | wc -l)
        [ "$workflow_count" -lt 3 ] && missing_count=$((missing_count + 1))
        
        [ "$missing_count" -eq 0 ] && compliant_services=$((compliant_services + 1))
    fi
done

echo "Total services: $total_services"
echo "Fully compliant: $compliant_services"
echo "Compliance rate: $((compliant_services * 100 / total_services))%"