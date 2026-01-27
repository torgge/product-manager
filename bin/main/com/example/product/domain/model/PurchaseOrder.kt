package com.example.product.domain.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonManagedReference
import com.fasterxml.jackson.annotation.JsonBackReference
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
data class PurchaseOrder(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    @JsonIgnoreProperties("hibernateLazyInitializer", "handler")
    var supplier: Supplier? = null,

    @OneToMany(mappedBy = "purchaseOrder", cascade = [CascadeType.ALL], orphanRemoval = true)
    @JsonManagedReference
    var items: MutableList<PurchaseOrderItem> = mutableListOf(),

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
data class PurchaseOrderItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_order_id", nullable = false)
    @JsonBackReference
    var purchaseOrder: PurchaseOrder? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnoreProperties("hibernateLazyInitializer", "handler")
    var product: Product? = null,

    var quantity: Int = 0,

    var unitPrice: BigDecimal = BigDecimal.ZERO,

    var subtotal: BigDecimal = BigDecimal.ZERO
)

enum class OrderStatus {
    PENDING,    // Pendente
    CONFIRMED,  // Confirmada
    RECEIVED,   // Recebida (para compras) / Entregue (para vendas)
    CANCELLED   // Cancelada
}
