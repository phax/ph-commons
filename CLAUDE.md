# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**ph-commons** is a multi-module Java 17+ utility library (`com.helger.*`) with 32 Maven modules. Core modules: `ph-base` → `ph-collection` → `ph-commons` (in dependency order). Testing uses JUnit 4.

## Build Commands

```bash
# Install all modules (required before testing individual modules)
mvn install -DskipTests

# Full build with tests (~2 minutes)
mvn install

# Test a specific module
mvn test -pl ph-base

# Run a single test class
mvn test -pl ph-base -Dtest=ValueEnforcerTest

# Validation
mvn spotbugs:check
mvn forbiddenapis:check
```

**Critical**: Always run `mvn install -DskipTests` before testing individual modules due to inter-module dependencies.

## CI

GitHub Actions matrix: Java 17, 21, 25. Command: `mvn --batch-mode --update-snapshots install`.

## Coding Conventions

### Naming (Hungarian Notation)

- **Interfaces**: `I` prefix (`ICommonsList`, `IBuilder<T>`)
- **Instance fields**: `m_` prefix with type hint (`m_aItems`, `m_nSize`, `m_bEnabled`, `m_sName`)
- **Static fields**: `s_` prefix (`s_aLogger`); static final may use UPPER_CASE
- **Type prefixes**: `a` (object/array), `b` (boolean), `c` (char), `d` (double), `e` (enum), `f` (float), `n` (numeric), `s` (String)
- **Private methods**: underscore prefix (`_doSomething()`)
- **Abstract classes**: `Abstract` prefix; **Helper classes**: `Helper` suffix; **Test classes**: `Test` suffix

### Formatting

- 2-space indentation, no tabs
- K&R brace style (opening brace on same line)

### Class Structure Order

1. Static fields → 2. Instance fields → 3. Constructors → 4. Private methods → 5. Protected/package methods → 6. Public instance methods → 7. Public static methods

### Helper/Utility Classes

Must be `final` with private constructor and a `@PresentForCodeCoverage` dummy instance:
```java
@Immutable
public final class FooHelper {
  @PresentForCodeCoverage
  private static final FooHelper INSTANCE = new FooHelper();
  private FooHelper() {}
}
```

### Annotations

- **Nullness**: `@NonNull` / `@Nullable` (from `org.jspecify`). Never annotate primitives with `@Nullable` — use wrapper types instead. Arrays of primitives can be `@Nullable`.
- **Return types**: `@ReturnsMutableCopy` / `@ReturnsImmutableObject`
- **Thread safety**: `@ThreadSafe` / `@NotThreadSafe` / `@Immutable` on every class
- **Constraints**: `@Nonnegative`, `@Nonempty`

### Key Patterns

- **Parameter validation**: Always use `ValueEnforcer` (`ValueEnforcer.notNull()`, `.notEmpty()`, `.isGT0()`)
- **Thread-safe classes**: Use `SimpleReadWriteLock` with `AutoLock` in try-with-resources
- **Builders**: Implement `IBuilder<T>` or `IResettableBuilder<T>` with fluent API
- **Collections**: Use `CommonsArrayList`, `CommonsHashMap`, etc. (not raw JDK collections)
- **Logging**: `private static final Logger LOGGER = LoggerFactory.getLogger(ClassName.class);`
- **Three-state logic**: `ETriState` instead of `Boolean`
- **No `serialVersionUID`** — handled by Java runtime

### File Headers

Every Java file must have the Apache License 2.0 header with copyright: `Copyright (C) YYYY-YYYY Philip Helger (www.helger.com)`

### Performance

Prefer regular iteration over Stream API. Avoid unnecessary object creation. Use primitive arrays where possible.
