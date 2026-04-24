package com.versatech.rits.config

import com.versatech.rits.entity.*
import com.versatech.rits.model.*
import com.versatech.rits.repository.*
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.Random

@Component
class DataInitializer(
    private val airlineRepository: AirlineRepository,
    private val flightRepository: FlightRepository,
    private val bookingRepository: BookingRepository
) : CommandLineRunner {

    private val random = Random()

    @Transactional
    override fun run(vararg args: String) {
        if (airlineRepository.count() > 0) return

        // 1. Initial Airlines
        val airlines = listOf(
            Airline(id = "GAA", name = "Garuda API"),
            Airline(id = "GAL", name = "Garuda Altea"),
            Airline(id = "GAW", name = "Garuda Web"),
            Airline(id = "SJ", name = "Sriwijaya Air")
        )
        airlineRepository.saveAll(airlines)

        // 2. Initial Flights
        val routes = listOf(
            Pair("CGK", "DPS"),
            Pair("CGK", "SUB"),
            Pair("SUB", "CGK"),
            Pair("DPS", "CGK")
        )

        val allFlights = mutableListOf<Flight>()
        routes.forEach { (origin, dest) ->
            airlines.forEach { airline ->
                repeat(5) { i ->
                    val departureTime = LocalDateTime.of(2024, 5, 1, (6 + i * 3) % 24, random.nextInt(6) * 10)
                    val arrivalTime = departureTime.plusHours(1).plusMinutes(30 + random.nextInt(30).toLong())
                    
                    val flight = Flight(
                        flightNo = "${airline.id}${100 + random.nextInt(899)}",
                        origin = origin,
                        destination = dest,
                        originTime = departureTime,
                        destTime = arrivalTime,
                        airline = airline,
                        aircraft = listOf("Boeing 737", "Airbus A320", "Boeing 777").random()
                    )
                    
                    val classes = mutableListOf<FlightClass>()
                    val classTypes = listOf(
                        Triple("Y", "Economy", BigDecimal("800000")),
                        Triple("C", "Business", BigDecimal("2500000")),
                        Triple("F", "First Class", BigDecimal("6000000"))
                    )
                    
                    classTypes.forEachIndexed { index, (code, name, basePrice) ->
                        if (code != "Y" && random.nextBoolean()) return@forEachIndexed
                        var availability = random.nextInt(10)
                        if (i == 0 && index == 0) availability = 0 
                        
                        val priceModifier = BigDecimal(1.0 + (random.nextDouble() * 0.5))
                        classes.add(FlightClass(
                            classCode = "$code$availability",
                            className = name,
                            basePrice = basePrice.multiply(priceModifier).setScale(0, java.math.RoundingMode.HALF_UP),
                            flight = flight
                        ))
                    }
                    flight.classes.addAll(classes)
                    allFlights.add(flight)
                }
            }
        }
        val savedFlights = flightRepository.saveAll(allFlights)
        val allAvailableClasses = savedFlights.flatMap { it.classes }

        // 3. Generate 33 Dummy Bookings
        val bookingList = mutableListOf<Booking>()
        val firstNames = listOf("Ahmad", "Budi", "Citra", "Dewi", "Eka", "Farhan", "Gita", "Hendra", "Indah", "Joko")
        val lastNames = listOf("Saputra", "Wijaya", "Kusuma", "Lestari", "Pratama", "Sari", "Utomo", "Wahyu", "Zulkarnain")

        repeat(33) { i ->
            val isLast = (i == 32)
            val status = if (isLast) "WAITING" else "CANCELLED"
            // Spread bookings over the last 2 days
            val bookingTime = LocalDateTime.now().minusDays(2).plusHours(i.toLong() * 1)
            
            val booking = Booking(
                pnr = generatePNR(),
                totalFare = BigDecimal(random.nextInt(2000000) + 500000),
                bookingTime = bookingTime,
                status = status
            )

            // Contact
            val contact = Contact(
                name = "${firstNames.random()} ${lastNames.random()}",
                email = "user${i}@example.com",
                phone = "+62812${1000000 + random.nextInt(8999999)}",
                booking = booking
            )
            booking.contact = contact

            // Passengers (1-3)
            val passengerCount = random.nextInt(3) + 1
            repeat(passengerCount) { pIdx ->
                val passenger = Passenger(
                    title = if (random.nextBoolean()) "MR" else "MS",
                    firstName = firstNames.random(),
                    surname = lastNames.random(),
                    birthDate = "1990-01-01",
                    passportNumber = "A${1000000 + random.nextInt(8999999)}",
                    type = PassengerType.ADULT,
                    booking = booking
                )
                booking.addPassenger(passenger)
            }

            // Detail (Flight Class)
            val randomClass = allAvailableClasses.random()
            val detail = BookingDetail(
                booking = booking,
                flightClass = randomClass
            )
            booking.addDetail(detail)
            
            bookingList.add(booking)
        }

        bookingRepository.saveAll(bookingList)
        println("Dummy data initialized with ${allFlights.size} flights and 33 dummy bookings!")
    }

    private fun generatePNR(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..6)
            .map { chars[random.nextInt(chars.length)] }
            .joinToString("")
    }
}
