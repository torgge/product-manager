package com.example.product.application.dto

import java.math.BigDecimal
import java.time.LocalDate

data class ProductSalesReport(
    val productId: Long,
    val productName: String,
    val category: String?,
    val purchasePrice: BigDecimal,
    val averageSalePrice: BigDecimal,
    val totalQuantitySold: Int,
    val totalRevenue: BigDecimal,
    val totalCost: BigDecimal,
    val totalProfit: BigDecimal,
    val profitMarginPercent: BigDecimal
)

data class BalanceReportSummary(
    val totalRevenue: BigDecimal,
    val totalCost: BigDecimal,
    val totalProfit: BigDecimal,
    val overallMarginPercent: BigDecimal,
    val totalItemsSold: Int,
    val totalOrders: Int
)

data class ReportFilters(
    val productId: Long? = null,
    val customerId: Long? = null,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null
)

data class BalanceReport(
    val products: List<ProductSalesReport>,
    val summary: BalanceReportSummary,
    val filters: ReportFilters
)
