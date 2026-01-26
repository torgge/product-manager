package com.example.product.domain.repository

import com.example.product.domain.model.Product
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class ProductRepository : PanacheRepository<Product> {

    fun findByName(name: String): List<Product> {
        return list("name like ?1", "%$name%")
    }

    fun findByCategory(category: String): List<Product> {
        return list("category", category)
    }
}
