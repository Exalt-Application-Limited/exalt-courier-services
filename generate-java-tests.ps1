# Script to generate test templates for Java services
# Creates test classes for controllers, services, and repositories

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

# List of Java services
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

# Controller test template
$controllerTestTemplate = @"
package {{PACKAGE}}.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import {{PACKAGE}}.controller.{{CLASS_NAME}};
import {{PACKAGE}}.service.{{SERVICE_CLASS}};
import {{PACKAGE}}.model.{{MODEL_CLASS}};
import {{PACKAGE}}.dto.{{DTO_CLASS}};

@WebMvcTest({{CLASS_NAME}}.class)
public class {{CLASS_NAME}}Test {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private {{SERVICE_CLASS}} service;

    @Autowired
    private ObjectMapper objectMapper;

    private {{DTO_CLASS}} testDto;
    private {{MODEL_CLASS}} testEntity;

    @BeforeEach
    void setUp() {
        // TODO: Initialize test data
        testDto = new {{DTO_CLASS}}();
        // Set properties on testDto
        
        testEntity = new {{MODEL_CLASS}}();
        // Set properties on testEntity
    }

    @Test
    void testGetAll() throws Exception {
        List<{{MODEL_CLASS}}> entities = Arrays.asList(testEntity);
        when(service.findAll()).thenReturn(entities);

        mockMvc.perform(get("/api/{{API_PATH}}"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].id").exists());
    }

    @Test
    void testGetById() throws Exception {
        when(service.findById(any())).thenReturn(Optional.of(testEntity));

        mockMvc.perform(get("/api/{{API_PATH}}/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void testCreate() throws Exception {
        when(service.save(any())).thenReturn(testEntity);

        mockMvc.perform(post("/api/{{API_PATH}}")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(testDto)))
               .andExpect(status().isCreated());
    }

    @Test
    void testUpdate() throws Exception {
        when(service.findById(any())).thenReturn(Optional.of(testEntity));
        when(service.save(any())).thenReturn(testEntity);

        mockMvc.perform(put("/api/{{API_PATH}}/1")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(testDto)))
               .andExpect(status().isOk());
    }

    @Test
    void testDelete() throws Exception {
        when(service.findById(any())).thenReturn(Optional.of(testEntity));

        mockMvc.perform(delete("/api/{{API_PATH}}/1"))
               .andExpect(status().isNoContent());
    }
}
"@

# Service test template
$serviceTestTemplate = @"
package {{PACKAGE}}.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import {{PACKAGE}}.repository.{{REPO_CLASS}};
import {{PACKAGE}}.model.{{MODEL_CLASS}};

@ExtendWith(MockitoExtension.class)
public class {{CLASS_NAME}}Test {

    @Mock
    private {{REPO_CLASS}} repository;

    @InjectMocks
    private {{CLASS_NAME}} service;

    private {{MODEL_CLASS}} testEntity;

    @BeforeEach
    void setUp() {
        // TODO: Initialize test data
        testEntity = new {{MODEL_CLASS}}();
        // Set properties on testEntity
    }

    @Test
    void testFindAll() {
        when(repository.findAll()).thenReturn(Arrays.asList(testEntity));

        List<{{MODEL_CLASS}}> result = service.findAll();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void testFindById() {
        when(repository.findById(any())).thenReturn(Optional.of(testEntity));

        Optional<{{MODEL_CLASS}}> result = service.findById(1L);

        assertTrue(result.isPresent());
    }

    @Test
    void testSave() {
        when(repository.save(any())).thenReturn(testEntity);

        {{MODEL_CLASS}} result = service.save(testEntity);

        assertNotNull(result);
        verify(repository, times(1)).save(testEntity);
    }

    @Test
    void testDelete() {
        service.deleteById(1L);

        verify(repository, times(1)).deleteById(1L);
    }
}
"@

# Repository test template
$repositoryTestTemplate = @"
package {{PACKAGE}}.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import {{PACKAGE}}.model.{{MODEL_CLASS}};

@DataJpaTest
public class {{CLASS_NAME}}Test {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private {{CLASS_NAME}} repository;

