package com.thesis.hotel_service.dto.response;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HotelResponse {
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

    List<RoomTypeResponse> roomTypeResponseList;
}
