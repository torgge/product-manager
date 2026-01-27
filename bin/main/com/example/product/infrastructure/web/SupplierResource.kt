package com.example.product.infrastructure.web

import com.example.product.application.service.SupplierService
import com.example.product.domain.model.Supplier
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

@Path("/api/suppliers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class SupplierResource(private val supplierService: SupplierService) {

    @GET
    fun listAll(): List<Supplier> {
        return supplierService.findAll()
    }

    @GET
    @Path("/{id}")
    fun getById(@PathParam("id") id: Long): Supplier? {
        return supplierService.findById(id)
    }

    @POST
    fun create(supplier: Supplier): Response {
        val created = supplierService.create(supplier)
        return Response.ok(created).build()
    }

    @PUT
    @Path("/{id}")
    fun update(@PathParam("id") id: Long, supplier: Supplier): Response {
        val updated = supplierService.update(id, supplier)
        return if (updated != null) {
            Response.ok(updated).build()
        } else {
            Response.status(Response.Status.NOT_FOUND).build()
        }
    }

    @DELETE
    @Path("/{id}")
    fun delete(@PathParam("id") id: Long): Response {
        val deleted = supplierService.delete(id)
        return if (deleted) {
            Response.noContent().build()
        } else {
            Response.status(Response.Status.NOT_FOUND).build()
        }
    }
}
