package com.versatech.rits.controller

import com.versatech.rits.model.*
import com.versatech.rits.service.ReservationService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/reservation")
class ReservationController(
    private val reservationService: ReservationService
) {

    @GetMapping("/availability")
    fun getAvailability(
        @RequestParam origin: String,
        @RequestParam destination: String,
        @RequestParam date: String,
        @RequestParam airlineCode: String
    ): AvailabilityResponse {
        return reservationService.getAvailability(origin, destination, date, airlineCode)
    }

    @GetMapping("/fare")
    fun getFare(
        @RequestParam flightClassId: Long,
        @RequestParam(defaultValue = "1") adult: Int,
        @RequestParam(defaultValue = "0") child: Int,
        @RequestParam(defaultValue = "0") infant: Int
    ): FareResponse {
        return reservationService.getFare(flightClassId, adult, child, infant)
    }

    @PostMapping("/book")
    fun book(@RequestBody request: BookingRequest): BookingResponse {
        return reservationService.book(request)
    }

    @GetMapping("/booking/{pnr}")
    fun getBookingByPnr(@PathVariable pnr: String): BookingResponse {
        return reservationService.getBookingByPnr(pnr)
    }

    @GetMapping("/history")
    fun getBookingHistory(): List<BookingResponse> {
        return reservationService.getBookingHistory()
    }

    @PostMapping("/cancel/{pnr}")
    fun cancelBooking(@PathVariable pnr: String): BookingResponse {
        return reservationService.cancelBooking(pnr)
    }
}
