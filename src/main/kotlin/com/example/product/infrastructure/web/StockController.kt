package com.example.product.infrastructure.web

import com.example.product.application.service.StockService
import com.example.product.application.service.ProductService
import com.example.product.domain.model.StockLocation
import io.quarkus.qute.Location
import io.quarkus.qute.Template
import io.quarkus.qute.TemplateInstance
import io.smallrye.common.annotation.Blocking
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import java.net.URI

@Path("/stock")
@Produces(MediaType.TEXT_HTML)
@Blocking
class StockController(
    private val stockService: StockService,
    private val productService: ProductService,
    @Location("stock/stockList") private val stockList: Template,
    @Location("stock/stockDetail") private val stockDetail: Template,
    @Location("stock/stockAdjustment") private val stockAdjustment: Template,
    @Location("stock/stockAlerts") private val stockAlerts: Template
) {

    @GET
    fun list(
        @QueryParam("search") search: String?,
        @QueryParam("location") locationParam: String?,
        @QueryParam("filter") filter: String?
    ): TemplateInstance {
        val stocks = when {
            filter == "low" -> stockService.getLowStockAlerts()
            filter == "out" -> stockService.getOutOfStockItems()
            !search.isNullOrBlank() -> stockService.searchStocks(search)
            else -> stockService.getAllStocks()
        }

        val location = locationParam?.let {
            try { StockLocation.valueOf(it) } catch (e: Exception) { null }
        }

        val filteredStocks = location?.let { loc ->
            stocks.filter { it.location == loc }
        } ?: stocks

        return stockList
            .data("stocks", filteredStocks)
            .data("search", search ?: "")
            .data("selectedLocation", location)
            .data("locations", StockLocation.values())
            .data("filter", filter)
            .data("activeMenu", "stock")
    }

    @GET
    @Path("/alerts")
    fun alerts(): TemplateInstance {
        val lowStock = stockService.getLowStockAlerts()
        val outOfStock = stockService.getOutOfStockItems()

        return stockAlerts
            .data("lowStock", lowStock)
            .data("outOfStock", outOfStock)
            .data("activeMenu", "stock")
    }

    @GET
    @Path("/product/{productId}")
    fun detail(@PathParam("productId") productId: Long): TemplateInstance {
        val product = productService.findById(productId)
        val stock = stockService.getCurrentStock(productId)
        val movements = stockService.findByProductId(productId)

        return stockDetail
            .data("product", product)
            .data("stock", stock)
            .data("movements", movements)
            .data("activeMenu", "stock")
    }

    @GET
    @Path("/product/{productId}/adjust")
    fun adjustmentForm(@PathParam("productId") productId: Long): TemplateInstance {
        val product = productService.findById(productId)
        val stock = stockService.getCurrentStock(productId)

        return stockAdjustment
            .data("product", product)
            .data("stock", stock)
            .data("locations", StockLocation.values())
            .data("activeMenu", "stock")
    }

    @POST
    @Path("/product/{productId}/adjust")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    fun performAdjustment(
        @PathParam("productId") productId: Long,
        @FormParam("quantity") quantity: Int,
        @FormParam("reason") reason: String?,
        @FormParam("location") locationParam: String?
    ): Response {
        val location = locationParam?.let {
            try { StockLocation.valueOf(it) } catch (e: Exception) { StockLocation.MAIN_WAREHOUSE }
        } ?: StockLocation.MAIN_WAREHOUSE

        stockService.adjustStock(
            productId = productId,
            newQuantity = quantity,
            reason = reason,
            location = location
        )

        return Response.seeOther(URI.create("/stock/product/$productId")).build()
    }

    @POST
    @Path("/product/{productId}/thresholds")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    fun updateThresholds(
        @PathParam("productId") productId: Long,
        @FormParam("minQuantity") minQuantity: Int,
        @FormParam("maxQuantity") maxQuantityParam: String?,
        @FormParam("location") locationParam: String?
    ): Response {
        val location = locationParam?.let {
            try { StockLocation.valueOf(it) } catch (e: Exception) { StockLocation.MAIN_WAREHOUSE }
        } ?: StockLocation.MAIN_WAREHOUSE

        val maxQuantity = maxQuantityParam?.takeIf { it.isNotBlank() }?.toIntOrNull()

        stockService.updateStockThresholds(
            productId = productId,
            minQuantity = minQuantity,
            maxQuantity = maxQuantity,
            location = location
        )

        return Response.seeOther(URI.create("/stock/product/$productId")).build()
    }
}
