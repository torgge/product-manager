---
description: Run project tests
disable-model-invocation: true
allowed-tools:
  - Bash(./gradlew test*)
  - Bash(./gradlew check*)
---

# /test - Run Tests

Run project tests with Gradle.

## Command
```bash
./gradlew test
```

## Usage

### Run all tests
```bash
./gradlew test
```

### Run specific test class
```bash
./gradlew test --tests "com.example.product.ProductServiceTest"
```

### Run tests matching pattern
```bash
./gradlew test --tests "*Service*"
```

### Run with verbose output
```bash
./gradlew test --info
```

## Test Reports
- HTML report: `build/reports/tests/test/index.html`
- XML results: `build/test-results/test/`

## Notes
- Tests require PostgreSQL (uses Testcontainers or dev services)
- Use `./gradlew check` to also run static analysis
