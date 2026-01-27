package com.example.product.infrastructure.web

import com.example.product.application.service.SaleOrderItemRequest
import com.example.product.application.service.SaleService
import com.example.product.domain.model.SaleOrder
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

data class CreateSaleRequest(
    val customerId: Long,
    val items: List<SaleOrderItemRequest>,
    val notes: String?
)

@Path("/api/sales")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class SaleResource(private val saleService: SaleService) {

    @GET
    fun listAll(): List<SaleOrder> {
        return saleService.findAll()
    }

    @GET
    @Path("/{id}")
    fun getById(@PathParam("id") id: Long): SaleOrder? {
        return saleService.findById(id)
    }

    @POST
    fun create(request: CreateSaleRequest): Response {
        return try {
            val sale = saleService.create(
                customerId = request.customerId,
                items = request.items,
                notes = request.notes,
                createdBy = "api-user"
            )
            Response.ok(sale).build()
        } catch (e: IllegalArgumentException) {
            Response.status(Response.Status.BAD_REQUEST)
                .entity(mapOf("error" to e.message))
                .build()
        }
    }

    @POST
    @Path("/{id}/confirm")
    fun confirm(@PathParam("id") id: Long): Response {
        return try {
            val sale = saleService.confirmSale(id)
            Response.ok(sale).build()
        } catch (e: IllegalArgumentException) {
            Response.status(Response.Status.BAD_REQUEST)
                .entity(mapOf("error" to e.message))
                .build()
        }
    }

    @POST
    @Path("/{id}/deliver")
    fun deliver(@PathParam("id") id: Long): Response {
        return try {
            val sale = saleService.deliverSale(id)
            Response.ok(sale).build()
        } catch (e: IllegalArgumentException) {
            Response.status(Response.Status.BAD_REQUEST)
                .entity(mapOf("error" to e.message))
                .build()
        }
    }

    @POST
    @Path("/{id}/cancel")
    fun cancel(@PathParam("id") id: Long): Response {
        return try {
            val sale = saleService.cancelSale(id)
            Response.ok(sale).build()
        } catch (e: IllegalArgumentException) {
            Response.status(Response.Status.BAD_REQUEST)
                .entity(mapOf("error" to e.message))
                .build()
        }
    }
}
