package com.versatech.rits.entity

import jakarta.persistence.*

@Entity
@Table(name = "contacts")
data class Contact(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    var booking: Booking? = null
)
