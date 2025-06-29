FROM openjdk:11-jre-slim

WORKDIR /app

# Add Maven dependencies (not changed often)
COPY target/lib/*.jar /app/lib/

# Add application
COPY target/*.jar app.jar

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "app.jar"]
