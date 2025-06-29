# Courier Services Test Standardization Guide

This guide documents the standardized testing approach for all courier-services domain microservices within the Social E-commerce Ecosystem.

## 1. Testing Framework Overview

We've implemented a comprehensive testing framework that ensures:

- **Cloud-compatible testing**: All tests run without local Docker dependencies
- **Regression prevention**: Multiple checks to prevent breaking changes
- **Consistent approach**: Standardized patterns across Java and Node.js services
- **Automated reporting**: Coverage analysis and consolidated test reports
- **CI/CD integration**: Automated testing in GitHub Actions workflows

## 2. Testing Components

### 2.1 Unit Tests

- **Java Services**: JUnit 5 with Mockito for controllers, services, repositories
- **Node.js Services**: Jest with mocking utilities for routes, services, middleware

### 2.2 Integration Tests

- **Java Services**: Spring Cloud OpenFeign clients with WireMock
- **Node.js Services**: Axios-based client classes with Nock for HTTP mocking

### 2.3 Test Data Generation

- **Java Services**: `TestDataFactory` classes with static factory methods
- **Node.js Services**: Test data factories with configurable fields

### 2.4 Coverage Reporting

- **Java Services**: JaCoCo configured at 60% minimum line coverage
- **Node.js Services**: Istanbul/NYC with 70% functions/statements coverage

## 3. Test Execution Scripts

| Script | Purpose |
|--------|---------|
| `generate-java-tests.ps1` | Creates standard Java test templates |
| `generate-nodejs-tests.ps1` | Creates standard Node.js test templates |
| `customize-test-templates.ps1` | Adds domain-specific business rules to test templates |
| `java-integration-tests.ps1` | Generates Java integration tests with WireMock |
| `nodejs-integration-tests.ps1` | Generates Node.js integration tests with Nock |
| `generate-test-data.ps1` | Creates test data factories for consistent test objects |
| `configure-test-coverage.ps1` | Sets up JaCoCo and Istanbul coverage tools |
| `run-tests-and-report.ps1` | Executes all tests and generates reports |
| `validate-no-regression.ps1` | API compatibility checks between versions |
| `setup-git-hooks.ps1` | Pre-commit and pre-push validation scripts |

## 4. Regression Prevention

Multiple layers of protection against regression:

1. **Pre-commit hooks**: Run tests and linting before code is committed
2. **API snapshots**: Track API changes to detect breaking changes
3. **Coverage thresholds**: Enforce minimum test coverage standards
4. **GitHub Action workflows**: Automated CI/CD pipeline validation
5. **Integration tests**: Validate service-to-service interactions

## 5. Standard Test Execution

To run all tests and generate reports:

```powershell
# From the courier-services directory
.\run-tests-and-report.ps1
```

This will:
- Execute all tests for both Java and Node.js services
- Generate coverage reports
- Create a consolidated HTML summary
- Report any test failures

## 6. Adding Tests for New Features

When adding new features:

1. **Controllers/Routes**: Add tests verifying request handling, status codes, response formats
2. **Services**: Add tests for business logic, validation rules, error handling
3. **Repositories/Data Access**: Add tests for query execution, data transformation
4. **Integration**: Add tests for service-to-service communication

## 7. GitHub Actions Workflow

The CI/CD workflow follows these steps:

1. Detect which services changed in the commit/PR
2. Run appropriate tests for those services
3. Generate and upload test reports
4. Create consolidated summary
5. Fail the build if tests fail or coverage drops

## 8. Developer Workflow

1. Write code implementing the feature
2. Run `.\run-tests-and-report.ps1` to ensure all tests pass
3. Commit code (pre-commit hooks will verify basics)
4. Push changes (pre-push hooks verify more extensively)
5. CI/CD pipeline runs full test suite

## 9. Troubleshooting Common Issues

- **Test failures**: Check test logs in the `test-reports` directory
- **Missing dependencies**: Ensure all required packages are in pom.xml or package.json
- **Coverage threshold failures**: Add tests for uncovered code paths
- **API regression warnings**: Review API changes for backward compatibility

## 10. Continuous Improvement

This testing infrastructure is designed to evolve:

- Periodically review and increase coverage thresholds
- Expand test suites as new features are added
- Refine test patterns based on discovered bugs
- Add performance tests for critical paths

By following this standardized approach, we maintain high quality across all courier services while enabling agile development without regression.

**Last Updated**: June 20, 2025
