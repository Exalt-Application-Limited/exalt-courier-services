#!/bin/bash
# Test compilation of courier services after installing Java/Maven in WSL

echo "==============================================="
echo "Testing Courier Services Compilation"
echo "==============================================="
echo ""

# Check Java and Maven
echo "Java version:"
java -version
echo ""
echo "Maven version:"
mvn -version
echo ""

# Color codes
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Test each service
services=("branch-courier-app" "commission-service" "courier-management" "routing-service" "tracking-service" "payout-service" "driver-mobile-app" "international-shipping")

total=0
passed=0
failed=0

echo "Starting compilation tests..."
echo "==============================="

for service in "${services[@]}"; do
    if [ -d "$service" ] && [ -f "$service/pom.xml" ]; then
        echo ""
        echo "Testing $service..."
        cd "$service"
        
        # Run Maven compile
        if mvn clean compile -q; then
            echo -e "${GREEN}✓ $service - PASSED${NC}"
            ((passed++))
        else
            echo -e "${RED}✗ $service - FAILED${NC}"
            ((failed++))
        fi
        
        cd ..
        ((total++))
    fi
done

echo ""
echo "==============================="
echo "COMPILATION TEST RESULTS"
echo "==============================="
echo "Total services tested: $total"
echo -e "${GREEN}Passed: $passed${NC}"
echo -e "${RED}Failed: $failed${NC}"
echo ""

if [ $failed -eq 0 ]; then
    echo -e "${GREEN}🎉 All services compiled successfully!${NC}"
else
    echo -e "${RED}⚠️  Some services failed compilation. Check the output above for details.${NC}"
fi

echo ""
echo "To run full tests with unit tests:"
echo "mvn clean test"