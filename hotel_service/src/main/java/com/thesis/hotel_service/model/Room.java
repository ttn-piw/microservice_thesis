package com.thesis.hotel_service.model;

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

    @NotNull
    UUID room_type_id;

    @NotNull
    UUID hotel_id;

    String room_number;
    String status;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    OffsetDateTime created_at;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    OffsetDateTime updated_at;


//    id           | uuid                     |           | not null | uuid_generate_v4()
//    room_type_id | uuid                     |           | not null |
//    hotel_id     | uuid                     |           | not null |
//    room_number  | character varying(10)    |           | not null |
//    status       | room_status              |           | not null | 'available'::room_status
//    created_at   | timestamp with time zone |           | not null | now()
//    updated_at   | timestamp with time zone |           | not null | now()
}
