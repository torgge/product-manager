package com.example.product.infrastructure.web

import com.example.product.application.service.StockService
import com.example.product.domain.model.StockMovement
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

data class StockOperationRequest(
    val quantity: Int,
    val reason: String?,
    val createdBy: String = "user"
)

@Path("/api/stock")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class StockMovementResource(private val stockService: StockService) {

    @GET
    @Path("/product/{productId}")
    fun getProductMovements(@PathParam("productId") productId: Long): List<StockMovement> {
        return stockService.findByProductId(productId)
    }

    @GET
    @Path("/recent")
    fun getRecentMovements(@QueryParam("limit") @DefaultValue("10") limit: Int): List<StockMovement> {
        return stockService.findRecentMovements(limit)
    }

    @POST
    @Path("/product/{productId}/add")
    fun addStock(
        @PathParam("productId") productId: Long,
        request: StockOperationRequest
    ): Response {
        return try {
            val movement = stockService.addStock(
                productId = productId,
                quantity = request.quantity,
                reason = request.reason,
                createdBy = request.createdBy
            )
            Response.ok(movement).build()
        } catch (e: IllegalArgumentException) {
            Response.status(Response.Status.BAD_REQUEST)
                .entity(mapOf("error" to e.message))
                .build()
        }
    }

    @POST
    @Path("/product/{productId}/remove")
    fun removeStock(
        @PathParam("productId") productId: Long,
        request: StockOperationRequest
    ): Response {
        return try {
            val movement = stockService.removeStock(
                productId = productId,
                quantity = request.quantity,
                reason = request.reason,
                createdBy = request.createdBy
            )
            Response.ok(movement).build()
        } catch (e: IllegalArgumentException) {
            Response.status(Response.Status.BAD_REQUEST)
                .entity(mapOf("error" to e.message))
                .build()
        }
    }

    @POST
    @Path("/product/{productId}/adjust")
    fun adjustStock(
        @PathParam("productId") productId: Long,
        request: StockOperationRequest
    ): Response {
        return try {
            val movement = stockService.adjustStock(
                productId = productId,
                newQuantity = request.quantity,
                reason = request.reason,
                createdBy = request.createdBy
            )
            Response.ok(movement).build()
        } catch (e: IllegalArgumentException) {
            Response.status(Response.Status.BAD_REQUEST)
                .entity(mapOf("error" to e.message))
                .build()
        }
    }
}
