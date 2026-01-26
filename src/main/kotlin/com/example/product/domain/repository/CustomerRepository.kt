package com.example.product.domain.repository

import com.example.product.domain.model.Customer
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class CustomerRepository : PanacheRepository<Customer> {

    fun findByName(name: String): List<Customer> {
        return list("name like ?1", "%$name%")
    }

    fun findByDocument(document: String): Customer? {
        return find("document", document).firstResult()
    }
}
