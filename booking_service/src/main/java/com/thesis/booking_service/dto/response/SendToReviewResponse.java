package com.thesis.booking_service.dto.response;

import com.thesis.booking_service.mapper.BookingStatus;
import com.thesis.booking_service.mapper.PaymentStatusType;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SendToReviewResponse {
    UUID id;
    String userId;
    String userEmail;
    String userPhone;
    UUID hotelId;
    String hotel_name_snapshot;
    LocalDate check_in_date;
    LocalDate check_out_date;
    Double total_price;
    String special_requests;

    @JdbcType(PostgreSQLEnumJdbcType.class)
    BookingStatus status;

    @JdbcType(PostgreSQLEnumJdbcType.class)
    PaymentStatusType paymentStatus;

    String payment_intent_id;
    OffsetDateTime created_at;
    OffsetDateTime updated_at;

    List<BookingGuestDTO> bookingGuests;
    List<BookedRoomTypeDTO> bookedRoomTypes;
}
