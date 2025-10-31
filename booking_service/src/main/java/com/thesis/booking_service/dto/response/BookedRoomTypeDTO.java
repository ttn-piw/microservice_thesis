package com.thesis.booking_service.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookedRoomTypeDTO {
    UUID room_type_id;
    String room_type_name_snapshot;
    int quantity;
    Double price_per_night_snapshot;
}
