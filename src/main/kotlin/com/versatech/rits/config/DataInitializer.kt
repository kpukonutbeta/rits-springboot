package com.versatech.rits.config

import com.versatech.rits.entity.Airline
import com.versatech.rits.entity.Flight
import com.versatech.rits.entity.FlightClass
import com.versatech.rits.repository.AirlineRepository
import com.versatech.rits.repository.FlightRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.Random

@Component
class DataInitializer(
    private val airlineRepository: AirlineRepository,
    private val flightRepository: FlightRepository
) : CommandLineRunner {

    private val random = Random()

    override fun run(vararg args: String) {
        if (airlineRepository.count() > 0) return

        val airlines = listOf(
            Airline(id = "GAA", name = "Garuda API"),
            Airline(id = "GAL", name = "Garuda Altea"),
            Airline(id = "GAW", name = "Garuda Web"),
            Airline(id = "SJ", name = "Sriwijaya Air")
        )
        airlineRepository.saveAll(airlines)

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
                    val hour = 6 + (i * 3) + random.nextInt(2)
                    val minute = random.nextInt(6) * 10
                    val departureTime = LocalDateTime.of(2024, 5, 1, hour % 24, minute)
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
                        // Ensure Y is always there, others random
                        if (code != "Y" && random.nextBoolean()) return@forEachIndexed
                        
                        // Force 0 availability for the first flight's first class to guarantee it exists
                        // or just use random but make 0 more frequent for some flights
                        var availability = random.nextInt(10)
                        if (i == 0 && index == 0) availability = 0 // Guaranteed Y0 for the first flight of each airline/route
                        
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

        flightRepository.saveAll(allFlights)
        println("Dummy data initialized with ${allFlights.size} flights! Guaranteed some classes have 0 seats.")
    }
}
