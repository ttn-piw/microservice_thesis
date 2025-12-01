package com.thesis.chat_service.repository.httpClient;

import com.thesis.chat_service.dto.request.AvailabilityRoomRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "booking-service", url = "localhost:8083/api/v1/bookings")
public interface BookingClient {
    @PostMapping("/api/availability/check")
    void checkRoomAvailability(@RequestBody AvailabilityRoomRequest request);

//    // Matches the Booking Service's /api/booking/create endpoint
//    @PostMapping("/api/booking/create")
//    BookingConfirmation createNewBooking(@RequestBody BookingRequest request);
}
