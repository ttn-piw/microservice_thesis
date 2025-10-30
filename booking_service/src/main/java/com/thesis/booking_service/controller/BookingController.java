package com.thesis.booking_service.controller;

import com.thesis.booking_service.dto.response.ApiResponse;
import com.thesis.booking_service.service.BookingService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {
    @Autowired
    BookingService bookingService;

    Logger log = LoggerFactory.getLogger(BookingController.class);

    @GetMapping("/test")
    public String testAPI(){
        return "Booking_service API";
    }

    @GetMapping("/")
    public ResponseEntity<ApiResponse> getAllBookings(HttpServletRequest request){
        String path = request.getMethod() + " " + request.getRequestURI() + request.getQueryString();
        log.info(path);

        ApiResponse response = bookingService.getAllBookings();
        HttpStatus status = response.getCode() == 200 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getBookingById(HttpServletRequest request,@PathVariable("id")UUID bookingId){
        String path = request.getMethod() + " " + request.getRequestURI() + request.getQueryString();
        log.info(path);

        ApiResponse response = bookingService.getBookingById(bookingId);
        HttpStatus status = response.getCode() == 200 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("user/{user_id}")
    public ResponseEntity<ApiResponse> getBookingByUserId(HttpServletRequest request,@PathVariable("user_id")UUID userId){
        String path = request.getMethod() + " " + request.getRequestURI() + request.getQueryString();
        log.info(path);

        ApiResponse response = bookingService.getBookingByUserId(userId);
        HttpStatus status = response.getCode() == 200 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @PutMapping("{id}/cancel")
//  Check payment_status # Successfully | today < check_in_date
    public ResponseEntity<ApiResponse> cancelBooking(HttpServletRequest request,@PathVariable("id") UUID bookingId){
        String path = request.getMethod() + " " + request.getRequestURI() + request.getQueryString();
        log.info(path);

        ApiResponse response = bookingService.cancelBooking(bookingId);
        HttpStatus status = response.getCode() == 200 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }


}
