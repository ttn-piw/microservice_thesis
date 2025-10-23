package com.thesis.booking_service.controller;

import com.thesis.booking_service.dto.response.ApiResponse;
import com.thesis.booking_service.service.BookingService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
