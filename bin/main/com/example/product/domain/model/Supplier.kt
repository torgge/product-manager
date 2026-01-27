package com.example.product.domain.model

import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime

@Entity
data class Supplier(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @field:NotBlank(message = "Name is required")
    var name: String = "",

    @field:Email(message = "Invalid email format")
    var email: String? = null,

    var phone: String? = null,

    var address: String? = null,

    var document: String? = null, // CNPJ

    var createdAt: LocalDateTime = LocalDateTime.now(),

    var updatedAt: LocalDateTime = LocalDateTime.now()
)
