---
description: Project coding conventions and style guide
user-invocable: false
---

# Coding Conventions

## Kotlin Style

### General
- Use 4-space indentation
- Max line length: 120 characters
- Use trailing commas in multi-line lists
- Prefer expression bodies for single-expression functions

### Naming
- Classes: `PascalCase` (e.g., `ProductService`)
- Functions: `camelCase` (e.g., `findById`)
- Variables: `camelCase` (e.g., `productRepository`)
- Constants: `SCREAMING_SNAKE_CASE` (e.g., `MAX_QUANTITY`)
- Packages: `lowercase` (e.g., `com.example.product`)

### Kotlin-Specific
```kotlin
// Prefer data classes for entities
data class Product(...)

// Use constructor injection
class ProductService(
    private val productRepository: ProductRepository
)

// Prefer when over if-else chains
when (status) {
    "ACTIVE" -> handleActive()
    "INACTIVE" -> handleInactive()
    else -> handleUnknown()
}

// Use safe calls and Elvis operator
val name = product?.name ?: "Unknown"

// Prefer let for null checks
product?.let { doSomething(it) }
```

## Package Structure

```
com.example.product/
├── domain/           # Domain layer (entities, repositories)
│   ├── model/        # JPA entities
│   └── repository/   # Panache repositories
├── application/      # Application layer
│   └── service/      # Business logic services
└── infrastructure/   # Infrastructure layer
    └── web/          # Controllers and resources
```

## Validation

### Entity Validation
```kotlin
@Entity
data class Product(
    @field:NotBlank(message = "Name is required")
    var name: String = "",

    @field:Positive(message = "Price must be positive")
    var price: BigDecimal = BigDecimal.ZERO,

    @field:Email(message = "Invalid email format")
    var email: String? = null
)
```

### Common Annotations
- `@field:NotBlank` - Required non-empty strings
- `@field:NotNull` - Required non-null values
- `@field:Positive` - Positive numbers
- `@field:PositiveOrZero` - Non-negative numbers
- `@field:Size(min, max)` - String/collection size
- `@field:Email` - Valid email format
- `@field:Pattern(regexp)` - Custom regex

## Error Handling

### Service Layer
```kotlin
@Transactional
fun update(id: Long, data: Entity): Entity? {
    val existing = repository.findById(id) ?: return null
    // Update and return
    return existing
}
```

### Controller Layer
```kotlin
@POST
@Path("/{id}")
fun update(@PathParam("id") id: Long, ...): Response {
    val updated = service.update(id, entity)
    return if (updated != null) {
        Response.seeOther(URI.create("/entities")).build()
    } else {
        Response.status(Response.Status.NOT_FOUND).build()
    }
}
```

## Transaction Management
- Use `@Transactional` on service methods that modify data
- Don't use `@Transactional` on read-only operations
- Panache repositories handle their own transactions

## Date/Time
- Use `java.time.LocalDateTime` for timestamps
- Always set `createdAt` and `updatedAt` in service layer
- Use `LocalDateTime.now()` for current time

## BigDecimal for Money
- Always use `BigDecimal` for monetary values
- Default to `BigDecimal.ZERO`
- Use `step="0.01"` in form inputs
