package com.thesis.booking_service.controller;

import com.thesis.booking_service.dto.request.CreateBookingRequest;
import com.thesis.booking_service.dto.response.ApiResponse;
import com.thesis.booking_service.dto.response.BookingUserResponse;
import com.thesis.booking_service.dto.response.RoomTypeResponse;
import com.thesis.booking_service.exception.AppException;
import com.thesis.booking_service.exception.ErrorCode;
import com.thesis.booking_service.repository.httpClient.authClient;
import com.thesis.booking_service.repository.httpClient.hotelClient;
import com.thesis.booking_service.repository.httpClient.userClient;
import com.thesis.booking_service.service.BookingService;
import com.thesis.booking_service.service.RoomAvailableService;
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

    @Autowired
    hotelClient hotelClient;

    @Autowired
    userClient userClient;

    @Autowired
    authClient authClient;

    @Autowired
    RoomAvailableService roomAvailableService;

    Logger log = LoggerFactory.getLogger(BookingController.class);

    @GetMapping("/test")
    public String testAPI(){
        return "Booking_service API";
    }

    @GetMapping("/testFeignHotel")
    public String getHotelName(@RequestParam(required = true) UUID hotelId ){
        log.info("Calling to hotel_service");
        return hotelClient.getHotelName(hotelId);
    }

    @GetMapping("/testFeignRoomType/{id}")
    public ApiResponse getRoomType(@PathVariable("id") UUID uuid){
        log.info("Calling to hotel_service");
        return hotelClient.getRoomType(uuid);
    }

//    @GetMapping("/testFeignRoomTypeResponse/{id}")
//    public void getRoomTypeResponse(@PathVariable("id") UUID uuid){
//        roomAvailableService.checkAvailability(uuid);
//    }

    @GetMapping("/testFeignUser")
    public BookingUserResponse testCallingtoUser(@RequestParam String userId){
        log.info("Calling to user_service");
        return userClient.getBookingUserResponse(userId);
    }

//    @GetMapping("/testFeignAuth")
//    public String testCallingToAuth(@RequestParam String userId){
//        log.info("Calling to auth_service");
//        return authClient.getUserId(userId);
//    }

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
    @PutMapping("/admin/{id}/cancel")
    public ResponseEntity<ApiResponse> cancelBooking(HttpServletRequest request,@PathVariable("id") UUID bookingId){
        String path = request.getMethod() + " " + request.getRequestURI() + request.getQueryString();
        log.info(path);

        ApiResponse response = bookingService.cancelBooking(bookingId);
        HttpStatus status = response.getCode() == 200 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }
    //Hotel owner
    //PATCH /api/v1/bookings/{bookingId}/status
    //POST /api/v1/bookings/{bookingId}/guests
    //PUT /api/v1/bookings/{bookingId}/guests/{guestId}
    //DELETE /api/v1/bookings/{bookingId}/guests/{guestId}
//    --------------------------------------------USER API---------------------------------
//    POST /bookings
    @PostMapping("/bookings")
    public ResponseEntity<ApiResponse> getBookingsByUser(HttpServletRequest request,
                                                         @RequestBody(required = true)CreateBookingRequest bookingRequest){
        String path = request.getMethod() + " " + request.getRequestURI() + request.getQueryString();
        log.info(path);

        var contextHolder = SecurityContextHolder.getContext().getAuthentication();
        var email = contextHolder.getName();

        log.info("Email: {}", email);

        ApiResponse response = bookingService.bookingRoom(bookingRequest, email);
        HttpStatus status = response.getCode() == 200 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/me/bookings/{booking_id}")
    public ResponseEntity<ApiResponse> getBookingDetailByUser(HttpServletRequest request,
                                                              @PathVariable("booking_id") UUID bookingId){
        String path = request.getMethod() + " " + request.getRequestURI() + request.getQueryString();
        log.info(path);

        var contextHolder = SecurityContextHolder.getContext().getAuthentication();
        var email = contextHolder.getName();

        log.info("Email: {}", email);

        try {
            ApiResponse response = bookingService.getBookingDetailOfUser(email,bookingId);
            HttpStatus status = response.getCode() == 200 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(response);
        } catch(AppException e) {
            return ResponseEntity.status(ErrorCode.UNAUTHENTICATED.getCode()).body(null);
        }
    }
    @PatchMapping("/me/bookings/{booking_id}/cancel")
    public ResponseEntity<ApiResponse> cancelBookingDetailByUser(HttpServletRequest request,
                                                                 @PathVariable("booking_id") UUID bookingId){
        String path = request.getMethod() + " " + request.getRequestURI() + request.getQueryString();
        log.info(path);


        var contextHolder = SecurityContextHolder.getContext().getAuthentication();
        var email = contextHolder.getName();

        log.info("Email: {}", email);

        try {
            ApiResponse response = bookingService.cancelBookingByUser(email,bookingId);
            HttpStatus status = response.getCode() == 200 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(response);
        } catch(AppException e) {
            return ResponseEntity.status(ErrorCode.UNAUTHENTICATED.getCode()).body(null);
        }
    }

    @DeleteMapping("/admin/{booking_id}")
    public ResponseEntity<ApiResponse> deleteBooking(HttpServletRequest request,
                                                     @PathVariable("booking_id") UUID id){
        String path = request.getMethod() + " " + request.getRequestURI() + request.getQueryString();
        log.info(path);

        var contextHolder = SecurityContextHolder.getContext().getAuthentication();
        var email = contextHolder.getName();

        log.info("Email: {}", email);

        ApiResponse response = bookingService.deleteBooking(id);
        HttpStatus status = response.getCode() == 200 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }
}
