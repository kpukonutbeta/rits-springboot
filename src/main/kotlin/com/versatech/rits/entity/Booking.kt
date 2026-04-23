package com.versatech.rits.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "bookings")
data class Booking(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    val pnr: String = "",
    val totalFare: BigDecimal = BigDecimal.ZERO,
    val bookingTime: LocalDateTime = LocalDateTime.now(),
    var status: String = "WAITING", // WAITING, ISSUED, CANCELLED
    
    @OneToMany(mappedBy = "booking", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var passengers: MutableList<Passenger> = mutableListOf(),
    
    @OneToMany(mappedBy = "booking", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var details: MutableList<BookingDetail> = mutableListOf(),
    
    @OneToOne(mappedBy = "booking", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var contact: Contact? = null
) {
    fun addPassenger(passenger: Passenger) {
        passengers.add(passenger)
        passenger.booking = this
    }

    fun addDetail(detail: BookingDetail) {
        details.add(detail)
        detail.booking = this
    }
}
