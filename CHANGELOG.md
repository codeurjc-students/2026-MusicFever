# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]
### Added
#### Phase 2
- Created the Spring Boot backend and Angular frontend project structure.
- Implemented the minimum end-to-end functionality to retrieve and display sample data from the main entity.
- Added REST API documentation using OpenAPI.
- Added automated backend tests for unit, integration, E2E, and system levels.
- Added automated frontend tests for unit and integration scenarios.
- Added backend and frontend code coverage reporting.
- Configured basic and full CI quality checks, including SonarCloud analysis.
- Added separate execution environments for development, unit testing, integration/E2E testing, and system testing.
- Added PostgreSQL for local development and system testing, H2 for unit tests, and PostgreSQL Testcontainers for integration and E2E tests.
- Updated the README with local execution, testing, environment, and tooling instructions.
- Added the required project documentation for Phase 2.

#### Phase 1
- Defined the main objectives and functionalities of the application.
- Created the initial application mockups and interface design.
- Added project documentation covering:
    - Image and graphics processing.
    - Main entities and their relationships.
    - User roles and permissions.
    - Complementary technologies and tools.
    - Advanced algorithms and their potential application.
- Added GitHub Projects workflows to automatically move issues through the project board when their status changes.