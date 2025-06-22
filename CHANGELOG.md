# Changelog

## [Unreleased]

### Added
- Overloaded `retry()` methods to support both returnable and void actions (`Supplier<String>` and `Runnable`)
- `Exiter` functional interface to abstract `System.exit()` for improved testability
- Unit tests for retry and exit flows with mockable `Exiter`

### Changed
- `AuthService`, `MenuService`, and `FintechApp` constructors now support `Exiter` injection
- `System.exit()` calls replaced with `Exiter.exit(int)` in business logic
- CLI input handling now allows retrying on invalid input or exiting cleanly

### Fixed
- Mockito `any()` type ambiguity resolved using `ArgumentMatchers.<Supplier<String>>any()`
- Test isolation issues due to unmocked `System.exit()` calls
sssssss