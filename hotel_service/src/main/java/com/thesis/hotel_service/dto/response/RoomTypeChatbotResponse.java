package com.thesis.hotel_service.dto.response;

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
public class RoomTypeChatbotResponse {
    UUID hotelId;
    String hotelName;
    String city;
    UUID roomTypeId;
    String roomTypeName;
    Integer totalRooms;
}
