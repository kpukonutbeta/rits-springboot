package com.versatech.rits.entity

import jakarta.persistence.*

@Entity
@Table(name = "flights")
data class Flight(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    val flightNo: String = "",
    val aircraft: String = "",
    val origin: String = "",
    val originTime: String = "",
    val destination: String = "",
    val destTime: String = "",
    val duration: String = "",
    val priceBasic: String = "",
    val priceTax: String = "",
    val priceTotal: String = "",
    val isReturn: Boolean = false,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "airline_id")
    var airline: Airline? = null
)
