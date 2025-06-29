#!/bin/bash

echo "=== FIXING REACT FRONTEND APPLICATIONS ==="
echo ""

# Array of React apps to fix
REACT_APPS=("corporate-admin" "global-hq-admin" "regional-admin")

for app in "${REACT_APPS[@]}"; do
    echo "🔧 Fixing $app..."
    cd "/mnt/c/Users/frich/Desktop/Micro-Social-Ecommerce-Ecosystems/Exalt-Application-Limited/social-ecommerce-ecosystem/courier-services/$app"
    
    # Clean install
    echo "  - Cleaning old dependencies..."
    rm -rf node_modules package-lock.json
    
    # Install with legacy peer deps
    echo "  - Installing dependencies..."
    npm install --legacy-peer-deps --silent
    
    # Add GENERATE_SOURCEMAP=false to prevent source map issues
    echo "  - Updating package.json scripts..."
    sed -i 's/"build": "react-scripts build"/"build": "GENERATE_SOURCEMAP=false react-scripts build"/g' package.json
    
    echo "✅ $app fixed"
    echo ""
done

echo "🎉 All React apps have been fixed!"