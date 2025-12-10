package com.thesis.review_service.repository.httpClient;

import com.thesis.review_service.dto.response.ApiResponse;
import com.thesis.review_service.dto.response.AvailableReviewBookingResponse;
import com.thesis.review_service.dto.response.BookingResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "booking-service", url = "http://localhost:8083/")
public interface BookingClient {

    @GetMapping("api/v1/bookings/{id}")
    ApiResponse getBookingById(@PathVariable("id")UUID bookingId);

    @GetMapping("internal/bookings/me")
    List<BookingResponse> getBookingByEmailToReview(@RequestParam String email);
}
