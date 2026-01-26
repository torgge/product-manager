package com.example.product.application.service

import com.example.product.domain.model.*
import com.example.product.domain.repository.ProductRepository
import com.example.product.domain.repository.PurchaseOrderRepository
import com.example.product.domain.repository.SupplierRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime

@ApplicationScoped
class PurchaseService(
    private val purchaseOrderRepository: PurchaseOrderRepository,
    private val supplierRepository: SupplierRepository,
    private val productRepository: ProductRepository,
    private val stockService: StockService
) {

    fun findAll(): List<PurchaseOrder> {
        return purchaseOrderRepository.listAll()
    }

    fun findById(id: Long): PurchaseOrder? {
        return purchaseOrderRepository.findById(id)
    }

    fun findBySupplier(supplierId: Long): List<PurchaseOrder> {
        return purchaseOrderRepository.findBySupplier(supplierId)
    }

    fun findByStatus(status: OrderStatus): List<PurchaseOrder> {
        return purchaseOrderRepository.findByStatus(status)
    }

    fun findRecent(limit: Int = 10): List<PurchaseOrder> {
        return purchaseOrderRepository.findRecent(limit)
    }

    @Transactional
    fun create(
        supplierId: Long,
        items: List<PurchaseOrderItemRequest>,
        notes: String?,
        createdBy: String = "system"
    ): PurchaseOrder {
        val supplier = supplierRepository.findById(supplierId)
            ?: throw IllegalArgumentException("Supplier not found: $supplierId")

        val purchaseOrder = PurchaseOrder(
            supplier = supplier,
            notes = notes,
            createdBy = createdBy,
            status = OrderStatus.PENDING
        )

        var total = BigDecimal.ZERO

        for (itemRequest in items) {
            val product = productRepository.findById(itemRequest.productId)
                ?: throw IllegalArgumentException("Product not found: ${itemRequest.productId}")

            val subtotal = itemRequest.unitPrice.multiply(BigDecimal(itemRequest.quantity))

            val item = PurchaseOrderItem(
                purchaseOrder = purchaseOrder,
                product = product,
                quantity = itemRequest.quantity,
                unitPrice = itemRequest.unitPrice,
                subtotal = subtotal
            )

            purchaseOrder.items.add(item)
            total = total.add(subtotal)
        }

        purchaseOrder.totalAmount = total
        purchaseOrderRepository.persist(purchaseOrder)

        return purchaseOrder
    }

    @Transactional
    fun confirmPurchase(id: Long): PurchaseOrder {
        val purchase = purchaseOrderRepository.findById(id)
            ?: throw IllegalArgumentException("Purchase order not found: $id")

        if (purchase.status != OrderStatus.PENDING) {
            throw IllegalArgumentException("Purchase order is not in pending status")
        }

        purchase.status = OrderStatus.CONFIRMED
        purchase.updatedAt = LocalDateTime.now()

        return purchase
    }

    @Transactional
    fun receivePurchase(id: Long): PurchaseOrder {
        val purchase = purchaseOrderRepository.findById(id)
            ?: throw IllegalArgumentException("Purchase order not found: $id")

        if (purchase.status != OrderStatus.CONFIRMED) {
            throw IllegalArgumentException("Purchase order must be confirmed before receiving")
        }

        // Atualizar estoque e preço de compra dos produtos
        for (item in purchase.items) {
            val product = item.product ?: continue

            // Atualizar preço de compra
            product.purchasePrice = item.unitPrice

            // Calcular preço de venda baseado na margem de lucro
            if (product.profitMargin > BigDecimal.ZERO) {
                val margin = product.profitMargin.divide(BigDecimal(100))
                product.price = item.unitPrice.multiply(BigDecimal.ONE.add(margin))
                    .setScale(2, RoundingMode.HALF_UP)
            }

            product.updatedAt = LocalDateTime.now()

            // Adicionar ao estoque
            product.id?.let { productId ->
                stockService.addStock(
                    productId = productId,
                    quantity = item.quantity,
                    reason = "Compra #${purchase.id} - Fornecedor: ${purchase.supplier?.name}",
                    createdBy = purchase.createdBy
                )
            }
        }

        purchase.status = OrderStatus.RECEIVED
        purchase.updatedAt = LocalDateTime.now()

        return purchase
    }

    @Transactional
    fun cancelPurchase(id: Long): PurchaseOrder {
        val purchase = purchaseOrderRepository.findById(id)
            ?: throw IllegalArgumentException("Purchase order not found: $id")

        if (purchase.status == OrderStatus.RECEIVED) {
            throw IllegalArgumentException("Cannot cancel a purchase that has already been received")
        }

        purchase.status = OrderStatus.CANCELLED
        purchase.updatedAt = LocalDateTime.now()

        return purchase
    }
}

data class PurchaseOrderItemRequest(
    val productId: Long,
    val quantity: Int,
    val unitPrice: BigDecimal
)
