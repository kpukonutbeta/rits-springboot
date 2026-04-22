package com.versatech.rits.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "bookings")
data class Booking(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    val pnr: String = "",
    val totalFare: String = "",
    val totalPassengers: Int = 0,
    val bookingTime: LocalDateTime = LocalDateTime.now(),
    
    @OneToMany(mappedBy = "booking", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var passengers: MutableList<Passenger> = mutableListOf(),
    
    @OneToOne(mappedBy = "booking", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var contact: Contact? = null,
    
    // We can just save the flight numbers or create a ManyToMany.
    // For simplicity, we'll store flight numbers as a comma separated string since
    // flights represent schedule details and we just want to link them.
    val flightNumbers: String = ""
) {
    fun addPassenger(passenger: Passenger) {
        passengers.add(passenger)
        passenger.booking = this
    }
    
    fun setBookingContact(newContact: Contact) {
        contact = newContact
        newContact.booking = this
    }
}
