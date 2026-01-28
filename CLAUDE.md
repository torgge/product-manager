# Product Manager - Quarkus/Kotlin Project

## Agent Skill Verification

Before starting any task, verify the skill structure is intact:

```bash
ls -la .claude/skills/*/SKILL.md 2>/dev/null | wc -l
```

Expected: **13 skills** in `.claude/skills/`

### Required Skills Checklist
- [ ] `dev/SKILL.md` - Start dev server
- [ ] `build/SKILL.md` - Build project
- [ ] `test/SKILL.md` - Run tests
- [ ] `db/SKILL.md` - Database operations
- [ ] `gen-entity/SKILL.md` - Generate entity
- [ ] `gen-service/SKILL.md` - Generate service
- [ ] `gen-controller/SKILL.md` - Generate controller
- [ ] `gen-resource/SKILL.md` - Generate REST resource
- [ ] `gen-template/SKILL.md` - Generate template
- [ ] `gen-crud/SKILL.md` - Generate full CRUD
- [ ] `conventions/SKILL.md` - Coding conventions
- [ ] `architecture/SKILL.md` - Architecture reference (DDD)
- [ ] `vertical-slices/SKILL.md` - Vertical Slices pattern

If any skills are missing, recreate them following the patterns in existing skills.

## Project Context

This is a **Quarkus 3.x** application with:
- **Language**: Kotlin
- **Architecture**: DDD (Domain-Driven Design) + Vertical Slices
- **UI**: Qute templates + PatternFly v6
- **Database**: PostgreSQL
- **Build**: Gradle

## Available Skills

### Development Commands
- `/dev` - Start Quarkus dev mode with hot reload
- `/build` - Clean and build the project
- `/test` - Run tests
- `/db start|stop|restart|logs|reset` - Database operations

### Code Generators
- `/gen-entity <Name> [fields]` - Generate JPA entity
- `/gen-service <Name>` - Generate application service
- `/gen-controller <Name>` - Generate web controller
- `/gen-resource <Name>` - Generate REST resource
- `/gen-template <type> <Name>` - Generate Qute template
- `/gen-crud <Name> [fields]` - Generate complete CRUD stack

## Architecture Patterns

### DDD Layered (Default)
```
infrastructure/web/  → Controllers (HTML), Resources (REST)
application/service/ → Business logic
domain/model/        → JPA entities
domain/repository/   → Panache repositories
```

### Vertical Slices (Feature-based)
```
features/{entity}/
├── commands/        → CreateX, UpdateX, DeleteX (write operations)
├── queries/         → GetX, ListX (read operations)
├── {Entity}.kt      → Shared entity
└── {Entity}Repository.kt
```

Use Vertical Slices for complex features or CQRS. See `vertical-slices/SKILL.md` for details.

## Key Conventions
- Use `data class` for entities with `var` fields
- Constructor injection for dependencies
- `@Transactional` on write operations
- PatternFly v6 CSS classes (pf-v6-c-*)
- Qute syntax: `{variable}`, `{#if}`, `{#for}`
