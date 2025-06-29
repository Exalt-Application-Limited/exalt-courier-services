# Script to create test data generators for courier services
# Creates test data factories for both Java and Node.js services

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

# Java test data factories
$javaTestDataFactoryTemplate = @"
package com.exalt.courierservices.{{SERVICE_PACKAGE}}.test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.exalt.courierservices.{{SERVICE_PACKAGE}}.model.*;

/**
 * Test data factory for {{SERVICE_PACKAGE}} domain objects.
 * Use this class in tests to generate consistent test data.
 */
public class TestDataFactory {
    
    /**
     * Creates a test {{ENTITY_NAME}} with random data
     */
    public static {{ENTITY_NAME}} create{{ENTITY_NAME}}() {
        {{ENTITY_NAME}} entity = new {{ENTITY_NAME}}();
        {{ENTITY_FIELDS_SETUP}}
        return entity;
    }
    
    /**
     * Creates a list of test {{ENTITY_NAME}}s
     */
    public static List<{{ENTITY_NAME}}> createMultiple{{ENTITY_NAME}}s(int count) {
        List<{{ENTITY_NAME}}> entities = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            entities.add(create{{ENTITY_NAME}}());
        }
        return entities;
    }
    
    {{ADDITIONAL_FACTORIES}}
}
"@

# Node.js test data factories
$nodeTestDataFactoryTemplate = @"
/**
 * Test data factory for {{SERVICE_NAME}} domain objects.
 * Use this module in tests to generate consistent test data.
 */

/**
 * Creates a test {{ENTITY_NAME}} with random or specified data
 * @param {Object} overrides - Properties to override default values
 * @returns {Object} A test {{ENTITY_NAME}} object
 */
function create{{ENTITY_NAME}}(overrides = {}) {
  return {
    {{ENTITY_FIELDS_SETUP}},
    ...overrides
  };
}

/**
 * Creates multiple test {{ENTITY_NAME}}s
 * @param {number} count - Number of objects to create
 * @param {Object} overrides - Properties to override default values
 * @returns {Array} Array of test {{ENTITY_NAME}} objects
 */
function createMultiple{{ENTITY_NAME}}s(count = 5, overrides = {}) {
  return Array(count).fill().map((_, index) => {
    return create{{ENTITY_NAME}}({
      id: String(index + 1),
      ...overrides
    });
  });
}

{{ADDITIONAL_FACTORIES}}

module.exports = {
  create{{ENTITY_NAME}},
  createMultiple{{ENTITY_NAME}}s,
  {{EXPORT_ADDITIONAL}}
};
"@

# Java domain entities and their fields
$javaEntities = @{
    "courier-management-service" = @{
        "main" = @{
            "name" = "Courier"
            "fields" = @{
                "id" = "setId(1L)"
                "name" = "setName(""Test Courier"")"
                "email" = "setEmail(""test.courier@example.com"")"
                "phone" = "setPhone(""+1234567890"")"
                "active" = "setActive(true)"
                "rating" = "setRating(4.5)"
            }
        }
        "additional" = @{
            "name" = "Assignment"
            "fields" = @{
                "id" = "setId(1L)"
                "courierId" = "setCourierId(1L)"
                "orderId" = "setOrderId(100L)"
                "status" = "setStatus(AssignmentStatus.PENDING)"
                "createdAt" = "setCreatedAt(LocalDateTime.now())"
            }
        }
    }
    "courier-tracking-service" = @{
        "main" = @{
            "name" = "TrackingRecord"
            "fields" = @{
                "id" = "setId(1L)"
                "courierId" = "setCourierId(1L)"
                "orderId" = "setOrderId(100L)"
                "latitude" = "setLatitude(40.7128)"
                "longitude" = "setLongitude(-74.0060)"
                "timestamp" = "setTimestamp(LocalDateTime.now())"
                "status" = "setStatus(DeliveryStatus.IN_PROGRESS)"
            }
        }
    }
    "regional-courier-service" = @{
        "main" = @{
            "name" = "RegionalCourier"
            "fields" = @{
                "id" = "setId(1L)"
                "name" = "setName(""Regional Test Courier"")"
                "region" = "setRegion(""North East"")"
                "active" = "setActive(true)"
                "maxDeliveryDistance" = "setMaxDeliveryDistance(50.0)"
            }
        }
    }
}

