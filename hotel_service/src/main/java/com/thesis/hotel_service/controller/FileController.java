package com.thesis.hotel_service.controller;

import com.thesis.hotel_service.model.Hotel;
import com.thesis.hotel_service.model.Hotel_image;
import com.thesis.hotel_service.model.Room_type;
import com.thesis.hotel_service.model.Room_type_image;
import com.thesis.hotel_service.repository.HotelImageRepository;
import com.thesis.hotel_service.repository.HotelRepository;
import com.thesis.hotel_service.repository.RoomTypeImageRepository;
import com.thesis.hotel_service.repository.RoomTypeRepository;
import com.thesis.hotel_service.service.FileStorageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Map;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/api/v1/files")
public class FileController {
    @Autowired
    FileStorageService fileStorageService;

    @Autowired
    HotelRepository hotelRepository;

    @Autowired
    HotelImageRepository hotelImageRepository;

    @Autowired
    RoomTypeRepository roomTypeRepository;

    @Autowired
    RoomTypeImageRepository roomTypeImageRepository;

    @PostMapping("/hotel-images/upload")
    public ResponseEntity<?> uploadHotelImage(
            HttpServletRequest request,
            @RequestParam("file")MultipartFile file,
            @RequestParam("hotelId")UUID hotelID){

        String pathRequest = request.getMethod() + " " + request.getRequestURI() + request.getQueryString();
        log.info(pathRequest);

        try{
            Hotel hotel = hotelRepository.findHotelById(hotelID);
            String fullUrl = "";

            if (hotel != null) {
                String path = request.getMethod() + " " + request.getRequestURI() + request.getQueryString();
                log.info(path);

                String uniqueFileName = fileStorageService.saveFileToHotels(file);
                Hotel_image newHotelImage = new Hotel_image();
                newHotelImage.setHotel(hotel);
                newHotelImage.setAlt_text(file.getOriginalFilename());
                newHotelImage.setImageUrl("/hotels/"+uniqueFileName);

                hotelImageRepository.save(newHotelImage);

                String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toString();
                fullUrl = baseUrl + "/uploads/hotels/" + uniqueFileName;
            }
                return ResponseEntity.ok(Map.of("url", fullUrl));

        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Fail when uploading hotel file" + e.getMessage());
        }
    }

    @PostMapping("/room-images/upload")
    public ResponseEntity<?> uploadRoomImage(
            HttpServletRequest request,
            @RequestParam("file")MultipartFile file,
            @RequestParam("roomTypeId")UUID roomTypeId){

        String pathRequest= request.getMethod() + " " + request.getRequestURI() + request.getQueryString();
        log.info(pathRequest);

        try{
            Room_type room_type = roomTypeRepository.findRoom_typeById(roomTypeId);
            String fullUrl = "";

            if (room_type != null) {
                String path = request.getMethod() + " " + request.getRequestURI() + request.getQueryString();
                log.info(path);

                String uniqueFileName = fileStorageService.saveFileToRoomTypes(file);
                Room_type_image roomTypeImage = new Room_type_image();
                roomTypeImage.setRoom_type(room_type);
                roomTypeImage.setAlt_text(file.getOriginalFilename());
                roomTypeImage.setImageUrl("/room_types/"+uniqueFileName);

                roomTypeImageRepository.save(roomTypeImage);

                String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toString();
                fullUrl = baseUrl + "/uploads/room_types/" + uniqueFileName;
            }
            return ResponseEntity.ok(Map.of("url", fullUrl));

        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Fail when uploading room type image file" + e.getMessage());
        }
    }
}
