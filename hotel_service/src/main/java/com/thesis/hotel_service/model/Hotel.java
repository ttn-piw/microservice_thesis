package com.thesis.hotel_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @Size(min = 1, max = 5)
    @NotNull(message = "Hotel's must not empty")
    Integer star_rating;

    @NotNull
    String address_line;

    String city;

    String state_province;

    String postal_code;

    String country;

    String phone_number;

    @Email
    String email;

    String check_in_time;

    String check_out_time;

    String created_at;

    String updated_at;
}
