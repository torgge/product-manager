package com.example.product.infrastructure.web

import com.example.product.application.service.ProductService
import com.example.product.application.service.PurchaseOrderItemRequest
import com.example.product.application.service.PurchaseService
import com.example.product.application.service.SupplierService
import io.quarkus.qute.Location
import io.quarkus.qute.Template
import io.quarkus.qute.TemplateInstance
import io.smallrye.common.annotation.Blocking
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import java.net.URI

@Path("/purchases")
@Produces(MediaType.TEXT_HTML)
@Blocking
class PurchaseController(
    private val purchaseService: PurchaseService,
    private val supplierService: SupplierService,
    private val productService: ProductService,
    @Location("purchases/purchaseList") private val purchaseList: Template,
    @Location("purchases/purchaseForm") private val purchaseForm: Template,
    @Location("purchases/purchaseDetail") private val purchaseDetail: Template
) {

    @GET
    fun list(): TemplateInstance {
        val purchases = purchaseService.findAll()
        return purchaseList
            .data("purchases", purchases)
            .data("activeMenu", "purchases")
    }

    @GET
    @Path("/{id}")
    fun detail(@PathParam("id") id: Long): TemplateInstance {
        val purchase = purchaseService.findById(id)
        return purchaseDetail
            .data("purchase", purchase)
            .data("activeMenu", "purchases")
    }

    @GET
    @Path("/new")
    fun newPurchase(): TemplateInstance {
        val suppliers = supplierService.findAll()
        val products = productService.findAll()
        return purchaseForm
            .data("suppliers", suppliers)
            .data("products", products)
            .data("activeMenu", "purchases")
    }

    @POST
    @Path("/{id}/confirm")
    fun confirm(@PathParam("id") id: Long): Response {
        try {
            purchaseService.confirmPurchase(id)
            return Response.seeOther(URI.create("/purchases/$id")).build()
        } catch (e: IllegalArgumentException) {
            return Response.seeOther(URI.create("/purchases/$id")).build()
        }
    }

    @POST
    @Path("/{id}/receive")
    fun receive(@PathParam("id") id: Long): Response {
        try {
            purchaseService.receivePurchase(id)
            return Response.seeOther(URI.create("/purchases/$id")).build()
        } catch (e: IllegalArgumentException) {
            return Response.seeOther(URI.create("/purchases/$id")).build()
        }
    }

    @POST
    @Path("/{id}/cancel")
    fun cancel(@PathParam("id") id: Long): Response {
        try {
            purchaseService.cancelPurchase(id)
            return Response.seeOther(URI.create("/purchases/$id")).build()
        } catch (e: IllegalArgumentException) {
            return Response.seeOther(URI.create("/purchases/$id")).build()
        }
    }
}
