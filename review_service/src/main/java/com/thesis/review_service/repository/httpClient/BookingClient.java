package com.thesis.review_service.repository.httpClient;

import com.thesis.review_service.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "booking-service", url = "http://localhost:8083/api/v1/bookings/")
public interface BookingClient {

    @GetMapping("/{id}")
    ApiResponse getBookingById(@PathVariable("id")UUID bookingId);
}
