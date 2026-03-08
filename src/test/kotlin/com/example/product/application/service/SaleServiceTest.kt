package com.example.product.application.service

import com.example.product.domain.model.*
import com.example.product.domain.repository.CustomerRepository
import com.example.product.domain.repository.SaleOrderRepository
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
class SaleServiceTest {

    @Inject
    lateinit var saleService: SaleService

    @Inject
    lateinit var saleOrderRepository: SaleOrderRepository

    @Inject
    lateinit var customerRepository: CustomerRepository

    private lateinit var testCustomer: Customer

    @BeforeEach
    @Transactional
    fun setup() {
        saleOrderRepository.deleteAll()
        customerRepository.deleteAll()

        testCustomer = Customer(
            name = "Test Customer",
            document = "123.456.789-01",
            email = "test@example.com"
        )
        customerRepository.persist(testCustomer)
    }

    @AfterEach
    @Transactional
    fun cleanup() {
        saleOrderRepository.deleteAll()
        customerRepository.deleteAll()
    }

    @Test
    @Transactional
    fun `findAll should return all sale orders`() {
        // Create test orders
        val sale1 = SaleOrder(
            customer = testCustomer,
            status = OrderStatus.PENDING,
            totalAmount = BigDecimal("100.00")
        )
        saleOrderRepository.persist(sale1)

        val sale2 = SaleOrder(
            customer = testCustomer,
            status = OrderStatus.CONFIRMED,
            totalAmount = BigDecimal("200.00")
        )
        saleOrderRepository.persist(sale2)

        val results = saleService.findAll()

        assertEquals(2, results.size)
    }

    @Test
    @Transactional
    fun `findById should return sale order when exists`() {
        val sale = SaleOrder(
            customer = testCustomer,
            status = OrderStatus.PENDING,
            totalAmount = BigDecimal("100.00")
        )
        saleOrderRepository.persist(sale)

        val result = saleService.findById(sale.id!!)

        assertNotNull(result)
        assertEquals(sale.id, result?.id)
    }

    @Test
    @Transactional
    fun `findByStatus should return only orders with given status`() {
        val sale1 = SaleOrder(
            customer = testCustomer,
            status = OrderStatus.PENDING,
            totalAmount = BigDecimal("100.00")
        )
        saleOrderRepository.persist(sale1)

        val sale2 = SaleOrder(
            customer = testCustomer,
            status = OrderStatus.CONFIRMED,
            totalAmount = BigDecimal("200.00")
        )
        saleOrderRepository.persist(sale2)

        val results = saleService.findByStatus(OrderStatus.PENDING)

        assertEquals(1, results.size)
        assertEquals(OrderStatus.PENDING, results[0].status)
    }

    @Test
    @Transactional
    fun `search should delegate to repository with correct parameters`() {
        val sale1 = SaleOrder(
            customer = testCustomer,
            orderDate = LocalDate.now().minusDays(5).atStartOfDay(),
            status = OrderStatus.PENDING,
            totalAmount = BigDecimal("100.00")
        )
        saleOrderRepository.persist(sale1)

        val results = saleService.search(
            customerName = "Test",
            status = OrderStatus.PENDING
        )

        assertEquals(1, results.size)
        assertEquals(testCustomer.id, results[0].customer?.id)
    }

    @Test
    @Transactional
    fun `search with date range should return filtered results`() {
        val sale1 = SaleOrder(
            customer = testCustomer,
            orderDate = LocalDate.now().minusDays(10).atStartOfDay(),
            status = OrderStatus.RECEIVED,
            totalAmount = BigDecimal("100.00")
        )
        saleOrderRepository.persist(sale1)

        val sale2 = SaleOrder(
            customer = testCustomer,
            orderDate = LocalDate.now().minusDays(2).atStartOfDay(),
            status = OrderStatus.PENDING,
            totalAmount = BigDecimal("200.00")
        )
        saleOrderRepository.persist(sale2)

        val results = saleService.search(
            startDate = LocalDate.now().minusDays(5),
            endDate = LocalDate.now()
        )

        assertEquals(1, results.size)
        assertTrue(results[0].orderDate.toLocalDate().isAfter(LocalDate.now().minusDays(6)))
    }

    @Test
    @Transactional
    fun `search without parameters should return all orders`() {
        val sale1 = SaleOrder(
            customer = testCustomer,
            status = OrderStatus.PENDING,
            totalAmount = BigDecimal("100.00")
        )
        saleOrderRepository.persist(sale1)

        val sale2 = SaleOrder(
            customer = testCustomer,
            status = OrderStatus.CONFIRMED,
            totalAmount = BigDecimal("200.00")
        )
        saleOrderRepository.persist(sale2)

        val results = saleService.search()

        assertEquals(2, results.size)
    }

    @Test
    @Transactional
    fun `findRecent should limit results`() {
        // Create 5 orders
        repeat(5) { i ->
            val sale = SaleOrder(
                customer = testCustomer,
                orderDate = LocalDate.now().minusDays(i.toLong()).atStartOfDay(),
                status = OrderStatus.PENDING,
                totalAmount = BigDecimal("100.00")
            )
            saleOrderRepository.persist(sale)
        }

        val results = saleService.findRecent(3)

        assertEquals(3, results.size)
        // Should be ordered by date descending (newest first)
        assertTrue(results[0].orderDate >= results[1].orderDate)
        assertTrue(results[1].orderDate >= results[2].orderDate)
    }
}
