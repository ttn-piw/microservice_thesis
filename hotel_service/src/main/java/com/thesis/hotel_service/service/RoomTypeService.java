package com.thesis.hotel_service.service;

import com.thesis.hotel_service.dto.request.NewRoomTypeRequest;
import com.thesis.hotel_service.dto.response.ApiResponse;
import com.thesis.hotel_service.dto.response.RoomTypeResponse;
import com.thesis.hotel_service.exception.ErrorCode;
import com.thesis.hotel_service.mapper.RoomTypeMapper;
import com.thesis.hotel_service.model.Hotel;
import com.thesis.hotel_service.model.Room_type;
import com.thesis.hotel_service.repository.HotelRepository;
import com.thesis.hotel_service.repository.RoomTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class RoomTypeService {
    @Autowired
    RoomTypeRepository roomTypeRepository;

    @Autowired
    RoomTypeMapper roomTypeMapper;

    Logger log = LoggerFactory.getLogger(RoomService.class);
    @Autowired
    private HotelRepository hotelRepository;

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

    public ApiResponse getRoomTypeByHotelId(UUID uuid){
        List<Room_type> roomTypes = roomTypeRepository.findByHotel_Id(uuid);

        if (roomTypes.isEmpty()) {
            return ApiResponse.builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .message(String.format("ROOM TYPE NOT FOUND with HOTEL ID: %s", uuid))
                    .data(null)
                    .build();
        }

        List<RoomTypeResponse> responseList = roomTypes.stream()
                .map(roomTypeMapper::toRoomTypeByIdResponse)
                .toList();

        return ApiResponse.builder()
                .code(200)
                .message("ROOM TYPE DATA")
                .data(responseList)
                .build();
    }

    public ApiResponse createNewRoomType(NewRoomTypeRequest request, UUID hotelId){
        if (roomTypeRepository.existsRoom_typeByName(request.getName()))
            return ApiResponse.builder()
                    .code(ErrorCode.ROOM_TYPE_EXISTED.getCode())
                    .message(ErrorCode.ROOM_TYPE_EXISTED.getMessage())
                    .data(null)
                    .build();

        Hotel hotelInfo = hotelRepository.findHotelById(hotelId);



        Room_type newRoomType = new Room_type();
        newRoomType.setHotel(hotelInfo);
        newRoomType.setName(request.getName());
        newRoomType.setDescription(request.getDescription());
        newRoomType.setPrice_per_night(request.getPrice_per_night());
        newRoomType.setCapacity_adults(request.getCapacity_adults());
        newRoomType.setCapacity_children(request.getCapacity_children());
        newRoomType.setTotal_rooms(request.getTotal_rooms());


        OffsetDateTime now = OffsetDateTime.now();
        newRoomType.setCreated_at(now);
        newRoomType.setUpdated_at(now);

        roomTypeRepository.save(newRoomType);

        return ApiResponse.builder()
                .code(HttpStatus.OK.value())
                .message(String.format("SUCCESSFULLY: New room type created with :%s",hotelId.toString()))
                .data(newRoomType.getId())
                .build();
    }

    public RoomTypeResponse getRoomTypeResponse(UUID id){
        return roomTypeMapper.toRoomTypeByIdResponse(roomTypeRepository.findRoom_typeById(id));
    }
}
