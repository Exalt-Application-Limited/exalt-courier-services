package com.microecosystem.courier.driver.app.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Integration test suite for Courier Services Migration.
 * 
 * This test suite runs all integration tests for the migrated courier services
 * from Field Staff Mobile app to Driver Mobile App.
 * 
 * @author Courier Services Migration Team
 * @version 1.0
 * @since 2025-05-25
 */
@Suite
@SuiteDisplayName("Courier Services Integration Test Suite")
@DisplayName("Integration Tests for Migrated Courier Services")
@SelectPackages("com.microecosystem.courier.driver.app.integration")
@IncludeClassNamePatterns(".*IntegrationTest")
public class CourierServicesIntegrationTestSuite {
    // This class serves as a test suite runner for all integration tests
    // No implementation needed - JUnit 5 will discover and run all tests
    // that match the specified criteria
}
