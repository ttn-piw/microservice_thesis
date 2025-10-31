package com.thesis.booking_service.repository;

import com.thesis.booking_service.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
    Booking findBookingById(UUID id);
    Booking findBookingByIdAndUserEmail(UUID id, String email);
    Boolean existsByUserId(UUID id);
    List<Booking> findByUserId(UUID userId);
    List<Booking> findByUserEmail(String email);
    boolean existsById(UUID id);
    Boolean existsBookingByUserEmail(String email);
}
