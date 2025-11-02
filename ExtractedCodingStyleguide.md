# Extracted Coding Styleguide

This document contains the coding styleguide rules extracted from analyzing the ph-commons Java project codebase.

## 1. Project Structure

### 1.1 Build Configuration
1. All projects use Apache Maven 3.x for building (`pom.xml`)
2. All projects include Eclipse project files (`.project`, `.classpath` and `.settings`)
3. FindBugs configuration file `findbugs-exclude.xml` must be present in project root
4. All projects (except JDK extensions) use ph-commons as their base library

### 1.2 File Organization
1. Every Java file must include in order:
   - Copyright notice (format: "Copyright (C) YYYY-YYYY Philip Helger (www.helger.com)")
   - Apache License 2.0 header
   - Package declaration
   - Organized imports (grouped: Java standard library, third-party, com.helger.*)

## 2. Code Documentation

### 2.1 JavaDoc Standards
1. Class-level Documentation:
   - Clear purpose description
   - Thread-safety documentation where applicable
   - Implementation notes for performance considerations
   - Mathematical documentation for complex algorithms
2. Method Documentation:
   - Parameter documentation with constraints (@NonNull, etc.)
   - Generic type parameter documentation with @param tags
   - Return value semantics (@ReturnsMutableCopy, etc.)
   - @serial tags for serializable fields

### 2.2 Code Comments
1. Implementation Notes:
   - Performance optimization considerations
   - Generated code markers
   - No-logging policy markers in collections
   - Thread-safety considerations
   - Lock usage patterns

## 3. Code Style

### 3.1 Naming Conventions
1. Class and Interface Names:
   - Classes use PascalCase (e.g., `MatrixInt`, `QRDecomposition`)
   - Interfaces start with 'I' followed by PascalCase
   - Names must be descriptive of purpose

2. Field Names:
   - Private/protected members use Hungarian notation:
     * `m_` prefix for instance fields
     * `s_` prefix for static fields
     * Static final fields may use upper case
   - Type prefixes:
     * `a` for arrays/objects
     * `b` for boolean
     * `c` for char
     * `d` for double
     * `e` for enum
     * `f` for float
     * `n` for numeric (byte/int/long/short)
     * `s` for String

3. Method Names:
   - Private methods start with underscore
   - Collection methods use consistent patterns (get*, add*, remove*)
   - Builder methods use fluent interface style

### 3.2 Code Organization
1. Class Structure:
   - Static fields first
   - Instance fields second
   - Constructors third
   - Public methods fourth
   - Protected/package methods fifth
   - Private methods last

2. Helper Classes:
   - Must be final with private constructor
   - Include `@PresentForCodeCoverage` instance
   - Provide comprehensive utility methods

## 4. Programming Patterns

### 4.1 Thread Safety
1. Class-level Thread Safety:
   - `@ThreadSafe` for thread-safe implementations
   - `@NotThreadSafe` for non-thread-safe classes
   - `@Immutable` for immutable classes
   - `@GuardedBy` for synchronized fields

2. Synchronization:
   - Use `SimpleReadWriteLock` for thread-safe implementations
   - Consistent try-finally blocks for lock releasing
   - Clear documentation of threading assumptions

### 4.2 Collections Framework
1. Interface Hierarchy:
   - Base: `ICommonsCollection`, `ICommonsList`, `ICommonsSet`, `ICommonsMap`
   - Specialized: Ordered/Sorted/Navigable variants
   - Clear distinction between mutable and immutable interfaces

2. Implementation Classes:
   - Standard: `CommonsArrayList`, `CommonsHashMap`, etc.
   - Thread-safe: `CommonsCopyOnWriteArrayList`, etc.
   - Special purpose: `CommonsWeakHashMap`, `CommonsEnumMap`

3. Collection Methods:
   - Return type annotations for mutability
   - Consistent validation using `ValueEnforcer`
   - Performance-optimized implementations

### 4.3 Error Handling
1. Exception Patterns:
   - Custom exception interfaces
   - Generic exception types in interfaces
   - Comprehensive exception documentation
   - Value validation using `ValueEnforcer`

### 4.4 Concurrent Programming Patterns

