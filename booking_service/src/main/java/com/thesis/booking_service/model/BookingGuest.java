package com.thesis.booking_service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "booking_guests")
public class BookingGuest {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    @JsonIgnore
    Booking booking;

    @NotNull
    String full_name;

    @Email
    String email;

    @NotNull
    Boolean is_primary;
}
