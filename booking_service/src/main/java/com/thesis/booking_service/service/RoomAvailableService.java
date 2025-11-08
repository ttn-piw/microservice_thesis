package com.thesis.booking_service.service;

import com.thesis.booking_service.dto.response.ApiResponse;
import com.thesis.booking_service.dto.response.RoomTypeResponse;
import com.thesis.booking_service.exception.RoomUnvailableException;
import com.thesis.booking_service.mapper.BookingStatus;
import com.thesis.booking_service.repository.BookingRepository;
import com.thesis.booking_service.repository.httpClient.hotelClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomAvailableService {
    final BookingRepository bookingRepository;
    final hotelClient hotelClient;

    public void checkAvailability(UUID roomTypeId, LocalDate checkIn, LocalDate checkOut, int quantityBooking){
        RoomTypeResponse roomInfo = hotelClient.getRoomTypeResponse(roomTypeId);
        int totalRoomsOfType = roomInfo.getTotal_rooms();

        List<String> statuses = List.of(BookingStatus.PENDING.name(), BookingStatus.CONFIRMED.name());
        Integer maxBooked = bookingRepository.
                findMaxBookedQuantityForRoomTypeInDateRange(roomTypeId, checkIn, checkOut, statuses);

        if (maxBooked == null) { maxBooked = 0; }

        int availableRooms = totalRoomsOfType - maxBooked;
        log.info("Available room: {}", availableRooms);

        if (quantityBooking > availableRooms) {
            log.error("Room unavailable: {} rooms requested, only {} available", quantityBooking, availableRooms);
            throw new RoomUnvailableException(
                    "Not enough room. Room type: '" + roomInfo.getName() +
                            "' only have " + availableRooms + " available room for this time."
            );
        }
    }
}
