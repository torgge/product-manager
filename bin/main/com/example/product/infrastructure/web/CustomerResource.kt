package com.example.product.infrastructure.web

import com.example.product.application.service.CustomerService
import com.example.product.domain.model.Customer
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

@Path("/api/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class CustomerResource(private val customerService: CustomerService) {

    @GET
    fun listAll(): List<Customer> {
        return customerService.findAll()
    }

    @GET
    @Path("/{id}")
    fun getById(@PathParam("id") id: Long): Customer? {
        return customerService.findById(id)
    }

    @POST
    fun create(customer: Customer): Response {
        val created = customerService.create(customer)
        return Response.ok(created).build()
    }

    @PUT
    @Path("/{id}")
    fun update(@PathParam("id") id: Long, customer: Customer): Response {
        val updated = customerService.update(id, customer)
        return if (updated != null) {
            Response.ok(updated).build()
        } else {
            Response.status(Response.Status.NOT_FOUND).build()
        }
    }

    @DELETE
    @Path("/{id}")
    fun delete(@PathParam("id") id: Long): Response {
        val deleted = customerService.delete(id)
        return if (deleted) {
            Response.noContent().build()
        } else {
            Response.status(Response.Status.NOT_FOUND).build()
        }
    }
}
