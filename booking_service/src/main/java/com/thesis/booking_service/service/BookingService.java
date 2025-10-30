package com.thesis.booking_service.service;

import com.thesis.booking_service.dto.response.ApiResponse;
import com.thesis.booking_service.mapper.BookingStatus;
import com.thesis.booking_service.mapper.PaymentStatusType;
import com.thesis.booking_service.model.Booking;
import com.thesis.booking_service.repository.BookingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class BookingService {
    Logger log = LoggerFactory.getLogger(BookingService.class);

    @Autowired
    BookingRepository bookingRepository;

    public ApiResponse getAllBookings(){
        return ApiResponse.builder()
                .code(200)
                .message("SUCCESSFULLY:")
                .data(bookingRepository.findAll())
                .build();
    }

    public ApiResponse getBookingById(UUID bookingId){
        if (bookingRepository.findBookingById(bookingId) == null)
            return ApiResponse.builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .message(String.format("FAIL: Booking with %s not found",bookingId.toString()))
                    .data(null)
                    .build();

        return ApiResponse.builder()
                .code(HttpStatus.OK.value())
                .message("SUCCESSFUL")
                .data(bookingRepository.findBookingById(bookingId))
                .build();
    }
    public ApiResponse getBookingByUserId(UUID userId) {
        if (!bookingRepository.existsByUserId(userId)) {
            return ApiResponse.builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .message(String.format("FAIL: User with %s not found", userId))
                    .data(null)
                    .build();
        }

        return ApiResponse.builder()
                .code(HttpStatus.OK.value())
                .message("SUCCESSFUL")
                .data(bookingRepository.findByUserId(userId))
                .build();
    }

    public ApiResponse cancelBooking(UUID id){
        Booking takeBookingInfo = bookingRepository.findBookingById(id);
        LocalDate now = LocalDate.now();
        log.info("takeBookingInfo {}",takeBookingInfo.toString());

        if (takeBookingInfo == null)
            return ApiResponse.builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .message(String.format("FAIL: Booking with %s not found", id))
                    .data(null)
                    .build();

        if (!takeBookingInfo.getCheck_in_date().isAfter(now))
            return ApiResponse.builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message("FAIL: Check_in_date must not be late than today")
                    .build();

        if (takeBookingInfo.getPaymentStatus() == PaymentStatusType.SUCCESSFUL)
            return ApiResponse.builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message("Cannot delete: Your payment process must not be different with SUCCESSFUL")
                    .build();

        takeBookingInfo.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(takeBookingInfo);

        return ApiResponse.builder()
                .code(HttpStatus.OK.value())
                .message(String.format("SUCCESSFULLY: Cancel booking with id: %s ", id))
                .build();
    }
}
