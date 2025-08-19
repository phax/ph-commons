# Copilot Instructions for ph-commons

This document provides essential information for coding agents working with the ph-commons repository. Follow these instructions to work efficiently and avoid common build failures.

## Repository Overview

**ph-commons** is a comprehensive Java 17+ utility library providing common functionality for Java applications. It consists of 32 Maven modules with over 600k lines of code, developed and maintained by Philip Helger.

### Key Information
- **Language**: Java 17+ (supports Java 17, 21, 24)
- **Build System**: Apache Maven 3.x
- **License**: Apache License 2.0
- **Testing Framework**: JUnit 4
- **Package Structure**: `com.helger.*`

### Module Categories
- **Core**: `ph-base`, `ph-collection`, `ph-commons`
- **I/O & Serialization**: `ph-io`, `ph-xml`, `ph-json`
- **Web & Network**: `ph-http`, `ph-wsclient`, `ph-mime`
- **Security & Crypto**: `ph-security`, `ph-bc`
- **Configuration**: `ph-config`, `ph-config-json`, `ph-settings`
- **Testing**: `ph-unittest-support`, `ph-unittest-support-ext`
- **Specialized**: `ph-jaxb`, `ph-matrix`, `ph-graph`, `ph-tree`, `ph-text`

## Build Instructions

### Environment Requirements
- **Java**: JDK 17 or later (tested with 17, 21, 24)
- **Maven**: 3.x (tested with 3.9.11)
- **Memory**: Configure JVM with `-Xmx1024m` for tests

### Essential Build Commands

#### Standard Build Cycle
```bash
# Clean project (always run first after git operations)
mvn clean

# Compile all modules
mvn compile

# Install without tests (fast dependency resolution)
mvn install -DskipTests

# Full build with tests (~2 minutes)
mvn install

# Test specific module
mvn test -pl ph-base
```

#### Validation Commands
```bash
# Generate SpotBugs report (non-failing)
mvn spotbugs:spotbugs

# Check SpotBugs violations (may fail with known issues)
mvn spotbugs:check

# Run forbidden-apis check
mvn forbiddenapis:check
```

### Build Timing Expectations
- **Clean**: ~1 second
- **Compile**: ~30 seconds
- **Install (no tests)**: ~1 minute
- **Full build with tests**: ~2 minutes
- **Single module test**: ~5-10 seconds

### Critical Build Requirements

#### Dependency Resolution
**ALWAYS run `mvn install -DskipTests` before testing individual modules.** The multi-module structure requires all modules to be built and installed locally before running tests on specific modules.

#### Module Build Order
Maven automatically handles build order, but be aware of these key dependencies:
- `ph-base` → foundation for all other modules
- `ph-collection` → depends on `ph-base`
- `ph-commons` → aggregates functionality from other modules

## Project Architecture

### Source Organization
```
src/main/java/          # Main source code
src/main/resources/     # Resources and configuration
src/test/java/         # Test source code  
src/test/resources/    # Test resources
target/                # Build output (exclude from searches)
```

### Package Hierarchy
- `com.helger.base.*` - Core utilities, reflection, I/O
- `com.helger.collection.*` - Enhanced collections framework
- `com.helger.commons.*` - High-level utility functions
- Each module follows `com.helger.{module-name}.*` pattern

### Configuration Files
- `pom.xml` - Maven configuration in each module
- `findbugs-exclude.xml` - SpotBugs exclusions (in most modules)
- `META-INF/services/` - SPI configuration files

## Code Guidelines

### Coding Standards
Reference `ExtractedCodingStyleguide.md` for comprehensive guidelines. Key points:

#### Annotations (CRITICAL)
- Use `@Nonnull` and `@Nullable` consistently
- `@ReturnsMutableCopy` / `@ReturnsImmutableObject` for return values
- `@ThreadSafe` / `@NotThreadSafe` for class-level thread safety

#### Class Structure
1. Static fields first
2. Instance fields second  
3. Constructors third
4. Public methods fourth
5. Protected/package methods fifth
6. Private methods last

#### Error Handling
- Use `ValueEnforcer` for parameter validation
- Throw appropriate exceptions with descriptive messages
- Document exception conditions in JavaDoc

### Common Patterns
- **Builder Pattern**: Implement `IBuilder<T>` interface
- **Thread Safety**: Use `SimpleReadWriteLock` for thread-safe operations  
- **Resource Management**: Implement `AutoCloseable` where appropriate
- **Logging**: Private static final `Logger` instances

## Continuous Integration

### GitHub Actions
- **Trigger**: Push to any branch, PRs to master
- **Java Versions**: 17, 21, 24 (matrix build)
- **Commands**: `mvn --batch-mode --update-snapshots install`
- **Deployment**: Snapshots deployed to Maven Central (Java 17 only)

### Pre-commit Validation
Before creating PRs, ensure:
1. `mvn clean install` passes
2. No new SpotBugs errors in modified code
3. All tests pass
4. Code follows established patterns

## Common Issues and Solutions

### Build Failures
- **Dependency Resolution**: Run `mvn install -DskipTests` first
- **Memory Issues**: Increase heap size with `-Xmx1024m`
- **Module Not Found**: Ensure proper module dependency order

### SpotBugs Issues
- SpotBugs check may fail with known issues (existing codebase has accepted violations)
- Use `mvn spotbugs:spotbugs` to generate reports without failing
- New code should not introduce additional violations

### Test Failures
- Some test output includes expected ERROR logs (e.g., reflection failures)
- Tests requiring specific timing may be flaky in slow environments
- Individual module tests require full project build first

## Working Efficiently

### File Search Optimization
Exclude these directories from searches:
- `target/` - Build output
- `.git/` - Version control
- `bin/` - Eclipse output

### Focus Areas by Task Type
- **Collection utilities**: `ph-collection`, `ph-base`
- **I/O operations**: `ph-io`, `ph-base`
- **XML/JSON processing**: `ph-xml`, `ph-json`
- **Web functionality**: `ph-http`, `ph-wsclient`
- **Configuration**: `ph-config*`, `ph-settings`

### Module Selection Strategy
- Start with core modules (`ph-base`, `ph-collection`) for fundamental changes
- Use `ph-commons` for high-level utility additions
- Choose specialized modules (`ph-xml`, `ph-security`, etc.) for domain-specific changes

## Trust These Instructions

These instructions are based on comprehensive analysis and testing of the ph-commons codebase. Trust them and avoid unnecessary exploration unless you encounter incomplete or incorrect information. The build system and project structure are well-established and stable.