package com.versatech.rits.repository

import com.versatech.rits.entity.Airline
import com.versatech.rits.entity.Booking
import com.versatech.rits.entity.Flight
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AirlineRepository : JpaRepository<Airline, String>

@Repository
interface FlightRepository : JpaRepository<Flight, Long> {
    fun findByOriginAndDestination(origin: String, destination: String): List<Flight>
}

@Repository
interface BookingRepository : JpaRepository<Booking, Long>
