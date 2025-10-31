package com.thesis.booking_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "booked_room_types")
public class BookedRoomType {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @NotNull
    UUID bookingId;

    @NotNull
    UUID room_type_id;

    @NotNull
    String room_type_name_snapshot;

    @NotNull
    Integer quantity;

    @NotNull
    Double price_per_night_snapshot;
}
