package com.example.product.domain.repository

import com.example.product.domain.model.*
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
class PurchaseOrderRepositoryTest {

    @Inject
    lateinit var purchaseOrderRepository: PurchaseOrderRepository

    @Inject
    lateinit var supplierRepository: SupplierRepository

    private lateinit var testSupplier1: Supplier
    private lateinit var testSupplier2: Supplier

    @BeforeEach
    @Transactional
    fun setup() {
        // Clean up existing data
        purchaseOrderRepository.deleteAll()
        supplierRepository.deleteAll()

        // Create test suppliers
        testSupplier1 = Supplier(
            name = "TechSupply Ltd",
            document = "33.333.333/0001-33",
            email = "techsupply@example.com"
        )
        supplierRepository.persist(testSupplier1)

        testSupplier2 = Supplier(
            name = "Global Wholesale Inc",
            document = "44.444.444/0001-44",
            email = "wholesale@example.com"
        )
        supplierRepository.persist(testSupplier2)

        // Create test purchase orders
        val purchase1 = PurchaseOrder(
            supplier = testSupplier1,
            orderDate = LocalDate.now().minusDays(10).atStartOfDay(),
            status = OrderStatus.PENDING,
            totalAmount = BigDecimal("500.00"),
            location = StockLocation.MAIN_WAREHOUSE
        )
        purchaseOrderRepository.persist(purchase1)

        val purchase2 = PurchaseOrder(
            supplier = testSupplier2,
            orderDate = LocalDate.now().minusDays(7).atStartOfDay(),
            status = OrderStatus.CONFIRMED,
            totalAmount = BigDecimal("750.00"),
            location = StockLocation.SECONDARY_WAREHOUSE
        )
        purchaseOrderRepository.persist(purchase2)

        val purchase3 = PurchaseOrder(
            supplier = testSupplier1,
            orderDate = LocalDate.now().minusDays(2).atStartOfDay(),
            status = OrderStatus.RECEIVED,
            totalAmount = BigDecimal("1000.00"),
            location = StockLocation.MAIN_WAREHOUSE
        )
        purchaseOrderRepository.persist(purchase3)
    }

    @AfterEach
    @Transactional
    fun cleanup() {
        purchaseOrderRepository.deleteAll()
        supplierRepository.deleteAll()
    }

    @Test
    @Transactional
    fun `search by supplier name should return matching orders`() {
        val results = purchaseOrderRepository.search(supplierName = "tech")

        assertEquals(2, results.size)
        assertTrue(results.all { it.supplier?.name?.contains("Tech", ignoreCase = true) == true })
    }

    @Test
    @Transactional
    fun `search by supplier name should be case insensitive`() {
        val results = purchaseOrderRepository.search(supplierName = "TECH")

        assertEquals(2, results.size)
    }

    @Test
    @Transactional
    fun `search by status should return only orders with that status`() {
        val results = purchaseOrderRepository.search(status = OrderStatus.CONFIRMED)

        assertEquals(1, results.size)
        assertEquals(OrderStatus.CONFIRMED, results[0].status)
    }

    @Test
    @Transactional
    fun `search by date range should return orders within range`() {
        val startDate = LocalDate.now().minusDays(8)
        val endDate = LocalDate.now().minusDays(6)

        val results = purchaseOrderRepository.search(startDate = startDate, endDate = endDate)

        assertEquals(1, results.size)
        assertTrue(results[0].orderDate.toLocalDate() in startDate..endDate)
    }

    @Test
    @Transactional
    fun `search by order ID should return exact match`() {
        val allOrders = purchaseOrderRepository.listAll()
        val targetId = allOrders.first().id!!

        val results = purchaseOrderRepository.search(orderId = targetId)

        assertEquals(1, results.size)
        assertEquals(targetId, results[0].id)
    }

    @Test
    @Transactional
    fun `search with multiple filters should combine with AND logic`() {
        val results = purchaseOrderRepository.search(
            supplierName = "tech",
            status = OrderStatus.RECEIVED
        )

        assertEquals(1, results.size)
        assertEquals(OrderStatus.RECEIVED, results[0].status)
        assertTrue(results[0].supplier?.name?.contains("Tech", ignoreCase = true) == true)
    }

    @Test
    @Transactional
    fun `search without filters should return all orders ordered by date desc`() {
        val results = purchaseOrderRepository.search()

        assertEquals(3, results.size)
        // Verify descending order
        assertTrue(results[0].orderDate >= results[1].orderDate)
        assertTrue(results[1].orderDate >= results[2].orderDate)
    }

    @Test
    @Transactional
    fun `search with non-matching criteria should return empty list`() {
        val results = purchaseOrderRepository.search(supplierName = "NonExistent")

        assertTrue(results.isEmpty())
    }
}
