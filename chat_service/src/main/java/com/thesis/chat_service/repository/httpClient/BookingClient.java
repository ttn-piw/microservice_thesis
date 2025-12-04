package com.thesis.chat_service.repository.httpClient;

import com.thesis.chat_service.configuration.TokenConfig;
import com.thesis.chat_service.dto.request.AvailabilityRoomRequest;
import com.thesis.chat_service.dto.request.CreateBookingToBookingServiceRequest;
import com.thesis.chat_service.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@FeignClient(name = "booking-service", url = "localhost:8083/api/v1/bookings", configuration = TokenConfig.class)
public interface BookingClient {
    @GetMapping("/api/availability/check")
    Integer checkAvailability(
            @RequestParam UUID roomTypeId,
            @RequestParam LocalDate checkIn,
            @RequestParam LocalDate checkOut,
            @RequestParam int quantityBooking
    );

    @PostMapping("/bookings")
    ApiResponse<Object> createBooking(
            @RequestBody CreateBookingToBookingServiceRequest request);


    @GetMapping("/{id}")
    ApiResponse getBookingById(@PathVariable("id")UUID bookingId);

    @PostMapping("/email/chatbot/{bookingId}")
    void sendEmail(@PathVariable UUID bookingId);
}
