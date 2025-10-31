package com.thesis.booking_service.repository;

import com.thesis.booking_service.model.BookingGuest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BookingGuestRepository extends JpaRepository<BookingGuest, UUID> {
    List<BookingGuest> findBookingGuestByBookingId(UUID bookingId);
}
