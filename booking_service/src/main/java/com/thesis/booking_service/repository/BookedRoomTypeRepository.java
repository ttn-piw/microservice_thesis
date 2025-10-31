package com.thesis.booking_service.repository;

import com.thesis.booking_service.model.BookedRoomType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BookedRoomTypeRepository extends JpaRepository<BookedRoomType, UUID> {
    List<BookedRoomType> findBookedRoomTypeByBookingId(UUID bookingId);
}
