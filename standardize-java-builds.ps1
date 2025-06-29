# Script to standardize Java pom.xml files
# Updates Java version, Maven version, Spring Boot version and Lombok annotations

function Log-Message {
    param (
        [string]$Message,
        [string]$Type = "INFO"
    )
    $timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    Write-Host "[$timestamp] [$Type] $Message"
}

$baseDir = "C:\Users\frich\Desktop\Exalt-Application-Limited\Exalt-Application-Limited\social-ecommerce-ecosystem\courier-services"
Set-Location $baseDir

Log-Message "Starting Java build standardization" "START"

# List of Java services to standardize
$javaServices = @(
    "courier-management-service",
    "courier-subscription-service", 
    "international-shipping-service",
    "commission-service",
    "payout-service",
    "notification-service",
    "regional-courier-service",
    "courier-tracking-service"
)

# Standard pom.xml template
$standardPomTemplate = @"
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.1.5</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    
    <groupId>com.exalt.courierservices</groupId>
    <artifactId>SERVICE_NAME_PLACEHOLDER</artifactId>
    <version>1.0.0</version>
    <name>SERVICE_DISPLAY_NAME_PLACEHOLDER</name>
    <description>SERVICE_DESCRIPTION_PLACEHOLDER</description>
    
    <properties>
        <java.version>17</java.version>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <spring-cloud.version>2022.0.4</spring-cloud.version>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
    </properties>
    
    <dependencies>
        <!-- Spring Boot -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        
        <!-- Spring Cloud -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-config</artifactId>
        </dependency>
        
        <!-- Database -->
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>
        
        <!-- Lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        
        <!-- API Documentation -->
        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
            <version>2.2.0</version>
        </dependency>
        
        <!-- Testing -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>postgresql</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>junit-jupiter</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <dependency>
                <groupId>org.testcontainers</groupId>
                <artifactId>testcontainers-bom</artifactId>
                <version>1.19.1</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <source>17</source>
                    <target>17</target>
                    <annotationProcessorPaths>
                        <path>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                            <version>${lombok.version}</version>
                        </path>
                    </annotationProcessorPaths>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.1.2</version>
            </plugin>
        </plugins>
    </build>
</project>
"@

# Standard application-test.yml template
$applicationTestYmlTemplate = @"
spring:
  application:
    name: SERVICE_NAME_PLACEHOLDER-test
  datasource:
    url: jdbc:h2:mem:testdb
    username: sa
    password: password
    driver-class-name: org.h2.Driver
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create-drop
    properties:
      hibernate:
        format_sql: true
        show_sql: true
  h2:
    console:
      enabled: true

# Disable Eureka client in tests
eureka:
  client:
    enabled: false
    register-with-eureka: false
    fetch-registry: false

# Disable Config Server in tests
spring.cloud.config:
  enabled: false
  discovery:
    enabled: false

# Server port for testing
server:
  port: 0
"@

