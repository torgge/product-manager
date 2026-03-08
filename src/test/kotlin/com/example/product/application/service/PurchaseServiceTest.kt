package com.example.product.application.service

import com.example.product.domain.model.*
import com.example.product.domain.repository.PurchaseOrderRepository
import com.example.product.domain.repository.SupplierRepository
import io.quarkus.test.junit.QuarkusTest
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.math.BigDecimal
import java.time.LocalDate

@QuarkusTest
class PurchaseServiceTest {

    @Inject
    lateinit var purchaseService: PurchaseService

    @Inject
    lateinit var purchaseOrderRepository: PurchaseOrderRepository

    @Inject
    lateinit var supplierRepository: SupplierRepository

    private lateinit var testSupplier: Supplier

    @BeforeEach
    @Transactional
    fun setup() {
        purchaseOrderRepository.deleteAll()
        supplierRepository.deleteAll()

        testSupplier = Supplier(
            name = "Test Supplier",
            document = "12.345.678/0001-90",
            email = "supplier@example.com"
        )
        supplierRepository.persist(testSupplier)
    }

    @AfterEach
    @Transactional
    fun cleanup() {
        purchaseOrderRepository.deleteAll()
        supplierRepository.deleteAll()
    }

    @Test
    @Transactional
    fun `findAll should return all purchase orders`() {
        val purchase1 = PurchaseOrder(
            supplier = testSupplier,
            status = OrderStatus.PENDING,
            totalAmount = BigDecimal("500.00")
        )
        purchaseOrderRepository.persist(purchase1)

        val purchase2 = PurchaseOrder(
            supplier = testSupplier,
            status = OrderStatus.CONFIRMED,
            totalAmount = BigDecimal("750.00")
        )
        purchaseOrderRepository.persist(purchase2)

        val results = purchaseService.findAll()

        assertEquals(2, results.size)
    }

    @Test
    @Transactional
    fun `findById should return purchase order when exists`() {
        val purchase = PurchaseOrder(
            supplier = testSupplier,
            status = OrderStatus.PENDING,
            totalAmount = BigDecimal("500.00")
        )
        purchaseOrderRepository.persist(purchase)

        val result = purchaseService.findById(purchase.id!!)

        assertNotNull(result)
        assertEquals(purchase.id, result?.id)
    }

    @Test
    @Transactional
    fun `findByStatus should return only orders with given status`() {
        val purchase1 = PurchaseOrder(
            supplier = testSupplier,
            status = OrderStatus.PENDING,
            totalAmount = BigDecimal("500.00")
        )
        purchaseOrderRepository.persist(purchase1)

        val purchase2 = PurchaseOrder(
            supplier = testSupplier,
            status = OrderStatus.CONFIRMED,
            totalAmount = BigDecimal("750.00")
        )
        purchaseOrderRepository.persist(purchase2)

        val results = purchaseService.findByStatus(OrderStatus.CONFIRMED)

        assertEquals(1, results.size)
        assertEquals(OrderStatus.CONFIRMED, results[0].status)
    }

    @Test
    @Transactional
    fun `search should delegate to repository with correct parameters`() {
        val purchase = PurchaseOrder(
            supplier = testSupplier,
            orderDate = LocalDate.now().minusDays(5).atStartOfDay(),
            status = OrderStatus.PENDING,
            totalAmount = BigDecimal("500.00")
        )
        purchaseOrderRepository.persist(purchase)

        val results = purchaseService.search(
            supplierName = "Test",
            status = OrderStatus.PENDING
        )

        assertEquals(1, results.size)
        assertEquals(testSupplier.id, results[0].supplier?.id)
    }

    @Test
    @Transactional
    fun `search with date range should return filtered results`() {
        val purchase1 = PurchaseOrder(
            supplier = testSupplier,
            orderDate = LocalDate.now().minusDays(15).atStartOfDay(),
            status = OrderStatus.RECEIVED,
            totalAmount = BigDecimal("500.00")
        )
        purchaseOrderRepository.persist(purchase1)

        val purchase2 = PurchaseOrder(
            supplier = testSupplier,
            orderDate = LocalDate.now().minusDays(3).atStartOfDay(),
            status = OrderStatus.PENDING,
            totalAmount = BigDecimal("750.00")
        )
        purchaseOrderRepository.persist(purchase2)

        val results = purchaseService.search(
            startDate = LocalDate.now().minusDays(7),
            endDate = LocalDate.now()
        )

        assertEquals(1, results.size)
        assertTrue(results[0].orderDate.toLocalDate().isAfter(LocalDate.now().minusDays(8)))
    }

    @Test
    @Transactional
    fun `search without parameters should return all orders`() {
        val purchase1 = PurchaseOrder(
            supplier = testSupplier,
            status = OrderStatus.PENDING,
            totalAmount = BigDecimal("500.00")
        )
        purchaseOrderRepository.persist(purchase1)

        val purchase2 = PurchaseOrder(
            supplier = testSupplier,
            status = OrderStatus.CONFIRMED,
            totalAmount = BigDecimal("750.00")
        )
        purchaseOrderRepository.persist(purchase2)

        val results = purchaseService.search()

        assertEquals(2, results.size)
    }

    @Test
    @Transactional
    fun `findRecent should limit results`() {
        // Create 5 orders
        repeat(5) { i ->
            val purchase = PurchaseOrder(
                supplier = testSupplier,
                orderDate = LocalDate.now().minusDays(i.toLong()).atStartOfDay(),
                status = OrderStatus.PENDING,
                totalAmount = BigDecimal("500.00")
            )
            purchaseOrderRepository.persist(purchase)
        }

        val results = purchaseService.findRecent(3)

        assertEquals(3, results.size)
        // Should be ordered by date descending (newest first)
        assertTrue(results[0].orderDate >= results[1].orderDate)
        assertTrue(results[1].orderDate >= results[2].orderDate)
    }
}
