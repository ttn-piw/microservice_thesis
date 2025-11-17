package com.thesis.hotel_service.mapper;

import com.thesis.hotel_service.dto.request.HotelUpdateRequest;
import com.thesis.hotel_service.dto.request.NewHotelRequest;
import com.thesis.hotel_service.dto.response.HotelMainPageResponse;
import com.thesis.hotel_service.dto.response.HotelResponse;
import com.thesis.hotel_service.model.Hotel;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface HotelMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatedHotel(@MappingTarget Hotel target, HotelUpdateRequest updatedHotel);

    HotelResponse toHotelResponse(Hotel hotel);

   HotelMainPageResponse toHotelMainPage(Hotel hotels);

   List<HotelMainPageResponse> toHotelMainPageResponse(List<Hotel> hotels);
}




