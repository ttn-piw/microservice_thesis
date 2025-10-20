package com.thesis.hotel_service.repository;

import com.thesis.hotel_service.model.Room_type;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RoomTypeRepository extends JpaRepository<Room_type, UUID>{
    Room_type findRoom_typeById(UUID roomTypeId);
}
