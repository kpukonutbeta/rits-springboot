package com.versatech.rits.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "products")
data class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    var name: String,
    var description: String,
    var price: Double
) {
    // No-arg constructor required by JPA
    constructor() : this(null, "", "", 0.0)
}
