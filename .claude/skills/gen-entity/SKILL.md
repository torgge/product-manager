---
description: Generate a new JPA entity following project patterns
disable-model-invocation: true
---

# /gen-entity - Generate JPA Entity

Generate a new JPA entity class following project conventions.

## Usage
```
/gen-entity <EntityName> [field:type, field:type, ...]
```

## Examples
```
/gen-entity Category name:String, description:String?
/gen-entity Order total:BigDecimal, status:String, createdAt:LocalDateTime
```

## Project Pattern

Location: `src/main/kotlin/com/example/product/domain/model/`

### Entity Template
```kotlin
package com.example.product.domain.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime

@Entity
data class {EntityName}(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @field:NotBlank(message = "{field} is required")
    var {field}: String = "",

    var createdAt: LocalDateTime = LocalDateTime.now(),
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
```

## Conventions
- Use `data class` for entities
- Always include `id: Long?` with `@GeneratedValue`
- Use `var` for all fields (Hibernate requirement)
- Include `createdAt` and `updatedAt` timestamps
- Use `@field:` prefix for validation annotations
- Nullable fields use `String?` and don't have `@NotBlank`
- Default values required for all fields

## Common Field Types
- `String` - Text fields
- `String?` - Optional text
- `BigDecimal` - Money/prices
- `Int` - Quantities
- `LocalDateTime` - Timestamps
- `Boolean` - Flags

## Validation Annotations
- `@field:NotBlank` - Required strings
- `@field:Positive` - Positive numbers
- `@field:Email` - Email format
- `@field:Size(min, max)` - Length constraints

## Also Generate
After creating an entity, also create:
1. Repository: `/gen-service` depends on repository
2. See existing: `ProductRepository.kt`, `CustomerRepository.kt`

### Repository Template
Location: `src/main/kotlin/com/example/product/domain/repository/`
```kotlin
package com.example.product.domain.repository

import com.example.product.domain.model.{EntityName}
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class {EntityName}Repository : PanacheRepository<{EntityName}> {
    // Add custom query methods as needed
}
```
