package com.example.product.application.service

import com.example.product.domain.model.MovementType
import com.example.product.domain.model.Stock
import com.example.product.domain.model.StockLocation
import com.example.product.domain.model.StockMovement
import com.example.product.domain.repository.ProductRepository
import com.example.product.domain.repository.StockMovementRepository
import com.example.product.domain.repository.StockRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import java.time.LocalDateTime

@ApplicationScoped
class StockService(
    private val stockMovementRepository: StockMovementRepository,
    private val stockRepository: StockRepository,
    private val productRepository: ProductRepository
) {

    fun findByProductId(productId: Long): List<StockMovement> {
        return stockMovementRepository.findByProductId(productId)
    }

    fun findRecentMovements(limit: Int = 10): List<StockMovement> {
        return stockMovementRepository.findRecentMovements(limit)
    }

    fun getCurrentStock(productId: Long, location: StockLocation = StockLocation.MAIN_WAREHOUSE): Stock? {
        return stockRepository.findByProductIdAndLocation(productId, location)
    }

    fun getAllStocks(): List<Stock> {
        return stockRepository.listAll()
    }

    fun searchStocks(query: String): List<Stock> {
        return if (query.isBlank()) {
            stockRepository.listAll()
        } else {
            stockRepository.searchByProductName(query)
        }
    }

    fun getLowStockAlerts(): List<Stock> {
        return stockRepository.findLowStock()
    }

    fun getOutOfStockItems(): List<Stock> {
        return stockRepository.findOutOfStock()
    }

    @Transactional
    fun addStock(productId: Long, quantity: Int, reason: String?, createdBy: String = "system", location: StockLocation = StockLocation.MAIN_WAREHOUSE): StockMovement {
        val product = productRepository.findById(productId)
            ?: throw IllegalArgumentException("Product not found: $productId")

        if (quantity <= 0) {
            throw IllegalArgumentException("Quantity must be positive")
        }

        // Get or create Stock entity
        var stock = stockRepository.findByProductIdAndLocation(productId, location)
        if (stock == null) {
            stock = Stock(
                product = product,
                location = location,
                quantity = 0
            )
            stockRepository.persist(stock)
        }

        val newBalance = stock.quantity + quantity

        // Create movement
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

        // Update stock entity
        stock.quantity = newBalance
        stock.lastUpdated = LocalDateTime.now()
        stock.lastMovementId = movement.id

        product.updatedAt = LocalDateTime.now()

        return movement
    }

    @Transactional
    fun removeStock(productId: Long, quantity: Int, reason: String?, createdBy: String = "system", location: StockLocation = StockLocation.MAIN_WAREHOUSE): StockMovement {
        val product = productRepository.findById(productId)
            ?: throw IllegalArgumentException("Product not found: $productId")

        if (quantity <= 0) {
            throw IllegalArgumentException("Quantity must be positive")
        }

        val stock = stockRepository.findByProductIdAndLocation(productId, location)
            ?: throw IllegalArgumentException("No stock found for product at location: $location")

        if (stock.quantity < quantity) {
            throw IllegalArgumentException(
                "Insufficient stock. Available: ${stock.quantity}, Requested: $quantity"
            )
        }

        val newBalance = stock.quantity - quantity

        // Create movement
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

        // Update stock entity
        stock.quantity = newBalance
        stock.lastUpdated = LocalDateTime.now()
        stock.lastMovementId = movement.id

        product.updatedAt = LocalDateTime.now()

        return movement
    }

    @Transactional
    fun adjustStock(productId: Long, newQuantity: Int, reason: String?, createdBy: String = "system", location: StockLocation = StockLocation.MAIN_WAREHOUSE): StockMovement {
        val product = productRepository.findById(productId)
            ?: throw IllegalArgumentException("Product not found: $productId")

        if (newQuantity < 0) {
            throw IllegalArgumentException("Stock quantity cannot be negative")
        }

        var stock = stockRepository.findByProductIdAndLocation(productId, location)
        if (stock == null) {
            stock = Stock(
                product = product,
                location = location,
                quantity = 0
            )
            stockRepository.persist(stock)
        }

        val currentStock = stock.quantity
        val difference = newQuantity - currentStock

        // Create movement
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

        // Update stock entity
        stock.quantity = newQuantity
        stock.lastUpdated = LocalDateTime.now()
        stock.lastMovementId = movement.id

        product.updatedAt = LocalDateTime.now()

        return movement
    }

    @Transactional
    fun updateStockThresholds(
        productId: Long,
        minQuantity: Int,
        maxQuantity: Int?,
        location: StockLocation = StockLocation.MAIN_WAREHOUSE
    ): Stock {
        val product = productRepository.findById(productId)
            ?: throw IllegalArgumentException("Product not found: $productId")

        var stock = stockRepository.findByProductIdAndLocation(productId, location)
        if (stock == null) {
            stock = Stock(
                product = product,
                location = location,
                quantity = 0,
                minQuantity = minQuantity,
                maxQuantity = maxQuantity
            )
            stockRepository.persist(stock)
        } else {
            stock.minQuantity = minQuantity
            stock.maxQuantity = maxQuantity
            stock.lastUpdated = LocalDateTime.now()
        }

        return stock
    }

    @Transactional
    fun migrateFromMovements() {
        val products = productRepository.listAll()

        for (product in products) {
            product.id?.let { productId ->
                val movements = stockMovementRepository.findByProductId(productId)

                if (movements.isNotEmpty()) {
                    val latestMovement = movements.first()
                    val currentQuantity = latestMovement.balanceAfter

                    var stock = stockRepository.findByProductId(productId)
                    if (stock == null) {
                        stock = Stock(
                            product = product,
                            location = StockLocation.MAIN_WAREHOUSE,
                            quantity = currentQuantity,
                            lastMovementId = latestMovement.id,
                            lastUpdated = latestMovement.createdAt
                        )
                        stockRepository.persist(stock)
                    }
                }
            }
        }
    }

    // Legacy method for backward compatibility
    fun calculateCurrentStock(productId: Long): Int {
        return getCurrentStock(productId)?.quantity ?: 0
    }
}