    @Test
    void testSaveAndFind() {
        // Create test entity
        {{MODEL_CLASS}} entity = new {{MODEL_CLASS}}();
        // TODO: Set entity properties
        
        // Save entity
        {{MODEL_CLASS}} savedEntity = entityManager.persistAndFlush(entity);
        
        // Find by ID
        Optional<{{MODEL_CLASS}}> foundEntity = repository.findById(savedEntity.getId());
        
        assertTrue(foundEntity.isPresent());
        assertEquals(savedEntity.getId(), foundEntity.get().getId());
    }

    @Test
    void testFindAll() {
        // Create test entities
        {{MODEL_CLASS}} entity1 = new {{MODEL_CLASS}}();
        // TODO: Set entity1 properties
        
        {{MODEL_CLASS}} entity2 = new {{MODEL_CLASS}}();
        // TODO: Set entity2 properties
        
        // Save entities
        entityManager.persistAndFlush(entity1);
        entityManager.persistAndFlush(entity2);
        
        // Find all
        Iterable<{{MODEL_CLASS}}> allEntities = repository.findAll();
        
        int count = 0;
        for (@SuppressWarnings("unused") {{MODEL_CLASS}} entity : allEntities) {
            count++;
        }
        
        assertTrue(count >= 2);
    }
}
"@

foreach ($service in $javaServices) {
    $serviceDir = Join-Path -Path $baseDir -ChildPath $service
    $srcMainDir = Join-Path -Path $serviceDir -ChildPath "src\main\java"
    $srcTestDir = Join-Path -Path $serviceDir -ChildPath "src\test\java"
    
    if (-not (Test-Path $srcTestDir)) {
        New-Item -ItemType Directory -Path $srcTestDir -Force | Out-Null
        Log-Message "Created test directory for $service" "CREATE"
    }
    
    Log-Message "Generating test templates for $service" "PROCESS"
    
    # Find controllers, services and repositories
    $controllers = Get-ChildItem -Path $srcMainDir -Filter "*Controller.java" -Recurse
    $services = Get-ChildItem -Path $srcMainDir -Filter "*Service.java" -Recurse | Where-Object { $_.Name -notmatch "Interface" }
    $repositories = Get-ChildItem -Path $srcMainDir -Filter "*Repository.java" -Recurse
    
    # Create controller tests
    foreach ($controller in $controllers) {
        $content = Get-Content -Path $controller.FullName -Raw
        
        # Extract package
        if ($content -match "package\s+([^;]+);") {
            $package = $matches[1]
            $testPackage = $package -replace "\.controller$", ""
            
            # Extract class name
            if ($controller.Name -match "([^\\]+)\.java$") {
                $className = $matches[1]
                
                # Determine related classes
                $serviceClass = $className -replace "Controller", "Service"
                $modelName = $className -replace "Controller", ""
                $dtoName = "${modelName}DTO"
                $apiPath = $modelName.ToLower()
                
                # Create test file path - same package structure but in test directory
                $testPackagePath = $testPackage.Replace(".", "\")
                $testPackageDir = Join-Path -Path $srcTestDir -ChildPath $testPackagePath
                
                if (-not (Test-Path $testPackageDir)) {
                    New-Item -ItemType Directory -Path $testPackageDir -Force | Out-Null
                }
                
                $testFilePath = Join-Path -Path $testPackageDir -ChildPath "controller\${className}Test.java"
                $parentDir = Split-Path -Parent $testFilePath
                
                if (-not (Test-Path $parentDir)) {
                    New-Item -ItemType Directory -Path $parentDir -Force | Out-Null
                }
                
                # Create test file content
                $testContent = $controllerTestTemplate.Replace("{{PACKAGE}}", $testPackage)
                $testContent = $testContent.Replace("{{CLASS_NAME}}", $className)
                $testContent = $testContent.Replace("{{SERVICE_CLASS}}", $serviceClass)
                $testContent = $testContent.Replace("{{MODEL_CLASS}}", $modelName)
                $testContent = $testContent.Replace("{{DTO_CLASS}}", $dtoName)
                $testContent = $testContent.Replace("{{API_PATH}}", $apiPath)
                
                Set-Content -Path $testFilePath -Value $testContent -NoNewline
                Log-Message "Generated controller test for ${className}" "CREATE"
            }
        }
    }
    
    # Create service tests
    foreach ($service in $services) {
        $content = Get-Content -Path $service.FullName -Raw
        
        # Extract package
        if ($content -match "package\s+([^;]+);") {
            $package = $matches[1]
            $testPackage = $package -replace "\.service$", ""
            
            # Extract class name
            if ($service.Name -match "([^\\]+)\.java$") {
                $className = $matches[1]
                
                # Determine related classes
                $repoName = $className -replace "Service", "Repository"
                $modelName = $className -replace "Service", ""
                
                # Create test file path
                $testPackagePath = $testPackage.Replace(".", "\")
                $testPackageDir = Join-Path -Path $srcTestDir -ChildPath $testPackagePath
                
                if (-not (Test-Path $testPackageDir)) {
                    New-Item -ItemType Directory -Path $testPackageDir -Force | Out-Null
                }
                
                $testFilePath = Join-Path -Path $testPackageDir -ChildPath "service\${className}Test.java" 
                $parentDir = Split-Path -Parent $testFilePath
                
                if (-not (Test-Path $parentDir)) {
                    New-Item -ItemType Directory -Path $parentDir -Force | Out-Null
                }
                
                # Create test file content
                $testContent = $serviceTestTemplate.Replace("{{PACKAGE}}", $testPackage)
                $testContent = $testContent.Replace("{{CLASS_NAME}}", $className)
                $testContent = $testContent.Replace("{{REPO_CLASS}}", $repoName)
                $testContent = $testContent.Replace("{{MODEL_CLASS}}", $modelName)
                
                Set-Content -Path $testFilePath -Value $testContent -NoNewline
                Log-Message "Generated service test for ${className}" "CREATE"
            }
        }
    }
    
    # Create repository tests
    foreach ($repo in $repositories) {
        $content = Get-Content -Path $repo.FullName -Raw
        
        # Extract package
        if ($content -match "package\s+([^;]+);") {
            $package = $matches[1]
            $testPackage = $package -replace "\.repository$", ""
            
            # Extract class name
            if ($repo.Name -match "([^\\]+)\.java$") {
                $className = $matches[1]
                
                # Determine model class
                $modelName = $className -replace "Repository", ""
                
                # Create test file path
                $testPackagePath = $testPackage.Replace(".", "\")
                $testPackageDir = Join-Path -Path $srcTestDir -ChildPath $testPackagePath
                
                if (-not (Test-Path $testPackageDir)) {
                    New-Item -ItemType Directory -Path $testPackageDir -Force | Out-Null
                }
                
                $testFilePath = Join-Path -Path $testPackageDir -ChildPath "repository\${className}Test.java"
                $parentDir = Split-Path -Parent $testFilePath
                
                if (-not (Test-Path $parentDir)) {
                    New-Item -ItemType Directory -Path $parentDir -Force | Out-Null
                }
                
                # Create test file content
                $testContent = $repositoryTestTemplate.Replace("{{PACKAGE}}", $testPackage)
                $testContent = $testContent.Replace("{{CLASS_NAME}}", $className)
                $testContent = $testContent.Replace("{{MODEL_CLASS}}", $modelName)
                
                Set-Content -Path $testFilePath -Value $testContent -NoNewline
                Log-Message "Generated repository test for ${className}" "CREATE"
            }
        }
    }
}

Log-Message "Java test template generation completed" "DONE"
