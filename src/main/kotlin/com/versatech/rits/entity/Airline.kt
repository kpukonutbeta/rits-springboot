package com.versatech.rits.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.OneToMany
import jakarta.persistence.CascadeType
import jakarta.persistence.FetchType

@Entity
@Table(name = "airlines")
data class Airline(
    @Id
    val id: String = "", // e.g. "sj"
    
    val name: String = "", // e.g. "Sriwijaya Air (SJ)"
    
    @OneToMany(mappedBy = "airline", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val flights: MutableList<Flight> = mutableListOf()
) {
    // JPA requires no-arg constructor, covered by default values in Kotlin
}
