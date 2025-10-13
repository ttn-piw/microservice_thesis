package com.thesis.auth_service.repository.httpClient;

import com.thesis.auth_service.dto.request.UserRequest;
import com.thesis.auth_service.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.awt.*;

@FeignClient(name = "user-service", url = "http://localhost:8080/api/v1/users")
public interface UserClient {

    @GetMapping("/test")
    String callUserService();

    @PostMapping(value = "/createUser", produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse createUserFeign(@RequestBody UserRequest userRequest);
}
