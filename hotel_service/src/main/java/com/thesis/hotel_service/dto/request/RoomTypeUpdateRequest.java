package com.thesis.hotel_service.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.thesis.hotel_service.model.Hotel;
import com.thesis.hotel_service.model.Room;
import com.thesis.hotel_service.model.Room_type_image;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomTypeUpdateRequest {
    String name;
    String description;
    Double price_per_night;
    Integer capacity_adults;
    Integer capacity_children;
    Integer total_rooms;
    OffsetDateTime updated_at;
}
