package com.thesis.hotel_service.controller;

import com.thesis.hotel_service.dto.response.ApiResponse;
import com.thesis.hotel_service.mapper.RoomMapper;
import com.thesis.hotel_service.service.RoomTypeService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/roomTypes")
public class RoomTypeController {
    @Autowired
    RoomTypeService roomTypeService;

    Logger log = LoggerFactory.getLogger(RoomMapper.class);

    @GetMapping("/")
    public ResponseEntity<ApiResponse> getAllRoomTypes(HttpServletRequest request){
        String path = request.getMethod() + " " + request.getRequestURI();

        log.info(path);

        ApiResponse response = roomTypeService.getAllRoomTypes();
        HttpStatus status = response.getCode() == 200 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<ApiResponse> getRoomTypeById(HttpServletRequest request, @PathVariable("uuid")UUID uuid){

        String path = request.getMethod() + " " + request.getRequestURI() + "/" + uuid ;

        log.info(path);

        ApiResponse response = roomTypeService.getRoomTypeById(uuid);
        HttpStatus status = response.getCode() == 200 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<ApiResponse> getRoomTypeByHotelId(HttpServletRequest request,
                                                            @PathVariable UUID hotelId) {

        log.info("{} {} ", request.getMethod(), request.getRequestURI());

        ApiResponse response = roomTypeService.getRoomTypeByHotelId(hotelId);
        HttpStatus status = response.getCode() == 200 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status).body(response);
    }

}
