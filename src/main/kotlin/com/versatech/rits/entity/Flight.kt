package com.versatech.rits.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "flights")
data class Flight(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    val flightNo: String = "",
    val aircraft: String = "",
    val origin: String = "",
    val originTime: LocalDateTime = LocalDateTime.now(),
    val destination: String = "",
    val destTime: LocalDateTime = LocalDateTime.now(),
    val duration: String = "",
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "airline_id")
    var airline: Airline? = null,

    @OneToMany(mappedBy = "flight", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var classes: MutableList<FlightClass> = mutableListOf()
)
