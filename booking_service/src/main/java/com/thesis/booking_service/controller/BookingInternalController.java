package com.thesis.booking_service.controller;

import com.thesis.booking_service.dto.response.ApiResponse;
import com.thesis.booking_service.service.BookingService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/internal/bookings")
public class BookingInternalController {
    @Autowired
    BookingService bookingService;


    @GetMapping("/availableRoom")
    public Map<UUID,Integer> getNumbersOfBookedRoomByHotelId(
            HttpServletRequest request,
            @RequestParam(required = true) UUID hotelId,
            @RequestParam(required = true) LocalDate checkIn,
            @RequestParam(required = true) LocalDate checkOut){
        String path = request.getMethod() + " " + request.getRequestURI() + "?" + request.getQueryString();
        log.info("API: -> {}", path);
        log.info("Check in: {}", checkIn);

        Map<UUID,Integer> response = bookingService.getNumberBookedRoom(hotelId, checkIn, checkOut);
        return response;
    }

    @GetMapping("/popular")
    public List<UUID> getPopularHotels() {
        return bookingService.getPopularHotels();
    }
}
