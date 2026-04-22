package com.versatech.rits.model

import java.time.LocalDateTime

data class FlightSearchRequest(
    val tripType: String,
    val fromCity: String,
    val toCity: String,
    val departureDate: String,
    val returnDate: String? = null,
    val adult: Int,
    val child: Int = 0,
    val infant: Int = 0
)

data class FlightSearchResponse(
    val airlines: List<AirlineDto>
)

data class AirlineDto(
    val id: String,
    val name: String,
    val flights: List<FlightDto>
)

data class FlightDto(
    val flightNo: String,
    val aircraft: String,
    val origin: String,
    val originTime: String,
    val destination: String,
    val destTime: String,
    val duration: String,
    val priceBasic: String,
    val priceTax: String,
    val priceTotal: String,
    val isReturn: Boolean
)

data class PassengerDto(
    val title: String,
    val firstName: String,
    val surname: String,
    val birthDate: String,
    val passportNumber: String? = null
)

data class ContactDto(
    val name: String,
    val email: String,
    val phone: String
)

data class FlightBookingRequest(
    val flights: List<FlightDto>,
    val passengers: List<PassengerDto>,
    val contact: ContactDto,
    val totalFare: String,
    val totalPassengers: Int
)

data class FlightBookingResponse(
    val success: Boolean,
    val pnr: String,
    val message: String,
    val timestamp: LocalDateTime = LocalDateTime.now()
)
