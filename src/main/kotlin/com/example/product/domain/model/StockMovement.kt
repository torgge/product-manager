package com.example.product.domain.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
data class StockMovement(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    var product: Product? = null,

    @Enumerated(EnumType.STRING)
    var type: MovementType = MovementType.IN,

    var quantity: Int = 0,

    var balanceAfter: Int = 0,

    var reason: String? = null,

    var createdAt: LocalDateTime = LocalDateTime.now(),

    var createdBy: String = "system"
)

enum class MovementType {
    IN,     // Entrada de estoque
    OUT,    // Saída de estoque
    ADJUSTMENT  // Ajuste de estoque
}
