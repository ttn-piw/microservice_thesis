package com.thesis.hotel_service.service;

import com.thesis.hotel_service.dto.response.ApiResponse;
import com.thesis.hotel_service.model.Hotel;
import com.thesis.hotel_service.repository.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class HotelService {
    @Autowired
    HotelRepository hotelRepository;

    public ApiResponse getAllHotels(){
        try {
            List<Hotel> hotelList = hotelRepository.findAll();
            return ApiResponse.builder()
                    .code(HttpStatus.OK.value())
                    .message("SUCCESS: Response data of hotels")
                    .data(hotelList)
                    .build();
        } catch(Exception e){
            return ApiResponse.builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message(e.getMessage())
                    .data(null)
                    .build();
        }
    }

    public ApiResponse getHotelById(UUID uuid){
        if (hotelRepository.findHotelById(uuid) == null)
            return ApiResponse.builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .message(String.format("FAIL: Hotel with %s not found", uuid.toString()))
                    .data(null)
                    .build();

        return ApiResponse.builder()
                .code(HttpStatus.OK.value())
                .message("SUCCESSFUL")
                .data(hotelRepository.findHotelById(uuid))
                .build();
    }
}
