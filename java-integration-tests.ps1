# Script to generate Java integration tests for courier services
# Creates tests for service interactions using WireMock and TestContainers

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

# Define service dependencies for integration tests
$serviceIntegrations = @{
    "courier-management-service" = @(
        "courier-tracking-service",
        "notification-service"
    )
    "courier-tracking-service" = @(
        "courier-management-service",
        "regional-courier-service"
    )
    "international-shipping-service" = @(
        "courier-management-service",
        "notification-service"
    )
}

# Java integration test template using WireMock
$javaIntegrationTestTemplate = @"
package com.exalt.courierservices.{{SERVICE_PACKAGE}}.integration;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.exalt.courierservices.{{SERVICE_PACKAGE}}.service.{{CLIENT_SERVICE}}Client;
import com.exalt.courierservices.{{SERVICE_PACKAGE}}.model.{{MODEL_CLASS}};

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
public class {{CLIENT_SERVICE}}IntegrationTest {

    @Autowired
    private {{CLIENT_SERVICE}}Client client;

    @BeforeEach
    void setUp() {
        // Reset WireMock
        resetAllRequests();
    }

    @Test
    void test{{CLIENT_SERVICE}}Integration() {
        // Setup mock response
        stubFor(get(urlPathMatching("/api/{{API_PATH}}"))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody("{{MOCK_RESPONSE}}")
                .withStatus(200)));

        // Call the client
        {{RETURN_TYPE}} result = client.{{CLIENT_METHOD}}();

        // Verify the request was made
        verify(getRequestedFor(urlPathMatching("/api/{{API_PATH}}")));
        
        // Verify the response
        assertNotNull(result);
        {{ASSERTIONS}}
    }

    @Test
    void testHandleError() {
        // Setup mock error response
        stubFor(get(urlPathMatching("/api/{{API_PATH}}"))
            .willReturn(aResponse()
                .withStatus(500)
                .withBody("{\"message\": \"Internal Server Error\"}")));

        // Verify error handling works
        {{ERROR_HANDLING}}
    }
}
"@

# Generate the client interface template
$clientInterfaceTemplate = @"
package com.exalt.courierservices.{{SERVICE_PACKAGE}}.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.exalt.courierservices.{{SERVICE_PACKAGE}}.model.{{MODEL_CLASS}};

@FeignClient(name = "{{CLIENT_SERVICE_LOWERCASE}}")
public interface {{CLIENT_SERVICE}}Client {

    @GetMapping("/api/{{API_PATH}}")
    {{RETURN_TYPE}} {{CLIENT_METHOD}}();
    
    @GetMapping("/api/{{API_PATH}}/{id}")
    {{MODEL_CLASS}} get{{MODEL_CLASS}}ById(@PathVariable("id") Long id);
    
    @PostMapping("/api/{{API_PATH}}")
    {{MODEL_CLASS}} create{{MODEL_CLASS}}(@RequestBody {{MODEL_CLASS}} {{MODEL_CLASS_LOWERCASE}});
}
"@

