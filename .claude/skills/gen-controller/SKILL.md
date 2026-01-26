---
description: Generate a Qute-based web controller
disable-model-invocation: true
---

# /gen-controller - Generate Web Controller

Generate a web controller with Qute templates for HTML rendering.

## Usage
```
/gen-controller <EntityName>
```

## Examples
```
/gen-controller Category
/gen-controller Order
```

## Project Pattern

Location: `src/main/kotlin/com/example/product/infrastructure/web/`

### Controller Template
```kotlin
package com.example.product.infrastructure.web

import com.example.product.application.service.{EntityName}Service
import com.example.product.domain.model.{EntityName}
import io.quarkus.qute.Location
import io.quarkus.qute.Template
import io.quarkus.qute.TemplateInstance
import io.smallrye.common.annotation.Blocking
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import java.net.URI

@Path("/{entityNames}")
@Produces(MediaType.TEXT_HTML)
@Blocking
class {EntityName}Controller(
    private val {entityName}Service: {EntityName}Service,
    @Location("{entityNames}/{entityName}List") private val {entityName}List: Template,
    @Location("{entityNames}/{entityName}Form") private val {entityName}Form: Template
) {

    @GET
    fun list(@QueryParam("search") search: String?): TemplateInstance {
        val {entityNames} = if (search.isNullOrBlank()) {
            {entityName}Service.findAll()
        } else {
            {entityName}Service.findAll() // Replace with search method
        }
        return {entityName}List
            .data("{entityNames}", {entityNames})
            .data("search", search ?: "")
            .data("activeMenu", "{entityNames}")
    }

    @GET
    @Path("/new")
    fun newForm(): TemplateInstance {
        return {entityName}Form
            .data("{entityName}", {EntityName}())
            .data("action", "create")
            .data("activeMenu", "{entityNames}")
    }

    @GET
    @Path("/{id}/edit")
    fun edit(@PathParam("id") id: Long): TemplateInstance {
        val {entityName} = {entityName}Service.findById(id)
        return {entityName}Form
            .data("{entityName}", {entityName})
            .data("action", "edit")
            .data("activeMenu", "{entityNames}")
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    fun create(
        @FormParam("name") name: String
        // Add other @FormParam fields
    ): Response {
        val {entityName} = {EntityName}(
            name = name
            // Set other fields
        )
        {entityName}Service.create({entityName})
        return Response.seeOther(URI.create("/{entityNames}")).build()
    }

    @POST
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    fun update(
        @PathParam("id") id: Long,
        @FormParam("name") name: String
        // Add other @FormParam fields
    ): Response {
        val {entityName} = {EntityName}(
            name = name
            // Set other fields
        )
        {entityName}Service.update(id, {entityName})
        return Response.seeOther(URI.create("/{entityNames}")).build()
    }

    @POST
    @Path("/{id}/delete")
    fun delete(@PathParam("id") id: Long): Response {
        {entityName}Service.delete(id)
        return Response.seeOther(URI.create("/{entityNames}")).build()
    }
}
```

## Conventions
- Use `@Blocking` for template rendering
- `@Location` annotation for template injection
- `@Produces(MediaType.TEXT_HTML)` for HTML output
- POST for create/update/delete (form submissions)
- Redirect with `Response.seeOther()` after mutations
- Pass `activeMenu` for navigation highlighting

## Template Data
- `{entityNames}` - List for table display
- `{entityName}` - Single entity for forms
- `action` - "create" or "edit" for form mode
- `activeMenu` - Navigation highlighting

## Prerequisites
- Service: `application/service/{EntityName}Service.kt`
- Templates: `resources/templates/{entityNames}/`

## Reference Files
- `ProductController.kt` - Full CRUD with detail view
- `CustomerController.kt` - Simple CRUD pattern
