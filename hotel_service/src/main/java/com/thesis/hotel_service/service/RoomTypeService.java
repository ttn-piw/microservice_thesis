package com.thesis.hotel_service.service;

import com.thesis.hotel_service.dto.response.ApiResponse;
import com.thesis.hotel_service.dto.response.RoomTypeResponse;
import com.thesis.hotel_service.mapper.RoomTypeMapper;
import com.thesis.hotel_service.model.Room_type;
import com.thesis.hotel_service.repository.RoomTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RoomTypeService {
    @Autowired
    RoomTypeRepository roomTypeRepository;

    @Autowired
    RoomTypeMapper roomTypeMapper;

    Logger log = LoggerFactory.getLogger(RoomService.class);

    public ApiResponse getAllRoomTypes(){
        return ApiResponse.builder()
                .code(200)
                .message("SUCCESS: ROOM_TYPES DATA")
                .data(roomTypeRepository.findAll())
                .build();
    }

    public ApiResponse getRoomTypeById(UUID uuid){
        Room_type getRoomTypeById = roomTypeRepository.findRoom_typeById(uuid);
        if (getRoomTypeById == null)
            return ApiResponse.builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .message(String.format(("ROOM TYPE NOT FOUND with ID: %s"), uuid.toString()))
                    .data(null)
                    .build();
        log.info(getRoomTypeById.toString());

        RoomTypeResponse response = roomTypeMapper.toRoomTypeByIdResponse(getRoomTypeById);
        log.info(response.toString());

        return ApiResponse.builder()
                .code(200)
                .message("ROOM TYPE DATA")
                .data(response)
                .build();
    }
}
