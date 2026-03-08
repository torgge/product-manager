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
    @Location("stock/stockAlerts") private val stockAlerts: Template,
    @Location("stock/stockDashboard") private val stockDashboard: Template
) {

    @GET
    @Path("/dashboard")
    fun dashboard(): TemplateInstance {
        val allStocks = stockService.getAllStocks()

        // Calculate totals by location
        val stockByLocation = allStocks.groupBy { it.location }
            .mapValues { (_, stocks) ->
                mapOf(
                    "totalItems" to stocks.size,
                    "totalQuantity" to stocks.sumOf { it.quantity },
                    "lowStockCount" to stocks.count { it.isLowStock() },
                    "outOfStockCount" to stocks.count { it.isOutOfStock() }
                )
            }

        // Top products by quantity
        val topProducts = allStocks
            .groupBy { it.product?.id }
            .mapNotNull { (_, stocks) ->
                stocks.firstOrNull()?.let { stock ->
                    mapOf(
                        "product" to stock.product,
                        "totalQuantity" to stocks.sumOf { it.quantity },
                        "locations" to stocks.size
                    )
                }
            }
            .sortedByDescending { it["totalQuantity"] as Int }
            .take(10)
            .mapIndexed { index, item ->
                item + ("rank" to index + 1)
            }

        // Category analysis
        val productsByCategory = productService.findAll()
            .groupBy { it.category ?: "Uncategorized" }
            .mapValues { (_, products) ->
                val categoryStocks = allStocks.filter { stock ->
                    products.any { it.id == stock.product?.id }
                }
                mapOf(
                    "productCount" to products.size,
                    "totalStock" to categoryStocks.sumOf { it.quantity },
                    "avgStock" to if (products.isNotEmpty())
                        categoryStocks.sumOf { it.quantity } / products.size
                        else 0
                )
            }

        // Overall stats
        val totalItems = allStocks.size
        val totalQuantity = allStocks.sumOf { it.quantity }
        val lowStockCount = allStocks.count { it.isLowStock() }
        val outOfStockCount = allStocks.count { it.isOutOfStock() }
        val averageStock = if (allStocks.isNotEmpty()) totalQuantity / allStocks.size else 0

        return stockDashboard
            .data("stockByLocation", stockByLocation)
            .data("topProducts", topProducts)
            .data("productsByCategory", productsByCategory)
            .data("totalItems", totalItems)
            .data("totalQuantity", totalQuantity)
            .data("lowStockCount", lowStockCount)
            .data("outOfStockCount", outOfStockCount)
            .data("averageStock", averageStock)
            .data("locations", StockLocation.values())
            .data("activeMenu", "stock")
    }

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
