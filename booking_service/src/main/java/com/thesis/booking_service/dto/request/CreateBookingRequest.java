package com.thesis.booking_service.dto.request;

import com.thesis.booking_service.dto.response.BookedRoomTypeDTO;
import com.thesis.booking_service.dto.response.BookingGuestDTO;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateBookingRequest {
    @NotNull
    UUID hotelId;

    @NotNull
    @FutureOrPresent
    LocalDate checkInDate;

    @NotNull
    @Future
    LocalDate checkOutDate;

    String specialRequests;

    @NotEmpty
    List<RoomTypeBookingRequest> roomTypes;

    @NotEmpty
    List<GuestBookingRequest> guests;
}
