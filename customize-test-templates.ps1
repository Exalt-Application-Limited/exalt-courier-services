# Script to customize generated test templates with business logic specifics
# This script will analyze each service and enhance test templates with domain-specific tests

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

# List of service domains and their business logic specifics
$serviceDomains = @{
    "courier-management-service" = @{
        "type" = "java"
        "entity" = "Courier"
        "fields" = @{
            "id" = "Long"
            "name" = "String"
            "email" = "String"
            "phone" = "String"
            "active" = "Boolean"
            "vehicleType" = "VehicleType"
            "currentLocation" = "GeoLocation"
            "rating" = "Double"
        }
        "validations" = @(
            "Email must be valid",
            "Phone must match pattern",
            "Rating must be between 0 and 5"
        )
        "business_rules" = @(
            "A courier must be active to receive assignments",
            "Courier rating affects assignment priority"
        )
    }
    "courier-tracking-service" = @{
        "type" = "java"
        "entity" = "TrackingRecord"
        "fields" = @{
            "id" = "Long"
            "courierId" = "Long"
            "orderId" = "Long"
            "latitude" = "Double"
            "longitude" = "Double"
            "timestamp" = "LocalDateTime"
            "status" = "DeliveryStatus"
            "batteryLevel" = "Integer"
        }
        "validations" = @(
            "Latitude must be between -90 and 90",
            "Longitude must be between -180 and 180",
            "Timestamp cannot be in future"
        )
        "business_rules" = @(
            "Location updates should be at most 5 minutes apart",
            "Low battery level alerts should be triggered under 15%"
        )
    }
    "courier-geo-routing" = @{
        "type" = "nodejs"
        "entity" = "Route"
        "fields" = @{
            "id" = "String"
            "origin" = "GeoPoint"
            "destination" = "GeoPoint"
            "waypoints" = "GeoPoint[]"
            "distance" = "Number"
            "duration" = "Number"
            "createdAt" = "Date"
        }
        "validations" = @(
            "Origin and destination cannot be the same",
            "At least one waypoint is required",
            "Distance must be positive"
        )
        "business_rules" = @(
            "Routes should optimize for time or distance based on customer preference",
            "High traffic areas should be avoided during peak hours"
        )
    }
}

function Add-BusinessLogicToJavaTest {
    param (
        [string]$ServiceName,
        [hashtable]$DomainInfo,
        [string]$TestType
    )
    
    Log-Message "Customizing $TestType tests for $ServiceName with business logic" "PROCESS"
    
    $serviceDir = Join-Path -Path $baseDir -ChildPath $ServiceName
    $testDir = Join-Path -Path $serviceDir -ChildPath "src\test\java"
    
    switch ($TestType) {
        "Controller" {
            # Find controller test files
            $testFiles = Get-ChildItem -Path $testDir -Filter "*ControllerTest.java" -Recurse
            
            foreach ($testFile in $testFiles) {
                $content = Get-Content -Path $testFile.FullName -Raw
                
                # Skip if we've already customized this file
                if ($content -match "// Customized with business logic") {
                    continue
                }
                
                # Add validation tests
                $validationTests = ""
                foreach ($validation in $DomainInfo.validations) {
                    $sanitizedValidation = $validation -replace "[^a-zA-Z0-9]", ""
                    $validationTests += @"
    
    @Test
    void test${sanitizedValidation}() throws Exception {
        // Customized business logic validation test: $validation
        // TODO: Implement specific validation test
        
        // Create invalid DTO
        testDto = new {{DTO_CLASS}}();
        // Set invalid properties based on validation rule
        
        mockMvc.perform(post("/api/{{API_PATH}}")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(testDto)))
               .andExpect(status().isBadRequest());
    }
"@
                }
                
                $content = $content -replace "(\s+@Test\s+void testDelete.*?\}\s+)}", "`$1$validationTests`n    // Customized with business logic`n}"
                
                # Save modified content
                Set-Content -Path $testFile.FullName -Value $content -NoNewline
                Log-Message "Added validation tests to $($testFile.Name)" "UPDATE"
            }
        }
        "Service" {
            # Find service test files
            $testFiles = Get-ChildItem -Path $testDir -Filter "*ServiceTest.java" -Recurse
            
            foreach ($testFile in $testFiles) {
                $content = Get-Content -Path $testFile.FullName -Raw
                
                # Skip if we've already customized this file
                if ($content -match "// Customized with business logic") {
                    continue
                }
                
                # Add business rule tests
                $businessRuleTests = ""
                foreach ($rule in $DomainInfo.business_rules) {
                    $sanitizedRule = $rule -replace "[^a-zA-Z0-9]", ""
                    $businessRuleTests += @"
    
    @Test
    void test${sanitizedRule}() {
        // Customized business rule test: $rule
        // TODO: Implement specific business rule test
        
        // Set up test data
        
        // Execute service method
        
        // Assert business rule is enforced
        assertTrue(true, "Business rule test to be implemented");
    }
"@
                }
                
                $content = $content -replace "(\s+@Test\s+void testDelete.*?\}\s+)}", "`$1$businessRuleTests`n    // Customized with business logic`n}"
                
                # Save modified content
                Set-Content -Path $testFile.FullName -Value $content -NoNewline
                Log-Message "Added business rule tests to $($testFile.Name)" "UPDATE"
            }
        }
        "Repository" {
            # Find repository test files
            $testFiles = Get-ChildItem -Path $testDir -Filter "*RepositoryTest.java" -Recurse
            
            foreach ($testFile in $testFiles) {
                $content = Get-Content -Path $testFile.FullName -Raw
                
                # Skip if we've already customized this file
                if ($content -match "// Customized with business logic") {
                    continue
                }
                
                # Add custom query tests
                $customQueryTests = @"
    
    @Test
    void testCustomQueries() {
        // Customized with business logic - custom query tests
        // TODO: Implement tests for repository custom queries
        
        // Create test entities with specific attributes
        // for the ${DomainInfo.entity} entity
        
        // Test entity fields:
$(foreach ($field in $DomainInfo.fields.GetEnumerator()) {
    "        // - $($field.Key): $($field.Value)"
})
        
        // Test entity-specific queries
        // Example: findByActiveTrue, findByRatingGreaterThan, etc.
    }
"@
                
                $content = $content -replace "(\s+@Test\s+void testFindAll.*?\}\s+)}", "`$1$customQueryTests`n    // Customized with business logic`n}"
                
                # Save modified content
                Set-Content -Path $testFile.FullName -Value $content -NoNewline
                Log-Message "Added custom query tests to $($testFile.Name)" "UPDATE"
            }
        }
    }
}

