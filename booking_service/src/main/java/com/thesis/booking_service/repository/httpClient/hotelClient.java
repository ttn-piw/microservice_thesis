package com.thesis.booking_service.repository.httpClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "hotel-service", url = "http://localhost:8082/api/v1/hotels")
public interface hotelClient {
    @GetMapping("/test")
    String callHotelService();
}