# Node.js domain entities and their fields
$nodeEntities = @{
    "courier-network-locations" = @{
        "main" = @{
            "name" = "Location"
            "fields" = @{
                "id" = "id: '1'"
                "name" = "name: 'Test Hub Location'"
                "address" = "address: '123 Test Street, City, State, 12345'"
                "latitude" = "latitude: 40.7128"
                "longitude" = "longitude: -74.0060"
                "isActive" = "isActive: true"
                "locationType" = "locationType: 'HUB'"
            }
        }
    }
    "courier-geo-routing" = @{
        "main" = @{
            "name" = "Route"
            "fields" = @{
                "id" = "id: '1'"
                "origin" = "origin: { lat: 40.7128, lng: -74.0060 }"
                "destination" = "destination: { lat: 40.7589, lng: -73.9851 }"
                "waypoints" = "waypoints: [{ lat: 40.7308, lng: -73.9973 }]"
                "distance" = "distance: 4.5"
                "duration" = "duration: 15"
                "createdAt" = "createdAt: new Date()"
            }
        }
    }
    "courier-fare-calculator" = @{
        "main" = @{
            "name" = "Fare"
            "fields" = @{
                "id" = "id: '1'"
                "distance" = "distance: 5.2"
                "baseFare" = "baseFare: 3.0"
                "perMileFare" = "perMileFare: 1.25"
                "surgeMultiplier" = "surgeMultiplier: 1.0"
                "totalFare" = "totalFare: 9.5"
                "currency" = "currency: 'USD'"
            }
        }
    }
}

# Generate Java test data factories
foreach ($serviceKey in $javaEntities.Keys) {
    $serviceDir = Join-Path -Path $baseDir -ChildPath $serviceKey
    $servicePackage = $serviceKey.Replace('-service', '').Replace('-', '')
    
    # Create test data factory directory
    $factoryDir = Join-Path -Path $serviceDir -ChildPath "src\test\java\com\exalt\courierservices\${servicePackage}\test"
    New-Item -ItemType Directory -Path $factoryDir -Force | Out-Null
    
    # Get main entity details
    $mainEntity = $javaEntities[$serviceKey].main
    $entityName = $mainEntity.name
    
    # Build field setup code
    $fieldsSetup = ""
    foreach ($field in $mainEntity.fields.GetEnumerator()) {
        $fieldsSetup += "        entity.$($field.Value);`n"
    }
    
    # Build additional factories
    $additionalFactories = ""
    if ($javaEntities[$serviceKey].ContainsKey("additional")) {
        $additionalEntity = $javaEntities[$serviceKey].additional
        $additionalEntityName = $additionalEntity.name
        
        $additionalFieldsSetup = ""
        foreach ($field in $additionalEntity.fields.GetEnumerator()) {
            $additionalFieldsSetup += "        entity.$($field.Value);`n"
        }
        
        $additionalFactories = @"
    
    /**
     * Creates a test $additionalEntityName with random data
     */
    public static $additionalEntityName create$additionalEntityName() {
        $additionalEntityName entity = new $additionalEntityName();
$additionalFieldsSetup
        return entity;
    }
    
    /**
     * Creates a list of test $($additionalEntityName)s
     */
    public static List<$additionalEntityName> createMultiple$($additionalEntityName)s(int count) {
        List<$additionalEntityName> entities = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            entities.add(create$additionalEntityName());
        }
        return entities;
    }
"@
    }
    
    # Create test data factory file
    $factoryContent = $javaTestDataFactoryTemplate.Replace("{{SERVICE_PACKAGE}}", $servicePackage)
    $factoryContent = $factoryContent.Replace("{{ENTITY_NAME}}", $entityName)
    $factoryContent = $factoryContent.Replace("{{ENTITY_FIELDS_SETUP}}", $fieldsSetup)
    $factoryContent = $factoryContent.Replace("{{ADDITIONAL_FACTORIES}}", $additionalFactories)
    
    $factoryPath = Join-Path -Path $factoryDir -ChildPath "TestDataFactory.java"
    Set-Content -Path $factoryPath -Value $factoryContent -NoNewline
    Log-Message "Created Java test data factory for $serviceKey" "CREATE"
    
    # Create sample usage in a test
    $sampleTestDir = Join-Path -Path $serviceDir -ChildPath "src\test\java\com\exalt\courierservices\${servicePackage}\sample"
    New-Item -ItemType Directory -Path $sampleTestDir -Force | Out-Null
    
    $sampleTestContent = @"
package com.exalt.courierservices.${servicePackage}.sample;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.exalt.courierservices.${servicePackage}.model.$entityName;
import com.exalt.courierservices.${servicePackage}.test.TestDataFactory;

/**
 * Sample showing how to use the TestDataFactory
 */
public class SampleTestDataTest {

    @Test
    void testDataFactoryUsage() {
        // Create a single test entity
        $entityName entity = TestDataFactory.create$entityName();
        assertNotNull(entity);
        
        // Create multiple test entities
        List<$entityName> entities = TestDataFactory.createMultiple${entityName}s(3);
        assertEquals(3, entities.size());
    }
}
"@
    
    $sampleTestPath = Join-Path -Path $sampleTestDir -ChildPath "SampleTestDataTest.java"
    Set-Content -Path $sampleTestPath -Value $sampleTestContent -NoNewline
    Log-Message "Created Java sample test data usage for $serviceKey" "CREATE"
}

