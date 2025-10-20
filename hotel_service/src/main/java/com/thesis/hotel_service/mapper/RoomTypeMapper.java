package com.thesis.hotel_service.mapper;

import com.thesis.hotel_service.dto.response.RoomIdResponse;
import com.thesis.hotel_service.dto.response.RoomTypeByIdResponse;
import com.thesis.hotel_service.model.Room;
import com.thesis.hotel_service.model.Room_type;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RoomTypeMapper {
    RoomTypeByIdResponse toRoomTypeByIdResponse(Room_type room_type);
}
