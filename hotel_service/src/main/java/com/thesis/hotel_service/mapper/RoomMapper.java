package com.thesis.hotel_service.mapper;

import com.thesis.hotel_service.dto.response.RoomIdResponse;
import com.thesis.hotel_service.model.Room;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RoomMapper {
    RoomIdResponse toRoomIdResponse(Room room);
}
