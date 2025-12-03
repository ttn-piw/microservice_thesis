package com.thesis.hotel_service.repository;

import com.thesis.hotel_service.dto.response.RoomTypeChatbotResponse;
import com.thesis.hotel_service.model.Room_type;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface RoomTypeRepository extends JpaRepository<Room_type, UUID>{
    Room_type findRoom_typeById(UUID roomTypeId);

    List<Room_type> findByHotel_Id(UUID hotelId);

    Boolean existsRoom_typeByName(String name);

    @Query(value = "SELECT h.id AS hotelId, h.name AS hotelName, h.city AS city," +
            "rt.id AS roomTypeId, rt.name AS roomTypeName, rt.total_rooms AS totalRooms " +
            "FROM room_types rt " +
            "JOIN hotels h ON h.id = rt.hotel_id " +
            "WHERE h.city = :city " +
            "AND rt.name LIKE %:keyword%",
            nativeQuery = true)
    List<RoomTypeChatbotResponse> findRoomTypesByCityAndName(
            @Param("city") String city,
            @Param("keyword") String keyword);
}
