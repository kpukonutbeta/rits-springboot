package com.versatech.rits.entity

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "flight_classes")
data class FlightClass(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id")
    var flight: Flight? = null,

    val classCode: String = "", // e.g. "Y", "C", "F"
    val className: String = "", // e.g. "Economy", "Business"
    val basePrice: BigDecimal = BigDecimal.ZERO
)
