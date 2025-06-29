package com.microecosystem.courier.verification;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

/**
 * Courier Services Readiness Verification Tool
 * 
 * This tool systematically verifies all components in the courier services ecosystem
 * for GitHub readiness and deployment completeness.
 * 
 * @author Courier Services Migration Team
 * @version 1.0
 * @since 2025-05-25
 */
public class CourierServicesReadinessVerifier {
    
    private static final String BASE_PATH = "C:\\Users\\frich\\Desktop\\Micro-Social-Ecommerce-Ecosystems\\social-ecommerce-ecosystem\\courier-services";
    
    // Essential files that should be present in each service
    private static final List<String> ESSENTIAL_FILES = Arrays.asList(
        "README.md",
        "pom.xml",
        "Dockerfile",
        "docker-compose.yml"
    );
    
    // Essential directories that should be present
    private static final List<String> ESSENTIAL_DIRECTORIES = Arrays.asList(
        "src",
        ".github",
        "docs",
        "api-docs"
    );
    
    // Source code structure validation
    private static final List<String> JAVA_SOURCE_STRUCTURE = Arrays.asList(
        "src/main/java",
        "src/main/resources",
        "src/test/java"
    );
    
    private Map<String, ComponentReadinessStatus> componentStatuses = new HashMap<>();
    
    public static void main(String[] args) {
        CourierServicesReadinessVerifier verifier = new CourierServicesReadinessVerifier();
        verifier.verifyAllComponents();
        verifier.generateReport();
    }
    
    public void verifyAllComponents() {
        try {
            Files.list(Paths.get(BASE_PATH))
                .filter(Files::isDirectory)
                .filter(path -> !path.getFileName().toString().equals("readiness-reports"))
                .forEach(this::verifyComponent);
        } catch (IOException e) {
            System.err.println("Error reading base directory: " + e.getMessage());
        }
    }
    
    private void verifyComponent(Path componentPath) {
        String componentName = componentPath.getFileName().toString();
        ComponentReadinessStatus status = new ComponentReadinessStatus(componentName);
        
        // Check essential files
        checkEssentialFiles(componentPath, status);
        
        // Check essential directories
        checkEssentialDirectories(componentPath, status);
        
        // Check source code structure
        checkSourceCodeStructure(componentPath, status);
        
        // Check documentation
        checkDocumentation(componentPath, status);
        
        // Check configuration
        checkConfiguration(componentPath, status);
        
        // Check tests
        checkTests(componentPath, status);
        
        // Calculate overall readiness
        calculateOverallReadiness(status);
        
        componentStatuses.put(componentName, status);
    }
    
    private void checkEssentialFiles(Path componentPath, ComponentReadinessStatus status) {
        for (String file : ESSENTIAL_FILES) {
            boolean exists = Files.exists(componentPath.resolve(file));
            status.addFileCheck(file, exists);
        }
    }
    
    private void checkEssentialDirectories(Path componentPath, ComponentReadinessStatus status) {
        for (String dir : ESSENTIAL_DIRECTORIES) {
            boolean exists = Files.isDirectory(componentPath.resolve(dir));
            status.addDirectoryCheck(dir, exists);
        }
    }
    
    private void checkSourceCodeStructure(Path componentPath, ComponentReadinessStatus status) {
        for (String structure : JAVA_SOURCE_STRUCTURE) {
            boolean exists = Files.isDirectory(componentPath.resolve(structure));
            status.addSourceStructureCheck(structure, exists);
        }
        
        // Check for main application class
        checkForMainClass(componentPath, status);
    }
    
    private void checkForMainClass(Path componentPath, ComponentReadinessStatus status) {
        try {
            Path javaPath = componentPath.resolve("src/main/java");
            if (Files.exists(javaPath)) {
                boolean hasMainClass = Files.walk(javaPath)
                    .filter(path -> path.toString().endsWith("Application.java"))
                    .findAny()
                    .isPresent();
                status.addSourceStructureCheck("Application.java", hasMainClass);
            }
        } catch (IOException e) {
            status.addSourceStructureCheck("Application.java", false);
        }
    }
    
    private void checkDocumentation(Path componentPath, ComponentReadinessStatus status) {
        // Check README.md content
        checkReadmeContent(componentPath, status);
        
        // Check API documentation
        checkApiDocumentation(componentPath, status);
    }
    
