package com.thesis.booking_service.service;

import com.thesis.booking_service.repository.BookingRepository;
import com.thesis.booking_service.repository.httpClient.hotelClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class checkAvailableRoom {
    final BookingRepository bookingRepository;
    final hotelClient hotelClient;

    public void checkAvailability(UUID roomTypeId, LocalDate checkIn, LocalDate checkOut, int quantityBooking){
        int
    }
}
