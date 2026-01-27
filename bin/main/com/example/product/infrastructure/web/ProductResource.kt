package com.example.product.infrastructure.web

import com.example.product.application.service.ProductService
import com.example.product.domain.model.Product
import jakarta.validation.Valid
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

@Path("/api/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class ProductResource(private val productService: ProductService) {

    @GET
    fun list(): List<Product> {
        return productService.findAll()
    }

    @GET
    @Path("/{id}")
    fun get(@PathParam("id") id: Long): Response {
        val product = productService.findById(id)
        return if (product != null) {
            Response.ok(product).build()
        } else {
            Response.status(Response.Status.NOT_FOUND).build()
        }
    }

    @POST
    fun create(@Valid product: Product): Response {
        val created = productService.create(product)
        return Response.status(Response.Status.CREATED).entity(created).build()
    }

    @PUT
    @Path("/{id}")
    fun update(@PathParam("id") id: Long, @Valid product: Product): Response {
        val updated = productService.update(id, product)
        return if (updated != null) {
            Response.ok(updated).build()
        } else {
            Response.status(Response.Status.NOT_FOUND).build()
        }
    }

    @DELETE
    @Path("/{id}")
    fun delete(@PathParam("id") id: Long): Response {
        val deleted = productService.delete(id)
        return if (deleted) {
            Response.noContent().build()
        } else {
            Response.status(Response.Status.NOT_FOUND).build()
        }
    }
}
