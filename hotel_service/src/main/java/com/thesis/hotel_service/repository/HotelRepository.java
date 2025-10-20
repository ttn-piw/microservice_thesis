package com.thesis.hotel_service.repository;

import com.thesis.hotel_service.model.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface HotelRepository extends JpaRepository<Hotel, UUID>, JpaSpecificationExecutor<Hotel> {
    Hotel findHotelById(UUID id);

    Hotel findHotelByEmail(String email);

    Boolean existsByNameAndCity(String name, String city);
}
