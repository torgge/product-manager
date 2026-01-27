package com.example.product.application.service

import com.example.product.domain.model.Supplier
import com.example.product.domain.repository.SupplierRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import java.time.LocalDateTime

@ApplicationScoped
class SupplierService(private val supplierRepository: SupplierRepository) {

    fun findAll(): List<Supplier> {
        return supplierRepository.listAll()
    }

    fun findById(id: Long): Supplier? {
        return supplierRepository.findById(id)
    }

    fun findByName(name: String): List<Supplier> {
        return supplierRepository.findByName(name)
    }

    @Transactional
    fun create(supplier: Supplier): Supplier {
        supplier.createdAt = LocalDateTime.now()
        supplier.updatedAt = LocalDateTime.now()
        supplierRepository.persist(supplier)
        return supplier
    }

    @Transactional
    fun update(id: Long, supplier: Supplier): Supplier? {
        val existingSupplier = supplierRepository.findById(id) ?: return null

        existingSupplier.name = supplier.name
        existingSupplier.email = supplier.email
        existingSupplier.phone = supplier.phone
        existingSupplier.address = supplier.address
        existingSupplier.document = supplier.document
        existingSupplier.updatedAt = LocalDateTime.now()

        return existingSupplier
    }

    @Transactional
    fun delete(id: Long): Boolean {
        return supplierRepository.deleteById(id)
    }

    fun count(): Long {
        return supplierRepository.count()
    }
}
