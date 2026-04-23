package com.versatech.rits.service

import com.versatech.rits.entity.*
import com.versatech.rits.model.*
import com.versatech.rits.repository.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Service
class ReservationService(
    private val flightRepository: FlightRepository,
    private val flightClassRepository: FlightClassRepository,
    private val bookingRepository: BookingRepository,
    private val airlineRepository: AirlineRepository
) {

    fun getAvailability(origin: String, destination: String, date: String, airlineCode: String): AvailabilityResponse {
        val flights = flightRepository.findByOriginAndDestinationAndAirlineId(origin, destination, airlineCode)
        
        val flightDtos = flights.map { flight ->
            FlightAvailabilityDto(
                flightId = flight.id,
                flightNo = flight.flightNo,
                origin = flight.origin,
                destination = flight.destination,
                departureTime = flight.originTime,
                arrivalTime = flight.destTime,
                airlineName = flight.airline?.name ?: "Unknown",
                classes = flight.classes.map { 
                    FlightClassDto(it.id, it.classCode, it.className, it.basePrice)
                }
            )
        }
        
        return AvailabilityResponse(flightDtos)
    }

    fun getFare(flightClassId: Long, adult: Int, child: Int, infant: Int): FareResponse {
        val flightClass = flightClassRepository.findById(flightClassId)
            .orElseThrow { RuntimeException("Flight class not found") }
        
        val basePrice = flightClass.basePrice
        val taxMultiplier = BigDecimal("0.1") // 10% tax
        
        val totalBase = basePrice.multiply(BigDecimal(adult))
            .add(basePrice.multiply(BigDecimal("0.75")).multiply(BigDecimal(child))) // Child 75%
            .add(basePrice.multiply(BigDecimal("0.1")).multiply(BigDecimal(infant))) // Infant 10%
            
        val totalTax = totalBase.multiply(taxMultiplier)
        val totalFare = totalBase.add(totalTax)
        
        return FareResponse(
            flightClassId = flightClassId,
            adultCount = adult,
            childCount = child,
            infantCount = infant,
            basePricePerAdult = basePrice,
            totalTax = totalTax,
            totalFare = totalFare
        )
    }

    @Transactional
    fun book(request: BookingRequest): BookingResponse {
        val flightClass = flightClassRepository.findById(request.flightClassId)
            .orElseThrow { RuntimeException("Flight class not found") }
        
        val adultCount = request.passengers.count { it.type == PassengerType.ADULT }
        val childCount = request.passengers.count { it.type == PassengerType.CHILD }
        val infantCount = request.passengers.count { it.type == PassengerType.INFANT }
        
        val fareInfo = getFare(request.flightClassId, adultCount, childCount, infantCount)
        
        val booking = Booking(
            pnr = generatePNR(),
            totalFare = fareInfo.totalFare,
            bookingTime = LocalDateTime.now(),
            status = "WAITING"
        )
        
        val contact = Contact(
            name = request.contact.name,
            email = request.contact.email,
            phone = request.contact.phone,
            booking = booking
        )
        booking.contact = contact
        
        request.passengers.forEach { pDto ->
            val passenger = Passenger(
                title = pDto.title,
                firstName = pDto.firstName,
                surname = pDto.surname,
                birthDate = pDto.birthDate,
                passportNumber = pDto.passportNumber,
                type = pDto.type,
                booking = booking
            )
            booking.addPassenger(passenger)
        }
        
        val detail = BookingDetail(
            booking = booking,
            flightClass = flightClass
        )
        booking.addDetail(detail)
        
        val savedBooking = bookingRepository.save(booking)
        return mapToBookingResponse(savedBooking)
    }

    fun getBookingByPnr(pnr: String): BookingResponse {
        val booking = bookingRepository.findByPnr(pnr)
            ?: throw RuntimeException("Booking not found")
        return mapToBookingResponse(booking)
    }

    fun getBookingHistory(): List<BookingResponse> {
        val bookings = bookingRepository.findAll()
        return bookings.map { mapToBookingResponse(it) }
    }

    @Transactional
    fun cancelBooking(pnr: String): BookingResponse {
        val booking = bookingRepository.findByPnr(pnr)
            ?: throw RuntimeException("Booking not found")
            
        if (booking.status != "WAITING") {
            throw RuntimeException("Only bookings with WAITING status can be cancelled. Current status: ${booking.status}")
        }
        
        booking.status = "CANCELLED"
        val savedBooking = bookingRepository.save(booking)
        return mapToBookingResponse(savedBooking)
    }

    private fun mapToBookingResponse(booking: Booking): BookingResponse {
        val detail = booking.details.firstOrNull() ?: throw RuntimeException("Booking details not found")
        val flightClass = detail.flightClass ?: throw RuntimeException("Flight class not found")
        val flight = flightClass.flight ?: throw RuntimeException("Flight not found")
        val contact = booking.contact ?: throw RuntimeException("Contact details not found")
        
        return BookingResponse(
            pnr = booking.pnr,
            bookingTime = booking.bookingTime,
            status = booking.status,
            totalFare = booking.totalFare,
            passengers = booking.passengers.map { p ->
                BookingPassengerDto(
                    title = p.title,
                    firstName = p.firstName,
                    surname = p.surname,
                    birthDate = p.birthDate,
                    passportNumber = p.passportNumber,
                    type = p.type
                )
            },
            itinerary = FlightAvailabilityDto(
                flightId = flight.id,
                flightNo = flight.flightNo,
                origin = flight.origin,
                destination = flight.destination,
                departureTime = flight.originTime,
                arrivalTime = flight.destTime,
                airlineName = flight.airline?.name ?: "Unknown",
                classes = listOf(FlightClassDto(flightClass.id, flightClass.classCode, flightClass.className, flightClass.basePrice))
            ),
            contact = BookingContactDto(
                name = contact.name,
                email = contact.email,
                phone = contact.phone
            )
        )
    }

    private fun generatePNR(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..6)
            .map { chars[Random().nextInt(chars.length)] }
            .joinToString("")
    }
}
