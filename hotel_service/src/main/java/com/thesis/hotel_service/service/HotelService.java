package com.thesis.hotel_service.service;

import com.thesis.hotel_service.dto.request.NewHotelRequest;
import com.thesis.hotel_service.dto.response.ApiResponse;
import com.thesis.hotel_service.model.Hotel;
import com.thesis.hotel_service.repository.HotelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
public class HotelService {
    @Autowired
    HotelRepository hotelRepository;

    Logger log = LoggerFactory.getLogger(HotelService.class);

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

    public ApiResponse createNewHotel(NewHotelRequest hotelRequest){
        //Handle business logic error before saving new hotel
        if (hotelRepository.existsByNameAndCity(
                hotelRequest.getName(), hotelRequest.getCity()))

            return ApiResponse.builder()
                    .code(HttpStatus.CONFLICT.value())
                    .message("Hotel existed in system!")
                    .data(null)
                    .build();

        if (hotelRepository.findHotelByEmail(hotelRequest.getEmail()) != null)
            return ApiResponse.builder()
                    .code(HttpStatus.CONFLICT.value())
                    .message("Email exited!")
                    .data(null)
                    .build();

        //Handle validation for Check out time before Check in time
        LocalTime check_in = LocalTime.parse(hotelRequest.getCheck_in_time());
        LocalTime check_out = LocalTime.parse(hotelRequest.getCheck_out_time());
        if (!check_out.isBefore(check_in))
            return ApiResponse.builder()
                    .code(HttpStatus.CONFLICT.value())
                    .message("Check-out time must be before check-in time")
                    .data(null)
                    .build();


        log.info(hotelRequest.toString());

        return ApiResponse.builder().code(HttpStatus.OK.value()).message("SUCCESSFULLY:Saving new a hotel").data(null).build();
    }
}
