package com.thesis.review_service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Builder
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
