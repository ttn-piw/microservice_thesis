package com.thesis.api_gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/api-gateway")
public class ApiGatewayController {
    @GetMapping("/test")
    String testApiGateway(){
        return "It's api gateway controller";
    }
}
