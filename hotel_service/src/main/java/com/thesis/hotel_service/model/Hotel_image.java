package com.thesis.hotel_service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "hotel_images")
public class Hotel_image {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "hotel_id")
//    @JsonIgnore
//    @ToString.Exclude
//    Hotel hotel;

    UUID hotelId;

    @Column(name = "image_url")
    String imageUrl;

    String alt_text;

    @Column(name = "is_thumbnail")
    Boolean isThumbnail;
}
