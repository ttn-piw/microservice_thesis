package com.thesis.booking_service.controller;

import com.thesis.booking_service.dto.response.ApiResponse;
import com.thesis.booking_service.exception.AppException;
import com.thesis.booking_service.exception.ErrorCode;
import com.thesis.booking_service.service.BookingService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;

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
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Username: {}", authentication.getName());
        log.info("ROLES: {}", authentication.getAuthorities());

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

    //Cancel by admin
    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse> cancelBooking(HttpServletRequest request,@PathVariable("id") UUID bookingId){
        String path = request.getMethod() + " " + request.getRequestURI() + request.getQueryString();
        log.info(path);

        ApiResponse response = bookingService.cancelBooking(bookingId);
        HttpStatus status = response.getCode() == 200 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

//    --------------------------------------------USER API---------------------------------
//    POST /bookings

    @GetMapping("/me/bookings")
    public ResponseEntity<ApiResponse> getBookingByUserId(HttpServletRequest request){
        String path = request.getMethod() + " " + request.getRequestURI() + request.getQueryString();
        log.info(path);

        var contextHolder = SecurityContextHolder.getContext().getAuthentication();
        var email = contextHolder.getName();

        log.info("Email: {}", email);
        try {
            ApiResponse response = bookingService.getBookingOfUser(email);
            HttpStatus status = response.getCode() == 200 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(response);
        } catch(AppException e) {
            return ResponseEntity.status(ErrorCode.UNAUTHENTICATED.getCode()).body(null);
        }
    }

//    @GetMapping("users/{user_id}/bookings/{booking_id}")
//    public ResponseEntity<ApiResponse> getBookingDetailByUserId(HttpServletRequest request,
//                                                                @PathVariable("user_id")UUID userId,
//                                                                @PathVariable("booking_id")UUID bookingId){
//        String path = request.getMethod() + " " + request.getRequestURI() + request.getQueryString();
//        log.info(path);
//
//        ApiResponse response = bookingService.getBookingDetailByUserId(userId, bookingId);
//        HttpStatus status = response.getCode() == 200 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
//        return ResponseEntity.status(status).body(response);
//    }
//
//    @PutMapping("/users/{user_id}/bookings/{booking_id}/cancel")
//    public ResponseEntity<ApiResponse> cancelBookingDetailByUser(HttpServletRequest request,
//                                                                 @PathVariable("user_id") UUID userId,
//                                                                 @PathVariable("booking_id") UUID bookingId){
//        String path = request.getMethod() + " " + request.getRequestURI() + request.getQueryString();
//        log.info(path);
//
//        ApiResponse response = bookingService.cancelBookingDetailByUser(userId,bookingId);
//        HttpStatus status = response.getCode() == 200 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
//        return ResponseEntity.status(status).body(response);
//    }
}
