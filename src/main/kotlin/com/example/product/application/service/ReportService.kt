package com.example.product.application.service

import com.example.product.application.dto.*
import com.example.product.domain.model.OrderStatus
import com.example.product.domain.repository.SaleOrderRepository
import jakarta.enterprise.context.ApplicationScoped
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime
import java.time.LocalTime

@ApplicationScoped
class ReportService(
    private val saleOrderRepository: SaleOrderRepository
) {

    fun generateBalanceReport(filters: ReportFilters): BalanceReport {
        val startDateTime = filters.startDate?.atStartOfDay() ?: LocalDateTime.of(2000, 1, 1, 0, 0)
        val endDateTime = filters.endDate?.atTime(LocalTime.MAX) ?: LocalDateTime.of(2100, 12, 31, 23, 59)

        val completedStatuses = listOf(OrderStatus.CONFIRMED, OrderStatus.RECEIVED)

        val allSales = saleOrderRepository.list(
            "status in ?1 and orderDate >= ?2 and orderDate <= ?3 order by orderDate desc",
            completedStatuses,
            startDateTime,
            endDateTime
        )

        val filteredSales = allSales.filter { sale ->
            filters.customerId == null || sale.customer?.id == filters.customerId
        }

        val allItems = filteredSales.flatMap { it.items }
            .filter { item ->
                filters.productId == null || item.product?.id == filters.productId
            }

        val productReports = allItems
            .groupBy { it.product?.id }
            .mapNotNull { (productId, productItems) ->
                productId?.let { id ->
                    val product = productItems.first().product!!
                    val totalQty = productItems.sumOf { it.quantity }
                    val totalRevenue = productItems.fold(BigDecimal.ZERO) { acc, item ->
                        acc.add(item.subtotal)
                    }
                    val totalCost = product.purchasePrice.multiply(BigDecimal(totalQty))
                    val totalProfit = totalRevenue.subtract(totalCost)

                    val margin = if (totalRevenue > BigDecimal.ZERO) {
                        totalProfit.divide(totalRevenue, 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal(100))
                            .setScale(2, RoundingMode.HALF_UP)
                    } else {
                        BigDecimal.ZERO
                    }

                    val avgPrice = if (totalQty > 0) {
                        totalRevenue.divide(BigDecimal(totalQty), 2, RoundingMode.HALF_UP)
                    } else {
                        BigDecimal.ZERO
                    }

                    ProductSalesReport(
                        productId = id,
                        productName = product.name,
                        category = product.category,
                        purchasePrice = product.purchasePrice,
                        averageSalePrice = avgPrice,
                        totalQuantitySold = totalQty,
                        totalRevenue = totalRevenue.setScale(2, RoundingMode.HALF_UP),
                        totalCost = totalCost.setScale(2, RoundingMode.HALF_UP),
                        totalProfit = totalProfit.setScale(2, RoundingMode.HALF_UP),
                        profitMarginPercent = margin
                    )
                }
            }
            .sortedByDescending { it.totalRevenue }

        val summaryRevenue = productReports.fold(BigDecimal.ZERO) { acc, r -> acc.add(r.totalRevenue) }
        val summaryCost = productReports.fold(BigDecimal.ZERO) { acc, r -> acc.add(r.totalCost) }
        val summaryProfit = productReports.fold(BigDecimal.ZERO) { acc, r -> acc.add(r.totalProfit) }

        val overallMargin = if (summaryRevenue > BigDecimal.ZERO) {
            summaryProfit.divide(summaryRevenue, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal(100))
                .setScale(2, RoundingMode.HALF_UP)
        } else {
            BigDecimal.ZERO
        }

        val summary = BalanceReportSummary(
            totalRevenue = summaryRevenue.setScale(2, RoundingMode.HALF_UP),
            totalCost = summaryCost.setScale(2, RoundingMode.HALF_UP),
            totalProfit = summaryProfit.setScale(2, RoundingMode.HALF_UP),
            overallMarginPercent = overallMargin,
            totalItemsSold = productReports.sumOf { it.totalQuantitySold },
            totalOrders = filteredSales.size
        )

        return BalanceReport(
            products = productReports,
            summary = summary,
            filters = filters
        )
    }
}