    private void checkReadmeContent(Path componentPath, ComponentReadinessStatus status) {
        Path readmePath = componentPath.resolve("README.md");
        if (Files.exists(readmePath)) {
            try {
                String content = Files.readString(readmePath);
                boolean hasTitle = content.contains("#");
                boolean hasDescription = content.length() > 100;
                boolean hasSetupInstructions = content.toLowerCase().contains("setup") || 
                                             content.toLowerCase().contains("install") ||
                                             content.toLowerCase().contains("getting started");
                
                status.addDocumentationCheck("README has title", hasTitle);
                status.addDocumentationCheck("README has description", hasDescription);
                status.addDocumentationCheck("README has setup instructions", hasSetupInstructions);
            } catch (IOException e) {
                status.addDocumentationCheck("README readable", false);
            }
        }
    }
    
    private void checkApiDocumentation(Path componentPath, ComponentReadinessStatus status) {
        boolean hasOpenApi = Files.exists(componentPath.resolve("api-docs/openapi.yaml")) ||
                           Files.exists(componentPath.resolve("api-docs/swagger.json"));
        status.addDocumentationCheck("API documentation", hasOpenApi);
    }
    
    private void checkConfiguration(Path componentPath, ComponentReadinessStatus status) {
        // Check application configuration
        boolean hasAppConfig = Files.exists(componentPath.resolve("src/main/resources/application.yml")) ||
                              Files.exists(componentPath.resolve("src/main/resources/application.properties"));
        status.addConfigurationCheck("Application configuration", hasAppConfig);
        
        // Check Docker configuration
        boolean hasDockerfile = Files.exists(componentPath.resolve("Dockerfile"));
        boolean hasDockerCompose = Files.exists(componentPath.resolve("docker-compose.yml"));
        status.addConfigurationCheck("Dockerfile", hasDockerfile);
        status.addConfigurationCheck("Docker Compose", hasDockerCompose);
        
        // Check Kubernetes configuration
        boolean hasK8sConfig = Files.exists(componentPath.resolve("k8s")) &&
                              Files.isDirectory(componentPath.resolve("k8s"));
        status.addConfigurationCheck("Kubernetes config", hasK8sConfig);
    }
    
    private void checkTests(Path componentPath, ComponentReadinessStatus status) {
        Path testPath = componentPath.resolve("src/test/java");
        if (Files.exists(testPath)) {
            try {
                long testFileCount = Files.walk(testPath)
                    .filter(path -> path.toString().endsWith("Test.java"))
                    .count();
                status.addTestCheck("Unit tests present", testFileCount > 0);
                status.addTestCheck("Test count", testFileCount);
            } catch (IOException e) {
                status.addTestCheck("Unit tests present", false);
            }
        } else {
            status.addTestCheck("Test directory exists", false);
        }
    }
    
    private void calculateOverallReadiness(ComponentReadinessStatus status) {
        int totalChecks = status.getTotalChecks();
        int passedChecks = status.getPassedChecks();
        
        if (totalChecks > 0) {
            double percentage = (double) passedChecks / totalChecks * 100;
            status.setReadinessPercentage(percentage);
            
            if (percentage >= 90) {
                status.setReadinessLevel("READY");
            } else if (percentage >= 70) {
                status.setReadinessLevel("MOSTLY READY");
            } else if (percentage >= 50) {
                status.setReadinessLevel("NEEDS WORK");
            } else {
                status.setReadinessLevel("NOT READY");
            }
        }
    }
    
    public void generateReport() {
        System.out.println("=".repeat(80));
        System.out.println("COURIER SERVICES ECOSYSTEM - READINESS VERIFICATION REPORT");
        System.out.println("=".repeat(80));
        System.out.println();
        
        // Summary statistics
        generateSummaryStatistics();
        
        // Individual component reports
        generateIndividualReports();
        
        // Overall recommendations
        generateRecommendations();
    }
    
    private void generateSummaryStatistics() {
        System.out.println("📊 SUMMARY STATISTICS");
        System.out.println("-".repeat(50));
        
        int totalComponents = componentStatuses.size();
        long readyComponents = componentStatuses.values().stream()
            .mapToLong(status -> "READY".equals(status.getReadinessLevel()) ? 1 : 0)
            .sum();
        
        double overallReadiness = componentStatuses.values().stream()
            .mapToDouble(ComponentReadinessStatus::getReadinessPercentage)
            .average()
            .orElse(0.0);
        
        System.out.printf("Total Components: %d%n", totalComponents);
        System.out.printf("Ready Components: %d%n", readyComponents);
        System.out.printf("Overall Readiness: %.1f%%%n", overallReadiness);
        System.out.println();
    }
    
