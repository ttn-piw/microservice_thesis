package com.thesis.hotel_service.service;

import com.thesis.hotel_service.dto.request.HotelUpdateRequest;
import com.thesis.hotel_service.dto.request.NewHotelRequest;
import com.thesis.hotel_service.dto.response.ApiResponse;
import com.thesis.hotel_service.dto.response.HotelResponse;
import com.thesis.hotel_service.mapper.HotelMapper;
import com.thesis.hotel_service.model.Hotel;
import com.thesis.hotel_service.repository.HotelRepository;
import com.thesis.hotel_service.repository.spec.HotelSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class HotelService {
    @Autowired
    HotelRepository hotelRepository;

    @Autowired
    HotelMapper hotelMapper;

    Logger log = LoggerFactory.getLogger(HotelService.class);

    public ApiResponse getAllHotels(){
        try {
            List<Hotel> hotelList = hotelRepository.findAll();
//            List<HotelResponse> resposne = hotelList
//                    .stream()
//                    .map(hotelMapper::toHotelResponse)
//                    .toList();
            return ApiResponse.builder()
                    .code(HttpStatus.OK.value())
                    .message("SUCCESS: Response data of hotels")
                    .data(hotelList)
                    .build();
        } catch(Exception e){
            return ApiResponse.builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message(e.getMessage())
                    .data(null)
                    .build();
        }
    }

    public ApiResponse getHotelById(UUID uuid){
        if (hotelRepository.findHotelById(uuid) == null)
            return ApiResponse.builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .message(String.format("FAIL: Hotel with %s not found", uuid.toString()))
                    .data(null)
                    .build();

        return ApiResponse.builder()
                .code(HttpStatus.OK.value())
                .message("SUCCESSFUL")
                .data(hotelRepository.findHotelById(uuid))
                .build();
    }

    public ApiResponse createNewHotel(NewHotelRequest hotelRequest){
        //Handle business logic error before saving new hotel
        if (hotelRepository.existsByNameAndCity(
                hotelRequest.getName(), hotelRequest.getCity()))

            return ApiResponse.builder()
                    .code(HttpStatus.CONFLICT.value())
                    .message("Hotel existed in system!")
                    .data(null)
                    .build();

        if (hotelRepository.findHotelByEmail(hotelRequest.getEmail()) != null)
            return ApiResponse.builder()
                    .code(HttpStatus.CONFLICT.value())
                    .message("Email exited!")
                    .data(null)
                    .build();

        //Handle validation for Check out time before Check in time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalTime checkIn = LocalTime.parse(hotelRequest.getCheck_in_time(), formatter);
        LocalTime checkOut = LocalTime.parse(hotelRequest.getCheck_out_time(), formatter);
        if (!checkOut.isBefore(checkIn))
            return ApiResponse.builder()
                    .code(HttpStatus.CONFLICT.value())
                    .message("Check-out time must be before check-in time")
                    .data(null)
                    .build();

        log.info(hotelRequest.toString());

        //Saving new hotel
        Hotel newHotel = new Hotel();
        newHotel.setName(hotelRequest.getName());
        newHotel.setDescription(hotelRequest.getDescription());
        newHotel.setStar_rating(hotelRequest.getStar_rating());
        newHotel.setAddress_line(hotelRequest.getAddress_line());
        newHotel.setCity(hotelRequest.getCity());
        newHotel.setState_province(hotelRequest.getState_province());
        newHotel.setPostal_code(hotelRequest.getPostal_code());
        newHotel.setCountry(hotelRequest.getCountry());
        newHotel.setPhone_number(hotelRequest.getPhone_number());
        newHotel.setEmail(hotelRequest.getEmail());

        //Handle to save datetime format
        OffsetDateTime getCurrentTime = OffsetDateTime.now();
        newHotel.setCreated_at(getCurrentTime);
        newHotel.setUpdated_at(getCurrentTime);
        newHotel.setCheck_in_time(checkIn);
        newHotel.setCheck_out_time(checkOut);

        log.info(newHotel.toString());

        hotelRepository.save(newHotel);

        return ApiResponse.builder().code(HttpStatus.OK.value()).message("SUCCESSFULLY:Saving new a hotel").data(null).build();
    }

    public ApiResponse updatedHotelInfo(UUID hotelId, HotelUpdateRequest hotelInfo){
        Hotel getHotelUpdated = hotelRepository.findHotelById(hotelId);
        //Check existed hotels
        if (getHotelUpdated == null)
            return ApiResponse.builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .message(String.format(("Hotel not found with id: %s"), hotelId.toString()))
                    .data(null)
                    .build();

        //Handle validation before updated info
        if (hotelRepository.findHotelByEmail(hotelInfo.getEmail()) != null)
            return ApiResponse.builder()
                    .code(HttpStatus.CONFLICT.value())
                    .message("Email exited!")
                    .data(null)
                    .build();

        //Handle validation for Check out time before Check in time
        if (hotelInfo.getCheck_in_time() != null && hotelInfo.getCheck_out_time() !=null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            LocalTime checkIn = LocalTime.parse(hotelInfo.getCheck_in_time(), formatter);
            LocalTime checkOut = LocalTime.parse(hotelInfo.getCheck_out_time(), formatter);
            if (!checkOut.isBefore(checkIn))
                return ApiResponse.builder()
                        .code(HttpStatus.CONFLICT.value())
                        .message("Check-out time must be before check-in time")
                        .data(null)
                        .build();
        }

        log.info(hotelInfo.toString());
        hotelMapper.updatedHotel(getHotelUpdated, hotelInfo);
        OffsetDateTime currentTime = OffsetDateTime.now();
        getHotelUpdated.setUpdated_at(currentTime);

        hotelRepository.save(getHotelUpdated);

        return ApiResponse.builder()
                .code(200)
                .message("SUCCESSFULLY: UPDATED new hotel's info")
                .data(getHotelUpdated)
                .build();
    }

    public ApiResponse searchHotel(String city, String country, String star, Double minPrice, Double maxPrice, String keyword){
        //Filter with specification
        Specification<Hotel> spec = Specification.<Hotel>unrestricted()
                .and(HotelSpecification.hasCity(city))
                .and( star != null && !star.isEmpty() ? HotelSpecification.hasRating(Integer.parseInt(star)) : null)
                .and(HotelSpecification.hasPriceBetween(minPrice,maxPrice))
                .and(HotelSpecification.nameContains(keyword));

        List<Hotel> search_hotels = hotelRepository.findAll(spec);
//        List<HotelResponse> response = search_hotels
//                .stream()
//                .map(hotelMapper::toHotelResponse)
//                .toList();

        return ApiResponse.builder()
                .code(82200)
                .message("SUCCESSFULLY: DATA FOR SEARCHING")
                .data(search_hotels)
                .build();
    }

    public ApiResponse deleteHotelById(UUID uuid){
        if (hotelRepository.findHotelById(uuid) == null)
            return ApiResponse.builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .message(String.format("FAIL: NOT FOUND hotel with id: %s not found", uuid.toString()))
                    .data(null)
                    .build();

        hotelRepository.deleteById(uuid);
        return ApiResponse.builder()
                .code(HttpStatus.OK.value())
                .message(String.format("SUCCESSFULLY: HOTEL DELETED with ID: %s", uuid.toString()))
                .data(null)
                .build();
    }
}
