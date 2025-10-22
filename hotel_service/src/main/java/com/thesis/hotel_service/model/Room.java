package com.thesis.hotel_service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "rooms")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_type_id")
    @JsonIgnore
    Room_type room_type;

    @NotNull
    UUID hotel_id;

    String room_number;
    String status;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    OffsetDateTime created_at;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    OffsetDateTime updated_at;
}
