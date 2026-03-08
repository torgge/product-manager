package com.example.product.domain.repository

import com.example.product.domain.model.OrderStatus
import com.example.product.domain.model.SaleOrder
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped
import java.time.LocalDate

@ApplicationScoped
class SaleOrderRepository : PanacheRepository<SaleOrder> {

    fun findByCustomer(customerId: Long): List<SaleOrder> {
        return list("customer.id = ?1 order by orderDate desc", customerId)
    }

    fun findByStatus(status: OrderStatus): List<SaleOrder> {
        return list("status = ?1 order by orderDate desc", status)
    }

    fun findRecent(limit: Int = 10): List<SaleOrder> {
        return find("order by orderDate desc").page(0, limit).list()
    }

    fun search(
        customerName: String? = null,
        status: OrderStatus? = null,
        startDate: LocalDate? = null,
        endDate: LocalDate? = null,
        orderId: Long? = null
    ): List<SaleOrder> {
        val conditions = mutableListOf<String>()
        val params = mutableMapOf<String, Any>()

        // Search by order ID
        if (orderId != null) {
            conditions.add("id = :orderId")
            params["orderId"] = orderId
        }

        // Search by customer name
        if (!customerName.isNullOrBlank()) {
            conditions.add("lower(customer.name) like :customerName")
            params["customerName"] = "%${customerName.lowercase()}%"
        }

        // Filter by status
        if (status != null) {
            conditions.add("status = :status")
            params["status"] = status
        }

        // Filter by date range
        if (startDate != null) {
            conditions.add("orderDate >= :startDate")
            params["startDate"] = startDate.atStartOfDay()
        }
        if (endDate != null) {
            conditions.add("orderDate <= :endDate")
            params["endDate"] = endDate.atTime(23, 59, 59)
        }

        val query = if (conditions.isEmpty()) {
            "order by orderDate desc"
        } else {
            conditions.joinToString(" and ") + " order by orderDate desc"
        }

        return if (params.isEmpty()) {
            list(query)
        } else {
            list(query, params)
        }
    }
}
