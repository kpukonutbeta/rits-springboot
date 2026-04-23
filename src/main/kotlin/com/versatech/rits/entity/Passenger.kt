package com.versatech.rits.entity

import jakarta.persistence.*

@Entity
@Table(name = "passengers")
data class Passenger(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    val title: String = "", // Mr, Mrs, Mstr, Miss
    val firstName: String = "",
    val surname: String = "",
    val birthDate: String = "",
    val passportNumber: String? = null,
    
    @Enumerated(EnumType.STRING)
    val type: PassengerType = PassengerType.ADULT,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    var booking: Booking? = null
)

enum class PassengerType {
    ADULT, CHILD, INFANT
}
