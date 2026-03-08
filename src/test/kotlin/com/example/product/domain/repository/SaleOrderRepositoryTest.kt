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
class SaleOrderRepositoryTest {

    @Inject
    lateinit var saleOrderRepository: SaleOrderRepository

    @Inject
    lateinit var customerRepository: CustomerRepository

    @Inject
    lateinit var productRepository: ProductRepository

    private lateinit var testCustomer1: Customer
    private lateinit var testCustomer2: Customer

    @BeforeEach
    @Transactional
    fun setup() {
        // Clean up existing data
        saleOrderRepository.deleteAll()
        customerRepository.deleteAll()

        // Create test customers
        testCustomer1 = Customer(
            name = "TechCorp Solutions",
            document = "11.111.111/0001-11",
            email = "tech@example.com"
        )
        customerRepository.persist(testCustomer1)

        testCustomer2 = Customer(
            name = "Global Retail Inc",
            document = "22.222.222/0001-22",
            email = "global@example.com"
        )
        customerRepository.persist(testCustomer2)

        // Create test sale orders
        val sale1 = SaleOrder(
            customer = testCustomer1,
            orderDate = LocalDate.now().minusDays(5).atStartOfDay(),
            status = OrderStatus.PENDING,
            totalAmount = BigDecimal("100.00"),
            location = StockLocation.MAIN_WAREHOUSE
        )
        saleOrderRepository.persist(sale1)

        val sale2 = SaleOrder(
            customer = testCustomer2,
            orderDate = LocalDate.now().minusDays(3).atStartOfDay(),
            status = OrderStatus.CONFIRMED,
            totalAmount = BigDecimal("200.00"),
            location = StockLocation.MAIN_WAREHOUSE
        )
        saleOrderRepository.persist(sale2)

        val sale3 = SaleOrder(
            customer = testCustomer1,
            orderDate = LocalDate.now().minusDays(1).atStartOfDay(),
            status = OrderStatus.RECEIVED,
            totalAmount = BigDecimal("300.00"),
            location = StockLocation.RETAIL_STORE
        )
        saleOrderRepository.persist(sale3)
    }

    @AfterEach
    @Transactional
    fun cleanup() {
        saleOrderRepository.deleteAll()
        customerRepository.deleteAll()
    }

    @Test
    @Transactional
    fun `search by customer name should return matching orders`() {
        val results = saleOrderRepository.search(customerName = "tech")

        assertEquals(2, results.size)
        assertTrue(results.all { it.customer?.name?.contains("Tech", ignoreCase = true) == true })
    }

    @Test
    @Transactional
    fun `search by customer name should be case insensitive`() {
        val results = saleOrderRepository.search(customerName = "TECH")

        assertEquals(2, results.size)
    }

    @Test
    @Transactional
    fun `search by status should return only orders with that status`() {
        val results = saleOrderRepository.search(status = OrderStatus.PENDING)

        assertEquals(1, results.size)
        assertEquals(OrderStatus.PENDING, results[0].status)
    }

    @Test
    @Transactional
    fun `search by date range should return orders within range`() {
        val startDate = LocalDate.now().minusDays(4)
        val endDate = LocalDate.now().minusDays(2)

        val results = saleOrderRepository.search(startDate = startDate, endDate = endDate)

        assertEquals(1, results.size)
        assertTrue(results[0].orderDate.toLocalDate() in startDate..endDate)
    }

    @Test
    @Transactional
    fun `search by order ID should return exact match`() {
        val allOrders = saleOrderRepository.listAll()
        val targetId = allOrders.first().id!!

        val results = saleOrderRepository.search(orderId = targetId)

        assertEquals(1, results.size)
        assertEquals(targetId, results[0].id)
    }

    @Test
    @Transactional
    fun `search with multiple filters should combine with AND logic`() {
        val results = saleOrderRepository.search(
            customerName = "tech",
            status = OrderStatus.PENDING
        )

        assertEquals(1, results.size)
        assertEquals(OrderStatus.PENDING, results[0].status)
        assertTrue(results[0].customer?.name?.contains("Tech", ignoreCase = true) == true)
    }

    @Test
    @Transactional
    fun `search without filters should return all orders ordered by date desc`() {
        val results = saleOrderRepository.search()

        assertEquals(3, results.size)
        // Verify descending order
        assertTrue(results[0].orderDate >= results[1].orderDate)
        assertTrue(results[1].orderDate >= results[2].orderDate)
    }

    @Test
    @Transactional
    fun `search with non-matching criteria should return empty list`() {
        val results = saleOrderRepository.search(customerName = "NonExistent")

        assertTrue(results.isEmpty())
    }
}
