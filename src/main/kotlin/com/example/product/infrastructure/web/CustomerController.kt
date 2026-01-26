package com.example.product.infrastructure.web

import com.example.product.application.service.CustomerService
import com.example.product.domain.model.Customer
import io.quarkus.qute.Location
import io.quarkus.qute.Template
import io.quarkus.qute.TemplateInstance
import io.smallrye.common.annotation.Blocking
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import java.net.URI

@Path("/customers")
@Produces(MediaType.TEXT_HTML)
@Blocking
class CustomerController(
    private val customerService: CustomerService,
    @Location("customers/customerList") private val customerList: Template,
    @Location("customers/customerForm") private val customerForm: Template
) {

    @GET
    fun list(@QueryParam("search") search: String?): TemplateInstance {
        val customers = if (search.isNullOrBlank()) {
            customerService.findAll()
        } else {
            customerService.findByName(search)
        }
        return customerList
            .data("customers", customers)
            .data("search", search ?: "")
            .data("activeMenu", "customers")
    }

    @GET
    @Path("/new")
    fun newCustomer(): TemplateInstance {
        return customerForm
            .data("customer", Customer())
            .data("action", "create")
            .data("activeMenu", "customers")
    }

    @GET
    @Path("/{id}/edit")
    fun edit(@PathParam("id") id: Long): TemplateInstance {
        val customer = customerService.findById(id)
        return customerForm
            .data("customer", customer)
            .data("action", "edit")
            .data("activeMenu", "customers")
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    fun create(
        @FormParam("name") name: String,
        @FormParam("email") email: String?,
        @FormParam("phone") phone: String?,
        @FormParam("address") address: String?,
        @FormParam("document") document: String?
    ): Response {
        val customer = Customer(
            name = name,
            email = email,
            phone = phone,
            address = address,
            document = document
        )
        customerService.create(customer)
        return Response.seeOther(URI.create("/customers")).build()
    }

    @POST
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    fun update(
        @PathParam("id") id: Long,
        @FormParam("name") name: String,
        @FormParam("email") email: String?,
        @FormParam("phone") phone: String?,
        @FormParam("address") address: String?,
        @FormParam("document") document: String?
    ): Response {
        val customer = Customer(
            name = name,
            email = email,
            phone = phone,
            address = address,
            document = document
        )
        customerService.update(id, customer)
        return Response.seeOther(URI.create("/customers")).build()
    }

    @POST
    @Path("/{id}/delete")
    fun delete(@PathParam("id") id: Long): Response {
        customerService.delete(id)
        return Response.seeOther(URI.create("/customers")).build()
    }
}
