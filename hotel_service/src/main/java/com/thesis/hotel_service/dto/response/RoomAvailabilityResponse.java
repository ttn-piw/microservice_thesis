package com.thesis.hotel_service.dto.response;
import com.thesis.hotel_service.model.Room_type_image;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomAvailabilityResponse {
    UUID Id;
    String name;
    String description;
    Double price_per_night;
    Integer totalRooms;
    Integer availableRooms;
    List<Room_type_image> roomTypeImages;
}
