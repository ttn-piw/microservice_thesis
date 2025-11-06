package com.thesis.user_service.mapper;

import com.thesis.user_service.document.User;
import com.thesis.user_service.dto.response.BookingUserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface bookingInfoUserMapper {
    BookingUserResponse toBookingUserResponse(User user);
}
