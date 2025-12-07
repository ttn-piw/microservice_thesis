package com.thesis.hotel_service.repository.httpClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "booking-service", url = "http://localhost:8083/internal/bookings/")
public interface bookingClient {
    @GetMapping("availableRoom")
    Map<UUID,Integer> getBookedRoomCounts(@RequestParam(required = true) UUID hotelId,
                                       @RequestParam(required = true) @DateTimeFormat(pattern = "YYYY-MM-dd") LocalDate checkIn,
                                       @RequestParam(required = true) @DateTimeFormat(pattern = "YYYY-MM-dd") LocalDate checkOut);

    @GetMapping("popular")
    List<UUID> getPopularHotels();
}
