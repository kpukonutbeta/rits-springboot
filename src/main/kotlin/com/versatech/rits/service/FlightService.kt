package com.versatech.rits.service

import com.versatech.rits.entity.Booking
import com.versatech.rits.entity.Contact
import com.versatech.rits.entity.Passenger
import com.versatech.rits.model.*
import com.versatech.rits.repository.AirlineRepository
import com.versatech.rits.repository.BookingRepository
import com.versatech.rits.repository.FlightRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class FlightService(
    private val flightRepository: FlightRepository,
    private val bookingRepository: BookingRepository,
    private val airlineRepository: AirlineRepository
) {

    fun searchFlights(request: FlightSearchRequest): FlightSearchResponse {
        // Find flights for the requested origin and destination
        val outboundFlights = flightRepository.findByOriginAndDestination(request.fromCity, request.toCity)
        
        // If it's a two-way trip, also find return flights
        val allFlights = outboundFlights.toMutableList()
        if (request.tripType == "TWO_WAY" && request.returnDate != null) {
            val returnFlights = flightRepository.findByOriginAndDestination(request.toCity, request.fromCity)
            allFlights.addAll(returnFlights)
        }

        // Group flights by airline
        val groupedByAirline = allFlights.groupBy { it.airline }

        val airlineDtos = groupedByAirline.mapNotNull { (airline, flights) ->
            if (airline == null) return@mapNotNull null
            
            val flightDtos = flights.map { f ->
                FlightDto(
                    flightNo = f.flightNo,
                    aircraft = f.aircraft,
                    origin = f.origin,
                    originTime = f.originTime,
                    destination = f.destination,
                    destTime = f.destTime,
                    duration = f.duration,
                    priceBasic = f.priceBasic,
                    priceTax = f.priceTax,
                    priceTotal = f.priceTotal,
                    isReturn = f.isReturn
                )
            }
            
            AirlineDto(
                id = airline.id,
                name = airline.name,
                flights = flightDtos
            )
        }

        return FlightSearchResponse(airlines = airlineDtos)
    }

    @Transactional
    fun bookFlights(request: FlightBookingRequest): FlightBookingResponse {
        val pnr = generatePnr()
        val flightNumbers = request.flights.joinToString(",") { it.flightNo }

        val booking = Booking(
            pnr = pnr,
            totalFare = request.totalFare,
            totalPassengers = request.totalPassengers,
            flightNumbers = flightNumbers
        )

        val contactEntity = Contact(
            name = request.contact.name,
            email = request.contact.email,
            phone = request.contact.phone
        )
        booking.setBookingContact(contactEntity)

        request.passengers.forEach { p ->
            val passengerEntity = Passenger(
                title = p.title,
                firstName = p.firstName,
                surname = p.surname,
                birthDate = p.birthDate,
                passportNumber = p.passportNumber
            )
            booking.addPassenger(passengerEntity)
        }

        bookingRepository.save(booking)

        return FlightBookingResponse(
            success = true,
            pnr = pnr,
            message = "Booking successfully processed"
        )
    }

    private fun generatePnr(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..6)
            .map { chars.random() }
            .joinToString("")
    }
}
