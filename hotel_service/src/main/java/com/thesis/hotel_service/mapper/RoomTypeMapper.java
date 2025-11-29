package com.thesis.hotel_service.mapper;

import com.thesis.hotel_service.dto.request.HotelUpdateRequest;
import com.thesis.hotel_service.dto.request.RoomTypeUpdateRequest;
import com.thesis.hotel_service.dto.response.RoomTypeResponse;
import com.thesis.hotel_service.model.Hotel;
import com.thesis.hotel_service.model.Room_type;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RoomTypeMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatedRoomType(@MappingTarget Room_type target, RoomTypeUpdateRequest updateRoomType);

    RoomTypeResponse toRoomTypeByIdResponse(Room_type room_type);
}
