package com.example.product.domain.repository

import com.example.product.domain.model.OrderStatus
import com.example.product.domain.model.SaleOrder
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped

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
}