1. Lock Implementations:
   - Use `@ThreadSafe` annotation for thread-safe implementations
   - Provide AutoCloseable wrappers for locks (see `AutoLock`)
   - Always validate lock parameters with `ValueEnforcer`
   - Document thread-safety characteristics clearly

2. Thread Factory Patterns:
   - Use builder pattern for complex thread configurations
   - Support both wrapped and default thread factories
   - Allow customization of thread properties
   - Provide proper exception handling for threads

3. Helper Class Patterns:
   - Mark utility classes as `@Immutable`
   - Use private constructors for helper classes
   - Provide consistent time unit handling methods
   - Include comprehensive logging

4. Interface Patterns:
   - Use `@FunctionalInterface` where appropriate
   - Clear documentation of threading implications
   - Proper parameter validation annotations
   - Document any potential blocking operations

5. Lock Usage Guidelines:
   - Prefer `SimpleReadWriteLock` for read/write operations
   - Use `AutoLock` with try-with-resources
   - Document lock ordering to prevent deadlocks
   - Clear indication of lock scope and purpose

6. Executor Service Patterns:
   - Factory pattern for executor service creation
   - Proper shutdown handling
   - Clear documentation of threading model
   - Support for task interruption and cancellation

### 4.5 Additional Programming Patterns

1. Exception Propagation:
   - Use `@NonNull` and `@Nullable` annotations consistently
   - Exception wrapping in helper classes
   - `throws Exception` in AutoCloseable implementations
   - Clear documentation of exception hierarchies

2. Resource Management:
   - Implement AutoCloseable for cleanup
   - Support try-with-resources pattern
   - Document resource cleanup requirements
   - Proper shutdown sequences

3. Builder Implementation:
   - Implement `IResettableBuilder`
   - Field validation in builder methods
   - Fluent interface with method chaining
   - Clear optional vs required parameters

4. Time Handling:
   - Support multiple time unit formats
   - Validate time parameters
   - Provide time conversion utilities
   - Document timing implications

5. Value Validation:
   - Use `ValueEnforcer` consistently
   - Null checks before operations
   - Range validation for numerics
   - Type safety in generics

6. Functional Programming:
   - Support Java 8 functional interfaces
   - Exception-aware functional interfaces
   - Document lambda usage patterns
   - Clear functional method contracts

7. State Management:
   - Use `ETriState` for three-state logic
   - Atomic state transitions
   - Thread-safe state handling
   - Constructor state validation

8. Logging:
   - Private static final loggers
   - Consistent log levels
   - Performance-aware logging
   - Proper exception logging

## 5. Best Practices

1. Performance:
   - Avoid unnecessary object creation
   - Use primitive arrays where possible
   - Document performance implications
   - Prefer regular iteration over Stream API

2. Code Generation:
   - Clear markers for generated code
   - Consistent patterns in generated methods
   - Maintain coding style in templates

3. General Rules:
   - No `serialVersionUID` (handled by Java runtime)
   - All logging via SLF4J
   - No constant-only interfaces
   - Interface methods never specify visibility

## Code Search Performance

### Large Codebase Organization
1. Source code should be organized in descriptive package hierarchies
   - Main package follows `com.helger.*` pattern
   - Subpackages group related functionality (e.g., `collection`, `io`, `xml`)
   - Clear separation between API and implementation packages

2. Resource Organization:
   - Keep generated code in separate directories
   - Group test resources logically
   - Maintain clear package structure in test code

3. File Naming Conventions for Searchability:
   - Classes implementing interfaces add descriptive prefix (e.g., `Commons` for collection implementations)
   - Test classes end with `Test` suffix
   - Abstract classes start with `Abstract`
   - Helper classes end with `Helper`

### Search Path Optimization
1. Source code organization:
   ```
   src/main/java/     - Main source code
   src/main/resources/ - Resources and configuration
   src/test/java/     - Test source code
   src/test/resources/ - Test resources
   ```

2. Exclude directories from code search:
   - `target/` directories (Maven build output)
   - `bin/` directories (Eclipse build output)
   - `.git/` directories
   - Generated code directories

3. Search efficiency recommendations:
   - Use package-specific searches when possible
   - Leverage consistent naming patterns
   - Focus searches on relevant module directories
   - Utilize IDE indexing features
