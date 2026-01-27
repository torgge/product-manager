package com.example.product.application.service

import com.example.product.domain.model.Product
import com.example.product.domain.repository.ProductRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import java.time.LocalDateTime

@ApplicationScoped
class ProductService(
    private val productRepository: ProductRepository,
    private val stockService: StockService
) {

    fun findAll(): List<Product> {
        return productRepository.listAll()
    }

    fun findById(id: Long): Product? {
        return productRepository.findById(id)
    }

    fun findByName(name: String): List<Product> {
        return productRepository.findByName(name)
    }

    fun findByCategory(category: String): List<Product> {
        return productRepository.findByCategory(category)
    }

    fun findAvailableProducts(): List<Product> {
        return productRepository.listAll().filter { product ->
            product.id?.let { stockService.calculateCurrentStock(it) > 0 } ?: false
        }
    }

    @Transactional
    fun create(product: Product): Product {
        product.createdAt = LocalDateTime.now()
        product.updatedAt = LocalDateTime.now()
        productRepository.persist(product)
        return product
    }

    @Transactional
    fun update(id: Long, product: Product): Product? {
        val existingProduct = productRepository.findById(id) ?: return null

        existingProduct.name = product.name
        existingProduct.description = product.description
        existingProduct.price = product.price
        existingProduct.purchasePrice = product.purchasePrice
        existingProduct.profitMargin = product.profitMargin
        existingProduct.category = product.category
        existingProduct.updatedAt = LocalDateTime.now()

        return existingProduct
    }

    @Transactional
    fun delete(id: Long): Boolean {
        return productRepository.deleteById(id)
    }

    fun count(): Long {
        return productRepository.count()
    }
}
