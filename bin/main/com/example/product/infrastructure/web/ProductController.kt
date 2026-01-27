package com.example.product.infrastructure.web

import com.example.product.application.service.ProductService
import com.example.product.application.service.StockService
import com.example.product.domain.model.Product
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
    private val index: Template,
    @Location("products/productList") private val productList: Template,
    @Location("products/productDetail") private val productDetail: Template,
    @Location("products/productForm") private val productForm: Template
) {

    @GET
    fun index(): TemplateInstance {
        val totalProducts = productService.count()
        val availableProducts = productService.findAvailableProducts().size
        return index
            .data("totalProducts", totalProducts)
            .data("availableProducts", availableProducts)
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
        @FormParam("price") price: java.math.BigDecimal,
        @FormParam("purchasePrice") purchasePrice: java.math.BigDecimal?,
        @FormParam("profitMargin") profitMargin: java.math.BigDecimal?
    ): Response {
        val product = Product(
            name = name,
            description = description,
            category = category,
            price = price,
            purchasePrice = purchasePrice ?: java.math.BigDecimal.ZERO,
            profitMargin = profitMargin ?: java.math.BigDecimal.ZERO
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
        @FormParam("price") price: java.math.BigDecimal,
        @FormParam("purchasePrice") purchasePrice: java.math.BigDecimal?,
        @FormParam("profitMargin") profitMargin: java.math.BigDecimal?
    ): Response {
        val product = Product(
            name = name,
            description = description,
            category = category,
            price = price,
            purchasePrice = purchasePrice ?: java.math.BigDecimal.ZERO,
            profitMargin = profitMargin ?: java.math.BigDecimal.ZERO
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
