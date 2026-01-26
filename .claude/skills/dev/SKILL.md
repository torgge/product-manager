---
description: Start Quarkus development server with hot reload
disable-model-invocation: true
allowed-tools:
  - Bash(./gradlew quarkusDev*)
---

# /dev - Start Development Server

Start the Quarkus development server with hot reload enabled.

## Command
```bash
./gradlew quarkusDev
```

## What it does
- Starts Quarkus in development mode
- Enables hot reload for code changes
- Exposes the application at http://localhost:8080
- Dev UI available at http://localhost:8080/q/dev-ui

## Prerequisites
- PostgreSQL database must be running (use `/db start` first)
- Java 17+ installed

## Notes
- Press `s` to force restart
- Press `h` for help
- Press `q` to quit
