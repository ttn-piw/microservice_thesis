package com.thesis.booking_service.service;

import com.thesis.booking_service.dto.response.ApiResponse;
import com.thesis.booking_service.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookingService {
    @Autowired
    BookingRepository bookingRepository;

    public ApiResponse getAllBookings(){
        return ApiResponse.builder()
                .code(200)
                .message("SUCCESSFULLY:")
                .data(bookingRepository.findAll())
                .build();
    }
}
