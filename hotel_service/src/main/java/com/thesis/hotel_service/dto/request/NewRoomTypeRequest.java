package com.thesis.hotel_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewRoomTypeRequest {
    @NotBlank
    String name;

    @NotBlank
    String description;

    @NotNull
    Double price_per_night;

    @NotNull
    Integer capacity_adults;

    @NotNull
    Integer capacity_children;

    @NotNull
    Integer total_rooms;
}
