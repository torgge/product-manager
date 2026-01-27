package com.example.product.infrastructure.web

import com.example.product.application.service.SupplierService
import com.example.product.domain.model.Supplier
import io.quarkus.qute.Location
import io.quarkus.qute.Template
import io.quarkus.qute.TemplateInstance
import io.smallrye.common.annotation.Blocking
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import java.net.URI

@Path("/suppliers")
@Produces(MediaType.TEXT_HTML)
@Blocking
class SupplierController(
    private val supplierService: SupplierService,
    @Location("suppliers/supplierList") private val supplierList: Template,
    @Location("suppliers/supplierForm") private val supplierForm: Template
) {

    @GET
    fun list(@QueryParam("search") search: String?): TemplateInstance {
        val suppliers = if (search.isNullOrBlank()) {
            supplierService.findAll()
        } else {
            supplierService.findByName(search)
        }
        return supplierList
            .data("suppliers", suppliers)
            .data("search", search ?: "")
            .data("activeMenu", "suppliers")
    }

    @GET
    @Path("/new")
    fun newSupplier(): TemplateInstance {
        return supplierForm
            .data("supplier", Supplier())
            .data("action", "create")
            .data("activeMenu", "suppliers")
    }

    @GET
    @Path("/{id}/edit")
    fun edit(@PathParam("id") id: Long): TemplateInstance {
        val supplier = supplierService.findById(id)
        return supplierForm
            .data("supplier", supplier)
            .data("action", "edit")
            .data("activeMenu", "suppliers")
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
        val supplier = Supplier(
            name = name,
            email = email,
            phone = phone,
            address = address,
            document = document
        )
        supplierService.create(supplier)
        return Response.seeOther(URI.create("/suppliers")).build()
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
        val supplier = Supplier(
            name = name,
            email = email,
            phone = phone,
            address = address,
            document = document
        )
        supplierService.update(id, supplier)
        return Response.seeOther(URI.create("/suppliers")).build()
    }

    @POST
    @Path("/{id}/delete")
    fun delete(@PathParam("id") id: Long): Response {
        supplierService.delete(id)
        return Response.seeOther(URI.create("/suppliers")).build()
    }
}
