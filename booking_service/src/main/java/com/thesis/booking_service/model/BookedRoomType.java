package com.thesis.booking_service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
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

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    @JsonIgnore
    Booking booking;

    @NotNull
    UUID room_type_id;

    @NotNull
    String room_type_name_snapshot;

    @NotNull
    Integer quantity;

    @NotNull
    Double price_per_night_snapshot;
}
