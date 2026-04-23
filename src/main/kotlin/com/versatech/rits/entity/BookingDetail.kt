package com.versatech.rits.entity

import jakarta.persistence.*

@Entity
@Table(name = "booking_details")
data class BookingDetail(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    var booking: Booking? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_class_id")
    var flightClass: FlightClass? = null
)
