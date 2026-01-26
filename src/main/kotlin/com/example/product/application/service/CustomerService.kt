package com.example.product.application.service

import com.example.product.domain.model.Customer
import com.example.product.domain.repository.CustomerRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import java.time.LocalDateTime

@ApplicationScoped
class CustomerService(private val customerRepository: CustomerRepository) {

    fun findAll(): List<Customer> {
        return customerRepository.listAll()
    }

    fun findById(id: Long): Customer? {
        return customerRepository.findById(id)
    }

    fun findByName(name: String): List<Customer> {
        return customerRepository.findByName(name)
    }

    @Transactional
    fun create(customer: Customer): Customer {
        customer.createdAt = LocalDateTime.now()
        customer.updatedAt = LocalDateTime.now()
        customerRepository.persist(customer)
        return customer
    }

    @Transactional
    fun update(id: Long, customer: Customer): Customer? {
        val existingCustomer = customerRepository.findById(id) ?: return null

        existingCustomer.name = customer.name
        existingCustomer.email = customer.email
        existingCustomer.phone = customer.phone
        existingCustomer.address = customer.address
        existingCustomer.document = customer.document
        existingCustomer.updatedAt = LocalDateTime.now()

        return existingCustomer
    }

    @Transactional
    fun delete(id: Long): Boolean {
        return customerRepository.deleteById(id)
    }

    fun count(): Long {
        return customerRepository.count()
    }
}
