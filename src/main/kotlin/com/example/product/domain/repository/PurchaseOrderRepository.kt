package com.example.product.domain.repository

import com.example.product.domain.model.OrderStatus
import com.example.product.domain.model.PurchaseOrder
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped
import java.time.LocalDate

@ApplicationScoped
class PurchaseOrderRepository : PanacheRepository<PurchaseOrder> {

    fun findBySupplier(supplierId: Long): List<PurchaseOrder> {
        return list("supplier.id = ?1 order by orderDate desc", supplierId)
    }

    fun findByStatus(status: OrderStatus): List<PurchaseOrder> {
        return list("status = ?1 order by orderDate desc", status)
    }

    fun findRecent(limit: Int = 10): List<PurchaseOrder> {
        return find("order by orderDate desc").page(0, limit).list()
    }

    fun search(
        supplierName: String? = null,
        status: OrderStatus? = null,
        startDate: LocalDate? = null,
        endDate: LocalDate? = null,
        orderId: Long? = null
    ): List<PurchaseOrder> {
        val conditions = mutableListOf<String>()
        val params = mutableMapOf<String, Any>()

        // Search by order ID
        if (orderId != null) {
            conditions.add("id = :orderId")
            params["orderId"] = orderId
        }

        // Search by supplier name
        if (!supplierName.isNullOrBlank()) {
            conditions.add("lower(supplier.name) like :supplierName")
            params["supplierName"] = "%${supplierName.lowercase()}%"
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
