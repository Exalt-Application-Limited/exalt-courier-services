#!/bin/bash

echo "=== SETTING UP DEVELOPMENT ENVIRONMENTS FOR MOBILE APPS ==="
echo ""

# React Native mobile apps
MOBILE_APPS=("branch-courier-app" "user-mobile-app")

for app in "${MOBILE_APPS[@]}"; do
    echo "📱 Setting up $app development environment..."
    cd "/mnt/c/Users/frich/Desktop/Micro-Social-Ecommerce-Ecosystems/Exalt-Application-Limited/social-ecommerce-ecosystem/courier-services/$app"
    
    # Create Android directory structure if missing
    if [ ! -d "android" ]; then
        echo "  - Creating Android directory structure..."
        mkdir -p android/app/src/main/assets
    fi
    
    # Create iOS directory structure if missing  
    if [ ! -d "ios" ]; then
        echo "  - Creating iOS directory structure..."
        mkdir -p ios
    fi
    
    # Create index.js if missing
    if [ ! -f "index.js" ]; then
        echo "  - Creating index.js entry point..."
        cat > index.js << 'EOF'
import {AppRegistry} from 'react-native';
import App from './App';
import {name as appName} from './app.json';

AppRegistry.registerComponent(appName, () => App);
EOF
    fi
    
    # Create app.json if missing
    if [ ! -f "app.json" ]; then
        echo "  - Creating app.json..."
        cat > app.json << EOF
{
  "name": "$app",
  "displayName": "$app"
}
EOF
    fi
    
    # Create basic App.js if missing
    if [ ! -f "App.js" ]; then
        echo "  - Creating App.js..."
        cat > App.js << 'EOF'
import React from 'react';
import {
  SafeAreaView,
  StyleSheet,
  Text,
  View,
} from 'react-native';

const App = () => {
  return (
    <SafeAreaView style={styles.container}>
      <View>
        <Text style={styles.title}>Courier Services App</Text>
      </View>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
  },
});

export default App;
EOF
    fi
    
    echo "✅ $app development environment ready"
    echo ""
done

# Node.js app (driver-mobile-app backend)
echo "🔧 Setting up driver-mobile-app backend environment..."
cd "/mnt/c/Users/frich/Desktop/Micro-Social-Ecommerce-Ecosystems/Exalt-Application-Limited/social-ecommerce-ecosystem/courier-services/driver-mobile-app"

# Create webpack config if missing
if [ ! -f "webpack.config.js" ]; then
    echo "  - Creating webpack.config.js..."
    cat > webpack.config.js << 'EOF'
const path = require('path');

module.exports = {
  entry: './src/index.js',
  output: {
    filename: 'bundle.js',
    path: path.resolve(__dirname, 'dist'),
  },
  target: 'node',
  mode: 'production',
  externals: {
    express: 'commonjs express'
  }
};
EOF
fi

# Create .env.example if missing
if [ ! -f ".env.example" ]; then
    echo "  - Creating .env.example..."
    cat > .env.example << 'EOF'
PORT=3001
NODE_ENV=development
API_BASE_URL=http://localhost:8080
JWT_SECRET=your-secret-key
EOF
fi

echo "✅ driver-mobile-app backend environment ready"
echo ""
echo "🎉 All development environments have been set up!"