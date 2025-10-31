package com.thesis.booking_service.mapper;

import com.thesis.booking_service.dto.response.BookedRoomTypeDTO;
import com.thesis.booking_service.dto.response.BookingGuestDTO;
import com.thesis.booking_service.model.BookedRoomType;
import com.thesis.booking_service.model.BookingGuest;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BookingGuestMapper {
    BookingGuestDTO toBookingGuestDTO(BookingGuest bookingGuest);
}