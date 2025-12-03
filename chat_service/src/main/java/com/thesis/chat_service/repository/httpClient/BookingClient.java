package com.thesis.chat_service.repository.httpClient;

import com.thesis.chat_service.dto.request.AvailabilityRoomRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.UUID;

@FeignClient(name = "booking-service", url = "localhost:8083/api/v1/bookings")
public interface BookingClient {
    @GetMapping("/api/availability/check")
    Integer checkAvailability(
            @RequestParam UUID roomTypeId,
            @RequestParam LocalDate checkIn,
            @RequestParam LocalDate checkOut,
            @RequestParam int quantityBooking
    );

    //    // Matches the Booking Service's /api/booking/create endpoint
//    @PostMapping("/api/booking/create")
//    BookingConfirmation createNewBooking(@RequestBody BookingRequest request);
}
