package com.example.product.infrastructure.web

import com.example.product.application.service.CustomerService
import com.example.product.domain.model.Customer
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

data class PaginatedResponse<T>(
    val items: List<T>,
    val total: Long
)

@Path("/api/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class CustomerResource(private val customerService: CustomerService) {

    @GET
    fun list(
        @QueryParam("search") search: String?,
        @QueryParam("limit") @DefaultValue("10") limit: Int,
        @QueryParam("offset") @DefaultValue("0") offset: Int
    ): PaginatedResponse<Customer> {
        val all = if (search.isNullOrBlank()) {
            customerService.findAll()
        } else {
            customerService.findByName(search)
        }
        val total = all.size.toLong()
        val items = all.drop(offset).take(limit)
        return PaginatedResponse(items, total)
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
