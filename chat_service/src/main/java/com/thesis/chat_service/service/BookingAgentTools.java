package com.thesis.chat_service.service;

import com.thesis.chat_service.dto.request.*;
import com.thesis.chat_service.dto.response.ApiResponse;
import com.thesis.chat_service.repository.httpClient.BookingClient;
import com.thesis.chat_service.repository.httpClient.HotelClient;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingAgentTools {
    private final HotelClient hotelClient;
    private final BookingClient bookingClient;

    @Tool(description = "Checks room availability. Return structured JSON-like string containing hotels with IDs. Save the 'ID: <uuid>' for booking.")
    public String getAvailability(AvailabilityRoomRequest request) {
        try {
            ApiResponse<List<Map<String, Object>>> hotelResponse = hotelClient.searchForChat(request.getCity(), request.getRoomTypeName());
            List<Map<String, Object>> foundRooms = hotelResponse.getData();

            if (foundRooms == null || foundRooms.isEmpty()) {
                return String.format("FAILURE: No rooms of type '%s' found in '%s'.", request.getRoomTypeName(), request.getCity());
            }

            StringBuilder sb = new StringBuilder();
            sb.append("{\n");
            sb.append(String.format("  \"city\": \"%s\",\n", request.getCity()));
            sb.append(String.format("  \"roomType\": \"%s\",\n", request.getRoomTypeName()));
            sb.append(String.format("  \"checkIn\": \"%s\",\n", request.getCheckInDate()));
            sb.append(String.format("  \"checkOut\": \"%s\",\n", request.getCheckOutDate()));
            sb.append(String.format("  \"quantity\": %d,\n", request.getQuantity()));
            sb.append("  \"hotels\": [\n");

            boolean first = true;
            for (Map<String, Object> room : foundRooms) {
                try {
                    UUID roomTypeId = UUID.fromString(room.get("roomTypeId").toString());
                    String hotelName = room.get("hotelName").toString();
                    UUID hotelId = UUID.fromString(room.get("hotelId").toString());

                    Integer available = bookingClient.checkAvailability(
                            roomTypeId,
                            request.getCheckInDate(),
                            request.getCheckOutDate(),
                            request.getQuantity()
                    );
                    // List of returning result
                    if (available != null && available >= request.getQuantity()) {
                        if (!first) sb.append(",\n");
                        sb.append(String.format("    { \"hotelId\": \"%s\", \"hotelName\": \"%s\", \"roomTypeId\": \"%s\", \"available\": %d }",
                                hotelId, hotelName, roomTypeId, available));
                        first = false;
                    }
                } catch (Exception e) {
                    log.warn("skip room entry due to error: {}", e.getMessage());
                }
            }

            sb.append("\n  ]\n}");
            log.info("Result: {}", sb);
            return sb.toString();
        } catch (FeignException e) {
            log.error("Feign error", e);
            return "ERROR: Unable to contact hotel/booking services.";
        } catch (Exception e) {
            log.error("Internal error", e);
            return "ERROR: Internal error when checking availability.";
        }
    }

    @Tool(name = "bookRoom", description = "Books a room based on roomTypeId, dates, and customer details. Use this tool ONLY when the user explicitly confirms they want to book.MUST use the hotelId (UUID) returned from the search/availability tool, NOT the hotel name.")
    public String bookRoom(BookRoomRequest request) {
        log.info("CALLING to bookRoom");
        try {
            if (request.getHotelId() == null) {
                return "FAILURE: Missing Hotel ID. Please searching availability again.";
            }

            if (request.getRoomTypeId() == null || request.getRoomTypeId().isEmpty()) {
                return "FAILURE: Missing roomTypeId";
            }

            LocalDate checkIn = LocalDate.parse(request.getCheckInDate());
            LocalDate checkOut = LocalDate.parse(request.getCheckOutDate());

            RoomTypeBookingRequest roomTypeReq = RoomTypeBookingRequest.builder()
                    .roomTypeId(UUID.fromString(request.getRoomTypeId()))
                    .quantity(request.getQuantity())
                    .build();

            GuestBookingRequest guestReq = GuestBookingRequest.builder()
                    .full_name(request.getCustomerName())
                    .email(request.getCustomerEmail())
                    .is_primary(true)
                    .build();

            CreateBookingToBookingServiceRequest bookingRequest = CreateBookingToBookingServiceRequest.builder()
                    .hotelId(request.getHotelId())
                    .checkInDate(checkIn)
                    .checkOutDate(checkOut)
                    .specialRequests(request.getSpecialRequests())
                    .roomTypes(List.of(roomTypeReq))
                    .guests(List.of(guestReq))
                    .build();

            ApiResponse response = bookingClient.createBooking(bookingRequest);
            String message = response.getMessage();
            UUID bookingId = UUID.fromString(message);

            var booking = bookingClient.getBookingById(bookingId).getData();
            log.info("Booking: {}", booking);

            bookingClient.sendEmail(bookingId);

            return String.format("SUCCESS: Booking successfully created! for %s at %s. Please check your booked page for more detail",
                    request.getCustomerName(), request.getHotelName());

        } catch (FeignException e) {
            log.error("Feign error", e);
            return "ERROR: Unable to contact hotel/booking services.";
        } catch (Exception e) {
            log.error("Internal error", e);
            return "ERROR: Internal error when booking.";
        }
    }
}
