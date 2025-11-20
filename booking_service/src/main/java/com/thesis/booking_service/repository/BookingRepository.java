package com.thesis.booking_service.repository;

import com.thesis.booking_service.dto.response.BookedCountResponse;
import com.thesis.booking_service.mapper.BookingStatus;
import com.thesis.booking_service.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
    Booking findBookingById(UUID id);
    List<Booking> findBookingByUserEmail(String email);
    Booking findBookingByIdAndUserEmail(UUID id, String email);
//    Boolean existsByUserId(UUID id);
//    List<Booking> findByUserId(UUID userId);
    List<Booking> findByUserEmail(String email);
//    boolean existsById(UUID id);
//    Boolean existsBookingByUserEmail(String email);
    @Query(value =
            "WITH requested_dates AS (" +
                    "  SELECT generate_series(:checkInDate\\:\\:date, :checkOutDate\\:\\:date - '1 day'\\:\\:interval, '1 day'\\:\\:interval)\\:\\:date AS day" +
                    "), " +
                    "daily_booked AS (" +
                    "  SELECT " +
                    "    d.day, " +
                    "    COALESCE(SUM(brt.quantity), 0) AS booked_count " +
                    "  FROM requested_dates d " +
                    "  LEFT JOIN bookings b ON b.check_in_date <= d.day AND b.check_out_date > d.day " +
                    "  LEFT JOIN booked_room_types brt ON brt.booking_id = b.id " +
                    "  WHERE (b.id IS NULL OR (brt.room_type_id = :roomTypeId AND b.status::text IN (:statuses))) " +
                    "  GROUP BY d.day " +
                    ") " +
                    "SELECT MAX(booked_count) " +
                    "FROM daily_booked",
            nativeQuery = true)
    Integer findMaxBookedQuantityForRoomTypeInDateRange(
            @Param("roomTypeId") UUID roomTypeId,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate,
            @Param("statuses") List<String> statuses
    );

    @Query(value =
            "SELECT " +
                    "    brt.room_type_id AS roomTypeId, " +
                    "    CAST(SUM(brt.quantity) AS INT) AS booked_count " +
                    "FROM " +
                    "    booked_room_types brt " +
                    "JOIN bookings b ON brt.booking_id = b.id " +
                    "WHERE " +
                    "    b.hotel_id = :hotelId " +
                    "    AND b.status::text IN (:status)" +
                    "    AND (b.check_in_date < :endDate AND b.check_out_date > :startDate) " +
                    "GROUP BY brt.room_type_id",
            nativeQuery = true)
    List<BookedCountResponse> findBookedRoomCountsNative(
            @Param("hotelId") UUID hotelId,
            @Param("status") String status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
