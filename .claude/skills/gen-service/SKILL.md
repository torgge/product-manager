---
description: Generate an application service with CRUD operations
disable-model-invocation: true
---

# /gen-service - Generate Application Service

Generate a service class with standard CRUD operations following project patterns.

## Usage
```
/gen-service <EntityName>
```

## Examples
```
/gen-service Category
/gen-service Order
```

## Project Pattern

Location: `src/main/kotlin/com/example/product/application/service/`

### Service Template
```kotlin
package com.example.product.application.service

import com.example.product.domain.model.{EntityName}
import com.example.product.domain.repository.{EntityName}Repository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import java.time.LocalDateTime

@ApplicationScoped
class {EntityName}Service(
    private val {entityName}Repository: {EntityName}Repository
) {

    fun findAll(): List<{EntityName}> {
        return {entityName}Repository.listAll()
    }

    fun findById(id: Long): {EntityName}? {
        return {entityName}Repository.findById(id)
    }

    @Transactional
    fun create({entityName}: {EntityName}): {EntityName} {
        {entityName}.createdAt = LocalDateTime.now()
        {entityName}.updatedAt = LocalDateTime.now()
        {entityName}Repository.persist({entityName})
        return {entityName}
    }

    @Transactional
    fun update(id: Long, {entityName}: {EntityName}): {EntityName}? {
        val existing = {entityName}Repository.findById(id) ?: return null

        // Update fields (customize based on entity)
        existing.updatedAt = LocalDateTime.now()

        return existing
    }

    @Transactional
    fun delete(id: Long): Boolean {
        return {entityName}Repository.deleteById(id)
    }

    fun count(): Long {
        return {entityName}Repository.count()
    }
}
```

## Conventions
- Use constructor injection for dependencies
- `@ApplicationScoped` for CDI bean
- `@Transactional` on write operations (create, update, delete)
- Return `null` for not found on update
- Return `Boolean` for delete success
- Use `listAll()` from PanacheRepository

## Prerequisites
Entity and Repository must exist:
- `domain/model/{EntityName}.kt`
- `domain/repository/{EntityName}Repository.kt`

## Reference Files
- `ProductService.kt` - Full CRUD with custom queries
- `CustomerService.kt` - Simple CRUD pattern
- `StockService.kt` - Complex business logic example
