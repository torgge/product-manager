package com.example.product.infrastructure.web

import com.example.product.application.service.*
import com.example.product.domain.model.Product
import com.example.product.domain.model.OrderStatus
import io.quarkus.qute.Location
import io.quarkus.qute.Template
import io.quarkus.qute.TemplateInstance
import io.smallrye.common.annotation.Blocking
import jakarta.validation.Valid
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import java.net.URI

@Path("/")
@Produces(MediaType.TEXT_HTML)
@Blocking
class ProductController(
    private val productService: ProductService,
    private val stockService: StockService,
    private val customerService: CustomerService,
    private val supplierService: SupplierService,
    private val saleService: SaleService,
    private val purchaseService: PurchaseService,
    private val index: Template,
    @Location("products/productList") private val productList: Template,
    @Location("products/productDetail") private val productDetail: Template,
    @Location("products/productForm") private val productForm: Template
) {

    @GET
    fun index(): TemplateInstance {
        // Products
        val totalProducts = productService.count()
        val availableProducts = productService.findAvailableProducts().size

        // Customers & Suppliers
        val totalCustomers = customerService.findAll().size
        val totalSuppliers = supplierService.findAll().size

        // Stock Alerts
        val lowStockItems = stockService.getLowStockAlerts()
        val outOfStockItems = stockService.getOutOfStockItems()
        val lowStockCount = lowStockItems.size
        val outOfStockCount = outOfStockItems.size

        // Orders
        val pendingSales = saleService.findByStatus(OrderStatus.PENDING).size
        val pendingPurchases = purchaseService.findByStatus(OrderStatus.PENDING).size
        val recentSales = saleService.findRecent(5)
        val recentPurchases = purchaseService.findRecent(5)

        // Total sales value (last 10 sales)
        val totalSalesValue = recentSales.sumOf { it.totalAmount }

        // Stock Analytics
        val allStocks = stockService.getAllStocks()
        val totalStockQuantity = allStocks.sumOf { it.quantity }

        val stockByLocation = allStocks.groupBy { it.location }
            .mapValues { (_, stocks) ->
                val locationQuantity = stocks.sumOf { it.quantity }
                val percentage = if (totalStockQuantity > 0) {
                    (locationQuantity * 100) / totalStockQuantity
                } else {
                    0
                }
                mapOf(
                    "totalItems" to stocks.size,
                    "totalQuantity" to locationQuantity,
                    "lowStockCount" to stocks.count { it.isLowStock() },
                    "outOfStockCount" to stocks.count { it.isOutOfStock() },
                    "percentage" to percentage
                )
            }
        val stockLocations = com.example.product.domain.model.StockLocation.values()

        return index
            .data("totalProducts", totalProducts)
            .data("availableProducts", availableProducts)
            .data("totalCustomers", totalCustomers)
            .data("totalSuppliers", totalSuppliers)
            .data("lowStockCount", lowStockCount)
            .data("outOfStockCount", outOfStockCount)
            .data("lowStockItems", lowStockItems.take(5))
            .data("outOfStockItems", outOfStockItems.take(5))
            .data("pendingSales", pendingSales)
            .data("pendingPurchases", pendingPurchases)
            .data("recentSales", recentSales)
            .data("recentPurchases", recentPurchases)
            .data("totalSalesValue", totalSalesValue)
            .data("stockByLocation", stockByLocation)
            .data("totalStockQuantity", totalStockQuantity)
            .data("stockLocations", stockLocations)
            .data("activeMenu", "home")
    }

    @GET
    @Path("/products")
    fun list(@QueryParam("search") search: String?): TemplateInstance {
        val products = if (search.isNullOrBlank()) {
            productService.findAll()
        } else {
            productService.findByName(search)
        }

        // Criar um mapa de product ID para estoque atual
        val stockMap = products.associate { product ->
            product.id!! to stockService.calculateCurrentStock(product.id!!)
        }

        return productList
            .data("products", products)
            .data("stockMap", stockMap)
            .data("search", search ?: "")
            .data("activeMenu", "products")
    }

    @GET
    @Path("/products/{id}")
    fun detail(@PathParam("id") id: Long): TemplateInstance {
        val product = productService.findById(id)
        val stockMovements = if (product != null) {
            stockService.findByProductId(id)
        } else {
            emptyList()
        }
        val currentStock = if (product != null) {
            stockService.calculateCurrentStock(id)
        } else {
            0
        }
        return productDetail
            .data("product", product)
            .data("currentStock", currentStock)
            .data("stockMovements", stockMovements)
            .data("activeMenu", "products")
    }

    @GET
    @Path("/products/new")
    fun newProduct(): TemplateInstance {
        return productForm
            .data("product", Product())
            .data("action", "create")
            .data("activeMenu", "products")
    }

    @GET
    @Path("/products/{id}/edit")
    fun edit(@PathParam("id") id: Long): TemplateInstance {
        val product = productService.findById(id)
        return productForm
            .data("product", product)
            .data("action", "edit")
            .data("activeMenu", "products")
    }

    @POST
    @Path("/products")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    fun create(
        @FormParam("name") name: String,
        @FormParam("description") description: String?,
        @FormParam("category") category: String?,
        @FormParam("price") priceParam: String?,
        @FormParam("purchasePrice") purchasePriceParam: String?,
        @FormParam("profitMargin") profitMarginParam: String?
    ): Response {
        val price = priceParam?.takeIf { it.isNotBlank() }?.toBigDecimalOrNull() ?: java.math.BigDecimal.ZERO
        val purchasePrice = purchasePriceParam?.takeIf { it.isNotBlank() }?.toBigDecimalOrNull() ?: java.math.BigDecimal.ZERO
        val profitMargin = profitMarginParam?.takeIf { it.isNotBlank() }?.toBigDecimalOrNull() ?: java.math.BigDecimal.ZERO

        val product = Product(
            name = name,
            description = description,
            category = category,
            price = price,
            purchasePrice = purchasePrice,
            profitMargin = profitMargin
        )
        productService.create(product)
        return Response.seeOther(URI.create("/products")).build()
    }

    @POST
    @Path("/products/{id}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    fun update(
        @PathParam("id") id: Long,
        @FormParam("name") name: String,
        @FormParam("description") description: String?,
        @FormParam("category") category: String?,
        @FormParam("price") priceParam: String?,
        @FormParam("purchasePrice") purchasePriceParam: String?,
        @FormParam("profitMargin") profitMarginParam: String?
    ): Response {
        val price = priceParam?.takeIf { it.isNotBlank() }?.toBigDecimalOrNull() ?: java.math.BigDecimal.ZERO
        val purchasePrice = purchasePriceParam?.takeIf { it.isNotBlank() }?.toBigDecimalOrNull() ?: java.math.BigDecimal.ZERO
        val profitMargin = profitMarginParam?.takeIf { it.isNotBlank() }?.toBigDecimalOrNull() ?: java.math.BigDecimal.ZERO

        val product = Product(
            name = name,
            description = description,
            category = category,
            price = price,
            purchasePrice = purchasePrice,
            profitMargin = profitMargin
        )
        productService.update(id, product)
        return Response.seeOther(URI.create("/products")).build()
    }

    @POST
    @Path("/products/{id}/delete")
    fun delete(@PathParam("id") id: Long): Response {
        productService.delete(id)
        return Response.seeOther(URI.create("/products")).build()
    }

    // Manual stock control endpoints removed - stock is now managed automatically:
    // - Purchase received → stock IN
    // - Sale confirmed → stock OUT
    // For manual adjustments (fixing errors), use the REST API: /api/stock/product/{id}/adjust
}
