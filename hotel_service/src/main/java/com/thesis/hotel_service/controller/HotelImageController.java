package com.thesis.hotel_service.controller;

import com.thesis.hotel_service.dto.response.ImageResponse;
import com.thesis.hotel_service.model.Hotel_image;
import com.thesis.hotel_service.repository.HotelImageRepository;
import com.thesis.hotel_service.service.FileStorageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/hotel-images")
public class HotelImageController {
    @Autowired
    FileStorageService fileStorageService;

    @Autowired
    HotelImageRepository hotelImageRepository;

    @GetMapping("/{hotel_id}")
    public ResponseEntity<List<ImageResponse>> getImageForHotels(HttpServletRequest request,
                                                               @PathVariable("hotel_id")UUID id){
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        log.info("Base URL: {}", baseUrl);

        List<Hotel_image> images = hotelImageRepository.findHotel_imageByHotelId(id);
        log.info("Images: {}", images);

        List<ImageResponse> responseList = images.stream()
                .map(image -> new ImageResponse(
                        image.getId(),
                        baseUrl + "/uploads/" + image.getImageUrl(),
                        image.getAlt_text()
                ))
                .toList();

        return ResponseEntity.ok(responseList);
    }
}
