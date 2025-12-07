package com.thesis.hotel_service.controller;

import com.thesis.hotel_service.dto.request.HotelUpdateRequest;
import com.thesis.hotel_service.dto.request.NewHotelRequest;
import com.thesis.hotel_service.dto.response.ApiResponse;
import com.thesis.hotel_service.repository.httpClient.bookingClient;
import com.thesis.hotel_service.service.HotelService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/hotels")
public class HotelController {
    @Autowired
    HotelService hotelService;

    @Autowired
    bookingClient bookingClient;

    Logger log = LoggerFactory.getLogger(HotelController.class);

    @GetMapping("/bookedRoom")
    Map<UUID, Integer> getBookedRoom(@RequestParam(required = true) UUID hotelId,
                                  @RequestParam(required = true) LocalDate checkIn,
                                  @RequestParam(required = true) LocalDate checkOut){
        return bookingClient.getBookedRoomCounts(hotelId,checkIn,checkOut);
    }

    @GetMapping("/test")
    String testHotel(){
        return "It's hotel service";
    }

    @GetMapping("/")
    public ResponseEntity<ApiResponse> getHotelMainPage(HttpServletRequest request){
        String path = request.getMethod() + " " + request.getRequestURI();

        log.info(path);

        ApiResponse response = hotelService.getAllHotelsMainPage();
        HttpStatus status = response.getCode() == 200 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/getAll")
    public ResponseEntity<ApiResponse> getAllHotels(HttpServletRequest request){
        String path = request.getMethod() + " " + request.getRequestURI();

        log.info(path);

        ApiResponse response = hotelService.getAllHotels();
        HttpStatus status = response.getCode() == 200 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/owner/")
    public ResponseEntity<ApiResponse> getHotelByOwnerId(HttpServletRequest request ,
                                                         @RequestParam(required = true) String email){
        String path = request.getMethod() + " " + request.getRequestURI() + "/" + email ;

        log.info(path);

        ApiResponse response = hotelService.getHotelByOwnerId(email);
        return ResponseEntity.status(response.getCode()).body(response);
    }


    @GetMapping("/{uuid}")
    public ResponseEntity<ApiResponse> getHotelById(HttpServletRequest request ,@PathVariable(value = "uuid")UUID uuid){
        String path = request.getMethod() + " " + request.getRequestURI() + "/" + uuid ;

        log.info(path);

        ApiResponse response = hotelService.getHotelById(uuid);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/homeSearch")
    public ResponseEntity<ApiResponse> searchHomePage(HttpServletRequest request,
                                                    @RequestParam( required = false) String city,
                                                    @RequestParam(required = true) LocalDate checkIn,
                                                    @RequestParam(required = false) LocalDate checkOut,
                                                    @RequestParam(required = true)  Integer bookedRoom){
        String path = request.getMethod() + " " + request.getRequestURI() + "?" + request.getQueryString();
        log.info("API: -> {}", path);

        ApiResponse response = hotelService.searchHomePage(city, checkIn, checkOut, bookedRoom);
        HttpStatus status = response.getCode() == 82200 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/availability")
    public ResponseEntity<ApiResponse> getAvailableRoom(
            HttpServletRequest request,
            @RequestParam(required = true) UUID hotelId,
            @RequestParam(required = true) LocalDate checkIn,
            @RequestParam(required = true) LocalDate checkOut){
        String path = request.getMethod() + " " + request.getRequestURI() + "?" + request.getQueryString();
        log.info("API: -> {}", path);

        ApiResponse response = hotelService.getAvailability(hotelId,checkIn,checkOut);
        HttpStatus status = response.getCode() == 82200 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
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

    @GetMapping("/searchTool")
    public ApiResponse<List<Map<String, Object>>> searchForChat(HttpServletRequest request,
                                                   @RequestParam(required = false) String city,
                                                   @RequestParam(required = false) String roomType){
        ApiResponse response = hotelService.searchForChat(city, roomType);
        return response;
    }
}
