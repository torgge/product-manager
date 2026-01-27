package com.example.product.application.service

import com.example.product.domain.model.*
import com.example.product.domain.repository.CustomerRepository
import com.example.product.domain.repository.ProductRepository
import com.example.product.domain.repository.SaleOrderRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime

@ApplicationScoped
class SaleService(
    private val saleOrderRepository: SaleOrderRepository,
    private val customerRepository: CustomerRepository,
    private val productRepository: ProductRepository,
    private val stockService: StockService
) {

    fun findAll(): List<SaleOrder> {
        return saleOrderRepository.listAll()
    }

    fun findById(id: Long): SaleOrder? {
        return saleOrderRepository.findById(id)
    }

    fun findByCustomer(customerId: Long): List<SaleOrder> {
        return saleOrderRepository.findByCustomer(customerId)
    }

    fun findByStatus(status: OrderStatus): List<SaleOrder> {
        return saleOrderRepository.findByStatus(status)
    }

    fun findRecent(limit: Int = 10): List<SaleOrder> {
        return saleOrderRepository.findRecent(limit)
    }

    @Transactional
    fun create(
        customerId: Long,
        items: List<SaleOrderItemRequest>,
        notes: String?,
        createdBy: String = "system"
    ): SaleOrder {
        val customer = customerRepository.findById(customerId)
            ?: throw IllegalArgumentException("Customer not found: $customerId")

        val saleOrder = SaleOrder(
            customer = customer,
            notes = notes,
            createdBy = createdBy,
            status = OrderStatus.PENDING
        )

        var total = BigDecimal.ZERO

        for (itemRequest in items) {
            val product = productRepository.findById(itemRequest.productId)
                ?: throw IllegalArgumentException("Product not found: ${itemRequest.productId}")

            // Verificar estoque disponível
            val currentStock = product.id?.let { stockService.calculateCurrentStock(it) } ?: 0
            if (currentStock < itemRequest.quantity) {
                throw IllegalArgumentException(
                    "Insufficient stock for product ${product.name}. Available: $currentStock, Requested: ${itemRequest.quantity}"
                )
            }

            // Usar o preço do produto ou preço customizado
            val unitPrice = itemRequest.unitPrice ?: product.price
            val subtotal = unitPrice.multiply(BigDecimal(itemRequest.quantity))

            val item = SaleOrderItem(
                saleOrder = saleOrder,
                product = product,
                quantity = itemRequest.quantity,
                unitPrice = unitPrice,
                subtotal = subtotal
            )

            saleOrder.items.add(item)
            total = total.add(subtotal)
        }

        saleOrder.totalAmount = total
        saleOrderRepository.persist(saleOrder)

        return saleOrder
    }

    @Transactional
    fun confirmSale(id: Long): SaleOrder {
        val sale = saleOrderRepository.findById(id)
            ?: throw IllegalArgumentException("Sale order not found: $id")

        if (sale.status != OrderStatus.PENDING) {
            throw IllegalArgumentException("Sale order is not in pending status")
        }

        // Remover do estoque ao confirmar venda
        for (item in sale.items) {
            val product = item.product ?: continue

            product.id?.let { productId ->
                stockService.removeStock(
                    productId = productId,
                    quantity = item.quantity,
                    reason = "Venda #${sale.id} - Cliente: ${sale.customer?.name}",
                    createdBy = sale.createdBy
                )
            }
        }

        sale.status = OrderStatus.CONFIRMED
        sale.updatedAt = LocalDateTime.now()

        return sale
    }

    @Transactional
    fun deliverSale(id: Long): SaleOrder {
        val sale = saleOrderRepository.findById(id)
            ?: throw IllegalArgumentException("Sale order not found: $id")

        if (sale.status != OrderStatus.CONFIRMED) {
            throw IllegalArgumentException("Sale order must be confirmed before delivery")
        }

        sale.status = OrderStatus.RECEIVED // RECEIVED significa "entregue" para vendas
        sale.updatedAt = LocalDateTime.now()

        return sale
    }

    @Transactional
    fun cancelSale(id: Long): SaleOrder {
        val sale = saleOrderRepository.findById(id)
            ?: throw IllegalArgumentException("Sale order not found: $id")

        if (sale.status == OrderStatus.RECEIVED) {
            throw IllegalArgumentException("Cannot cancel a sale that has already been delivered")
        }

        // Se a venda foi confirmada, devolver ao estoque
        if (sale.status == OrderStatus.CONFIRMED) {
            for (item in sale.items) {
                val product = item.product ?: continue

                product.id?.let { productId ->
                    stockService.addStock(
                        productId = productId,
                        quantity = item.quantity,
                        reason = "Cancelamento da Venda #${sale.id}",
                        createdBy = sale.createdBy
                    )
                }
            }
        }

        sale.status = OrderStatus.CANCELLED
        sale.updatedAt = LocalDateTime.now()

        return sale
    }
}

data class SaleOrderItemRequest(
    val productId: Long,
    val quantity: Int,
    val unitPrice: BigDecimal? = null // Se null, usa o preço do produto
)
