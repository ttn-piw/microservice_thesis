package com.thesis.booking_service.repository.httpClient;

import com.thesis.booking_service.dto.response.ApiResponse;
import com.thesis.booking_service.dto.response.BookingUserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service", url = "http://localhost:8080/api/v1/users")
public interface userClient {
    @GetMapping("/test")
    String callUserService();

    @GetMapping("/getBookingUserResponse")
    BookingUserResponse getBookingUserResponse(@RequestParam("userId") String userId);

}

