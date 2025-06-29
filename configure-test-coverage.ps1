# Script to configure test coverage reporting for courier services
# Sets up JaCoCo for Java services and Istanbul/NYC for Node.js services

function Log-Message {
    param (
        [string]$Message,
        [string]$Type = "INFO"
    )
    $timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    Write-Host "[$timestamp] [$Type] $Message" -ForegroundColor $(
        switch ($Type) {
            "INFO" { "White" }
            "SUCCESS" { "Green" }
            "WARNING" { "Yellow" }
            "ERROR" { "Red" }
            default { "Gray" }
        }
    )
}

$baseDir = "C:\Users\frich\Desktop\Exalt-Application-Limited\Exalt-Application-Limited\social-ecommerce-ecosystem\courier-services"
Set-Location $baseDir

# Java services list
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

# Node.js services list
$nodeServices = @(
    "courier-network-locations",
    "courier-events-service",
    "courier-geo-routing",
    "courier-fare-calculator",
    "courier-pickup-engine",
    "courier-location-tracker"
)

# Function to configure JaCoCo for a Java service
function Configure-JaCoCo {
    param (
        [string]$ServicePath
    )
    
    $pomPath = Join-Path -Path $ServicePath -ChildPath "pom.xml"
    
    if (-not (Test-Path $pomPath)) {
        Log-Message "pom.xml not found in $ServicePath" "WARNING"
        return
    }
    
    $pomContent = Get-Content -Path $pomPath -Raw
    
    # Check if JaCoCo is already configured
    if ($pomContent -match "<artifactId>jacoco-maven-plugin</artifactId>") {
        Log-Message "JaCoCo already configured in $(Split-Path -Leaf $ServicePath)" "INFO"
        return
    }
    
    # Add JaCoCo plugin configuration
    $jacocoConfig = @"
            <!-- JaCoCo Test Coverage Configuration -->
            <plugin>
                <groupId>org.jacoco</groupId>
                <artifactId>jacoco-maven-plugin</artifactId>
                <version>0.8.8</version>
                <executions>
                    <execution>
                        <id>prepare-agent</id>
                        <goals>
                            <goal>prepare-agent</goal>
                        </goals>
                    </execution>
                    <execution>
                        <id>report</id>
                        <phase>test</phase>
                        <goals>
                            <goal>report</goal>
                        </goals>
                    </execution>
                    <execution>
                        <id>check</id>
                        <goals>
                            <goal>check</goal>
                        </goals>
                        <configuration>
                            <rules>
                                <rule>
                                    <element>BUNDLE</element>
                                    <limits>
                                        <limit>
                                            <counter>LINE</counter>
                                            <value>COVEREDRATIO</value>
                                            <minimum>0.60</minimum>
                                        </limit>
                                    </limits>
                                </rule>
                            </rules>
                            <excludes>
                                <exclude>**/*Application.*</exclude>
                                <exclude>**/config/**/*</exclude>
                                <exclude>**/model/**/*</exclude>
                                <exclude>**/dto/**/*</exclude>
                            </excludes>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
"@
    
    # Insert JaCoCo plugin into the build plugins section
    $pomContent = $pomContent -replace "(<plugins>.*?)(\s*</plugins>)", "`$1`n$jacocoConfig`$2"
    
    # Save updated pom.xml
    Set-Content -Path $pomPath -Value $pomContent -NoNewline
    
    Log-Message "JaCoCo configured successfully in $(Split-Path -Leaf $ServicePath)" "SUCCESS"
}

# Function to configure Istanbul/NYC for a Node.js service
function Configure-Istanbul {
    param (
        [string]$ServicePath
    )
    
    $packageJsonPath = Join-Path -Path $ServicePath -ChildPath "package.json"
    
    if (-not (Test-Path $packageJsonPath)) {
        Log-Message "package.json not found in $ServicePath" "WARNING"
        return
    }
    
    try {
        $packageJson = Get-Content -Path $packageJsonPath -Raw | ConvertFrom-Json
        
        # Check if Istanbul/NYC is already configured
        $testScript = $packageJson.scripts.test
        if ($testScript -and $testScript -match "nyc") {
            Log-Message "Istanbul/NYC already configured in $(Split-Path -Leaf $ServicePath)" "INFO"
        }
        else {
            # Add NYC as dev dependency if not already present
            if (-not $packageJson.devDependencies.nyc) {
                if (-not $packageJson.PSObject.Properties.Name -contains "devDependencies") {
                    $packageJson | Add-Member -Type NoteProperty -Name "devDependencies" -Value @{}
                }
                
                $packageJson.devDependencies | Add-Member -Type NoteProperty -Name "nyc" -Value "^15.1.0" -Force
            }
            
            # Update test script to include coverage
            if (-not $packageJson.PSObject.Properties.Name -contains "scripts") {
                $packageJson | Add-Member -Type NoteProperty -Name "scripts" -Value @{}
            }
            
            # Keep existing test command if any
            $existingTest = if ($packageJson.scripts.test) { $packageJson.scripts.test } else { "jest" }
            
            # Set test script with NYC
            $packageJson.scripts.test = $existingTest
            $packageJson.scripts | Add-Member -Type NoteProperty -Name "test:coverage" -Value "nyc --reporter=lcov --reporter=text $existingTest" -Force
            
            # Add NYC configuration
            if (-not $packageJson.PSObject.Properties.Name -contains "nyc") {
                $nycConfig = [PSCustomObject]@{
                    include = @("src/**/*.js")
                    exclude = @("**/*.spec.js", "**/*.test.js", "**/node_modules/**")
                    "check-coverage" = $true
                    "per-file" = $true
                    lines = 70
                    statements = 70
                    functions = 70
                    branches = 60
                }
                
                $packageJson | Add-Member -Type NoteProperty -Name "nyc" -Value $nycConfig -Force
            }
            
            # Save updated package.json with proper formatting
            $packageJsonContent = $packageJson | ConvertTo-Json -Depth 10
            Set-Content -Path $packageJsonPath -Value $packageJsonContent -NoNewline
            
            Log-Message "Istanbul/NYC configured successfully in $(Split-Path -Leaf $ServicePath)" "SUCCESS"
        }
    }
    catch {
        Log-Message "Error configuring Istanbul/NYC in $(Split-Path -Leaf $ServicePath): $_" "ERROR"
    }
}

# Configure JaCoCo for each Java service
Log-Message "Configuring JaCoCo for Java services..." "INFO"
foreach ($service in $javaServices) {
    $servicePath = Join-Path -Path $baseDir -ChildPath $service
    if (Test-Path $servicePath) {
        Configure-JaCoCo -ServicePath $servicePath
    }
    else {
        Log-Message "$service directory not found" "WARNING"
    }
}

# Configure Istanbul/NYC for each Node.js service
Log-Message "Configuring Istanbul/NYC for Node.js services..." "INFO"
foreach ($service in $nodeServices) {
    $servicePath = Join-Path -Path $baseDir -ChildPath $service
    if (Test-Path $servicePath) {
        Configure-Istanbul -ServicePath $servicePath
    }
    else {
        Log-Message "$service directory not found" "WARNING"
    }
}

Log-Message "Test coverage configuration completed" "SUCCESS"
