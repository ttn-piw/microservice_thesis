package com.thesis.booking_service.model;

import com.thesis.booking_service.mapper.BookingStatus;
import com.thesis.booking_service.mapper.PaymentStatusType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @NotNull
    @Column(name = "user_id")
    UUID userId;

    @Email
    String userEmail;

    @NotBlank
    String user_phone;

    @NotNull
    UUID hotel_id;

    @NotBlank
    String hotel_name_snapshot;

    LocalDate check_in_date;

    LocalDate check_out_date;
//    @Column(name = "check_in_date", columnDefinition = "TIME")
//    LocalTime check_in_date;
//
//    @Column(name = "check_out_date", columnDefinition = "TIME")
//    LocalTime check_out_date;

    @NotNull
    Double total_price;

    String special_requests;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status")
    private BookingStatus status;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "payment_status")
    private PaymentStatusType paymentStatus;

    @NotBlank
    String payment_intent_id;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    OffsetDateTime created_at;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    OffsetDateTime updated_at;
}
