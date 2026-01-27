# Product Manager - Quarkus/Kotlin Project

## Agent Skill Verification

Before starting any task, verify the skill structure is intact:

```bash
ls -la .claude/skills/*/SKILL.md 2>/dev/null | wc -l
```

Expected: **12 skills** in `.claude/skills/`

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
- [ ] `architecture/SKILL.md` - Architecture reference

If any skills are missing, recreate them following the patterns in existing skills.

## Project Context

This is a **Quarkus 3.x** application with:
- **Language**: Kotlin
- **Architecture**: DDD (Domain-Driven Design)
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

## Architecture Layers

```
infrastructure/web/  → Controllers (HTML), Resources (REST)
application/service/ → Business logic
domain/model/        → JPA entities
domain/repository/   → Panache repositories
```

## Key Conventions
- Use `data class` for entities with `var` fields
- Constructor injection for dependencies
- `@Transactional` on write operations
- PatternFly v6 CSS classes (pf-v6-c-*)
- Qute syntax: `{variable}`, `{#if}`, `{#for}`