# Generate Node.js test data factories
foreach ($serviceKey in $nodeEntities.Keys) {
    $serviceDir = Join-Path -Path $baseDir -ChildPath $serviceKey
    $serviceName = $serviceKey.Replace('-', '')
    
    # Create test data factory directory
    $factoryDir = Join-Path -Path $serviceDir -ChildPath "tests\utils"
    New-Item -ItemType Directory -Path $factoryDir -Force | Out-Null
    
    # Get main entity details
    $mainEntity = $nodeEntities[$serviceKey].main
    $entityName = $mainEntity.name
    
    # Build field setup code
    $fieldsSetup = ""
    foreach ($field in $mainEntity.fields.GetEnumerator()) {
        $fieldsSetup += "    $($field.Value),`n"
    }
    $fieldsSetup = $fieldsSetup.TrimEnd(",`n")
    
    # Build additional factories
    $additionalFactories = ""
    $exportAdditional = ""
    if ($nodeEntities[$serviceKey].ContainsKey("additional")) {
        $additionalEntity = $nodeEntities[$serviceKey].additional
        $additionalEntityName = $additionalEntity.name
        
        $additionalFieldsSetup = ""
        foreach ($field in $additionalEntity.fields.GetEnumerator()) {
            $additionalFieldsSetup += "    $($field.Value),`n"
        }
        $additionalFieldsSetup = $additionalFieldsSetup.TrimEnd(",`n")
        
        $additionalFactories = @"

/**
 * Creates a test $additionalEntityName with random or specified data
 * @param {Object} overrides - Properties to override default values
 * @returns {Object} A test $additionalEntityName object
 */
function create$additionalEntityName(overrides = {}) {
  return {
$additionalFieldsSetup,
    ...overrides
  };
}

/**
 * Creates multiple test $($additionalEntityName)s
 * @param {number} count - Number of objects to create
 * @param {Object} overrides - Properties to override default values
 * @returns {Array} Array of test $additionalEntityName objects
 */
function createMultiple$($additionalEntityName)s(count = 5, overrides = {}) {
  return Array(count).fill().map((_, index) => {
    return create$additionalEntityName({
      id: String(index + 1),
      ...overrides
    });
  });
}
"@
        $exportAdditional = "create$additionalEntityName, createMultiple$($additionalEntityName)s,"
    }
    
    # Create test data factory file
    $factoryContent = $nodeTestDataFactoryTemplate.Replace("{{SERVICE_NAME}}", $serviceName)
    $factoryContent = $factoryContent.Replace("{{ENTITY_NAME}}", $entityName)
    $factoryContent = $factoryContent.Replace("{{ENTITY_FIELDS_SETUP}}", $fieldsSetup)
    $factoryContent = $factoryContent.Replace("{{ADDITIONAL_FACTORIES}}", $additionalFactories)
    $factoryContent = $factoryContent.Replace("{{EXPORT_ADDITIONAL}}", $exportAdditional)
    
    $factoryPath = Join-Path -Path $factoryDir -ChildPath "testDataFactory.js"
    Set-Content -Path $factoryPath -Value $factoryContent -Encoding UTF8
    Log-Message "Created Node.js test data factory for $serviceKey" "CREATE"
    
    # Create sample usage in a test
    $sampleTestDir = Join-Path -Path $serviceDir -ChildPath "tests\samples"
    New-Item -ItemType Directory -Path $sampleTestDir -Force | Out-Null
    
    $sampleTestContent = @"
const { create$entityName, createMultiple${entityName}s } = require('../utils/testDataFactory');

describe('Test Data Factory Usage Sample', () => {
  it('should create a single test entity', () => {
    const entity = create$entityName();
    expect(entity).toBeDefined();
    expect(entity.id).toBe('1');
  });

  it('should create multiple test entities', () => {
    const entities = createMultiple${entityName}s(3);
    expect(entities).toHaveLength(3);
    expect(entities[0].id).toBe('1');
    expect(entities[1].id).toBe('2');
    expect(entities[2].id).toBe('3');
  });

  it('should override default values', () => {
    const entity = create$entityName({ name: 'Custom Name' });
    expect(entity.name).toBe('Custom Name');
  });
});
"@
    
    $sampleTestPath = Join-Path -Path $sampleTestDir -ChildPath "testDataFactorySample.test.js"
    Set-Content -Path $sampleTestPath -Value $sampleTestContent -Encoding UTF8
    Log-Message "Created Node.js sample test data usage for $serviceKey" "CREATE"
}

Log-Message "Test data factory generation completed" "DONE"