    private void generateIndividualReports() {
        System.out.println("📋 INDIVIDUAL COMPONENT REPORTS");
        System.out.println("-".repeat(50));
        
        componentStatuses.entrySet().stream()
            .sorted(Map.Entry.<String, ComponentReadinessStatus>comparingByValue(
                Comparator.comparing(ComponentReadinessStatus::getReadinessPercentage).reversed()))
            .forEach(entry -> {
                ComponentReadinessStatus status = entry.getValue();
                System.out.printf("🏗️  %s [%s - %.1f%%]%n", 
                    entry.getKey(), 
                    status.getReadinessLevel(), 
                    status.getReadinessPercentage());
                
                if (status.getReadinessPercentage() < 90) {
                    System.out.println("   Issues found:");
                    status.getFailedChecks().forEach(check -> 
                        System.out.printf("   ❌ %s%n", check));
                }
                System.out.println();
            });
    }
    
    private void generateRecommendations() {
        System.out.println("💡 RECOMMENDATIONS");
        System.out.println("-".repeat(50));
        
        // Components needing attention
        componentStatuses.entrySet().stream()
            .filter(entry -> entry.getValue().getReadinessPercentage() < 90)
            .forEach(entry -> {
                System.out.printf("⚠️  %s needs attention:%n", entry.getKey());
                entry.getValue().getFailedChecks().forEach(check -> 
                    System.out.printf("   - Fix: %s%n", check));
                System.out.println();
            });
        
        System.out.println("📝 Next Steps:");
        System.out.println("1. Address issues in components with readiness < 90%");
        System.out.println("2. Ensure all components have comprehensive tests");
        System.out.println("3. Verify all documentation is complete and up-to-date");
        System.out.println("4. Configure CI/CD pipelines for all components");
        System.out.println("5. Set up monitoring and logging for production deployment");
    }
    
    // Inner class for tracking component status
    private static class ComponentReadinessStatus {
        private final String componentName;
        private final Map<String, Boolean> checks = new HashMap<>();
        private final List<String> failedChecks = new ArrayList<>();
        private double readinessPercentage = 0.0;
        private String readinessLevel = "UNKNOWN";
        
        public ComponentReadinessStatus(String componentName) {
            this.componentName = componentName;
        }
        
        public void addFileCheck(String file, boolean exists) {
            checks.put("File: " + file, exists);
            if (!exists) failedChecks.add("Missing file: " + file);
        }
        
        public void addDirectoryCheck(String dir, boolean exists) {
            checks.put("Directory: " + dir, exists);
            if (!exists) failedChecks.add("Missing directory: " + dir);
        }
        
        public void addSourceStructureCheck(String structure, boolean exists) {
            checks.put("Source: " + structure, exists);
            if (!exists) failedChecks.add("Missing source structure: " + structure);
        }
        
        public void addDocumentationCheck(String doc, boolean exists) {
            checks.put("Documentation: " + doc, exists);
            if (!exists) failedChecks.add("Documentation issue: " + doc);
        }
        
        public void addConfigurationCheck(String config, boolean exists) {
            checks.put("Configuration: " + config, exists);
            if (!exists) failedChecks.add("Missing configuration: " + config);
        }
        
        public void addTestCheck(String test, boolean exists) {
            checks.put("Test: " + test, exists);
            if (!exists) failedChecks.add("Test issue: " + test);
        }
        
        public void addTestCheck(String test, long count) {
            boolean hasTests = count > 0;
            checks.put("Test: " + test, hasTests);
            if (!hasTests) failedChecks.add("No tests found");
        }
        
        public int getTotalChecks() {
            return checks.size();
        }
        
        public int getPassedChecks() {
            return (int) checks.values().stream().mapToLong(passed -> passed ? 1 : 0).sum();
        }
        
        public double getReadinessPercentage() {
            return readinessPercentage;
        }
        
        public void setReadinessPercentage(double percentage) {
            this.readinessPercentage = percentage;
        }
        
        public String getReadinessLevel() {
            return readinessLevel;
        }
        
        public void setReadinessLevel(String level) {
            this.readinessLevel = level;
        }
        
        public List<String> getFailedChecks() {
            return failedChecks;
        }
    }
}
