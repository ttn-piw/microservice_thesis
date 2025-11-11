package com.thesis.hotel_service.repository;

import com.thesis.hotel_service.model.Hotel_image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface HotelImageRepository extends JpaRepository<Hotel_image, UUID> {
    List<Hotel_image> findHotel_imageByHotelId(UUID id);
}
