package com.thesis.hotel_service.controller;

import com.thesis.hotel_service.dto.response.ApiResponse;
import com.thesis.hotel_service.model.Hotel;
import com.thesis.hotel_service.service.HotelService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/hotels")
public class HotelController {
    @Autowired
    HotelService hotelService;

    Logger log = LoggerFactory.getLogger(HotelController.class);

    @GetMapping("/test")
    String testHotel(){
        return "It's hotel service";
    }

    @GetMapping("/")
    public ResponseEntity<ApiResponse> getAllHotels(HttpServletRequest request){
        String path = request.getMethod() + " " + request.getRequestURI();

        log.info(path);

        ApiResponse response = hotelService.getAllHotels();
        HttpStatus status = response.getCode() == 200 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<ApiResponse> getHotelById(HttpServletRequest request ,@PathVariable(value = "uuid")UUID uuid){
        String path = request.getMethod() + " " + request.getRequestURI() + "/" + uuid ;

        log.info(path);

        ApiResponse response = hotelService.getHotelById(uuid);
        return ResponseEntity.status(response.getCode()).body(response);
    }
}
