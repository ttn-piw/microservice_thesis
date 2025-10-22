package com.thesis.hotel_service.mapper;

import com.thesis.hotel_service.dto.response.RoomTypeResponse;
import com.thesis.hotel_service.model.Room_type;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RoomTypeMapper {
    RoomTypeResponse toRoomTypeByIdResponse(Room_type room_type);
}
