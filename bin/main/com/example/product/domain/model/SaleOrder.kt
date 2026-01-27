package com.example.product.domain.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonManagedReference
import com.fasterxml.jackson.annotation.JsonBackReference
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
data class SaleOrder(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonIgnoreProperties("hibernateLazyInitializer", "handler")
    var customer: Customer? = null,

    @OneToMany(mappedBy = "saleOrder", cascade = [CascadeType.ALL], orphanRemoval = true)
    @JsonManagedReference
    var items: MutableList<SaleOrderItem> = mutableListOf(),

    var totalAmount: BigDecimal = BigDecimal.ZERO,

    var orderDate: LocalDateTime = LocalDateTime.now(),

    @Enumerated(EnumType.STRING)
    var status: OrderStatus = OrderStatus.PENDING,

    var notes: String? = null,

    var createdAt: LocalDateTime = LocalDateTime.now(),

    var updatedAt: LocalDateTime = LocalDateTime.now(),

    var createdBy: String = "system"
)

@Entity
@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
data class SaleOrderItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_order_id", nullable = false)
    @JsonBackReference
    var saleOrder: SaleOrder? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnoreProperties("hibernateLazyInitializer", "handler")
    var product: Product? = null,

    var quantity: Int = 0,

    var unitPrice: BigDecimal = BigDecimal.ZERO,

    var subtotal: BigDecimal = BigDecimal.ZERO
)
