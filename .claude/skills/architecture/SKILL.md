---
description: DDD architecture reference and layer responsibilities
user-invocable: false
---

# Architecture Reference

## Overview

This project follows Domain-Driven Design (DDD) with a layered architecture:

```
┌─────────────────────────────────────────────────────┐
│                 Infrastructure Layer                 │
│    (Controllers, Resources, External Integrations)   │
├─────────────────────────────────────────────────────┤
│                  Application Layer                   │
│              (Services, Use Cases)                   │
├─────────────────────────────────────────────────────┤
│                    Domain Layer                      │
│           (Entities, Repositories, Value Objects)    │
└─────────────────────────────────────────────────────┘
```

## Layer Responsibilities

### Domain Layer (`domain/`)
**Purpose**: Core business logic and entities

**Contains**:
- `model/` - JPA entities (e.g., `Product`, `Customer`)
- `repository/` - Panache repository interfaces

**Rules**:
- No dependencies on other layers
- Pure business logic
- Entities are data classes with validation
- Repositories extend `PanacheRepository`

```kotlin
// domain/model/Product.kt
@Entity
data class Product(
    @Id @GeneratedValue var id: Long? = null,
    var name: String = ""
)

// domain/repository/ProductRepository.kt
@ApplicationScoped
class ProductRepository : PanacheRepository<Product>
```

### Application Layer (`application/`)
**Purpose**: Orchestrate use cases and business operations

**Contains**:
- `service/` - Application services with business logic

**Rules**:
- Depends only on Domain layer
- Handles transactions (`@Transactional`)
- Coordinates between repositories
- No HTTP/web concerns

```kotlin
// application/service/ProductService.kt
@ApplicationScoped
class ProductService(
    private val productRepository: ProductRepository
) {
    @Transactional
    fun create(product: Product): Product { ... }
}
```

### Infrastructure Layer (`infrastructure/`)
**Purpose**: External interfaces (web, database, messaging)

**Contains**:
- `web/` - Controllers (HTML) and Resources (REST API)

**Rules**:
- Depends on Application and Domain layers
- Handles HTTP requests/responses
- Template rendering (Qute)
- JSON serialization

```kotlin
// infrastructure/web/ProductController.kt - HTML/Qute
@Path("/products")
@Produces(MediaType.TEXT_HTML)
class ProductController(private val productService: ProductService)

// infrastructure/web/ProductResource.kt - REST API
@Path("/api/products")
@Produces(MediaType.APPLICATION_JSON)
class ProductResource(private val productService: ProductService)
```

## Dependency Rules

```
Infrastructure → Application → Domain
      ↓              ↓           ↓
   (Web)        (Services)   (Entities)
```

- **Domain**: No external dependencies
- **Application**: Imports from Domain only
- **Infrastructure**: Imports from Application and Domain

## File Organization

```
src/main/kotlin/com/example/product/
├── domain/
│   ├── model/
│   │   ├── Product.kt
│   │   ├── Customer.kt
│   │   └── ...
│   └── repository/
│       ├── ProductRepository.kt
│       ├── CustomerRepository.kt
│       └── ...
├── application/
│   └── service/
│       ├── ProductService.kt
│       ├── CustomerService.kt
│       └── ...
└── infrastructure/
    └── web/
        ├── ProductController.kt    # HTML endpoints
        ├── ProductResource.kt      # REST API
        └── ...

src/main/resources/
├── templates/                      # Qute templates
│   ├── products/
│   │   ├── productList.html
│   │   └── productForm.html
│   └── ...
└── application.properties          # Configuration
```

## URL Structure

| Type | Pattern | Example |
|------|---------|---------|
| Web UI | `/{entities}` | `/products`, `/customers` |
| Web Detail | `/{entities}/{id}` | `/products/1` |
| Web Form | `/{entities}/new` | `/products/new` |
| Web Edit | `/{entities}/{id}/edit` | `/products/1/edit` |
| REST API | `/api/{entities}` | `/api/products` |
| REST Single | `/api/{entities}/{id}` | `/api/products/1` |

## Technology Stack

- **Framework**: Quarkus 3.x
- **Language**: Kotlin
- **ORM**: Hibernate with Panache
- **Templates**: Qute
- **UI Framework**: PatternFly v5
- **Database**: PostgreSQL
- **Build**: Gradle with Kotlin DSL
