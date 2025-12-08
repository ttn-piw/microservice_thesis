package com.thesis.review_service.repository.httpClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "hotel-service", url = "http://localhost:8082/api/v1/")
public interface hotelClient {

    @GetMapping("hotels/owner/hotelId")
    List<UUID> getHotelIdByOwnerId(@RequestParam(required = true) String email);
}
