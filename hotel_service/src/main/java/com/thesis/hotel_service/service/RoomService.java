package com.thesis.hotel_service.service;

import com.thesis.hotel_service.dto.response.ApiResponse;
import com.thesis.hotel_service.dto.response.RoomIdResponse;
import com.thesis.hotel_service.mapper.RoomMapper;
import com.thesis.hotel_service.model.Room;
import com.thesis.hotel_service.repository.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RoomService {
    @Autowired
    RoomRepository roomRepository;

    @Autowired
    RoomMapper roomMapper;

    Logger log = LoggerFactory.getLogger(RoomService.class);

    public ApiResponse getRooms(){
        return ApiResponse.builder()
                .code(200)
                .message("SUCCESS: ROOMS DATA")
                .data(roomRepository.findAll())
                .build();
    }

    public ApiResponse getRoomById(UUID roomId){
        Room getRoomById = roomRepository.findRoomById(roomId);
        if (getRoomById == null)
            return ApiResponse.builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .message(String.format(("ROOM NOT FOUND with ID: %s"), roomId.toString()))
                    .data(null)
                    .build();
        log.info(getRoomById.toString());

        RoomIdResponse response = roomMapper.toRoomIdResponse(getRoomById);
        log.info(response.toString());

        return ApiResponse.builder()
                .code(200)
                .message("ROOM DATA")
                .data(response)
                .build();
    }
}
