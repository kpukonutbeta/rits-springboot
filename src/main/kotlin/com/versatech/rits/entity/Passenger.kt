package com.versatech.rits.entity

import jakarta.persistence.*

@Entity
@Table(name = "passengers")
data class Passenger(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    val title: String = "",
    val firstName: String = "",
    val surname: String = "",
    val birthDate: String = "",
    val passportNumber: String? = null,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    var booking: Booking? = null
)
