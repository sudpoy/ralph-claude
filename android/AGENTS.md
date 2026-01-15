# Android Development Patterns

## Overview

This directory contains Android projects managed by Ralph.

## Build & Validation

```bash
# Build debug APK
./gradlew assembleDebug

# Run unit tests
./gradlew test

# Run lint checks
./gradlew lint

# Run all checks
./gradlew check
```

## Patterns

- Use Kotlin for all new code
- Follow MVVM architecture pattern
- Use Jetpack Compose for UI (unless project specifies otherwise)
- Use Hilt for dependency injection
- Use Coroutines and Flow for async operations

## Testing

- Unit tests in `src/test/`
- Instrumented tests in `src/androidTest/`
- UI tests use Compose testing APIs

## Common Gotchas

- Always update `AndroidManifest.xml` when adding new activities
- Remember to add internet permission for network calls
- ProGuard rules may need updates when adding new libraries
