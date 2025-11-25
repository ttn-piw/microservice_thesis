package com.thesis.hotel_service.repository;

import com.thesis.hotel_service.model.Room_type_image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RoomTypeImageRepository extends JpaRepository<Room_type_image, UUID> {
}
