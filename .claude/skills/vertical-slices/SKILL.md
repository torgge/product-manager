---
description: Vertical Slices architecture pattern - Feature-based code organization
user-invocable: false
---

# Vertical Slices Architecture Pattern

## Overview

Vertical Slices organiza o código por **feature/funcionalidade** em vez de camadas técnicas. Cada slice contém tudo necessário para uma funcionalidade específica, desde a UI até o banco de dados.

```
┌─────────────────────────────────────────────────────────────────┐
│                        Application                               │
├───────────────┬───────────────┬───────────────┬────────────────┤
│  Create       │  List         │  Update       │  Delete        │
│  Product      │  Products     │  Product      │  Product       │
│  ┌─────────┐  │  ┌─────────┐  │  ┌─────────┐  │  ┌─────────┐   │
│  │ Handler │  │  │ Handler │  │  │ Handler │  │  │ Handler │   │
│  │ Request │  │  │ Request │  │  │ Request │  │  │ Request │   │
│  │ Response│  │  │ Response│  │  │ Response│  │  │ Response│   │
│  │ Validator│ │  │ Query   │  │  │ Validator│ │  │         │   │
│  └─────────┘  │  └─────────┘  │  └─────────┘  │  └─────────┘   │
└───────────────┴───────────────┴───────────────┴────────────────┘
```

## Comparação: DDD Layered vs Vertical Slices

### DDD Layered (Atual)
```
src/main/kotlin/com/example/
├── domain/
│   ├── model/Product.kt
│   └── repository/ProductRepository.kt
├── application/
│   └── service/ProductService.kt
└── infrastructure/
    └── web/
        ├── ProductController.kt
        └── ProductResource.kt
```

### Vertical Slices
```
src/main/kotlin/com/example/
├── features/
│   └── products/
│       ├── CreateProduct.kt      # Request, Handler, Response
│       ├── ListProducts.kt       # Query, Handler, Response
│       ├── GetProduct.kt         # Query, Handler, Response
│       ├── UpdateProduct.kt      # Request, Handler, Response
│       ├── DeleteProduct.kt      # Command, Handler
│       ├── Product.kt            # Entity (shared)
│       └── ProductRepository.kt  # Repository (shared)
└── shared/
    └── infrastructure/           # Cross-cutting concerns
```

## Implementação em Quarkus/Kotlin

### Estrutura de um Slice

Cada slice contém:
- **Request/Command**: Dados de entrada
- **Response**: Dados de saída
- **Handler**: Lógica de negócio
- **Validator** (opcional): Validação específica

### Exemplo: CreateProduct Slice

```kotlin
// features/products/CreateProduct.kt

package com.example.features.products

import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

// Request DTO
data class CreateProductRequest(
    val name: String,
    val description: String?,
    val price: BigDecimal,
    val sku: String
)

// Response DTO
data class CreateProductResponse(
    val id: Long,
    val name: String,
    val sku: String
)

// Handler (Use Case)
@ApplicationScoped
class CreateProductHandler(
    private val productRepository: ProductRepository
) {
    @Transactional
    fun handle(request: CreateProductRequest): CreateProductResponse {
        // Validation
        require(request.name.isNotBlank()) { "Name is required" }
        require(request.price > BigDecimal.ZERO) { "Price must be positive" }

        // Business logic
        val product = Product(
            name = request.name,
            description = request.description,
            price = request.price,
            sku = request.sku
        )

        productRepository.persist(product)

        return CreateProductResponse(
            id = product.id!!,
            name = product.name,
            sku = product.sku
        )
    }
}

// REST Endpoint (thin layer)
@Path("/api/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class CreateProductEndpoint(
    private val handler: CreateProductHandler
) {
    @POST
    fun create(request: CreateProductRequest): Response {
        val response = handler.handle(request)
        return Response.status(Response.Status.CREATED)
            .entity(response)
            .build()
    }
}
```

### Exemplo: ListProducts Slice (Query)

