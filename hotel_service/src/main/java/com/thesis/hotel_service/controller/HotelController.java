package com.thesis.hotel_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/hotels")
public class HotelController {
    @GetMapping("/test")
    String testHotel(){
        return "It's hotel service";
    }
}