foreach ($service in $javaServices) {
    $serviceDir = Join-Path -Path $baseDir -ChildPath $service
    $pomPath = Join-Path -Path $serviceDir -ChildPath "pom.xml"
    
    # Check if this is a Java service with pom.xml
    if (Test-Path $pomPath) {
        Log-Message "Standardizing pom.xml for $service" "PROCESS"
        
        # Extract the service short name
        $serviceShortName = $service -replace '-service', ''
        $serviceShortName = $serviceShortName -replace 'courier-', ''
        
        # Customize the standard pom.xml template
        $customizedPom = $standardPomTemplate -replace 'SERVICE_NAME_PLACEHOLDER', $service
        $customizedPom = $customizedPom -replace 'SERVICE_DISPLAY_NAME_PLACEHOLDER', "$service"
        $customizedPom = $customizedPom -replace 'SERVICE_DESCRIPTION_PLACEHOLDER', "Exalt Courier Services - $service"
        
        # Backup existing pom.xml
        $backupPath = $pomPath + ".backup.old"
        if (Test-Path $pomPath) {
            Copy-Item -Path $pomPath -Destination $backupPath -Force
        }
        
        # Write new standardized pom.xml
        Set-Content -Path $pomPath -Value $customizedPom -NoNewline
        Log-Message "Updated pom.xml for $service" "UPDATED"
        
        # Create application-test.yml
        $testResourcesDir = Join-Path -Path $serviceDir -ChildPath "src\test\resources"
        if (-not (Test-Path $testResourcesDir)) {
            New-Item -ItemType Directory -Path $testResourcesDir -Force | Out-Null
        }
        
        $testConfigPath = Join-Path -Path $testResourcesDir -ChildPath "application-test.yml"
        $customizedTestConfig = $applicationTestYmlTemplate -replace 'SERVICE_NAME_PLACEHOLDER', $serviceShortName
        Set-Content -Path $testConfigPath -Value $customizedTestConfig -NoNewline
        Log-Message "Created application-test.yml for $service" "CREATED"
        
        # Ensure application.yml exists as well
        $mainResourcesDir = Join-Path -Path $serviceDir -ChildPath "src\main\resources"
        if (-not (Test-Path $mainResourcesDir)) {
            New-Item -ItemType Directory -Path $mainResourcesDir -Force | Out-Null
        }
        
        $appConfigPath = Join-Path -Path $mainResourcesDir -ChildPath "application.yml"
        if (-not (Test-Path $appConfigPath)) {
            $applicationYml = @"
spring:
  application:
    name: $serviceShortName
  profiles:
    active: dev
  config:
    import: optional:configserver:http://config-server:8888
  cloud:
    config:
      fail-fast: false
      retry:
        max-attempts: 20
        max-interval: 15000
        initial-interval: 10000

server:
  port: 8080

# Eureka Client Configuration
eureka:
  client:
    serviceUrl:
      defaultZone: http://eureka:8761/eureka/
  instance:
    preferIpAddress: true

# Actuator Configuration
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: always

# Logging Configuration
logging:
  level:
    root: INFO
    com.exalt.courierservices: DEBUG
"@
            Set-Content -Path $appConfigPath -Value $applicationYml -NoNewline
            Log-Message "Created application.yml for $service" "CREATED"
        }
    } else {
        Log-Message "pom.xml not found for $service, creating basic structure" "WARNING"
        
        # Create directory structure
        $srcMainJavaDir = Join-Path -Path $serviceDir -ChildPath "src\main\java\com\exalt\courierservices"
        $srcMainResourcesDir = Join-Path -Path $serviceDir -ChildPath "src\main\resources"
        $srcTestJavaDir = Join-Path -Path $serviceDir -ChildPath "src\test\java\com\exalt\courierservices"
        $srcTestResourcesDir = Join-Path -Path $serviceDir -ChildPath "src\test\resources"
        
        New-Item -ItemType Directory -Path $srcMainJavaDir -Force | Out-Null
        New-Item -ItemType Directory -Path $srcMainResourcesDir -Force | Out-Null
        New-Item -ItemType Directory -Path $srcTestJavaDir -Force | Out-Null
        New-Item -ItemType Directory -Path $srcTestResourcesDir -Force | Out-Null
        
        # Create pom.xml
        $customizedPom = $standardPomTemplate -replace 'SERVICE_NAME_PLACEHOLDER', $service
        $customizedPom = $customizedPom -replace 'SERVICE_DISPLAY_NAME_PLACEHOLDER', "$service"
        $customizedPom = $customizedPom -replace 'SERVICE_DESCRIPTION_PLACEHOLDER', "Exalt Courier Services - $service"
        Set-Content -Path $pomPath -Value $customizedPom -NoNewline
        Log-Message "Created new pom.xml for $service" "CREATED"
        
        # Create application.yml and application-test.yml
        $serviceShortName = $service -replace '-service', ''
        $serviceShortName = $serviceShortName -replace 'courier-', ''
        
        $applicationYml = @"
spring:
  application:
    name: $serviceShortName
  profiles:
    active: dev
  config:
    import: optional:configserver:http://config-server:8888
  cloud:
    config:
      fail-fast: false
      retry:
        max-attempts: 20
        max-interval: 15000
        initial-interval: 10000

server:
  port: 8080

# Eureka Client Configuration
eureka:
  client:
    serviceUrl:
      defaultZone: http://eureka:8761/eureka/
  instance:
    preferIpAddress: true

# Actuator Configuration
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: always

# Logging Configuration
logging:
  level:
    root: INFO
    com.exalt.courierservices: DEBUG
"@
        
        $appConfigPath = Join-Path -Path $srcMainResourcesDir -ChildPath "application.yml"
        Set-Content -Path $appConfigPath -Value $applicationYml -NoNewline
        
        $customizedTestConfig = $applicationTestYmlTemplate -replace 'SERVICE_NAME_PLACEHOLDER', $serviceShortName
        $testConfigPath = Join-Path -Path $srcTestResourcesDir -ChildPath "application-test.yml"
        Set-Content -Path $testConfigPath -Value $customizedTestConfig -NoNewline
        
        Log-Message "Created configuration files for $service" "CREATED"
    }
}

Log-Message "Java build standardization completed" "FINISH"
