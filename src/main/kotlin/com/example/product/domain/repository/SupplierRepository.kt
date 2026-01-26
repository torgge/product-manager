package com.example.product.domain.repository

import com.example.product.domain.model.Supplier
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class SupplierRepository : PanacheRepository<Supplier> {

    fun findByName(name: String): List<Supplier> {
        return list("name like ?1", "%$name%")
    }

    fun findByDocument(document: String): Supplier? {
        return find("document", document).firstResult()
    }
}
