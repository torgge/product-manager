# Product Manager - Quarkus/Kotlin Project

## Agent Skill Verification

Before starting any task, verify the skill structure is intact:

```bash
ls -la .claude/skills/*/SKILL.md 2>/dev/null | wc -l
```

Expected: **15 skills** in `.claude/skills/`

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
- [ ] `ui/SKILL.md` - UI components, PatternFly v6 fonts & classes
- [ ] `tdd/SKILL.md` - TDD guard pattern (run tests after every modification)

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

### UI Reference
- `/ui` - PatternFly v6 fonts, CSS classes, and component patterns

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
- PostgreSQL container name: `product-manager-db` (use for docker exec commands)
- Port conflicts: Check with `lsof -ti:8080` before starting server
- `@OneToOne` creates unique constraint on FK; use `@ManyToOne` for multiple records per parent
- Hibernate doesn't auto-drop constraints when changing relationships; drop manually with `ALTER TABLE`

## TDD Rule - MANDATORY

**After every code modification, run the test suite before reporting the task as done.**

```bash
./gradlew test
```

- A task is NOT complete until tests pass
- Never skip, disable, or delete a failing test to make the suite green — fix the code
- Report test results (pass/fail count) to the user after each run
- See `.claude/skills/tdd/SKILL.md` for full pattern and writing test examples

## UI Patterns

### Modal Selector Pattern
- `EntitySelector` utility in `base.html` for searchable, paginated selection
- Usage: `EntitySelector.open({ title, endpoint, columns, onSelect })`
- Debounced search (300ms), pagination (10 items/page), click row to select
- REST APIs return `PaginatedResponse<T>` with `items: List<T>` and `total: Long`
- Query params: `?search=query&limit=10&offset=0`
