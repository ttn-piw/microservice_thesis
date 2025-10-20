package com.thesis.hotel_service.controller;

import com.thesis.hotel_service.dto.response.ApiResponse;
import com.thesis.hotel_service.service.RoomService;
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

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rooms")
public class RoomController {
    @Autowired
    RoomService roomService;

    Logger log = LoggerFactory.getLogger(RoomController.class);

    @GetMapping("/")
    public ResponseEntity<ApiResponse> getAllRooms(HttpServletRequest request){
        String path = request.getMethod() + " " + request.getRequestURI() + request.getQueryString();
        log.info(path);

        ApiResponse response =  roomService.getRooms();
        HttpStatus status = response.getCode() == 200 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<ApiResponse> getRoomById(HttpServletRequest request ,@PathVariable(value = "uuid") UUID uuid){
        String path = request.getMethod() + " " + request.getRequestURI() + "/" + uuid ;

        log.info(path);

        ApiResponse response = roomService.getRoomById(uuid);
        HttpStatus status = response.getCode() == 200 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }
}
