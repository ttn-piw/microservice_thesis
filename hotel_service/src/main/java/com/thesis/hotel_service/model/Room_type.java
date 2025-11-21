package com.thesis.hotel_service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "room_types")
public class Room_type {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id")
    @JsonIgnore
    @ToString.Exclude
    Hotel hotel;

    String name;
    String description;
    Double price_per_night;
    Integer capacity_adults;
    Integer capacity_children;
    Integer total_rooms;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    OffsetDateTime created_at;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    OffsetDateTime updated_at;

    @OneToMany(mappedBy = "room_type", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    List<Room> rooms;

    @OneToMany(mappedBy = "room_type", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<Room_type_image> roomTypeImages;
}
