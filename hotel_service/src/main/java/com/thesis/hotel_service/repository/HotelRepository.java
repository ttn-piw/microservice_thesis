package com.thesis.hotel_service.repository;

import com.thesis.hotel_service.model.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HotelRepository extends JpaRepository<Hotel, UUID> {
    Hotel findHotelById(UUID id);
}
