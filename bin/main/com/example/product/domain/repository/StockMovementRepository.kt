package com.example.product.domain.repository

import com.example.product.domain.model.StockMovement
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class StockMovementRepository : PanacheRepository<StockMovement> {

    fun findByProductId(productId: Long): List<StockMovement> {
        return list("product.id = ?1 order by createdAt desc", productId)
    }

    fun findRecentMovements(limit: Int = 10): List<StockMovement> {
        return find("order by createdAt desc").page(0, limit).list()
    }
}