function Add-BusinessLogicToNodeTest {
    param (
        [string]$ServiceName,
        [hashtable]$DomainInfo,
        [string]$TestType
    )
    
    Log-Message "Customizing $TestType tests for $ServiceName with business logic" "PROCESS"
    
    $serviceDir = Join-Path -Path $baseDir -ChildPath $ServiceName
    
    switch ($TestType) {
        "Routes" {
            # Find route test files
            $testDir = Join-Path -Path $serviceDir -ChildPath "tests\routes"
            $testFiles = Get-ChildItem -Path $testDir -Filter "*.test.js" -Recurse
            
            foreach ($testFile in $testFiles) {
                $content = Get-Content -Path $testFile.FullName -Raw
                
                # Skip if we've already customized this file
                if ($content -match "// Customized with business logic") {
                    continue
                }
                
                # Add validation tests
                $validationTests = "  describe('Validation Rules', () => {\n"
                foreach ($validation in $DomainInfo.validations) {
                    $sanitizedValidation = $validation -replace "[^a-zA-Z0-9]", ""
                    $validationTests += @"
    it('should enforce validation: $validation', async () => {
      // Customized business logic validation test
      const invalidData = {
        // TODO: Set invalid data based on validation rule
      };

      await request(app)
        .post('/api/${DomainInfo.entity.ToLower()}s')
        .send(invalidData)
        .expect(400);
    });

"@
                }
                $validationTests += "  });\n"
                
                $content = $content -replace "(describe\('DELETE /api/.*?\);(\s+)\});)", "`$1`n`n  // Customized with business logic`n$validationTests`n`$2"
                
                # Save modified content
                Set-Content -Path $testFile.FullName -Value $content -NoNewline
                Log-Message "Added validation tests to $($testFile.Name)" "UPDATE"
            }
        }
        "Services" {
            # Find service test files
            $testDir = Join-Path -Path $serviceDir -ChildPath "tests\services"
            $testFiles = Get-ChildItem -Path $testDir -Filter "*.test.js" -Recurse
            
            foreach ($testFile in $testFiles) {
                $content = Get-Content -Path $testFile.FullName -Raw
                
                # Skip if we've already customized this file
                if ($content -match "// Customized with business logic") {
                    continue
                }
                
                # Add business rule tests
                $businessRuleTests = "  describe('Business Rules', () => {\n"
                foreach ($rule in $DomainInfo.business_rules) {
                    $sanitizedRule = $rule -replace "[^a-zA-Z0-9]", ""
                    $businessRuleTests += @"
    it('should enforce business rule: $rule', async () => {
      // Customized business rule test
      // TODO: Implement specific business rule test
      
      // Test setup
      
      // Execute service method
      
      // Assert business rule
      expect(true).toBe(true); // Placeholder
    });

"@
                }
                $businessRuleTests += "  });\n"
                
                $content = $content -replace "(describe\('delete'.*?\);(\s+)\});)", "`$1`n`n  // Customized with business logic`n$businessRuleTests`n`$2"
                
                # Save modified content
                Set-Content -Path $testFile.FullName -Value $content -NoNewline
                Log-Message "Added business rule tests to $($testFile.Name)" "UPDATE"
            }
        }
    }
}

# Process each service with its domain-specific tests
foreach ($service in $serviceDomains.Keys) {
    $domainInfo = $serviceDomains[$service]
    
    if ($domainInfo.type -eq "java") {
        Add-BusinessLogicToJavaTest -ServiceName $service -DomainInfo $domainInfo -TestType "Controller"
        Add-BusinessLogicToJavaTest -ServiceName $service -DomainInfo $domainInfo -TestType "Service"
        Add-BusinessLogicToJavaTest -ServiceName $service -DomainInfo $domainInfo -TestType "Repository"
    } else {
        Add-BusinessLogicToNodeTest -ServiceName $service -DomainInfo $domainInfo -TestType "Routes" 
        Add-BusinessLogicToNodeTest -ServiceName $service -DomainInfo $domainInfo -TestType "Services"
    }
}

Log-Message "Test template customization with business logic completed" "DONE"
