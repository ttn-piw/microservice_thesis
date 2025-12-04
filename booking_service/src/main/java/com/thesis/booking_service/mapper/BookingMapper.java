package com.thesis.booking_service.mapper;

import com.thesis.booking_service.dto.response.BookingDetailsResponse;
import com.thesis.booking_service.dto.response.BookingResponse;
import com.thesis.booking_service.model.Booking;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BookingMapper {
    BookingResponse toBookingResponse(Booking booking);
//    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    void updatedBookingDetailResponse(@MappingTarget BookingDetailsResponse target, Booking updatedHotel);
}
