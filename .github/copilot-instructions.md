# Copilot Instructions for ph-commons

## Repository Overview

ph-commons is a comprehensive set of shared Java 11+ utility libraries created by Philip Helger. This multi-module Maven project provides foundational functionality used across many other Java projects, including collections, XML processing, configuration management, cryptography utilities, and more.

**Key Repository Facts:**
- **Size**: ~2,029 Java files across 19 modules
- **Language**: Java 11+ (supports Java 11, 17, 21, 24)
- **Build System**: Maven 3.9.11+
- **Testing**: JUnit 4 framework
- **CI/CD**: GitHub Actions with multi-version Java testing
- **License**: Apache 2.0

## Build and Validation Instructions

### Prerequisites
- Java 11 or higher (tested on 11, 17, 21, 24)
- Maven 3.9.11 or higher
- No additional tools required for basic development

### Essential Build Commands

**Always use these commands in the specified order:**

1. **Clean and compile** (fastest validation):
   ```bash
   mvn clean compile
   ```

2. **Full build with tests** (recommended for all changes):
   ```bash
   mvn clean install
   ```
   - Duration: ~5-10 minutes
   - Runs all tests across all 19 modules
   - Creates JAR files for all modules

3. **Test specific module** (when working on single module):
   ```bash
   mvn test -pl <module-name>
   # Example: mvn test -pl ph-commons
   ```

4. **Validate only** (quick check):
   ```bash
   mvn validate
   ```

### Important Build Notes

- **Always run `mvn clean install` before submitting changes** - this ensures all modules compile and pass tests
- Expected test output includes deliberate warnings and errors from test cases (e.g., MockThrowingCSVWriter exceptions)
- Build artifacts are placed in `target/` directories (ignored by git)
- The build uses a parent POM inheritance structure with `com.helger:parent-pom`

### Common Build Issues

- **Module dependency order**: If build fails, ensure you're building from root directory, not individual modules
- **Java version**: Verify Java 11+ is active (`java -version`)
- **Memory**: For large builds, increase heap with `export MAVEN_OPTS="-Xmx2g"`

## Project Architecture and Layout

### Module Structure

The repository follows a multi-module Maven structure with 19 core modules:

```
ph-commons/               # Core utilities (collections, IO, strings, etc.)
ph-collection/            # Enhanced collection implementations  
ph-scopes/               # Scoping and lifecycle management
ph-cli/                  # Command-line interface utilities
ph-bc/                   # BouncyCastle cryptography wrappers
ph-security/             # Security utilities and key management
ph-xml/                  # XML processing and serialization
ph-wsclient/             # Web service client utilities
ph-jaxb/                 # JAXB bindings and utilities
ph-jaxb-adapter/         # JAXB type adapters
ph-json/                 # JSON processing utilities
ph-tree/                 # Tree data structures
ph-matrix/               # Matrix operations
ph-graph/                # Graph algorithms and structures
ph-datetime/             # Date/time utilities
ph-settings/             # Settings and configuration
ph-config/               # Advanced configuration management
ph-less-commons/         # Smaller utility collections
ph-dao/                  # Data access object patterns
```

### Key Configuration Files

- **Root POM**: `/pom.xml` - Parent POM defining all modules and shared dependencies
- **Module POMs**: Each `ph-*/pom.xml` inherits from parent and defines module-specific deps
- **GitHub Workflow**: `.github/workflows/maven.yml` - CI/CD with Java 11,17,21,24 testing
- **Findbugs**: `ph-commons/findbugs-exclude.xml` - Static analysis exclusions
- **Gitignore**: `.gitignore` - Excludes `target/`, IDE files, temp files

### Service Provider Interface (SPI) Pattern

This project extensively uses Java SPI patterns. Key SPI interfaces include:
- `IThirdPartyModuleProviderSPI` - Third-party module registration
- `ITypeConverterRegistrarSPI` - Type conversion registration  
- `IEqualsImplementationRegistrarSPI` - Custom equals implementations
- `IHashCodeImplementationRegistrarSPI` - Custom hashcode implementations

**Important**: When adding SPI implementations, ensure proper META-INF/services configuration files are created.

### Common Code Patterns

1. **Null Safety**: Heavy use of `@Nonnull`, `@Nullable` annotations
2. **Thread Safety**: Classes marked `@ThreadSafe`, `@NotThreadSafe`, `@Immutable`
3. **Return Types**: `@ReturnsMutableCopy`, `@ReturnsImmutableObject` for collection clarity
4. **Builder Pattern**: Many classes use builder pattern (e.g., `ConfigFileBuilder`)
5. **Error Handling**: Extensive use of `ValueEnforcer` for parameter validation

### Testing Approach

- **Test Location**: Tests in `src/test/java` mirror `src/main/java` structure
- **SPI Tests**: Each module has `SPITest.java` validating SPI configurations
- **Naming**: Test classes end with `Test` or `FuncTest` (functional tests)
- **Test Categories**: Mock objects in package `*.mock.*`, functional tests in `*.supplementary.test.*`

## Continuous Integration

The project uses GitHub Actions (`.github/workflows/maven.yml`) with:
- **Trigger**: Pushes to master branch
- **Java Versions**: 11, 17, 21, 24 (matrix build)
- **Build Command**: `mvn --batch-mode --update-snapshots install`
- **Deployment**: Java 11 build publishes to Maven Central (snapshots)

## Key Architectural Principles

1. **Minimal Dependencies**: Core modules avoid external dependencies where possible
2. **Backward Compatibility**: Maintains compatibility within major versions
3. **Performance Focus**: Optimized for high-performance applications
4. **Utility-First**: Provides utilities that work well in larger applications
5. **Standards Compliance**: Follows Java best practices and coding standards

## Working with This Repository

### Making Changes

1. **Start with tests**: Create/update tests first when possible
2. **Build frequently**: Run `mvn clean compile` after each significant change
3. **Test thoroughly**: Always run `mvn clean install` before committing
4. **Check SPI**: If adding service providers, validate SPI configuration files
5. **Follow patterns**: Study existing code patterns before implementing new features

### Common Gotchas

- **Module Dependencies**: Changes in core modules (ph-commons) may affect all others
- **SPI Configuration**: Missing META-INF/services files cause runtime failures
- **Thread Safety**: Pay attention to thread safety annotations when modifying classes
- **Null Safety**: Always respect @Nonnull/@Nullable contracts
- **Build Order**: Always build from root directory to ensure proper module dependency resolution

### When to Run Full Build

- Before committing any changes
- When modifying shared interfaces or parent classes  
- When adding/removing dependencies
- When changing SPI configurations
- Before creating pull requests

Trust these instructions for efficient development workflow. Only explore further if you encounter issues not covered here or need to understand specific implementation details.