package com.thesis.hotel_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "hotels")
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(name = "name", nullable = false)
    @NotEmpty(message = "Hotel's name is required")
    String name;

    String description;

    @NotNull(message = "Hotel's star rating must not be empty")
    @Min(value = 1, message = "Star rating must be at least 1")
    @Max(value = 5, message = "Star rating must not exceed 5")
    private Integer star_rating;

    @NotNull
    String address_line;

    String city;

    String state_province;

    String postal_code;

    String country;

    String phone_number;

    @Email
    String email;

    @Column(name = "check_in_time", columnDefinition = "TIME")
    LocalTime check_in_time;

    @Column(name = "check_out_time", columnDefinition = "TIME")
    LocalTime check_out_time;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    OffsetDateTime created_at;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    OffsetDateTime updated_at;
}
