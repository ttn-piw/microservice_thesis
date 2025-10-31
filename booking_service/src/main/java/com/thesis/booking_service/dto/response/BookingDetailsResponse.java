package com.thesis.booking_service.dto.response;


import com.thesis.booking_service.mapper.BookingStatus;
import com.thesis.booking_service.mapper.PaymentStatusType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingDetailsResponse {
    //Booking info
    UUID id;
    BookingStatus status;
    PaymentStatusType paymentStatus;
    LocalDate check_in_date;
    LocalDate check_out_date;
    Double totalPrice;
    String special_requests;
    OffsetDateTime created_at;
    UUID hotel_id;
    String hotel_name_snapshot;

    //List join with other tables
    List<BookedRoomTypeDTO> roomTypes;
    List<BookingGuestDTO> guests;
}