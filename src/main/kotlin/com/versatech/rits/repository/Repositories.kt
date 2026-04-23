package com.versatech.rits.repository

import com.versatech.rits.entity.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AirlineRepository : JpaRepository<Airline, String>

@Repository
interface FlightRepository : JpaRepository<Flight, Long> {
    fun findByOriginAndDestinationAndAirlineId(origin: String, destination: String, airlineId: String): List<Flight>
}

@Repository
interface FlightClassRepository : JpaRepository<FlightClass, Long>

@Repository
interface BookingRepository : JpaRepository<Booking, Long> {
    fun findByPnr(pnr: String): Booking?
}

@Repository
interface BookingDetailRepository : JpaRepository<BookingDetail, Long>

@Repository
interface ContactRepository : JpaRepository<Contact, Long>

@Repository
interface PassengerRepository : JpaRepository<Passenger, Long>
