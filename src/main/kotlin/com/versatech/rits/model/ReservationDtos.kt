package com.versatech.rits.model

import com.versatech.rits.entity.PassengerType
import java.math.BigDecimal
import java.time.LocalDateTime

// 1. Availability DTOs
data class AvailabilityResponse(
    val flights: List<FlightAvailabilityDto>
)

data class FlightAvailabilityDto(
    val flightId: Long,
    val flightNo: String,
    val origin: String,
    val destination: String,
    val departureTime: LocalDateTime,
    val arrivalTime: LocalDateTime,
    val airlineName: String,
    val classes: List<FlightClassDto>
)

data class FlightClassDto(
    val classId: Long,
    val classCode: String, // Y, C, F
    val className: String,
    val basePrice: BigDecimal
)

// 2. Fare DTOs
data class FareResponse(
    val flightClassId: Long,
    val adultCount: Int,
    val childCount: Int,
    val infantCount: Int,
    val basePricePerAdult: BigDecimal,
    val totalTax: BigDecimal,
    val totalFare: BigDecimal
)

// 3. Booking DTOs
data class BookingRequest(
    val flightClassId: Long,
    val passengers: List<BookingPassengerDto>,
    val contact: BookingContactDto
)

data class BookingPassengerDto(
    val title: String,
    val firstName: String,
    val surname: String,
    val birthDate: String,
    val passportNumber: String? = null,
    val type: PassengerType
)

data class BookingContactDto(
    val name: String,
    val email: String,
    val phone: String
)

data class BookingResponse(
    val pnr: String,
    val bookingTime: LocalDateTime,
    val status: String,
    val totalFare: BigDecimal,
    val passengers: List<BookingPassengerDto>,
    val itinerary: FlightAvailabilityDto,
    val contact: BookingContactDto
)
