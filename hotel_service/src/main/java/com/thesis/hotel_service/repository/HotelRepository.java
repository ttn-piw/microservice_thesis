package com.thesis.hotel_service.repository;

import com.thesis.hotel_service.model.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotelRepository extends JpaRepository<Hotel, Integer> {
}
