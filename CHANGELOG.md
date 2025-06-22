# Changelog

## [Unreleased]

### Added
- Overloaded `retry()` methods supporting both `Supplier<String>` and `Runnable` for CLI retry handling
- `Exiter` functional interface to replace direct `System.exit()` calls for testability
- Unit tests for exit behavior using injectable `Exiter` with `RuntimeException` interception
- Dockerfile for containerized CLI execution with volume support
- Instructions in README for building and running via Docker

### Changed
- Refactored `AuthService`, `MenuService`, and `FintechApp` to accept `Exiter` for clean exit handling
- Improved file initialization for `accounts.json` and `transactions.json` to support Docker runtime paths
- CLI prompts made more robust with input validation loops

### Fixed
- FileNotFound and Permission errors when writing to `src/main/resources/` inside Docker
- Mockito type inference issues with generic `Supplier<String>` in retry method tests
- Test case interruptions due to unmocked `System.exit()` calls

---

## [v1.0.0] - Initial Release

### Added
- CLI menu for account signup, login, deposit, withdraw
- JSON persistence with SHA-256 password hashing
- Basic unit test coverage with JUnit 5 and Mockito