```kotlin
// features/products/ListProducts.kt

package com.example.features.products

import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType

// Query parameters
data class ListProductsQuery(
    val page: Int = 0,
    val size: Int = 20,
    val search: String? = null
)

// Response DTO
data class ListProductsResponse(
    val items: List<ProductSummary>,
    val total: Long,
    val page: Int,
    val size: Int
)

data class ProductSummary(
    val id: Long,
    val name: String,
    val price: BigDecimal,
    val sku: String
)

// Handler
@ApplicationScoped
class ListProductsHandler(
    private val productRepository: ProductRepository
) {
    fun handle(query: ListProductsQuery): ListProductsResponse {
        val products = if (query.search != null) {
            productRepository.find("name like ?1", "%${query.search}%")
        } else {
            productRepository.findAll()
        }

        val total = products.count()
        val items = products
            .page(query.page, query.size)
            .list()
            .map { ProductSummary(it.id!!, it.name, it.price, it.sku) }

        return ListProductsResponse(
            items = items,
            total = total,
            page = query.page,
            size = query.size
        )
    }
}

// REST Endpoint
@Path("/api/products")
@Produces(MediaType.APPLICATION_JSON)
class ListProductsEndpoint(
    private val handler: ListProductsHandler
) {
    @GET
    fun list(
        @QueryParam("page") @DefaultValue("0") page: Int,
        @QueryParam("size") @DefaultValue("20") size: Int,
        @QueryParam("search") search: String?
    ): ListProductsResponse {
        return handler.handle(ListProductsQuery(page, size, search))
    }
}
```

## Quando Usar Vertical Slices

### Vantagens

| Benefício | Descrição |
|-----------|-----------|
| **Coesão** | Todo código relacionado a uma feature está junto |
| **Independência** | Slices podem evoluir independentemente |
| **Testabilidade** | Fácil testar uma feature completa |
| **Onboarding** | Novos devs entendem features isoladamente |
| **Escalabilidade** | Features podem virar microserviços |

### Quando Aplicar

- Projetos com muitas features independentes
- Equipes trabalhando em features diferentes
- Preparação para microserviços
- CQRS (Command Query Responsibility Segregation)

### Quando Evitar

- Projetos pequenos com poucas entidades
- Muita lógica compartilhada entre features
- Equipe pequena trabalhando em tudo

## Abordagem Híbrida (Recomendada)

Combine DDD Layered com Vertical Slices:

```
src/main/kotlin/com/example/
├── domain/                    # Shared domain (DDD)
│   ├── model/
│   └── repository/
├── features/                  # Vertical Slices
│   ├── products/
│   │   ├── commands/         # Write operations
│   │   │   ├── CreateProduct.kt
│   │   │   └── UpdateProduct.kt
│   │   └── queries/          # Read operations
│   │       ├── ListProducts.kt
│   │       └── GetProductDetails.kt
│   └── orders/
│       ├── commands/
│       └── queries/
└── shared/                    # Cross-cutting
    ├── security/
    └── validation/
```

## Convenções para Vertical Slices

### Nomenclatura

| Tipo | Padrão | Exemplo |
|------|--------|---------|
| Command | `{Verb}{Entity}` | `CreateProduct`, `UpdateOrder` |
| Query | `{Get/List}{Entity}` | `GetProduct`, `ListOrders` |
| Handler | `{Action}Handler` | `CreateProductHandler` |
| Request | `{Action}Request` | `CreateProductRequest` |
| Response | `{Action}Response` | `CreateProductResponse` |

### Estrutura de Arquivo

```kotlin
// {Action}{Entity}.kt

// 1. Request/Command/Query DTOs
data class CreateProductRequest(...)

// 2. Response DTO
data class CreateProductResponse(...)

// 3. Handler (business logic)
@ApplicationScoped
class CreateProductHandler(...) {
    fun handle(request: ...): Response { ... }
}

// 4. Endpoint (optional, can be in separate file)
@Path(...)
class CreateProductEndpoint(...) { ... }
```

### Validação

```kotlin
// Inline validation in Handler
class CreateProductHandler {
    fun handle(request: CreateProductRequest): CreateProductResponse {
        // Validation rules
        require(request.name.length >= 3) { "Name must have at least 3 characters" }
        require(request.price > BigDecimal.ZERO) { "Price must be positive" }

        // Or use Bean Validation
        // validator.validate(request)

        // Business logic...
    }
}
```

## Migração Gradual

1. **Mantenha a estrutura atual** para features existentes
2. **Crie novas features** usando Vertical Slices
3. **Migre gradualmente** features críticas ou complexas
4. **Compartilhe** entidades e repositórios entre abordagens

```kotlin
// Existing: application/service/ProductService.kt (keep)
// New feature: features/products/ImportProducts.kt (vertical slice)
// Both can use: domain/repository/ProductRepository.kt
```
