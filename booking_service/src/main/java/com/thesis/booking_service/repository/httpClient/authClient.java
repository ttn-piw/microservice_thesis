package com.thesis.booking_service.repository.httpClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "auth-service", url = "http://localhost:8081/api/v1/auth")
public interface authClient {
    @GetMapping("/getUserId")
    String getUserId(@RequestParam("email") String email);
}
