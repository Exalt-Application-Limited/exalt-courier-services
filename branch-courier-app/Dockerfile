# Multi-stage build for hybrid Java Spring Boot + React Native project

# Stage 1: Build Java backend
FROM maven:3.8.6-openjdk-17 AS java-build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Build React Native bundle (if needed for embedded web views)
FROM node:18-alpine AS node-build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build || echo "No build script for React Native mobile components"

# Stage 3: Runtime - Java Spring Boot with optional Node.js for hybrid features
FROM openjdk:17-jdk-slim
WORKDIR /app

# Install Node.js for potential hybrid operations
RUN apt-get update && apt-get install -y curl && \
    curl -fsSL https://deb.nodesource.com/setup_18.x | bash - && \
    apt-get install -y nodejs && \
    rm -rf /var/lib/apt/lists/*

# Copy Java application
COPY --from=java-build /app/target/*.jar app.jar

# Copy React Native assets (if any web bundles were created)
COPY --from=node-build /app/android/app/src/main/assets/ ./assets/ || echo "No React Native assets to copy"

# Expose port for Spring Boot backend
EXPOSE 8080

# Start Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]