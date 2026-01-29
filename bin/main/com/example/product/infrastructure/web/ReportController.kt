package com.example.product.infrastructure.web

import com.example.product.application.dto.ReportFilters
import com.example.product.application.service.CustomerService
import com.example.product.application.service.ProductService
import com.example.product.application.service.ReportService
import io.quarkus.qute.Location
import io.quarkus.qute.Template
import io.quarkus.qute.TemplateInstance
import io.smallrye.common.annotation.Blocking
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import java.time.LocalDate

@Path("/reports")
@Produces(MediaType.TEXT_HTML)
@Blocking
class ReportController(
    private val reportService: ReportService,
    private val productService: ProductService,
    private val customerService: CustomerService,
    @Location("reports/balanceReport") private val balanceReportTemplate: Template
) {

    @GET
    fun balanceReport(
        @QueryParam("productId") productIdParam: String?,
        @QueryParam("customerId") customerIdParam: String?,
        @QueryParam("startDate") startDate: String?,
        @QueryParam("endDate") endDate: String?
    ): TemplateInstance {
        val productId = productIdParam?.takeIf { it.isNotBlank() }?.toLongOrNull()
        val customerId = customerIdParam?.takeIf { it.isNotBlank() }?.toLongOrNull()

        val filters = ReportFilters(
            productId = productId,
            customerId = customerId,
            startDate = startDate?.takeIf { it.isNotBlank() }?.let { LocalDate.parse(it) },
            endDate = endDate?.takeIf { it.isNotBlank() }?.let { LocalDate.parse(it) }
        )

        val report = reportService.generateBalanceReport(filters)
        val products = productService.findAll()
        val customers = customerService.findAll()

        return balanceReportTemplate
            .data("report", report)
            .data("products", products)
            .data("customers", customers)
            .data("selectedProductId", productId)
            .data("selectedCustomerId", customerId)
            .data("startDate", startDate ?: "")
            .data("endDate", endDate ?: "")
            .data("activeMenu", "reports")
    }
}
