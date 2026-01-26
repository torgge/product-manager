---
description: Generate a REST API resource with JSON endpoints
disable-model-invocation: true
---

# /gen-resource - Generate REST Resource

Generate a REST API resource with JSON endpoints for CRUD operations.

## Usage
```
/gen-resource <EntityName>
```

## Examples
```
/gen-resource Category
/gen-resource Order
```

## Project Pattern

Location: `src/main/kotlin/com/example/product/infrastructure/web/`

### Resource Template
```kotlin
package com.example.product.infrastructure.web

import com.example.product.application.service.{EntityName}Service
import com.example.product.domain.model.{EntityName}
import jakarta.validation.Valid
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

@Path("/api/{entityNames}")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class {EntityName}Resource(private val {entityName}Service: {EntityName}Service) {

    @GET
    fun list(): List<{EntityName}> {
        return {entityName}Service.findAll()
    }

    @GET
    @Path("/{id}")
    fun get(@PathParam("id") id: Long): Response {
        val {entityName} = {entityName}Service.findById(id)
        return if ({entityName} != null) {
            Response.ok({entityName}).build()
        } else {
            Response.status(Response.Status.NOT_FOUND).build()
        }
    }

    @POST
    fun create(@Valid {entityName}: {EntityName}): Response {
        val created = {entityName}Service.create({entityName})
        return Response.status(Response.Status.CREATED).entity(created).build()
    }

    @PUT
    @Path("/{id}")
    fun update(@PathParam("id") id: Long, @Valid {entityName}: {EntityName}): Response {
        val updated = {entityName}Service.update(id, {entityName})
        return if (updated != null) {
            Response.ok(updated).build()
        } else {
            Response.status(Response.Status.NOT_FOUND).build()
        }
    }

    @DELETE
    @Path("/{id}")
    fun delete(@PathParam("id") id: Long): Response {
        val deleted = {entityName}Service.delete(id)
        return if (deleted) {
            Response.noContent().build()
        } else {
            Response.status(Response.Status.NOT_FOUND).build()
        }
    }
}
```

## Conventions
- Path: `/api/{entityNames}` (plural, lowercase)
- `@Produces` and `@Consumes` JSON
- Use `@Valid` for request body validation
- Return appropriate HTTP status codes:
  - 200 OK: Successful GET/PUT
  - 201 Created: Successful POST
  - 204 No Content: Successful DELETE
  - 404 Not Found: Entity not found

## HTTP Methods
| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/{entityNames}` | List all |
| GET | `/api/{entityNames}/{id}` | Get by ID |
| POST | `/api/{entityNames}` | Create new |
| PUT | `/api/{entityNames}/{id}` | Update |
| DELETE | `/api/{entityNames}/{id}` | Delete |

## Prerequisites
- Service: `application/service/{EntityName}Service.kt`

## Reference Files
- `ProductResource.kt` - Standard REST CRUD
- `CustomerResource.kt` - Simple pattern
