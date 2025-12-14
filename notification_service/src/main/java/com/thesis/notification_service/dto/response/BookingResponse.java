package com.thesis.booking_service.dto.response;

import com.thesis.booking_service.mapper.BookingStatus;
import com.thesis.booking_service.mapper.PaymentStatusType;
import com.thesis.booking_service.model.BookedRoomType;
import com.thesis.booking_service.model.BookingGuest;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class BookingResponse {
    UUID id;
    String userId;
    String userEmail;
    String user_phone;
    UUID hotelId;
    String hotel_name_snapshot;
    LocalDate check_in_date;
    LocalDate check_out_date;
    Double total_price;
    String special_requests;
    private BookingStatus status;
    private PaymentStatusType paymentStatus;
    String payment_intent_id;
    OffsetDateTime created_at;
    OffsetDateTime updated_at;
    private List<BookingGuest> bookingGuests;
    private List<BookedRoomType> bookedRoomTypes;
}