foreach ($serviceKey in $serviceIntegrations.Keys) {
    $serviceDir = Join-Path -Path $baseDir -ChildPath $serviceKey
    $clientServices = $serviceIntegrations[$serviceKey]
    
    # Create Java test directories if they don't exist
    $integrationTestDir = Join-Path -Path $serviceDir -ChildPath "src\test\java\com\exalt\courierservices\${serviceKey.Replace('-service', '').Replace('-', '')}\integration"
    New-Item -ItemType Directory -Path $integrationTestDir -Force | Out-Null
    
    # Create client interfaces directory
    $clientDir = Join-Path -Path $serviceDir -ChildPath "src\main\java\com\exalt\courierservices\${serviceKey.Replace('-service', '').Replace('-', '')}\service"
    New-Item -ItemType Directory -Path $clientDir -Force | Out-Null
    
    # Add Feign client dependency if not exist
    $pomPath = Join-Path -Path $serviceDir -ChildPath "pom.xml"
    $pomContent = Get-Content -Path $pomPath -Raw
    
    if (-not ($pomContent -match "<artifactId>spring-cloud-starter-openfeign<\/artifactId>")) {
        $pomContent = $pomContent -replace "(<dependencies>)", @"
`$1
        <!-- Feign Client for service integration -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
        </dependency>
        <!-- WireMock for testing -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-contract-stub-runner</artifactId>
            <scope>test</scope>
        </dependency>
"@
        Set-Content -Path $pomPath -Value $pomContent -NoNewline
        Log-Message "Added Feign client dependencies to $serviceKey" "UPDATE"
    }
    
    # Create application class update to enable Feign clients
    $appJavaPath = Get-ChildItem -Path "$serviceDir\src\main\java" -Filter "*Application.java" -Recurse | Select-Object -First 1 -ExpandProperty FullName
    
    if ($appJavaPath -and (Test-Path $appJavaPath)) {
        $appJavaContent = Get-Content -Path $appJavaPath -Raw
        
        if (-not ($appJavaContent -match "@EnableFeignClients")) {
            $appJavaContent = $appJavaContent -replace "(import .*?;)", "`$1`nimport org.springframework.cloud.openfeign.EnableFeignClients;"
            $appJavaContent = $appJavaContent -replace "(@SpringBootApplication)", "`$1`n@EnableFeignClients"
            Set-Content -Path $appJavaPath -Value $appJavaContent -NoNewline
            Log-Message "Enabled Feign clients in application class for $serviceKey" "UPDATE"
        }
    }
    
    # Get the service package name
    $servicePackage = $serviceKey.Replace('-service', '').Replace('-', '')
    
    # Create client interfaces and integration tests for each dependent service
    foreach ($clientService in $clientServices) {
        $clientServiceName = $clientService.Replace('-service', '').Replace('-', '')
        $clientServiceClass = $clientServiceName.Substring(0, 1).ToUpper() + $clientServiceName.Substring(1)
        
        # Determine domain entity based on the client service
        $modelClass = switch ($clientServiceClass) {
            "Couriertracking" { "TrackingRecord" }
            "Couriermanagement" { "Courier" }
            "Regionalcourier" { "RegionalCourier" }
            "Notification" { "Notification" }
            default { $clientServiceClass }
        }
        
        $apiPath = $modelClass.ToLower() + "s"
        $modelClassLowercase = $modelClass.Substring(0, 1).ToLower() + $modelClass.Substring(1)
        
        # Create client interface
        $clientInterfacePath = Join-Path -Path $clientDir -ChildPath "${clientServiceClass}Client.java"
        
        $clientContent = $clientInterfaceTemplate.Replace("{{SERVICE_PACKAGE}}", $servicePackage)
        $clientContent = $clientContent.Replace("{{CLIENT_SERVICE}}", $clientServiceClass)
        $clientContent = $clientContent.Replace("{{CLIENT_SERVICE_LOWERCASE}}", $clientServiceClass.ToLower())
        $clientContent = $clientContent.Replace("{{MODEL_CLASS}}", $modelClass)
        $clientContent = $clientContent.Replace("{{MODEL_CLASS_LOWERCASE}}", $modelClassLowercase)
        $clientContent = $clientContent.Replace("{{API_PATH}}", $apiPath)
        $clientContent = $clientContent.Replace("{{RETURN_TYPE}}", "java.util.List<${modelClass}>")
        $clientContent = $clientContent.Replace("{{CLIENT_METHOD}}", "getAll${modelClass}s")
        
        Set-Content -Path $clientInterfacePath -Value $clientContent -NoNewline
        Log-Message "Created Feign client interface for $clientServiceClass in $serviceKey" "CREATE"
        
        # Create integration test
        $integrationTestPath = Join-Path -Path $integrationTestDir -ChildPath "${clientServiceClass}IntegrationTest.java"
        
        # Create mock JSON response based on model
        $mockResponse = "[\n    {\n        \"id\": 1,\n        \"name\": \"Test $modelClass\"\n    }\n]"
        
        $testContent = $javaIntegrationTestTemplate.Replace("{{SERVICE_PACKAGE}}", $servicePackage)
        $testContent = $testContent.Replace("{{CLIENT_SERVICE}}", $clientServiceClass)
        $testContent = $testContent.Replace("{{MODEL_CLASS}}", $modelClass)
        $testContent = $testContent.Replace("{{API_PATH}}", $apiPath)
        $testContent = $testContent.Replace("{{MOCK_RESPONSE}}", $mockResponse)
        $testContent = $testContent.Replace("{{RETURN_TYPE}}", "java.util.List<${modelClass}>")
        $testContent = $testContent.Replace("{{CLIENT_METHOD}}", "getAll${modelClass}s")
        $testContent = $testContent.Replace("{{ASSERTIONS}}", "assertEquals(1, result.size());")
        $testContent = $testContent.Replace("{{ERROR_HANDLING}}", "assertThrows(Exception.class, () -> client.getAll${modelClass}s());")
        
        Set-Content -Path $integrationTestPath -Value $testContent -NoNewline
        Log-Message "Created integration test for $clientServiceClass in $serviceKey" "CREATE"
    }
}

Log-Message "Java integration test generation completed" "DONE"
