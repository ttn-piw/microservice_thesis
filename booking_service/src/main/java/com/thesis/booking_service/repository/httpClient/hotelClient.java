package com.thesis.booking_service.repository.httpClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "hotel-service", url = "http://localhost:8082/api/v1/")
public interface hotelClient {
    @GetMapping("hotels/test")
    String callHotelService();

    @GetMapping("hotels/{hotelId}/getHotelNameSnapshot")
    String getHotelName(@PathVariable("hotelId")UUID hotelId);

    @GetMapping("roomTypes/{id}/getPrice")
    Double getPrice(@PathVariable("id") UUID id);
}
