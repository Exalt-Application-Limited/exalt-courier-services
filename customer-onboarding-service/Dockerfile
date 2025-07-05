FROM openjdk:17-slim

WORKDIR /app

# Copy Maven files
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download all required dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Package the application
RUN ./mvnw package -DskipTests

# Run stage
FROM openjdk:17-slim
WORKDIR /app

# Copy the built jar file from the build stage
COPY --from=0 /app/target/*.jar app.jar

# Environment variables
ENV SPRING_PROFILES_ACTIVE=production
ENV SERVER_PORT=8310

# Expose the port
EXPOSE 8310

# Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]