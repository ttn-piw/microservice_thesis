package com.thesis.hotel_service.dto.response;

import com.thesis.hotel_service.model.Room_type;
import com.thesis.hotel_service.model.Room_type_image;
import jakarta.persistence.Column;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomTypeResponse {
    UUID id;
    String name;
    String description;
    Float price_per_night;
    Integer capacity_adults;
    Integer capacity_children;
    Integer total_rooms;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    OffsetDateTime created_at;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    OffsetDateTime updated_at;

    List<Room_type_image> roomTypeImages;
}
