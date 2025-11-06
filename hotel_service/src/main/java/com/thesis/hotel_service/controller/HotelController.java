package com.thesis.hotel_service.controller;

import com.thesis.hotel_service.dto.request.HotelUpdateRequest;
import com.thesis.hotel_service.dto.request.NewHotelRequest;
import com.thesis.hotel_service.dto.response.ApiResponse;
import com.thesis.hotel_service.service.HotelService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/search")
    public ResponseEntity<ApiResponse> searchHotels(HttpServletRequest request,
                                                    @RequestParam( required = false) String city,
                                                    @RequestParam( required = false) String country,
                                                    @RequestParam( required = false) String rating_star,
                                                    @RequestParam(required = false) Double minPrice,
                                                    @RequestParam(required = false) Double maxPrice,
                                                    @RequestParam(required = false) String keyword){
        String path = request.getMethod() + " " + request.getRequestURI() + "?" + request.getQueryString();
        log.info("API: -> {}", path);

        ApiResponse response = hotelService.searchHotel(city, country, rating_star, minPrice, maxPrice, keyword);
        HttpStatus status = response.getCode() == 82200 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @PostMapping("/createHotel")
    public ResponseEntity<ApiResponse> createNewHotel(HttpServletRequest request, @RequestBody NewHotelRequest newHotel){
        String path = request.getMethod() + " " + request.getRequestURI();

        log.info(path);

        log.info(newHotel.toString());

        ApiResponse response = hotelService.createNewHotel(newHotel);
        HttpStatus status = response.getCode() == 200 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status).body(response);

    }

    @PutMapping("/updateHotel/{id}")
    public ResponseEntity<ApiResponse> updateHotelInfo(HttpServletRequest request, @PathVariable("id") UUID id,@RequestBody HotelUpdateRequest hotelUpdated){
        String path = request.getMethod() + " " + request.getRequestURI();

        log.info(path);

        ApiResponse response = hotelService.updatedHotelInfo(id, hotelUpdated);
        HttpStatus status = response.getCode() == 200 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status).body(response);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteHotelById(@PathVariable("id") UUID hotelId, HttpServletRequest request){
        String path = request.getMethod() + " " + request.getRequestURI() + "/" + hotelId ;

        log.info(path);

        ApiResponse response = hotelService.deleteHotelById(hotelId);
        return ResponseEntity.status(response.getCode()).body(response);
    }

//    API FOR SENDING BACK DATA TO OTHER SERVICE
    @GetMapping("/{hotelId}/getHotelNameSnapshot")
    public String getHotelSnapshot(@PathVariable("hotelId") UUID hotelId){
        String hotelNameSnapshot = hotelService.getHotelNameSnapshot(hotelId);
        return hotelNameSnapshot;
    }
}
