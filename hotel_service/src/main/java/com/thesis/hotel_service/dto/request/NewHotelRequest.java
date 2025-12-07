package com.thesis.hotel_service.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewHotelRequest {
    String owner_email;

    @Column(name = "name", nullable = false)
    @NotEmpty(message = "Hotel's name is required")
    String name;

    @NotBlank
    String description;

    @Size(min = 1, max = 5)
    @NotNull(message = "Hotel's must not empty")
    Integer star_rating;

    @NotNull
    String address_line;

    @NotNull
    String city;

    @NotBlank
    String state_province;

    @NotBlank
    String postal_code;

    @NotNull
    String country;

    @NotBlank
    String phone_number;

    @Email
    String email;

    String check_in_time;

    String check_out_time;

    String created_at;

    String updated_at;
}
