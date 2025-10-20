package com.thesis.hotel_service.repository;

import com.thesis.hotel_service.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RoomRepository extends JpaRepository<Room, UUID> {
    Room findRoomById(UUID roomId);
}
