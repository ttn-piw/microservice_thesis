package com.thesis.hotel_service.dto.response;

import com.thesis.hotel_service.model.Hotel_image;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HotelMainPageResponse {
    UUID id;
    String name;
    String description;
    Integer star_rating;
    String address_line;
    String city;
    String state_province;
    String postal_code;
    String country;
    String phone_number;
    String email;

    LocalTime check_in_time;
    LocalTime check_out_time;

    List<Hotel_image> hotelImages;
}
