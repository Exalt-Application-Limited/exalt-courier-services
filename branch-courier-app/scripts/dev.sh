#!/bin/bash

# Development script for branch-courier-app
echo "Starting branch-courier-app in development mode..."

# Set development profile
export SPRING_PROFILES_ACTIVE=dev

# Set default port if not specified
export SERVER_PORT=${SERVER_PORT:-8080}

echo "Starting service on port $SERVER_PORT with profile $SPRING_PROFILES_ACTIVE"

# Start the Spring Boot application
mvn spring-boot:run \
  -Dspring-boot.run.profiles=$SPRING_PROFILES_ACTIVE \
  -Dspring-boot.run.arguments="--server.port=$SERVER_PORT"