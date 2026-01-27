package com.example.product.application.service

import com.example.product.domain.model.MovementType
import com.example.product.domain.model.StockMovement
import com.example.product.domain.repository.ProductRepository
import com.example.product.domain.repository.StockMovementRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import java.time.LocalDateTime

@ApplicationScoped
class StockService(
    private val stockMovementRepository: StockMovementRepository,
    private val productRepository: ProductRepository
) {

    fun findByProductId(productId: Long): List<StockMovement> {
        return stockMovementRepository.findByProductId(productId)
    }

    fun findRecentMovements(limit: Int = 10): List<StockMovement> {
        return stockMovementRepository.findRecentMovements(limit)
    }

    @Transactional
    fun addStock(productId: Long, quantity: Int, reason: String?, createdBy: String = "system"): StockMovement {
        val product = productRepository.findById(productId)
            ?: throw IllegalArgumentException("Product not found: $productId")

        if (quantity <= 0) {
            throw IllegalArgumentException("Quantity must be positive")
        }

        val currentStock = calculateCurrentStock(productId)
        val newBalance = currentStock + quantity

        product.updatedAt = LocalDateTime.now()

        // Registra a movimentação
        val movement = StockMovement(
            product = product,
            type = MovementType.IN,
            quantity = quantity,
            balanceAfter = newBalance,
            reason = reason,
            createdAt = LocalDateTime.now(),
            createdBy = createdBy
        )

        stockMovementRepository.persist(movement)
        return movement
    }

    @Transactional
    fun removeStock(productId: Long, quantity: Int, reason: String?, createdBy: String = "system"): StockMovement {
        val product = productRepository.findById(productId)
            ?: throw IllegalArgumentException("Product not found: $productId")

        if (quantity <= 0) {
            throw IllegalArgumentException("Quantity must be positive")
        }

        val currentStock = calculateCurrentStock(productId)
        if (currentStock < quantity) {
            throw IllegalArgumentException("Insufficient stock. Available: $currentStock, Requested: $quantity")
        }

        val newBalance = currentStock - quantity

        product.updatedAt = LocalDateTime.now()

        // Registra a movimentação
        val movement = StockMovement(
            product = product,
            type = MovementType.OUT,
            quantity = quantity,
            balanceAfter = newBalance,
            reason = reason,
            createdAt = LocalDateTime.now(),
            createdBy = createdBy
        )

        stockMovementRepository.persist(movement)
        return movement
    }

    @Transactional
    fun adjustStock(productId: Long, newQuantity: Int, reason: String?, createdBy: String = "system"): StockMovement {
        val product = productRepository.findById(productId)
            ?: throw IllegalArgumentException("Product not found: $productId")

        if (newQuantity < 0) {
            throw IllegalArgumentException("Stock quantity cannot be negative")
        }

        val currentStock = calculateCurrentStock(productId)
        val difference = newQuantity - currentStock

        product.updatedAt = LocalDateTime.now()

        // Registra a movimentação
        val movement = StockMovement(
            product = product,
            type = MovementType.ADJUSTMENT,
            quantity = kotlin.math.abs(difference),
            balanceAfter = newQuantity,
            reason = reason ?: "Ajuste de estoque de $currentStock para $newQuantity",
            createdAt = LocalDateTime.now(),
            createdBy = createdBy
        )

        stockMovementRepository.persist(movement)
        return movement
    }

    fun calculateCurrentStock(productId: Long): Int {
        val movements = stockMovementRepository.findByProductId(productId)

        // Se não há movimentações, o estoque é zero
        if (movements.isEmpty()) {
            return 0
        }

        // Retorna o balanceAfter da última movimentação
        return movements.first().balanceAfter
    }
}
