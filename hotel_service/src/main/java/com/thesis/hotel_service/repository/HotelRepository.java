package com.thesis.hotel_service.repository;

import com.thesis.hotel_service.model.Hotel;
import com.thesis.hotel_service.model.Room_type;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface HotelRepository extends JpaRepository<Hotel, UUID>, JpaSpecificationExecutor<Hotel> {
    List<Hotel> findHotelByOwnerId(UUID id);

    Hotel findHotelById(UUID id);

    Hotel findHotelByEmail(String email);

    Boolean existsByNameAndCity(String name, String city);

    @Query(value =
            "SELECT h.id"+ " FROM hotels h" + " WHERE h.owner_id = :ownerId",
            nativeQuery = true)
    List<UUID> getListHotelId(@Param("ownerId") UUID ownerId);
}
