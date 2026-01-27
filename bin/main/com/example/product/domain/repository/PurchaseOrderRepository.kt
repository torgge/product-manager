package com.example.product.domain.repository

import com.example.product.domain.model.OrderStatus
import com.example.product.domain.model.PurchaseOrder
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped

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
}
