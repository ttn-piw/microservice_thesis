package com.thesis.chat_service.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateBookingToBookingServiceRequest {
    UUID hotelId;
    LocalDate checkInDate;
    LocalDate checkOutDate;
    String specialRequests;
    List<RoomTypeBookingRequest> roomTypes;
    List<GuestBookingRequest> guests;
}
