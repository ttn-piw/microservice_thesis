package com.thesis.hotel_service.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalTime;
import java.time.OffsetDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HotelUpdateRequest {
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
    String check_in_time;
    String check_out_time;
    OffsetDateTime updated_at;
}
