package com.thesis.booking_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalTime;
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
    UUID user_id;

    @Email
    String user_email;

    @NotBlank
    String user_phone;

    @NotNull
    UUID hotel_id;

    @NotBlank
    String hotel_name_snapshot;

    @Column(name = "check_in_date", columnDefinition = "TIME")
    LocalTime check_in_date;

    @Column(name = "check_out_date", columnDefinition = "TIME")
    LocalTime check_out_date;

    @NotBlank
    Double total_price;

    String special_requests;

    @NotBlank
    String status;

    @NotBlank
    String payment_status;

    @NotBlank
    String payment_intent_id;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    OffsetDateTime created_at;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    OffsetDateTime updated_at;
}
