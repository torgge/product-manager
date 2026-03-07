package com.example.product.domain.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "stock",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["product_id", "location"])
    ]
)
data class Stock(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    var product: Product? = null,

    var quantity: Int = 0,

    var minQuantity: Int = 0,

    var maxQuantity: Int? = null,

    @Enumerated(EnumType.STRING)
    var location: StockLocation = StockLocation.MAIN_WAREHOUSE,

    var lastUpdated: LocalDateTime = LocalDateTime.now(),

    var lastMovementId: Long? = null
) {
    fun isLowStock(): Boolean = quantity <= minQuantity && quantity > 0

    fun isHighStock(): Boolean = maxQuantity?.let { quantity >= it } ?: false

    fun isOutOfStock(): Boolean = quantity == 0
}

enum class StockLocation {
    MAIN_WAREHOUSE,
    SECONDARY_WAREHOUSE,
    RETAIL_STORE,
    DISTRIBUTION_CENTER
}
