package com.example.product.domain.repository

import com.example.product.domain.model.Stock
import com.example.product.domain.model.StockLocation
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class StockRepository : PanacheRepository<Stock> {

    fun findByProductId(productId: Long): Stock? {
        return find("product.id", productId).firstResult()
    }

    fun findByLocation(location: StockLocation): List<Stock> {
        return list("location", location)
    }

    fun findLowStock(): List<Stock> {
        return list("quantity <= minQuantity and quantity > 0")
    }

    fun findOutOfStock(): List<Stock> {
        return list("quantity", 0)
    }

    fun findByProductIdAndLocation(productId: Long, location: StockLocation): Stock? {
        return find("product.id = ?1 and location = ?2", productId, location).firstResult()
    }

    fun searchByProductName(search: String): List<Stock> {
        return list("LOWER(product.name) LIKE LOWER(?1)", "%$search%")
    }
}
