#!/bin/bash

# Setup script for branch-courier-app
echo "Setting up branch-courier-app development environment..."

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "Error: Maven is not installed. Please install Maven first."
    exit 1
fi

# Check if Java 17 is installed
if ! command -v java &> /dev/null; then
    echo "Error: Java is not installed. Please install Java 17 first."
    exit 1
fi

# Install dependencies
echo "Installing Maven dependencies..."
mvn clean install

# Check if Docker is installed (optional)
if command -v docker &> /dev/null; then
    echo "Docker is available for containerization."
else
    echo "Warning: Docker is not installed. Install Docker for containerization support."
fi

echo "Setup completed successfully!"
echo "Run './scripts/dev.sh' to start the development server."