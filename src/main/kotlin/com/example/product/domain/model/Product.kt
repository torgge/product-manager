package com.example.product.domain.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.PositiveOrZero
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
data class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @field:NotBlank(message = "Name is required")
    var name: String = "",

    var description: String? = null,

    @field:PositiveOrZero(message = "Price must be zero or positive")
    var price: BigDecimal = BigDecimal.ZERO,

    var purchasePrice: BigDecimal = BigDecimal.ZERO, // Preço de compra

    var profitMargin: BigDecimal = BigDecimal.ZERO, // Margem de lucro em percentual (ex: 30.00 = 30%)

    var category: String? = null,

    var createdAt: LocalDateTime = LocalDateTime.now(),

    var updatedAt: LocalDateTime = LocalDateTime.now()
)
