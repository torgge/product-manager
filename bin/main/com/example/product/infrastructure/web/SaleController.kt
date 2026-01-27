package com.example.product.infrastructure.web

import com.example.product.application.service.CustomerService
import com.example.product.application.service.ProductService
import com.example.product.application.service.SaleService
import com.example.product.application.service.StockService
import io.quarkus.qute.Location
import io.quarkus.qute.Template
import io.quarkus.qute.TemplateInstance
import io.smallrye.common.annotation.Blocking
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import java.net.URI

@Path("/sales")
@Produces(MediaType.TEXT_HTML)
@Blocking
class SaleController(
    private val saleService: SaleService,
    private val customerService: CustomerService,
    private val productService: ProductService,
    private val stockService: StockService,
    @Location("sales/saleList") private val saleList: Template,
    @Location("sales/saleForm") private val saleForm: Template,
    @Location("sales/saleDetail") private val saleDetail: Template
) {

    @GET
    fun list(): TemplateInstance {
        val sales = saleService.findAll()
        return saleList
            .data("sales", sales)
            .data("activeMenu", "sales")
    }

    @GET
    @Path("/{id}")
    fun detail(@PathParam("id") id: Long): TemplateInstance {
        val sale = saleService.findById(id)
        return saleDetail
            .data("sale", sale)
            .data("activeMenu", "sales")
    }

    @GET
    @Path("/new")
    fun newSale(): TemplateInstance {
        val customers = customerService.findAll()
        val products = productService.findAll()

        // Criar mapa de estoque disponível
        val stockMap = products.associate { product ->
            product.id!! to stockService.calculateCurrentStock(product.id!!)
        }

        return saleForm
            .data("customers", customers)
            .data("products", products)
            .data("stockMap", stockMap)
            .data("activeMenu", "sales")
    }

    @POST
    @Path("/{id}/confirm")
    fun confirm(@PathParam("id") id: Long): Response {
        try {
            saleService.confirmSale(id)
            return Response.seeOther(URI.create("/sales/$id")).build()
        } catch (e: IllegalArgumentException) {
            return Response.seeOther(URI.create("/sales/$id")).build()
        }
    }

    @POST
    @Path("/{id}/deliver")
    fun deliver(@PathParam("id") id: Long): Response {
        try {
            saleService.deliverSale(id)
            return Response.seeOther(URI.create("/sales/$id")).build()
        } catch (e: IllegalArgumentException) {
            return Response.seeOther(URI.create("/sales/$id")).build()
        }
    }

    @POST
    @Path("/{id}/cancel")
    fun cancel(@PathParam("id") id: Long): Response {
        try {
            saleService.cancelSale(id)
            return Response.seeOther(URI.create("/sales/$id")).build()
        } catch (e: IllegalArgumentException) {
            return Response.seeOther(URI.create("/sales/$id")).build()
        }
    }
}
