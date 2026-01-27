package com.example.product.infrastructure.web

import com.example.product.application.service.PurchaseOrderItemRequest
import com.example.product.application.service.PurchaseService
import com.example.product.domain.model.PurchaseOrder
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

data class CreatePurchaseRequest(
    val supplierId: Long,
    val items: List<PurchaseOrderItemRequest>,
    val notes: String?
)

@Path("/api/purchases")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class PurchaseResource(private val purchaseService: PurchaseService) {

    @GET
    fun listAll(): List<PurchaseOrder> {
        return purchaseService.findAll()
    }

    @GET
    @Path("/{id}")
    fun getById(@PathParam("id") id: Long): PurchaseOrder? {
        return purchaseService.findById(id)
    }

    @POST
    fun create(request: CreatePurchaseRequest): Response {
        return try {
            val purchase = purchaseService.create(
                supplierId = request.supplierId,
                items = request.items,
                notes = request.notes,
                createdBy = "api-user"
            )
            Response.ok(purchase).build()
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
            val purchase = purchaseService.confirmPurchase(id)
            Response.ok(purchase).build()
        } catch (e: IllegalArgumentException) {
            Response.status(Response.Status.BAD_REQUEST)
                .entity(mapOf("error" to e.message))
                .build()
        }
    }

    @POST
    @Path("/{id}/receive")
    fun receive(@PathParam("id") id: Long): Response {
        return try {
            val purchase = purchaseService.receivePurchase(id)
            Response.ok(purchase).build()
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
            val purchase = purchaseService.cancelPurchase(id)
            Response.ok(purchase).build()
        } catch (e: IllegalArgumentException) {
            Response.status(Response.Status.BAD_REQUEST)
                .entity(mapOf("error" to e.message))
                .build()
        }
    }
}
