package com.example.product.infrastructure.web

import com.example.product.application.service.StockService
import com.example.product.domain.model.Stock
import com.example.product.domain.model.StockLocation
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

data class StockThresholdsRequest(
    val minQuantity: Int,
    val maxQuantity: Int?,
    val location: StockLocation = StockLocation.MAIN_WAREHOUSE
)

@Path("/api/stock")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class StockResource(private val stockService: StockService) {

    @GET
    fun getAllStocks(): List<Stock> {
        return stockService.getAllStocks()
    }

    @GET
    @Path("/product/{productId}")
    fun getProductStock(@PathParam("productId") productId: Long): Response {
        val stock = stockService.getCurrentStock(productId)
        return if (stock != null) {
            Response.ok(stock).build()
        } else {
            Response.status(Response.Status.NOT_FOUND).build()
        }
    }

    @GET
    @Path("/alerts/low")
    fun getLowStockAlerts(): List<Stock> {
        return stockService.getLowStockAlerts()
    }

    @GET
    @Path("/alerts/out")
    fun getOutOfStockItems(): List<Stock> {
        return stockService.getOutOfStockItems()
    }

    @POST
    @Path("/product/{productId}/thresholds")
    fun updateThresholds(
        @PathParam("productId") productId: Long,
        request: StockThresholdsRequest
    ): Response {
        return try {
            val stock = stockService.updateStockThresholds(
                productId = productId,
                minQuantity = request.minQuantity,
                maxQuantity = request.maxQuantity,
                location = request.location
            )
            Response.ok(stock).build()
        } catch (e: IllegalArgumentException) {
            Response.status(Response.Status.BAD_REQUEST)
                .entity(mapOf("error" to e.message))
                .build()
        }
    }

    @POST
    @Path("/migrate")
    fun migrateFromMovements(): Response {
        stockService.migrateFromMovements()
        return Response.ok(mapOf("message" to "Migration completed successfully")).build()
    }
}
