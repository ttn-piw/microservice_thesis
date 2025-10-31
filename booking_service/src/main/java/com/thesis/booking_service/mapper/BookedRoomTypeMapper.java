package com.thesis.booking_service.mapper;

import com.thesis.booking_service.dto.response.BookedRoomTypeDTO;
import com.thesis.booking_service.dto.response.BookingDetailsResponse;
import com.thesis.booking_service.model.BookedRoomType;
import com.thesis.booking_service.model.Booking;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BookedRoomTypeMapper {
    BookedRoomTypeDTO toBookedRoomTypeDTO(BookedRoomType bookedRoomType);
}
